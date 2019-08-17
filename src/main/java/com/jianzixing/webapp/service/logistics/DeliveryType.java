package com.jianzixing.webapp.service.logistics;

public enum DeliveryType {
    FREE(1, "包邮"),
    EXPRESS(10, "快递"),
    EMS(11, "EMS"),
    SURFACE(12, "平邮");
    private int code;
    private String msg;

    DeliveryType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static DeliveryType get(int i) {
        if (i == DeliveryType.FREE.getCode()) return DeliveryType.FREE;
        if (i == DeliveryType.EXPRESS.getCode()) return DeliveryType.EXPRESS;
        if (i == DeliveryType.EMS.getCode()) return DeliveryType.EMS;
        if (i == DeliveryType.SURFACE.getCode()) return DeliveryType.SURFACE;
        return null;
    }

    public static DeliveryType getByType(int i) {
        if (i == DeliveryType.EXPRESS.getCode()) return DeliveryType.EXPRESS;
        if (i == DeliveryType.EMS.getCode()) return DeliveryType.EMS;
        if (i == DeliveryType.SURFACE.getCode()) return DeliveryType.SURFACE;
        return null;
    }
}
