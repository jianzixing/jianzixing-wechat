package com.jianzixing.webapp.service.system;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;

import java.util.List;

/**
 * @author yangankang
 */
public interface SystemService {

    List<ModelObject> getTopModules();

    List<ModelObject> getTreeList(int roleId);

    List<ModelObject> getTreeModules(String module, boolean checkRole, int roleId);

    List<ModelObject> getTreeModules(String module, String node);

    void addModule(ModelObject object) throws ModuleException;

    void deleteModule(int id) throws ModuleException;

    void updateModule(ModelObject object) throws ModuleException;

    void addSystemConfig(String name, String value, boolean isSystem, int pos);

    void addSystemConfig(String name, String value);

    void deleteSystemConfig(String name);

    void updateSystemConfig(String name, String value, int pos);

    List<ModelObject> getSystemConfigs(int start, int limit);

    ModelObject getSystemConfig(String name);

    Paging getRoles(int start, int limit);

    void addRole(ModelObject object) throws ModuleException;

    void deleteRole(int id);

    void updateRole(ModelObject object) throws ModuleException;

    List<Integer> getModuleIdByRoleId(int roleId);

    void addModuleAndRole(int roleId, List<Integer> moduleIds);

    void removeModuleAndRole(int roleId, int moduleId);

    void addPageApi(ModelObject object) throws ModuleException;

    void deleteModuleApis(List<Integer> ids);

    List<ModelObject> getPageApis(String page);

    void updatePageApi(ModelObject object) throws ModuleException;

    ModelObject getPageApis(String clazz, String method, String page, int roleId);

    List<ModelObject> getRoles();

}
