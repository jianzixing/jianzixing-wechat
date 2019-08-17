package com.jianzixing.webapp.service.order;

import org.mimosaframework.core.json.ModelObject;

import java.math.BigDecimal;

public class OrderGoodsModel {
    private long gid;
    private ModelObject goods;
    private int buyAmount;
    private long skuId;
    private ModelObject sku;
    // 没有任何折扣之前的价格，就是价格乘以数量
    private BigDecimal totalPrice;
    // 所有商品的重量
    private BigDecimal totalWeight;
    // 所有商品的体积
    private BigDecimal totalVolume;
    // 参见DeliveryType
    private int deliveryType;

    private BigDecimal payPrice;
    private BigDecimal discountPrice;

    // 如果有优惠活动及优惠券则是计算后得出的
    private long discountId;
    private ModelObject discount;
    private long couponId;
    private ModelObject coupon;


    public ModelObject getDiscount() {
        return discount;
    }

    public void setDiscount(ModelObject discount) {
        this.discount = discount;
    }

    public long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(long discountId) {
        this.discountId = discountId;
    }

    public long getCouponId() {
        return couponId;
    }

    public void setCouponId(long couponId) {
        this.couponId = couponId;
    }

    public ModelObject getCoupon() {
        return coupon;
    }

    public void setCoupon(ModelObject coupon) {
        this.coupon = coupon;
    }

    public long getGid() {
        return gid;
    }

    public void setGid(long gid) {
        this.gid = gid;
    }

    public ModelObject getGoods() {
        return goods;
    }

    public void setGoods(ModelObject goods) {
        this.goods = goods;
    }

    public int getBuyAmount() {
        return buyAmount;
    }

    public void setBuyAmount(int buyAmount) {
        this.buyAmount = buyAmount;
    }

    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    public ModelObject getSku() {
        return sku;
    }

    public void setSku(ModelObject sku) {
        this.sku = sku;
    }

    public BigDecimal getPayPrice() {
        if (payPrice == null) {
            payPrice = totalPrice;
        }
        return payPrice;
    }

    public void setPayPrice(BigDecimal payPrice) {
        this.payPrice = payPrice;
    }

    public BigDecimal getDiscountPrice() {
        if (discountPrice == null) {
            discountPrice = new BigDecimal(0);
        }
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    public BigDecimal getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(BigDecimal totalVolume) {
        this.totalVolume = totalVolume;
    }

    public int getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(int deliveryType) {
        this.deliveryType = deliveryType;
    }
}
