package com.jianzixing.webapp.service.wechatsm;

public enum WeChatAccountType {
    DYH(1, "普通订阅号"),
    FWH(2, "普通服务号"),
    RZ_DYH(3, "认证订阅号"),
    TS_ACC(4, "认证服务号/认证媒体/政府订阅号");
    private int code;
    private String msg;

    WeChatAccountType(int code, String msg) {
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
