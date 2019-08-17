package com.jianzixing.webapp.handler;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.user.TableUser;
import org.mimosaframework.core.utils.CookieUtils;
import org.mimosaframework.core.utils.RequestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.json.ModelObject;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.Date;

public class WebLoginInterceptor implements HandlerInterceptor {
    private static Log logger = LogFactory.getLog(WebLoginInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        GlobalService.systemConfigService.setWebUrl(RequestUtils.getWebUrl(request));
        String user = CookieUtils.getCookieValue(request, "user");
        if (StringUtils.isNotBlank(user)) {
            try {
                user = URLDecoder.decode(user, "utf-8");
                ModelObject userObj = ModelObject.parseObject(user);
                long uid = userObj.getLongValue("uid");
                String token = userObj.getString("token");
                ModelObject fullUser = GlobalService.userService.getUser(uid);
                if (fullUser != null) {
                    Date lastLoginTime = fullUser.getDate(TableUser.lastLoginTime);
                    String fromToken = fullUser.getString(TableUser.token);

                    if (StringUtils.isNotBlank(token) && token.equals(fromToken)) {
                        if (lastLoginTime != null &&
                                lastLoginTime.getTime() > (System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000l)) {
                            WebLoginHolder.setUser(fullUser);

                            // 如果15天后又登录了则刷新登录时间
                            if (lastLoginTime.getTime() < (System.currentTimeMillis() - 15 * 24 * 60 * 60 * 1000l)) {
                                GlobalService.userService.updateLastLoginTime(uid);
                            }
                        }

                        if (lastLoginTime == null) {
                            WebLoginHolder.setUser(fullUser);
                            GlobalService.userService.updateLastLoginTime(uid);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
