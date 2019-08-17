package com.jianzixing.webapp.service.hotsearch;

public enum HotSearchType {
    WE_CHAT("wx"), PC("pc");

    private String code;

    HotSearchType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
