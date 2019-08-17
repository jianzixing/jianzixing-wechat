package com.jianzixing.webapp.admin;

import com.jianzixing.webapp.service.GlobalService;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;
import org.mimosaframework.springmvc.SearchForm;

import java.util.List;

@APIController
public class SystemConfigController {

    @Printer
    public ResponseMessage addGroup(ModelObject object) {
        try {
            GlobalService.systemConfigService.addGroup(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage delGroup(int id) {
        try {
            GlobalService.systemConfigService.delGroup(id);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateGroup(ModelObject object) {
        try {
            GlobalService.systemConfigService.updateGroup(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage getGroups() {
        List<ModelObject> objects = GlobalService.systemConfigService.getGroups();
        return new ResponseMessage(objects);
    }

    @Printer(name = "添加系统配置")
    public ResponseMessage addConfig(ModelObject object) {
        try {
            GlobalService.systemConfigService.addSystemConfig(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer(name = "删除系统配置")
    public ResponseMessage delConfig(List<String> names) {
        if (names != null) {
            for (String name : names) {
                try {
                    GlobalService.systemConfigService.delSystemConfig(name);
                } catch (Exception e) {
                    return new ResponseMessage(e);
                }
            }
        }
        return new ResponseMessage();
    }

    @Printer(name = "更新系统配置")
    public ResponseMessage updateConfig(ModelObject object) {
        try {
            GlobalService.systemConfigService.updateSystemConfig(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer(name = "查看系统配置列表")
    public ResponsePageMessage getConfigs(SearchForm form, int gid, int start, int limit) {
        Paging paging = GlobalService.systemConfigService.getSystemConfigs(form != null ? form.getQuery() : null, gid, start, limit);
        return new ResponsePageMessage(paging);
    }
}
