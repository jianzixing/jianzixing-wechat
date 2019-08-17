package com.jianzixing.webapp.service.wechatsm;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.wechat.WeChatOpenType;
import com.jianzixing.webapp.tables.wechat.*;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Keyword;
import org.mimosaframework.orm.criteria.Query;
import org.mimosaframework.orm.utils.ModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DefaultWeChatPublicService implements WeChatPublicService {
    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addAccount(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableWeChatAccount.class);
        object.checkAndThrowable();
        object.put(TableWeChatAccount.createTime, new Date());
        ModelObject def = getDefaultAccount();
        if (def == null) {
            object.put(TableWeChatAccount.isDefault, 1);
        }
        sessionTemplate.save(object);
    }

    @Override
    public void deleteAccount(int id) {
        sessionTemplate.delete(TableWeChatAccount.class, id);
    }

    @Override
    public void updateAccount(ModelObject object) throws ModelCheckerException {
        object.remove(TableWeChatAccount.createTime);
        object.remove(TableWeChatAccount.checked);
        object.remove(TableWeChatAccount.tokenExpires);
        object.remove(TableWeChatAccount.lastTokenTime);
        object.remove(TableWeChatAccount.accessToken);
        object.setObjectClass(TableWeChatAccount.class);
        object.checkUpdateThrowable();
        sessionTemplate.update(object);
    }

    @Override
    public void updateAccountInfo(ModelObject object) throws ModelCheckerException {
        object.remove(TableWeChatAccount.createTime);
        object.setObjectClass(TableWeChatAccount.class);
        object.checkUpdateThrowable();
        sessionTemplate.update(object);
    }

    @Override
    public Paging getAccounts(Query query, int start, int limit) {
        if (query == null) {
            query = Criteria.query(TableWeChatAccount.class);
        }
        query.setTableClass(TableWeChatAccount.class);
        query.order(TableWeChatAccount.id, false);
        query.limit(start, limit);
        return sessionTemplate.paging(query);
    }

    @Override
    public ModelObject getAccountByCode(String code) {
        return sessionTemplate.get(Criteria.query(TableWeChatAccount.class)
                .eq(TableWeChatAccount.code, code)
                .eq(TableWeChatAccount.enable, 1));
    }

    @Override
    public String getAdminAccountString(ModelObject object) {
        if (object.getIntValue("accountType") == 0) {
            return object.getString(TableWeChatAccount.code);
        } else {
            return object.getString(TableWeChatOpenAccount.authorizerAppid);
        }
    }

    @Override
    public List<ModelObject> getAccountChildTree() {
        List<ModelObject> objects = sessionTemplate.list(Criteria.query(TableWeChatTrees.class)
                .eq(TableWeChatTrees.type, 1).eq(TableWeChatTrees.enable, 1));
        return ModelUtils.getListToTree(objects, TableWeChatTrees.id, TableWeChatTrees.pid, "children");
    }

    @Override
    public ModelObject getAccount(int accountId) {
        return sessionTemplate.get(
                Criteria.query(TableWeChatAccount.class)
                        .eq(TableWeChatAccount.id, accountId)
                        .eq(TableWeChatAccount.enable, 1)
        );
    }

    @Override
    public void enableAccounts(List<Integer> ids) {
        sessionTemplate.update(
                Criteria.update(TableWeChatAccount.class)
                        .in(TableWeChatAccount.id, ids)
                        .value(TableWeChatAccount.enable, 1)
        );
    }

    @Override
    public void disableAccounts(List<Integer> ids) {
        sessionTemplate.update(
                Criteria.update(TableWeChatAccount.class)
                        .in(TableWeChatAccount.id, ids)
                        .value(TableWeChatAccount.enable, 0)
        );
    }

    @Override
    public ModelObject getDefaultAccount() {
        ModelObject defaultAccount = null;
        try {
            defaultAccount = this.getDefaultAccount(1, 1);
        } catch (ModuleException e) {
            e.printStackTrace();
        }
        return defaultAccount;
    }

    /**
     * 第三方授权默认公众号 和 自己填写的公众号只能有一个默认
     * 这里查找并获取，如果有多余的默认设置则将多余的默认设置为非默认
     *
     * @param gtSize
     * @param skip
     * @return
     */
    private ModelObject getDefaultAccount(int gtSize, int skip) throws ModuleException {
        List<ModelObject> defaults1 = sessionTemplate.list(
                Criteria.query(TableWeChatAccount.class)
                        .eq(TableWeChatAccount.isDefault, 1));
        List<ModelObject> defaults2 = sessionTemplate.list(
                Criteria.query(TableWeChatOpenAccount.class)
                        .eq(TableWeChatOpenAccount.isMiniProgram, 0) //获取公众号部分
                        .eq(TableWeChatOpenAccount.isDefault, 1));

        ModelObject defaultAccount = null;
        if (defaults1 != null && defaults1.size() > 0) {
            if (defaults1.size() > gtSize) {
                for (int i = skip; i < defaults1.size(); i++) {
                    ModelObject other = defaults1.get(i);
                    ModelObject update = new ModelObject(TableWeChatAccount.class);
                    update.put(TableWeChatAccount.id, other.getIntValue(TableWeChatAccount.id));
                    update.put(TableWeChatAccount.isDefault, 0);
                    sessionTemplate.update(update);
                }
            }
            defaultAccount = defaults1.get(0);
            defaultAccount.put("openType", WeChatOpenType.PUBLIC.getCode());
            if (defaultAccount.getIntValue(TableWeChatOpen.enable) == 0) {
                throw new ModuleException("not_enable_public", "公众号配置未启用");
            }
        }
        if (defaults2 != null && defaults2.size() > 0) {
            if (defaults2.size() > gtSize) {
                for (int i = skip; i < defaults2.size(); i++) {
                    ModelObject other = defaults2.get(i);
                    ModelObject update = new ModelObject(TableWeChatOpenAccount.class);
                    update.put(TableWeChatAccount.id, other.getIntValue(TableWeChatOpenAccount.id));
                    update.put(TableWeChatAccount.isDefault, 0);
                    sessionTemplate.update(update);
                }
            }
            if (defaultAccount == null) {
                defaultAccount = defaults2.get(0);
                defaultAccount.put("appId", defaultAccount.getString(TableWeChatOpenAccount.authorizerAppid));
                defaultAccount.put("openType", WeChatOpenType.OPEN_PUBLIC.getCode());
                ModelObject openInfo = GlobalService.weChatOpenService.getOpenById(
                        defaultAccount.getIntValue(TableWeChatOpenAccount.tpId));
                if (openInfo.getIntValue(TableWeChatOpen.enable) == 0) {
                    throw new ModuleException("not_enable_open", "第三方平台配置未启用");
                }
            }
        }
        return defaultAccount;
    }

    @Override
    public void setDefaultAccount(int openType, int accountId) {
        // 重置公众号默认标识
        try {
            ModelObject defaultAccount = this.getDefaultAccount(0, 0);
        } catch (ModuleException e) {
        }

        if (openType == WeChatOpenType.OPEN_PUBLIC.getCode()) {
            ModelObject update = new ModelObject(TableWeChatOpenAccount.class);
            update.put(TableWeChatAccount.id, accountId);
            update.put(TableWeChatAccount.isDefault, 1);
            sessionTemplate.update(update);
        } else if (openType == WeChatOpenType.PUBLIC.getCode()) {
            ModelObject update = new ModelObject(TableWeChatAccount.class);
            update.put(TableWeChatAccount.id, accountId);
            update.put(TableWeChatAccount.isDefault, 1);
            sessionTemplate.update(update);
        }
    }

    @Override
    public void setChecked(long id) {
        ModelObject update = new ModelObject(TableWeChatAccount.class);
        update.put(TableWeChatAccount.id, id);
        update.put(TableWeChatAccount.checked, true);
        update.put(TableWeChatAccount.checkedTime, new Date());
        sessionTemplate.update(update);
    }

    @Override
    public void setEmptyAccountToken(int accountId) {
        if (accountId > 0) {
            sessionTemplate.update(Criteria.update(TableWeChatAccount.class)
                    .eq(TableWeChatAccount.id, accountId)
                    .value(TableWeChatAccount.accessToken, Keyword.NULL));
        }
    }

    @Override
    public void setEmptyAccountToken(String code) {
        if (StringUtils.isNotBlank(code)) {
            sessionTemplate.update(Criteria.update(TableWeChatAccount.class)
                    .eq(TableWeChatAccount.code, code)
                    .value(TableWeChatAccount.accessToken, Keyword.NULL));
        }
    }
}
