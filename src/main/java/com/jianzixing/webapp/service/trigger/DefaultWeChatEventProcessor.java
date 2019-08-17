package com.jianzixing.webapp.service.trigger;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.wechat.WeChatListener;
import com.jianzixing.webapp.service.wechat.model.EventListenerConfig;
import com.jianzixing.webapp.service.wechatsm.WeChatServiceManagerUtils;
import com.jianzixing.webapp.tables.user.TableUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.json.ModelObject;
import org.springframework.stereotype.Service;

@Service
public class DefaultWeChatEventProcessor implements WeChatListener {
    private static final Log logger = LogFactory.getLog(DefaultWeChatEventProcessor.class);

    @Override
    public ModelObject event(EventListenerConfig config) throws Exception {
        ModelObject object = WeChatServiceManagerUtils.getCreateUser(config);
        String key = config.getKey();
        if (object != null) {
            long uid = object.getLongValue(TableUser.id);
            EventType eventType = null;
            ModelObject params = new ModelObject();
            if (key.equals("MSG_EVENT_SUBSCRIBE")) {
                eventType = EventType.WECHAT_MSG_EVENT_SUBSCRIBE;
                params.put("userId", uid);
            }
            if (key.equals("MSG_EVENT_SUBSCRIBE_SCAN")) {
                eventType = EventType.WECHAT_MSG_EVENT_SUBSCRIBE_SCAN;
                params.put("userId", uid);
            }
            if (key.equals("MSG_EVENT_UNSUBSCRIBE")) {
                eventType = EventType.WECHAT_MSG_EVENT_UNSUBSCRIBE;
                params.put("userId", uid);
            }
            if (key.equals("MSG_EVENT_CLICK")) {
                eventType = EventType.WECHAT_MSG_EVENT_CLICK;
                params.put("userId", uid);
            }
            if (key.equals("MSG_EVENT_VIEW")) {
                eventType = EventType.WECHAT_MSG_EVENT_VIEW;
                params.put("userId", uid);
            }
            if (key.equals("MSG_TEXT")) {
                eventType = EventType.WECHAT_MSG_TEXT;
                ModelObject msgObj = config.getObject();
                String text = null;
                if (msgObj != null) {
                    text = msgObj.getString("Content");
                }
                params.put("userId", uid);
                params.put("text", text);
            }
            if (key.equals("MSG_IMAGE")) {
                eventType = EventType.WECHAT_MSG_IMAGE;
                params.put("userId", uid);
            }
            if (key.equals("MSG_IMAGE")) {
                eventType = EventType.WECHAT_MSG_IMAGE;
                params.put("userId", uid);
            }
            if (key.equals("MSG_VOICE")) {
                eventType = EventType.WECHAT_MSG_VOICE;
                params.put("userId", uid);
            }
            if (key.equals("MSG_VIDEO")) {
                eventType = EventType.WECHAT_MSG_VIDEO;
                params.put("userId", uid);
            }
            if (key.equals("MSG_EVENT_SCAN")) {
                eventType = EventType.WECHAT_MSG_EVENT_SCAN;
                params.put("userId", uid);
            }

            if (eventType == null) {
                logger.error("接收到微信通知事件但是不支持当前类型" + config.getKey());
            }
            GlobalService.triggerService.trigger(uid, eventType, params);
            logger.info("接收微信消息事件且触发" + eventType.toString() + "事件");
        } else {
            logger.error("接收到微信通知事件但是没有找到该用户信息" + config.getOpenid());
        }
        return null;
    }
}
