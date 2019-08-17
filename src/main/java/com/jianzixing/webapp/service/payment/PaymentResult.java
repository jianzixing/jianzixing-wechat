package com.jianzixing.webapp.service.payment;

import org.mimosaframework.core.json.ModelObject;

import java.math.BigDecimal;

public class PaymentResult {
    private TYPE type;
    /**
     * 当前支付方式已经支付的钱数
     * 比如：积分支付了1块钱这里记录的就是一块钱
     */
    private BigDecimal payPrice;

    /**
     * 这里是消耗数量，如果是价格则是BigDecimal类型
     * 如果是积分则是long类型
     */
    private Object payAmount;

    /**
     * 如果是延迟支付的接口，则需要返回一个url
     * 如果有额外信息可以传入params中
     */
    private String url;
    private ModelObject params;
    /**
     * 第三方系统或者本系统的支付单号
     */
    private String outPaymentNumber;
    /**
     * 当前生成的订单的支付单号
     * 当支付系统回调回来时使用这个单号改变订单支付记录中的状态和外部单号
     */
    private String paymentNumber;

    public BigDecimal getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(BigDecimal payPrice) {
        this.payPrice = payPrice;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public Object getPayAmount() {
        return payAmount;
    }

    public BigDecimal getBigDecimalPayAmount() {
        return (BigDecimal) payAmount;
    }

    public long getLongPayAmount() {
        return (long) payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public void setPayAmount(long payAmount) {
        this.payAmount = payAmount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ModelObject getParams() {
        return params;
    }

    public void setParams(ModelObject params) {
        this.params = params;
    }

    public String getOutPaymentNumber() {
        return outPaymentNumber;
    }

    public void setOutPaymentNumber(String outPaymentNumber) {
        this.outPaymentNumber = outPaymentNumber;
    }

    public String getPaymentNumber() {
        return paymentNumber;
    }

    public void setPaymentNumber(String paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    public enum TYPE {
        OK, //即时支付方式，支付成功
        PART, //即时支付方式，支付部分金额
        URL,//延时支付方式，需要URL跳转
        QR_CODE, //延时支付方式，返回图片扫码
        JS_API, //延时支付方式，返回JS片段比如微信js调用支付
        PARAMS //只需要返回信息即可,剩下的交给前端做
    }

    public enum PriceType {
        LONG, // 整数类型
        DOUBLE // 小数类型
    }
}
