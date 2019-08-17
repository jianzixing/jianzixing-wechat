package com.jianzixing.webapp.service.marketing;

public enum SmsAction {
    OTHER(0, "其他类型"),
    CODE(1, "验证码");
    private int code;
    private String msg;

    SmsAction(int code, String msg) {
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
