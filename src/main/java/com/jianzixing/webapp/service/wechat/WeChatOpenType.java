package com.jianzixing.webapp.service.wechat;

public enum WeChatOpenType {
    PUBLIC(1, "公众号"),
    OPEN_PUBLIC(2, "第三方平台公众号"),
    MINI_PROGRAM(3, "小程序"),
    OPEN_MINI_PROGRAM(4, "第三方平台小程序"),
    WEBSITE(5, "开放平台-网站应用"),
    APP(6, "开发平台-移动应用");

    private int code;
    private String name;

    WeChatOpenType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static WeChatOpenType getOpenType(int code) {
        if (code == 1) return PUBLIC;
        if (code == 2) return OPEN_PUBLIC;
        if (code == 3) return MINI_PROGRAM;
        if (code == 4) return OPEN_MINI_PROGRAM;
        if (code == 5) return WEBSITE;
        if (code == 6) return APP;
        return null;
    }
}
