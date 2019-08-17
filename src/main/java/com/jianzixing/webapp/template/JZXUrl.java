package com.jianzixing.webapp.template;

import org.mimosaframework.core.utils.RequestUtils;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@FreemarkerComponent("JZXUrl")
public class JZXUrl implements TemplateMethodModelEx {
    @Override
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments != null && arguments.size() > 0) {
            Object arg0 = arguments.get(0);
            if (arg0 instanceof HttpRequestHashModel) {
                HttpRequestHashModel hashModel = (HttpRequestHashModel) arg0;
                if (arguments.size() > 1) {
                    String url = arguments.get(1).toString();
                    if (url.equals("$")) {
                        return RequestUtils.getCurrentUrl(hashModel.getRequest());
                    } else if (url.equals("$$")) {
                        try {
                            return URLEncoder.encode(RequestUtils.getCurrentUrl(hashModel.getRequest()), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (url.startsWith("http://")) {
                            return url;
                        }
                        if (url.startsWith("local://")) {
                            return RequestUtils.getWebUrl(hashModel.getRequest()) + "/" + url.substring("local://".length());
                        }
                        return RequestUtils.getWebUrl(hashModel.getRequest()) + url;
                    }
                } else {
                    return RequestUtils.getWebUrl(hashModel.getRequest());
                }
            }
            if (arg0 instanceof SimpleScalar) {
                String cmd = arg0.toString();
                if (cmd.equals("&") || cmd.equals("&&")) {
                    if (arguments.size() > 1) {
                        String url = arguments.get(1).toString();
                        try {
                            url = URLDecoder.decode(url, "UTF-8");
                            url = url.trim();

                            if (arguments.size() > 3) {
                                String key = arguments.get(2).toString();
                                String value = arguments.get(3).toString();
                                if (StringUtils.isNotBlank(key)) {
                                    if (url.indexOf("?") > 0) {
                                        Map<String, String> map = RequestUtils.getUrlParams(url);
                                        map.put(key.trim(), value.trim());
                                        url = RequestUtils.replaceUrlParams(url, map);
                                    } else {
                                        url += "?" + key + "=" + value;
                                    }
                                }
                            }

                            if (cmd.equals("&&")) {
                                return URLEncoder.encode(url, "UTF-8");
                            }
                            return url;
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return "";
    }
}
