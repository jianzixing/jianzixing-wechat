package com.jianzixing.webapp.service.wechat;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.wechat.model.AccountConfig;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelBuilder;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.HttpUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 用户管理
 * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140837
 */
public class WeChatUserConnector {
    private static final Map<Integer, ModelObject> cacheLabels = new ConcurrentHashMap<>();
    private static long lastUpdateTime = 0;
    private WeChatService weChatService;
    private List<WeChatListener> events;

    public WeChatUserConnector(WeChatService weChatService, List<WeChatListener> events) {
        this.weChatService = weChatService;
        this.events = events;
    }

    public String getLabelName(AccountConfig config, Object id) throws IOException, ModuleException {
        long currTime = System.currentTimeMillis();
        if (currTime - lastUpdateTime > 1 * 60 * 1000l) {
            List<ModelObject> objects = this.getLabels(config);
            if (objects != null) {
                for (ModelObject o : objects) {
                    cacheLabels.put(o.getIntValue("id"), o);
                }
            }
            lastUpdateTime = currTime;
        }
        ModelObject o = cacheLabels.get(id);
        if (o != null) {
            return o.getString("name");
        }
        return null;
    }

    /**
     * 创建用户标签
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140837
     * 响应：{   "tag":{ "id":134,//标签id "name":"广东"   } }
     */
    public ModelObject createLabel(AccountConfig config, String tagName) throws IOException, ModuleException {
        String accessToken = this.weChatService.getShareAccountToken(config);
        ModelObject post = ModelBuilder.create().startModel("tag").put("name", tagName).toRootObject();
        String resp = HttpUtils.postJson("https://api.weixin.qq.com/cgi-bin/tags/create?access_token=" + accessToken, post.toJSONString());
        ModelObject json = ModelObject.parseObject(resp);
        if (WeChatInterfaceUtils.isResponseSuccess(json, config)) {
            return json.getModelObject("tag");
        }
        return null;
    }

    /**
     * 获取公众号已创建的标签
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140837
     * 响应：{
     * "tags":[
     * {"id":1,"name":"每天一罐可乐星人","count":0 //此标签下粉丝数},
     * {"id":2,"name":"星标组","count":0},
     * {"id":127,"name":"广东","count":5}]
     * }
     *
     * @return
     */
    public List<ModelObject> getLabels(AccountConfig config) throws IOException, ModuleException {
        String accessToken = this.weChatService.getShareAccountToken(config);
        String resp = HttpUtils.get("https://api.weixin.qq.com/cgi-bin/tags/get?access_token=" + accessToken);
        ModelObject json = ModelObject.parseObject(resp);
        if (WeChatInterfaceUtils.isResponseSuccess(json, config)) {
            return json.getArray("tags");
        }
        return null;
    }

    /**
     * 编辑标签
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140837
     *
     * @param config
     * @param id
     * @param tagName
     */
    public void updateLabel(AccountConfig config, long id, String tagName) throws IOException, ModuleException {
        String accessToken = this.weChatService.getShareAccountToken(config);
        ModelObject post = ModelBuilder.create().startModel("tag").put("id", id).put("name", tagName).toRootObject();
        String resp = HttpUtils.postJson("https://api.weixin.qq.com/cgi-bin/tags/update?access_token=" + accessToken, post.toJSONString());
        ModelObject json = ModelObject.parseObject(resp);
        if (WeChatInterfaceUtils.isResponseSuccess(json, config)) {
        }
    }

    /**
     * 删除标签
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140837
     *
     * @param config
     * @param id
     */
    public void deleteLabel(AccountConfig config, long id) throws IOException, ModuleException {
        String accessToken = this.weChatService.getShareAccountToken(config);
        ModelObject post = ModelBuilder.create().startModel("tag").put("id", id).toRootObject();
        String resp = HttpUtils.postJson("https://api.weixin.qq.com/cgi-bin/tags/delete?access_token=" + accessToken, post.toJSONString());
        ModelObject json = ModelObject.parseObject(resp);
        if (WeChatInterfaceUtils.isResponseSuccess(json, config)) {
        }
    }

