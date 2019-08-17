package com.jianzixing.webapp.web;

import com.jianzixing.webapp.handler.WebLoginHolder;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.goods.TableGoods;
import com.jianzixing.webapp.tables.order.TableOrder;
import com.jianzixing.webapp.tables.order.TableOrderAddress;
import com.jianzixing.webapp.tables.order.TableOrderGoods;
import com.jianzixing.webapp.tables.shopcart.TableShoppingCart;
import org.mimosaframework.core.utils.RequestUtils;
import org.mimosaframework.core.utils.StringTools;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class WebWXMineOrderController {

    @RequestMapping("/wx/mine/myorder")
    public ModelAndView mineMyorder(HttpServletRequest request,
                                    ModelAndView view,
                                    @RequestParam(defaultValue = "0") String type) {
        if (WebLoginHolder.isLogin(view, request)) {
            view.setViewName("wx/mine/myorder");
            view.addObject("type", type);
        }
        return view;
    }

    @RequestMapping("/wx/mine/search_order")
    public ModelAndView mineSearchMyorder(HttpServletRequest request,
                                          ModelAndView view,
                                          String keyword) {
        if (WebLoginHolder.isLogin(view, request)) {
            view.setViewName("wx/mine/search_order");
            view.addObject("keyword", keyword);
        }
        return view;
    }

    @RequestMapping("/wx/mine/order_detail")
    public ModelAndView mineOrderDetail(HttpServletRequest request,
                                        ModelAndView view,
                                        @RequestParam(defaultValue = "0") long oid) {
        if (WebLoginHolder.isLogin(view, request)) {
            view.setViewName("wx/mine/order_detail");
            ModelObject order = GlobalService.orderService.getUserOrderById(WebLoginHolder.getUid(), oid);
            if (order != null) {
                if (order.isNotEmpty(TableOrderAddress.class.getSimpleName())) {
                    ModelObject address = order.getModelObject(TableOrderAddress.class);
                    if (address != null) {
                        address.put(TableOrderAddress.phoneNumber,
                                StringTools.replace(address.getString(TableOrderAddress.phoneNumber), 3, 7, "****"));
                    }
                }
                view.addObject("order", order);
                int status = order.getIntValue(TableOrder.status);
                String txt = GlobalService.systemConfigService.getValue("order_detail_status_" + status);
                view.addObject("statusText", txt);
            }
        }
        return view;
    }

    @RequestMapping("/wx/mine/myorder_list")
    @ResponseBody
    public String getMyOrderList(HttpServletRequest request,
                                 @RequestParam(defaultValue = "1") int page,
                                 String status) {
        ModelObject object = new ModelObject();
        object.put("success", 1);
        if (WebLoginHolder.isLogin()) {
            List<String> orderStatus = null;
            if (StringUtils.isNotBlank(status)) {
                orderStatus = Arrays.asList(status.split(","));
            }
            List<ModelObject> orders = GlobalService.orderService.getUserOrderByPage(WebLoginHolder.getUid(), orderStatus, page);
            object.put("data", orders);
        } else {
            object.put("success", 0);
            object.put("code", "not_login");
        }
        return object.toJSONString();
    }


    @RequestMapping("/wx/mine/get_myorder_search")
    @ResponseBody
    public String searchMyOrderList(HttpServletRequest request,
                                    @RequestParam(defaultValue = "1") int page,
                                    String keyword) {
        ModelObject object = new ModelObject();
        object.put("success", 1);
        if (WebLoginHolder.isLogin()) {
            List<ModelObject> orders = GlobalService.orderService.searchUserOrderByPage(WebLoginHolder.getUid(), keyword, page);
            object.put("data", orders);
        } else {
            object.put("success", 0);
            object.put("code", "not_login");
        }
        return object.toJSONString();
    }

    @RequestMapping("/wx/mine/myorder_del")
    @ResponseBody
    public String deleteMyOrder(HttpServletRequest request,
                                @RequestParam(defaultValue = "0") long oid) {
        ModelObject object = new ModelObject();
        object.put("success", 1);
        if (WebLoginHolder.isLogin()) {
            try {
                GlobalService.orderService.deleteUserOrder(WebLoginHolder.getUid(), oid);
                return object.toJSONString();
            } catch (Exception e) {
                object.put("success", 0);
                object.put("code", "server_error");
                return object.toJSONString();
            }
        } else {
            object.put("success", 0);
            object.put("code", "not_login");
        }
        return object.toJSONString();
    }

    @RequestMapping("/wx/mine/myorder_cancel")
    @ResponseBody
    public String cancelMyOrder(HttpServletRequest request,
                                @RequestParam(defaultValue = "0") long oid) {
        ModelObject object = new ModelObject();
        object.put("success", 1);
        if (WebLoginHolder.isLogin()) {
            try {
                GlobalService.orderService.cancelOrder(WebLoginHolder.getUid(), oid);
                return object.toJSONString();
            } catch (Exception e) {
                object.put("success", 0);
                object.put("code", "server_error");
                return object.toJSONString();
            }
        } else {
            object.put("success", 0);
            object.put("code", "not_login");
        }
        return object.toJSONString();
    }

    @RequestMapping("/wx/mine/myorder_confirm")
    @ResponseBody
    public String confirmMyOrder(HttpServletRequest request,
                                 @RequestParam(defaultValue = "0") long oid) {
        ModelObject object = new ModelObject();
        object.put("success", 1);
        if (WebLoginHolder.isLogin()) {
            try {
                GlobalService.orderService.confirmOrder(WebLoginHolder.getUid(), oid);
                return object.toJSONString();
            } catch (Exception e) {
                object.put("success", 0);
                object.put("code", "server_error");
                return object.toJSONString();
            }
        } else {
            object.put("success", 0);
            object.put("code", "not_login");
        }
        return object.toJSONString();
    }

    @RequestMapping("/wx/mine/myorder_buy_again")
    @ResponseBody
    public String buyAgainMyOrder(@RequestParam(defaultValue = "0") long oid) {
        ModelObject object = new ModelObject();
        object.put("success", 1);
        if (WebLoginHolder.isLogin()) {
            try {
                ModelObject order = GlobalService.orderService.getSimpleOrderById(oid);
                if (order != null) {
                    if (order.getLongValue(TableOrder.userId) == WebLoginHolder.getUid()) {
                        List<ModelObject> orderGoods = GlobalService.orderService.getOrderGoods(oid);

                        List<String> carts = new ArrayList<>();
                        for (ModelObject orderGoodsItem : orderGoods) {
                            ModelObject mCart = new ModelObject();
                            mCart.put(TableShoppingCart.gid, orderGoodsItem.getLongValue(TableOrderGoods.goodsId));
                            mCart.put(TableShoppingCart.skuId, orderGoodsItem.getLongValue(TableOrderGoods.skuId));
                            mCart.put(TableShoppingCart.userId, WebLoginHolder.getUid());
                            mCart.put(TableShoppingCart.amount, orderGoodsItem.getIntValue(TableOrderGoods.amount));
                            try {
                                GlobalService.shoppingCartService.addCartGoods(mCart);
                                carts.add(mCart.getString(TableShoppingCart.id));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        object.put("data", String.join(",", carts));
                        return object.toJSONString();
                    } else {
                        object.put("success", 0);
                        object.put("code", "order_not_found");
                        return object.toJSONString();
                    }
                } else {
                    object.put("success", 0);
                    object.put("code", "order_not_found");
                    return object.toJSONString();
                }
            } catch (Exception e) {
                object.put("success", 0);
                object.put("code", "server_error");
                return object.toJSONString();
            }
        } else {
            object.put("success", 0);
            object.put("code", "not_login");
        }
        return object.toJSONString();
    }

    @RequestMapping("/wx/mine/myorder_rebuy")
    public ModelAndView rebuyOrder(
            ModelAndView view,
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") long oid) {
        if (WebLoginHolder.isLogin(view, request)) {
            List<ModelObject> goods = GlobalService.orderService.getOrderGoods(oid);
            List<String> ids = new ArrayList<>();
            if (goods != null && goods.size() > 0) {
                for (ModelObject g : goods) {
                    long gid = g.getLongValue(TableOrderGoods.goodsId);
                    long skuId = g.getLongValue(TableOrderGoods.skuId);
                    int amount = g.getIntValue(TableOrderGoods.amount);

                    ModelObject mCart = new ModelObject();
                    mCart.put(TableShoppingCart.gid, gid);
                    mCart.put(TableShoppingCart.skuId, skuId);
                    mCart.put(TableShoppingCart.userId, WebLoginHolder.getUid());
                    mCart.put(TableShoppingCart.amount, amount);
                    try {
                        mCart.put("fast", true); //标识立即购买，数量直接覆盖
                        GlobalService.shoppingCartService.addCartGoods(mCart);
                        ids.add(mCart.getString(TableShoppingCart.id));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            view.setViewName("redirect:" +
                    RequestUtils.getWebUrl(request) +
                    "/wx/order.jhtml?c=" + String.join(",", ids));
        }
        return view;
    }
}
