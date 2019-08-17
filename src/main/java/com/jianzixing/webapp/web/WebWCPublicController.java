package com.jianzixing.webapp.web;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.wechat.WeChatCookieUtils;
import com.jianzixing.webapp.service.wechat.WeChatOpenType;
import com.jianzixing.webapp.service.wechat.WeChatStartDevelopConnector;
import com.jianzixing.webapp.service.wechat.WeChatWebPageConnector;
import com.jianzixing.webapp.service.wechat.model.*;
import com.jianzixing.webapp.service.wechatsm.WeChatServiceManagerUtils;
import com.jianzixing.webapp.tables.user.TableUser;
import com.jianzixing.webapp.tables.wechat.TableWeChatAccount;
import com.jianzixing.webapp.tables.wechat.TableWeChatUser;
import org.mimosaframework.core.utils.RequestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.springmvc.APIController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

@APIController
public class WebWCPublicController extends AbstractWeChatController {
    private static final Log logger = LogFactory.getLog(WebWCPublicController.class);

    // 微信 服务器配置->服务器地址
    public static String getWcAuthorize(HttpServletRequest request, String code) {
        return RequestUtils.getWebUrl(request) + "/wc_authorize/" + code + ".jhtml";
    }

    /**
     * 微信公众号后台  基本配置 -> 填写服务器配置
     *
     * @return
     */
    @RequestMapping("/wc_authorize/{accountCode}")
    @ResponseBody
    public String serverAuthorize(@PathVariable(value = "accountCode") String accountCode,
                                  HttpServletRequest request,
                                  @RequestBody(required = false) String body) {
        WeChatStartDevelopConnector connector = GlobalService.weChatService.getStartDevelopConnector();
        ModelObject pubAcc = GlobalService.weChatPublicService.getAccountByCode(accountCode);

        if (pubAcc != null) {
            String isAuthorize = connector.authorize(accountCode, request, pubAcc);
            if (StringUtils.isNotBlank(isAuthorize)) {
                logger.info("微信服务器验证消息结果:" + isAuthorize);
                return isAuthorize;
            } else if (StringUtils.isNotBlank(body)) {
                logger.info("公众平台消息与事件接收URL");
                AccountServerConfig config = new AccountServerConfig(accountCode);
                config.setAccountId(pubAcc.getIntValue(TableWeChatAccount.id));
                config.setBody(body);
                config.setType(WeChatOpenType.PUBLIC);
                String resp = connector.server(config);
                if (pubAcc.getIntValue(TableWeChatAccount.checked) == 0) {
                    GlobalService.weChatPublicService.setChecked(pubAcc.getLongValue(TableWeChatAccount.id));
                }
                if (StringUtils.isNotBlank(resp)) {
                    return resp;
                }
            }
        } else {
            logger.error("接收到微信服务器请求,但是没有找到" + accountCode + "的公众号配置");
        }
        return "";
    }