    /**
     * 获取标签下粉丝列表
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140837
     *
     * @param config
     * @param tagid
     * @param listener
     * @throws IOException
     * @throws ModuleException
     */
    public void getLabelUsers(AccountConfig config, long tagid, GetUserListener listener) throws Exception {
        String accessToken = this.weChatService.getShareAccountToken(config);
        ModelObject post = ModelBuilder.create().put("tagid", tagid).toRootObject();
        String nextOpenid = null;
        while (true) {
            post.put("next_openid", nextOpenid);
            String resp = HttpUtils.postJson("https://api.weixin.qq.com/cgi-bin/user/tag/get?access_token=" + accessToken, post.toJSONString());
            ModelObject json = ModelObject.parseObject(resp);
            if (WeChatInterfaceUtils.isResponseSuccess(json, config)) {
                int count = json.getIntValue("count");
                if (count <= 0) break;
                nextOpenid = json.getString("next_openid");
                listener.callback(config, json);
            } else {
                break;
            }
        }
    }

    /**
     * 批量为用户打标签
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140837
     */
    public void setUserlabel(AccountConfig config, List<String> openids, long tagid) throws IOException, ModuleException {
        String accessToken = this.weChatService.getShareAccountToken(config);
        ModelObject post = ModelBuilder.create().put("openid_list", openids).put("tagid", tagid).toRootObject();
        String resp = HttpUtils.postJson("https://api.weixin.qq.com/cgi-bin/tags/members/batchtagging?access_token=" + accessToken, post.toJSONString());
        ModelObject json = ModelObject.parseObject(resp);
        if (WeChatInterfaceUtils.isResponseSuccess(json, config)) {
        }
    }

    /**
     * 批量为用户取消标签
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140837
     */
    public void cancelUserLabel(AccountConfig config, List<String> openids, long tagid) throws IOException, ModuleException {
        String accessToken = this.weChatService.getShareAccountToken(config);
        ModelObject post = ModelBuilder.create().put("openid_list", openids).put("tagid", tagid).toRootObject();
        String resp = HttpUtils.postJson("https://api.weixin.qq.com/cgi-bin/tags/members/batchuntagging?access_token=" + accessToken, post.toJSONString());
        ModelObject json = ModelObject.parseObject(resp);
        if (WeChatInterfaceUtils.isResponseSuccess(json, config)) {
        }
    }

    /**
     * 获取用户身上的标签列表
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140837
     *
     * @return
     */
    public List<Integer> getUserLabels(AccountConfig config, String openid) throws IOException, ModuleException {
        String accessToken = this.weChatService.getShareAccountToken(config);
        ModelObject post = ModelBuilder.create().put("openid", openid).toRootObject();
        String resp = HttpUtils.postJson("https://api.weixin.qq.com/cgi-bin/tags/getidlist?access_token=" + accessToken, post.toJSONString());
        ModelObject json = ModelObject.parseObject(resp);
        if (WeChatInterfaceUtils.isResponseSuccess(json, config)) {
            return json.getArray("tagid_list");
        }
        return null;
    }

    /**
     * 获取用户列表
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140840
     *
     * @param config
     * @param listener
     * @throws IOException
     * @throws ModuleException
     */
    public void getAllUsers(AccountConfig config, GetUserListener listener) throws Exception {
        String accessToken = this.weChatService.getShareAccountToken(config);
        String nextOpenid = "";
        while (true) {
            String resp = HttpUtils.get("https://api.weixin.qq.com/cgi-bin/user/get?access_token=" + accessToken + "&next_openid=" + nextOpenid);
            ModelObject json = ModelObject.parseObject(resp);
            if (WeChatInterfaceUtils.isResponseSuccess(json, config)) {
                nextOpenid = json.getString("next_openid");
                int count = json.getIntValue("count");
                listener.callback(config, json);
                if (StringUtils.isBlank(nextOpenid) || count == 0) {
                    break;
                }
            } else {
                break;
            }
        }
    }

