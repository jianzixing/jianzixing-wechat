package com.jianzixing.webapp.service.payment;

public enum PaymentTransactionType {
    ORDER(1, "订单支付"),
    RECHARGE(2, "余额充值");

    private int code;
    private String msg;

    PaymentTransactionType(int code, String msg) {
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
