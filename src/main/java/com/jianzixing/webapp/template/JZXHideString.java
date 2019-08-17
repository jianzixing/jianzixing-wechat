package com.jianzixing.webapp.template;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

@FreemarkerComponent("JZXHideString")
public class JZXHideString implements TemplateMethodModelEx {

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        String str = arguments.get(0).toString();
        return getHideString(str);
    }

    public static String getHideString(String str) {
        if (str.length() > 1) {
            return str.charAt(0) + "***" + str.charAt(str.length() - 1);
        } else {
            return str + "***";
        }
    }
}
