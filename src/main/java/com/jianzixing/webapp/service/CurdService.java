package com.jianzixing.webapp.service;

import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Delete;
import org.mimosaframework.orm.criteria.Query;
import org.mimosaframework.orm.criteria.Update;
import org.mimosaframework.springmvc.CurdImplement;
import org.mimosaframework.springmvc.SearchForm;

import java.util.ArrayList;
import java.util.List;


public class CurdService implements CurdImplement {
    private SessionTemplate sessionTemplate;
    private Class tableClass;
    private String pk;

    @Override
    public void setSessionTemplate(SessionTemplate sessionTemplate) {
        this.sessionTemplate = sessionTemplate;
    }

    @Override
    public void setTableClass(Class tableClass) {
        this.tableClass = tableClass;
    }

    @Override
    public void setPrimarykey(String pk) {
        this.pk = pk;
    }

    @Override
    public String add(ModelObject object) {
        ResponseMessage message = null;
        if (object != null) {
            object.setObjectClass(this.tableClass);
            try {
                object.checkAndThrowable();
                sessionTemplate.save(object);
                message = new ResponseMessage();
            } catch (ModelCheckerException e) {
                e.printStackTrace();
                message = new ResponseMessage(e);
            }
        } else {
            message = new ResponseMessage();
            message.setCode(-110);
            message.setMsg("保存的数据缺失");
        }
        return message.toString();
    }

    @Override
    public String del(String id) {
        sessionTemplate.delete(tableClass, id);
        return new ResponseMessage().toString();
    }

    @Override
    public String dels(List<String> ids) {
        sessionTemplate.delete(Criteria.delete(tableClass).in(this.pk));
        return new ResponseMessage().toString();
    }

    @Override
    public String delSearch(SearchForm form) {
        ResponseMessage message = null;
        if (form != null) {
            Delete delete = form.getDelete();
            if (delete != null) {
                delete.setTableClass(tableClass);
                sessionTemplate.delete(delete);
                message = new ResponseMessage();
            } else {
                message = new ResponseMessage(-120, "缺少删除条件");
            }
        } else {
            message = new ResponseMessage(-110, "缺少查询条件");
        }
        return message.toString();
    }

    @Override
    public String update(ModelObject object) {
        ResponseMessage message = null;
        if (object != null) {
            object.setObjectClass(tableClass);
            try {
                object.checkUpdateThrowable();
                sessionTemplate.update(object);
                message = new ResponseMessage();
            } catch (ModelCheckerException e) {
                e.printStackTrace();
                message = new ResponseMessage(e);
            }
        } else {
            message = new ResponseMessage();
            message.setCode(-110);
            message.setMsg("保存的数据缺失");
        }
        return message.toString();
    }

    @Override
    public String updateSearch(SearchForm form) {
        ResponseMessage message = null;
        if (form != null) {
            Update update = form.getUpdate();
            if (update != null) {
                update.setTableClass(tableClass);
                sessionTemplate.update(update);
                message = new ResponseMessage();
            } else {
                message = new ResponseMessage(-120, "缺少更新条件");
            }
        } else {
            message = new ResponseMessage(-110, "缺少查询条件");
        }
        return message.toString();
    }

    @Override
    public String get(String id) {
        ResponseMessage message = new ResponseMessage();
        ModelObject object = sessionTemplate.get(tableClass, id);
        if (object != null) {
            message.setData(object);
        }
        return message.toString();
    }

    @Override
    public String list(SearchForm form, Long start, Long limit) {
        ResponseMessage message = new ResponseMessage();
        Query query = getQueryFromWeb(form, start, limit);

        List<ModelObject> objects = sessionTemplate.list(query);
        if (objects != null) {
            message.setData(objects);
        }
        return message.toString();
    }

    @Override
    public String page(SearchForm form, Long start, Long limit) {
        ResponsePageMessage message = new ResponsePageMessage(0, new ArrayList<>());
        Query query = getQueryFromWeb(form, start, limit);

        Paging<ModelObject> objects = sessionTemplate.paging(query);
        if (objects != null) {
            message = new ResponsePageMessage(objects);
        }
        return message.toString();
    }

    private Query getQueryFromWeb(SearchForm form, Long start, Long limit) {
        Query query = form.getQuery();
        if (query != null) {
            query.setTableClass(tableClass);
            query.limit(start, limit);
        } else {
            query = Criteria.query(tableClass);
            query.limit(start, limit);
        }
        return query;
    }
}
