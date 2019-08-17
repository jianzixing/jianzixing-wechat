package com.jianzixing.webapp.service.wechat;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.wechat.model.AccountConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.json.ModelArray;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.HttpUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class WeChatMenuConnector {
    private static final Log logger = LogFactory.getLog(WeChatMenuConnector.class);
    private static final String TYPE = "MENU";
    private final List<WeChatListener> events;
    private final WeChatService weChatService;

    public WeChatMenuConnector(WeChatService weChatService, List<WeChatListener> events) {
        this.weChatService = weChatService;
        this.events = events;
    }

    /**
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421141013
     *
     * @param config
     * @param array
     * @return
     */
    public boolean saveMenus(AccountConfig config, ModelArray array) throws IOException, ModuleException {
        if (config != null) {
            String accessToken = GlobalService.weChatService.getShareAccountToken(config);
            if (accessToken != null) {
                ModelObject object = new ModelObject();
                object.put("button", array);
                String text = HttpUtils.postJson(WeChatDomains.getApiUrl("/cgi-bin/menu/create") +
                        "?access_token=" + accessToken, object.toJSONString());
                ModelObject json = ModelObject.parseObject(text);
                if (WeChatInterfaceUtils.isResponseSuccess(json, config)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421141014
     *
     * @param publicAccountCode
     * @return
     */
    public ModelObject getMenus(String publicAccountCode) throws IOException, ModuleException {
        if (publicAccountCode != null) {
            String accessToken = weChatService.getStartDevelopConnector().getAccessToken(publicAccountCode);
            if (accessToken != null) {
                String text = HttpUtils.get(WeChatDomains.getApiUrl("/cgi-bin/menu/get") +
                        "?access_token=" + accessToken);
                ModelObject json = ModelObject.parseObject(text);
                return json;
            }
        }
        return null;
    }

    /**
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421141015
     *
     * @param accountConfig
     * @return
     */
    public boolean delMenus(AccountConfig accountConfig) throws IOException, ModuleException {
        if (accountConfig != null) {
            String accessToken = GlobalService.weChatService.getShareAccountToken(accountConfig);
            if (accessToken != null) {
                String text = HttpUtils.get(WeChatDomains.getApiUrl("/cgi-bin/menu/delete") +
                        "?access_token=" + accessToken);
                ModelObject json = ModelObject.parseObject(text);
                if (WeChatInterfaceUtils.isResponseSuccess(json, accountConfig)) {
                    return true;
                }
            }
        }
        return false;
    }
}
