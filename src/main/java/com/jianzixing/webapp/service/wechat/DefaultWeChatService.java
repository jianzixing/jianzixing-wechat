package com.jianzixing.webapp.service.wechat;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.wechat.model.AccountConfig;
import com.jianzixing.webapp.service.wechatsm.WeChatServiceManagerUtils;
import com.jianzixing.webapp.tables.wechat.TableWeChatOpenAccount;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.json.ModelObject;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 测试账号申请
 * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421137522
 */
@Service
public class DefaultWeChatService implements WeChatService, ApplicationContextAware {
    private static final Log logger = LogFactory.getLog(DefaultWeChatService.class);
    private static final List<WeChatListener> events = new CopyOnWriteArrayList<>();

    /**
     * "MSG_EVENT_SUBSCRIBE         // 关注 或者 扫码带参数二维码并关注
     * "MSG_EVENT_SUBSCRIBE_SCAN    // 扫码带参数二维码并关注
     * "MSG_EVENT_UNSUBSCRIBE       // 取消关注
     * "MSG_EVENT_CLICK             // 点击自定义菜单
     * "MSG_EVENT_VIEW              // 点击自定义菜单并跳转
     * "MSG_TEXT                    // 接收文本
     * "MSG_IMAGE                   // 接收图片
     * "MSG_VOICE                   // 接收声音
     * "MSG_VIDEO                   // 接收视频
     * "MSG_EVENT_SCAN              // 扫码带参数二维码
     *
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, WeChatListener> listeners = applicationContext.getBeansOfType(WeChatListener.class);
        if (listeners != null) {
            for (Map.Entry<String, WeChatListener> listener : listeners.entrySet()) {
                events.add(listener.getValue());
                logger.info("动态添加微信事件: " + listener.getValue().getClass().getSimpleName());
            }
        }
    }

    @Override
    public String getAccountToken(String code, String appid) throws IOException {
        return this.getThirdPartyConnector().getAuthorizerAccessToken(code, appid);
    }

    @Override
    public String getAccountToken(String code) throws IOException, ModuleException {
        return this.getStartDevelopConnector().getAccessToken(code);
    }

    @Override
    public String getShareAccountToken(AccountConfig accountConfig) throws IOException, ModuleException {
        if (accountConfig.getType() == WeChatOpenType.OPEN_PUBLIC || accountConfig.getType() == WeChatOpenType.OPEN_MINI_PROGRAM) {
            if (accountConfig.isAccount()) {
                ModelObject authAccount = GlobalService.weChatOpenService.getOpenAccountById(accountConfig.getAccountId());
                ModelObject componentAccount = GlobalService.weChatOpenService.getOpenById(authAccount.getIntValue(TableWeChatOpenAccount.tpId));
                return this.getThirdPartyConnector().getAuthorizerAccessToken(componentAccount, authAccount);
            } else {
                return getAccountToken(accountConfig.getCode(), accountConfig.getAppid());
            }
        } else if (accountConfig.getType() == WeChatOpenType.PUBLIC) {
            if (accountConfig.isAccount()) {
                ModelObject acc = WeChatServiceManagerUtils.getAccountModel(accountConfig.getType().getCode(), accountConfig.getAccountId());
                return this.getStartDevelopConnector().getAccessToken(acc);
            } else {
                return getAccountToken(accountConfig.getCode());
            }
        } else if (accountConfig.getType() == WeChatOpenType.MINI_PROGRAM) {
            return this.getMiniProgramConnector().getAccessToken(accountConfig.getCode());
        }
        return null;
    }

    @Override
    public void emptyAccountToken(AccountConfig accountConfig) {
        if (accountConfig.getType() == WeChatOpenType.OPEN_PUBLIC || accountConfig.getType() == WeChatOpenType.OPEN_MINI_PROGRAM) {
            if (accountConfig.isAccount()) {
                GlobalService.weChatOpenService.setEmptyAccountToken(accountConfig.getAccountId());
            }
        } else if (accountConfig.getType() == WeChatOpenType.PUBLIC) {
            if (accountConfig.isAccount()) {
                GlobalService.weChatPublicService.setEmptyAccountToken(accountConfig.getAccountId());
            } else {
                GlobalService.weChatPublicService.setEmptyAccountToken(accountConfig.getCode());
            }
        } else if (accountConfig.getType() == WeChatOpenType.MINI_PROGRAM) {
            if (accountConfig.isAccount()) {
                GlobalService.weChatMiniProgramService.setEmptyAccountToken(accountConfig.getAccountId());
            } else {
                GlobalService.weChatMiniProgramService.setEmptyAccountToken(accountConfig.getCode());
            }
        }
    }

    @Override
    public WeChatStartDevelopConnector getStartDevelopConnector() {
        return new WeChatStartDevelopConnector(this, events);
    }

    @Override
    public WeChatMessageConnector getMessageConnector() {
        return new WeChatMessageConnector(this, events);
    }

    @Override
    public WeChatMenuConnector getMenuConnector() {
        return new WeChatMenuConnector(this, events);
    }

    @Override
    public WeChatThirdPartyConnector getThirdPartyConnector() {
        return new WeChatThirdPartyConnector(this, events);
    }

    @Override
    public WeChatAccountConnector getAccountConnector() {
        return new WeChatAccountConnector(this, events);
    }

    @Override
    public WeChatMaterialConnector getMaterialConnector() {
        return new WeChatMaterialConnector(this, events);
    }

    @Override
    public WeChatWebPageConnector getWebPageConnector() {
        return new WeChatWebPageConnector();
    }

    @Override
    public WeChatUserConnector getUserConnector() {
        return new WeChatUserConnector(this, events);
    }

    @Override
    public WeChatMiniProgramConnector getMiniProgramConnector() {
        return new WeChatMiniProgramConnector(this, events);
    }

    @Override
    public WeChatWebSiteConnector getWebSiteConnector() {
        return new WeChatWebSiteConnector(this, events);
    }

    @Override
    public WeChatAppConnector getAppConnector() {
        return new WeChatAppConnector(this, events);
    }
}
