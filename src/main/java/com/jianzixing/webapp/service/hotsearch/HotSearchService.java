package com.jianzixing.webapp.service.hotsearch;

import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;

import java.util.List;

public interface HotSearchService {
    void addHotSearch(ModelObject object) throws ModelCheckerException;

    void delHotSearch(int id);

    void updateHotSearch(ModelObject object) throws ModelCheckerException;

    Paging getHotSearch(int start, int limit);

    List<ModelObject> getHotSearchByType(HotSearchType type, int count);
}
