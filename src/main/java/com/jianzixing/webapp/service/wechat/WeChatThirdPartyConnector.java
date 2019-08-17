package com.jianzixing.webapp.service.wechat;

import org.mimosaframework.core.utils.RandomUtils;
import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.wechat.aes.AesException;
import com.jianzixing.webapp.service.wechat.aes.WXBizMsgCrypt;
import com.jianzixing.webapp.service.wechat.model.AccountConfig;
import com.jianzixing.webapp.service.wechat.model.AccountServerConfig;
import com.jianzixing.webapp.service.wechatsm.WeChatServiceManagerUtils;
import com.jianzixing.webapp.tables.wechat.TableWeChatOpen;
import com.jianzixing.webapp.tables.wechat.TableWeChatOpenAccount;
import com.jianzixing.webapp.tables.wechat.TableWeChatUser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.HttpUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1453779503&token=&lang=zh_CN
 * 功能	API的作用
 * 1、推送component_verify_ticket	出于安全考虑，在第三方平台创建审核通过后，微信服务器 每隔10分钟会向第三方的消息接收地址推送一次component_verify_ticket，用于获取第三方平台接口调用凭据。
 * 2、获取第三方平台component_access_token	第三方平台通过自己的component_appid（即在微信开放平台管理中心的第三方平台详情页中的AppID和AppSecret）和component_appsecret，以及component_verify_ticket（每10分钟推送一次的安全ticket）来获取自己的接口调用凭据（component_access_token）
 * 3、获取预授权码pre_auth_code	第三方平台通过自己的接口调用凭据（component_access_token）来获取用于授权流程准备的预授权码（pre_auth_code）
 * 4、使用授权码换取公众号或小程序的接口调用凭据和授权信息	通过授权码和自己的接口调用凭据（component_access_token），换取公众号或小程序的接口调用凭据（authorizer_access_token和用于前者快过期时用来刷新它的authorizer_refresh_token）和授权信息（授权了哪些权限等信息）
 * 5、获取（刷新）授权公众号或小程序的接口调用凭据	通过authorizer_refresh_token来刷新公众号或小程序的接口调用凭据
 * 6、获取授权公众号或小程序基本信息	在需要的情况下，第三方平台可以获取公众号或小程序的帐号基本信息，包括帐号名、帐号类型等
 * 7、获取授权方的选项设置信息	在需要的情况下，第三方平台可以获取公众号或小程序的选项设置，包括地理位置上报设置、语音识别开关设置、微信多客服功能开关设置
 * 8、设置授权方的选项信息	在需要的情况下，第三方平台可以修改上述公众号或小程序的选项设置，包括地理位置上报设置、语音识别开关设置、微信多客服功能开关设置
 * 9、推送授权相关通知	当公众号或小程序对第三方进行授权、取消授权、更新授权时，将通过事件推送告诉开发者
 * 接下来：代替公众号或小程序调用接口	取在完成授权后，第三方平台可通过公众号或小程序的接口调用凭据（authorizer_access_token）来代替它调用接口，具体请见“代公众号实现业务”和“代小程序实现业务”文件夹中的内容
 */
public class WeChatThirdPartyConnector {
    private static final Log logger = LogFactory.getLog(WeChatThirdPartyConnector.class);
    private final WeChatService weChatService;
    private final List<WeChatListener> events;

    public WeChatThirdPartyConnector(WeChatService weChatService, List<WeChatListener> events) {
        this.weChatService = weChatService;
        this.events = events;
    }

