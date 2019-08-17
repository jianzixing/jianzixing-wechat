package com.jianzixing.webapp.service.order;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;
import org.mimosaframework.orm.exception.TransactionException;

import java.util.Date;
import java.util.List;

public interface OrderService {
    ModelObject addOrder(ModelObject order, List<ModelObject> products) throws ModuleException;

    ModelObject getOrderPrices(ModelObject order, List<ModelObject> products) throws ModuleException;

    /**
     * 取消订单
     * 取消后退回订单使用的所有数据
     * 比如，优惠券，积分，余额，购物卡
     *
     * @param order
     * @throws ModuleException
     */
    void cancelOrder(ModelObject order) throws ModuleException, TransactionException;

    /**
     * 同上
     *
     * @param orderId
     * @throws ModuleException
     */
    void cancelOrder(long orderId) throws ModuleException, TransactionException;

    /**
     * 用户取消自己的订单
     * 取消后退回订单使用的所有数据
     * 比如，优惠券，积分，余额，购物卡
     *
     * @param uid
     * @param orderId
     * @throws ModuleException
     */
    void cancelOrder(long uid, long orderId) throws ModuleException, TransactionException;

    Paging getOrders(ModelObject search, List<Integer> status, long start, long limit);

    Paging getSearchOrder() throws ModuleException;

    void deleteOrder(long orderId) throws TransactionException;

    ModelObject getSimpleOrderById(long orderId);

    ModelObject getSimpleOrderByNumber(String orderNumber);

    /**
     * 获取一个订单，前台使用方法
     *
     * @param uid
     * @param orderId
     * @return
     */
    ModelObject getOrderById(long uid, long orderId);

    /**
     * 获取一个订单，后台使用方法
     *
     * @param orderId
     * @return
     */
    ModelObject getOrderById(long orderId);

    void updateOrderStatus(long orderId, int code);

    /**
     * 开始发货
     *
     * @param orderId
     * @param logisticsCompanyCode
     * @param trackingNumber
     */
    void sendOutOrder(long orderId, String logisticsCompanyCode, String trackingNumber) throws ModuleException;

    Paging getStatusOrders(Query query, long uid, OrderStatus status, long start, long limit);

    /**
     * 设置订单商品为发货状态并设置发货物流及物流单号
     *
     * @param oid
     * @param code
     * @param number
     * @throws ModuleException
     */
    void updateOrderLogistics(long oid, String code, String number) throws ModuleException;

    /**
     * 设置订单商品为出库状态
     *
     * @param id
     * @throws ModuleException
     */
    void setOrderDelivery(long id) throws ModuleException;

    void updateOrderPrice(long id, String price) throws ModuleException;

    void updateOrderAddress(ModelObject object) throws ModuleException, ModelCheckerException;

    /**
     * 通过订单商品ID获取一个订单商品
     *
     * @param orderGoodsId
     * @return
     */
    ModelObject getOrderGoodsById(long orderGoodsId);

    /**
     * 通过一个订单号获取当前订单的所有商品
     *
     * @param orderId
     * @return
     */
    List<ModelObject> getOrderGoods(long orderId);

    /**
     * 获得一个订单商品，带有sku信息的
     *
     * @param orderGoodsId
     * @return
     */
    ModelObject getOrderGoodsWithSku(long orderGoodsId);

    Date getOrderExpireTime(Date orderTime);

    /**
     * 设置订单支付成功
     *
     * @param orderNumber
     */
    void setOrderPaySuccess(String orderNumber);

    ModelObject getOrderByNumber(long uid, String number, OrderStatus orderStatus);

    /**
     * 设置订单状态为部分支付状态
     *
     * @param orderNumber
     */
    void setOrderPayPartSuccess(String orderNumber);

    /**
     * 获取用户的订单列表
     * 可以传入不同状态
     * 然后分页
     *
     * @param uid
     * @param status
     * @param page
     * @return
     */
    List<ModelObject> getUserOrderByPage(long uid, List<String> status, int page);

    List<ModelObject> searchUserOrderByPage(long uid, String keyword, int page);

    void deleteUserOrder(long uid, long oid);

    ModelObject getUserOrderById(long uid, long oid);

    /**
     * 用户确认收货
     *
     * @param uid
     * @param oid
     */
    void confirmOrder(long uid, long oid);

    /**
     * 设置订单已评价
     * 如果订单有多个商品则有一个评价就算
     */
    void setOrderComment(long uid, long oid);

    void setLastAfterSaleType(long orderGoodsId, int afterSaleType);

    /**
     * 获取一个订单的地址
     *
     * @param orderId
     * @return
     */
    ModelObject getOrderAddress(long orderId);

    long getOrderCount(OrderStatus... statuses);
}
