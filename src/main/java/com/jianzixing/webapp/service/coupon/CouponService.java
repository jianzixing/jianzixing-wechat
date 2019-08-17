package com.jianzixing.webapp.service.coupon;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.order.OrderDiscountModel;
import com.jianzixing.webapp.service.order.OrderModel;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.exception.TransactionException;

import java.util.List;

public interface CouponService {
    void saveCoupon(ModelObject object) throws ModelCheckerException, ModuleException;

    void delCoupon(long id);

    void updateCoupon(ModelObject object) throws ModuleException, ModelCheckerException;

    Paging getCoupons(ModelObject search, int start, int limit);

    void enableCoupon(long id);

    void finishCoupon(long id);

    void addCouponGoods(long id, List<ModelObject> gids) throws ModuleException;

    void removeCouponGoods(long id, long gid, long skuId);

    Paging getCouponGoods(long cid, int start, int limit);

    /**
     * 用户领取一个优惠券
     * 属于
     * {@link CouponService#userGetCoupon(ModelObject, ModelObject, CouponChannelType)}
     * 的包装
     *
     * @param uid
     * @param cid
     * @param channelType
     * @return
     * @throws ModuleException
     * @throws TransactionException
     */
    ModelObject userGetCoupon(long uid, long cid, CouponChannelType channelType) throws ModuleException, TransactionException;

    /**
     * 用户领取一个优惠券
     *
     * @param user
     * @param coupon
     * @param channelType
     * @throws ModuleException
     * @throws TransactionException
     */
    void userGetCoupon(ModelObject user, ModelObject coupon, CouponChannelType channelType) throws ModuleException, TransactionException;

    Paging getUserCoupons(ModelObject search, long cid, int start, int limit);

    /**
     * 获取一个用户未使用的优惠券
     *
     * @param uid
     */
    List<ModelObject> getUserValidCoupons(long uid);

    /**
     * 作废一个用户已领取的优惠券
     *
     * @param id
     */
    void declareUserCoupon(int id);

    /**
     * 获取特定状态的用户拥有的优惠券列表
     *
     * @param uid
     * @param page
     * @param status 特定状态
     * @return
     */
    List<ModelObject> getCouponsByUser(long uid, int page, CouponUserStatus... status);

    long getCouponsCountByUser(long uid, CouponUserStatus... status);

    /**
     * 获取一个商品可以领取的优惠券列表
     * 如果是单品类则skuId传入0
     * uid可以用来判断是否领取
     *
     * @param uid
     * @param gid
     * @param skuId
     * @return
     */
    List<ModelObject> getCouponsByGid(long uid, long gid, long skuId);

    /**
     * 方法同
     * {@link CouponService#getCouponsByGid(long, long, long)}
     * 类似
     * goodsList 属于商品列表，如果有sku则需要传入商品的SKU信息
     *
     * @param user
     * @param goodsList {... ,TableGoodsSku:{...}}
     * @return
     */
    List<ModelObject> getUserCouponsByGoods(ModelObject user, List<ModelObject> goodsList);

    /**
     * 方法同
     * {@link CouponService#getCouponsByGid(long, long, long)}
     * 类似
     * goodsList 属于商品列表，如果有sku则需要传入商品的SKU信息
     *
     * @param uid
     * @param goodsList {... ,TableGoodsSku:{...}}
     * @return
     */
    List<ModelObject> getUserCouponsInGoods(long uid, List<ModelObject> goodsList);

    /**
     * 用户下单时计算当前所选的优惠券优惠金额
     *
     * @param orderModel
     */
    List<OrderDiscountModel> calCoupon(OrderModel orderModel);

    /**
     * 用户取消订单后退回用户使用的优惠券
     *
     * @param order
     */
    void handBack(ModelObject order);

    /**
     * 获取一个用户有效优惠券的数量
     *
     * @param uid
     * @return
     */
    long getUserValidCouponCount(long uid);
}
