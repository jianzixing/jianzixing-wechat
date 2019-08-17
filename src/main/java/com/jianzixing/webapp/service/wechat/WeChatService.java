package com.jianzixing.webapp.service.wechat;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.wechat.model.AccountConfig;

import java.io.IOException;

public interface WeChatService {
    /**
     * 通过第三方接口获得公众号的AccessToken
     *
     * @param code  第三方平台code码
     * @param appid 关注第三方平台的appid
     * @return
     */
    String getAccountToken(String code, String appid) throws IOException;

    /**
     * 获取公众号的AccessToken
     *
     * @param code
     * @return
     * @throws IOException
     */
    String getAccountToken(String code) throws IOException, ModuleException;

    /**
     * 获取公众号AccessToken
     * 如果传入第二个参数appid则表示通过第三方平台获取AccessToken
     *
     * @return
     * @throws IOException
     */
    String getShareAccountToken(AccountConfig accountConfig) throws IOException, ModuleException;

    /**
     * 假如accessToken失效则清除数据库中的token记录，
     * 下次重新获取
     *
     * @param accountConfig
     */
    void emptyAccountToken(AccountConfig accountConfig);

    WeChatStartDevelopConnector getStartDevelopConnector();

    WeChatMessageConnector getMessageConnector();

    WeChatMenuConnector getMenuConnector();

    WeChatThirdPartyConnector getThirdPartyConnector();

    WeChatAccountConnector getAccountConnector();

    WeChatMaterialConnector getMaterialConnector();

    WeChatWebPageConnector getWebPageConnector();

    WeChatUserConnector getUserConnector();

    WeChatMiniProgramConnector getMiniProgramConnector();

    WeChatWebSiteConnector getWebSiteConnector();

    WeChatAppConnector getAppConnector();
}
