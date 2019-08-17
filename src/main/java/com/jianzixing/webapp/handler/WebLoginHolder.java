package com.jianzixing.webapp.handler;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.tables.user.TableUser;
import org.mimosaframework.core.utils.RequestUtils;
import com.jianzixing.webapp.web.AbstractWeChatController;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelObject;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

public class WebLoginHolder {
    private static ThreadLocal<ModelObject> threadLocal = new ThreadLocal();

    public static void setUser(ModelObject user) {
        threadLocal.set(user);
    }

    public static ModelObject getUser() {
        return threadLocal.get();
    }

    public static long getUid() {
        ModelObject u = threadLocal.get();
        if (u != null) {
            return u.getLongValue(TableUser.id);
        }
        return 0;
    }

    public static boolean isLogin() {
        ModelObject u = threadLocal.get();
        if (u != null) {
            return true;
        }
        return false;
    }

    public static boolean isLogin(ModelAndView view, HttpServletRequest request) {
        if (isLogin()) {
            String r = request.getParameter("r");
            if (StringUtils.isNotBlank(r)) {
                view.addObject("r", r);
            }
            return true;
        } else {
            try {
                String r = RequestUtils.getCurrentUrl(request);
                view.setViewName("redirect:" +
                        RequestUtils.getWebUrl(request) + "/wx/go_auth.jhtml?r=" +
                        URLEncoder.encode(r, "UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 如果非微信用户则用这个登录
            // RequestUtils.redirect(view, request, "/wx/login.jhtml", "r");
        }
        return false;
    }
}
