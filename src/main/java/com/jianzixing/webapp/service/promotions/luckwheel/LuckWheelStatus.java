package com.jianzixing.webapp.service.promotions.luckwheel;

public enum LuckWheelStatus {
    NOT_DELIVERY(0, "未发货"),
    DELIVERY(1, "已发货");

    private int code;
    private String msg;

    LuckWheelStatus(int code, String msg) {
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
