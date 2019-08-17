package com.jianzixing.webapp.service.log;

import com.jianzixing.webapp.tables.log.TableRequestAddress;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.RandomUtils;
import org.mimosaframework.core.utils.RequestUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestAddressUtils {
    public static final String SESSION_KEY = "_session_flag";

    public static String getSessionCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals(SESSION_KEY)) {
                    return c.getValue();
                }
            }
        }
        return null;
    }

    public static ModelObject getRequestAddress(HttpServletRequest request, HttpServletResponse response) {
        String sid = setSessionCookie(request, response);
        ModelObject object = new ModelObject();
        object.put(TableRequestAddress.ip, RequestUtils.getIpAddr(request));
        object.put(TableRequestAddress.sessionid, sid);
        object.put(TableRequestAddress.uri, request.getRequestURI());
        object.put(TableRequestAddress.query, request.getQueryString());

        return object;
    }

    public static String setSessionCookie(HttpServletRequest request, HttpServletResponse response) {
        String session = getSessionCookie(request);
        if (StringUtils.isBlank(session)) {
            Cookie cookie = new Cookie(SESSION_KEY, RandomUtils.uuid().toUpperCase());
            cookie.setMaxAge(Integer.MAX_VALUE);
            cookie.setPath("/");
            response.addCookie(cookie);
            return cookie.getValue();
        }
        return session;
    }
}