    /**
     * 获取用户基本信息（包括UnionID机制）
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140839
     * <p>
     * subscribe	用户是否订阅该公众号标识，值为0时，代表此用户没有关注该公众号，拉取不到其余信息。
     * openid	用户的标识，对当前公众号唯一
     * nickname	用户的昵称
     * sex	用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
     * city	用户所在城市
     * country	用户所在国家
     * province	用户所在省份
     * language	用户的语言，简体中文为zh_CN
     * headimgurl	用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。
     * subscribe_time	用户关注时间，为时间戳。如果用户曾多次关注，则取最后关注时间
     * unionid	只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
     * remark	公众号运营者对粉丝的备注，公众号运营者可在微信公众平台用户管理界面对粉丝添加备注
     * groupid	用户所在的分组ID（兼容旧的用户分组接口）
     * tagid_list	用户被打上的标签ID列表
     * subscribe_scene	返回用户关注的渠道来源，ADD_SCENE_SEARCH 公众号搜索，ADD_SCENE_ACCOUNT_MIGRATION 公众号迁移，ADD_SCENE_PROFILE_CARD 名片分享，ADD_SCENE_QR_CODE 扫描二维码，ADD_SCENEPROFILE LINK 图文页内名称点击，ADD_SCENE_PROFILE_ITEM 图文页右上角菜单，ADD_SCENE_PAID 支付后关注，ADD_SCENE_OTHERS 其他
     * qr_scene	二维码扫码场景（开发者自定义）
     * qr_scene_str	二维码扫码场景描述（开发者自定义）
     *
     * @param config
     * @param openid
     * @return
     */
    public ModelObject getUserInfo(AccountConfig config, String openid) throws IOException, ModuleException {
        String accessToken = this.weChatService.getShareAccountToken(config);
        String resp = HttpUtils.get("https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + accessToken + "&openid=" + openid + "&lang=zh_CN");
        ModelObject json = ModelObject.parseObject(resp);
        if (WeChatInterfaceUtils.isResponseSuccess(json, config)) {
            return json;
        }
        return null;
    }

    /**
     * 批量获取用户基本信息
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140839
     *
     * @param config
     * @param openids
     * @return
     */
    public List<ModelObject> getBatchUserInfos(AccountConfig config, List<String> openids) throws IOException, ModuleException {
        String accessToken = this.weChatService.getShareAccountToken(config);
        ModelBuilder builder = ModelBuilder.create().startArray("user_list");
        if (openids != null && openids.size() > 0) {
            for (String openid : openids) {
                builder.startModel().put("openid", openid).put("lang", "zh_CN");
            }
            String resp = HttpUtils.postJson("https://api.weixin.qq.com/cgi-bin/user/info/batchget?access_token=" + accessToken, builder.toRootObject().toJSONString());
            ModelObject json = ModelObject.parseObject(resp);
            if (WeChatInterfaceUtils.isResponseSuccess(json, config)) {
                return json.getArray("user_info_list");
            }
        }
        return null;
    }

    public void setUserRemark(AccountConfig config, String openid, String remark) throws IOException, ModuleException {
        String accessToken = this.weChatService.getShareAccountToken(config);
        ModelObject post = new ModelObject();
        post.put("openid", openid);
        post.put("remark", remark);
        String resp = HttpUtils.postJson("https://api.weixin.qq.com/cgi-bin/user/info/updateremark?access_token=" + accessToken, post.toJSONString());
        ModelObject json = ModelObject.parseObject(resp);
        if (WeChatInterfaceUtils.isResponseSuccess(json, config)) {
        }
    }
}
