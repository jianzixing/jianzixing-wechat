package com.jianzixing.webapp.service.wecharqrcode;

import com.jianzixing.webapp.service.wechat.WeChatListener;
import com.jianzixing.webapp.service.wechat.WeChatOpenType;
import com.jianzixing.webapp.service.wechat.model.EventListenerConfig;
import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.wechat.WeChatAccountConnector;
import com.jianzixing.webapp.service.wechat.model.AccountConfig;
import com.jianzixing.webapp.service.wechatsm.WeChatServiceManagerUtils;
import com.jianzixing.webapp.tables.wechat.TableWeChatQrcode;
import com.jianzixing.webapp.tables.wechat.TableWeChatQrcodeOpenid;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.RandomUtils;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class DefaultWeChatQRCodeService implements WeChatQRCodeService, WeChatListener {
    private static final Log logger = LogFactory.getLog(WeChatQRCodeService.class);

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addQRCode(ModelObject object) throws ModelCheckerException, ModuleException {
        String actionName = object.getString(TableWeChatQrcode.actionName);
        String expireSecondStr = object.getString(TableWeChatQrcode.expireSeconds);
        long expireSeconds = 0;
        if (StringUtils.isBlank(expireSecondStr) || expireSecondStr.equals("0")) {
            expireSeconds = 2592000l;
        } else {
            expireSeconds = Integer.parseInt(expireSecondStr);
        }
        object.setObjectClass(TableWeChatQrcode.class);

        int accountId = object.getIntValue(TableWeChatQrcode.accountId);
        int openType = object.getIntValue(TableWeChatQrcode.openType);
        ModelObject acc = WeChatServiceManagerUtils.getAccountModel(accountId, openType);
        if (acc == null) {
            throw new ModuleException(StockCode.ARG_NULL, "没有找到公众号信息");
        }

        String appid = WeChatServiceManagerUtils.getAppidByObject(openType, acc);
        if (StringUtils.isBlank(appid)) {
            throw new ModuleException(StockCode.ARG_NULL, "没有找到公众号appid信息");
        }
        object.put(TableWeChatQrcode.appid, appid);


        AccountConfig accountConfig = WeChatServiceManagerUtils.createAccountConfig(acc, openType);
        WeChatAccountConnector accountService = GlobalService.weChatService.getAccountConnector();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String sceneId = format.format(new Date()) + RandomUtils.randomNumber(100000, 999999);
        object.put(TableWeChatQrcode.sceneId, sceneId);
        object.put(TableWeChatQrcode.createTime, new Date());
        object.put(TableWeChatQrcode.actionName, actionName);
        object.checkAndThrowable();

        try {
            ModelObject r = accountService.createQRCode(accountConfig, actionName, sceneId, expireSeconds);
            logger.info("添加二维码接口创建返回:" + r.toJSONString());

            if (r.containsKey("ticket")) {
                object.put(TableWeChatQrcode.ticket, r.getString("ticket"));
                object.put(TableWeChatQrcode.expireSeconds, r.getLongValue("expire_seconds"));
                object.put(TableWeChatQrcode.url, r.getString("url"));
            } else {
                throw new ModuleException(StockCode.API_CALL_FAIL, "创建二维码失败,接口出错");
            }

            sessionTemplate.save(object);
        } catch (Exception e) {
            throw new ModuleException(StockCode.API_CALL_FAIL, "微信接口创建失败", e);
        }
    }

    @Override
    public void deleteQRCode(int id) {
        sessionTemplate.delete(
                Criteria.delete(TableWeChatQrcode.class)
                        .eq(TableWeChatQrcode.id, id)
        );
    }

    @Override
    public void updateQRCode(ModelObject object) {
        int id = object.getIntValue(TableWeChatQrcode.id);
        String name = object.getString("name");
        if (StringUtils.isNotBlank(name) && id > 0) {
            ModelObject update = new ModelObject(TableWeChatQrcode.class);
            update.put(TableWeChatQrcode.id, object.getIntValue(TableWeChatQrcode.id));
            update.put(TableWeChatQrcode.name, name);
            sessionTemplate.update(update);
        }
    }

    @Override
    public Paging<ModelObject> getQRCodes(Query query, int openType, int accountId, int start, int limit) {
        if (query == null) {
            query = Criteria.query(TableWeChatQrcode.class);
        } else {
            query = Criteria.query(TableWeChatQrcode.class);
        }
        query.eq(TableWeChatQrcode.openType, openType);
        query.eq(TableWeChatQrcode.accountId, accountId);
        query.limit(start, limit);
        query.order(TableWeChatQrcode.id, false);
        return sessionTemplate.paging(query);
    }

    @Override
    public synchronized void addOpenid(String sceneId, String openid, boolean isFromMark) {
        ModelObject sceneObj = sessionTemplate.get(
                Criteria.query(TableWeChatQrcode.class)
                        .eq(TableWeChatQrcode.sceneId, sceneId)
        );
        if (sceneObj != null) {
            ModelObject so = new ModelObject(TableWeChatQrcodeOpenid.class);
            so.put(TableWeChatQrcodeOpenid.qrcodeId, sceneObj.getIntValue(TableWeChatQrcode.id));
            so.put(TableWeChatQrcodeOpenid.openid, openid);
            so.put(TableWeChatQrcodeOpenid.mark, 1);
            so.put(TableWeChatQrcodeOpenid.createTime, new Date());
            sessionTemplate.saveAndUpdate(so);

            long count = sessionTemplate.count(
                    Criteria.query(TableWeChatQrcodeOpenid.class)
                            .eq(TableWeChatQrcodeOpenid.qrcodeId, sceneObj.getIntValue(TableWeChatQrcode.id))
            );

            long markCount = sessionTemplate.count(
                    Criteria.query(TableWeChatQrcodeOpenid.class)
                            .eq(TableWeChatQrcodeOpenid.qrcodeId, sceneObj.getIntValue(TableWeChatQrcode.id))
                            .eq(TableWeChatQrcodeOpenid.mark, 1)
            );

            int scanCount = sceneObj.getIntValue(TableWeChatQrcode.scanCount);
            int focusCount = sceneObj.getIntValue(TableWeChatQrcode.focusCount);

            scanCount = scanCount + 1;
            if (isFromMark) {
                focusCount = focusCount + 1;
            }

            ModelObject update = new ModelObject(TableWeChatQrcode.class);
            update.put(TableWeChatQrcode.id, sceneObj.getIntValue(TableWeChatQrcode.id));
            update.put(TableWeChatQrcode.scanCount, scanCount);
            update.put(TableWeChatQrcode.focusCount, focusCount);
            update.put(TableWeChatQrcode.userCount, count);
            update.put(TableWeChatQrcode.keepCount, markCount);
            sessionTemplate.update(update);
        } else {
            logger.info("记录二维码关注人数但没找到 scene_id : " + sceneId);
        }
    }

    @Override
    public void removeOpenid(String openid) {
        List<ModelObject> scenes = sessionTemplate.list(
                Criteria.query(TableWeChatQrcodeOpenid.class)
                        .eq(TableWeChatQrcodeOpenid.openid, openid)
        );

        if (scenes != null) {
            sessionTemplate.update(
                    Criteria.update(TableWeChatQrcodeOpenid.class)
                            .eq(TableWeChatQrcodeOpenid.openid, openid)
                            .value(TableWeChatQrcodeOpenid.mark, 0)
            );
            for (ModelObject o : scenes) {
                long count = sessionTemplate.count(
                        Criteria.query(TableWeChatQrcodeOpenid.class)
                                .eq(TableWeChatQrcodeOpenid.qrcodeId, o.getIntValue(TableWeChatQrcodeOpenid.qrcodeId))
                );

                long markCount = sessionTemplate.count(
                        Criteria.query(TableWeChatQrcodeOpenid.class)
                                .eq(TableWeChatQrcodeOpenid.qrcodeId, o.getIntValue(TableWeChatQrcodeOpenid.qrcodeId))
                                .eq(TableWeChatQrcodeOpenid.mark, 1)
                );

                ModelObject update = new ModelObject(TableWeChatQrcode.class);
                update.put(TableWeChatQrcode.id, o.getIntValue(TableWeChatQrcodeOpenid.qrcodeId));
                update.put(TableWeChatQrcode.userCount, count);
                update.put(TableWeChatQrcode.keepCount, markCount);
                sessionTemplate.update(update);
            }
        }
    }

    @Override
    public void addCount(TableWeChatQrcode field, String sceneId, int count) {
        sessionTemplate.update(
                Criteria.update(TableWeChatQrcode.class)
                        .eq(TableWeChatQrcode.sceneId, sceneId)
                        .addSelf(field, count)
        );
    }

    @Override
    public void subCount(TableWeChatQrcode field, String sceneId, int count) {
        sessionTemplate.update(
                Criteria.update(TableWeChatQrcode.class)
                        .eq(TableWeChatQrcode.sceneId, sceneId)
                        .subSelf(field, count)
        );
    }

    private void updateCounts(EventListenerConfig config, ModelObject object, String eventKey, boolean isFromMark) {
        String openid = object.getString("FromUserName");
        if (eventKey == null) {
            logger.info("执行扫码统计移除操作");
            this.removeOpenid(openid);
        } else {
            logger.info("执行扫码统计增加操作scene_id:" + eventKey);
            this.addOpenid(eventKey, openid, isFromMark);
        }
    }

    private ModelObject getOpenModel(EventListenerConfig config) {
        WeChatOpenType type = config.getType();
        String code = config.getCode();
        String appid = config.getAppid();
        return WeChatServiceManagerUtils.getAccountModel(code, appid, type.getCode());
    }

    @Override
    public ModelObject event(EventListenerConfig config) throws Exception {
        String key = config.getKey();
        ModelObject object = config.getObject();
        if (key.equals("MSG_EVENT_SUBSCRIBE_SCAN")) {
            String eventKey = object.getString("EventKey");
            if (eventKey.startsWith("qrscene_")) {
                this.updateCounts(config, object, eventKey.replaceFirst("qrscene_", ""), true);
            }
        }
        if (key.equals("MSG_EVENT_UNSUBSCRIBE")) {
            this.updateCounts(config, object, null, false);
        }
        if (key.equals("MSG_EVENT_SCAN")) {
            String eventKey = object.getString("EventKey");
            this.updateCounts(config, object, eventKey, false);
        }
        return null;
    }
}
