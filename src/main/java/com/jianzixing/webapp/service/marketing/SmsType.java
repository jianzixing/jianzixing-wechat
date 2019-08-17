package com.jianzixing.webapp.service.marketing;

public enum SmsType {
    TEXT(0, "文本格式内容"),
    // 比如短信的模板ID
    TEMPLATE(1, "模板格式内容");
    private int code;
    private String msg;

    SmsType(int code, String msg) {
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
