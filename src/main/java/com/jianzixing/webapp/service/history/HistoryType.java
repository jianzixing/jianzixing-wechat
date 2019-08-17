package com.jianzixing.webapp.service.history;

public enum HistoryType {
    GOODS(1, "商品收藏");

    private int code;
    private String msg;

    HistoryType(int code, String msg) {
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
