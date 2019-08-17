package com.jianzixing.webapp.service.wechat;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.wechat.model.AccountConfig;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.HttpUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WeChatMaterialConnector {
    private static final Log logger = LogFactory.getLog(WeChatMaterialConnector.class);
    private WeChatService weChatService;
    private List<WeChatListener> events;

    public WeChatMaterialConnector(WeChatService weChatService, List<WeChatListener> events) {
        this.weChatService = weChatService;
        this.events = events;
    }

    /**
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1444738726
     * 上传临时素材
     */
    public ModelObject uploadTemporaryFile(AccountConfig config, String type, InputStream inputStream, String fileName, long size) throws IOException, ModuleException {
        String accessToken = weChatService.getShareAccountToken(config);
        if (StringUtils.isNotBlank(accessToken)) {
            String url = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=" + accessToken + "&type=" + type;
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("media", inputStream);
            map.put("media_file_name", fileName);
            map.put("media_file_size", size);
            String resp = HttpUtils.upload(url, map);
            if (StringUtils.isNotBlank(resp)) {
                ModelObject object = ModelObject.parseObject(resp);
                return object;
            }
            return null;
        } else {
            throw new IllegalArgumentException("获取AccessToken失败");
        }
    }

    /**
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1444738727
     * 获取临时素材
     */
    public void getTemporaryFile(OutputStream outputStream) throws IOException {
        String url = "https://api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";
        HttpUtils.download(url, outputStream);
    }

    /**
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1444738729
     * 新增永久图文素材
     */
    public String addTextImageMaterial(AccountConfig config, ModelObject object) throws IOException, ModuleException {
        String accessToken = weChatService.getShareAccountToken(config);
        String url = "https://api.weixin.qq.com/cgi-bin/material/add_news?access_token=" + accessToken;
        String resp = HttpUtils.postJson(url, object.toJSONString());
        logger.info("新增永久图文素材:" + resp);
        ModelObject respJson = ModelObject.parseObject(resp);
        if (WeChatInterfaceUtils.isResponseSuccess(respJson, null)) {
            return respJson.getString("media_id");
        }
        return null;
    }

    /**
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1444738732
     * 修改永久图文素材
     *
     * @param config
     * @param object
     * @return
     */
    public void updateTextImageMaterial(AccountConfig config, ModelObject object) throws IOException, ModuleException {
        this.updateTextImageMaterial(config, object.toJSONString());
    }

    public void updateTextImageMaterial(AccountConfig config, String str) throws IOException, ModuleException {
        String accessToken = weChatService.getShareAccountToken(config);
        String url = "https://api.weixin.qq.com/cgi-bin/material/update_news?access_token=" + accessToken;
        String resp = HttpUtils.postJson(url, str);
        logger.info("修改永久图文素材:" + resp);
        ModelObject respJson = ModelObject.parseObject(resp);
        if (WeChatInterfaceUtils.isResponseSuccess(respJson, null)) {
        }
    }

    /**
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1444738729
     * 上传图文消息内的图片获取URL
     *
     * @return
     */
    public String addTextImageMaterialImage(AccountConfig config, InputStream inputStream, String fileName, long size) throws IOException, ModuleException {
        String accessToken = weChatService.getShareAccountToken(config);
        String url = "https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=" + accessToken;

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("media", inputStream);
        map.put("media_file_name", fileName);
        map.put("media_file_size", size);
        String resp = HttpUtils.upload(url, map);
        ModelObject respJson = ModelObject.parseObject(resp);
        if (WeChatInterfaceUtils.isResponseSuccess(respJson, null)) {
            return respJson.getString("url");
        }
        return null;
    }

    /**
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1444738729
     * 新增其他类型永久素材
     *
     * @return
     */
    public ModelObject uploadMaterial(AccountConfig config, String type, InputStream inputStream, String fileName, long size) throws IOException, ModuleException {
        String accessToken = weChatService.getShareAccountToken(config);
        String url = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=" + accessToken + "&type=" + type;
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("media", inputStream);
        map.put("media_file_name", fileName);
        map.put("media_file_size", size);
        String resp = HttpUtils.upload(url, map);
        ModelObject respJson = ModelObject.parseObject(resp);
        if (WeChatInterfaceUtils.isResponseSuccess(respJson, null)) {
            return respJson;
        }
        return null;
    }

    /**
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1444738733
     * 获取素材总数
     *
     * @return
     */
    public ModelObject getMaterialCount(AccountConfig config) throws IOException, ModuleException {
        String accessToken = weChatService.getShareAccountToken(config);
        String url = "https://api.weixin.qq.com/cgi-bin/material/get_materialcount?access_token=" + accessToken;
        String resp = HttpUtils.get(url);
        ModelObject respJson = ModelObject.parseObject(resp);
        if (WeChatInterfaceUtils.isResponseSuccess(respJson, null)) {
            return respJson;
        }
        return respJson;
    }

    /**
     * {
     * "type":TYPE,
     * "offset":OFFSET,
     * "count":COUNT
     * }
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1444738734
     * 获取素材列表
     *
     * @return
     */
    public ModelObject getMaterials(AccountConfig config, String type, int offset, int count) throws IOException, ModuleException {
        String accessToken = weChatService.getShareAccountToken(config);
        if (StringUtils.isNotBlank(accessToken)) {
            String url = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=" + accessToken;
            ModelObject object = new ModelObject();
            object.put("type", type);
            object.put("offset", offset);
            object.put("count", count);
            String resp = HttpUtils.postJson(url, object.toJSONString());
            ModelObject respJson = ModelObject.parseObject(resp);
            if (WeChatInterfaceUtils.isResponseSuccess(respJson, config)) {
                return respJson;
            }
            return respJson;
        } else {
            throw new IllegalArgumentException("获取AccessToken失败");
        }
    }

    /**
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1444738731
     *
     * @param config
     * @param mediaId
     * @throws IOException
     */
    public void deleteMaterial(AccountConfig config, String mediaId) throws IOException, ModuleException {
        String accessToken = weChatService.getShareAccountToken(config);
        if (StringUtils.isNotBlank(accessToken)) {
            String url = "https://api.weixin.qq.com/cgi-bin/material/del_material?access_token=" + accessToken;
            ModelObject object = new ModelObject();
            object.put("media_id", mediaId);
            String resp = HttpUtils.postJson(url, object.toJSONString());
            ModelObject respJson = ModelObject.parseObject(resp);
            WeChatInterfaceUtils.isResponseSuccess(respJson, config);
        } else {
            throw new IllegalArgumentException("获取AccessToken失败");
        }
    }

    /**
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1444738730
     *
     * @param config
     * @param mediaId
     */
    public ModelObject getMaterial(AccountConfig config, String mediaId) throws IOException, ModuleException {
        String accessToken = weChatService.getShareAccountToken(config);
        if (StringUtils.isNotBlank(accessToken)) {
            String url = "https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=" + accessToken;
            ModelObject object = new ModelObject();
            object.put("media_id", mediaId);
            String resp = HttpUtils.postJson(url, object.toJSONString());
            ModelObject respJson = ModelObject.parseObject(resp);
            WeChatInterfaceUtils.isResponseSuccess(respJson, config);

            return respJson;
        } else {
            throw new IllegalArgumentException("获取AccessToken失败");
        }
    }
}
