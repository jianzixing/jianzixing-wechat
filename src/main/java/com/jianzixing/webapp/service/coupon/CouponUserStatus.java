package com.jianzixing.webapp.service.coupon;

public enum CouponUserStatus {
    NORMAL(0, "正常"),
    USED(1, "已使用"),
    EXPIRED(2, "已过期"),
    DECLARE(3, "已作废");

    private int code;
    private String msg;

    CouponUserStatus(int code, String msg) {
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
