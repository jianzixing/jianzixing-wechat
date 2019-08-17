package com.jianzixing.webapp.service.wechat.model;

public class OAuthAuthorizeConfig {
    private String publicAccountCode;
    /**
     * 授权后重定向的回调链接地址， 请使用 urlEncode 对链接进行处理
     */
    private String redirectUri;
    /**
     * 返回类型，请填写code
     */
    private String responseType = "code";
    /**
     * 应用授权作用域，
     * snsapi_base （不弹出授权页面，直接跳转，只能获取用户openid），
     * snsapi_userinfo （弹出授权页面，可通过openid拿到昵称、性别、所在地。并且， 即使在未关注的情况下，只要用户授权，也能获取其信息 ）
     */
    private String scope = "snsapi_userinfo";
    /**
     * 重定向后会带上state参数，开发者可以填写a-zA-Z0-9的参数值，最多128字节
     */
    private String state = "S0";

    public String getPublicAccountCode() {
        return publicAccountCode;
    }

    public void setPublicAccountCode(String publicAccountCode) {
        this.publicAccountCode = publicAccountCode;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
