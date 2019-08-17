package com.jianzixing.webapp.handler;

import com.jianzixing.webapp.tables.system.TableAdmin;
import org.mimosaframework.core.json.ModelObject;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

import static com.jianzixing.webapp.admin.AdminController.LOGIN_SESS_STR;

public class RequestAdminInstance implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class type = parameter.getParameterType();
        if (type.isAssignableFrom(RequestAdminWrapper.class)) {
            return true;
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        ModelObject object = (ModelObject) request.getSession().getAttribute(LOGIN_SESS_STR);
        if (object != null) {
            RequestAdminWrapper wrapper = new RequestAdminWrapper();
            wrapper.setId(object.getIntValue(TableAdmin.id));
            wrapper.setUserName(object.getString(TableAdmin.userName));
            wrapper.setPassword(object.getString(TableAdmin.password));
            wrapper.setRealName(object.getString(TableAdmin.realName));
            wrapper.setRoleId(object.getIntValue(TableAdmin.roleId));
            return wrapper;
        }
        return null;
    }
}
