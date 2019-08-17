package com.jianzixing.webapp.service.system;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.tables.system.TableAdmin;
import com.jianzixing.webapp.tables.system.TableAdminDepartment;
import com.jianzixing.webapp.tables.system.TableAdminPosition;
import com.jianzixing.webapp.tables.system.TableRoles;
import org.mimosaframework.core.encryption.MD5Utils;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author yangankang
 */
@Service
public class DefaultAdminService implements AdminService {
    private static final String DEFAULT_PASSWORD = "a805b40ad7f72a8b26bfb08348384a93";

    @Autowired
    SessionTemplate sessionTemplate;


    @Override
    public void addDepartment(ModelObject department) throws ModelCheckerException {
        department.setObjectClass(TableAdminDepartment.class);
        department.checkAndThrowable();
        sessionTemplate.save(department);
    }

    @Override
    public void deleteDepartment(int id) {
        sessionTemplate.delete(TableAdminDepartment.class, id);
    }

    @Override
    public void updateDepartment(ModelObject department) throws ModelCheckerException {
        department.setObjectClass(TableAdminDepartment.class);
        department.checkUpdateThrowable();
        sessionTemplate.update(department);
    }

    @Override
    public List<ModelObject> getDepartment() {
        return sessionTemplate.list(Criteria.query(TableAdminDepartment.class));
    }

    @Override
    public void addPosition(ModelObject position) throws ModelCheckerException {
        position.setObjectClass(TableAdminPosition.class);
        position.checkAndThrowable();
        sessionTemplate.save(position);
    }

    @Override
    public void deletePosition(int id) {
        sessionTemplate.delete(TableAdminPosition.class, id);
    }

    @Override
    public void updatePosition(ModelObject position) throws ModelCheckerException {
        position.setObjectClass(TableAdminPosition.class);
        position.checkUpdateThrowable();
        sessionTemplate.update(position);
    }

    @Override
    public List<ModelObject> getPosition() {
        return sessionTemplate.list(Criteria.query(TableAdminPosition.class));
    }

    @Override
    public void addAdmin(ModelObject object) throws ModuleException {
        object.setObjectClass(TableAdmin.class);

        try {
            object.checkAndThrowable();
        } catch (ModelCheckerException e) {
            throw new ModuleException(e);
        }

        long count = sessionTemplate.query(TableAdmin.class)
                .eq(TableAdmin.userName, object.getString(TableAdmin.userName))
                .count();
        if (count > 0) {
            throw new ModuleException(StockCode.EXIST_OBJ, "用户名已存在");
        }

        sessionTemplate.save(object);
    }

    @Override
    public void deleteAdmin(int id) throws ModuleException {
        ModelObject object = this.getAdmin(id);
        if (object.getString(TableAdmin.userName).equals("admin")) {
            throw new ModuleException(StockCode.SYSTEM_MUST, "管理员用户不能删除");
        }
        sessionTemplate.delete(TableAdmin.class, id);
    }

    @Override
    public void updateAdmin(ModelObject object) throws ModuleException {
        object.setObjectClass(TableAdmin.class);
        try {
            object.checkUpdateThrowable(TableAdmin.userName, TableAdmin.password);
        } catch (ModelCheckerException e) {
            throw new ModuleException(e);
        }

        sessionTemplate.update(object);
    }

    @Override
    public Paging getAdmins(int start, int limit, ModelObject search) {
        Query query = Criteria.query(TableAdmin.class)
                .subjoin(TableAdminDepartment.class).eq(TableAdminDepartment.id, TableAdmin.departmentId).single()
                .query()
                .subjoin(TableAdminPosition.class).eq(TableAdminPosition.id, TableAdmin.positionId).single()
                .query()
                .subjoin(TableRoles.class).eq(TableRoles.id, TableAdmin.roleId).single()
                .query()
                .limit(start, limit);

        if (search != null) {
            if (search.isNotEmpty("userName")) {
                query.eq(TableAdmin.userName, search.getString("userName"));
            }
            if (search.isNotEmpty("realName")) {
                query.eq(TableAdmin.realName, search.getString("realName"));
            }
            if (search.isNotEmpty("jobNumber")) {
                query.eq(TableAdmin.jobNumber, search.getString("jobNumber"));
            }
            if (search.isNotEmpty("phoneNumber")) {
                query.eq(TableAdmin.phoneNumber, search.getString("phoneNumber"));
            }
            if (search.isNotEmpty("email")) {
                query.eq(TableAdmin.email, search.getString("email"));
            }
        }

        return sessionTemplate.paging(query);
    }

    @Override
    public long countAdmin() {
        return sessionTemplate.query(TableAdmin.class).count();
    }

    @Override
    public ModelObject getAdmin(int id) {
        return sessionTemplate.get(TableAdmin.class, id);
    }

    @Override
    public ModelObject getAdminByUserName(String userName) {
        return sessionTemplate.query(TableAdmin.class)
                .eq(TableAdmin.userName, userName)
                .query();
    }

    @Override
    public void updateAdminPassword(int id, String password) {
        ModelObject object = new ModelObject(TableAdmin.class);
        object.put(TableAdmin.id, id);
        object.put(TableAdmin.password, this.getPasswordString(password));
        sessionTemplate.update(object);
    }

    @Override
    public String getPasswordString(String password) {
        return MD5Utils.md5("[" + password + "]").toUpperCase();
    }

    @Override
    public void updateLoginInfo(int id, String ip, Date date) {
        ModelObject object = new ModelObject(TableAdmin.class);
        object.put(TableAdmin.id, id);
        object.put(TableAdmin.lastLoginIp, ip);
        object.put(TableAdmin.lastLoginTime, date);
        sessionTemplate.update(object);
    }

    @Override
    public void updateEditUser(int editUser, int uid, Date date) {
        ModelObject object = new ModelObject(TableAdmin.class);
        object.put(TableAdmin.id, uid);
        object.put(TableAdmin.editUser, editUser);
        object.put(TableAdmin.editTime, date);
        sessionTemplate.update(object);
    }

    @Override
    public List<ModelObject> getAdmins(int start, int limit) {
        Query query = Criteria.query(TableAdmin.class).eq(TableAdmin.isValid, 1).limit(start, limit);
        return sessionTemplate.list(query);
    }

    @Override
    public boolean checkAdminPassword(int aid, String adminPwd) {
        ModelObject admin = sessionTemplate.get(Criteria.query(TableAdmin.class)
                .eq(TableAdmin.id, aid)
                .eq(TableAdmin.isValid, 1));
        if (admin != null) {
            String password = admin.getString(TableAdmin.password);
            if (password.equalsIgnoreCase(this.getPasswordString(adminPwd))) {
                return true;
            }
        }
        return false;
    }

}
