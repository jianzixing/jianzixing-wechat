package com.jianzixing.webapp.service.log;

import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

public interface RequestAddressService {
    void addAddress(ModelObject object);

    Paging getAddresses(Query query, int start, int limit);
}
