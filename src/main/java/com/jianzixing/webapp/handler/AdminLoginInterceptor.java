package com.jianzixing.webapp.handler;

import com.jianzixing.webapp.admin.AdminController;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.system.TableAdmin;
import org.mimosaframework.core.utils.RequestUtils;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yangankang
 */
public class AdminLoginInterceptor implements HandlerInterceptor {
    private void write(HttpServletResponse response, ResponseMessage message) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        try {
            response.getWriter().print(message.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        GlobalService.systemConfigService.setWebUrl(RequestUtils.getWebUrl(request));
        if (handler instanceof HandlerMethod) {
            HandlerMethod methodHandler = (HandlerMethod) handler;

            AdminSkipLoginCheck adminLogined = (AdminSkipLoginCheck) methodHandler.getMethodAnnotation(AdminSkipLoginCheck.class);
            if (adminLogined == null) {
                ModelObject object = (ModelObject) request.getSession().getAttribute(AdminController.LOGIN_SESS_STR);
                if (object == null) {
                    write(response, new ResponseMessage(-100, "您已退出登录,请先登录后继续使用!"));
                    return false;
                }

                /**
                 * 如果Controller有注解则一样跳过不检查权限
                 */
                AuthSkipCheck authSkipCheck = methodHandler.getBeanType().getAnnotation(AuthSkipCheck.class);
                if (authSkipCheck == null) {
                    authSkipCheck = methodHandler.getMethodAnnotation(AuthSkipCheck.class);
                }
                if (authSkipCheck == null) {
                    String uri = request.getRequestURI();
                    String[] s = uri.split("\\/");
                    String clazz = s[s.length - 2];
                    String method = s[s.length - 1].split("\\.")[0];
                    String page = request.getParameter("_page");

                    int roleId = object.getIntValue(TableAdmin.roleId);

                    if (AdminController.isSuperUser(request)) {
                        roleId = -100;
                    }

                    ModelObject role = GlobalService.systemService.getPageApis(clazz, method, page, roleId);
                    if (role == null) {
                        write(response, new ResponseMessage(-100, "没有权限访问当前URL:  " + clazz + "   " + method));
                        return false;
                    }
                }
            }

            return true;
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
