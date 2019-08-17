package com.jianzixing.webapp.service.wechatsm;

import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

import java.util.List;

public interface WeChatOpenService {

    void addOpen(ModelObject object) throws ModelCheckerException;

    void deleteOpen(int id);

    void updateOpenBase(ModelObject object) throws ModelCheckerException;

    void updateOpen(ModelObject object) throws ModelCheckerException;

    void updateOpenByCode(ModelObject object) throws ModelCheckerException;

    Paging getOpens(Query query, int start, int limit);

    ModelObject getOpenById(int id);

    ModelObject getOpenAccountById(int accountId);

    Paging getOpenAccounts(Query query, int openPlatformId, int start, int limit);

    ModelObject getAccountByAppId(String code, String appId);

    ModelObject getOpenByCode(String code);

    void updateOpenAccountByCode(ModelObject update);

    void updateOpenAccountById(ModelObject update) throws ModelCheckerException;

    void enableOpens(List<Integer> ids);

    void disableOpens(List<Integer> ids);

    void setEmptyAccountToken(int accountId);
}
