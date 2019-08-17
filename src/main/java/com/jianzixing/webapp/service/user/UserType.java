package com.jianzixing.webapp.service.user;

public enum UserType {
    DEFAULT(10, "默认用户"),
    WECHAT(20, "微信用户");

    private int code;
    private String msg;

    UserType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }
}
