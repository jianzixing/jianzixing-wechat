package com.jianzixing.webapp.service.wechatsm;

import com.jianzixing.webapp.service.wechat.WeChatOpenType;
import com.jianzixing.webapp.tables.wechat.TableWeChatWebSite;
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
public class DefaultWeChatWebSiteService implements WeChatWebSiteService {
    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public ModelObject getWebSiteByCode(String code) {
        return sessionTemplate.get(
                Criteria.query(TableWeChatWebSite.class)
                        .eq(TableWeChatWebSite.enable, 1)
                        .eq(TableWeChatWebSite.code, code)
        );
    }

    @Override
    public ModelObject getWebSiteById(int accountId) {
        return sessionTemplate.get(
                Criteria.query(TableWeChatWebSite.class)
                        .eq(TableWeChatWebSite.enable, 1)
                        .eq(TableWeChatWebSite.id, accountId)
        );
    }

    @Override
    public void addWebSite(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableWeChatWebSite.class);
        object.checkAndThrowable();
        object.put(TableWeChatWebSite.createTime, new Date());
        sessionTemplate.save(object);
    }

    @Override
    public void delWebSite(int id) {
        sessionTemplate.delete(TableWeChatWebSite.class, id);
    }

    @Override
    public void updateWebSite(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableWeChatWebSite.class);
        object.checkUpdateThrowable();
        sessionTemplate.update(object);
    }

    @Override
    public Paging getWebSites(ModelObject search, int start, int limit) {
        Query query = Criteria.query(TableWeChatWebSite.class);
        query.setTableClass(TableWeChatWebSite.class);
        query.limit(start, limit);
        query.order(TableWeChatWebSite.id, false);
        Paging paging = sessionTemplate.paging(query);
        return paging;
    }


    @Override
    public ModelObject getDefaultAccount() {
        List<ModelObject> defaults = sessionTemplate.list(
                Criteria.query(TableWeChatWebSite.class)
                        .eq(TableWeChatWebSite.isDefault, 1));
        ModelObject defaultAccount = null;
        if (defaults != null && defaults.size() > 0) {
            if (defaults.size() > 1) {
                for (int i = 1; i < defaults.size(); i++) {
                    ModelObject other = defaults.get(i);
                    ModelObject update = new ModelObject(TableWeChatWebSite.class);
                    update.put(TableWeChatWebSite.id, other.getIntValue(TableWeChatWebSite.id));
                    update.put(TableWeChatWebSite.isDefault, 0);
                    sessionTemplate.update(update);
                }
            }
            defaultAccount = defaults.get(0);
            defaultAccount.put("openType", WeChatOpenType.WEBSITE.getCode());
        }
        return defaultAccount;
    }

    @Override
    public void setDefaultAccount(int accountId) {
        List<ModelObject> defaults = sessionTemplate.list(
                Criteria.query(TableWeChatWebSite.class)
                        .eq(TableWeChatWebSite.isDefault, 1));

        if (defaults != null && defaults.size() > 0) {
            for (int i = 0; i < defaults.size(); i++) {
                ModelObject other = defaults.get(i);
                ModelObject update = new ModelObject(TableWeChatWebSite.class);
                update.put(TableWeChatWebSite.id, other.getIntValue(TableWeChatWebSite.id));
                update.put(TableWeChatWebSite.isDefault, 0);
                sessionTemplate.update(update);
            }
        }

        ModelObject update = new ModelObject(TableWeChatWebSite.class);
        update.put(TableWeChatWebSite.id, accountId);
        update.put(TableWeChatWebSite.isDefault, 1);
        sessionTemplate.update(update);
    }
}
