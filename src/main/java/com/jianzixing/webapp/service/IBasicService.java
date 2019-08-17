package com.jianzixing.webapp.service;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

public interface IBasicService {
    void add(ModelObject object) throws ModuleException;

    void delete(int id) throws ModuleException;

    void update(ModelObject object) throws ModuleException;

    Paging getPage(int start, int limit, ModelObject search);

    ModelObject get(Query query);
}