    /**
     * 访问返回的url后会跳转回调,使用authRedirectParam方法接收回调参数
     *
     * @param tpCode
     * @param redirectUri
     * @return
     */
    public String authPCUrl(String tpCode, String redirectUri) throws IOException {
        ModelObject object = GlobalService.weChatOpenService.getOpenByCode(tpCode);
        if (object != null) {
            String appid = object.getString(TableWeChatOpen.appId);
            String preAuthCode = this.getPreAuthCode(tpCode);
            if (StringUtils.isBlank(preAuthCode)) {
                logger.info("没有找到preAuthCode开始从接口获取!");
                preAuthCode = this.getPreAuthCode(tpCode);
            }
            String url = "https://mp.weixin.qq.com/cgi-bin/componentloginpage?" +
                    "component_appid=" + appid +
                    "&pre_auth_code=" + preAuthCode +
                    "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8") +
                    "&auth_type=3";
            return url;
        }
        return null;
    }

    /**
     * 访问返回的url后会跳转回调,使用authRedirectParam方法接收回调参数
     *
     * @param tpCode
     * @param redirectUri
     * @return
     */
    public String authMobileUrl(String tpCode, String redirectUri) throws IOException {
        ModelObject object = GlobalService.weChatOpenService.getOpenByCode(tpCode);
        if (object != null) {
            String appid = object.getString(TableWeChatOpen.appId);
            String preAuthCode = this.getPreAuthCode(tpCode);
            if (StringUtils.isBlank(preAuthCode)) {
                logger.info("没有找到preAuthCode开始从接口获取!");
                preAuthCode = this.getPreAuthCode(tpCode);
            }
            String url = "https://mp.weixin.qq.com/safe/bindcomponent?" +
                    "action=bindcomponent" +
                    "&auth_type=3" +
                    "&no_scan=1" +
                    "&component_appid=" + appid +
                    "&pre_auth_code=" + preAuthCode +
                    "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8") +
                    "&auth_type=3" +
                    //"&biz_appid=xxxx" +
                    "#wechat_redirect";
            return url;
        }
        return null;
    }

    /**
     * 使用 authPCUrl 或者 authMobileUrl 创建URL后redirectUri跳转回来会带入一下参数并处理
     * 这个autoCode在用来获取当前授权的公众号，通过接口
     * https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token=xxxx
     * 获取到authorizer_appid微信公众号的appid
     * <p>
     * 当第三方平台绑定多个微信公众号时请各自授权authCode存储到各自的第三方平台授权公众号列表表中
     * <p>
     * 简化说明：就是公众号在通过第三方账号授权之后保存信息到数据库
     *
     * @param request
     * @param tpCode
     */
    public void authRedirectParam(HttpServletRequest request, String tpCode) throws IOException {
        String authCode = request.getParameter("auth_code");
        String expiresIn = request.getParameter("expires_in");
        ModelObject tp = GlobalService.weChatOpenService.getOpenByCode(tpCode);
        String accessToken = this.isValidComponentAccessToken(tp);
        if (StringUtils.isBlank(accessToken)) accessToken = this.getAccessToken(tpCode);

        if (tp != null) {
            ModelObject post = new ModelObject();
            post.put("component_appid", tp.getString(TableWeChatOpen.appId));
            post.put("authorization_code", authCode);
            logger.info("开始获取第三方平台信息:start...");
            String resp = HttpUtils.postJson("https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token=" + accessToken, post.toJSONString());
            logger.info("结束获取第三方平台信息:" + resp);

            ModelObject respJson = ModelObject.parseObject(resp);
            if (WeChatInterfaceUtils.isResponseSuccess(respJson, null)) {
                ModelObject authInfo = respJson.getModelObject("authorization_info");
                String appid = authInfo.getString("authorizer_appid");
                String authAccessToken = authInfo.getString("authorizer_access_token");
                String authExpiresIn = authInfo.getString("expires_in");
                String authRefreshToken = authInfo.getString("authorizer_refresh_token");
                ModelObject verifyTypeInfo = authInfo.getModelObject("verify_type_info");
                ModelObject serviceTypeInfo = authInfo.getModelObject("service_type_info");

                post.put("authorizer_appid", appid);

                ModelObject update = new ModelObject(TableWeChatOpen.class);
                update.put(TableWeChatOpenAccount.tpCode, tpCode);
                update.put(TableWeChatOpenAccount.tpId, tp.getIntValue(TableWeChatOpen.id));
                update.put(TableWeChatOpenAccount.authorizerAppid, appid);
                update.put(TableWeChatOpenAccount.authCode, authCode);
                update.put(TableWeChatOpenAccount.authCodeExpires, Integer.parseInt(expiresIn));
                update.put(TableWeChatOpenAccount.lastAuthTime, new Date());

                update.put(TableWeChatOpenAccount.authorizerAccessToken, authAccessToken);
                update.put(TableWeChatOpenAccount.authorizerExpires, authExpiresIn);
                update.put(TableWeChatOpenAccount.lastAuthorizerTime, new Date());
                update.put(TableWeChatOpenAccount.authorizerRefreshToken, authRefreshToken);

                if (verifyTypeInfo != null) {
                    update.put(TableWeChatOpenAccount.verifyTypeInfo, verifyTypeInfo.getIntValue("id"));
                }
                if (serviceTypeInfo != null) {
                    update.put(TableWeChatOpenAccount.serviceTypeInfo, serviceTypeInfo.getIntValue("id"));
                }
                GlobalService.weChatOpenService.updateOpenAccountByCode(update);
            }

            post.remove("authorization_code");
            logger.info("通过第三方平台获取基本信息:start...");
            resp = HttpUtils.postJson("https://api.weixin.qq.com/cgi-bin/component/api_get_authorizer_info?component_access_token=" + accessToken, post.toJSONString());
            logger.info("通过第三方平台获取基本信息:" + resp);
            respJson = ModelObject.parseObject(resp);
            if (WeChatInterfaceUtils.isResponseSuccess(respJson, null)) {
                ModelObject authInfo = respJson.getModelObject("authorizer_info");

                String nickName = authInfo.getString("nick_name");
                String headImg = authInfo.getString("head_img");
                String userName = authInfo.getString("user_name");
                String businessInfo = authInfo.getString("business_info");
                String qrcodeUrl = authInfo.getString("qrcode_url");
                String funcInfo = authInfo.getString("func_info");
                String principalName = authInfo.getString("principal_name");
                ModelObject verifyTypeInfo = authInfo.getModelObject("verify_type_info");
                ModelObject serviceTypeInfo = authInfo.getModelObject("service_type_info");

                ModelObject update = new ModelObject(TableWeChatOpen.class);
                update.put(TableWeChatOpenAccount.tpCode, tpCode);
                update.put(TableWeChatOpenAccount.tpId, tp.getIntValue(TableWeChatOpen.id));
                update.put(TableWeChatOpenAccount.authorizerAppid, post.getString("authorizer_appid"));
                update.put(TableWeChatOpenAccount.nickName, nickName);
                update.put(TableWeChatOpenAccount.headImg, headImg);
                update.put(TableWeChatOpenAccount.userName, userName);
                update.put(TableWeChatOpenAccount.businessInfo, businessInfo);
                update.put(TableWeChatOpenAccount.qrCodeUrl, qrcodeUrl);
                update.put(TableWeChatOpenAccount.funcInfo, funcInfo);
                update.put(TableWeChatOpenAccount.principalName, principalName);

                if (verifyTypeInfo != null) {
                    update.put(TableWeChatOpenAccount.verifyTypeInfo, verifyTypeInfo.getIntValue("id"));
                }
                if (serviceTypeInfo != null) {
                    update.put(TableWeChatOpenAccount.serviceTypeInfo, serviceTypeInfo.getIntValue("id"));
                }
                GlobalService.weChatOpenService.updateOpenAccountByCode(update);
            }
        }
    }

    /**
     * 推送component_verify_ticket协议
     * <p>
     * 在第三方平台创建审核通过后，微信服务器会向其“授权事件接收URL”每隔10分钟定时推送component_verify_ticket。
     * 第三方平台方在收到ticket推送后也需进行解密（详细请见【消息加解密接入指引】），接收到后必须直接返回字符串success。
     * <p>
     * 注意：
     * component_verify_ticket的有效时间较component_access_token更长，建议保存最近可用的component_verify_ticket，
     * 在component_access_token过期之前使用该ticket进行更新，避免出现因为ticket接收失败而无法更新component_access_token的情况。
     *
     * @param tpCode
     * @param body
     */
    public String pushVerifyTicket(String tpCode, String body, boolean isEncrypt) throws AesException {
        if (StringUtils.isNotBlank(body)) {
            ModelObject object = WeChatInterfaceUtils.xmlToModel(body);
            ModelObject tp = GlobalService.weChatOpenService.getOpenByCode(tpCode);

            if (isEncrypt && StringUtils.isNotBlank(object.getString("Encrypt"))) {
                String key = tp.getString(TableWeChatOpen.appKey);
                String token = tp.getString(TableWeChatOpen.appToken);
                String appid = tp.getString(TableWeChatOpen.appId);
                WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, key, appid);
                String xml = wxcpt.decrypt(object.getString("Encrypt"));
                object = WeChatInterfaceUtils.xmlToModel(xml);
            }

            String appid = object.getString("AppId");
            String createTime = object.getString("CreateTime");
            String infoType = object.getString("InfoType"); //component_verify_ticket
            if (infoType.equalsIgnoreCase("component_verify_ticket")) {
                String componentVerifyTicket = object.getString("ComponentVerifyTicket");
                if (tp != null) {
                    ModelObject update = new ModelObject(TableWeChatOpen.class);
                    update.put(TableWeChatOpen.id, tp.getIntValue(TableWeChatOpen.id));
                    update.put(TableWeChatOpen.componentVerifyTicket, componentVerifyTicket.trim());
                    update.put(TableWeChatOpen.checked, 1);
                    try {
                        GlobalService.weChatOpenService.updateOpen(update);
                    } catch (ModelCheckerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return "success";
    }

    private String isValidComponentAccessToken(ModelObject tp) {
        String componentAccessToken = tp.getString(TableWeChatOpen.componentAccessToken);
        int expires = tp.getIntValue(TableWeChatOpen.accessTokenExpires);
        Date last = tp.getDate(TableWeChatOpen.lastTokenTime);
        if (expires != 0 && StringUtils.isNotBlank(componentAccessToken)
                && (last.getTime() + (expires - 10) * 1000l) > System.currentTimeMillis()) {
            return componentAccessToken;
        }
        return null;
    }

    /**
     * 获取第三方平台component_access_token
     * <p>
     * 第三方平台component_access_token是第三方平台的下文中接口的调用凭据，也叫做令牌（component_access_token）。
     * 每个令牌是存在有效期（2小时）的，且令牌的调用不是无限制的，请第三方平台做好令牌的管理，在令牌快过期时（比如1小时50分）再进行刷新。
     *
     * @param tpCode
     * @return
     */
    public String getAccessToken(String tpCode) throws IOException {
        ModelObject tp = GlobalService.weChatOpenService.getOpenByCode(tpCode);
        String componentAccessToken = this.isValidComponentAccessToken(tp);
        if (StringUtils.isNotBlank(componentAccessToken)) {
            return componentAccessToken;
        }

        String componentVerifyTicket = tp.getString(TableWeChatOpen.componentVerifyTicket);
        if (StringUtils.isBlank(componentVerifyTicket)) {
            throw new IllegalArgumentException("需要参数component_verify_ticket是微信服务器推送过来的!");
        }
        ModelObject post = new ModelObject();
        post.put("component_appid", tp.getString(TableWeChatOpen.appId));
        post.put("component_appsecret", tp.getString(TableWeChatOpen.appSecret));
        post.put("component_verify_ticket", componentVerifyTicket);
        String url = "https://api.weixin.qq.com/cgi-bin/component/api_component_token";

        logger.info("开始获取component_access_token从接口:start...");
        String resp = HttpUtils.postJson(url, post.toJSONString());
        logger.info("结束获取component_access_token从接口:" + resp);


        ModelObject respJson = ModelObject.parseObject(resp);
        if (WeChatInterfaceUtils.isResponseSuccess(respJson, null)) {
            componentAccessToken = respJson.getString("component_access_token");
            int expires = respJson.getIntValue("expires_in");

            ModelObject update = new ModelObject(TableWeChatOpen.class);
            update.put(TableWeChatOpen.id, tp.getIntValue(TableWeChatOpen.id));
            update.put(TableWeChatOpen.componentAccessToken, componentAccessToken.trim());
            update.put(TableWeChatOpen.accessTokenExpires, expires);
            update.put(TableWeChatOpen.lastTokenTime, new Date());
            try {
                GlobalService.weChatOpenService.updateOpen(update);
            } catch (ModelCheckerException e) {
                e.printStackTrace();
            }
            return componentAccessToken;
        }
        return null;
    }

    /**
     * 获取预授权码pre_auth_code
     * <p>
     * 该API用于获取预授权码。预授权码用于公众号或小程序授权时的第三方平台方安全验证。
     * authPCUrl 或者 authMobileUrl 需要用到
     *
     * @param tpCode
     * @return
     */
    public String getPreAuthCode(String tpCode) throws IOException {
        ModelObject tp = GlobalService.weChatOpenService.getOpenByCode(tpCode);
        String preAuthCode = tp.getString(TableWeChatOpen.preAuthCode);
        int expires = tp.getIntValue(TableWeChatOpen.preExpires);
        Date last = tp.getDate(TableWeChatOpen.lastPreTime);
        if (expires != 0 && StringUtils.isNotBlank(preAuthCode)
                && (last.getTime() + (expires - 10) * 1000l) > System.currentTimeMillis()) {
            return preAuthCode;
        }

        ModelObject post = new ModelObject();
        post.put("component_appid", tp.getString(TableWeChatOpen.appId));
        String url = "https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token=" + this.getAccessToken(tpCode);
        logger.info("开始获取PreAuthCode从接口:start...");
        String resp = HttpUtils.postJson(url, post.toJSONString());
        logger.info("结束获取PreAuthCode从接口:" + resp);

        ModelObject respJson = ModelObject.parseObject(resp);
        preAuthCode = respJson.getString("pre_auth_code");
        expires = respJson.getIntValue("expires_in");

        ModelObject update = new ModelObject(TableWeChatOpen.class);
        update.put(TableWeChatOpen.id, tp.getIntValue(TableWeChatOpen.id));
        update.put(TableWeChatOpen.preAuthCode, preAuthCode.trim());
        update.put(TableWeChatOpen.preExpires, expires);
        update.put(TableWeChatOpen.lastPreTime, new Date());
        try {
            GlobalService.weChatOpenService.updateOpen(update);
        } catch (ModelCheckerException e) {
            e.printStackTrace();
        }
        return preAuthCode;
    }

    /**
     * 使用授权码换取公众号或小程序的接口调用凭据和授权信息
     * <p>
     * 该API用于使用授权码换取授权公众号或小程序的授权信息，并换取authorizer_access_token和authorizer_refresh_token。
     * 授权码的获取，需要在用户在第三方平台授权页中完成授权流程后，在回调URI中通过URL参数提供给第三方平台方。
     * 请注意，由于现在公众号或小程序可以自定义选择部分权限授权给第三方平台，因此第三方平台开发者需要通过该接口来获取
     * 公众号或小程序具体授权了哪些权限，而不是简单地认为自己声明的权限就是公众号或小程序授权的权限。
     *
     * @param tpCode
     * @return
     */
    public String getAuthorizerAccessToken(String tpCode, String authAppid) throws IOException {
        ModelObject componentAccount = GlobalService.weChatOpenService.getOpenByCode(tpCode);
        ModelObject authAccount = GlobalService.weChatOpenService.getAccountByAppId(tpCode, authAppid);
        return getAuthorizerAccessToken(componentAccount, authAccount);
    }

    public String getAuthorizerAccessToken(ModelObject componentAccount, ModelObject authAccount) throws IOException {
        String tpCode = componentAccount.getString(TableWeChatOpen.code);
        String authorizerAccessToken = authAccount.getString(TableWeChatOpenAccount.authorizerAccessToken);
        String authorizerAppidOld = authAccount.getString(TableWeChatOpenAccount.authorizerAppid);
        String authorizerRefreshTokenOld = authAccount.getString(TableWeChatOpenAccount.authorizerRefreshToken);
        int authorizerExpires = authAccount.getIntValue(TableWeChatOpenAccount.authorizerExpires);
        Date lastAuthorizerTime = authAccount.getDate(TableWeChatOpenAccount.lastAuthorizerTime);
        if (authorizerExpires != 0 && StringUtils.isNotBlank(authorizerAccessToken)
                && (lastAuthorizerTime.getTime() + (authorizerExpires - 10) * 1000l) > System.currentTimeMillis()) {
            return authorizerAccessToken;
        }

        ModelObject post = new ModelObject();
        post.put("component_appid", componentAccount.getString(TableWeChatOpen.appId));

        // 如果已经存在刷新token就调用刷新接口否则就重新让管理员授权
        if (StringUtils.isNotBlank(authorizerAppidOld) && StringUtils.isNotBlank(authorizerRefreshTokenOld)) {
            String url = "https://api.weixin.qq.com/cgi-bin/component/api_authorizer_token?component_access_token=" + this.getAccessToken(tpCode);
            post.put("authorizer_appid", authorizerAppidOld);
            post.put("authorizer_refresh_token", authorizerRefreshTokenOld);
            String resp = HttpUtils.postJson(url, post.toJSONString());
            logger.info("刷新第三方公众号authorizer_access_token并响应:" + resp);

            ModelObject respJson = ModelObject.parseObject(resp);
            if (respJson.containsKey("authorizer_access_token")) {
                String authorizerRefreshToken = respJson.getString("authorizer_refresh_token");
                authorizerAccessToken = respJson.getString("authorizer_access_token");
                authorizerExpires = respJson.getIntValue("expires_in");
                ModelObject update = new ModelObject(TableWeChatOpenAccount.class);
                update.put(TableWeChatOpenAccount.id, authAccount.getIntValue(TableWeChatOpenAccount.id));
                update.put(TableWeChatOpenAccount.authorizerAccessToken, authorizerAccessToken.trim());
                update.put(TableWeChatOpenAccount.authorizerExpires, authorizerExpires);
                update.put(TableWeChatOpenAccount.authorizerRefreshToken, authorizerRefreshToken);
                update.put(TableWeChatOpenAccount.lastAuthorizerTime, new Date());
                try {
                    GlobalService.weChatOpenService.updateOpenAccountById(update);
                } catch (ModelCheckerException e) {
                    e.printStackTrace();
                }
                return authorizerAccessToken;
            } else {
                return null;
            }

        } else {
            post.put("authorization_code", authAccount.getString(TableWeChatOpenAccount.authCode));
            String url = "https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token=" + this.getAccessToken(tpCode);
            String resp = HttpUtils.postJson(url, post.toJSONString());
            logger.info("获取第三方公众号authorizer_access_token并响应:" + resp);

            ModelObject respJson = ModelObject.parseObject(resp);
            if (respJson.containsKey("authorization_info")) {
                respJson = respJson.getModelObject("authorization_info");
                String wxAppId = respJson.getString("authorizer_appid");
                String authorizerRefreshToken = respJson.getString("authorizer_refresh_token");
                authorizerAccessToken = respJson.getString("authorizer_access_token");
                authorizerExpires = respJson.getIntValue("expires_in");

                ModelObject update = new ModelObject(TableWeChatOpenAccount.class);
                update.put(TableWeChatOpenAccount.id, authAccount.getIntValue(TableWeChatOpenAccount.id));
                update.put(TableWeChatOpenAccount.authorizerAppid, wxAppId);
                update.put(TableWeChatOpenAccount.authorizerAccessToken, authorizerAccessToken.trim());
                update.put(TableWeChatOpenAccount.authorizerRefreshToken, authorizerRefreshToken);
                update.put(TableWeChatOpenAccount.authorizerExpires, authorizerExpires);
                update.put(TableWeChatOpenAccount.lastAuthorizerTime, new Date());
                try {
                    GlobalService.weChatOpenService.updateOpenAccountById(update);
                } catch (ModelCheckerException e) {
                    e.printStackTrace();
                }
                return authorizerAccessToken;
            } else {
                return null;
            }
        }
    }

    /**
     * https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419318590&token=&lang=zh_CN
     * 第二步：通过code换取access_token
     */
    public ModelObject getUserAccessToken(String tp, String appid, String code) throws IOException, ModelCheckerException, ModuleException {
        ModelObject object = GlobalService.weChatOpenService.getOpenByCode(tp);
        ModelObject acc = GlobalService.weChatOpenService.getAccountByAppId(tp, appid);
        String tpAppid = object.getString(TableWeChatOpen.appId);

        String cat = GlobalService.weChatService.getThirdPartyConnector().getAccessToken(tp);
        String url = "https://api.weixin.qq.com/sns/oauth2/component/access_token?" +
                "appid=" + appid +
                "&code=" + code +
                "&grant_type=authorization_code" +
                "&component_appid=" + tpAppid +
                "&component_access_token=" + cat;

        String accessTokenStr = HttpUtils.get(url);
        ModelObject accessTokenJson = ModelObject.parseObject(accessTokenStr);
        if (accessTokenJson.containsKey("access_token")) {
            String accessToken = accessTokenJson.getString("access_token");
            long expiresIn = accessTokenJson.getLongValue("expires_in");
            String refreshToken = accessTokenJson.getString("refresh_token");
            String openid = accessTokenJson.getString("openid");

            ModelObject user = new ModelObject();
            user.put(TableWeChatUser.openid, openid);
            user.put(TableWeChatUser.accessToken, accessToken);
            user.put(TableWeChatUser.expiresIn, expiresIn);
            user.put(TableWeChatUser.refreshToken, refreshToken);
            user.put(TableWeChatUser.lastTokenTime, new Date());
            user = GlobalService.weChatUserService.updateUserByOpenid(user);

            GlobalService.weChatService.getStartDevelopConnector().getUserInfo(
                    WeChatOpenType.OPEN_PUBLIC.getCode(), acc.getIntValue(TableWeChatOpenAccount.id),
                    accessToken, openid);

            return user;
        }
        return null;
    }

    public String server(HttpServletRequest request, String code, String authAppid, String body, boolean isEncrypt) throws AesException {
        ModelObject tp = GlobalService.weChatOpenService.getOpenByCode(code);
        ModelObject object = WeChatInterfaceUtils.xmlToModel(body);
        String xml = null;
        String key = tp.getString(TableWeChatOpen.appKey);
        String token = tp.getString(TableWeChatOpen.appToken);
        String appid = tp.getString(TableWeChatOpen.appId);
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, key, appid);

        if (isEncrypt && StringUtils.isNotBlank(object.getString("Encrypt"))) {
            xml = wxcpt.decrypt(object.getString("Encrypt"));
        }
        AccountServerConfig config = AccountServerConfig.builder(xml, code, authAppid, WeChatOpenType.OPEN_PUBLIC);
        ModelObject openAccount = WeChatServiceManagerUtils.getAccountModel(config);
        if (openAccount != null) {
            config.setAccountId(openAccount.getIntValue(TableWeChatOpenAccount.id));
        }
        String reply = GlobalService.weChatService.getStartDevelopConnector().server(config);
        if (StringUtils.isNotBlank(reply)) {
            return wxcpt.encryptMsg(reply, System.currentTimeMillis() / 1000 + "", RandomUtils.randomLetter(8));
        }
        return reply;
    }
}
