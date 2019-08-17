package com.jianzixing.webapp.service.wechatsm;

import com.jianzixing.webapp.tables.wechat.TableWeChatImageTextSub;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.json.ModelArray;
import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.wechat.WeChatListener;
import com.jianzixing.webapp.service.wechat.WeChatReplyUtils;
import com.jianzixing.webapp.service.wechat.model.EventListenerConfig;
import com.jianzixing.webapp.service.wechat.WeChatOpenType;
import com.jianzixing.webapp.tables.wechat.TableWeChatAccount;
import com.jianzixing.webapp.tables.wechat.TableWeChatReply;
import com.jianzixing.webapp.tables.wechat.TableWeChatImageText;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DefaultWeChatReplyService implements WeChatReplyService, WeChatListener {
    private static final Log logger = LogFactory.getLog(DefaultWeChatReplyService.class);

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addReply(ModelObject object) throws ModelCheckerException, ModuleException {
        object.setObjectClass(TableWeChatReply.class);
        int rid = object.getIntValue(TableWeChatReply.id);
        boolean isUpdate = rid != 0 ? true : false;

        if (isUpdate) {
            object.checkUpdateThrowable();
        } else {
            object.checkAndThrowable();
        }

        int openType = object.getIntValue(TableWeChatReply.openType);
        int accountId = object.getIntValue(TableWeChatReply.accountId);
        ModelObject acc = WeChatServiceManagerUtils.getAccountModel(accountId, openType);
        if (acc == null) {
            throw new ModuleException(StockCode.ARG_NULL, "未找到公众号账号");
        }

        int type = object.getIntValue(TableWeChatReply.type);
        String value = object.getString(TableWeChatReply.value);
        String text = object.getString(TableWeChatReply.text);
        String mediaId = object.getString(TableWeChatReply.mediaId);
        if (StringUtils.isBlank(text) && StringUtils.isBlank(mediaId)) {
            throw new ModuleException(StockCode.ARG_NULL, "回复文本或者素材必须填写至少一个");
        }

        if (type == WeChatReplyType.PARAM_QRCODE.getCode() && StringUtils.isBlank(value)) {
            throw new ModuleException(StockCode.ARG_NULL, "扫描带参数二维码回复时必须填写场景号");
        }

        if (isUpdate) {
            object.put(TableWeChatReply.mediaId, mediaId);
            sessionTemplate.update(object);
        } else {
            object.put(TableWeChatReply.mediaId, mediaId);
            object.put(TableWeChatReply.createTime, new Date());
            sessionTemplate.save(object);
        }
    }

    @Override
    public void deleteReply(int id) {
        sessionTemplate.delete(Criteria.delete(TableWeChatReply.class).eq(TableWeChatReply.id, id));
    }

    @Override
    public Paging getReplys(Query query, int openType, int accountId, int start, int limit) {
        if (query == null) {
            query = Criteria.query(TableWeChatReply.class);
        } else {
            query.setTableClass(TableWeChatReply.class);
        }
        query.eq(TableWeChatReply.openType, openType);
        query.eq(TableWeChatReply.accountId, accountId);
        query.order(TableWeChatReply.id, false);
        query.limit(start, limit);
        return sessionTemplate.paging(query);
    }

    @Override
    public List<ModelObject> getTypeCount() {
        WeChatReplyType[] types = WeChatReplyType.values();
        List<ModelObject> result = new ArrayList<>();
        for (WeChatReplyType type : types) {
            long count = sessionTemplate.count(Criteria.query(TableWeChatReply.class).eq(TableWeChatReply.type, type.getCode()));
            ModelObject object = new ModelObject();
            object.put("code", type.getCode());
            object.put("msg", type.getMsg());
            object.put("count", count);
            result.add(object);
        }
        return result;
    }

    @Override
    public void updateReply(ModelObject object) throws ModelCheckerException, ModuleException {
        if (object.getIntValue(TableWeChatReply.id) == 0) {
            throw new ModuleException(StockCode.ARG_NULL, "更新回复信息id必须存在");
        }
        this.addReply(object);
    }

    @Override
    public ModelObject getReplyMaterial(String id) {
        return sessionTemplate.get(Criteria.query(TableWeChatImageText.class)
                .subjoin(TableWeChatImageTextSub.class).eq(TableWeChatImageTextSub.imageTextId, TableWeChatImageText.id).query()
                .eq(TableWeChatImageText.id, id));
    }

    private ModelObject triggerReplyContent(WeChatReplyType type, ModelObject object, EventListenerConfig config) {
        WeChatOpenType openType = config.getType();
        if (type == WeChatReplyType.FULL_KEYWORD || type == WeChatReplyType.HALF_KEYWORD) {
            String content = object.getString("Content");
            if (StringUtils.isNotBlank(content)) {
                content = content.trim();
                List<ModelObject> replys = null;
                if (type == WeChatReplyType.FULL_KEYWORD) {
                    ModelObject acc = this.getOpenModel(config);
                    replys = sessionTemplate.list(Criteria.query(TableWeChatReply.class)
                            .eq(TableWeChatReply.type, WeChatReplyType.FULL_KEYWORD.getCode())
                            .eq(TableWeChatReply.enable, 1)
                            .eq(TableWeChatReply.openType, openType.getCode())
                            .eq(TableWeChatReply.accountId, acc.getIntValue(TableWeChatAccount.id))
                            .eq(TableWeChatReply.value, content));
                }
                if (type == WeChatReplyType.HALF_KEYWORD) {
                    ModelObject acc = this.getOpenModel(config);
                    replys = sessionTemplate.list(Criteria.query(TableWeChatReply.class)
                            .eq(TableWeChatReply.type, WeChatReplyType.HALF_KEYWORD.getCode())
                            .eq(TableWeChatReply.enable, 1)
                            .eq(TableWeChatReply.openType, openType.getCode())
                            .eq(TableWeChatReply.accountId, acc.getIntValue(TableWeChatAccount.id))
                            .like(TableWeChatReply.value, "%" + content + "%"));
                }
                return this.replyPackModel(config, replys);
            }
        }

        if (type == WeChatReplyType.CONCERN) {
            ModelObject acc = this.getOpenModel(config);
            List<ModelObject> replys = sessionTemplate.list(Criteria.query(TableWeChatReply.class)
                    .eq(TableWeChatReply.type, WeChatReplyType.CONCERN.getCode())
                    .eq(TableWeChatReply.enable, 1)
                    .eq(TableWeChatReply.openType, openType.getCode())
                    .eq(TableWeChatReply.accountId, acc.getIntValue(TableWeChatAccount.id)));
            return this.replyPackModel(config, replys);
        }

        if (type == WeChatReplyType.CANCEL_CONCERN) {
            ModelObject acc = this.getOpenModel(config);
            List<ModelObject> replys = sessionTemplate.list(Criteria.query(TableWeChatReply.class)
                    .eq(TableWeChatReply.type, WeChatReplyType.CANCEL_CONCERN.getCode())
                    .eq(TableWeChatReply.enable, 1)
                    .eq(TableWeChatReply.openType, openType.getCode())
                    .eq(TableWeChatReply.accountId, acc.getIntValue(TableWeChatAccount.id)));
            return this.replyPackModel(config, replys);
        }

        if (type == WeChatReplyType.PARAM_QRCODE) {
            String eventKey = object.getString("EventKey");
            if (eventKey.startsWith("qrscene_")) {
                eventKey = eventKey.replaceFirst("qrscene_", "");
            }
            String ticket = object.getString("Ticket");
            ModelObject acc = this.getOpenModel(config);
            List<ModelObject> replys = sessionTemplate.list(Criteria.query(TableWeChatReply.class)
                    .eq(TableWeChatReply.type, WeChatReplyType.PARAM_QRCODE.getCode())
                    .eq(TableWeChatReply.enable, 1)
                    .eq(TableWeChatReply.openType, openType.getCode())
                    .eq(TableWeChatReply.accountId, acc.getIntValue(TableWeChatAccount.id))
                    .eq(TableWeChatReply.value, eventKey));
            return this.replyPackModel(config, replys);
        }
        if (type == WeChatReplyType.CUSTOM_MENU) {
            String eventKey = object.getString("EventKey");
            ModelObject acc = this.getOpenModel(config);
            List<ModelObject> replys = sessionTemplate.list(Criteria.query(TableWeChatReply.class)
                    .eq(TableWeChatReply.type, WeChatReplyType.CUSTOM_MENU.getCode())
                    .eq(TableWeChatReply.enable, 1)
                    .eq(TableWeChatReply.openType, openType.getCode())
                    .eq(TableWeChatReply.accountId, acc.getIntValue(TableWeChatAccount.id))
                    .eq(TableWeChatReply.value, eventKey));
            return this.replyPackModel(config, replys);
        }
        return null;
    }

    private ModelObject replyPackModel(EventListenerConfig config, List<ModelObject> replys) {
        if (replys != null && replys.size() > 0) {
            ModelObject request = config.getObject();
            String fromUserName = request.getString("ToUserName");
            String toUserName = request.getString("FromUserName");

            ModelObject reply = replys.get(0);
            int type = reply.getIntValue(TableWeChatReply.replyType);
            if (type == 1) { // 图文消息
                String mediaId = reply.getString(TableWeChatReply.mediaId);

                ModelObject imageText = this.getReplyMaterial(mediaId);
                if (imageText != null) {
                    List<ModelObject> arts = new ArrayList<>();
                    List<ModelObject> materials = imageText.getArray(TableWeChatImageTextSub.class);
                    for (ModelObject m : materials) {
                        ModelObject art = new ModelObject();
                        art.put("Title", m.getString(TableWeChatImageTextSub.title));
                        art.put("Description", m.getString(TableWeChatImageTextSub.desc));
                        art.put("PicUrl", m.getString(TableWeChatImageTextSub.coverUrl));
                        art.put("Url", m.getString(TableWeChatImageTextSub.url));
                        arts.add(art);
                    }
                    return WeChatReplyUtils.getReplyArticles(toUserName, fromUserName, arts);
                }
            }
            if (type == 2) { // 文字
                String text = reply.getString(TableWeChatReply.text);
                return WeChatReplyUtils.getReplyText(toUserName, fromUserName, text);
            }
            if (type == 3) { // 图片
                String mediaId = reply.getString(TableWeChatReply.mediaId);
                return WeChatReplyUtils.getReplyImage(toUserName, fromUserName, mediaId);
            }
            if (type == 4) { // 语音
                String mediaId = reply.getString(TableWeChatReply.mediaId);
                return WeChatReplyUtils.getReplyVoice(toUserName, fromUserName, mediaId);
            }
            if (type == 5) { // 视频
                String mediaId = reply.getString(TableWeChatReply.mediaId);
                return WeChatReplyUtils.getReplyVideo(toUserName, fromUserName, mediaId, "", "");
            }
            if (type == 6) { // 消息模板，需要主动发送

            }
        }
        return null;
    }

    private List<String> convertMediaIdIfArray(String mediaId) {
        List<String> mediaIds = new ArrayList<>();
        if (mediaId.startsWith("[") && mediaId.endsWith("]")) {
            ModelArray array = ModelArray.parseArray(mediaId);
            for (int i = 0; i < array.size(); i++) {
                mediaIds.add(String.valueOf(array.get(i)));
            }
            return mediaIds;
        } else {
            mediaIds.add(mediaId);
            return mediaIds;
        }
    }


    private ModelObject getOpenModel(EventListenerConfig config) {
        WeChatOpenType type = config.getType();
        String code = config.getCode();
        String appid = config.getAppid();
        return WeChatServiceManagerUtils.getAccountModel(code, appid, type.getCode());
    }

    @Override
    public ModelObject event(EventListenerConfig config) {
        String key = config.getKey();
        ModelObject object = config.getObject();
        if (key.equals("MSG_EVENT_SUBSCRIBE")) {
            String eventKey = object.getString("EventKey");
            if (eventKey.startsWith("qrscene_")) {
                ModelObject r = this.triggerReplyContent(WeChatReplyType.PARAM_QRCODE, object, config);
                if (r != null) {
                    return r;
                }
            }
            return this.triggerReplyContent(WeChatReplyType.CONCERN, object, config);
        }
        if (key.equals("MSG_TEXT")) {
            ModelObject r = this.triggerReplyContent(WeChatReplyType.FULL_KEYWORD, object, config);
            if (r != null) return r;
            return this.triggerReplyContent(WeChatReplyType.HALF_KEYWORD, object, config);
        }
        if (key.equals("MSG_EVENT_UNSUBSCRIBE")) {
            return this.triggerReplyContent(WeChatReplyType.CANCEL_CONCERN, object, config);
        }
        if (key.equals("MSG_EVENT_SCAN")) {
            return this.triggerReplyContent(WeChatReplyType.PARAM_QRCODE, object, config);
        }
        if (key.equals("MSG_EVENT_CLICK")) {
            return this.triggerReplyContent(WeChatReplyType.CUSTOM_MENU, object, config);
        }
        return null;
    }
}
