package com.jianzixing.webapp.web;

import com.jianzixing.webapp.handler.WebLoginHolder;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.order.PlatformType;
import com.jianzixing.webapp.tables.discount.TableDiscount;
import com.jianzixing.webapp.tables.goods.TableGoods;
import com.jianzixing.webapp.tables.goods.TableGoodsSku;
import com.jianzixing.webapp.tables.order.TableOrder;
import com.jianzixing.webapp.tables.order.TableOrderInvoice;
import com.jianzixing.webapp.tables.order.TableUserAddress;
import com.jianzixing.webapp.tables.shopcart.TableShoppingCart;
import org.mimosaframework.core.utils.StringTools;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelArray;
import org.mimosaframework.core.json.ModelObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class WebWXOrderController {
    @RequestMapping("/wx/order")
    public ModelAndView order(HttpServletRequest request,
                              ModelAndView view,
                              String c,
                              @RequestParam(defaultValue = "0") long addrid) {
        if (WebLoginHolder.isLogin(view, request)) {
            long uid = WebLoginHolder.getUid();
            view.setViewName("wx/order");
            if (StringUtils.isNotBlank(c)) {
                List<ModelObject> goodsList = GlobalService.goodsService.getOrderGoodsByCart(uid, Arrays.asList(c.split(",")));
                if (goodsList != null && goodsList.size() > 0) {
                    view.addObject("goods", goodsList);

                    ModelObject address = null;
                    if (addrid > 0) {
                        address = GlobalService.userAddressService.getUserAddressById(uid, addrid);
                    }
                    if (address == null) {
                        address = GlobalService.userAddressService.getDefaultAddress(uid);
                    }
                    if (address != null) {
                        address.put(TableUserAddress.phoneNumber,
                                StringTools.getHideMobile(address.getString(TableUserAddress.phoneNumber)));
                    }
                    view.addObject("address", address);
                    List<Long> goodsIds = new ArrayList<>();
                    for (ModelObject g : goodsList) {
                        goodsIds.add(g.getLongValue(TableGoods.id));
                    }

                    // 获得运费模板
                    List<ModelObject> dts = GlobalService.logisticsService.getGoodsLogistics(goodsIds);
                    view.addObject("deliveryTypes", dts);

                    // 获取当前用户在当前订单下可使用的优惠活动
                    Map<ModelObject, List<ModelObject>> map =
                            GlobalService.discountService.getUserDiscountByGoods(WebLoginHolder.getUser(), goodsList, PlatformType.ALL, false);
                    if (map != null && map.size() > 0) {
                        List<ModelObject> discounts = new ArrayList<>();
                        Set<Map.Entry<ModelObject, List<ModelObject>>> entries = map.entrySet();
                        for (Map.Entry<ModelObject, List<ModelObject>> entry : entries) {
                            ModelObject g = entry.getKey();
                            if (entry.getValue() != null && entry.getValue().size() > 0) {
                                g.put(TableDiscount.class, entry.getValue());
                                discounts.add(g);
                            }
                        }
                        if (discounts != null && discounts.size() > 0) {
                            view.addObject("discounts", discounts);
                        }
                    }

                    // 获取用户拥有的优惠券，并判断当前订单是否可用
                    List<ModelObject> coupons = GlobalService.couponService.getUserCouponsInGoods(WebLoginHolder.getUid(), goodsList);
                    view.addObject("coupons", coupons);
                }
            }

        }
        return view;
    }

    @RequestMapping("/wx/cal_order")
    @ResponseBody
    public String calOrder(String goods,
                           String cart,
                           @RequestParam(defaultValue = "0") long addrId,
                           @RequestParam(defaultValue = "0") int deliveryType,
                           @RequestParam(defaultValue = "0") long couponId,
                           String msg,
                           String invoice) {
        ModelObject message = new ModelObject();
        message.put("success", 1);
        if (WebLoginHolder.isLogin()) {
            try {
                this.processOrderArgs(
                        cart,
                        goods,
                        addrId,
                        deliveryType,
                        couponId,
                        msg,
                        invoice,
                        message,
                        false);
            } catch (Exception e) {
                e.printStackTrace();
                message.put("success", 0);
                message.put("code", "failure");
                return message.toJSONString();
            }
        } else {
            message.put("success", 0);
            message.put("code", "not_login");
        }
        return message.toJSONString();
    }

    @RequestMapping("/wx/add_order")
    @ResponseBody
    public String addOrder(String goods,
                           String cart,
                           @RequestParam(defaultValue = "0") long addrId,
                           @RequestParam(defaultValue = "0") int deliveryType,
                           @RequestParam(defaultValue = "0") long couponId,
                           String msg,
                           String invoice) {
        ModelObject message = new ModelObject();
        message.put("success", 1);
        if (StringUtils.isNotBlank(invoice)) {
            ModelObject invoiceObj = ModelObject.parseObject(invoice);
            int type = invoiceObj.getIntValue(TableOrderInvoice.type);
            int headType = invoiceObj.getIntValue(TableOrderInvoice.headType);
            if (type == 1 && headType == 1) {
                if (invoiceObj.isEmpty(TableOrderInvoice.companyName)) {
                    message.put("code", "empty_company");
                    return message.toJSONString();
                }
                if (invoiceObj.isEmpty(TableOrderInvoice.taxNumber)) {
                    message.put("code", "empty_company");
                    return "empty_tax_number";
                }
            }
            if (type == 1 && headType == 0) {
                invoiceObj.remove(TableOrderInvoice.companyName);
                invoiceObj.remove(TableOrderInvoice.taxNumber);
            }
            if (type == 2) {
                if (invoiceObj.isEmpty(TableOrderInvoice.companyName)) {
                    message.put("code", "empty_company");
                    return message.toJSONString();
                }
                if (invoiceObj.isEmpty(TableOrderInvoice.taxNumber)) {
                    message.put("code", "empty_tax_number");
                    return message.toJSONString();
                }
                if (invoiceObj.isEmpty(TableOrderInvoice.address)) {
                    message.put("code", "empty_address");
                    return message.toJSONString();
                }
                if (invoiceObj.isEmpty(TableOrderInvoice.phone)) {
                    message.put("code", "empty_phone");
                    return message.toJSONString();
                }
                if (invoiceObj.isEmpty(TableOrderInvoice.bank)) {
                    message.put("code", "empty_bank");
                    return message.toJSONString();
                }
                if (invoiceObj.isEmpty(TableOrderInvoice.bankAccount)) {
                    message.put("code", "empty_bank_account");
                    return message.toJSONString();
                }
            }
        }

        if (WebLoginHolder.isLogin()) {
            try {
                this.processOrderArgs(
                        cart,
                        goods,
                        addrId,
                        deliveryType,
                        couponId,
                        msg,
                        invoice,
                        message,
                        true);
            } catch (Exception e) {
                e.printStackTrace();
                message.put("success", 0);
                message.put("code", "failure");
                return message.toJSONString();
            }
            message.put("code", "ok");
            return message.toJSONString();
        } else {
            message.put("success", 0);
            message.put("code", "not_login");
            return message.toJSONString();
        }
    }

    private void processOrderArgs(String cart,
                                  String goods,
                                  long addrId,
                                  int deliveryType,
                                  long couponId,
                                  String msg,
                                  String invoice,
                                  ModelObject message,
                                  boolean isCreateOrder) throws Exception {
        long uid = WebLoginHolder.getUid();

        if (StringUtils.isNotBlank(goods)) {
            ModelArray goodsArray = ModelArray.parseArray(goods);
            for (int i = 0; i < goodsArray.size(); i++) {
                ModelObject cartObj = goodsArray.getModelObject(i);
                cartObj.put(TableShoppingCart.userId, uid);
                GlobalService.shoppingCartService.modifyCartGoods(cartObj);
            }
        }

        List<ModelObject> goodsList = GlobalService.goodsService.getOrderGoodsByCart(uid, Arrays.asList(cart.split(",")));
        ModelObject invoiceObj = ModelObject.parseObject(invoice);
        ModelObject calOrder = new ModelObject();
        calOrder.put("uid", uid);
        calOrder.put("aid", addrId);
        calOrder.put("deliveryType", deliveryType);
        calOrder.put("couponId", couponId);
        calOrder.put("message", msg);
        calOrder.put("invoice", invoiceObj);

        if (goodsList != null) {
            List<ModelObject> products = new ArrayList<>();
            for (ModelObject g : goodsList) {
                ModelObject product = new ModelObject();
                product.put("pid", g.getLongValue(TableGoods.id));
                product.put("buyAmount", g.getIntValue("buyAmount"));
                if (g.isNotEmpty(TableGoodsSku.class.getSimpleName())) {
                    product.put("skuId", g.getLongValue("skuId"));
                }
                product.put("goods", g);
                product.put("discountId", g.getString("discountId"));
                products.add(product);
            }
            if (isCreateOrder) {
                ModelObject r = GlobalService.orderService.addOrder(calOrder, products);
                GlobalService.shoppingCartService.deleteCarts(WebLoginHolder.getUid(), Arrays.asList(cart.split(",")));
                message.put("code", "ok");
                ModelObject order = new ModelObject();
                order.put("number", r.getString(TableOrder.number));
                message.put("order", order);
            } else {
                ModelObject prices = GlobalService.orderService.getOrderPrices(calOrder, products);
                prices.remove("buyGoods");
                message.put("prices", prices);
            }
        } else {
            message.put("success", 0);
            message.put("code", "miss_goods");
        }
    }
}
