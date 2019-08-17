package com.jianzixing.webapp.web;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.wechat.WeChatCookieUtils;
import com.jianzixing.webapp.service.wechat.WeChatThirdPartyConnector;
import com.jianzixing.webapp.service.wechat.WeChatWebPageConnector;
import com.jianzixing.webapp.service.wechat.aes.AesException;
import com.jianzixing.webapp.service.wechat.model.AccountConfig;
import com.jianzixing.webapp.service.wechat.WeChatOpenType;
import com.jianzixing.webapp.service.wechatsm.WeChatServiceManagerUtils;
import com.jianzixing.webapp.tables.user.TableUser;
import com.jianzixing.webapp.tables.wechat.TableWeChatOpenAccount;
import com.jianzixing.webapp.tables.wechat.TableWeChatUser;
import com.jianzixing.webapp.tables.wechat.TableWeChatOpen;
import org.mimosaframework.core.utils.RequestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.HttpUtils;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.springmvc.APIController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;

@APIController
public class WebWCThirdPartyController extends AbstractWeChatController {
    private static final Log logger = LogFactory.getLog(WebWCThirdPartyController.class);

    // 定时(约十分钟)发送授权事件url
    public static String getUrlByAuthEvent(HttpServletRequest request, String code) {
        return RequestUtils.getWebUrl(request) + "/wc_tp/" + code + ".jhtml";
    }

    // 公众号授权消息接收地址
    public static String getUrlByEventMessage(HttpServletRequest request, String code) {
        return RequestUtils.getWebUrl(request) + "/wc_tp_server/$APPID$/" + code + ".jhtml";
    }

    // 公众号管理员授权公众号权限地址
    public static String getMobileUrlByAdminAuth(HttpServletRequest request, String code) {
        return RequestUtils.getWebUrl(request) + "/wc_tp/auth/" + code + ".jhtml";
    }

    // 公众号管理员授权公众号权限地址
    public static String getPCUrlByAdminAuth(HttpServletRequest request, String code) throws IOException {
        // return RequestUtils.getWebUrl(request) + "/wc_tp/auth_pc/" + code + ".html";

        WeChatThirdPartyConnector connector = GlobalService.weChatService.getThirdPartyConnector();
        String pre = RequestUtils.getWebUrl(request);
        String url = connector.authPCUrl(code, pre + "/wc_tp/auth_succ/" + code + ".jhtml");
        return url;
    }

    /**
     * <xml>
     * <AppId><![CDATA[wx5eb4c13d4dc8dc04]]></AppId>
     * <Encrypt><![CDATA[OEfOdJMtSymURFHl9OaL53AiXjZS9TV3A9QzjUvA1TcxY/OuAZlLT2lY2XBo+pIbM/8ucOsGpnTJLgfOWuOlRuZ+iPdJDijfU0R17UI3zue577oDbdPaXFtuTv86o1zPleYGUHhdzlUUV3rgI7XpF0OOcBIWkUDyMu+W1UE7d99Oq7OerDO7Id62RNGH7Yi2/WnPlK8oT4PED6G+zy7/4BpyWq5LiC19/hzuQFMw7jTgOJ9KIpnrZHJV6dfdJFCwpkg6phrCN2BYql3r7RGuQXpHTZYFsckbKkDqw5Ooc+XKLOsCknOu2MlI4u0O94e+FF8Q8BfpotHMXWoU6ppaPU90vlpWgHwg4py6JwsGvV0OP+5J9nX6VNBTqEcR4/a//zK5JyGG4oCxYAMUZFzKE4xMP6TI+m+/tJ6GeF68wa9WWj/WMd2fmkyTODp5OJ8jI6N8PDlrdWliOPznkF2k4A==]]></Encrypt>
     * </xml>
     *
     * @param code
     * @param body
     * @return
     */
    @RequestMapping("/wc_tp/{code}")
    @ResponseBody
    public String authEventReceive(@PathVariable(value = "code") String code,
                                   @RequestBody String body) {
        logger.info("第三方平台授权事件接收URL:" + code + "," + body);
        WeChatThirdPartyConnector connector = GlobalService.weChatService.getThirdPartyConnector();
        try {
            return connector.pushVerifyTicket(code, body, true);
        } catch (AesException e) {
            logger.error("接收第三方平台授权事件接收URL出错", e);
        }
        return "success";
    }

