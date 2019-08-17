package com.jianzixing.webapp.service.payment;

public enum PaymentRefundType {
    AFTER_SALES(1, "售后退货退款"),
    ORDER_CANCEL(2, "订单取消退款");

    private int code;
    private String msg;

    PaymentRefundType(int code, String msg) {
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
