package com.jianzixing.webapp.service.wechat;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.wechat.model.AccountConfig;
import com.jianzixing.webapp.service.wechatsm.WeChatServiceManagerUtils;
import com.jianzixing.webapp.tables.wechat.TableWeChatAccount;
import com.jianzixing.webapp.tables.wechat.TableWeChatUser;
import org.mimosaframework.core.utils.CookieUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.mimosaframework.core.json.ModelObject;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class WeChatCookieUtils {
    public static final String HTTP_COOKIE_NAME_PRE = "user";

    public static Cookie buildWeChatCookie(AccountConfig config, ModelObject weChatUser) {
        String flag = config.getCode();
        if (config.getType() == WeChatOpenType.OPEN_PUBLIC) {
            flag = flag + config.getAppid();
        }

        long uid = weChatUser.getLongValue(TableWeChatUser.userId);
        String openid = weChatUser.getString(TableWeChatUser.openid);

        ModelObject object = new ModelObject();
        object.put("uid", uid);
        object.put("code", flag);
        object.put("openid", openid);
        object.put("token", weChatUser.getString("token"));
        Cookie cookie = null;
        try {
            cookie = new Cookie(HTTP_COOKIE_NAME_PRE, URLEncoder.encode(object.toJSONString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        cookie.setPath("/");
        cookie.setMaxAge(Integer.MAX_VALUE);
        return cookie;
    }

    public static ModelObject getWeChatCookieCode(HttpServletRequest request, AccountConfig config) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            String flag = config.getCode();
            if (config.getType() == WeChatOpenType.OPEN_PUBLIC) {
                flag = flag + config.getAppid();
            }
            for (Cookie cookie : cookies) {
                if (cookie != null && cookie.getName().equals(HTTP_COOKIE_NAME_PRE)) {
                    String json = cookie.getValue();
                    if (StringUtils.isNotBlank(json)) {
                        try {
                            ModelObject object = null;
                            try {
                                object = ModelObject.parseObject(URLDecoder.decode(json, "utf-8"));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            if (object.getString("code").equalsIgnoreCase(flag)) {
                                return object;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return null;
    }

    public static String getWeChatCookieOpenId(HttpServletRequest request, AccountConfig config) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            String flag = config.getCode();
            if (config.getType() == WeChatOpenType.OPEN_PUBLIC) {
                flag = flag + config.getAppid();
            }
            for (Cookie cookie : cookies) {
                if (cookie != null && cookie.getName().equals(HTTP_COOKIE_NAME_PRE)) {
                    String json = cookie.getValue();
                    if (StringUtils.isNotBlank(json)) {
                        try {
                            ModelObject object = null;
                            try {
                                object = ModelObject.parseObject(URLDecoder.decode(json, "utf-8"));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            if (object.getString("code").equalsIgnoreCase(flag)) {
                                return object.getString("openid");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return null;
    }

    public static ModelObject getCurrentUser(HttpServletRequest request, AccountConfig config) {
        String openid = WeChatCookieUtils.getWeChatCookieOpenId(request, config);
        if (StringUtils.isNotBlank(openid)) {
            ModelObject acc = WeChatServiceManagerUtils.getAccountModel(config);
            if (acc != null) {
                ModelObject user = GlobalService.weChatUserService.getUserByOpenId(WeChatOpenType.PUBLIC.getCode(), acc.getIntValue(TableWeChatAccount.id), openid);
                return user;
            }
        }
        return null;
    }

    public static long getUidByDebug(HttpServletRequest request) {
        Cookie uid = CookieUtils.getCookie(request, "uid");
        if (uid != null) {
            String uidStr = uid.getValue();
            if (NumberUtils.isNumber(uidStr)) {
                return Long.parseLong(uidStr);
            }
        }
        return 0;
    }
}
