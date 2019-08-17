package com.jianzixing.webapp.service.wechat;

import org.mimosaframework.core.json.ModelBuilder;
import org.mimosaframework.core.json.ModelObject;

import java.util.Date;
import java.util.List;

public class WeChatReplyUtils {
    /**
     * <xml>
     * **<ToUserName><![CDATA[toUser]]></ToUserName>
     * **<FromUserName><![CDATA[fromUser]]></FromUserName>
     * **<CreateTime>12345678</CreateTime>
     * **<MsgType><![CDATA[text]]></MsgType>
     * **<Content><![CDATA[你好]]></Content>
     * </xml>
     *
     * @param toUser
     * @param fromUser
     * @param content
     * @return
     */
    public static ModelObject getReplyText(String toUser, String fromUser, String content) {
        return ModelBuilder.create().put("ToUserName", toUser)
                .put("FromUserName", fromUser)
                .put("CreateTime", (new Date()).getTime() / 1000)
                .put("MsgType", "text")
                .put("Content", content).toModelObject();
    }

    /**
     * <xml>
     * **<ToUserName><![CDATA[toUser]]></ToUserName>
     * **<FromUserName><![CDATA[fromUser]]></FromUserName>
     * **<CreateTime>12345678</CreateTime>
     * **<MsgType><![CDATA[image]]></MsgType>
     * **<Image>
     * ****<MediaId><![CDATA[media_id]]></MediaId>
     * **</Image>
     * </xml>
     *
     * @param toUser
     * @param fromUser
     * @param mediaId
     * @return
     */
    public static ModelObject getReplyImage(String toUser, String fromUser, String mediaId) {
        return ModelBuilder.create()
                .put("ToUserName", toUser)
                .put("FromUserName", fromUser)
                .put("CreateTime", (new Date()).getTime() / 1000)
                .put("MsgType", "image")
                .startModel("Image").put("MediaId", mediaId)
                .toRootObject();
    }

    /**
     * <xml>
     * **<ToUserName><![CDATA[toUser]]></ToUserName>
     * **<FromUserName><![CDATA[fromUser]]></FromUserName>
     * **<CreateTime>12345678</CreateTime>
     * **<MsgType><![CDATA[voice]]></MsgType>
     * **<Voice>
     * ****<MediaId><![CDATA[media_id]]></MediaId>
     * **</Voice>
     * </xml>
     *
     * @param toUser
     * @param fromUser
     * @param mediaId
     * @return
     */
    public static ModelObject getReplyVoice(String toUser, String fromUser, String mediaId) {
        return ModelBuilder.create()
                .put("ToUserName", toUser)
                .put("FromUserName", fromUser)
                .put("CreateTime", (new Date()).getTime() / 1000)
                .put("MsgType", "voice")
                .startModel("Voice").put("MediaId", mediaId)
                .toRootObject();
    }

    /**
     * <xml>
     * **<ToUserName><![CDATA[toUser]]></ToUserName>
     * **<FromUserName><![CDATA[fromUser]]></FromUserName>
     * **<CreateTime>12345678</CreateTime>
     * **<MsgType><![CDATA[video]]></MsgType>
     * **<Video>
     * ****<MediaId><![CDATA[media_id]]></MediaId>
     * ****<Title><![CDATA[title]]></Title>
     * ****<Description><![CDATA[description]]></Description>
     * **</Video>
     * </xml>
     *
     * @param toUser
     * @param fromUser
     * @param mediaId
     * @param title
     * @param description
     * @return
     */
    public static ModelObject getReplyVideo(String toUser, String fromUser, String mediaId, String title, String description) {
        return ModelBuilder.create()
                .put("ToUserName", toUser)
                .put("FromUserName", fromUser)
                .put("CreateTime", (new Date()).getTime() / 1000)
                .put("MsgType", "video")
                .startModel("Video")
                .put("MediaId", mediaId)
                .put("Title", title)
                .put("Description", description)
                .toRootObject();
    }

    /**
     * <xml>
     * **<ToUserName><![CDATA[toUser]]></ToUserName>
     * **<FromUserName><![CDATA[fromUser]]></FromUserName>
     * **<CreateTime>12345678</CreateTime>
     * **<MsgType><![CDATA[music]]></MsgType>
     * **<Music>
     * ****<Title><![CDATA[TITLE]]></Title>
     * ****<Description><![CDATA[DESCRIPTION]]></Description>
     * ****<MusicUrl><![CDATA[MUSIC_Url]]></MusicUrl>
     * ****<HQMusicUrl><![CDATA[HQ_MUSIC_Url]]></HQMusicUrl>
     * ****<ThumbMediaId><![CDATA[media_id]]></ThumbMediaId>
     * **</Music>
     * </xml>
     *
     * @param toUser
     * @param fromUser
     * @param mediaId
     * @param musicUrl
     * @param hqMusicUrl
     * @param title
     * @param description
     * @return
     */
    public static ModelObject getReplyMusic(String toUser,
                                            String fromUser,
                                            String mediaId,
                                            String musicUrl,
                                            String hqMusicUrl,
                                            String title,
                                            String description) {
        return ModelBuilder.create()
                .put("ToUserName", toUser)
                .put("FromUserName", fromUser)
                .put("CreateTime", (new Date()).getTime() / 1000)
                .put("MsgType", "music")
                .startModel("Music")
                .put("Title", title)
                .put("Description", description)
                .put("MusicUrl", musicUrl)
                .put("HQMusicUrl", hqMusicUrl)
                .put("ThumbMediaId", mediaId)
                .toRootObject();
    }

    /**
     * <xml>
     * **<ToUserName><![CDATA[toUser]]></ToUserName>
     * **<FromUserName><![CDATA[fromUser]]></FromUserName>
     * **<CreateTime>12345678</CreateTime>
     * **<MsgType><![CDATA[news]]></MsgType>
     * **<ArticleCount>1</ArticleCount>
     * **<Articles>
     * ****<item>
     * ******<Title><![CDATA[title1]]></Title>
     * ******<Description><![CDATA[description1]]></Description>
     * ******<PicUrl><![CDATA[picurl]]></PicUrl>
     * ******<Url><![CDATA[url]]></Url>
     * ****</item>
     * ***</Articles>
     * </xml>
     *
     * @param toUser
     * @param fromUser
     * @return
     */
    public static ModelObject getReplyArticles(String toUser,
                                               String fromUser,
                                               List<ModelObject> objects) {
        ModelBuilder builder = ModelBuilder.create()
                .put("ToUserName", toUser)
                .put("FromUserName", fromUser)
                .put("CreateTime", (new Date()).getTime() / 1000)
                .put("MsgType", "news")
                .put("ArticleCount", objects.size())
                .startArray("Articles");
        if (objects != null) {
            for (ModelObject object : objects) {
                builder.startModel().startModel("item")
                        .put("Title", object.getString("Title"))
                        .put("Description", object.getString("Description"))
                        .put("PicUrl", object.getString("PicUrl"))
                        .put("Url", object.getString("Url"));
            }
        }
        return builder.toRootObject();
    }
}
