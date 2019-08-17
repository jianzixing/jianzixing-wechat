package com.jianzixing.webapp.web;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.wechat.WeChatCookieUtils;
import com.jianzixing.webapp.service.wechat.WeChatOpenType;
import com.jianzixing.webapp.service.wechat.WeChatWebPageConnector;
import com.jianzixing.webapp.service.wechat.model.AccountConfig;
import com.jianzixing.webapp.service.wechatsm.WeChatServiceManagerUtils;
import com.jianzixing.webapp.tables.user.TableUser;
import com.jianzixing.webapp.tables.wechat.TableWeChatAccount;
import com.jianzixing.webapp.tables.wechat.TableWeChatOpenAccount;
import org.mimosaframework.core.utils.RequestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.json.ModelObject;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public abstract class AbstractWeChatController {
    private static final Log logger = LogFactory.getLog(AbstractWeChatController.class);
    private static final String DEFAULT_HTML_NAME = "jhtml";

    /**
     * 授权成功后跳转到首页
     *
     * @return
     */
    protected ModelAndView toIndexPage(ModelAndView view, AccountConfig accountConfig) {
        view.addObject("title", "授权成功");
        view.setViewName("redirect:" + getHomePage(accountConfig));
        return view;
    }

    /**
     * 获取用户的授权跳转地址出错
     *
     * @return
     */
    protected ModelAndView toSuccessPage(ModelAndView view, String title) {
        return toSuccessPage(view, title, null);
    }

    protected ModelAndView toSuccessPage(ModelAndView view, String title, String detail) {
        view.addObject("desc", detail);
        view.addObject("title", title);
        view.setViewName("common/wechat_success");
        return view;
    }

    /**
     * 用户授权地址跳转回来后获取用户的登录状态时出错
     *
     * @return
     */
    protected ModelAndView toErrorPage(ModelAndView view, String title, String detail) {
        return toErrorPage(view, title, detail, null);
    }

    protected ModelAndView toErrorPage(ModelAndView view, String title, String detail, String url, String buttonName) {
        view.addObject("url", url);
        view.addObject("button", buttonName);
        return toErrorPage(view, title, detail, null);
    }

    protected ModelAndView toErrorPage(ModelAndView view, String title, Exception e) {
        return toErrorPage(view, title, null, e);
    }

    protected ModelAndView toErrorPage(ModelAndView view, String title, String detail, Exception e) {
        if (e != null) {
            logger.error(title, e);
            view.addObject("desc", e.getMessage());
        }
        if (StringUtils.isNotBlank(detail)) {
            view.addObject("desc", detail);
        }
        view.addObject("title", title);
        view.setViewName("common/wechat_failure");
        return view;
    }

    public String getHomePage(AccountConfig config) {
        if (config != null) {
            WeChatOpenType type = config.getType();
            if (type != null &&
                    (type.equals(WeChatOpenType.OPEN_PUBLIC)
                            || type.equals(WeChatOpenType.PUBLIC))) {

                ModelObject object = WeChatServiceManagerUtils.getAccountModel(config);
                if (type.equals(WeChatOpenType.OPEN_PUBLIC)) {
                    String tpCode = object.getString(TableWeChatOpenAccount.tpCode);
                    String appid = object.getString(TableWeChatOpenAccount.authorizerAppid);
                    return "/" + tpCode + "/" + appid + "." + DEFAULT_HTML_NAME;
                }
                if (type.equals(WeChatOpenType.PUBLIC)) {
                    String code = object.getString(TableWeChatAccount.code);
                    return "/" + code + "." + DEFAULT_HTML_NAME;
                }
            }
        }
        return null;
    }

    public String getHomePage(HttpServletRequest request, AccountConfig config) {
        return RequestUtils.getWebUrl(request) + getHomePage(config);
    }

    public AccountConfig getAccountConfigByFlag(String flag) {
        // 如果没有下划线则表示公众号的code如果有则表示第三方平台的code_appid
        if (flag.indexOf("_") >= 0) {
            String[] flagArr = flag.split("_");
            return AccountConfig.builder(flagArr[0], flagArr[1], WeChatOpenType.OPEN_PUBLIC);
        } else {
            return AccountConfig.builder(flag);
        }
    }

    public AccessTokenValid isAccessTokenValid(HttpServletRequest request, AccountConfig config) throws Exception {
        String openid = request.getParameter("openid");
        String accessToken = request.getParameter("accessToken");
        if (StringUtils.isBlank(openid)) {
            openid = WeChatCookieUtils.getWeChatCookieOpenId(request, config);
        }
        if (StringUtils.isBlank(accessToken)) {
            ModelObject cookie = WeChatCookieUtils.getWeChatCookieCode(request, config);
            if (cookie != null) accessToken = cookie.getString("accessToken");
        }
        if (StringUtils.isBlank(openid)) {
            return new AccessTokenValid(false, null, null);
        }
        ModelObject acc = WeChatServiceManagerUtils.getAccountModel(config);
        if (acc == null) {
            throw new Exception("非常抱歉您访问公众号地址不存在");
        }

        ModelObject user = GlobalService.weChatUserService.getUserByOpenId(
                config.getType().getCode(),
                acc.getIntValue(TableWeChatAccount.id),
                openid);
        if (user == null) {
            return new AccessTokenValid(false, null, acc);
        }
        WeChatWebPageConnector webPageConnector = GlobalService.weChatService.getWebPageConnector();
        String token = webPageConnector.getAccessToken(config, openid);
        if (StringUtils.isNotBlank(token) /*&& token.equals(accessToken)*/) {
            return new AccessTokenValid(true, user, acc);
        }
        return new AccessTokenValid(false, user, acc);
    }

    public static String getPublicUserAuthUrl(HttpServletRequest request, AccountConfig config, String r) {
        String web = RequestUtils.getWebUrl(request);
        String url = null;
        if (config.getType() == WeChatOpenType.OPEN_PUBLIC) {
            url = web + "/wc_tp/oauth2/" + config.getCode() + "/" + config.getAppid() + "." + DEFAULT_HTML_NAME;
        }
        if (config.getType() == WeChatOpenType.PUBLIC) {
            url = web + "/wc_auth/" + config.getCode() + "." + DEFAULT_HTML_NAME;
        }
        if (StringUtils.isNotBlank(r)) {
            try {
                url += "?l=" + URLEncoder.encode(r, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return url;
    }

    public static String getServerUrl(HttpServletRequest request, AccountConfig config) {
        String url = null;
        if (config.getType() == WeChatOpenType.OPEN_PUBLIC) {
            url = WebWCThirdPartyController.getUrlByAuthEvent(request, config.getCode());
        }
        if (config.getType() == WeChatOpenType.PUBLIC) {
            url = WebWCPublicController.getWcAuthorize(request, config.getCode());
        }
        return url;
    }

    /**
     * 获取系统默认的账号或者第三方账号的用户登录地址
     *
     * @param request
     * @return
     */
    public static String getDefaultUserAuthUser(HttpServletRequest request, String r) throws ModuleException {
        ModelObject dfpublic = GlobalService.weChatPublicService.getDefaultAccount();
        if (dfpublic == null) {
            throw new ModuleException("not_found_default", "没有找到默认的公众号信息");
        }
        int openType = dfpublic.getIntValue("openType");
        AccountConfig config = null;
        if (openType == WeChatOpenType.PUBLIC.getCode()) {
            config = new AccountConfig(WeChatOpenType.getOpenType(openType), dfpublic.getString(TableWeChatAccount.code));
        }
        if (openType == WeChatOpenType.OPEN_PUBLIC.getCode()) {
            config = new AccountConfig(dfpublic.getString(TableWeChatOpenAccount.tpCode),
                    dfpublic.getString(TableWeChatOpenAccount.authorizerAppid), WeChatOpenType.getOpenType(openType));
        }

        return getPublicUserAuthUrl(request, config, r);
    }

    public static class AccessTokenValid {
        private boolean isValid;
        private ModelObject user;
        private ModelObject acc;

        public AccessTokenValid(boolean isValid, ModelObject user, ModelObject acc) {
            this.isValid = isValid;
            this.user = user;
            this.acc = acc;
        }

        public boolean isValid() {
            return isValid;
        }

        public ModelObject getUser() {
            return user;
        }

        public ModelObject getAcc() {
            return acc;
        }

        public long getUid() {
            if (user != null) {
                return user.getLongValue(TableUser.id);
            }
            return 0;
        }
    }
}
