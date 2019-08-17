package com.jianzixing.webapp.service.order;

public enum OrderDiscussStatus {
    NOT_DISCUSS(0, "订单未评价"),
    DISCUSSED(1, "订单已评价");

    private int code;
    private String msg;

    OrderDiscussStatus(int code, String msg) {
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
