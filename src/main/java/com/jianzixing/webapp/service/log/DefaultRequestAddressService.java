package com.jianzixing.webapp.service.log;

import com.jianzixing.webapp.tables.log.TableRequestAddress;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DefaultRequestAddressService implements RequestAddressService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addAddress(ModelObject object) {
        object.setObjectClass(TableRequestAddress.class);
        try {
            object.put(TableRequestAddress.createTime, new Date());
            object.checkAndThrowable();
            sessionTemplate.save(object);
        } catch (ModelCheckerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Paging getAddresses(Query query, int start, int limit) {
        if (query == null) {
            query = Criteria.query(TableRequestAddress.class);
        }
        query.order(TableRequestAddress.id, false);
        query.limit(start, limit);
        return sessionTemplate.paging(query);
    }
}
