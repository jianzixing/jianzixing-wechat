package com.jianzixing.webapp.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import com.jianzixing.webapp.handler.WebLoginHolder;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.collect.CollectType;
import com.jianzixing.webapp.service.coupon.CouponChannelType;
import com.jianzixing.webapp.service.order.OrderDiscountModel;
import com.jianzixing.webapp.service.order.OrderGoodsModel;
import com.jianzixing.webapp.tables.shopcart.TableShoppingCart;
import org.mimosaframework.core.utils.RequestUtils;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelArray;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.exception.TransactionException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class WebWXCartController {
    @RequestMapping("/wx/cart")
    public ModelAndView cart(HttpServletRequest request,
                             ModelAndView view) {
        // todo: 如果没有登录则从cookie中读取购物车
        // todo: ...
        if (WebLoginHolder.isLogin(view, request)) {
            long userId = WebLoginHolder.getUid();
            List<ModelObject> carts = GlobalService.shoppingCartService.getCarts(userId);
            view.addObject("carts", carts);
            view.addObject("cartsStr", JSON.toJSONString(carts));
            if (carts != null && carts.size() > 0) {
                List<ModelObject> goodsList = new ArrayList<>();
                carts.forEach(item -> {
                    goodsList.add(item.getModelObject("TableGoods"));
                });
                List<ModelObject> couponList = GlobalService.couponService.getUserCouponsByGoods(GlobalService.userService.getUser(userId), goodsList);
                view.addObject("coupons", couponList);
            }

            view.setViewName("wx/cart");
        }
        return view;
    }

    @RequestMapping("/wx/cart_buy_now") //立即购买
    public ModelAndView buyNowCart(HttpServletRequest request,
                                   ModelAndView view,
                                   String goods) {
        if (WebLoginHolder.isLogin(view, request)) {
            if (StringUtils.isNotBlank(goods)) {
                String[] s = goods.split(",");
                ModelObject mCart = new ModelObject();
                mCart.put(TableShoppingCart.gid, Long.parseLong(s[0]));
                mCart.put(TableShoppingCart.skuId, StringUtils.isNotBlank(s[1]) ? Long.parseLong(s[1]) : 0);
                mCart.put(TableShoppingCart.userId, WebLoginHolder.getUid());
                mCart.put(TableShoppingCart.amount, Integer.parseInt(s[2]));
                try {
                    mCart.put("fast", true); //标识立即购买，数量直接覆盖
                    GlobalService.shoppingCartService.addCartGoods(mCart);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                view.setViewName("redirect:" +
                        RequestUtils.getWebUrl(request) +
                        "/wx/order.jhtml?c=" + mCart.getLongValue(TableShoppingCart.id));
            }
        }
        return view;
    }

    @RequestMapping("/wx/add_shopping_cart")
    @ResponseBody
    private String addShoppingCart(int gid,
                                   @RequestParam(required = false, defaultValue = "0") int skuId,
                                   @RequestParam(required = false, defaultValue = "1") int amount) {
        // todo:如果没有登录则添加到cookie中去
        // todo: ...
        if (WebLoginHolder.isLogin()) {
            ModelObject mCart = new ModelObject();
            mCart.put(TableShoppingCart.gid, gid);
            mCart.put(TableShoppingCart.skuId, skuId);
            mCart.put(TableShoppingCart.userId, WebLoginHolder.getUid());
            mCart.put(TableShoppingCart.amount, amount);
            try {
                GlobalService.shoppingCartService.addCartGoods(mCart);
            } catch (Exception e) {
                return JSON.toJSONString(new ResponseMessage(99, e.getMessage()));
            }
            return JSON.toJSONString(new ResponseMessage());
        }
        return JSON.toJSONString(new ResponseMessage(999)); //未登录
    }

    @RequestMapping("/wx/modify_shopping_cart")
    @ResponseBody
    private String modifyShoppingCart(int gid,
                                      @RequestParam(required = false, defaultValue = "0") int skuId,
                                      @RequestParam(required = false) String amount,
                                      @RequestParam(required = false) String did) {
        if (WebLoginHolder.isLogin()) {
            ModelObject mCart = new ModelObject();
            mCart.put(TableShoppingCart.gid, gid);
            mCart.put(TableShoppingCart.skuId, skuId);
            mCart.put(TableShoppingCart.userId, WebLoginHolder.getUid());
            if (StringUtils.isNotBlank(amount)) {
                mCart.put(TableShoppingCart.amount, amount);
            }
            if (StringUtils.isNotBlank(did)) {
                mCart.put(TableShoppingCart.discountId, did);
            }
            try {
                GlobalService.shoppingCartService.modifyCartGoods(mCart);
            } catch (Exception e) {
                return JSON.toJSONString(new ResponseMessage(99, e.getMessage()));
            }
            return JSON.toJSONString(new ResponseMessage());
        }
        return JSON.toJSONString(new ResponseMessage(999));
    }

    @RequestMapping("/wx/remove_shopping_cart")
    @ResponseBody
    private String removeShoppingCart(int gid, @RequestParam(required = false, defaultValue = "0") int skuId) {
        if (WebLoginHolder.isLogin()) {
            try {
                GlobalService.shoppingCartService.deleteCartGoods(WebLoginHolder.getUid(), gid, skuId);
            } catch (Exception e) {
                return JSON.toJSONString(new ResponseMessage(99, e.getMessage()));
            }
            return JSON.toJSONString(new ResponseMessage());
        }
        return JSON.toJSONString(new ResponseMessage(999));
    }

    @RequestMapping("/wx/get_cart_price")
    @ResponseBody
    public String getCartPrice() {
        if (WebLoginHolder.isLogin()) {
            List<OrderGoodsModel> price;
            try {
                price = GlobalService.shoppingCartService.getCartPriceTotal(WebLoginHolder.getUid());
                if (price != null && price.size() > 0) {
                    DecimalFormat df = new DecimalFormat("#0.00");
                    BigDecimal totalPrice = new BigDecimal(0);
                    BigDecimal payPrice = new BigDecimal(0);
                    BigDecimal discountPrice = new BigDecimal(0);
                    for (OrderGoodsModel goodsModel : price) {
                        totalPrice = totalPrice.add(goodsModel.getTotalPrice());
                        payPrice = payPrice.add(goodsModel.getPayPrice());
                        discountPrice = discountPrice.add(totalPrice.subtract(payPrice));
                    }
                    ModelObject cartPrice = new ModelObject();
                    cartPrice.put("totalPrice", df.format(totalPrice.setScale(2, BigDecimal.ROUND_HALF_UP)));
                    cartPrice.put("payPrice", df.format(payPrice.setScale(2, BigDecimal.ROUND_HALF_UP)));
                    cartPrice.put("discountPrice", df.format(discountPrice.setScale(2, BigDecimal.ROUND_HALF_UP)));
                    return JSON.toJSONString(new ResponseMessage(cartPrice));
                }
            } catch (ModuleException e) {
                return JSON.toJSONString(new ResponseMessage(99, e.getMessage()));
            }
        }
        return JSON.toJSONString(new ResponseMessage(999));
    }

    @RequestMapping("/wx/move_to_collect_goods")
    @ResponseBody
    public String setCollectGoods(@RequestParam(defaultValue = "0") long gid,
                                  @RequestParam(defaultValue = "0") long skuId) {
        if (WebLoginHolder.isLogin()) {
            long uid = WebLoginHolder.getUid();
            GlobalService.shoppingCartService.deleteCartGoods(uid, gid, skuId);
            GlobalService.collectService.addCollect(uid, gid, skuId, CollectType.GOODS);
            return JSON.toJSONString(new ResponseMessage());
        }
        return JSON.toJSONString(new ResponseMessage(999));
    }

    @RequestMapping("/wx/user_get_coupon")
    @ResponseBody
    public String userGetCoupon(@RequestParam long cid) {
        if (WebLoginHolder.isLogin()) {
            long uid = WebLoginHolder.getUid();
            try {
                GlobalService.couponService.userGetCoupon(uid, cid, CouponChannelType.WEB);
            } catch (ModuleException | TransactionException e) {
                return JSON.toJSONString(new ResponseMessage(99, e.getMessage()));
            }
            return JSON.toJSONString(new ResponseMessage());
        }
        return JSON.toJSONString(new ResponseMessage(999));
    }

    @RequestMapping("/wx/cart_update_check")
    @ResponseBody
    public String updateCheckState(@RequestParam String o) {
        ModelArray cartList=ModelObject.parseArray(o);
        if (cartList != null && cartList.size() > 0) {
            try {
                for (int i=0; i<cartList.size(); i++) {
                    GlobalService.shoppingCartService.modifyCartCheckState((ModelObject)cartList.get(i));
                }
            } catch (ModuleException e) {
                e.printStackTrace();
            }
        }
        return JSON.toJSONString(new ResponseMessage());
    }
}
