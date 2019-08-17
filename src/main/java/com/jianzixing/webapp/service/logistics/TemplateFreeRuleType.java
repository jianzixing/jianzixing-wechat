package com.jianzixing.webapp.service.logistics;

public enum TemplateFreeRuleType {
    AMOUNT(11, "按件数"),
    WEIGHT(12, "按重量"),
    VOLUME(13, "按体积");

    private int code;
    private String msg;

    TemplateFreeRuleType(int code, String msg) {
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
