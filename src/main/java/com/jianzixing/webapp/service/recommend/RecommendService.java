package com.jianzixing.webapp.service.recommend;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;

import java.util.List;

public interface RecommendService {
    void addGroup(ModelObject object) throws ModelCheckerException, ModuleException;

    void delGroup(int id);

    void updateGroup(ModelObject object) throws ModelCheckerException, ModuleException;

    List<ModelObject> getTreeGroup();

    void addContent(ModelObject object) throws ModelCheckerException, ModuleException;

    void delContent(int id);

    void updateContent(ModelObject object) throws ModelCheckerException, ModuleException;

    Paging getContents(int gid, int start, int limit);

    List<ModelObject> getRecommends(String code, int count);

    List<ModelObject> getRecommendContents(String code, int count);

    ModelObject getLevelRecommends(String code, int count);

    ModelObject getRecommendGroupByCode(String code);

    List<ModelObject> getRecommendsByObject(ModelObject c, int count);

    List<ModelObject> getChildRecommendGroup(int intValue);

    void setRecommendTop(int id, int level);

    void setRecommendImage(int id, String cover);
}
