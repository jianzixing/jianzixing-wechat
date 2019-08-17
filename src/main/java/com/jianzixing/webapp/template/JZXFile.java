package com.jianzixing.webapp.template;

import org.mimosaframework.core.utils.RequestUtils;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@FreemarkerComponent("JZXFile")
public class JZXFile implements TemplateMethodModelEx {
    private static final String fileDownloadUrl = "/web/image/load.jhtml?f=";

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        if(arguments==null){
            return "";
        }
        if (arguments.size() == 2) {
            HttpRequestHashModel hashModel = (HttpRequestHashModel) arguments.get(0);
            return getFileUrl(hashModel.getRequest(), arguments.get(1).toString());
        }
        if (arguments.size() == 1) {
            return getFileUrl(null, arguments.get(0).toString());
        }
        return "";
    }

    public static String getFileUrl(HttpServletRequest request, String name) {
        if (StringUtils.isNotBlank(name)) {
            String fileName = name;
            if (fileName.startsWith("http://")) {
                return fileName;
            }
            if (request == null) {
                return fileDownloadUrl + name;
            } else {
                return RequestUtils.getWebUrl(request) + fileDownloadUrl + fileName;
            }
        } else {
            return RequestUtils.getWebUrl(request) + fileDownloadUrl;
        }
    }

    public static String getFileDownloadUrl(HttpServletRequest request) {
        return RequestUtils.getWebUrl(request) + fileDownloadUrl;
    }
}
