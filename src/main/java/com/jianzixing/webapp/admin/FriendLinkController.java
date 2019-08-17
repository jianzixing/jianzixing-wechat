package com.jianzixing.webapp.admin;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.cooperation.TableFriendLink;
import com.jianzixing.webapp.tables.system.TableAdmin;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@APIController
public class FriendLinkController {
    @Printer(name = "查看友情链接")
    public ResponsePageMessage getFriendLink(int start, int limit) {
        Paging objects = GlobalService.friendLinkService.getFriendLink(start, limit);

        return new ResponsePageMessage(objects.getCount(), objects.getObjects());
    }

    @Printer(name = "添加友情链接")
    public ResponseMessage addFriendLink(HttpServletRequest request, ModelObject object) {
        try {
            ModelObject user = getLoginUer(request);
            object.put(TableFriendLink.adminId, user.getInteger(TableAdmin.id));
            object.put(TableFriendLink.createTime, new Date());
            GlobalService.friendLinkService.addFriendLink(object);
        } catch (ModelCheckerException e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer(name = "删除友情链接")
    public ResponseMessage deleteFriendLink(List<Integer> ids) {
        for (int id : ids) {
            GlobalService.friendLinkService.delFriendLink(id);
        }
        return new ResponseMessage();
    }

    ModelObject getLoginUer(HttpServletRequest request) {
        return (ModelObject) request.getSession().getAttribute(AdminController.LOGIN_SESS_STR);
    }

    @Printer(name = "更新友情链接")
    public ResponseMessage updateFriendLink(HttpServletRequest request, ModelObject object) {
        ModelObject user = getLoginUer(request);
        try {
            object.put(TableFriendLink.adminId, user.getInteger(TableAdmin.id));
            GlobalService.friendLinkService.updateFriendLink(object);
        } catch (ModelCheckerException e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }
}
