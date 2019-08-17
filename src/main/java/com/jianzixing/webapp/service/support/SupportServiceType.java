package com.jianzixing.webapp.service.support;

public enum SupportServiceType {
    SALES_RETURN(1, "支持退货"),
    EXCHANGE_GOODS(2, "支持换货"),
    MAINTAIN(3, "支持维修"),
    OTHER(0, "其他");

    private int code;
    private String msg;

    SupportServiceType(int code, String msg) {
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
