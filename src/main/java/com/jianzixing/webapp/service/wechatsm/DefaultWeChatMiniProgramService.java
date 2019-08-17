package com.jianzixing.webapp.service.wechatsm;

import com.jianzixing.webapp.service.wechat.WeChatOpenType;
import com.jianzixing.webapp.tables.wechat.TableWeChatMiniProgram;
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
public class DefaultWeChatMiniProgramService implements WeChatMiniProgramService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addMiniProgram(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableWeChatMiniProgram.class);
        object.checkAndThrowable();
        object.put(TableWeChatMiniProgram.createTime, new Date());
        sessionTemplate.save(object);
    }

    @Override
    public void delMiniProgram(int id) {
        sessionTemplate.delete(TableWeChatMiniProgram.class, id);
    }

    @Override
    public void updateMiniProgram(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableWeChatMiniProgram.class);
        object.checkUpdateThrowable();
        object.remove(TableWeChatMiniProgram.createTime);
        object.remove(TableWeChatMiniProgram.accessToken);
        object.remove(TableWeChatMiniProgram.expiresIn);
        object.remove(TableWeChatMiniProgram.lastTokenTime);
        sessionTemplate.update(object);
    }

    @Override
    public Paging getMiniPrograms(Query query, int start, int limit) {
        if (query == null) query = Criteria.query(TableWeChatMiniProgram.class);
        query.setTableClass(TableWeChatMiniProgram.class);
        query.limit(start, limit);
        query.order(TableWeChatMiniProgram.id, false);
        return sessionTemplate.paging(query);
    }

    @Override
    public ModelObject getMiniProgramByCode(String code) {
        return sessionTemplate.get(Criteria.query(TableWeChatMiniProgram.class)
                .eq(TableWeChatMiniProgram.code, code)
                .eq(TableWeChatMiniProgram.enable, 1));
    }

    @Override
    public ModelObject getMiniProgramById(int accountId) {
        return sessionTemplate.get(Criteria.query(TableWeChatMiniProgram.class)
                .eq(TableWeChatMiniProgram.id, accountId)
                .eq(TableWeChatMiniProgram.enable, 1));
    }

    @Override
    public void updateMiniProgramInfo(ModelObject update) throws ModelCheckerException {
        if (update != null) {
            update.setObjectClass(TableWeChatMiniProgram.class);
            update.retain(
                    TableWeChatMiniProgram.id,
                    TableWeChatMiniProgram.accessToken,
                    TableWeChatMiniProgram.expiresIn,
                    TableWeChatMiniProgram.lastTokenTime);
            update.checkUpdateThrowable();
            sessionTemplate.update(update);
        }
    }

    @Override
    public ModelObject getDefaultAccount() {
        ModelObject defaultAccount = this.getDefaultAccount(1, 1);
        return defaultAccount;
    }

    private ModelObject getDefaultAccount(int gtSize, int skip) {
        List<ModelObject> defaults1 = sessionTemplate.list(
                Criteria.query(TableWeChatMiniProgram.class)
                        .eq(TableWeChatMiniProgram.isDefault, 1));
        List<ModelObject> defaults2 = sessionTemplate.list(
                Criteria.query(TableWeChatOpenAccount.class)
                        .eq(TableWeChatOpenAccount.isMiniProgram, 1) //获取小程序部分
                        .eq(TableWeChatOpenAccount.isDefault, 1));

        ModelObject defaultAccount = null;
        if (defaults1 != null && defaults1.size() > 0) {
            if (defaults1.size() > gtSize) {
                for (int i = skip; i < defaults1.size(); i++) {
                    ModelObject other = defaults1.get(i);
                    ModelObject update = new ModelObject(TableWeChatMiniProgram.class);
                    update.put(TableWeChatMiniProgram.id, other.getIntValue(TableWeChatMiniProgram.id));
                    update.put(TableWeChatMiniProgram.isDefault, 0);
                    sessionTemplate.update(update);
                }
            }
            defaultAccount = defaults1.get(0);
            defaultAccount.put("openType", WeChatOpenType.MINI_PROGRAM.getCode());
        }
        if (defaults2 != null && defaults2.size() > 0) {
            if (defaults2.size() > gtSize) {
                for (int i = skip; i < defaults2.size(); i++) {
                    ModelObject other = defaults2.get(i);
                    ModelObject update = new ModelObject(TableWeChatOpenAccount.class);
                    update.put(TableWeChatMiniProgram.id, other.getIntValue(TableWeChatOpenAccount.id));
                    update.put(TableWeChatMiniProgram.isDefault, 0);
                    sessionTemplate.update(update);
                }
            }
            if (defaultAccount == null) {
                defaultAccount = defaults2.get(0);
                defaultAccount.put("openType", WeChatOpenType.MINI_PROGRAM.getCode());
            }
        }
        return defaultAccount;
    }

    @Override
    public void setDefaultAccount(int openType, int accountId) {
        ModelObject defaultAccount = this.getDefaultAccount(0, 0);

        if (openType == WeChatOpenType.OPEN_MINI_PROGRAM.getCode()) {
            ModelObject update = new ModelObject(TableWeChatOpenAccount.class);
            update.put(TableWeChatMiniProgram.id, accountId);
            update.put(TableWeChatMiniProgram.isDefault, 1);
            sessionTemplate.update(update);
        } else if (openType == WeChatOpenType.MINI_PROGRAM.getCode()) {
            ModelObject update = new ModelObject(TableWeChatMiniProgram.class);
            update.put(TableWeChatMiniProgram.id, accountId);
            update.put(TableWeChatMiniProgram.isDefault, 1);
            sessionTemplate.update(update);
        }
    }

    @Override
    public void setEmptyAccountToken(int accountId) {
        if (accountId > 0) {
            sessionTemplate.update(Criteria.update(TableWeChatMiniProgram.class)
                    .eq(TableWeChatMiniProgram.id, accountId)
                    .value(TableWeChatMiniProgram.accessToken, Keyword.NULL));
        }
    }

    @Override
    public void setEmptyAccountToken(String code) {
        if (StringUtils.isNotBlank(code)) {
            sessionTemplate.update(Criteria.update(TableWeChatMiniProgram.class)
                    .eq(TableWeChatMiniProgram.code, code)
                    .value(TableWeChatMiniProgram.accessToken, Keyword.NULL));
        }
    }
}
