package com.jianzixing.webapp.template;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.mimosaframework.core.encryption.Hashids;

import java.util.List;

/**
 * 将顺序的自增ID数字加密成非顺序的字符串，防止有人循环爬取数字
 * <p>
 * 使用方式，默认是加密，如果是加密第二个参数传入Long类型数字
 * ${jzxHashid("salt",1)}
 * 如果是解密，第二个参数传入字符串，且第三个参数传入true
 * ${jzxHashid("salt","UV",true)}
 */
@FreemarkerComponent("JZXHashid")
public class JZXHashid implements TemplateMethodModelEx {

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments != null && arguments.size() > 1) {
            String salt = (String) arguments.get(0);
            Object number = arguments.get(1);
            boolean isDecode = false;
            if (arguments.size() > 2) {
                isDecode = (Boolean) arguments.get(2);
            }
            if (isDecode) {
                return decode(salt, (String) number);
            } else {
                return encode(salt, (Long) number);
            }
        }
        return "";
    }

    public static String encode(String salt, long number) {
        Hashids hashids = new Hashids(salt);
        String id = hashids.encode(number);
        return id;
    }

    public static long decode(String salt, String encodeStr) {
        Hashids hashids = new Hashids(salt);
        long[] id = hashids.decode(encodeStr);
        if (id != null && id.length > 0) {
            return id[0];
        }
        return 0;
    }
}
