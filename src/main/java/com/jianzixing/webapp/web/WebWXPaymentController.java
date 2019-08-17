package com.jianzixing.webapp.web;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.handler.WebLoginHolder;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.order.OrderStatus;
import com.jianzixing.webapp.service.payment.OrderPayment;
import com.jianzixing.webapp.service.payment.PaymentFlow;
import com.jianzixing.webapp.service.payment.PaymentModeInterface;
import com.jianzixing.webapp.tables.balance.TableBalance;
import com.jianzixing.webapp.tables.integral.TableIntegral;
import com.jianzixing.webapp.tables.order.TableOrder;
import com.jianzixing.webapp.tables.payment.TablePaymentChannel;
import org.mimosaframework.core.utils.RequestUtils;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelArray;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.calculator.CalcNumber;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class WebWXPaymentController {

    @RequestMapping("/wx/payment")
    public ModelAndView toPayment(HttpServletRequest request,
                                  ModelAndView view, String oid) {
        if (WebLoginHolder.isLogin(view, request)) {
            view.setViewName("wx/payment");
            long uid = WebLoginHolder.getUid();
            if (StringUtils.isNotBlank(oid)) {
                oid = oid.trim();
            }

            view.addObject("paymentType", 0);
            ModelObject order = GlobalService.orderService
                    .getOrderByNumber(uid, oid, OrderStatus.CREATE);
            view.addObject("order", order);
            if (order != null) {
                BigDecimal payPrice = order.getBigDecimal(TableOrder.payPrice);
                BigDecimal payed = GlobalService.paymentService.getChannelPayedPrice(WebLoginHolder.getUid(), oid);
                view.addObject("orderPayPrice", CalcNumber.as(payPrice.subtract(payed)).toPrice());

                List<ModelObject> channels = GlobalService.paymentService.getValidPayChannelByUid(uid);
                List<ModelObject> timelyChannels = new ArrayList<>();
                List<ModelObject> delayChannels = new ArrayList<>();
                if (channels != null) {
                    List<ModelObject> rms = new ArrayList<>();
                    for (ModelObject channel : channels) {
                        long channelId = channel.getLongValue(TablePaymentChannel.id);
                        PaymentModeInterface paymentModeInterface = (PaymentModeInterface) channel.get("paymentModeInterface");
                        String code = paymentModeInterface.getPaymentCode();
                        if (code.equals("balance")) {
                            ModelObject balance = GlobalService.balanceService.getBalanceByUid(uid);
                            view.addObject("balance", balance);
                            BigDecimal payedPrice = GlobalService.paymentService.getChannelPayedPrice(uid, channelId, oid);
                            view.addObject("balancePayedPrice", payedPrice == null ? "0" : CalcNumber.as(payedPrice).toPrice());
                            if (balance == null || balance.getBigDecimal(TableBalance.balance).doubleValue() <= 0) {
                                rms.add(channel);
                            }
                        }
                        if (code.equals("integral")) {
                            ModelObject integral = GlobalService.integralService.getIntegralCanPayByUid(uid);
                            view.addObject("integral", integral);
                            String rateMoney = GlobalService.systemConfigService.getValue("integral_rate_money");
                            view.addObject("rateMoney", rateMoney);
                            BigDecimal payedPrice = GlobalService.paymentService.getChannelPayedPrice(uid, channelId, oid);
                            view.addObject("integralPayedPrice", payedPrice == null ? "0" : CalcNumber.as(payedPrice).toPrice());
                            if (integral == null || integral.getBigDecimal(TableIntegral.amount).doubleValue() == 0) {
                                rms.add(channel);
                            }
                        }
                        if (code.equals("spcard")) {
                            List<ModelObject> spcards = GlobalService.shoppingCardService.getValidShoppingCardsByUid(uid);
                            view.addObject("spcards", spcards);
                            view.addObject("cardDetail", GlobalService.systemConfigService.getValue("shopping_card_detail"));
                            BigDecimal payedPrice = GlobalService.paymentService.getChannelPayedPrice(uid, channelId, oid);
                            view.addObject("spcardsPayedPrice", payedPrice == null ? "0" : CalcNumber.as(payedPrice).toPrice());
                            if (spcards == null || spcards.size() == 0) {
                                rms.add(channel);
                            }
                        }
                        if (paymentModeInterface.getPaymentFlow() == PaymentFlow.TIMELY) {
                            timelyChannels.add(channel);
                        }
                        if (paymentModeInterface.getPaymentFlow() == PaymentFlow.DELAY) {
                            delayChannels.add(channel);
                        }
                    }
                    timelyChannels.removeAll(rms);
                    delayChannels.removeAll(rms);
                    view.addObject("timelyChannels", timelyChannels);
                    view.addObject("delayChannels", delayChannels);
                }

                view.addObject("hasTimely", timelyChannels.size() > 0 ? true : false);
                view.addObject("hasDelay", delayChannels.size() > 0 ? true : false);
            }
        }
        return view;
    }

    @RequestMapping("/wx/cal_payment")
    @ResponseBody
    public String calPaymentPrice(HttpServletRequest request,
                                  String oid,
                                  String payment,
                                  @RequestParam(defaultValue = "0") int type) {
        ModelObject result = new ModelObject();
        result.put("message", 1);
        if (WebLoginHolder.isLogin() && type == 0) {
            try {
                long uid = WebLoginHolder.getUid();
                ModelArray paymentList = ModelObject.parseArray(payment);
                if (paymentList != null) {
                    OrderPayment orderPayment = this.getOrderPayment(request, payment, oid);
                    if (orderPayment != null) {
                        try {
                            ModelObject object = GlobalService.paymentService.payOrderFront(orderPayment);
                            result.put("data", object);
                            return result.toJSONString();
                        } catch (Exception e) {
                            e.printStackTrace();
                            result.put("message", 0);
                            return result.toJSONString();
                        }
                    }
                }
                result.put("message", 0);
                result.put("code", "args_empty");
                return result.toJSONString();
            } catch (Exception e) {
                e.printStackTrace();
                result.put("message", 0);
                result.put("code", "server_error");
                return result.toJSONString();
            }
        }
        return null;
    }

    private OrderPayment getOrderPayment(HttpServletRequest request,
                                         String payment,
                                         String oid) {
        long uid = WebLoginHolder.getUid();
        ModelArray paymentList = ModelObject.parseArray(payment);
        if (paymentList != null) {
            OrderPayment orderPayment = new OrderPayment();
            orderPayment.setIp(RequestUtils.getIpAddr(request));
            orderPayment.setHost(RequestUtils.getWebDomain(request));
            orderPayment.setOrderNumber(oid);
            orderPayment.setUid(uid);
            orderPayment.setCreatePayment(false);

            List<ModelObject> paymentChannels = new ArrayList<>();
            for (int i = 0; i < paymentList.size(); i++) {
                ModelObject item = (ModelObject) paymentList.get(i);
                if (item != null) {
                    long channelId = item.getLongValue("channelId");
                    double money = item.getDoubleValue("money");
                    String relIds = item.getString("relIds");

                    ModelObject channel = new ModelObject();
                    channel.put(TablePaymentChannel.id, channelId);
                    if (money > 0) {
                        channel.put("money", money);
                    }
                    if (StringUtils.isNotBlank(relIds)) {
                        channel.put("cardNumbers", Arrays.asList(relIds.split(",")));
                    }
                    paymentChannels.add(channel);
                }
            }
            if (paymentChannels.size() > 0) {
                orderPayment.setPaymentChannels(paymentChannels);
                return orderPayment;
            }
        }
        return null;
    }

    @RequestMapping("/wx/submit_payment")
    @ResponseBody
    public String submitPaymentPrice(HttpServletRequest request,
                                     String oid,
                                     String payment,
                                     @RequestParam(defaultValue = "0") int type) {
        ModelObject result = new ModelObject();
        result.put("message", 1);
        if (WebLoginHolder.isLogin()) {
            try {
                ModelArray paymentList = ModelObject.parseArray(payment);
                if (paymentList.size() > 0) {
                    if (type == 0) {
                        if (paymentList != null) {
                            OrderPayment orderPayment = this.getOrderPayment(request, payment, oid);
                            if (orderPayment != null) {
                                try {
                                    orderPayment.setCreatePayment(true);
                                    ModelObject object = GlobalService.paymentService.payOrderFront(orderPayment);
                                    result.put("data", object);
                                    return result.toJSONString();
                                } catch (ModuleException e) {
                                    e.printStackTrace();
                                    result.put("message", 0);
                                    result.put("code", e.getCode().toString());
                                    return result.toJSONString();
                                }
                            }
                        }
                    } else {
                        ModelObject recharge = new ModelObject();
                        recharge.put("oid", oid);
                        recharge.put("ip", RequestUtils.getIpAddr(request));
                        recharge.put("host", RequestUtils.getWebDomain(request));
                        recharge.put("uid", WebLoginHolder.getUid());
                        recharge.put("payment", paymentList.getModelObject(0));
                        ModelObject object = GlobalService.paymentService.payRechargeFront(recharge);
                        result.put("data", object);
                        return result.toJSONString();
                    }
                }
                result.put("message", 0);
                result.put("code", "args_empty");
                return result.toJSONString();
            } catch (Exception e) {
                e.printStackTrace();
                result.put("message", 0);
                result.put("code", "server_error");
                return result.toJSONString();
            }
        }
        return null;
    }

    @RequestMapping("/wx/payment_succ")
    public ModelAndView toPaymentSucc(HttpServletRequest request,
                                      ModelAndView view,
                                      String type,
                                      String oid) {

        if (WebLoginHolder.isLogin(view, request)) {
            view.addObject("type", type);
            view.addObject("oid", oid);
            view.setViewName("wx/pay_succ");
        }
        return view;
    }
}
