package com.jianzixing.webapp.service.wechatsm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.wechat.WeChatMassUtils;
import com.jianzixing.webapp.service.wechat.model.AccountConfig;
import com.jianzixing.webapp.service.wechat.WeChatOpenType;
import com.jianzixing.webapp.tables.wechat.TableWeChatMass;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class DefaultWeChatMassService implements WeChatMassService {
    private static final Log logger = LogFactory.getLog(DefaultWeChatMassService.class);

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addMass(ModelObject object) throws ModelCheckerException, ModuleException {
        object.setObjectClass(TableWeChatMass.class);
        object.checkAndThrowable();

        int openType = object.getIntValue(TableWeChatMass.openType);
        int accountId = object.getIntValue(TableWeChatMass.accountId);
        String mediaId = object.getString(TableWeChatMass.mediaId);
        ModelObject acc = null;
        if (openType == WeChatOpenType.PUBLIC.getCode()) {
            acc = GlobalService.weChatPublicService.getAccount(accountId);
        }
        if (openType == WeChatOpenType.OPEN_PUBLIC.getCode()) {
            acc = GlobalService.weChatOpenService.getOpenAccountById(accountId);
        }
        if (acc == null) {
            throw new ModuleException(StockCode.getStockCode("miss_account"), "没有找到公众号信息");
        }

        object.put(TableWeChatMass.mediaId, mediaId);

        object.put(TableWeChatMass.createTime, new Date());
        sessionTemplate.save(object);
    }

    @Override
    public void delMass(int id) {
        sessionTemplate.delete(TableWeChatMass.class, id);
    }

    @Override
    public void updateMass(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableWeChatMass.class);
        object.remove(TableWeChatMass.openType);
        object.remove(TableWeChatMass.accountId);
        object.remove(TableWeChatMass.createTime);

        String mediaId = object.getString(TableWeChatMass.mediaId);
        object.put(TableWeChatMass.mediaId, mediaId);

        object.checkUpdateThrowable();
        sessionTemplate.update(object);
    }

    @Override
    public Paging<ModelObject> getMasses(Query query, int openType, int accountId, int start, int limit) throws ModuleException {
        if (query == null) {
            query = Criteria.query(TableWeChatMass.class);
        }
        if (openType == 0 || accountId == 0) {
            throw new ModuleException(StockCode.ARG_NULL, "所属公众号参数不完整");
        }
        query.eq(TableWeChatMass.openType, openType);
        query.eq(TableWeChatMass.accountId, accountId);
        query.setTableClass(TableWeChatMass.class);
        query.order(TableWeChatMass.createTime, false);
        return sessionTemplate.paging(query);
    }

    @Override
    public void enableMass(int id) {
        ModelObject update = ModelObject.builder(TableWeChatMass.class);
        update.put(TableWeChatMass.id, id);
        update.put(TableWeChatMass.enable, 1);
        sessionTemplate.update(update);
    }

    @Override
    public void disableMass(int id) {
        ModelObject update = ModelObject.builder(TableWeChatMass.class);
        update.put(TableWeChatMass.id, id);
        update.put(TableWeChatMass.enable, 0);
        sessionTemplate.update(update);
    }

    @Override
    public void triggerTimerMass() throws IOException, ModuleException {
        long time = System.currentTimeMillis();
        long end = time - 24 * 60 * 60 * 1000l;
        List<ModelObject> masses = sessionTemplate.list(Criteria.query(TableWeChatMass.class)
                .between(TableWeChatMass.triggerTime, new Date(end), new Date(time))
                .eq(TableWeChatMass.status, 0)
                .eq(TableWeChatMass.enable, 1));

        if (masses != null) {
            for (ModelObject mass : masses) {
                int openType = mass.getIntValue(TableWeChatMass.openType);
                int accountId = mass.getIntValue(TableWeChatMass.accountId);
                int type = mass.getIntValue(TableWeChatMass.type);
                String mediaId = mass.getString(TableWeChatMass.mediaId);
                int tagid = mass.getIntValue(TableWeChatMass.tagid);
                String text = mass.getString(TableWeChatMass.text);

                logger.info("开始执行定时群发：" + mass.getIntValue(TableWeChatMass.id));

                ModelObject post = null;
                if (type == WeChatMediaType.TI.getCode()) {
                    if (tagid > 0)
                        post = WeChatMassUtils.createNews(mediaId, false, tagid, true);
                    else
                        post = WeChatMassUtils.createNews(mediaId, true, 0, true);
                }
                if (type == WeChatMediaType.TEXT.getCode()) {
                    if (tagid > 0)
                        post = WeChatMassUtils.createText(text, false, tagid);
                    else
                        post = WeChatMassUtils.createText(text, true, 0);
                }
                if (type == WeChatMediaType.IMAGE.getCode()) {
                    if (tagid > 0)
                        post = WeChatMassUtils.createImage(mediaId, false, tagid);
                    else
                        post = WeChatMassUtils.createImage(mediaId, true, 0);
                }
                if (type == WeChatMediaType.VOICE.getCode()) {
                    if (tagid > 0)
                        post = WeChatMassUtils.createVoice(mediaId, false, tagid);
                    else
                        post = WeChatMassUtils.createVoice(mediaId, true, 0);
                }
                if (type == WeChatMediaType.VIDEO.getCode()) {
                    if (tagid > 0)
                        post = WeChatMassUtils.createMPVideo(mediaId, false, tagid);
                    else
                        post = WeChatMassUtils.createMPVideo(mediaId, true, 0);
                }

                if (post != null) {
                    AccountConfig config = WeChatServiceManagerUtils.createAccountConfig(openType, accountId);
                    String msg = null;
                    try {
                        GlobalService.weChatService.getMessageConnector().sendMassMessage(config, post);
                    } catch (Exception e) {
                        msg = e.getMessage();
                        throw e;
                    } finally {
                        ModelObject update = ModelObject.builder(TableWeChatMass.class);
                        update.put(TableWeChatMass.id, mass.getIntValue(TableWeChatMass.id));
                        update.put(TableWeChatMass.status, 1);
                        if (msg != null) {
                            update.put(TableWeChatMass.status, 2);
                            update.put(TableWeChatMass.error, msg);
                        }
                        sessionTemplate.update(update);
                    }
                }
            }
        }
    }
}