    @RequestMapping("/wc_auth/{accountCode}")
    public Object redirectAuthPage(HttpServletRequest request,
                                   HttpServletResponse response,
                                   ModelAndView view,
                                   String l,
                                   @PathVariable(value = "accountCode") String accountCode) {

        try {
            AccountConfig accountConfig = AccountConfig.builder(accountCode);
            String openid = WeChatCookieUtils.getWeChatCookieOpenId(request, accountConfig);
            if (StringUtils.isNotBlank(openid)) {
                ModelObject acc = WeChatServiceManagerUtils.getAccountModel(accountConfig);
                if (acc != null) {
                    ModelObject user = GlobalService.weChatUserService.getUserByOpenId(WeChatOpenType.PUBLIC.getCode(), acc.getIntValue(TableWeChatAccount.id), openid);
                    if (user != null) {
                        WeChatWebPageConnector webPageConnector = GlobalService.weChatService.getWebPageConnector();

                        // 判断用户的微信AccessToken
                        String token = webPageConnector.getAccessToken(accountConfig, openid);
                        if (StringUtils.isNotBlank(token)) {

                            // 判断用户本地的登录态
                            ModelObject sysUser = GlobalService.userService.silenceLogin(user.getLongValue(TableWeChatUser.userId));
                            if (sysUser != null) {
                                String sysToken = sysUser.getString(TableUser.token);
                                user.put("token", sysToken);
                            }
                            Cookie cookie = WeChatCookieUtils.buildWeChatCookie(accountConfig, user);
                            response.addCookie(cookie);
                            if (StringUtils.isNotBlank(l)) {
                                return "redirect:" + l;
                            } else {
                                return this.toIndexPage(view, accountConfig);
                            }
                        }
                    }
                } else {
                    return this.toErrorPage(view, "获取授权地址失败", "非常抱歉您访问公众号地址不存在");
                }
            }

            OAuthAuthorizeConfig config = new OAuthAuthorizeConfig();
            config.setPublicAccountCode(accountCode);
            String redirectUri = "";
            if (StringUtils.isNotBlank(l)) {
                redirectUri = RequestUtils.getWebUrl(request) + "/wc_auth/access_token/" + accountCode + ".action?l=" + URLEncoder.encode(l, "UTF-8");
            } else {
                redirectUri = RequestUtils.getWebUrl(request) + "/wc_auth/access_token/" + accountCode + ".action";
            }
            config.setRedirectUri(redirectUri);
            WeChatStartDevelopConnector connector = GlobalService.weChatService.getStartDevelopConnector();
            String url = connector.getOAuthAuthorizeUrl(config);
            if (StringUtils.isNotBlank(url)) {
                return "redirect:" + url;
            } else {
                return this.toErrorPage(view, "获取授权地址失败", "非常抱歉您访问的URL有误或者无效的公众号标识码!");
            }
        } catch (Exception e) {
            return this.toErrorPage(view, "获取授权地址失败", e);
        }
    }

    @RequestMapping("/wc_auth/access_token/{accountCode}")
    public Object redirectAndAccessToken(HttpServletResponse response,
                                         ModelAndView view,
                                         @PathVariable(value = "accountCode") String accountCode,
                                         String code,
                                         String state,
                                         String l) {
        try {
            OAuthAccessTokenConfig config = new OAuthAccessTokenConfig();
            config.setCode(code);
            config.setPublicAccountCode(accountCode);
            config.setState(state);
            WeChatStartDevelopConnector connector = GlobalService.weChatService.getStartDevelopConnector();
            ModelObject user = connector.getOAuthAccessToken(config, false);
            if (user != null) {
                AccountConfig accountConfig = AccountConfig.builder(accountCode);
                ModelObject acc = GlobalService.weChatPublicService.getAccountByCode(accountCode);
                if (acc != null) {
                    user = connector.getUserInfo(WeChatOpenType.PUBLIC.getCode(), acc.getIntValue(TableWeChatAccount.id), user);
                    ModelObject sysUser = GlobalService.userService.silenceLogin(user.getLongValue(TableWeChatUser.userId));
                    if (sysUser != null) {
                        String token = sysUser.getString(TableUser.token);
                        user.put("token", token);
                    }
                    Cookie cookie = WeChatCookieUtils.buildWeChatCookie(accountConfig, user);
                    response.addCookie(cookie);
                    if (StringUtils.isNotBlank(l)) {
                        return "redirect:" + l;
                    } else {
                        return this.toIndexPage(view, accountConfig);
                    }
                } else {
                    return this.toErrorPage(view, "获取用户AccessToken失败", "非常抱歉您访问的公众号不存在");
                }
            } else {
                return this.toErrorPage(view, "获取用户AccessToken失败", "无法获取到用户信息");
            }
        } catch (Exception e) {
            return this.toErrorPage(view, "获取用户AccessToken失败", e);
        }
    }
}
