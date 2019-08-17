package com.jianzixing.webapp.service.wechatsm;

import com.jianzixing.webapp.tables.wechat.TableWeChatOpen;
import com.jianzixing.webapp.tables.wechat.TableWeChatOpenAccount;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Keyword;
import org.mimosaframework.orm.criteria.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DefaultWeChatOpenService implements WeChatOpenService {

    @Autowired
    SessionTemplate sessionTemplate;


    @Override
    public void addOpen(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableWeChatOpen.class);
        object.checkAndThrowable();
        object.put(TableWeChatOpen.createTime, new Date());
        sessionTemplate.save(object);
    }

    @Override
    public void deleteOpen(int id) {
        sessionTemplate.delete(TableWeChatOpen.class, id);
    }

    @Override
    public void updateOpenBase(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableWeChatOpen.class);
        object.checkUpdateThrowable();
        object.retain(TableWeChatOpen.id, TableWeChatOpen.name, TableWeChatOpen.logo, TableWeChatOpen.code,
                TableWeChatOpen.appId, TableWeChatOpen.appSecret, TableWeChatOpen.appToken, TableWeChatOpen.appKey,
                TableWeChatOpen.enable, TableWeChatOpen.detail);
        sessionTemplate.update(object);
    }

    @Override
    public void updateOpen(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableWeChatOpen.class);
        object.checkUpdateThrowable();
        sessionTemplate.update(object);
    }

    @Override
    public void updateOpenByCode(ModelObject object) throws ModelCheckerException {
        String code = object.getString(TableWeChatOpen.code);
        ModelObject obj = sessionTemplate.get(Criteria.query(TableWeChatOpen.class).eq(TableWeChatOpen.code, code));
        if (obj != null) {
            object.setObjectClass(TableWeChatOpen.class);
            object.put(TableWeChatOpen.id, obj.getLongValue(TableWeChatOpen.id));
            sessionTemplate.update(object);
        }
    }

    @Override
    public Paging getOpens(Query query, int start, int limit) {
        if (query == null) {
            query = Criteria.query(TableWeChatOpen.class);
        }
        query.setTableClass(TableWeChatOpen.class);
        query.limit(start, limit);
        query.order(TableWeChatOpen.id, false);
        return sessionTemplate.paging(query);
    }

    @Override
    public ModelObject getOpenById(int id) {
        return sessionTemplate.get(Criteria.query(TableWeChatOpen.class)
                .eq(TableWeChatOpen.id, id));
    }

    @Override
    public ModelObject getOpenAccountById(int accountId) {
        return sessionTemplate.get(
                Criteria.query(TableWeChatOpenAccount.class)
                        .subjoin(TableWeChatOpen.class).eq(TableWeChatOpen.id, TableWeChatOpenAccount.tpId).single().query()
                        .eq(TableWeChatOpenAccount.id, accountId)
        );
    }

    @Override
    public Paging getOpenAccounts(Query query, int openPlatformId, int start, int limit) {
        if (query == null) {
            query = Criteria.query(TableWeChatOpenAccount.class);
        } else {
            query.setTableClass(TableWeChatOpenAccount.class);
        }
        query.eq(TableWeChatOpenAccount.tpId, openPlatformId);
        query.limit(start, limit);
        query.order(TableWeChatOpenAccount.id, false);
        return sessionTemplate.paging(query);
    }

    @Override
    public ModelObject getAccountByAppId(String code, String appId) {
        return sessionTemplate.get(
                Criteria.query(TableWeChatOpenAccount.class)
                        .eq(TableWeChatOpenAccount.tpCode, code)
                        .eq(TableWeChatOpenAccount.authorizerAppid, appId)
        );
    }

    @Override
    public ModelObject getOpenByCode(String code) {
        return sessionTemplate.get(
                Criteria.query(TableWeChatOpen.class)
                        .eq(TableWeChatOpen.code, code)
                        .eq(TableWeChatOpen.enable, 1)
        );
    }

    @Override
    public void updateOpenAccountByCode(ModelObject update) {
        String tpCode = update.getString(TableWeChatOpenAccount.tpCode);
        String appid = update.getString(TableWeChatOpenAccount.authorizerAppid);

        if (StringUtils.isNotBlank(tpCode) && StringUtils.isNotBlank(appid)) {
            update.setObjectClass(TableWeChatOpenAccount.class);
            ModelObject obj = sessionTemplate.get(Criteria.query(TableWeChatOpenAccount.class)
                    .eq(TableWeChatOpenAccount.tpCode, tpCode).eq(TableWeChatOpenAccount.authorizerAppid, appid));
            if (obj != null) {
                update.put(TableWeChatOpenAccount.id, obj.getLongValue(TableWeChatOpenAccount.id));
                sessionTemplate.update(update);
            } else {
                update.put(TableWeChatOpenAccount.createTime, new Date());
                sessionTemplate.save(update);
            }
        }
    }

    @Override
    public void updateOpenAccountById(ModelObject update) throws ModelCheckerException {
        update.setObjectClass(TableWeChatOpenAccount.class);
        update.checkUpdateThrowable();
        sessionTemplate.update(update);
    }

    @Override
    public void enableOpens(List<Integer> ids) {
        sessionTemplate.update(
                Criteria.update(TableWeChatOpen.class)
                        .in(TableWeChatOpen.id, ids)
                        .value(TableWeChatOpen.enable, 1)
        );
    }

    @Override
    public void disableOpens(List<Integer> ids) {
        sessionTemplate.update(
                Criteria.update(TableWeChatOpen.class)
                        .in(TableWeChatOpen.id, ids)
                        .value(TableWeChatOpen.enable, 0)
        );
    }

    @Override
    public void setEmptyAccountToken(int accountId) {
        if (accountId > 0) {
            sessionTemplate.update(Criteria.update(TableWeChatOpenAccount.class)
                    .eq(TableWeChatOpenAccount.id, accountId)
                    .value(TableWeChatOpenAccount.authorizerAccessToken, Keyword.NULL));
        }
    }
}
