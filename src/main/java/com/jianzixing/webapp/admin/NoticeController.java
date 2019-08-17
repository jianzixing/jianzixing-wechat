package com.jianzixing.webapp.admin;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.notice.TableNotice;
import com.jianzixing.webapp.tables.system.TableAdmin;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@APIController
public class NoticeController {
    @Printer(name = "查看公告列表")
    public ResponsePageMessage getNotice(int start, int limit) {
        ModelObject modelObject = new ModelObject();
        modelObject.setObjectClass(TableNotice.class);
        Paging objects = GlobalService.noticeService.getPage(start, limit, modelObject);

        return new ResponsePageMessage(objects.getCount(), objects.getObjects());
    }

    @Printer(name = "添加公告")
    public ResponseMessage addNotice(HttpServletRequest request, ModelObject object) {
        try {
            ModelObject user = getLoginUer(request);
            object.put(TableNotice.adminId, user.getInteger(TableAdmin.id));
            object.put(TableNotice.editTime, new Date());
            GlobalService.noticeService.add(object);
        } catch (ModuleException e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer(name = "删除公告")
    public ResponseMessage deleteNotice(List<Integer> ids) {
        for (int id : ids) {
            try {
                GlobalService.noticeService.delete(id);
            } catch (ModuleException e) {
                return new ResponseMessage(e);
            }
        }
        return new ResponseMessage();
    }

    ModelObject getLoginUer(HttpServletRequest request) {
        return (ModelObject) request.getSession().getAttribute(AdminController.LOGIN_SESS_STR);
    }

    @Printer(name = "更新公告")
    public ResponseMessage updateNotice(HttpServletRequest request, ModelObject object) {
        ModelObject user = getLoginUer(request);
        try {
            object.put(TableNotice.adminId, user.getInteger(TableAdmin.id));
            GlobalService.noticeService.update(object);
        } catch (ModuleException e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }
}
