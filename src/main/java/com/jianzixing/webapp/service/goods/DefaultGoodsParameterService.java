package com.jianzixing.webapp.service.goods;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.BasicService;
import com.jianzixing.webapp.tables.goods.TableGoodsGroup;
import com.jianzixing.webapp.tables.goods.TableGoodsParameter;
import com.jianzixing.webapp.tables.goods.TableGoodsParameterRel;
import com.jianzixing.webapp.tables.goods.TableGoodsValue;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangankang
 */
@Service
public class DefaultGoodsParameterService implements GoodsParameterService {

    @Autowired
    SessionTemplate sessionTemplate;

    private P getPInstance() {
        return new P();
    }

    private V getVInstance() {
        return new V();
    }

    @Override
    public ModelObject getGroupSet(int gid) {
        ModelObject object = sessionTemplate.query(TableGoodsGroup.class)
                .subjoin(TableGoodsParameterRel.class).eq(TableGoodsParameterRel.groupId, TableGoodsGroup.id)
                .childJoin(TableGoodsParameter.class).eq(TableGoodsParameter.id, TableGoodsParameterRel.parameterId).single()
                .childJoin(TableGoodsValue.class).eq(TableGoodsValue.parameterId, TableGoodsParameter.id).parent()
                .parent()
                .query()
                .eq(TableGoodsGroup.id, gid)
                .query();

        if (object != null) {
            List<ModelObject> rels = object.getArray(TableGoodsParameterRel.class);
            if (rels != null) {
                List<ModelObject> params = new ArrayList<>();
                for (ModelObject rel : rels) {
                    ModelObject param = rel.getModelObject(TableGoodsParameter.class);
                    params.add(param);
                }
                object.put(TableGoodsParameter.class, params);
            }
        }
        return object;
    }

    @Override
    public void addParameter(ModelObject parameter) throws ModuleException {
        int gid = parameter.getIntValue("gid");
        getPInstance().add(parameter);
        ModelObject rel = new ModelObject(TableGoodsParameterRel.class);
        rel.put(TableGoodsParameterRel.groupId, gid);
        rel.put(TableGoodsParameterRel.parameterId, parameter.getLongValue(TableGoodsParameter.id));
        sessionTemplate.save(rel);
    }

    @Override
    public void deleteParameter(int id) {

        sessionTemplate.delete(Criteria.delete(TableGoodsParameterRel.class)
                .eq(TableGoodsParameterRel.parameterId, id));

        sessionTemplate.delete(TableGoodsValue.class)
                .eq(TableGoodsValue.parameterId, id)
                .delete();
        try {
            getPInstance().delete(id);
        } catch (ModuleException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateParameter(ModelObject parameter) throws ModuleException {
        getPInstance().update(parameter);
    }

    @Override
    public void updateSimpleParameter(ModelObject parameter) throws ModuleException {
        parameter.setObjectClass(TableGoodsParameter.class);
        if (parameter.isEmpty(TableGoodsParameter.id)) {
            throw new ModuleException(StockCode.ARG_NULL, "id不能为空");
        }
        parameter.clearEmpty();
        sessionTemplate.update(parameter);
    }

    @Override
    public List<ModelObject> getParameter(int gid) {
        List<ModelObject> rels = sessionTemplate.list(Criteria.query(TableGoodsParameterRel.class)
                .eq(TableGoodsParameterRel.groupId, gid));

        List<Long> pids = new ArrayList<>();
        if (rels != null) {
            for (ModelObject rel : rels) {
                pids.add(rel.getLongValue(TableGoodsParameterRel.parameterId));
            }
        }

        if (pids != null && pids.size() > 0) {
            return sessionTemplate.query(TableGoodsParameter.class)
                    .in(TableGoodsParameter.id, pids)
                    .order(TableGoodsParameter.pos, true)
                    .queries();
        }
        return null;
    }

    @Override
    public void addValue(ModelObject object) throws ModuleException {
        getVInstance().add(object);
    }

    @Override
    public void deleteValue(int id) {
        try {
            getVInstance().delete(id);
        } catch (ModuleException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateValue(ModelObject object) throws ModuleException {
        object.setObjectClass(TableGoodsValue.class);
        if (object.isEmpty(TableGoodsValue.id)) {
            throw new ModuleException(StockCode.ARG_NULL, "id不能为空");
        }
        sessionTemplate.update(object);
    }

    @Override
    public List<ModelObject> getValues(int pid) {
        if (pid != 0) {
            return sessionTemplate.query(TableGoodsValue.class)
                    .eq(TableGoodsValue.parameterId, pid)
                    .order(TableGoodsParameter.pos, true)
                    .queries();
        }
        return null;
    }

    @Override
    public Paging getParameterList(String keyword, long start, long limit) {
        Query query = Criteria.query(TableGoodsParameter.class);
        if (StringUtils.isNotBlank(keyword)) {
            query.like(TableGoodsParameter.name, "%" + keyword + "%");
        }
        query.limit(start, limit);
        return sessionTemplate.paging(query);
    }

    @Override
    public void setParameterRel(int pid, List<Long> ids) {
        if (pid != 0 && ids != null && ids.size() > 0) {
            for (long id : ids) {
                ModelObject rel = new ModelObject(TableGoodsParameterRel.class);
                rel.put(TableGoodsParameterRel.parameterId, id);
                rel.put(TableGoodsParameterRel.groupId, pid);
                sessionTemplate.saveAndUpdate(rel);
            }
        }
    }

    @Override
    public void removeParameterRel(int pid, List<Long> ids) {
        if (pid != 0 && ids != null && ids.size() > 0) {
            for (long id : ids) {
                ModelObject rel = new ModelObject(TableGoodsParameterRel.class);
                rel.put(TableGoodsParameterRel.parameterId, id);
                rel.put(TableGoodsParameterRel.groupId, pid);
                sessionTemplate.delete(rel);
            }
        }
    }

    private class P extends BasicService {

        @Override
        public SessionTemplate getSessionTemplate() {
            return sessionTemplate;
        }

        @Override
        public Class getTable() {
            return TableGoodsParameter.class;
        }
    }

    private class V extends BasicService {

        @Override
        public SessionTemplate getSessionTemplate() {
            return sessionTemplate;
        }

        @Override
        public Class getTable() {
            return TableGoodsValue.class;
        }
    }
}
