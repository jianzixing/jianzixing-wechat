package com.jianzixing.webapp.service.wechat;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.wechat.aes.AesException;
import com.jianzixing.webapp.service.wechat.aes.WeChatSHA1;
import com.jianzixing.webapp.service.wechat.model.*;
import com.jianzixing.webapp.tables.wechat.TableWeChatUser;
import com.jianzixing.webapp.tables.wechat.TableWeChatAccount;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.HttpUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class WeChatStartDevelopConnector {
    private static final Log logger = LogFactory.getLog(WeChatStartDevelopConnector.class);
    private List<WeChatListener> events = null;
    private WeChatService weChatService;

    public WeChatStartDevelopConnector(WeChatService weChatService, List<WeChatListener> events) {
        this.weChatService = weChatService;
        this.events = events;
    }

    /**
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421135319
     *
     * @param config
     * @return
     */
    public String checkDevelopmentBaseConfig(BaseConfig config) {
        try {
            if (StringUtils.isNotBlank(config.getPublicAccountCode())) {
                String token = config.getToken();
                String sign = WeChatSHA1.getSHA1(token, config.getTimestamp(), config.getNonce());
                if (sign.equalsIgnoreCase(config.getSignature())) {
                    GlobalService.weChatPublicService.setChecked(config.getId());
                    return config.getEchostr();
                } else {
                    logger.error("微信服务器首次消息验证失败: from:" + config.getSignature() + " build:" + sign);
                    return "Sign Code Fail";
                }
            } else {
                return "Unknow Account";
            }
        } catch (AesException e) {
            e.printStackTrace();
        }
        return "Error Decode Sign";
    }

    /**
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140183
     *
     * @param publicAccountCode
     * @return
     */
    public String getAccessToken(String publicAccountCode) throws IOException, ModuleException {
        ModelObject object = GlobalService.weChatPublicService.getAccountByCode(publicAccountCode);
        return getAccessToken(object);
    }

    public String getAccessToken(ModelObject object) throws IOException, ModuleException {
        if (object == null) {
            throw new ModuleException(StockCode.ARG_NULL, "没有找到该公众号信息或者公众号未启用");
        }
        String token = object.getString(TableWeChatAccount.accessToken);
        Date lastTokenTime = object.getDate(TableWeChatAccount.lastTokenTime);
        int tokenExpires = object.getIntValue(TableWeChatAccount.tokenExpires);
        boolean needReload = false;
        if (StringUtils.isBlank(token)) {
            needReload = true;
        }
        if (lastTokenTime == null) {
            lastTokenTime = new Date(0);
        }
        if ((lastTokenTime.getTime() + tokenExpires * 1000l) < System.currentTimeMillis()) {
            needReload = true;
        }

        if (needReload) {
            String appid = object.getString(TableWeChatAccount.appId);
            String appSecret = object.getString(TableWeChatAccount.appSecret);
            String text = HttpUtils.get(WeChatDomains.getApiUrl("/cgi-bin/token") +
                    "?grant_type=client_credential&appid=" + appid + "&secret=" + appSecret);
            ModelObject json = ModelObject.parseObject(text);
            if (WeChatInterfaceUtils.isResponseSuccess(json, null)) {
                String accessToken = json.getString("access_token");
                int expiresIn = json.getIntValue("expires_in");
                ModelObject update = new ModelObject(TableWeChatAccount.class);
                update.put(TableWeChatAccount.id, object.getIntValue(TableWeChatAccount.id));
                update.put(TableWeChatAccount.accessToken, accessToken);
                update.put(TableWeChatAccount.tokenExpires, expiresIn - 5 * 60);
                update.put(TableWeChatAccount.lastTokenTime, new Date());
                try {
                    GlobalService.weChatPublicService.updateAccountInfo(update);
                } catch (ModelCheckerException e) {
                    e.printStackTrace();
                }
                return accessToken;
            } else {
                return null;
            }
        } else {
            return object.getString(TableWeChatAccount.accessToken);
        }
    }

    /**
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140842
     * 开发 - 接口权限 - 网页服务 - 网页帐号 - 网页授权获取用户基本信息
     * 需要设置网页域名后才可以使用
     *
     * @param config
     * @return
     */
    public String getOAuthAuthorizeUrl(OAuthAuthorizeConfig config) {
        String publicAccountCode = config.getPublicAccountCode();
        String redirect = config.getRedirectUri();
        ModelObject object = GlobalService.weChatPublicService.getAccountByCode(publicAccountCode);
        if (object != null) {
            String appid = object.getString(TableWeChatAccount.appId);
            String url = null;
            try {
                url = "https://open.weixin.qq.com/connect/oauth2/authorize?" +
                        "appid=" + appid +
                        "&redirect_uri=" + URLEncoder.encode(redirect, "UTF-8") +
                        "&response_type=code" +
                        "&scope=" + config.getScope() +
                        "&state=10#wechat_redirect";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return url;
        }
        return null;
    }

    /**
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140842
     *
     * @param config
     * @return
     */
    public ModelObject getOAuthAccessToken(OAuthAccessTokenConfig config, boolean isUpdateToken) throws IOException, ModelCheckerException, ModuleException {
        String publicAccountCode = config.getPublicAccountCode();
        String code = config.getCode();
        ModelObject object = GlobalService.weChatPublicService.getAccountByCode(publicAccountCode);
        if (object != null) {
            String appid = object.getString(TableWeChatAccount.appId);
            String secret = object.getString(TableWeChatAccount.appSecret);
            String url = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                    "appid=" + appid +
                    "&secret=" + secret +
                    "&code=" + code +
                    "&grant_type=" + config.getGrantType();
            String text = HttpUtils.get(url);
            ModelObject json = ModelObject.parseObject(text);
            if (WeChatInterfaceUtils.isResponseSuccess(json, null)) {
                String openid = json.getString("openid");
                String accessToken = json.getString("access_token");
                int expiresIn = json.getIntValue("expires_in");
                String refreshToken = json.getString("refresh_token");

                ModelObject user = updateUserToken(
                        WeChatOpenType.PUBLIC.getCode(),
                        object.getIntValue(TableWeChatAccount.id),
                        openid,
                        accessToken,
                        expiresIn,
                        refreshToken, null, isUpdateToken);
                return user;
            }
        }
        return null;
    }

    private ModelObject updateUserToken(int openType,
                                        int accountId,
                                        String openid,
                                        String accessToken,
                                        int expiresIn,
                                        String refreshToken,
                                        String unionid,
                                        boolean isUpdateToken) throws ModelCheckerException, ModuleException {
        ModelObject user = new ModelObject();
        user.put(TableWeChatUser.openType, openType);
        user.put(TableWeChatUser.accountId, accountId);
        user.put(TableWeChatUser.openid, openid);
        user.put(TableWeChatUser.accessToken, accessToken);
        user.put(TableWeChatUser.expiresIn, expiresIn);
        user.put(TableWeChatUser.refreshToken, refreshToken);
        user.put(TableWeChatUser.lastTokenTime, new Date());
        if (unionid != null) {
            user.put(TableWeChatUser.unionid, unionid);
        }
        if (isUpdateToken) {
            user = GlobalService.weChatUserService.updateUserByOpenid(user);
        }
        return user;
    }

    /**
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140842
     *
     * @param publicAccountCode
     * @param refreshToken
     */
    public void setOAuthRefreshToken(String publicAccountCode, String refreshToken) throws IOException, ModelCheckerException, ModuleException {
        ModelObject object = GlobalService.weChatPublicService.getAccountByCode(publicAccountCode);
        if (object != null && StringUtils.isNotBlank(refreshToken)) {
            String appid = object.getString(TableWeChatAccount.appId);
            String url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?" +
                    "appid=" + appid +
                    "&grant_type=refresh_token" +
                    "&refresh_token=" + refreshToken;
            String text = HttpUtils.get(url);
            ModelObject json = ModelObject.parseObject(text);
            if (WeChatInterfaceUtils.isResponseSuccess(json, null)) {
                String openid = json.getString("openid");
                String accessToken = json.getString("access_token");
                int expiresIn = json.getIntValue("expires_in");
                refreshToken = json.getString("refresh_token");

                updateUserToken(
                        WeChatOpenType.PUBLIC.getCode(),
                        object.getIntValue(TableWeChatAccount.id),
                        openid,
                        accessToken,
                        expiresIn,
                        refreshToken,
                        null, true);
            }
        }
    }

    /**
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140842
     *
     * @param accessToken
     * @param openid
     * @return
     */
    public ModelObject getUserInfo(int openType, int accountId, String accessToken, String openid) throws IOException, ModelCheckerException, ModuleException {
        ModelObject user = new ModelObject();
        user.put(TableWeChatUser.accessToken, accessToken);
        user.put(TableWeChatUser.openid, openid);
        return getUserInfo(openType, accountId, user);
    }

    /**
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140842
     *
     * @return
     */
    public ModelObject getUserInfo(int openType, int accountId, ModelObject user) throws IOException, ModelCheckerException, ModuleException {
        String accessToken = user.getString(TableWeChatUser.accessToken);
        String openid = user.getString(TableWeChatUser.openid);

        String url = "https://api.weixin.qq.com/sns/userinfo?" +
                "access_token=" + accessToken +
                "&openid=" + openid +
                "&lang=zh_CN";
        String text = HttpUtils.get(url);
        ModelObject json = ModelObject.parseObject(text);
        if (WeChatInterfaceUtils.isResponseSuccess(json, AccountConfig.builder(openType, accountId))) {
            json.put(TableWeChatUser.openType, openType);
            json.put(TableWeChatUser.accountId, accountId);
            String nickName = json.getString("nickname");
            logger.info("获取用户: " + nickName + " 的信息");
            json.put(TableWeChatUser.nickname, URLEncoder.encode(nickName, "UTF-8"));
            json.put(TableWeChatUser.subscribeTime, json.get("subscribe_time"));
            json.put(TableWeChatUser.subscribeScene, json.get("subscribe_scene"));
            json.put(TableWeChatUser.qrScene, json.get("qr_scene"));
            json.put(TableWeChatUser.qrSceneStr, json.get("qr_scene_str"));

            json.put(TableWeChatUser.openid, openid);
            json.put(TableWeChatUser.accessToken, accessToken);
            if (user.isNotEmpty(TableWeChatUser.expiresIn)) {
                json.put(TableWeChatUser.expiresIn, user.get(TableWeChatUser.expiresIn));
            }
            if (user.isNotEmpty(TableWeChatUser.refreshToken)) {
                json.put(TableWeChatUser.refreshToken, user.get(TableWeChatUser.refreshToken));
            }
            if (user.isNotEmpty(TableWeChatUser.lastTokenTime)) {
                json.put(TableWeChatUser.lastTokenTime, user.get(TableWeChatUser.lastTokenTime));
            }

            GlobalService.weChatUserService.updateUserByOpenid(json);
            return json;
        }
        return null;
    }

    /**
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140842
     *
     * @param accessToken
     * @param openid
     * @return
     */
    public boolean isValidAuthAccessToken(String accessToken, String openid) throws IOException {
        String url = "https://api.weixin.qq.com/sns/auth?" +
                "access_token=" + accessToken +
                "&openid=" + openid;
        String text = HttpUtils.get(url);
        ModelObject json = ModelObject.parseObject(text);
        if (WeChatInterfaceUtils.isResponseSuccess(json, null)) {
            return true;
        }
        return false;
    }

    public String authorize(String accountCode, HttpServletRequest request, ModelObject pubAcc) {
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");

        if (StringUtils.isNotBlank(signature) && StringUtils.isNotBlank(timestamp)
                && StringUtils.isNotBlank(echostr) && StringUtils.isNotBlank(nonce)) {
            BaseConfig config = new BaseConfig();
            config.setPublicAccountCode(accountCode);
            config.setSignature(signature);
            config.setTimestamp(timestamp);
            config.setNonce(nonce);
            config.setEchostr(echostr);
            config.setId(pubAcc.getLongValue(TableWeChatAccount.id));
            config.setToken(pubAcc.getString(TableWeChatAccount.appToken));

            try {
                logger.info("微信服务器首次校验参数: " + ModelObject.toJSONString(request.getParameterMap()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return this.checkDevelopmentBaseConfig(config);
        }
        return null;
    }

    public String server(AccountServerConfig config) {
        if (config != null && StringUtils.isNotBlank(config.getBody())) {
            return weChatService.getMessageConnector().receive(config);
        }
        return "";
    }
}
