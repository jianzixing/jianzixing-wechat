package com.jianzixing.webapp.service.wechat;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.wechat.model.AccountConfig;
import com.jianzixing.webapp.service.wechatsm.WeChatServiceManagerUtils;
import com.jianzixing.webapp.tables.wechat.TableWeChatUser;
import com.jianzixing.webapp.tables.wechat.TableWeChatWebSite;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.HttpUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class WeChatWebSiteConnector {
    private static final Log logger = LogFactory.getLog(WeChatMiniProgramConnector.class);
    private final WeChatService weChatService;
    private final List<WeChatListener> events;

    public WeChatWebSiteConnector(WeChatService weChatService, List<WeChatListener> events) {
        this.weChatService = weChatService;
        this.events = events;
    }

    /**
     * 获取网页微信登录地址
     * https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419316505&token=&lang=zh_CN
     *
     * @param config
     * @param redirectUri
     * @param state
     * @return
     */
    public String getAuthUrl(AccountConfig config, String redirectUri, String state) {
        ModelObject object = WeChatServiceManagerUtils.getAccountModel(config);
        if (object != null) {
            if (StringUtils.isBlank(state)) state = "10";
            String appid = object.getString(TableWeChatWebSite.appId);
            String url = null;
            try {
                url = "https://open.weixin.qq.com/connect/qrconnect" +
                        "?appid=" + appid +
                        "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8") +
                        "&response_type=code" +
                        "&scope=snsapi_login" +
                        "&state=" + state +
                        "#wechat_redirect";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return url;
        }
        return null;
    }

    public String getAccessToken(AccountConfig config) throws IOException, ModelCheckerException, ModuleException {
        return getAccessToken(config, null);
    }

    public String getAccessToken(AccountConfig config, String openid) throws IOException, ModelCheckerException, ModuleException {
        ModelObject object = WeChatServiceManagerUtils.getAccountModel(config);
        if (object != null) {
            int accountId = object.getIntValue(TableWeChatWebSite.id);
            ModelObject user = null;
            if (StringUtils.isNotBlank(openid)) {
                user = GlobalService.weChatUserService.getUserByOpenId(WeChatOpenType.WEBSITE.getCode(), accountId, openid);
                String accessToken = user.getString(TableWeChatUser.accessToken);
                long expiresIn = object.getLongValue(TableWeChatUser.expiresIn);
                Date lastTokenTime = object.getDate(TableWeChatUser.lastTokenTime);
                // 不需要刷新AccessToken假如当前服务器存储的登录态失效需要用户重新登录
                String refreshToken = object.getString(TableWeChatUser.refreshToken);
                if ((lastTokenTime.getTime() + expiresIn * 1000l) > System.currentTimeMillis() - 100) {
                    return accessToken;
                }
            }

            String appid = object.getString(TableWeChatWebSite.appId);
            String appSecret = object.getString(TableWeChatWebSite.appSecret);

            String resp = HttpUtils.get("https://api.weixin.qq.com/sns/oauth2/access_token" +
                    "?appid=" + appid +
                    "&secret=" + appSecret +
                    "&code=code" +
                    "&grant_type=authorization_code");
            ModelObject json = ModelObject.parseObject(resp);
            if (WeChatInterfaceUtils.isResponseSuccess(json, null)) {
                String accessToken = json.getString("access_token");
                long expiresIn = json.getLongValue("expires_in");
                String refreshToken = json.getString("refresh_token");
                openid = json.getString("openid");
                String scope = json.getString("scope");
                String unionid = json.getString("unionid");

                user = new ModelObject();
                user.put(TableWeChatUser.openType, WeChatOpenType.WEBSITE.getCode());
                user.put(TableWeChatUser.accountId, accountId);
                user.put(TableWeChatUser.openid, openid);
                user.put(TableWeChatUser.accessToken, accessToken);
                user.put(TableWeChatUser.expiresIn, expiresIn);
                user.put(TableWeChatUser.refreshToken, refreshToken);
                user.put(TableWeChatUser.lastTokenTime, new Date());
                user.put(TableWeChatUser.unionid, unionid);
                GlobalService.weChatUserService.updateUserByOpenid(user);
                return accessToken;
            }
        }
        return null;
    }

    public ModelObject getUserInfo(AccountConfig config, String accessToken, String openid) throws ModelCheckerException, ModuleException, IOException {
        ModelObject object = WeChatServiceManagerUtils.getAccountModel(config);
        int accountId = object.getIntValue(TableWeChatWebSite.id);
        return this.weChatService.getStartDevelopConnector().getUserInfo(WeChatOpenType.WEBSITE.getCode(), accountId, accessToken, openid);
    }
}
