package com.jianzixing.webapp.service.user;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.integral.TableIntegral;
import com.jianzixing.webapp.tables.user.TableUser;
import com.jianzixing.webapp.tables.user.TableUserLevel;
import com.jianzixing.webapp.tables.wechat.TableWeChatUser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.mimosaframework.core.encryption.MD5Utils;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.RandomUtils;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.mimosaframework.orm.utils.ModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author yangankang
 */
@Service
public class DefaultUserService implements UserService {
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

    @Autowired
    private SessionTemplate sessionTemplate;

    @Override
    public void register(ModelObject object) throws ModuleException {
        String password = object.getString(TableUser.password);
        this.setRegisterPassword(object);
        object.setObjectClass(TableUser.class);
        try {
            object.checkAndThrowable();
        } catch (ModelCheckerException e) {
            throw new ModuleException(e);
        }

        String nick = object.getString(TableUser.nick);
        if (StringUtils.isNotBlank(nick)) {
            try {
                object.put(TableUser.nick, URLEncoder.encode(nick, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        String userName = object.getString(TableUser.userName);
        String email = object.getString(TableUser.email);
        String phone = object.getString(TableUser.phone);

        if (NumberUtils.isNumber(userName)) {
            throw new ModuleException(StockCode.FAILURE, "用户名不允许是纯数字");
        }

        if (!userName.matches("[a-zA-Z][a-zA-Z0-9]{5,30}")) {
            throw new ModuleException(StockCode.FAILURE, "用户名只允许字母开头6-30位字母或者数字");
        }

        if (!isExist(userName, email, phone)) {
            object.put(TableUser.registerTime, new Date());
            sessionTemplate.save(object);
        } else {
            throw new ModuleException(StockCode.EXIST_OBJ, "用户已经存在");
        }
    }

    @Override
    public ModelObject login(String fixUserName, String password) throws ModuleException {
        ModelObject user = null;
        if (NumberUtils.isNumber(fixUserName)) {
            user = sessionTemplate.get(Criteria.query(TableUser.class).eq(TableUser.phone, fixUserName));
        }
        if (user == null && fixUserName.indexOf("@") > 0) {
            user = sessionTemplate.get(Criteria.query(TableUser.class).eq(TableUser.email, fixUserName));
        }
        if (user == null) {
            user = sessionTemplate.get(Criteria.query(TableUser.class).eq(TableUser.userName, fixUserName));
        }

        if (user != null) {
            String pwd = this.getUserPassword(user, password);
            if (pwd.equals(user.getString(TableUser.password))) {
                ModelObject update = new ModelObject(TableUser.class);
                update.put(TableUser.id, user.getLongValue(TableUser.id));
                update.put(TableUser.token, RandomUtils.uuid());
                update.put(TableUser.lastLoginTime, new Date());
                sessionTemplate.update(update);
                user.put(TableUser.token, update.getString(TableUser.token));
                return user;
            } else {
                throw new ModuleException(StockCode.ARG_VALID, "用户名密码不正确");
            }
        } else {
            throw new ModuleException(StockCode.ARG_NULL, "用户不存在");
        }
    }

    @Override
    public ModelObject silenceLogin(long uid) throws ModuleException {
        ModelObject user = sessionTemplate.get(Criteria.query(TableUser.class)
                .eq(TableUser.id, uid).eq(TableUser.enable, 1));
        if (user != null) {
            ModelObject update = new ModelObject(TableUser.class);
            update.put(TableUser.id, user.getLongValue(TableUser.id));
            update.put(TableUser.token, RandomUtils.uuid());
            update.put(TableUser.lastLoginTime, new Date());
            sessionTemplate.update(update);
            user.put(TableUser.token, update.getString(TableUser.token));
        } else {
            throw new ModuleException(StockCode.ARG_NULL, "用户" + uid + "不存在");
        }
        return user;
    }

    private String getUserPassword(ModelObject user, String password) {
        String salt = user.getString(TableUser.signature);
        if (StringUtils.isNotBlank(salt)) {
            String[] s = salt.split(",");
            String saltPassword = s[0] + password + s[1];
            return MD5Utils.md5(saltPassword);
        }
        return null;
    }

    private void setRegisterPassword(ModelObject user) {
        String password = user.getString(TableUser.password);
        String[] s = UUID.randomUUID().toString().split("-");
        String salt = s[0] + "," + s[1];
        String saltPassword = s[0] + password + s[1];
        user.put(TableUser.signature, salt);
        user.put(TableUser.password, MD5Utils.md5(saltPassword));
    }

    @Override
    public boolean isExist(String userName, String email, String phone) {
        if (StringUtils.isNotBlank(userName)) {
            List list = sessionTemplate.list(Criteria.query(TableUser.class).addFilter().eq(TableUser.userName, userName).query());
            if (list != null && list.size() > 0) {
                return true;
            }
        }

        if (StringUtils.isNotBlank(email)) {
            List list = sessionTemplate.list(Criteria.query(TableUser.class).addFilter().eq(TableUser.email, email).query());
            if (list != null && list.size() > 0) {
                return true;
            }
        }

        if (StringUtils.isNotBlank(phone)) {
            List list = sessionTemplate.list(Criteria.query(TableUser.class).addFilter().eq(TableUser.phone, phone).query());
            if (list != null && list.size() > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateLastLoginTime(long uid) {
        ModelObject update = new ModelObject(TableUser.class);
        update.put(TableUser.id, uid);
        update.put(TableUser.lastLoginTime, new Date());
        sessionTemplate.update(update);
    }

    @Override
    public ModelObject getUser(String userName, String email, String phone) {
        if (StringUtils.isNotBlank(userName)) {
            ModelObject object = sessionTemplate.get(Criteria.query(TableUser.class).addFilter().eq(TableUser.userName, userName).query());
            return object;
        }

        if (StringUtils.isNotBlank(email)) {
            ModelObject object = sessionTemplate.get(Criteria.query(TableUser.class).addFilter().eq(TableUser.email, email).query());
            return object;
        }

        if (StringUtils.isNotBlank(phone)) {
            ModelObject object = sessionTemplate.get(Criteria.query(TableUser.class).addFilter().eq(TableUser.phone, phone).query());
            return object;
        }
        return null;
    }

    @Override
    public ModelObject getUserByUserName(String userName) {
        if (StringUtils.isNotBlank(userName)) {
            ModelObject object = sessionTemplate.get(Criteria.query(TableUser.class).addFilter().eq(TableUser.userName, userName).query());
            return object;
        }
        return null;
    }

    @Override
    public ModelObject getUserByEmail(String email) {
        if (StringUtils.isNotBlank(email)) {
            ModelObject object = sessionTemplate.get(Criteria.query(TableUser.class).addFilter().eq(TableUser.email, email).query());
            return object;
        }
        return null;
    }

    @Override
    public ModelObject getUserByPhone(String phone) {
        if (StringUtils.isNotBlank(phone)) {
            ModelObject object = sessionTemplate.get(
                    Criteria.query(TableUser.class)
                            .eq(TableUser.phone, phone.trim()));
            return object;
        }
        return null;
    }

    @Override
    public void setEmailValid(long uid, boolean isValid) {
        ModelObject object = new ModelObject(TableUser.class);
        object.put(TableUser.id, uid);
        object.put(TableUser.validEmail, isValid ? 1 : 0);
        sessionTemplate.update(object);
    }

    @Override
    public void setPhoneValid(long uid, boolean isValid) {
        ModelObject object = new ModelObject(TableUser.class);
        object.put(TableUser.id, uid);
        object.put(TableUser.validPhone, isValid ? 1 : 0);
        sessionTemplate.update(object);
    }

    @Override
    public ModelObject getUser(long uid) {
        return sessionTemplate.get(Criteria.query(TableUser.class).eq(TableUser.id, uid).eq(TableUser.enable, 1));
    }

    @Override
    public String getEncodePassword(long uid, String password) {
        ModelObject object = this.getUser(uid);
        String salt = object.getString(TableUser.signature);
        String[] s = salt.split(",");

        return MD5Utils.md5(s[0] + password + s[1]);
    }

    @Override
    public void updateUser(ModelObject object) throws ModuleException {
        object.setObjectClass(TableUser.class);
        object.remove(TableUser.userName);
        object.remove(TableUser.password);
        object.remove(TableUser.validEmail);
        object.remove(TableUser.validPhone);
        object.remove(TableUser.signature);

        String nick = object.getString(TableUser.nick);
        if (StringUtils.isNotBlank(nick)) {
            try {
                object.put(TableUser.nick, URLEncoder.encode(nick, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        try {
            object.checkUpdateThrowable(TableUser.userName, TableUser.password, TableUser.validEmail,
                    TableUser.validPhone, TableUser.signature);
        } catch (ModelCheckerException e) {
            throw new ModuleException(e);
        }

        sessionTemplate.update(object);
    }

    @Override
    public Paging getUsers(long start, long limit, ModelObject search) {
        Query query = Criteria.query(TableUser.class)
                .subjoin(TableIntegral.class).eq(TableIntegral.userId, TableUser.id).single().query()
                .subjoin(TableUserLevel.class).eq(TableUserLevel.id, TableUser.levelId).single().query()
                .limit(start, limit);
        ModelUtils.setLikeUrlEncodeSearch(search, "nick");
        return ModelUtils.getSearch("user", sessionTemplate, search, query, TableUser.id);
    }

    @Override
    public void deleteUsers(List ids) {
        if (ids != null && ids.size() > 0) {
            sessionTemplate.delete(TableUser.class)
                    .in(TableUser.id, ids)
                    .delete();
        }
    }

    @Override
    public Paging getOrderUsers(Query query, long start, long limit) {
        if (query == null) {
            Criteria.query(TableUser.class);
        }
        query.eq(TableUser.enable, 1);
        query.setTableClass(TableUser.class);
        query.limit(start, limit);
        return sessionTemplate.paging(query);
    }

    @Override
    public ModelObject updateUserByOpenid(ModelObject wcuser) {
        int openType = wcuser.getIntValue(TableWeChatUser.openType);
        int accountId = wcuser.getIntValue(TableWeChatUser.accountId);
        String openid = wcuser.getString(TableWeChatUser.openid);

        ModelObject user = sessionTemplate.get(Criteria.query(TableUser.class)
                .eq(TableUser.openType, openType)
                .eq(TableUser.accountId, accountId)
                .eq(TableUser.openid, openid));
        if (user != null) {
            ModelObject update = new ModelObject(TableUser.class);
            update.put(TableUser.id, user.getLongValue(TableUser.id));
            update.put(TableUser.nick, wcuser.getString(TableWeChatUser.nickname));
            update.put(TableUser.gender, wcuser.get(TableWeChatUser.sex));
            update.put(TableUser.avatar, wcuser.getString(TableWeChatUser.headimgurl));
            sessionTemplate.update(update);
        } else {
            String u = "U" + format.format(new Date()) + RandomUtils.randomNumber(6);
            String signature1 = RandomUtils.randomIgnoreCaseLetter(6);
            String signature2 = RandomUtils.randomIgnoreCaseLetter(6);
            String pwd = signature1 + u + signature2;
            user = new ModelObject(TableUser.class);
            user.put(TableUser.openType, openType);
            user.put(TableUser.accountId, accountId);
            user.put(TableUser.openid, openid);
            user.put(TableUser.userName, u);
            user.put(TableUser.signature, signature1 + "," + signature2);
            user.put(TableUser.password, MD5Utils.md5(pwd));
            user.put(TableUser.nick, wcuser.getString(TableWeChatUser.nickname));
            user.put(TableUser.gender, wcuser.get(TableWeChatUser.sex));
            user.put(TableUser.registerTime, new Date());
            user.put(TableUser.avatar, wcuser.getString(TableWeChatUser.headimgurl));
            sessionTemplate.save(user);
        }
        long id = wcuser.getLongValue(TableWeChatUser.id);
        GlobalService.weChatUserService.updateUserRelId(id, user.getLongValue(TableUser.id));
        return user;
    }

    @Override
    public ModelObject getUserByOpenid(int openType, int accountId, String openid) {
        return sessionTemplate.get(
                Criteria.query(TableUser.class)
                        .eq(TableUser.openType, openType)
                        .eq(TableUser.accountId, accountId)
                        .eq(TableUser.openid, openid)
                        .eq(TableUser.enable, 1)
        );
    }

    @Override
    public void updateUserLevelAmount(long uid, int amount, String msg) {
        ModelObject obj = sessionTemplate.get(TableUser.class, uid);
        if (obj != null) {
            long before = obj.getLongValue(TableUser.levelAmount);
            long after = before + amount;
            if (amount < 0) {
                sessionTemplate.update(Criteria.update(TableUser.class).subSelf(TableUser.levelAmount, Math.abs(amount)).eq(TableUser.id, uid));
            } else {
                sessionTemplate.update(Criteria.update(TableUser.class).addSelf(TableUser.levelAmount, amount).eq(TableUser.id, uid));
            }
            long levelAmount = obj.getLongValue(TableUser.levelAmount);
            ModelObject level = GlobalService.userLevelService.getLevelByAmount(levelAmount);
            if (level != null) {
                ModelObject update = new ModelObject(TableUser.class);
                update.put(TableUser.id, obj.getLongValue(TableUser.id));
                update.put(TableUser.levelId, level.getIntValue(TableUserLevel.id));
                sessionTemplate.update(update);
            }

            GlobalService.userLevelService.setLevelAmountChange(uid, amount, before, after, msg);
        }
    }

    @Override
    public void registerByPhone(String phone, String password) {
        ModelObject user = new ModelObject(TableUser.class);
        user.put(TableUser.userName, phone);
        user.put(TableUser.phone, phone);
        this.setRegisterPassword(user);
        user.put(TableUser.validPhone, 1);
        sessionTemplate.save(user);
    }

    @Override
    public void updateUserPassword(long id, String password) {
        ModelObject user = new ModelObject(TableUser.class);
        user.put(TableUser.id, id);
        user.put(TableUser.password, password);
        this.setRegisterPassword(user);
        sessionTemplate.update(user);
    }

    @Override
    public long getUserCount() {
        return sessionTemplate.count(Criteria.query(TableUser.class));
    }
}
