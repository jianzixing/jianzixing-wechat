package com.jianzixing.webapp.service.wechat.model;

public class OAuthAccessTokenConfig {
    private String publicAccountCode;
    /**
     * 填写第一步获取的code参数
     * 第一步跳转后微信url带入
     */
    private String code;

    private String state;

    private String grantType = "authorization_code";

    public String getPublicAccountCode() {
        return publicAccountCode;
    }

    public void setPublicAccountCode(String publicAccountCode) {
        this.publicAccountCode = publicAccountCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
