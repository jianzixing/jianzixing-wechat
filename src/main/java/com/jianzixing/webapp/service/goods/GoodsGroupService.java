package com.jianzixing.webapp.service.goods;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.json.ModelObject;

import java.util.List;

public interface GoodsGroupService {
    void addGroup(ModelObject object) throws ModuleException;

    void deleteGroups(List<Integer> ids) throws ModuleException;

    void updateGroup(ModelObject object) throws ModuleException;

    List<ModelObject> getGroups();

    List<ModelObject> getListGroups();

    void checkGroupNeedUpdate();

    List<ModelObject> getParentGroups(int gid);

    List<ModelObject> getGroups(List<Integer> ids);

    ModelObject getGroupById(long gid);

    /**
     * 获取子分类
     * @param id 分类ID
     * @return 子分类列表
     */
    List<ModelObject> getChildrenGroups(int id);
}
