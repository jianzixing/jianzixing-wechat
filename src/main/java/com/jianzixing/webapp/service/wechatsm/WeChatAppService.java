package com.jianzixing.webapp.service.wechatsm;

import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

public interface WeChatAppService {
    ModelObject getAppByCode(String code);

    ModelObject getAppById(int accountId);

    void addApp(ModelObject object) throws ModelCheckerException;

    void delApp(int id);

    void updateApp(ModelObject object) throws ModelCheckerException;

    Paging getApps(Query query, int start, int limit);

    ModelObject getDefaultAccount();

    void setDefaultAccount(int accountId);
}
