package com.jianzixing.webapp.service;

import com.jianzixing.webapp.admin.AdminController;
import org.mimosaframework.core.utils.RequestUtils;
import org.mimosaframework.core.json.ModelObject;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yangankang
 */
public class ServiceFunction {

    public static ModelObject getAdminUser(HttpServletRequest request) {
        return (ModelObject) request.getSession().getAttribute(AdminController.LOGIN_SESS_STR);
    }

    public static String getWebPath(HttpServletRequest request) {
        return RequestUtils.getWebUrl(request);
    }
}
