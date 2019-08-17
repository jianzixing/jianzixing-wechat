package com.jianzixing.webapp.service.payment;

/**
 * 区分支付实现是否是本系统直接支付的，比如余额、积分等等
 * 如果TIMELY表示是当前系统直接支付
 * 如果不是当前系统直接支付则需要一个回调过程
 * <p>
 * 另外即时支付允许多个支付方式同时使用，延迟支付只允许和即时支付组合使用不允许多个
 * 延迟支付方式同时使用
 */
public enum PaymentFlow {
    TIMELY(1, "即时支付,下单后立即扣款,如果扣除成功则直接已支付,如果支付了部分订单金额则继续等待支付"),
    DELAY(2, "延迟支付,下单后需要外部系统回调");

    private int code;
    private String msg;

    PaymentFlow(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
