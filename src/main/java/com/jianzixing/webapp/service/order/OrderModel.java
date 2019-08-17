package com.jianzixing.webapp.service.order;

import com.jianzixing.webapp.service.logistics.LogisticsFreightModel;
import com.jianzixing.webapp.tables.goods.TableGoods;
import com.jianzixing.webapp.tables.goods.TableGoodsSku;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.calculator.CalcNumber;

import java.math.BigDecimal;
import java.util.List;

public class OrderModel {
    private PlatformType platformType = PlatformType.ALL;
    private boolean isOrderCreate = false;
    private long uid;
    private String orderNumber;
    private ModelObject user;
    private long aid;

    private long couponId;

    private ModelObject address;
    private List<ModelObject> paymentChannelParams;
    private List<OrderGoodsModel> products;
    private int deliveryType;
    // 创建订单时不存在，支付订单或者其它操作才有
    private long oid;

    // 商品原始价格
    private BigDecimal constProductPrice;
    // 商品优惠后价格
    private BigDecimal productPrice;
    // 商品运费价格
    private BigDecimal freightPrice;
    // 商品促销价格
    private BigDecimal discountPrice;
    // 商品优惠券价格
    private BigDecimal couponPrice;
    // 整个订单价格
    private BigDecimal orderPrice;

    private LogisticsFreightModel freightModel;

    private List<OrderDiscountModel> discounts;
    private List<OrderDiscountModel> coupons;

    private String message;
    private ModelObject invoice;

    public ModelObject getInvoice() {
        return invoice;
    }

    public void setInvoice(ModelObject invoice) {
        this.invoice = invoice;
    }

    public String getMessage() {
        if (message != null && message.length() > 300) {
            return message.substring(0, 300);
        }
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PlatformType getPlatformType() {
        return platformType;
    }

    public void setPlatformType(PlatformType platformType) {
        this.platformType = platformType;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public ModelObject getUser() {
        return user;
    }

    public void setUser(ModelObject user) {
        this.user = user;
    }

    public long getAid() {
        return aid;
    }

    public void setAid(long aid) {
        this.aid = aid;
    }

    public ModelObject getAddress() {
        return address;
    }

    public void setAddress(ModelObject address) {
        this.address = address;
    }

    public List<ModelObject> getPaymentChannelParams() {
        return paymentChannelParams;
    }

    public void setPaymentChannelParams(List<ModelObject> paymentChannelParams) {
        this.paymentChannelParams = paymentChannelParams;
    }

    public List<OrderGoodsModel> getProducts() {
        return products;
    }

    public void setProducts(List<OrderGoodsModel> products) {
        this.products = products;
        // 开始计算必要值
        CalcNumber totalProductPrice = CalcNumber.as(0);
        for (OrderGoodsModel p : products) {
            ModelObject product = p.getGoods();
            int buyAmount = p.getBuyAmount();
            String price = product.getString(TableGoods.price);
            double weight = product.getDoubleValue(TableGoods.weight);
            double volume = product.getDoubleValue(TableGoods.volume);
            ModelObject sku = p.getSku();
            if (sku != null) {
                price = sku.getString(TableGoodsSku.price);
            }

            p.setTotalPrice(CalcNumber.as(price).multiply(buyAmount).toBigDecimal());
            p.setTotalWeight(CalcNumber.as(weight).multiply(buyAmount).toBigDecimal());
            p.setTotalVolume(CalcNumber.as(volume).multiply(buyAmount).toBigDecimal());

            totalProductPrice.add(p.getTotalPrice());
        }

        this.constProductPrice = totalProductPrice.toBigDecimal();
        this.orderPrice = this.constProductPrice;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public BigDecimal getFreightPrice() {
        return freightPrice;
    }

    public void setFreightPrice(BigDecimal freightPrice) {
        this.freightPrice = freightPrice;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public BigDecimal getCouponPrice() {
        return couponPrice;
    }

    public void setCouponPrice(BigDecimal couponPrice) {
        this.couponPrice = couponPrice;
    }

    public List<OrderDiscountModel> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<OrderDiscountModel> discounts) {
        this.discounts = discounts;
    }

    public List<OrderDiscountModel> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<OrderDiscountModel> coupons) {
        this.coupons = coupons;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
        if (CalcNumber.as(orderPrice).toDouble() < 0) {
            this.orderPrice = new BigDecimal(0);
        }
    }

    public BigDecimal getConstProductPrice() {
        return constProductPrice;
    }

    public void setConstProductPrice(BigDecimal constProductPrice) {
        this.constProductPrice = constProductPrice;
    }

    public LogisticsFreightModel getFreightModel() {
        return freightModel;
    }

    public void setFreightModel(LogisticsFreightModel freightModel) {
        this.freightModel = freightModel;
    }

    public int getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(int deliveryType) {
        this.deliveryType = deliveryType;
    }

    public long getOid() {
        return oid;
    }

    public void setOid(long oid) {
        this.oid = oid;
    }

    public long getCouponId() {
        return couponId;
    }

    public void setCouponId(long couponId) {
        this.couponId = couponId;
    }

    public boolean isOrderCreate() {
        return isOrderCreate;
    }

    public void setOrderCreate(boolean orderCreate) {
        isOrderCreate = orderCreate;
    }
}
