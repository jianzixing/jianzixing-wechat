package com.jianzixing.webapp.service.order;

public enum OrderPayStatus {
    NOT_PAY(0, "订单未支付"),
    PAYED(1, "订单已支付"),
    PART_PAY(2, "订单已支付");

    private int code;
    private String msg;

    OrderPayStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
