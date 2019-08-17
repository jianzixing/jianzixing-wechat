package com.jianzixing.webapp.admin;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.system.TableAdmin;
import com.jianzixing.webapp.tables.user.TableUser;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author yangankang
 */
@APIController
public class UserController {

    @Printer(name = "添加用户")
    public ResponseMessage addUser(ModelObject object) {
        try {
            GlobalService.userService.register(object);
        } catch (ModuleException e) {
            e.printStackTrace();
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer(name = "更新用户")
    public ResponseMessage updateUser(ModelObject object) {
        try {
            GlobalService.userService.updateUser(object);
        } catch (ModuleException e) {
            e.printStackTrace();
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer(name = "查看用户列表")
    public ResponsePageMessage getUsers(int start, int limit, ModelObject search) {
        return new ResponsePageMessage(GlobalService.userService.getUsers(start, limit, search));
    }

    @Printer(name = "删除用户")
    public ResponseMessage deleteUser(List ids) {
        GlobalService.userService.deleteUsers(ids);
        return new ResponseMessage();
    }

    @Printer(name = "订单获取用户")
    public ResponsePageMessage getOrderUsers(String keyword, int start, int limit) throws UnsupportedEncodingException {
        Query query = Criteria.query(TableUser.class);
        if (StringUtils.isNotBlank(keyword)) {
            query.like(TableUser.userName, "%" + keyword + "%");
            query.or(Criteria.filter().like(TableUser.email, "%" + keyword + "%"));
            query.or(Criteria.filter().like(TableUser.phone, "%" + keyword + "%"));
            query.or(Criteria.filter().like(TableUser.nick, "%" + keyword + "%"));
        }
        Paging paging = GlobalService.userService.getOrderUsers(query, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage getUserLevels() {
        List<ModelObject> objects = GlobalService.userLevelService.getLevels();
        return new ResponseMessage(objects);
    }

    @Printer
    public ResponseMessage addUserLevel(ModelObject object) {
        try {
            GlobalService.userLevelService.addLevel(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage deleteUserLevel(List<Integer> ids) {
        try {
            if (ids != null) {
                for (int id : ids) {
                    GlobalService.userLevelService.deleteLevel(id);
                }
            }
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateUserLevel(ModelObject object) {
        try {
            GlobalService.userLevelService.updateLevel(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage resetUserPwd(HttpServletRequest request,
                                        long uid, String adminPwd, String userPwd) {
        ModelObject admin = AdminController.getLoginUer(request);
        if (admin != null) {
            if (GlobalService.adminService.checkAdminPassword(admin.getIntValue(TableAdmin.id), adminPwd)) {
                GlobalService.userService.updateUserPassword(uid, userPwd);
                return new ResponseMessage();
            } else {
                return new ResponseMessage(-100, "管理员密码输入错误");
            }
        } else {
            return new ResponseMessage(-110, "管理员未登录");
        }
    }
}
