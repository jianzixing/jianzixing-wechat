package com.jianzixing.webapp.service.shopcart;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.order.OrderDiscountModel;
import com.jianzixing.webapp.service.order.OrderGoodsModel;
import org.mimosaframework.core.json.ModelObject;

import java.util.List;

public interface ShoppingCartService {
    void addCartGoods(ModelObject cart) throws Exception; //添加购物车

    void modifyCartGoods(ModelObject cart) throws Exception; //修改购物车 数量，活动

    void modifyCartCheckState(ModelObject cart) throws ModuleException; //修改购物车选中信息

    void deleteCartGoods(long userId, long goodsId, long skuId);

    void deleteCartGoods(long userId, long goodsId);

    List<ModelObject> getCarts(long userId);  //获取购物车商品信息列表
    List<ModelObject> getCheckCarts(long userId);  //获取选中的购物车商品信息列表

    List<OrderGoodsModel> getCartPriceTotal(ModelObject user) throws ModuleException;  //获取购物车价格信息

    List<OrderGoodsModel> getCartPriceTotal(long userId) throws ModuleException;  //获取购物车价格信息

    List<ModelObject> getUserCarts(long uid, List ids);

    /**
     * 下单之后删除购物车商品
     *
     * @param uid
     * @param ids
     */
    void deleteCarts(long uid, List<String> ids);
}
