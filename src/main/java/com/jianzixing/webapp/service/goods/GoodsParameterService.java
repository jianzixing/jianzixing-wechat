package com.jianzixing.webapp.service.goods;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;

import java.util.List;

/**
 * @author yangankang
 */
public interface GoodsParameterService {

    ModelObject getGroupSet(int gid);

    void addParameter(ModelObject parameter) throws ModuleException;

    void deleteParameter(int id);

    void updateParameter(ModelObject parameter) throws ModuleException;

    void updateSimpleParameter(ModelObject parameter) throws ModuleException;

    List<ModelObject> getParameter(int gid);

    void addValue(ModelObject object) throws ModuleException;

    void deleteValue(int id);

    void updateValue(ModelObject object) throws ModuleException;

    List<ModelObject> getValues(int pid);

    Paging getParameterList(String keyword, long start, long limit);

    void setParameterRel(int pid, List<Long> ids);

    void removeParameterRel(int pid, List<Long> ids);
}
