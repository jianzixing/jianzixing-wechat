package com.jianzixing.webapp.service.payment;

import org.mimosaframework.core.json.ModelObject;

import java.math.BigDecimal;

public class PaymentRefundParams {
    private long refundOrderId;// 退款单ID，必须参数
    private ModelObject refundOrder;
    private ModelObject channel;
    private ModelObject cnfParams;
    private ModelObject trans; // 交易单TablePaymentTransaction
    private BigDecimal payPrice;  // 退款金额
    private BigDecimal refundAmount;

    public PaymentRefundParams(ModelObject channel, ModelObject cnfParams, ModelObject trans, BigDecimal payPrice) {
        this.channel = channel;
        this.cnfParams = cnfParams;
        this.trans = trans;
        this.payPrice = payPrice;
    }

    public ModelObject getChannel() {
        return channel;
    }

    public void setChannel(ModelObject channel) {
        this.channel = channel;
    }

    public ModelObject getCnfParams() {
        return cnfParams;
    }

    public void setCnfParams(ModelObject cnfParams) {
        this.cnfParams = cnfParams;
    }

    public ModelObject getTrans() {
        return trans;
    }

    public void setTrans(ModelObject trans) {
        this.trans = trans;
    }

    public BigDecimal getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(BigDecimal payPrice) {
        this.payPrice = payPrice;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public long getRefundOrderId() {
        return refundOrderId;
    }

    public void setRefundOrderId(long refundOrderId) {
        this.refundOrderId = refundOrderId;
    }

    public ModelObject getRefundOrder() {
        return refundOrder;
    }

    public void setRefundOrder(ModelObject refundOrder) {
        this.refundOrder = refundOrder;
    }
}
