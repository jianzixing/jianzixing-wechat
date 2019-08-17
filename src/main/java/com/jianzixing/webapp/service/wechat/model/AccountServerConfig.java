package com.jianzixing.webapp.service.wechat.model;

import com.jianzixing.webapp.service.wechat.WeChatOpenType;

public class AccountServerConfig extends AccountConfig {
    private String body;

    public AccountServerConfig(String code) {
        super(code);
    }

    public AccountServerConfig(String code, String appid) {
        super(code, appid);
    }

    public AccountServerConfig(String code, String appid, WeChatOpenType type) {
        super(code, appid, type);
    }

    public AccountServerConfig(String body, String code, String appid, WeChatOpenType type) {
        super(code, appid, type);
        this.body = body;
    }

    public static AccountServerConfig builder(String body, String code, String appid, WeChatOpenType type) {
        return new AccountServerConfig(body, code, appid, type);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