    /**
     * <xml>
     * <ToUserName><![CDATA[gh_946efc68636f]]></ToUserName>
     * <Encrypt><![CDATA[QhI3liE2R642BEqyDEnc6eyKNWykuBWfOp3JeEpsewVtuUZI8SGbwmAg8Phz8o0/7KW0WdX3G5hJ1z4PQmO1PkZ9adIjoh9+vWJBEKn4iLon1kWEPlWLSWU8aJXMDb1m5seTaKMvjOA0e0NKBO7vGuNmMOci3hI4sRyTfKR6Rht8jczqZ2HutTPQx/rAS/WLksI41+lNJAdCIje4QKPl0/X+MFKcx213nxlRz+Ith5lZh2mO3IsnBJlkIFGSL+yob89Lk8k+Ov4llR25DL0APDyNqJ3krczIFWE9w14hzewVUtifXG34w+RiHkLYtrMJJNIyWbPdvN1jDEXZOvwBPxI6icnn5sT7PqygzfYeMUm0SfGHw96oN7y53waZ+UShKOPSiVVWFhs6Abky0BQVa6YDIRnHF7WSLyZrVT/vZAs=]]></Encrypt>
     * </xml>
     *
     * @param request
     * @param code
     * @param appid
     * @param body
     * @return
     */
    @RequestMapping("/wc_tp_server/{appid}/{code}")
    @ResponseBody
    public String eventMessageReceive(HttpServletRequest request,
                                      @PathVariable(value = "code") String code,
                                      @PathVariable(value = "appid") String appid,
                                      @RequestBody String body) {
        logger.info("第三方平台消息与事件接收URL:" + code + "," + body);
        WeChatThirdPartyConnector connector = GlobalService.weChatService.getThirdPartyConnector();
        try {
            return connector.server(request, code, appid, body, true);
        } catch (AesException e) {
            logger.error("处理第三方平台消息与事件接收URL出错", e);
        }
        return "";
    }

    /**
     * 微信公众号管理员授权给第三方平台的地址(微信浏览器授权)
     *
     * @param request
     * @param code
     * @return
     */
    @RequestMapping("/wc_tp/auth/{code}")
    public Object redirectMobileAuthPage(HttpServletRequest request,
                                         HttpServletResponse response,
                                         ModelAndView view,
                                         @PathVariable(value = "code") String code) {
        try {
            WeChatThirdPartyConnector connector = GlobalService.weChatService.getThirdPartyConnector();
            String pre = RequestUtils.getWebUrl(request);
            String url = connector.authMobileUrl(code, pre + "/wc_tp/auth_succ/" + code + ".action");
            return "redirect:" + url;
        } catch (IOException e) {
            return this.toErrorPage(view, "获取第三方平台微信授权地址出错", e);
        }
    }

    /**
     * 微信公众号管理员授权给第三方平台的地址(电脑授权)
     *
     * @param request
     * @param code
     * @return
     */
    @RequestMapping("/wc_tp/auth_pc/{code}")
    public Object redirectPCAuthPage(HttpServletRequest request,
                                     HttpServletResponse response,
                                     ModelAndView view,
                                     @PathVariable(value = "code") String code) {
        try {
            WeChatThirdPartyConnector connector = GlobalService.weChatService.getThirdPartyConnector();
            String pre = RequestUtils.getWebUrl(request);
            String url = connector.authPCUrl(code, pre + "/wc_tp/auth_succ/" + code + ".action");
            return "redirect:" + url;
        } catch (IOException e) {
            return this.toErrorPage(view, "获取第三方平台电脑授权地址出错", e);
        }
    }

    /**
     * 微信公众号管理员授权给第三方平台,授权成功后处理
     *
     * @param request
     * @param code
     * @return
     */
    @RequestMapping("/wc_tp/auth_succ/{code}")
    public Object redirectIndexPage(HttpServletRequest request,
                                    ModelAndView view,
                                    @PathVariable(value = "code") String code) {
        try {
            WeChatThirdPartyConnector connector = GlobalService.weChatService.getThirdPartyConnector();
            connector.authRedirectParam(request, code);
            return this.toSuccessPage(view, "第三方平台公众号授权成功", "恭喜您，您的公众号已经授权给第三方平台管理");
        } catch (IOException e) {
            return this.toErrorPage(view, "第三方平台公众号授权回调失败", e);
        }
    }

