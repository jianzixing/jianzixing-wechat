package com.jianzixing.webapp.service.wechat;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.wechat.model.AccountConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.HttpUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class WeChatAccountConnector {
    private static final Log logger = LogFactory.getLog(WeChatAccountConnector.class);
    public static final String AN_QR_SCENE = "QR_SCENE";
    public static final String AN_QR_STR_SCENE = "QR_STR_SCENE";
    public static final String AN_QR_LIMIT_SCENE = "QR_LIMIT_SCENE";
    public static final String AN_QR_LIMIT_STR_SCENE = "QR_LIMIT_STR_SCENE";

    private WeChatService weChatService;

    public WeChatAccountConnector(WeChatService weChatService, List<WeChatListener> events) {
        this.weChatService = weChatService;
    }

    /**
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1443433542
     * 创建二维码ticket
     */
    public ModelObject createQRCode(AccountConfig accountConfig, String actionName, String scene, Long expireSeconds) throws IOException, ModuleException {
        String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="
                + weChatService.getShareAccountToken(accountConfig);

        ModelObject object = new ModelObject();
        object.put("action_name", actionName);
        if (expireSeconds != null && expireSeconds > 0) {
            object.put("expire_seconds", expireSeconds);
        }

        ModelObject actionInfo = new ModelObject();
        ModelObject sceneJson = new ModelObject();
        actionInfo.put("scene", sceneJson);
        object.put("action_info", actionInfo);
        if (actionName.equals(AN_QR_STR_SCENE)
                || actionName.equals(AN_QR_LIMIT_STR_SCENE)) {
            sceneJson.put("scene_str", scene);
        } else {
            sceneJson.put("scene_id", scene);
        }
        String resp = HttpUtils.postJson(url, object.toJSONString());
        logger.info("微信创建二维码结果 : " + resp);
        ModelObject json = ModelObject.parseObject(resp);

        return json;
    }

}
