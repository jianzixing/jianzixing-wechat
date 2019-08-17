package com.jianzixing.webapp.service.system;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.tables.system.TableSystemDict;
import com.jianzixing.webapp.tables.system.TableSystemDictType;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultSystemDictService implements SystemDictService {
    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addType(ModelObject object) throws ModelCheckerException, ModuleException {
        object.remove(TableSystemDictType.id);
        object.setObjectClass(TableSystemDictType.class);
        object.checkAndThrowable();

        List<ModelObject> list = sessionTemplate.list(Criteria.query(TableSystemDictType.class)
                .eq(TableSystemDictType.table, object.getString(TableSystemDictType.table))
                .eq(TableSystemDictType.field, object.getString(TableSystemDictType.field)));
        if (list == null || list.size() == 0) {
            sessionTemplate.save(object);
        } else {
            throw new ModuleException(StockCode.EXIST_OBJ, "该表中已经存在该字段的字典");
        }
    }

    @Override
    public void delType(int id) {
        sessionTemplate.delete(Criteria.delete(TableSystemDict.class).eq(TableSystemDict.dictType, id));
        sessionTemplate.delete(TableSystemDictType.class, id);
    }

    @Override
    public void updateType(ModelObject object) throws ModelCheckerException, ModuleException {
        object.setObjectClass(TableSystemDictType.class);
        object.remove(TableSystemDictType.table);
        object.remove(TableSystemDictType.field);
        object.checkUpdateThrowable();
        sessionTemplate.update(object);
    }

    @Override
    public Paging getTypes(Query query, int start, int limit) {
        if (query == null) {
            query = Criteria.query(TableSystemDictType.class);
        }
        query.limit(start, limit);
        return sessionTemplate.paging(query);
    }

    @Override
    public void copy(ModelObject object) throws ModelCheckerException, ModuleException {
        object.setObjectClass(TableSystemDictType.class);
        object.checkAndThrowable();
        if (object.getIntValue(TableSystemDictType.id) == 0) {
            throw new ModuleException(StockCode.NOT_PRIMARY_KEY, "没有找到要Copy的主键");
        }
        int dictTypeId = object.getIntValue(TableSystemDictType.id);
        List<ModelObject> objects = sessionTemplate.list(Criteria.query(TableSystemDict.class).eq(TableSystemDict.dictType, dictTypeId));
        if (objects == null) {
            throw new ModuleException(StockCode.NOT_EXIST, "没有找到类型 " + object.getString(TableSystemDictType.name) + " 下的字典列表");
        }

        List<ModelObject> list = sessionTemplate.list(Criteria.query(TableSystemDictType.class)
                .eq(TableSystemDictType.table, object.getString(TableSystemDictType.table))
                .eq(TableSystemDictType.field, object.getString(TableSystemDictType.field)));
        if (list == null || list.size() == 0) {
            sessionTemplate.update(object);
        } else {
            throw new ModuleException(StockCode.EXIST_OBJ, "该表中已经存在该字段的字典,请修改字典表和字段");
        }

        object.remove(TableSystemDictType.id);
        sessionTemplate.save(object);
        for (ModelObject o : objects) {
            o.setObjectClass(TableSystemDict.class);
            o.remove(TableSystemDict.id);
            o.put(TableSystemDict.dictType, object.getIntValue(TableSystemDictType.id));
        }
        sessionTemplate.save(objects);
    }

    @Override
    public void addDict(ModelObject object) throws ModelCheckerException, ModuleException {
        ModelObject o = sessionTemplate.get(TableSystemDictType.class, object.getIntValue(TableSystemDict.dictType));
        if (o == null) {
            throw new ModuleException(StockCode.NOT_EXIST, "关联的字典类型不存在");
        }

        object.put(TableSystemDict.table, o.getString(TableSystemDictType.table));
        object.put(TableSystemDict.field, o.getString(TableSystemDictType.field));
        object.remove(TableSystemDict.id);
        object.setObjectClass(TableSystemDict.class);
        object.checkAndThrowable();

        sessionTemplate.save(object);
    }

    @Override
    public void delDict(int id) {
        sessionTemplate.delete(TableSystemDict.class, id);
    }

    @Override
    public void updateDict(ModelObject object) throws ModelCheckerException {
        object.remove(TableSystemDict.table);
        object.remove(TableSystemDict.field);
        object.setObjectClass(TableSystemDict.class);
        object.checkUpdateThrowable();

        object.remove(TableSystemDict.dictType);
        sessionTemplate.update(object);
    }

    @Override
    public List<ModelObject> getDicts(int typeId) {
        return sessionTemplate.list(Criteria.query(TableSystemDict.class).eq(TableSystemDict.dictType, typeId));
    }

    @Override
    public List<ModelObject> getDicts(String tableName, String field) {

        ModelObject object = sessionTemplate.get(Criteria.query(TableSystemDictType.class)
                .eq(TableSystemDictType.table, tableName)
                .eq(TableSystemDictType.field, field)
                .subjoin(TableSystemDict.class).eq(TableSystemDict.dictType, TableSystemDictType.id)
                .query());

        if (object != null) {
            return object.getArray(TableSystemDict.class.getSimpleName());
        }
        return null;
    }
}
