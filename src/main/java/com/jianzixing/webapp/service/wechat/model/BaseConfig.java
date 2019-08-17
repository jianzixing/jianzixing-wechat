package com.jianzixing.webapp.service.wechat.model;

public class BaseConfig {
    //公众号ID
    private long id;
    private String publicAccountCode;
    private String signature;
    private String timestamp;
    private String nonce;
    private String echostr;
    private String token;
    private String encodingAESKey;

    public String getPublicAccountCode() {
        return publicAccountCode;
    }

    public void setPublicAccountCode(String publicAccountCode) {
        this.publicAccountCode = publicAccountCode;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getEchostr() {
        return echostr;
    }

    public void setEchostr(String echostr) {
        this.echostr = echostr;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEncodingAESKey() {
        return encodingAESKey;
    }

    public void setEncodingAESKey(String encodingAESKey) {
        this.encodingAESKey = encodingAESKey;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
