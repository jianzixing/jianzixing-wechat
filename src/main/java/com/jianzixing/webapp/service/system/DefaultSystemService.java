package com.jianzixing.webapp.service.system;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.tables.system.*;
import org.apache.commons.lang.math.NumberUtils;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author yangankang
 */
@Service
public class DefaultSystemService implements SystemService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public List<ModelObject> getTopModules() {
        return sessionTemplate.query(TableModule.class)
                .addFilter().eq(TableModule.top, 1)
                .query()
                .order(TableModule.pos, true).queries();
    }

    @Override
    public List<ModelObject> getTreeList(int roleId) {
        List<Integer> authObjects = this.getModuleIdByRoleId(roleId);
        return sessionTemplate.query(TableModule.class)
                .in(TableModule.id, authObjects).queries();
    }

    @Override
    public List<ModelObject> getTreeModules(String module, boolean checkRole, int roleId) {
        Order order = new Order();
        order.setField(TableModule.pos);
        order.setAsc(true);
        List<ModelObject> objects = null;
        if (module != null) {
            objects = sessionTemplate.query(TableModule.class)
                    .addFilter().eq(TableModule.linkModule, module).query()
                    .addFilter().ne(TableModule.top, 1).query()
                    .addOrder(order).queries();
        } else {
            objects = sessionTemplate.query(TableModule.class)
                    .addOrder(order)
                    .queries();
        }

        Map<Integer, ModelObject> map = new LinkedHashMap();
        if (objects != null) {
            if (checkRole && roleId != -100) {
                List<Integer> authObjects = this.getModuleIdByRoleId(roleId);
                objects.stream().forEach(m -> {
                    if (authObjects.contains(m.getIntValue(TableModule.id))) {
                        map.put(m.getIntValue(TableModule.id), m);
                    }
                });
            } else {
                objects.stream().forEach(m -> map.put(m.getIntValue(TableModule.id), m));
            }

            List<ModelObject> result = new ArrayList();
            List<ModelObject> removes = new ArrayList<>();
            map.entrySet().stream().forEach(entry -> {
                int pid = entry.getValue().getIntValue(TableModule.pid);
                map.entrySet().stream().forEach(cen -> {
                    if (cen.getValue().getIntValue(TableModule.id) == pid) {
                        ModelObject object = cen.getValue();
                        List children = object.getModelArray("children");
                        if (children == null) {
                            children = new ArrayList<ModelObject>();
                        }
                        children.add(entry.getValue());
                        cen.getValue().put("children", children);
                        removes.add(entry.getValue());
                    }
                });

                result.add(entry.getValue());
            });

            result.removeAll(removes);
            return result;
        }
        return null;
    }

    @Override
    public List<ModelObject> getTreeModules(String module, String node) {
        if (!NumberUtils.isNumber(node)) {
            node = "0";
        }
        return sessionTemplate.query(TableModule.class)
                .eq(TableModule.pid, node)
                .eq(TableModule.linkModule, module)
                .eq(TableModule.top, 0)
                .queries();
    }

    @Override
    public void addModule(ModelObject object) throws ModuleException {
        object.setObjectClass(TableModule.class);
        if (!object.containsKey(TableModule.pid)) {
            object.put(TableModule.pid, 0);
        }
        if (!object.containsKey(TableModule.module)) {
            object.put(TableModule.module, object.get(TableModule.linkModule));
        }
        if (!object.containsKey(TableModule.expanded)) {
            object.put(TableModule.expanded, 1);
        }
        if (object.getIntValue(TableModule.top) == 1) {
            object.put(TableModule.module, object.getString(TableModule.linkModule));
        }
        try {
            object.checkAndThrowable();
        } catch (ModelCheckerException e) {
            throw new ModuleException(e);
        }

        sessionTemplate.save(object);
    }

    @Override
    public void deleteModule(int id) throws ModuleException {
        ModelObject object = sessionTemplate.query(TableModule.class).eq(TableModule.id, id).query();
        if (object != null) {
            if (object.getIntValue(TableModule.isSystem) == 1) {
                throw new ModuleException(StockCode.SYSTEM_DEPEND_DATA, "系统菜单不允许删除");
            }
            List<ModelObject> modelObjects = sessionTemplate.query(TableModule.class)
                    .eq(TableModule.id, id)
                    .queries();
            if (modelObjects != null && modelObjects.size() > 0) {
                this.deleteChildModules(modelObjects);
                sessionTemplate.delete(TableModule.class, id);
            }
        }
    }

    private void deleteChildModules(List<ModelObject> childs) {
        for (ModelObject child : childs) {
            int pid = child.getIntValue(TableModule.id);
            List<ModelObject> ch = sessionTemplate.query(TableModule.class)
                    .eq(TableModule.pid, pid)
                    .queries();
            if (ch != null) {
                this.deleteChildModules(ch);
            }
            sessionTemplate.delete(TableModule.class, pid);
        }
    }

    @Override
    public void updateModule(ModelObject object) throws ModuleException {
        object.setObjectClass(TableModule.class);
        try {
            object.checkUpdateThrowable();
        } catch (ModelCheckerException e) {
            throw new ModuleException(e);
        }
        sessionTemplate.update(object);
    }

    @Override
    public void addSystemConfig(String key, String value, boolean isSystem, int pos) {
        ModelObject object = new ModelObject(TableSystemConfig.class);
        object.put(TableSystemConfig.key, key);
        object.put(TableSystemConfig.value, value);
        object.put(TableSystemConfig.pos, pos);
        object.put(TableSystemConfig.isSystem, isSystem ? 1 : 0);
        sessionTemplate.save(object);
    }

    @Override
    public void addSystemConfig(String key, String value) {
        ModelObject object = new ModelObject(TableSystemConfig.class);
        object.put(TableSystemConfig.key, key);
        object.put(TableSystemConfig.value, value);
        sessionTemplate.save(object);
    }

    @Override
    public void deleteSystemConfig(String name) {
        sessionTemplate.delete(TableSystemConfig.class, name);
    }

    @Override
    public void updateSystemConfig(String key, String value, int pos) {
        ModelObject object = new ModelObject(TableSystemConfig.class);
        object.put(TableSystemConfig.key, key);
        object.put(TableSystemConfig.value, value);
        object.put(TableSystemConfig.pos, pos);
        sessionTemplate.update(object);
    }

    @Override
    public List<ModelObject> getSystemConfigs(int start, int limit) {
        return sessionTemplate.query(TableSystemConfig.class).limit(start, limit).queries();
    }

    @Override
    public ModelObject getSystemConfig(String key) {
        return sessionTemplate.query(TableSystemConfig.class).eq(TableSystemConfig.key, key).query();
    }

    @Override
    public Paging getRoles(int start, int limit) {
        return sessionTemplate.query(TableRoles.class)
                .limit(start, limit)
                .order(TableRoles.pos, true)
                .paging();
    }

    @Override
    public void addRole(ModelObject object) throws ModuleException {
        object.setObjectClass(TableRoles.class);
        try {
            object.checkAndThrowable();
        } catch (ModelCheckerException e) {
            throw new ModuleException(e);
        }
        sessionTemplate.save(object);
    }

    @Override
    public void deleteRole(int id) {
        sessionTemplate.delete(TableRoles.class, id);
    }

    @Override
    public void updateRole(ModelObject object) throws ModuleException {
        object.setObjectClass(TableRoles.class);
        try {
            object.checkUpdateThrowable();
        } catch (ModelCheckerException e) {
            throw new ModuleException(e);
        }
        sessionTemplate.update(object);
    }

    @Override
    public List<Integer> getModuleIdByRoleId(int roleId) {
        List<ModelObject> objects = sessionTemplate.query(TableRoleModule.class)
                .eq(TableRoleModule.roleId, roleId)
                .queries();

        List<Integer> moduleIds = new ArrayList<>();
        if (objects != null) {
            objects.stream().forEach(o -> moduleIds.add(o.getIntValue(TableRoleModule.moduleId)));
        }
        return moduleIds;
    }

    @Override
    public void addModuleAndRole(int roleId, List<Integer> moduleIds) {
        sessionTemplate.delete(TableRoleModule.class)
                .eq(TableRoleModule.roleId, roleId)
                .delete();

        moduleIds.stream().forEach(id -> {
            try {
                ModelObject object = new ModelObject(TableRoleModule.class);
                object.put(TableRoleModule.roleId, roleId);
                object.put(TableRoleModule.moduleId, id);
                sessionTemplate.save(object);
            } catch (Exception e) {
            }
        });
    }

    @Override
    public void removeModuleAndRole(int roleId, int moduleId) {
        ModelObject object = new ModelObject(TableRoleModule.class);
        object.put(TableRoleModule.roleId, roleId);
        object.put(TableRoleModule.moduleId, moduleId);
        sessionTemplate.delete(object);
    }

    @Override
    public void addPageApi(ModelObject object) throws ModuleException {
        object.setObjectClass(TableModuleAuth.class);

        long count = sessionTemplate.query(TableModuleAuth.class)
                .eq(TableModuleAuth.page, object.getString(TableModuleAuth.page))
                .eq(TableModuleAuth.clazz, object.getString(TableModuleAuth.clazz))
                .eq(TableModuleAuth.method, object.getString(TableModuleAuth.method))
                .count();

        if (count <= 0) {
            try {
                object.checkAndThrowable();
            } catch (ModelCheckerException e) {
                e.getCode();
                throw new ModuleException(e);
            }

            sessionTemplate.save(object);
        }
    }

    @Override
    public void deleteModuleApis(List<Integer> ids) {
        sessionTemplate.delete(TableModuleAuth.class)
                .in(TableModuleAuth.id, ids)
                .delete();
    }

    @Override
    public List<ModelObject> getPageApis(String page) {
        return sessionTemplate.query(TableModuleAuth.class)
                .eq(TableModuleAuth.page, page)
                .queries();
    }

    @Override
    public void updatePageApi(ModelObject object) throws ModuleException {
        object.setObjectClass(TableModuleAuth.class);
        try {
            object.checkUpdateThrowable();
        } catch (ModelCheckerException e) {
            throw new ModuleException(e);
        }

        sessionTemplate.update(object);
    }

    @Override
    public ModelObject getPageApis(String clazz, String method, String page, int roleId) {

        Query query = Criteria.query(TableModuleAuth.class);
        if (roleId == -100) {
            query.eq(TableModuleAuth.method, method);
            query.eq(TableModuleAuth.clazz, clazz);
            query.eq(TableModuleAuth.page, page);
        } else {
            List<ModelObject> rms = sessionTemplate.list(Criteria.query(TableRoleModule.class).eq(TableRoleModule.roleId, roleId));
            if (rms == null) {
                return null;
            }
            List<Long> mid = new ArrayList<>();
            for (ModelObject object : rms) {
                mid.add(object.getLongValue(TableRoleModule.moduleId));
            }

            List<ModelObject> mps = sessionTemplate.list(Criteria.query(TableModule.class).in(TableModule.id, mid));
            if (mps == null) {
                return null;
            }
            List<String> pages = new ArrayList<>();
            for (ModelObject mp : mps) {
                pages.add(mp.getString(TableModule.module));
            }
            query.eq(TableModuleAuth.method, method);
            query.eq(TableModuleAuth.clazz, clazz);
            query.in(TableModuleAuth.page, pages);
        }
        query.order(TableModuleAuth.id, true);

        return sessionTemplate.get(query);
    }

    @Override
    public List<ModelObject> getRoles() {
        return sessionTemplate.query(TableRoles.class)
                .queries();
    }

}
