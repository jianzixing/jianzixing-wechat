package com.jianzixing.webapp.admin;

import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.handler.AuthSkipCheck;
import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.system.TableAdmin;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * @author yangankang
 */
@APIController
public class ModuleController {

    @AuthSkipCheck
    @Printer(name = "查看所有功能模块")
    public ResponseMessage getModules() {
        return new ResponseMessage(GlobalService.systemService.getTopModules());
    }

    @AuthSkipCheck
    @Printer(name = "查看功能模块")
    public ResponseMessage getTreeModules(HttpServletRequest request, String module) {
        ModelObject object = AdminController.getLoginUer(request);
        int roleId = object.getIntValue(TableAdmin.roleId);
        if (AdminController.isSuperUser(request)) {
            roleId = -100;
        }
        return new ResponseMessage(GlobalService.systemService.getTreeModules(module, true, roleId));
    }

    @Printer(name = "查看子功能模块")
    public ResponseMessage getFullTreeModules(String module) {
        return new ResponseMessage(GlobalService.systemService.getTreeModules(module, false, 0));
    }

    @Printer(name = "查看所有功能模块")
    public ResponseMessage getAllTreeModules() {
        return new ResponseMessage(GlobalService.systemService.getTreeModules(null, false, 0));
    }

    @Printer(name = "查看角色功能模块")
    public ResponseMessage getRoleTreeModules(int roleId) {
        return new ResponseMessage(GlobalService.systemService.getTreeModules(null, true, roleId));
    }

    @Printer(name = "查看角色功能模块")
    public ResponseMessage getRoleTreeList(int roleId) {
        return new ResponseMessage(GlobalService.systemService.getTreeList(roleId));
    }

    @Printer(name = "查看顶级模块")
    public ResponsePageMessage getTopModules() {
        List list = GlobalService.systemService.getTopModules();
        return new ResponsePageMessage(list.size(), list);
    }

    @Printer(name = "查看页面功能模块")
    public List<ModelObject> getPageTreeModules(String module, String node) {
        return GlobalService.systemService.getTreeModules(module, node);
    }

    @Printer(name = "删除功能模块")
    public ResponseMessage deleteTopModule(int id) {
        try {
            GlobalService.systemService.deleteModule(id);
        } catch (ModuleException e) {
            if (e.getCode().equals(StockCode.SYSTEM_DEPEND_DATA)) {
                return new ResponseMessage(e.getCode(), "系统菜单不允许删除");
            }
        }
        return new ResponseMessage();
    }

    @Printer(name = "更新功能模块")
    public ResponseMessage updateModule(ModelObject object) {
        try {
            GlobalService.systemService.updateModule(object);
        } catch (ModuleException e) {
            e.printStackTrace();
            return new ResponseMessage(101, "[处理失败]" + e.getMessage());
        }
        return new ResponseMessage();
    }

    @Printer(name = "添加功能模块")
    public ResponseMessage addModule(ModelObject object) {
        try {
            GlobalService.systemService.addModule(object);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseMessage(101, "[处理失败]" + e.getMessage());
        }
        return new ResponseMessage();
    }

}