    /**
     * 第三方平台授权用户登录获取openid获取用户基本信息
     *
     * @param code
     * @return
     */
    @RequestMapping("/wc_tp/oauth2/{code}/{appid}")
    public Object oauth2(@PathVariable(value = "code") String code,
                         @PathVariable(value = "appid") String appid,
                         String l,
                         ModelAndView view,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        try {
            AccountConfig accountConfig = AccountConfig.builder(code, appid, WeChatOpenType.OPEN_PUBLIC);
            String openid = WeChatCookieUtils.getWeChatCookieOpenId(request, accountConfig);
            ModelObject acc = WeChatServiceManagerUtils.getAccountModel(accountConfig);
            if (acc != null) {
                int accountId = acc.getIntValue(TableWeChatOpenAccount.id);
                if (StringUtils.isNotBlank(openid)) {
                    ModelObject user = GlobalService.weChatUserService.getUserByOpenId(WeChatOpenType.OPEN_PUBLIC.getCode(), accountId, openid);
                    if (user != null) {
                        WeChatWebPageConnector webPageService = GlobalService.weChatService.getWebPageConnector();
                        String token = webPageService.getAccessToken(accountConfig, openid);
                        if (StringUtils.isNotBlank(token)) {
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
                                request.getSession().setAttribute("user_token", token);
                                return this.toIndexPage(view, accountConfig);
                            }
                        }
                    }
                }


                ModelObject object = GlobalService.weChatOpenService.getOpenByCode(code);
                String tpAppid = object.getString(TableWeChatOpen.appId);
                String redirect = "";
                if (StringUtils.isNotBlank(l)) {
                    redirect = RequestUtils.getWebUrl(request) + "/wc_tp/oauth2/redirect/" + code + "/" + appid + ".action?l=" + URLEncoder.encode(l, "UTF-8");
                } else {
                    redirect = RequestUtils.getWebUrl(request) + "/wc_tp/oauth2/redirect/" + code + "/" + appid + ".action";
                }

                String url = "https://open.weixin.qq.com/connect/oauth2/authorize?" +
                        "appid=" + appid +
                        "&redirect_uri=" + URLEncoder.encode(redirect, "UTF-8") +
                        "&response_type=code" +
                        "&scope=snsapi_userinfo" +
                        "&state=STATE" +
                        "&component_appid=" + tpAppid +
                        "#wechat_redirect";
                return "redirect:" + url;
            } else {
                return this.toErrorPage(view, "第三方平台授权用户AccessToken", "没有找到授权公众号");
            }
        } catch (Exception e) {
            return this.toErrorPage(view, "第三方平台授权用户AccessToken", e);
        }
    }


    @Autowired
    SessionTemplate sessionTemplate;

    /**
     * 第三方平台授权用户登录后跳转会返回处理
     *
     * @param code
     * @param state
     * @param tp
     * @return
     */
    @RequestMapping("/wc_tp/oauth2/redirect/{tp}/{appid}")
    public Object redirectOAuth2(HttpServletRequest request,
                                 HttpServletResponse response,
                                 ModelAndView view,
                                 String code,
                                 String state,
                                 String l,
                                 @PathVariable(value = "appid") String appid,
                                 @PathVariable(value = "tp") String tp) {
        try {
            WeChatThirdPartyConnector connector = GlobalService.weChatService.getThirdPartyConnector();
            ModelObject user = connector.getUserAccessToken(tp, appid, code);

            if (user != null) {
                String accessToken = user.getString(TableWeChatUser.accessToken);
                AccountConfig accountConfig = AccountConfig.builder(code, appid, WeChatOpenType.OPEN_PUBLIC);
                ModelObject sysUser = GlobalService.userService.silenceLogin(user.getLongValue(TableWeChatUser.userId));
                if (sysUser != null) {
                    String sysToken = sysUser.getString(TableUser.token);
                    user.put("token", sysToken);
                }
                Cookie cookie = WeChatCookieUtils.buildWeChatCookie(accountConfig, user);
                response.addCookie(cookie);
                request.getSession().setAttribute("user_token", accessToken);
                if (StringUtils.isNotBlank(l)) {
                    return "redirect:" + l;
                } else {
                    return this.toIndexPage(view, accountConfig);
                }
            } else {
                return this.toErrorPage(view, "登录失败", "第三方平台用户授权AccessToken获取用户信息失败");
            }
        } catch (Exception e) {
            return this.toErrorPage(view, "第三方平台用户授权AccessToken获取用户信息失败", e);
        }
    }
}
