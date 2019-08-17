package com.jianzixing.webapp.service.system;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;

import java.util.Date;
import java.util.List;

/**
 * @author yangankang
 */
public interface AdminService {

    /*****部门管理*****/
    void addDepartment(ModelObject department) throws ModelCheckerException;

    void deleteDepartment(int id);

    void updateDepartment(ModelObject department) throws ModelCheckerException;

    List<ModelObject> getDepartment();

    /*****职位管理*****/
    void addPosition(ModelObject position) throws ModelCheckerException;

    void deletePosition(int id);

    void updatePosition(ModelObject position) throws ModelCheckerException;

    List<ModelObject> getPosition();

    /*****管理员管理*****/

    void addAdmin(ModelObject object) throws ModuleException;

    void deleteAdmin(int id) throws ModuleException;

    void updateAdmin(ModelObject object) throws ModuleException;

    Paging getAdmins(int start, int limit, ModelObject search);

    long countAdmin();

    ModelObject getAdmin(int id);

    ModelObject getAdminByUserName(String userName);

    void updateAdminPassword(int id, String password);

    String getPasswordString(String password);

    void updateLoginInfo(int id, String ip, Date date);

    void updateEditUser(int editUser, int uid, Date date);

    List<ModelObject> getAdmins(int start, int limit);

    boolean checkAdminPassword(int aid, String adminPwd);
}
