package com.jianzixing.webapp.service.coupon;

public enum CouponStatus {
    DISABLE(0, "未启用"),
    BEFORE(1, "未开始"),
    GETTING(2, "获取中"),
    FINISH(3, "已结束");

    private int code;
    private String msg;

    CouponStatus(int code, String msg) {
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
