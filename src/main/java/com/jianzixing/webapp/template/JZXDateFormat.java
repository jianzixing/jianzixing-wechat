package com.jianzixing.webapp.template;

import freemarker.template.SimpleDate;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@FreemarkerComponent("JZXDateFormat")
public class JZXDateFormat implements TemplateMethodModelEx {

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        Object date = arguments.get(0);
        Date time = null;
        if (date instanceof Long) {
            time = new Date((Long) date);
        }
        if (date instanceof Date) {
            time = (Date) date;
        }
        if (date instanceof SimpleDate) {
            time = ((SimpleDate) date).getAsDate();
        }

        if (arguments != null && arguments.size() > 1) {
            String format = arguments.get(1).toString();
            return new SimpleDateFormat(format).format(time);
        }
        if (arguments != null && arguments.size() == 1) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
        }
        return "";
    }
}
