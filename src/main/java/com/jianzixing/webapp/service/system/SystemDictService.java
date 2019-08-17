package com.jianzixing.webapp.service.system;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

import java.util.List;

public interface SystemDictService {
    void addType(ModelObject object) throws ModelCheckerException, ModuleException;

    void delType(int id);

    void updateType(ModelObject object) throws ModelCheckerException, ModuleException;

    Paging getTypes(Query query, int start, int limit);

    void copy(ModelObject object) throws ModelCheckerException, ModuleException;

    void addDict(ModelObject object) throws ModelCheckerException, ModuleException;

    void delDict(int id);

    void updateDict(ModelObject object) throws ModelCheckerException;

    List<ModelObject> getDicts(int typeId);

    List<ModelObject> getDicts(String tableName, String field);
}
