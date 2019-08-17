package com.jianzixing.webapp.service.coupon;

public enum CouponChannelType {
    OTHER(0, "其他渠道"),
    WEB(1, "网站领取"),
    IGNORE(99, "忽略获取渠道");

    private int code;
    private String msg;

    CouponChannelType(int code, String msg) {
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
