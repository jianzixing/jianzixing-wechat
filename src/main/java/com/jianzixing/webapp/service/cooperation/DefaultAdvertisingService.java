package com.jianzixing.webapp.service.cooperation;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.tables.cooperation.TableAdvertisingConfig;
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
public class DefaultAdvertisingService implements AdvertisingService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addAdvertising(ModelObject object) throws ModelCheckerException, ModuleException {
        object.setObjectClass(TableAdvertisingConfig.class);
        object.checkAndThrowable();
        object.put(TableAdvertisingConfig.createTime, new Date());
        String code = object.getString(TableAdvertisingConfig.code);
        ModelObject old = sessionTemplate.get(Criteria.query(TableAdvertisingConfig.class).eq(TableAdvertisingConfig.code, code));
        if (old != null) {
            throw new ModuleException(StockCode.EXIST_OBJ, "当前广告码已经存在!");
        }
        sessionTemplate.save(object);
    }

    @Override
    public void delAdvertising(int id) {
        sessionTemplate.delete(TableAdvertisingConfig.class, id);
    }

    @Override
    public void updateAdvertising(ModelObject object) throws ModelCheckerException, ModuleException {
        object.setObjectClass(TableAdvertisingConfig.class);
        object.checkUpdateThrowable();
        object.put(TableAdvertisingConfig.createTime, new Date());
        String code = object.getString(TableAdvertisingConfig.code);
        int id = object.getIntValue(TableAdvertisingConfig.id);
        ModelObject old = sessionTemplate.get(
                Criteria.query(TableAdvertisingConfig.class).eq(TableAdvertisingConfig.code, code)
                        .ne(TableAdvertisingConfig.id, id)
        );
        if (old != null) {
            throw new ModuleException(StockCode.EXIST_OBJ, "当前广告码已经存在!");
        }

        sessionTemplate.update(object);
    }

    @Override
    public Paging getAdvertising(Query query, int start, int limit) {
        if (query == null) {
            query = Criteria.query(TableAdvertisingConfig.class);
        }
        query.setTableClass(TableAdvertisingConfig.class);
        query.order(TableAdvertisingConfig.id, false);
        query.limit(start, limit);
        return sessionTemplate.paging(query);
    }
}
