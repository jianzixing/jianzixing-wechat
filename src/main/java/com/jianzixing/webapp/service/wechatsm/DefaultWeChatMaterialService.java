package com.jianzixing.webapp.service.wechatsm;

import com.jianzixing.webapp.tables.wechat.TableWeChatImageText;
import com.jianzixing.webapp.tables.wechat.TableWeChatImageTextSub;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelArray;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.exception.TransactionException;
import org.mimosaframework.orm.transaction.Transaction;
import org.mimosaframework.orm.transaction.TransactionCallback;
import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.wechat.WeChatMaterialConnector;
import com.jianzixing.webapp.service.wechat.model.AccountConfig;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DefaultWeChatMaterialService implements WeChatMaterialService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public Paging getTemporaryMaterials(ModelObject search, String type, int accountId, int openType, int start, int limit) {
        return null;
    }

    @Override
    public Paging getForeverMaterials(ModelObject search, String type, int accountId, int openType, int start, int limit) throws IOException, ModuleException {
        WeChatMaterialConnector connector = GlobalService.weChatService.getMaterialConnector();
        ModelObject object = connector.getMaterials(AccountConfig.builder(openType, accountId), type, start, limit);

        Paging paging = new Paging();
        List<ModelObject> objects = object.getArray("item");
        if (objects != null) {
            for (ModelObject o : objects) {
                o.put("type", type);
            }
            paging.setObjects(objects);
        }
        paging.setCount(object.getIntValue("total_count"));
        return paging;
    }

    @Override
    public void deleteForeverMaterials(int accountId, int openType, List<String> mediaIds) throws IOException, ModuleException {
        if (mediaIds != null && mediaIds.size() > 0) {
            WeChatMaterialConnector connector = GlobalService.weChatService.getMaterialConnector();
            for (String mediaId : mediaIds) {
                connector.deleteMaterial(AccountConfig.builder(openType, accountId), mediaId);
            }
        }
    }

    @Override
    public void addImageText(String host, ModelObject object) throws ModelCheckerException, TransactionException {
        object.setObjectClass(TableWeChatImageText.class);
        object.put(TableWeChatImageText.createTime, new Date());
        object.checkAndThrowable();

        List<ModelObject> subs = object.getArray("subs");

        sessionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean invoke(Transaction transaction) throws Exception {
                sessionTemplate.save(object);

                ModelObject wc = new ModelObject();
                List<ModelObject> articles = new ArrayList<>();
                int i = 0;
                for (ModelObject sub : subs) {
                    sub.setObjectClass(TableWeChatImageTextSub.class);
                    sub.put(TableWeChatImageTextSub.index, i);
                    sub.put(TableWeChatImageTextSub.imageTextId, object.getIntValue(TableWeChatImageText.id));
                    sub.put(TableWeChatImageTextSub.host, host);

                    sub.put(TableWeChatImageTextSub.resUrl, host + sub.getString(TableWeChatImageTextSub.resUrl) + "?id=" +
                            object.getIntValue(TableWeChatImageText.id) + "&i=" + i);

                    ModelObject articleItem = new ModelObject();
                    articleItem.put("title", sub.getString(TableWeChatImageTextSub.title));
                    articleItem.put("thumb_media_id", sub.getString(TableWeChatImageTextSub.thumbMediaId));
                    articleItem.put("author", sub.getString(TableWeChatImageTextSub.author));
                    articleItem.put("digest", sub.getString(TableWeChatImageTextSub.desc));
                    articleItem.put("content", sub.getString(TableWeChatImageTextSub.content));
                    articleItem.put("content_source_url", sub.getString(TableWeChatImageTextSub.resUrl));
                    articles.add(articleItem);

                    i++;
                }
                wc.put("articles", articles);

                int openType = object.getIntValue(TableWeChatImageText.openType);
                int accountId = object.getIntValue(TableWeChatImageText.accountId);
                AccountConfig config = AccountConfig.builder(openType, accountId);
                WeChatMaterialConnector materialService = GlobalService.weChatService.getMaterialConnector();
                String mediaId = materialService.addTextImageMaterial(config, wc);
                ModelObject imageTextObj = materialService.getMaterial(config, mediaId);

                List<ModelObject> newsItems = imageTextObj.getArray("news_item");
                object.put(TableWeChatImageText.mediaId, mediaId);
                if (newsItems != null && newsItems.size() > 0) {
                    for (ModelObject newsItem : newsItems) {
                        String url = newsItem.getString("url");
                        for (ModelObject sub : subs) {
                            if (newsItem.getString("content_source_url").equals(sub.getString(TableWeChatImageTextSub.resUrl))) {
                                sub.put(TableWeChatImageTextSub.url, url);
                            }
                        }
                    }
                }

                sessionTemplate.save(subs);
                sessionTemplate.update(object);
                return true;
            }
        });
    }

    @Override
    public Paging getImageTexts(int openType, int accountId, int start, int limit) {
        return sessionTemplate.paging(Criteria.query(TableWeChatImageText.class)
                .subjoin(TableWeChatImageTextSub.class).eq(TableWeChatImageTextSub.imageTextId, TableWeChatImageText.id).query()
                .eq(TableWeChatImageText.openType, openType)
                .eq(TableWeChatImageText.accountId, accountId)
                .limit(start, limit));
    }

    @Override
    public void delImageText(int id) throws IOException, ModuleException {
        try {
            ModelObject object = sessionTemplate.get(Criteria.query(TableWeChatImageText.class).eq(TableWeChatImageText.id, id));
            List<ModelObject> subs = sessionTemplate.list(Criteria.query(TableWeChatImageTextSub.class).eq(TableWeChatImageTextSub.imageTextId, id));
            int openType = object.getIntValue(TableWeChatImageText.openType);
            int accountId = object.getIntValue(TableWeChatImageText.accountId);
            String mediaId = object.getString(TableWeChatImageText.mediaId);
            AccountConfig config = AccountConfig.builder(openType, accountId);
            WeChatMaterialConnector materialService = GlobalService.weChatService.getMaterialConnector();
            materialService.deleteMaterial(config, mediaId);
            if (subs != null) {
                for (ModelObject sub : subs) {
                    mediaId = sub.getString(TableWeChatImageTextSub.thumbMediaId);
                    materialService.deleteMaterial(config, mediaId);
                }
            }
        } finally {
            sessionTemplate.delete(Criteria.delete(TableWeChatImageText.class)
                    .eq(TableWeChatImageText.id, id));
            sessionTemplate.delete(Criteria.delete(TableWeChatImageTextSub.class)
                    .eq(TableWeChatImageTextSub.imageTextId, id));
        }
    }

    @Override
    public void updateImageText(String host, ModelObject object) throws ModelCheckerException, TransactionException {
        object.setObjectClass(TableWeChatImageText.class);
        object.put(TableWeChatImageText.createTime, new Date());
        object.checkUpdateThrowable();

        ModelObject old = sessionTemplate.get(TableWeChatImageText.class, object.getIntValue(TableWeChatImageText.id));
        String mediaId = old.getString(TableWeChatImageText.mediaId);
        object.put(TableWeChatImageText.mediaId, mediaId);

        int id = old.getIntValue(TableWeChatImageText.id);
        // List<ModelObject> oldSubs = sessionTemplate.list(Criteria.query(TableWeChatImageTextSub.class)
        //         .eq(TableWeChatImageTextSub.imageTextId, id));
        List<ModelObject> subs = object.getArray("subs");

        sessionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean invoke(Transaction transaction) throws Exception {
                int i = 0;
                int openType = old.getIntValue(TableWeChatImageText.openType);
                int accountId = old.getIntValue(TableWeChatImageText.accountId);

                for (ModelObject sub : subs) {
                    sub.setObjectClass(TableWeChatImageTextSub.class);
                    sub.put(TableWeChatImageTextSub.index, i);
                    sub.put(TableWeChatImageTextSub.imageTextId, id);
                    sub.put(TableWeChatImageTextSub.host, host);

                    String resUrl = sub.getString(TableWeChatImageTextSub.resUrl);
                    if (resUrl.indexOf("?id=" + old.getIntValue(TableWeChatImageText.id) + "&i=" + i) < 0) {
                        sub.put(TableWeChatImageTextSub.resUrl, host + sub.getString(TableWeChatImageTextSub.resUrl) + "?id=" +
                                old.getIntValue(TableWeChatImageText.id) + "&i=" + i);
                    }


                    ModelObject wc = new ModelObject();
                    wc.put("media_id", mediaId);
                    // 要更新的文章在图文消息中的位置（多图文消息时，此字段才有意义），第一篇为0
                    // 注意多图文时这个可以指定修改index位置的图文信息，不是更新图文位置
                    wc.put("index", i);
                    ModelObject articleItem = new ModelObject();
                    articleItem.put("title", sub.getString(TableWeChatImageTextSub.title));
                    articleItem.put("thumb_media_id", sub.getString(TableWeChatImageTextSub.thumbMediaId));
                    articleItem.put("author", sub.getString(TableWeChatImageTextSub.author));
                    articleItem.put("digest", sub.getString(TableWeChatImageTextSub.desc));
                    articleItem.put("content", sub.getString(TableWeChatImageTextSub.content));
                    articleItem.put("content_source_url", sub.getString(TableWeChatImageTextSub.resUrl));
                    wc.put("articles", articleItem);

                    i++;

                    AccountConfig config = AccountConfig.builder(openType, accountId);
                    WeChatMaterialConnector materialService = GlobalService.weChatService.getMaterialConnector();
                    materialService.updateTextImageMaterial(config, wc);
                }

                sessionTemplate.delete(Criteria.delete(TableWeChatImageTextSub.class).eq(TableWeChatImageTextSub.imageTextId, id));
                sessionTemplate.save(subs);
                return true;
            }
        });
    }

    @Override
    public ModelObject getImageText(int id) {
        return sessionTemplate.get(Criteria.query(TableWeChatImageText.class)
                .subjoin(TableWeChatImageTextSub.class).eq(TableWeChatImageTextSub.imageTextId, TableWeChatImageText.id).query()
                .eq(TableWeChatImageText.id, id));
    }

    @Override
    public ModelObject getImageTextSub(String id, String index) {
        return sessionTemplate.get(Criteria.query(TableWeChatImageTextSub.class)
                .subjoin(TableWeChatImageText.class).eq(TableWeChatImageText.id, TableWeChatImageTextSub.imageTextId).single().query()
                .eq(TableWeChatImageTextSub.index, index)
                .eq(TableWeChatImageTextSub.imageTextId, id));
    }

    @Override
    public String uploadImageWeChat(int openType, int accountId, String fileName) throws IOException, ModuleException {
        ModelObject file = GlobalService.fileService.getFileByName(fileName);
        File f = GlobalService.fileService.getSystemFile(file);
        WeChatMaterialConnector materialService = GlobalService.weChatService.getMaterialConnector();
        FileInputStream inputStream = null;
        try {
            long len = f.length();
            if (len > 1 * 1024 * 1024) {
                throw new ModuleException("file_max", "上传图片只能小于1M");
            }
            inputStream = new FileInputStream(f);
            String url = materialService.addTextImageMaterialImage(AccountConfig.builder(openType, accountId), inputStream, fileName, len);
            return url;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    @Override
    public ModelObject uploadMaterialByFile(int openType, int accountId, String fileName) throws ModuleException, IOException {
        ModelObject file = GlobalService.fileService.getFileByName(fileName);
        File f = GlobalService.fileService.getSystemFile(file);
        String type = this.getMaterialStringType(fileName);

        if (StringUtils.isBlank(type)) {
            throw new ModuleException(StockCode.NOT_SUPPORT, "不支持的文件类型");
        }
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(f);
            WeChatMaterialConnector materialService = GlobalService.weChatService.getMaterialConnector();
            ModelObject response = materialService.uploadMaterial(AccountConfig.builder(openType, accountId), type, inputStream, fileName, f.length());
            return response;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private String getMaterialStringType(String fileName) {
        int type = this.getMaterialType(fileName);
        if (type == 1) return "image";
        if (type == 2) return "voice";
        if (type == 3) return "video";
        return null;
    }

    private int getMaterialType(String fileName) {
        String[] s = fileName.split("\\.");
        String suffix = s[s.length - 1].trim();
        if (suffix.equalsIgnoreCase("png")
                || suffix.equalsIgnoreCase("jpeg")
                || suffix.equalsIgnoreCase("jpg")
                || suffix.equalsIgnoreCase("gif")) {
            return 1; // 图片 image
        }
        if (suffix.equalsIgnoreCase("amr")
                || suffix.equalsIgnoreCase("mp3")) {
            return 2; // 语音 voice
        }
        if (suffix.equalsIgnoreCase("mp4")) {
            return 3; // 视频 video
        }
        return 0;
    }
}
