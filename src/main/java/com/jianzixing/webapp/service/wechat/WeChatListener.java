package com.jianzixing.webapp.service.wechat;

import com.jianzixing.webapp.service.wechat.model.EventListenerConfig;
import org.mimosaframework.core.json.ModelObject;

public interface WeChatListener {
    ModelObject event(EventListenerConfig config) throws Exception;
}
