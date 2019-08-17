package com.jianzixing.webapp.service.wechatsm;

public enum WeChatReplyType {
    FULL_KEYWORD(1, "全匹配关键字"),
    HALF_KEYWORD(2, "半匹配关键字"),
    CONCERN(5, "关注公众号"),
    CANCEL_CONCERN(6, "取消关注"),
    PARAM_QRCODE(7, "扫描带参数二维码"),
    CUSTOM_MENU(8, "自定义菜单事件");

    private int code;
    private String msg;

    WeChatReplyType(int code, String msg) {
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
