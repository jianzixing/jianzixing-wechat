package com.jianzixing.webapp.service.wechatsm;

import com.jianzixing.webapp.service.wechat.WeChatOpenType;
import com.jianzixing.webapp.tables.wechat.TableWeChatAccount;
import com.jianzixing.webapp.tables.wechat.TableWeChatApp;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DefaultWeChatAppService implements WeChatAppService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public ModelObject getAppByCode(String code) {
        return sessionTemplate.get(
                Criteria.query(TableWeChatApp.class)
                        .eq(TableWeChatApp.enable, 1)
                        .eq(TableWeChatApp.code, code)
        );
    }

    @Override
    public ModelObject getAppById(int accountId) {
        return sessionTemplate.get(
                Criteria.query(TableWeChatApp.class)
                        .eq(TableWeChatApp.enable, 1)
                        .eq(TableWeChatApp.id, accountId)
        );
    }

    @Override
    public void addApp(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableWeChatApp.class);
        object.checkAndThrowable();
        object.put(TableWeChatApp.createTime, new Date());
        sessionTemplate.save(object);
    }

    @Override
    public void delApp(int id) {
        sessionTemplate.delete(TableWeChatApp.class, id);
    }

    @Override
    public void updateApp(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableWeChatApp.class);
        object.checkUpdateThrowable();
        sessionTemplate.update(object);
    }

    @Override
    public Paging getApps(Query query, int start, int limit) {
        if (query == null) query = Criteria.query(TableWeChatApp.class);
        query.setTableClass(TableWeChatApp.class);
        query.limit(start, limit);
        query.order(TableWeChatApp.id, false);
        return sessionTemplate.paging(query);
    }

    @Override
    public ModelObject getDefaultAccount() {
        List<ModelObject> defaults = sessionTemplate.list(
                Criteria.query(TableWeChatApp.class)
                        .eq(TableWeChatApp.isDefault, 1));
        ModelObject defaultAccount = null;
        if (defaults != null && defaults.size() > 0) {
            if (defaults.size() > 1) {
                for (int i = 1; i < defaults.size(); i++) {
                    ModelObject other = defaults.get(i);
                    ModelObject update = new ModelObject(TableWeChatApp.class);
                    update.put(TableWeChatApp.id, other.getIntValue(TableWeChatApp.id));
                    update.put(TableWeChatApp.isDefault, 0);
                    sessionTemplate.update(update);
                }
            }
            defaultAccount = defaults.get(0);
            defaultAccount.put("openType", WeChatOpenType.APP.getCode());
        }
        return defaultAccount;
    }

    @Override
    public void setDefaultAccount(int accountId) {
        List<ModelObject> defaults = sessionTemplate.list(
                Criteria.query(TableWeChatApp.class)
                        .eq(TableWeChatApp.isDefault, 1));

        if (defaults != null && defaults.size() > 0) {
            for (int i = 0; i < defaults.size(); i++) {
                ModelObject other = defaults.get(i);
                ModelObject update = new ModelObject(TableWeChatApp.class);
                update.put(TableWeChatApp.id, other.getIntValue(TableWeChatApp.id));
                update.put(TableWeChatApp.isDefault, 0);
                sessionTemplate.update(update);
            }
        }

        ModelObject update = new ModelObject(TableWeChatApp.class);
        update.put(TableWeChatApp.id, accountId);
        update.put(TableWeChatApp.isDefault, 1);
        sessionTemplate.update(update);
    }
}
