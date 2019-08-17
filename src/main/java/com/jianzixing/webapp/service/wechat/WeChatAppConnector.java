package com.jianzixing.webapp.service.wechat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;

public class WeChatAppConnector {
    private static final Log logger = LogFactory.getLog(WeChatMiniProgramConnector.class);
    private final WeChatService weChatService;
    private final List<WeChatListener> events;

    public WeChatAppConnector(WeChatService weChatService, List<WeChatListener> events) {
        this.weChatService = weChatService;
        this.events = events;
    }

}
