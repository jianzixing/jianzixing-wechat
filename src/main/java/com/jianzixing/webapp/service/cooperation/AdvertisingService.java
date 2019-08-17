package com.jianzixing.webapp.service.cooperation;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

public interface AdvertisingService {
    void addAdvertising(ModelObject object) throws ModelCheckerException, ModuleException;

    void delAdvertising(int id);

    void updateAdvertising(ModelObject object) throws ModelCheckerException, ModuleException;

    Paging getAdvertising(Query query, int start, int limit);
}
