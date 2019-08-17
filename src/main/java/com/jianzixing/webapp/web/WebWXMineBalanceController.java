package com.jianzixing.webapp.web;

import com.jianzixing.webapp.handler.WebLoginHolder;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.payment.PaymentFlow;
import com.jianzixing.webapp.service.payment.PaymentModeInterface;
import com.jianzixing.webapp.tables.balance.TableBalanceRecharge;
import com.jianzixing.webapp.tables.integral.TableIntegralRecord;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.calculator.CalcNumber;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WebWXMineBalanceController {

    @RequestMapping("/wx/mine/balance")
    public ModelAndView toBalancePage(HttpServletRequest request,
                                      ModelAndView view) {
        if (WebLoginHolder.isLogin(view, request)) {
            view.setViewName("wx/mine/balance");
            ModelObject object = GlobalService.balanceService.getBalanceByUid(WebLoginHolder.getUid());
            view.addObject("balance", object);
        }
        return view;
    }

    @RequestMapping("/wx/mine/balance_list")
    @ResponseBody
    public String getBalanceList(@RequestParam(defaultValue = "1") int page) {
        ModelObject json = new ModelObject();
        json.put("success", 1);
        if (WebLoginHolder.isLogin()) {
            List<ModelObject> objects = GlobalService.balanceService.getUserRecords(WebLoginHolder.getUid(), page);
            if (objects != null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (ModelObject object : objects) {
                    if (object.isNotEmpty(TableIntegralRecord.createTime))
                        object.put(TableIntegralRecord.createTime, format.format(object.getDate(TableIntegralRecord.createTime)));
                }
            }
            json.put("data", objects);
        } else {
            json.put("success", 0);
            json.put("code", "not_login");
        }
        return json.toJSONString();
    }

    @RequestMapping("/wx/mine/balance_recharge")
    @ResponseBody
    public String getBalanceRecharge(@RequestParam(defaultValue = "0") double money) {
        ModelObject response = new ModelObject();
        response.put("success", 1);
        if (WebLoginHolder.isLogin()) {
            ModelObject recharge = new ModelObject();
            recharge.put(TableBalanceRecharge.userId, WebLoginHolder.getUid());
            recharge.put(TableBalanceRecharge.money, money);
            try {
                GlobalService.balanceService.addRechargeOrder(recharge);
                response.put("number", recharge.getString(TableBalanceRecharge.number));
            } catch (Exception e) {
                e.printStackTrace();
                response.put("success", 0);
                response.put("code", "params_error");
            }
        } else {
            response.put("success", 0);
            response.put("code", "not_login");
        }
        return response.toJSONString();
    }


    @RequestMapping("/wx/mine/balance_recharge_payment")
    public ModelAndView toPayment(HttpServletRequest request,
                                  ModelAndView view, String oid) {
        if (WebLoginHolder.isLogin(view, request)) {
            view.setViewName("wx/payment");
            long uid = WebLoginHolder.getUid();
            if (StringUtils.isNotBlank(oid)) {
                oid = oid.trim();
            }

            view.addObject("paymentType", 1);
            ModelObject order = GlobalService.balanceService.getRechargeOrderByNumber(oid);
            view.addObject("order", order);
            if (order != null) {
                order.put("payPrice", order.getBigDecimal(TableBalanceRecharge.money));
                view.addObject("orderPayPrice", CalcNumber.as(order.getBigDecimal(TableBalanceRecharge.money)).toPrice());
                boolean hasDelay = false;
                List<ModelObject> channels = GlobalService.paymentService.getValidPayChannelByUid(uid);
                if (channels != null) {
                    List<ModelObject> delayChannels = new ArrayList<>();
                    for (ModelObject channel : channels) {
                        PaymentModeInterface paymentModeInterface = (PaymentModeInterface) channel.get("paymentModeInterface");
                        if (paymentModeInterface.getPaymentFlow() == PaymentFlow.DELAY) {
                            hasDelay = true;
                            delayChannels.add(channel);
                        }
                    }
                    view.addObject("delayChannels", delayChannels);
                }

                view.addObject("hasTimely", false);
                view.addObject("hasDelay", hasDelay);
            }
        }
        return view;
    }
}
