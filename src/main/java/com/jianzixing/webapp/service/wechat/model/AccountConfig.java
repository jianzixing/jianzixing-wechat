package com.jianzixing.webapp.service.wechat.model;

import com.jianzixing.webapp.service.wechat.WeChatOpenType;

/**
 * 账号配置需要准守一下规则
 * 1.如果WeChatOpenType.OPEN_PUBLIC 或者 WeChatOpenType.OPEN_MINI_PROGRAM则code和appid都必须填写
 * code是开放平台小程序的标识码(需要在第三方平台添加),appid是授权的小程序appid
 * 2.如果WeChatOpenType.PUBLIC 或者 WeChatOpenType.MINI_PROGRAM只需要填写code即可
 * 3.如果isAccount是true则只需要填写accountId和type即可
 */
public class AccountConfig {
    private String code;
    private String appid;
    private WeChatOpenType type = WeChatOpenType.PUBLIC;
    private int accountId;
    // 是否是使用当前系统的账号id
    // 表 TableWeChatAccount 或者 TableWeChatOpenAccount 的id
    // 如果不是则使用code获取数据，如果是则使用accountId获取数据
    private boolean isAccount;

    public AccountConfig(WeChatOpenType type, int accountId, boolean isAccount) {
        this.type = type;
        this.accountId = accountId;
        this.isAccount = isAccount;
    }

    public AccountConfig(WeChatOpenType type, String code) {
        this.type = type;
        this.code = code;
    }

    public AccountConfig(String code) {
        this.code = code;
    }

    public AccountConfig(String code, String appid) {
        this.code = code;
        this.appid = appid;
    }

    public AccountConfig(String code, String appid, WeChatOpenType type) {
        this.code = code;
        this.appid = appid;
        this.type = type;
    }

    public static AccountConfig builder(String code) {
        return new AccountConfig(code);
    }

    public static AccountConfig builder(String code, String appid) {
        return new AccountConfig(code, appid);
    }

    public static AccountConfig builder(String code, String appid, WeChatOpenType type) {
        return new AccountConfig(code, appid, type);
    }

    public static AccountConfig builder(int openType, int accountId) {
        return new AccountConfig(WeChatOpenType.getOpenType(openType), accountId, true);
    }

    public static AccountConfig builder(int openType, String code) {
        return new AccountConfig(WeChatOpenType.getOpenType(openType), code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public WeChatOpenType getType() {
        return type;
    }

    public void setType(WeChatOpenType type) {
        this.type = type;
    }

    public boolean isAccount() {
        return isAccount;
    }

    public void setAccount(boolean account) {
        isAccount = account;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
}
