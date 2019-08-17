package com.jianzixing.webapp.service.wechat;

import org.mimosaframework.core.json.ModelObject;

import java.util.List;

public class WeChatMassUtils {
    private static ModelObject createBaseModel(List<String> toUser, boolean isToAll, long tagid) {
        ModelObject object = new ModelObject();
        if (toUser != null) {
            // openid 列表
            object.put("touser", toUser);
        } else {
            ModelObject filter = new ModelObject();
            filter.put("is_to_all", isToAll);
            filter.put("tag_id", tagid);
            object.put("filter", filter);
        }
        return object;
    }

    public static ModelObject createNews(String mediaId, List<String> toUser, boolean isSendIgnoreReprint) {
        return getNews(mediaId, toUser, false, 0, isSendIgnoreReprint);
    }

    public static ModelObject createNews(String mediaId, boolean isToAll, long tagid, boolean isSendIgnoreReprint) {
        return getNews(mediaId, null, isToAll, tagid, isSendIgnoreReprint);
    }

    public static ModelObject createText(String content, List<String> toUser) {
        return getText(content, toUser, false, 0);
    }

    public static ModelObject createText(String content, boolean isToAll, long tagid) {
        return getText(content, null, isToAll, tagid);
    }

    public static ModelObject createVoice(String mediaId, List<String> toUser) {
        return getVoice(mediaId, toUser, false, 0);
    }

    public static ModelObject createVoice(String mediaId, boolean isToAll, long tagid) {
        return getVoice(mediaId, null, isToAll, tagid);
    }

    public static ModelObject createImage(String mediaId, List<String> toUser) {
        return getImage(mediaId, toUser, false, 0);
    }

    public static ModelObject createImage(String mediaId, boolean isToAll, long tagid) {
        return getImage(mediaId, null, isToAll, tagid);
    }

    public static ModelObject createMPVideo(String mediaId, List<String> toUser) {
        return getMPVideo(mediaId, toUser, false, 0);
    }

    public static ModelObject createMPVideo(String mediaId, boolean isToAll, long tagid) {
        return getMPVideo(mediaId, null, isToAll, tagid);
    }

    public static ModelObject createWXCard(String cardId, List<String> toUser) {
        return getWXCard(cardId, toUser, false, 0);
    }

    public static ModelObject createWXCard(String cardId, boolean isToAll, long tagid) {
        return getWXCard(cardId, null, isToAll, tagid);
    }

    public static ModelObject getNews(String mediaId, List<String> toUser, boolean isToAll, long tagid, boolean isSendIgnoreReprint) {
        ModelObject object = createBaseModel(toUser, isToAll, tagid);
        ModelObject mpnews = new ModelObject();
        mpnews.put("media_id", mediaId);
        object.put("mpnews", mpnews);
        object.put("msgtype", "mpnews");
        // 如果微信服务器检查到当前文章是属于转载，是否还进行发送
        object.put("send_ignore_reprint", isSendIgnoreReprint ? "1" : "0");
        return object;
    }

    public static ModelObject getText(String content, List<String> toUser, boolean isToAll, long tagid) {
        ModelObject object = createBaseModel(toUser, isToAll, tagid);
        ModelObject text = new ModelObject();
        text.put("content", content);
        object.put("text", text);
        object.put("msgtype", "text");
        return object;
    }

    public static ModelObject getVoice(String mediaId, List<String> toUser, boolean isToAll, long tagid) {
        ModelObject object = createBaseModel(toUser, isToAll, tagid);
        ModelObject voice = new ModelObject();
        voice.put("media_id", mediaId);
        object.put("voice", voice);
        object.put("msgtype", "voice");
        return object;
    }

    public static ModelObject getImage(String mediaId, List<String> toUser, boolean isToAll, long tagid) {
        ModelObject object = createBaseModel(toUser, isToAll, tagid);
        ModelObject image = new ModelObject();
        image.put("media_id", mediaId);
        object.put("image", image);
        object.put("msgtype", "image");
        return object;
    }

    public static ModelObject getMPVideo(String mediaId, List<String> toUser, boolean isToAll, long tagid) {
        ModelObject object = createBaseModel(toUser, isToAll, tagid);
        ModelObject mpvideo = new ModelObject();
        mpvideo.put("media_id", mediaId);
        object.put("mpvideo", mpvideo);
        object.put("msgtype", "mpvideo");
        return object;
    }

    public static ModelObject getWXCard(String cardId, List<String> toUser, boolean isToAll, long tagid) {
        ModelObject object = createBaseModel(toUser, isToAll, tagid);
        ModelObject wxcard = new ModelObject();
        wxcard.put("card_id", cardId);
        object.put("wxcard", wxcard);
        object.put("msgtype", "wxcard");
        return object;
    }
}
