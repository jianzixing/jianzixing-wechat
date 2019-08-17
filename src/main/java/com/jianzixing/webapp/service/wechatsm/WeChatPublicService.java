package com.jianzixing.webapp.service.wechatsm;

import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

import java.util.List;

public interface WeChatPublicService {
    void addAccount(ModelObject object) throws ModelCheckerException;

    void deleteAccount(int id);

    void updateAccount(ModelObject object) throws ModelCheckerException;

    void updateAccountInfo(ModelObject object) throws ModelCheckerException;

    Paging getAccounts(Query query, int start, int limit);

    ModelObject getAccountByCode(String code);

    String getAdminAccountString(ModelObject object);

    List<ModelObject> getAccountChildTree();

    ModelObject getAccount(int accountId);

    void enableAccounts(List<Integer> ids);

    void disableAccounts(List<Integer> ids);

    /**
     * 获得默认的账号
     * 公众号和第三方授权的公众号作用是相同的
     * 所以公众号和第三方公众号的默认账号只能有一个启用
     * 这里获得的账号可能是公众号账号也可能是第三方授权公众号
     *
     * @return
     */
    ModelObject getDefaultAccount();

    /**
     * openType判断是否是第三方管理的账号
     * 当前系统只允许启用一个默认账号，这个默认账号是公众号或者
     * 第三方平台账号
     *
     * @param openType
     * @param accountId
     */
    void setDefaultAccount(int openType, int accountId);

    /**
     * 设置公众号已连接服务器
     *
     * @param id
     */
    void setChecked(long id);

    void setEmptyAccountToken(int accountId);

    void setEmptyAccountToken(String code);
}
