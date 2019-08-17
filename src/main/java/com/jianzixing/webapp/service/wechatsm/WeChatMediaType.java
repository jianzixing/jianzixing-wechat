package com.jianzixing.webapp.service.wechatsm;

public enum WeChatMediaType {
    TI(1, "图文"),
    TEXT(2, "文字"),
    IMAGE(3, "图片"),
    VOICE(4, "语音"),
    VIDEO(5, "视频");

    private int code;
    private String name;

    WeChatMediaType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
