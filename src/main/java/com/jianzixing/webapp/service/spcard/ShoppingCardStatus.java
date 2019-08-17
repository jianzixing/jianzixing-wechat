package com.jianzixing.webapp.service.spcard;

public enum ShoppingCardStatus {
    NORMAL(0, "未绑定"),
    DECLARE(1, "已作废"),
    USED(2, "已使用"), // 当余额为0时是已使用
    BIND(3, "已绑定"); // 余额未使用或使用一部分时

    private int code;
    private String msg;

    ShoppingCardStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return this.code + "";
    }

    public static ShoppingCardStatus get(int code) {
        if (code == 0) return NORMAL;
        if (code == 1) return DECLARE;
        if (code == 2) return USED;
        return null;
    }
}
