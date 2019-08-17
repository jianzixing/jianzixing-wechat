package com.jianzixing.webapp.service.spcard;

public enum ShoppingCardSpeedingType {
    SUB(0, "下单消费扣减"),
    ADD(1, "退款退还");

    private int code;
    private String msg;

    ShoppingCardSpeedingType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return this.code + "";
    }
}
