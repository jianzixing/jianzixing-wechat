package com.jianzixing.webapp.template;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@FreemarkerComponent("JZXUrlEncode")
public class JZXUrlEncode implements TemplateMethodModelEx {

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        try {
            if (arguments.size() > 0 && arguments.get(0) != null) {
                String encode = "UTF-8";
                if (arguments.size() > 1) {
                    encode = arguments.get(1).toString();
                }
                return URLEncoder.encode(arguments.get(0).toString(), encode);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
