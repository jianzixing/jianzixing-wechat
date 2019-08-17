package com.jianzixing.webapp.service.order;

public enum OrderRefundStatus {
    INIT(0, "订单新建"),
    FULL(1, "全部退款"),
    PART(2, "部分退款");

    private int code;
    private String msg;

    OrderRefundStatus(int code, String msg) {
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
