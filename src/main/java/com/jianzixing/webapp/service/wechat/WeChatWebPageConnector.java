package com.jianzixing.webapp.service.wechat;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.wechat.model.AccountConfig;
import com.jianzixing.webapp.service.wechatsm.WeChatServiceManagerUtils;
import com.jianzixing.webapp.tables.wechat.TableWeChatOpenAccount;
import com.jianzixing.webapp.tables.wechat.TableWeChatUser;
import com.jianzixing.webapp.tables.wechat.TableWeChatAccount;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.HttpUtils;

import java.io.IOException;
import java.util.Date;

public class WeChatWebPageConnector {

    public String getAccessToken(AccountConfig accountConfig, String openid) throws IOException, ModelCheckerException, ModuleException {
        ModelObject accountObj = WeChatServiceManagerUtils.getAccountModel(accountConfig);
        int openType = accountConfig.getType().getCode();
        int accountId = accountObj.getIntValue(TableWeChatAccount.id);
        ModelObject object = GlobalService.weChatUserService.getUserByOpenId(openType, accountId, openid);

        if (object != null) {
            Date tokenTime = object.getDate(TableWeChatUser.lastTokenTime);
            long expiresIn = object.getIntValue(TableWeChatUser.expiresIn);
            if (tokenTime != null && tokenTime.getTime() + expiresIn * 1000l > System.currentTimeMillis()) {
                return object.getString(TableWeChatUser.accessToken);
            }
        }

        String appid = (accountConfig.getType() == WeChatOpenType.OPEN_PUBLIC) ?
                accountObj.getString(TableWeChatOpenAccount.authorizerAppid) : accountObj.getString(TableWeChatAccount.appId);
        String refreshToken = object.getString(TableWeChatUser.refreshToken);
        String url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?" +
                "appid=" + appid +
                "&refresh_token=" + refreshToken +
                "&grant_type=refresh_token";
        String text = HttpUtils.get(url);
        ModelObject json = ModelObject.parseObject(text);
        if (WeChatInterfaceUtils.isResponseSuccess(json, null)) {
            String accessToken = json.getString("access_token");
            int ei = json.getIntValue("expires_in");
            String rt = json.getString("refresh_token");

            ModelObject user = new ModelObject();
            user.put(TableWeChatUser.openid, openid);
            user.put(TableWeChatUser.openType, openType);
            user.put(TableWeChatUser.accessToken, accessToken);
            user.put(TableWeChatUser.expiresIn, ei);
            user.put(TableWeChatUser.accountId, accountId);
            user.put(TableWeChatUser.refreshToken, rt);
            user.put(TableWeChatUser.lastTokenTime, new Date());
            GlobalService.weChatUserService.updateUserByOpenid(user);
            return accessToken;
        }
        return null;
    }
}
