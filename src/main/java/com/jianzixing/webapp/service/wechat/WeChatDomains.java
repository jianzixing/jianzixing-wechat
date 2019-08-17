package com.jianzixing.webapp.service.wechat;

public class WeChatDomains {
    private static final String[] DOMAINS = new String[]{
            "api.weixin.qq.com", "sh.api.weixin.qq.com",
            "sz.api.weixin.qq.com", "hk.api.weixin.qq.com"
    };

    public static final String OPEN_URL = "https://open.weixin.qq.com";

    public static String getApiUrl() {
        return DOMAINS[0];
    }

    public static String getApiUrl(int index) {
        return DOMAINS[index];
    }

    public static String getApiUrl(String uri) {
        return "https://" + DOMAINS[0] + uri;
    }
}
