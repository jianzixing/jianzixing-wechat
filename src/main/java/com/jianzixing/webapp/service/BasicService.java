package com.jianzixing.webapp.service;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Query;

import java.util.List;

public abstract class BasicService {

    public abstract SessionTemplate getSessionTemplate();

    public abstract Class getTable();

    public Object getIdName() {
        return null;
    }

    public void add(ModelObject object) throws ModuleException {
        object.setObjectClass(getTable());

        try {
            object.checkAndThrowable();
        } catch (ModelCheckerException e) {
            throw new ModuleException(e);
        }

        getSessionTemplate().save(object);
    }

    public void delete(int id) throws ModuleException {
        getSessionTemplate().delete(getTable(), id);
    }

    public void delete(List ids) throws ModuleException {
        Object idk = this.getIdName();
        if (idk != null) {
            getSessionTemplate().delete(getTable())
                    .in(idk, ids)
                    .delete();
        }
    }

    public void update(ModelObject object) throws ModuleException {
        object.setObjectClass(getTable());

        try {
            object.checkUpdateThrowable();
        } catch (ModelCheckerException e) {
            throw new ModuleException(e);
        }

        getSessionTemplate().update(object);
    }

    public List<ModelObject> getObjects() {
        return getSessionTemplate().query(getTable()).queries();
    }

    public Paging getPage(int start, int limit, ModelObject search) {
        return getSessionTemplate().query(getTable()).from(search).limit(start, limit).paging();
    }

    public ModelObject get(Query search) {
        search.setTableClass(getTable());
        return getSessionTemplate().get(search);
    }
}
