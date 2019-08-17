package com.jianzixing.webapp.service.aftersales;

/**
 * 退货和换货和维修都有一定的时间期限
 */
public enum AfterSalesType {
    REFUND(10, "退货"),
    EXCHANGE(20, "换货"),
    REPAIR(30, "维修");

    private int code;
    private String msg;

    AfterSalesType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static AfterSalesType get(int type) {
        if (type == 10) return REFUND;
        if (type == 20) return EXCHANGE;
        if (type == 30) return REPAIR;
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
