package com.jianzixing.webapp.service.user;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yangankang
 */
public interface UserService {

    void register(ModelObject object) throws ModuleException;

    ModelObject login(String fixUserName, String password) throws ModuleException;

    /**
     * 只提供登录所需的信息，不校验用户名和密码
     * ps: 提供给其他登录方式使用
     *
     * @param uid
     * @return
     */
    ModelObject silenceLogin(long uid) throws ModuleException;

    boolean isExist(String userName, String email, String phone);

    void updateLastLoginTime(long uid);

    ModelObject getUser(String userName, String email, String phone);

    ModelObject getUserByUserName(String userName);

    ModelObject getUserByEmail(String email);

    ModelObject getUserByPhone(String phone);

    void setEmailValid(long uid, boolean isValid);

    void setPhoneValid(long uid, boolean isValid);

    ModelObject getUser(long uid);

    String getEncodePassword(long uid, String password);

    void updateUser(ModelObject object) throws ModuleException;

    Paging getUsers(long start, long limit, ModelObject search);

    void deleteUsers(List ids);

    Paging getOrderUsers(Query query, long start, long limit);

    ModelObject updateUserByOpenid(ModelObject wcuser);

    ModelObject getUserByOpenid(int openType, int accountId, String openid);

    /**
     * 更新用户等级分数，amount如果是正数则是添加如果是负数就是减少
     *
     * @param uid
     * @param amount
     * @param msg
     */
    void updateUserLevelAmount(long uid, int amount, String msg);

    void registerByPhone(String phone, String password);

    void updateUserPassword(long id, String password);

    long getUserCount();
}
