package com.jianzixing.webapp.service.payment;

import org.mimosaframework.core.json.ModelObject;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class OrderPayment {
    private long uid;
    private long rid;
    // 如果不是创建支付单，则所有数据库修改动作都不允许发生
    // 一般支付试算时使用
    private boolean isCreatePayment = false;
    // 前端必须传入
    private String orderNumber;
    private BigDecimal orderPrice;

    private String ip;
    private Date timeStart;
    private Date timeExpire;
    private String host;
    // 前端必须传入
    List<ModelObject> paymentChannels;

    private List<OrderPaymentProduct> products;
    private List<ModelObject> paymentChannelParams;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public long getRid() {
        return rid;
    }

    public void setRid(long rid) {
        this.rid = rid;
    }

    public List<OrderPaymentProduct> getProducts() {
        return products;
    }

    public void setProducts(List<OrderPaymentProduct> products) {
        this.products = products;
    }

    public List<ModelObject> getPaymentChannelParams() {
        return paymentChannelParams;
    }

    public void setPaymentChannelParams(List<ModelObject> paymentChannelParams) {
        this.paymentChannelParams = paymentChannelParams;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }

    public Date getTimeExpire() {
        return timeExpire;
    }

    public void setTimeExpire(Date timeExpire) {
        this.timeExpire = timeExpire;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public List<ModelObject> getPaymentChannels() {
        return paymentChannels;
    }

    public void setPaymentChannels(List<ModelObject> paymentChannels) {
        this.paymentChannels = paymentChannels;
    }

    public boolean isCreatePayment() {
        return isCreatePayment;
    }

    public void setCreatePayment(boolean createPayment) {
        isCreatePayment = createPayment;
    }
}
