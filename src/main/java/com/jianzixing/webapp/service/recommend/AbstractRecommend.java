package com.jianzixing.webapp.service.recommend;

import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;

import java.util.List;

public interface AbstractRecommend {
    List<ModelObject> getRecommends(List<Long> ids);

    ModelObject getRecommend(long id);

    Paging search(String keyword);

    int getType();

    boolean equalsPrimaryKey(long id, ModelObject rel);
}
