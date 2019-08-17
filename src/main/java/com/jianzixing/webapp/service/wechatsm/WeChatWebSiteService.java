package com.jianzixing.webapp.service.wechatsm;

import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

public interface WeChatWebSiteService {
    ModelObject getWebSiteByCode(String code);

    ModelObject getWebSiteById(int accountId);

    void addWebSite(ModelObject object) throws ModelCheckerException;

    void delWebSite(int id);

    void updateWebSite(ModelObject object) throws ModelCheckerException;

    Paging getWebSites(ModelObject search, int start, int limit);

    ModelObject getDefaultAccount();

    void setDefaultAccount(int accountId);
}
