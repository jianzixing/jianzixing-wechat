package com.jianzixing.webapp.service.spcard;

public enum BatchStatus {
    NORMAL(0, "未创建"),
    CREATED(1, "已创建"),
    DECLARE(2, "已作废");

    private int code;
    private String msg;

    BatchStatus(int code, String msg) {
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

    public static BatchStatus get(int code) {
        if (code == 0) return NORMAL;
        if (code == 1) return CREATED;
        if (code == 2) return DECLARE;
        return null;
    }
}
