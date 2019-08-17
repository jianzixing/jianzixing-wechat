package com.jianzixing.webapp.service.order;

public enum PlatformType {
    ALL(0, "全平台"),
    PC(1, "PC"),
    APP(2, "APP"),
    WAP(3, "WAP"),
    WECHAT(4, "微信"),
    ALIPAY(5, "支付宝");
    private int code;
    private String name;

    PlatformType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static PlatformType get(int code) {
        if (code == 0) return PlatformType.ALL;
        if (code == 1) return PlatformType.PC;
        if (code == 2) return PlatformType.APP;
        if (code == 3) return PlatformType.WAP;
        if (code == 4) return PlatformType.WECHAT;
        if (code == 5) return PlatformType.ALIPAY;
        return PlatformType.ALL;
    }
}
