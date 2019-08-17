package com.jianzixing.webapp.service.wechat.model;

import com.jianzixing.webapp.service.wechat.WeChatOpenType;
import org.mimosaframework.core.json.ModelObject;

public class EventListenerConfig extends AccountConfig {
    private String openid;
    private String key;
    private ModelObject object;

    public EventListenerConfig(String code) {
        super(code);
    }

    public EventListenerConfig(String code, String appid) {
        super(code, appid);
    }

    public EventListenerConfig(String code, String appid, WeChatOpenType type) {
        super(code, appid, type);
    }

    public EventListenerConfig(ModelObject object, String code, String appid, WeChatOpenType type) {
        super(code, appid, type);
        this.object = object;
    }

    public EventListenerConfig(String key, ModelObject object, String code, String appid, WeChatOpenType type) {
        super(code, appid, type);
        this.key = key;
        this.object = object;
    }


    public static EventListenerConfig builder(String code) {
        return new EventListenerConfig(code);
    }

    public static EventListenerConfig builder(String code, String appid) {
        return new EventListenerConfig(code, appid);
    }

    public static EventListenerConfig builder(String code, String appid, WeChatOpenType type) {
        return new EventListenerConfig(code, appid, type);
    }

    public static EventListenerConfig builder(String key, ModelObject object, String code, String appid, WeChatOpenType type) {
        return new EventListenerConfig(key, object, code, appid, type);
    }

    public static EventListenerConfig builder(ModelObject object, String code, String appid, WeChatOpenType type) {
        return new EventListenerConfig(object, code, appid, type);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ModelObject getObject() {
        return object;
    }

    public void setObject(ModelObject object) {
        this.object = object;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
}
