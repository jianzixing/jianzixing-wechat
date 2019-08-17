package com.jianzixing.webapp.service.trigger;

public enum TimeType {
    YEAR(1, "年"),
    MONTH(2, "月"),
    DAY(3, "日"),
    HOUR(4, "时"),
    MINUTE(5, "分"),
    SECOND(6, "秒"),
    PERPETUAL(7, "永久");
    private int code;
    private String msg;

    TimeType(int code, String msg) {
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
