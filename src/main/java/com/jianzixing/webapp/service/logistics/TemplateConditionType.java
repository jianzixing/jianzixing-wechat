package com.jianzixing.webapp.service.logistics;

public enum TemplateConditionType {
    TYPE(1, "按照类型(件数，重量，体积)"),
    MONEY(2, "按金额"),
    TYPE_MONEY(3, "类型+金额");

    private int code;
    private String msg;

    TemplateConditionType(int code, String msg) {
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
