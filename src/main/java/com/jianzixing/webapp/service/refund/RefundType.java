package com.jianzixing.webapp.service.refund;

public enum RefundType {
    SOURCE(10, "原路返还"),
    CARD(20, "退到银行卡"),
    BALANCE(30, "退到账户余额");
    private int code;
    private String msg;

    RefundType(int code, String msg) {
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
