package com.jianzixing.webapp.admin;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.system.TableModuleAuth;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

import java.util.List;

/**
 * @author yangankang
 */
@APIController
public class SystemController {

    @Printer(name = "查看角色列表")
    public ResponsePageMessage getRoles(int start, int limit) {
        return new ResponsePageMessage(GlobalService.systemService.getRoles(start, limit));
    }

    @Printer(name = "查看角色列表")
    public List<ModelObject> getAllRoles() {
        return GlobalService.systemService.getRoles();
    }

    @Printer(name = "删除角色")
    public ResponseMessage deleteRole(int id) {
        GlobalService.systemService.deleteRole(id);
        return new ResponseMessage();
    }

    @Printer(name = "添加角色")
    public ResponseMessage addRole(ModelObject object) {
        try {
            GlobalService.systemService.addRole(object);
        } catch (ModuleException e) {
            return new ResponseMessage(-110, e.getMessage());
        }
        return new ResponseMessage();
    }

    @Printer(name = "更新角色")
    public ResponseMessage updateRole(ModelObject object) {
        try {
            GlobalService.systemService.updateRole(object);
        } catch (ModuleException e) {
            return new ResponseMessage(-110, e.getMessage());
        }
        return new ResponseMessage();
    }

    @Printer(name = "关联角色功能模块")
    public ResponseMessage setModuleAndRole(int roleId, List moduleId) {
        GlobalService.systemService.addModuleAndRole(roleId, moduleId);
        return new ResponseMessage();
    }

    @Printer(name = "删除角色功能模块")
    public ResponseMessage removeModuleAndRole(int roleId, int moduleId) {
        GlobalService.systemService.removeModuleAndRole(roleId, moduleId);
        return new ResponseMessage();
    }

    @Printer(name = "关联功能模块API")
    public ResponseMessage addPageApi(ModelObject object) {
        try {
            String[] methods = object.getString(TableModuleAuth.method).split(",");
            for (String s : methods) {
                object.remove(TableModuleAuth.id);
                object.put(TableModuleAuth.method, s);
                GlobalService.systemService.addPageApi(object);
            }
        } catch (ModuleException e) {
            e.printStackTrace();
            return new ResponseMessage(-110, e.getMessage());
        }
        return new ResponseMessage();
    }

    @Printer(name = "删除功能模块API")
    public ResponseMessage deletePageApis(List<Integer> ids) {
        GlobalService.systemService.deleteModuleApis(ids);
        return new ResponseMessage();
    }

    @Printer(name = "更新功能模块API")
    public ResponseMessage updatePageApi(ModelObject object) {
        try {
            GlobalService.systemService.updatePageApi(object);
        } catch (ModuleException e) {
            e.printStackTrace();
            return new ResponseMessage(-110, e.getMessage());
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage getPageApis(String page) {
        List<ModelObject> objects = GlobalService.systemService.getPageApis(page);
        return new ResponseMessage(objects);
    }
}
