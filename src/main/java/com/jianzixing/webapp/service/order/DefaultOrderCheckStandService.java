package com.jianzixing.webapp.service.order;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.logistics.LogisticsFreightModel;
import com.jianzixing.webapp.tables.order.TableOrder;
import com.jianzixing.webapp.tables.user.TableUser;
import org.mimosaframework.core.json.ModelObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultOrderCheckStandService implements OrderCheckstandInterface {

    /**
     * 1.首先计算商品优惠，如果商品有优惠则如果多个商品同一个优惠活动则均摊优惠钱数
     * 2.如果有其他的，比如卡券之类的再计算卡券之类的所有优惠，然后均摊优惠
     * 3.最后再计算运费
     *
     * @param orderModel
     * @return
     */
    @Override
    public OrderModel checkin(OrderModel orderModel) throws ModuleException {
        List<OrderGoodsModel> products = orderModel.getProducts();
        BigDecimal payGoodsPrice = new BigDecimal("0");

        for (OrderGoodsModel p : products) {
            p.setDeliveryType(orderModel.getDeliveryType());
        }

        // 开始计算每个商品的优惠活动，如果多个商品同一个优惠活动则均摊优惠价格
        List<OrderDiscountModel> discountGoodsModels = null;
        discountGoodsModels = GlobalService.discountService.calDiscount(orderModel);
        orderModel.setDiscounts(discountGoodsModels);


        // 然后再计算是否使用优惠券
        List<OrderDiscountModel> couponGoodsModels = null;
        if (orderModel.getCouponId() > 0) {
            couponGoodsModels = GlobalService.couponService.calCoupon(orderModel);
            orderModel.setCoupons(couponGoodsModels);
        }


        // 最后需要先算好优惠活动后平摊优惠再计算运费
        LogisticsFreightModel freightModel = GlobalService.logisticsService.calFreight(orderModel, products);
        BigDecimal freightPrice = freightModel.getFreight();
        BigDecimal discountPrice = new BigDecimal(0);
        if (discountGoodsModels != null) {
            for (OrderDiscountModel discountGoodsModel : discountGoodsModels) {
                if (discountGoodsModel != null && discountGoodsModel.getDiscountPrice() != null) {
                    discountPrice = discountPrice.add(discountGoodsModel.getDiscountPrice());
                }
            }
        }
        BigDecimal couponPrice = new BigDecimal(0);
        if (couponGoodsModels != null) {
            for (OrderDiscountModel couponGoodsModel : couponGoodsModels) {
                if (couponGoodsModel != null && couponGoodsModel.getDiscountPrice() != null) {
                    couponPrice = couponPrice.add(couponGoodsModel.getDiscountPrice());
                }
            }
        }

        // 优惠后的商品价格
        orderModel.setProductPrice(payGoodsPrice);
        orderModel.setFreightPrice(freightPrice);
        orderModel.setCouponPrice(couponPrice);
        orderModel.setFreightModel(freightModel);
        // 优惠后的订单价格
        BigDecimal orderPrice = orderModel.getConstProductPrice();
        if (discountPrice != null) {
            orderPrice = orderPrice.subtract(discountPrice);
        }
        if (couponPrice != null) {
            orderPrice = orderPrice.subtract(couponPrice);
        }

        // 计算整个订单的优惠金额
        BigDecimal orderDiscountPrice = discountPrice.add(couponPrice);
        orderModel.setDiscountPrice(orderDiscountPrice);

        orderPrice = orderPrice.add(freightPrice);
        orderModel.setOrderPrice(orderPrice);
        orderModel.setProductPrice(orderModel.getConstProductPrice().subtract(orderDiscountPrice));
        return orderModel;
    }

    /**
     * 如果取消订单或者删除订单退货之类的可能会退回用户相关的优惠之类的东西
     *
     * @param order
     */
    @Override
    public void checkout(ModelObject order) {
        if (order != null) {
            GlobalService.discountService.handBack(order);
            GlobalService.couponService.handBack(order);
        }
    }
}
