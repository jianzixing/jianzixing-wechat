package com.jianzixing.webapp.service.wechat;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.wechat.model.AccountConfig;
import com.jianzixing.webapp.service.wechatsm.WeChatServiceManagerUtils;
import com.jianzixing.webapp.tables.wechat.TableWeChatMiniProgram;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.HttpUtils;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 微信小程序服务端接口
 * https://developers.weixin.qq.com/miniprogram/dev/api-backend/
 */
public class WeChatMiniProgramConnector {
    private static final Log logger = LogFactory.getLog(WeChatMiniProgramConnector.class);
    private final WeChatService weChatService;
    private final List<WeChatListener> events;

    public WeChatMiniProgramConnector(WeChatService weChatService, List<WeChatListener> events) {
        this.weChatService = weChatService;
        this.events = events;
    }

    public String getAccessToken(String code) throws IOException, ModuleException {
        ModelObject mp = GlobalService.weChatMiniProgramService.getMiniProgramByCode(code);
        return getAccessToken(mp);
    }

    public String getAccessToken(ModelObject mp) throws IOException, ModuleException {
        String appid = mp.getString(TableWeChatMiniProgram.appId);
        String secret = mp.getString(TableWeChatMiniProgram.appSecret);
        String accessToken = mp.getString(TableWeChatMiniProgram.accessToken);
        Date lastTokenTime = mp.getDate(TableWeChatMiniProgram.lastTokenTime);
        long expiresIn = mp.getLongValue(TableWeChatMiniProgram.expiresIn);
        if ((lastTokenTime.getTime() + expiresIn * 1000l) > System.currentTimeMillis() - 100) {
            return accessToken;
        }
        String resp = HttpUtils.get("https://api.weixin.qq.com/cgi-bin/token" +
                "?grant_type=client_credential" +
                "&appid=" + appid +
                "&secret=" + secret);
        ModelObject json = ModelObject.parseObject(resp);
        if (WeChatInterfaceUtils.isMiniProgramSuccess(json)) {
            accessToken = json.getString("access_token");
            expiresIn = json.getLongValue("expires_in");

            ModelObject update = new ModelObject();
            update.put(TableWeChatMiniProgram.id, mp.getLongValue(TableWeChatMiniProgram.id));
            update.put(TableWeChatMiniProgram.accessToken, accessToken);
            update.put(TableWeChatMiniProgram.expiresIn, expiresIn);
            update.put(TableWeChatMiniProgram.lastTokenTime, new Date());
            try {
                GlobalService.weChatMiniProgramService.updateMiniProgramInfo(update);
            } catch (ModelCheckerException e) {
                e.printStackTrace();
                throw new ModuleException(StockCode.FAILURE, "更新小程序AccessToken出错", e);
            }
            return accessToken;
        }
        return null;
    }

    /**
     * code2Session
     * https://developers.weixin.qq.com/miniprogram/dev/api-backend/code2Session.html
     *
     * @return
     */
    public ModelObject getCode2Session(AccountConfig config, String jsCode) throws IOException {
        ModelObject mp = WeChatServiceManagerUtils.getAccountModel(config);
        if (mp != null) {
            String appid = mp.getString(TableWeChatMiniProgram.appId);
            String secret = mp.getString(TableWeChatMiniProgram.appSecret);
            String resp = HttpUtils.get("https://api.weixin.qq.com/sns/jscode2session"
                    + "?appid=" + appid
                    + "&secret=" + secret
                    + "&js_code=" + jsCode
                    + "&grant_type=authorization_code");
            ModelObject json = ModelObject.parseObject(resp);
            if (WeChatInterfaceUtils.isMiniProgramSuccess(json)) {
                return json;
            }
        }
        return null;
    }

    /**
     * getPaidUnionId
     * https://developers.weixin.qq.com/miniprogram/dev/api-backend/getPaidUnionId.html
     *
     * @param config
     * @param openid
     * @return
     * @throws IOException
     * @throws ModuleException
     */
    public ModelObject getPaidUnionId(AccountConfig config, String openid) throws IOException, ModuleException {
        String accessToken = this.weChatService.getShareAccountToken(config);
        String resp = HttpUtils.get("https://api.weixin.qq.com/wxa/getpaidunionid?access_token=" + accessToken + "&openid=" + openid);
        ModelObject json = ModelObject.parseObject(resp);
        if (WeChatInterfaceUtils.isMiniProgramSuccess(json)) {
            return json;
        }
        return null;
    }
}
