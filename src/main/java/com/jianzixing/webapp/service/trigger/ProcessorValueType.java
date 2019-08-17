package com.jianzixing.webapp.service.trigger;

public enum ProcessorValueType {
    EMAIL(10, "邮件类型"),
    SMS(20, "短信类型"),
    MESSAGE(30, "站内信类型"),
    PARAMS(100, "参数类型");

    private int code;
    private String msg;

    ProcessorValueType(int code, String msg) {
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
