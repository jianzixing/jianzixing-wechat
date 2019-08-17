package com.jianzixing.webapp.service.wechat;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.wechat.model.AccountConfig;
import com.jianzixing.webapp.service.wechat.model.AccountServerConfig;
import com.jianzixing.webapp.service.wechat.model.EventListenerConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.HttpUtils;

import java.io.IOException;
import java.util.List;

public class WeChatMessageConnector {
    private static final Log logger = LogFactory.getLog(WeChatMessageConnector.class);
    private static final String TYPE = "MSG";
    private final List<WeChatListener> events;
    private final WeChatService weChatService;

    public WeChatMessageConnector(WeChatService weChatService, List<WeChatListener> events) {
        this.weChatService = weChatService;
        this.events = events;
    }

    public String receive(AccountServerConfig config) {
        String xml = config.getBody();
        logger.info("接收到微信消息事件通知:" + xml);
        ModelObject object = WeChatInterfaceUtils.xmlToModel(xml);
        String msgType = object.getString("MsgType");
        String openid = object.getString("FromUserName");

        EventListenerConfig elc = EventListenerConfig.builder(object, config.getCode(), config.getAppid(), config.getType());
        elc.setOpenid(openid);
        elc.setAccountId(config.getAccountId());
        if (msgType.equalsIgnoreCase("event")) {
            return this.receiveEventMessage(elc);
        } else if (msgType.equalsIgnoreCase("text")) {
            return this.receiveTextMessage(elc);
        }
        return null;
    }

    /**
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140454
     * <p>
     * 关注/取消关注事件    MsgType:event   Event:subscribe #关注 , unsubscribe #取消关注
     * 扫描带参数二维码事件   MsgType:event   Event:subscribe #扫码关注 , SCAN #关注后扫码
     * 上报地理位置事件 MsgType:event   Event:LOCATION
     * 自定义菜单事件#点击菜单拉取消息时的事件推送   MsgType:event   Event:CLICK
     * 自定义菜单事件#点击菜单跳转链接时的事件推送   MsgType:event   Event:VIEW
     *
     * @param config
     * @return
     */
    private String receiveEventMessage(EventListenerConfig config) {
        ModelObject object = config.getObject();
        String msgType = object.getString("MsgType");
        String event = object.getString("Event");
        if (events != null) {
            String key = TYPE + "_" + msgType.toUpperCase() + "_" + event.toUpperCase();
            config.setKey(key);
            String reply = null;
            if (events != null) {
                reply = evalEventNotify(config, object, events);
            }

            String eventKey = object.getString("EventKey");
            if (key.equals("MSG_EVENT_SUBSCRIBE") && eventKey.startsWith("qrscene_")) {
                key = "MSG_EVENT_SUBSCRIBE_SCAN";
                config.setKey(key);
                if (events != null) {
                    evalEventNotify(config, object, events);
                }
            }
            if (reply != null) return reply;
        }
        return "success";
    }

    private String evalEventNotify(EventListenerConfig config, ModelObject object, List<WeChatListener> listeners) {
        ModelObject reply = null;
        for (WeChatListener listener : listeners) {
            try {
                ModelObject r = listener.event(config);
                if (r != null) {
                    reply = r;
                }
            } catch (Exception e) {
                logger.error("执行接收事件出错:" + object.toJSONString(), e);
            }
        }
        if (reply != null) {
            return WeChatInterfaceUtils.modelToXml(reply, true);
        }
        return null;
    }

    /**
     * 接收消息：https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140453
     * 回复消息：https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140543
     *
     * @param config
     * @return
     */
    private String receiveTextMessage(EventListenerConfig config) {
        ModelObject object = config.getObject();
        String msgType = object.getString("MsgType");
        if (events != null) {
            String key = TYPE + "_" + msgType.toUpperCase();
            config.setKey(key);
            if (events != null) {
                if (events.size() > 1) {
                    String s = "";
                    for (WeChatListener listener : events) {
                        s += " " + listener.getClass().getSimpleName();
                    }
                    logger.warn("发现多个微信消息回复实现(只会使用一个回复):" + s);
                }
                String reply = evalEventNotify(config, object, events);
                logger.info("微信消息回复内容:" + reply);
                return reply;
            }
        }
        return "success";
    }


    ////////////////////////////////////////// 模板消息接口 ////////////////////////////////////////

    /**
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1433751277
     * 参数	是否必填	说明
     * touser	是	接收者openid
     * template_id	是	模板ID
     * url	否	模板跳转链接
     * miniprogram	否	跳小程序所需数据，不需跳小程序可不用传该数据
     * appid	是	所需跳转到的小程序appid（该小程序appid必须与发模板消息的公众号是绑定关联关系，暂不支持小游戏）
     * pagepath	否	所需跳转到小程序的具体页面路径，支持带参数,（示例index?foo=bar），暂不支持小游戏
     * data	是	模板数据
     * color	否	模板内容字体颜色，不填默认为黑色
     * <p>
     * 例子:
     * {
     * "touser":"OPENID",
     * "template_id":"ngqIpbwh8bUfcSsECmogfXcV14J0tQlEpBO27izEYtY",
     * "url":"http://weixin.qq.com/download",
     * "miniprogram":{
     * "appid":"xiaochengxuappid12345",
     * "pagepath":"index?foo=bar"
     * },
     * "data":{
     * "first": {
     * "value":"恭喜你购买成功！",
     * "color":"#173177"
     * },
     * "keyword1":{
     * "value":"巧克力",
     * "color":"#173177"
     * },
     * "keyword2": {
     * "value":"39.8元",
     * "color":"#173177"
     * },
     * "keyword3": {
     * "value":"2014年9月22日",
     * "color":"#173177"
     * },
     * "remark":{
     * "value":"欢迎再次购买！",
     * "color":"#173177"
     * }
     * }
     * }
     */
    public void sendTemplateMesssage(ModelObject object) {
        if (object != null) {
            String openid = object.getString("touser");
//            ModelObject user = GlobalService.userService.getUserByOpenId(openid);
//            if (user != null) {
//                String token = user.getString(TableUser.accessToken);
//                String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + token;
//                String resp = HttpUtils.post(url, object.toJSONString());
//                ModelObject respJson = ModelObject.parseObject(resp);
//                if (WeChatInterfaceUtils.isResponseSuccess(respJson)) {
//
//                }
//            }
        }
    }
    //////////////////////////////////////////     END     ////////////////////////////////////////

    /**
     * 根据标签进行群发【订阅号与服务号认证后均可用】
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1481187827_i0l21
     */
    public void sendMassMessage(AccountConfig config, ModelObject post) throws IOException, ModuleException {
        String accessToken = this.weChatService.getShareAccountToken(config);
        String resp = HttpUtils.postJson("https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=" + accessToken, post.toJSONString());
        ModelObject json = ModelObject.parseObject(resp);
        if (WeChatInterfaceUtils.isResponseSuccess(json, config)) {

        }
    }
}
