package com.jianzixing.webapp.service.wechatsm;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.wechat.model.AccountConfig;
import com.jianzixing.webapp.service.wechat.model.EventListenerConfig;
import com.jianzixing.webapp.service.wechat.WeChatOpenType;
import com.jianzixing.webapp.tables.wechat.*;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;

public class WeChatServiceManagerUtils {

    public static ModelObject getAccountModel(AccountConfig config) {
        if (config.isAccount()) {
            return getAccountModel(config.getAccountId(), config.getType().getCode());
        } else {
            return getAccountModel(config.getCode(), config.getAppid(), config.getType().getCode());
        }
    }

    public static ModelObject getAccountModel(String code, String appid, int openType) {
        WeChatOpenType type = WeChatOpenType.getOpenType(openType);
        if (type == WeChatOpenType.PUBLIC) {
            ModelObject object = GlobalService.weChatPublicService.getAccountByCode(code);
            return object;
        }

        if (type == WeChatOpenType.OPEN_PUBLIC) {
            ModelObject object = GlobalService.weChatOpenService.getAccountByAppId(code, appid);
            return object;
        }

        if (openType == WeChatOpenType.MINI_PROGRAM.getCode()) {
            ModelObject acc = GlobalService.weChatMiniProgramService.getMiniProgramByCode(code);
            return acc;
        }

        if (openType == WeChatOpenType.WEBSITE.getCode()) {
            ModelObject acc = GlobalService.weChatWebSiteService.getWebSiteByCode(code);
            return acc;
        }

        if (openType == WeChatOpenType.APP.getCode()) {
            ModelObject acc = GlobalService.weChatAppService.getAppByCode(code);
            return acc;
        }
        return null;
    }

    public static ModelObject getAccountModel(int accountId, int openType) {
        if (openType == WeChatOpenType.PUBLIC.getCode()) {
            ModelObject acc = GlobalService.weChatPublicService.getAccount(accountId);
            return acc;
        }
        if (openType == WeChatOpenType.OPEN_PUBLIC.getCode()) {
            ModelObject acc = GlobalService.weChatOpenService.getOpenAccountById(accountId);
            return acc;
        }
        if (openType == WeChatOpenType.MINI_PROGRAM.getCode()) {
            ModelObject acc = GlobalService.weChatMiniProgramService.getMiniProgramById(accountId);
            return acc;
        }

        if (openType == WeChatOpenType.WEBSITE.getCode()) {
            ModelObject acc = GlobalService.weChatWebSiteService.getWebSiteById(accountId);
            return acc;
        }

        if (openType == WeChatOpenType.APP.getCode()) {
            ModelObject acc = GlobalService.weChatAppService.getAppById(accountId);
            return acc;
        }
        return null;
    }

    public static AccountConfig createAccountConfig(int openType, int id) {
        ModelObject object = WeChatServiceManagerUtils.getAccountModel(id, openType);
        return createAccountConfig(object, openType);
    }

    public static AccountConfig createAccountConfig(ModelObject object, int openType) {
        AccountConfig config = new AccountConfig(null);
        config.setType(WeChatOpenType.getOpenType(openType));
        if (object != null) {
            if (openType == WeChatOpenType.PUBLIC.getCode()) {
                String code = object.getString(TableWeChatAccount.code);
                config.setCode(code);
                config.setAccountId(object.getIntValue(TableWeChatAccount.id));
                config.setAccount(true);
                return config;
            }
            if (openType == WeChatOpenType.OPEN_PUBLIC.getCode()) {
                String code = object.getString(TableWeChatOpenAccount.tpCode);
                String appid = object.getString(TableWeChatOpenAccount.authorizerAppid);
                config.setCode(code);
                config.setAppid(appid);
                config.setAccountId(object.getIntValue(TableWeChatOpenAccount.id));
                config.setAccount(true);
                return config;
            }

            if (openType == WeChatOpenType.OPEN_MINI_PROGRAM.getCode()) {
                String code = object.getString(TableWeChatOpenAccount.tpCode);
                String appid = object.getString(TableWeChatOpenAccount.authorizerAppid);
                config.setCode(code);
                config.setAppid(appid);
                config.setAccountId(object.getIntValue(TableWeChatOpenAccount.id));
                config.setAccount(true);
                return config;
            }

            if (openType == WeChatOpenType.MINI_PROGRAM.getCode()) {
                String code = object.getString(TableWeChatMiniProgram.code);
                config.setCode(code);
                config.setAccountId(object.getIntValue(TableWeChatMiniProgram.id));
                config.setAccount(true);
                return config;
            }

            if (openType == WeChatOpenType.APP.getCode()) {
                String code = object.getString(TableWeChatApp.code);
                config.setCode(code);
                config.setAccountId(object.getIntValue(TableWeChatApp.id));
                config.setAccount(true);
                return config;
            }

            if (openType == WeChatOpenType.WEBSITE.getCode()) {
                String code = object.getString(TableWeChatWebSite.code);
                config.setCode(code);
                config.setAccountId(object.getIntValue(TableWeChatWebSite.id));
                config.setAccount(true);
                return config;
            }
        }
        return null;
    }

    public static AccountConfig createAccountConfig(ModelObject acc) {
        int openType = acc.getIntValue("openType");
        return createAccountConfig(acc, openType);
    }

    public static String getAppidByObject(int openType, ModelObject acc) {
        if (openType == WeChatOpenType.PUBLIC.getCode()) {
            return acc.getString(TableWeChatAccount.appId);
        }
        if (openType == WeChatOpenType.OPEN_PUBLIC.getCode()) {
            return acc.getString(TableWeChatOpenAccount.authorizerAppid);
        }

        if (openType == WeChatOpenType.MINI_PROGRAM.getCode()) {
            return acc.getString(TableWeChatMiniProgram.appId);
        }
        return null;
    }

    public static ModelObject getCreateUser(EventListenerConfig config) throws ModelCheckerException, ModuleException {
        int openType = config.getType().getCode();
        int accountId = config.getAccountId();
        String openid = config.getOpenid();
        if (StringUtils.isNotBlank(openid)) {
            openid = openid.trim();
            ModelObject user = GlobalService.userService.getUserByOpenid(openType, accountId, openid);
            if (user == null) {
                ModelObject wcuser = new ModelObject();
                wcuser.put(TableWeChatUser.openType, openType);
                wcuser.put(TableWeChatUser.accountId, accountId);
                wcuser.put(TableWeChatUser.openid, openid);
                GlobalService.weChatUserService.updateUserByOpenid(wcuser);

                user = GlobalService.userService.getUserByOpenid(openType, accountId, openid);
            }
            return user;
        }
        return null;
    }

    public static ModelObject getCreateWeChatUser(EventListenerConfig config) throws ModelCheckerException, ModuleException {
        int openType = config.getType().getCode();
        int accountId = config.getAccountId();
        String openid = config.getOpenid();
        if (StringUtils.isNotBlank(openid)) {
            ModelObject user = GlobalService.weChatUserService.getUserByOpenId(openType, accountId, openid);
            if (user == null) {
                ModelObject wcuser = new ModelObject();
                wcuser.put(TableWeChatUser.openType, openType);
                wcuser.put(TableWeChatUser.accountId, accountId);
                wcuser.put(TableWeChatUser.openid, openid);
                GlobalService.weChatUserService.updateUserByOpenid(wcuser);
            }
            return user;
        }
        return null;
    }
}
