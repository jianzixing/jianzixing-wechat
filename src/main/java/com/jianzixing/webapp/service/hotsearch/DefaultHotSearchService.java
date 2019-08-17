package com.jianzixing.webapp.service.hotsearch;

import com.jianzixing.webapp.tables.hotsearch.TableHotSearch;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultHotSearchService implements HotSearchService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addHotSearch(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableHotSearch.class);
        object.checkAndThrowable();
        sessionTemplate.save(object);
    }

    @Override
    public void delHotSearch(int id) {
        sessionTemplate.delete(TableHotSearch.class, id);
    }

    @Override
    public void updateHotSearch(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableHotSearch.class);
        object.checkUpdateThrowable();
        sessionTemplate.update(object);
    }

    @Override
    public Paging getHotSearch(int start, int limit) {
        return sessionTemplate.paging(Criteria.query(TableHotSearch.class)
                .order(TableHotSearch.id, false)
                .limit(start, limit));
    }

    @Override
    public List<ModelObject> getHotSearchByType(HotSearchType type, int count) {
        return sessionTemplate.list(Criteria.query(TableHotSearch.class)
                .eq(TableHotSearch.type, type.getCode())
                .order(TableHotSearch.pos, true)
                .limit(0, count));
    }
}
