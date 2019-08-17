package com.jianzixing.webapp.service.wechatsm;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.wechat.GetUserListener;
import com.jianzixing.webapp.service.wechat.WeChatUserConnector;
import com.jianzixing.webapp.service.wechat.model.AccountConfig;
import com.jianzixing.webapp.service.wechatsm.WeChatServiceManagerUtils;
import com.jianzixing.webapp.service.wechatsm.WeChatUserService;
import com.jianzixing.webapp.tables.user.TableUser;
import com.jianzixing.webapp.tables.wechat.TableWeChatUser;
import com.jianzixing.webapp.tables.wechat.TableWeChatUserTag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DefaultWeChatUserService implements WeChatUserService {
    private static final Log logger = LogFactory.getLog(DefaultWeChatUserService.class);

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public Paging getUsers(Query query, int openType, int accountId, long start, long limit) {
        if (query == null) {
            query = Criteria.query(TableWeChatUser.class);
        }
        query.setTableClass(TableWeChatUser.class);
        query.eq(TableWeChatUser.openType, openType);
        query.eq(TableWeChatUser.accountId, accountId);
        query.subjoin(TableUser.class).eq(TableUser.id, TableWeChatUser.userId).single();
        query.subjoin(TableWeChatUserTag.class).eq(TableWeChatUserTag.wcuId, TableWeChatUser.id);
        query.limit(start, limit);
        query.order(TableWeChatUser.id, false);
        return sessionTemplate.paging(query);
    }

    @Override
    public void addUser(ModelObject wcuser) throws ModelCheckerException, ModuleException, IOException {
        List tags = wcuser.getArray("tagid_list");
        wcuser.setObjectClass(TableWeChatUser.class);
        int openType = wcuser.getIntValue(TableWeChatUser.openType);
        int accountId = wcuser.getIntValue(TableWeChatUser.accountId);
        ModelObject acc = WeChatServiceManagerUtils.getAccountModel(accountId, openType);
        if (acc == null) {
            throw new ModuleException(StockCode.ARG_NULL, "没有找到公众号信息");
        }

        wcuser.put(TableWeChatUser.createTime, new Date());
        wcuser.checkAndThrowable();
        sessionTemplate.save(wcuser);
        int wcid = wcuser.getIntValue(TableWeChatUser.id);
        updateUserTags(AccountConfig.builder(openType, accountId), tags, wcid);
    }

    @Override
    public void updateUser(ModelObject wcuser) throws ModelCheckerException, IOException, ModuleException {
        List tags = wcuser.getArray("tagid_list");
        wcuser.setObjectClass(TableWeChatUser.class);
        wcuser.remove(TableWeChatUser.openType);
        wcuser.remove(TableWeChatUser.accountId);
        wcuser.remove(TableWeChatUser.createTime);
        wcuser.checkUpdateThrowable();
        sessionTemplate.update(wcuser);

        int wcid = wcuser.getIntValue(TableWeChatUser.id);
        wcuser = sessionTemplate.get(TableWeChatUser.class, wcid);
        int openType = wcuser.getIntValue(TableWeChatUser.openType);
        int accountId = wcuser.getIntValue(TableWeChatUser.accountId);

        sessionTemplate.delete(Criteria.delete(TableWeChatUserTag.class).eq(TableWeChatUserTag.wcuId, wcid));
        updateUserTags(AccountConfig.builder(openType, accountId), tags, wcid);
    }

    private void updateUserTags(AccountConfig config, List tags, int wcid) throws IOException, ModuleException {
        if (tags != null && tags.size() > 0) {
            WeChatUserConnector connector = GlobalService.weChatService.getUserConnector();

            for (Object id : tags) {
                ModelObject tagobj = ModelObject.builder(TableWeChatUserTag.class);
                tagobj.put(TableWeChatUserTag.tagid, id);
                tagobj.put(TableWeChatUserTag.wcuId, wcid);
                tagobj.put(TableWeChatUserTag.tagName, connector.getLabelName(config, id));
                sessionTemplate.saveAndUpdate(tagobj);
            }
        }
    }

    @Override
    public ModelObject updateUserByOpenid(ModelObject wcuser) throws ModelCheckerException, ModuleException {
        if (wcuser != null) {
            wcuser.setObjectClass(TableWeChatUser.class);
            String openid = wcuser.getString(TableWeChatUser.openid);
            int openType = wcuser.getIntValue(TableWeChatUser.openType);
            int accountId = wcuser.getIntValue(TableWeChatUser.accountId);

            ModelObject obj = sessionTemplate.get(
                    Criteria.query(TableWeChatUser.class)
                            .eq(TableWeChatUser.openType, openType)
                            .eq(TableWeChatUser.accountId, accountId)
                            .eq(TableWeChatUser.openid, openid));
            if (obj != null) {
                wcuser.put(TableWeChatUser.id, obj.getLongValue(TableWeChatUser.id));
                wcuser.checkUpdateThrowable();
                sessionTemplate.update(wcuser);
                logger.info("已经存在微信用户更新信息:" + openid);
            } else {
                ModelObject acc = WeChatServiceManagerUtils.getAccountModel(accountId, openType);
                if (acc == null) {
                    throw new ModuleException(StockCode.ARG_NULL, "没有找到公众号信息");
                }
                wcuser.checkAndThrowable();
                wcuser.put(TableWeChatUser.createTime, new Date());
                sessionTemplate.save(wcuser);
                obj = wcuser;
                logger.info("不存在微信用户添加信息:" + openid);
            }

            ModelObject user = GlobalService.userService.updateUserByOpenid(wcuser);
            long uid = user.getLongValue(TableUser.id);
            obj.put(TableWeChatUser.userId, uid);
            wcuser.put(TableWeChatUser.userId, uid);
            return obj;
        }
        return null;
    }

    @Override
    public ModelObject getUserByOpenId(int openType, int accountId, String openid) {
        return sessionTemplate.get(
                Criteria.query(TableWeChatUser.class)
                        .eq(TableWeChatUser.openid, openid)
                        .eq(TableWeChatUser.openType, openType)
                        .eq(TableWeChatUser.accountId, accountId)
        );
    }

    @Override
    public void syncFromWeChat(int openType, int accountId) throws Exception {
        AccountConfig config = WeChatServiceManagerUtils.createAccountConfig(openType, accountId);
        WeChatUserConnector connector = GlobalService.weChatService.getUserConnector();
        connector.getAllUsers(config, new GetUserListener() {
            @Override
            public void callback(AccountConfig config, ModelObject json) throws Exception {
                ModelObject data = json.getModelObject("data");
                if (data != null) {
                    List<String> openids = data.getArray("openid");
                    if (openids != null && openids.size() > 0) {
                        List<ModelObject> users = connector.getBatchUserInfos(config, openids);
                        if (users != null) {
                            for (ModelObject user : users) {
                                user.put(TableWeChatUser.openType, config.getType().getCode());
                                user.put(TableWeChatUser.accountId, config.getAccountId());
                                String nickName = user.getString("nickname");
                                user.put(TableWeChatUser.nickname, URLEncoder.encode(nickName, "UTF-8"));
                                user.put(TableWeChatUser.subscribeTime, user.get("subscribe_time"));
                                user.put(TableWeChatUser.subscribeScene, user.get("subscribe_scene"));
                                user.put(TableWeChatUser.qrScene, user.get("qr_scene"));
                                user.put(TableWeChatUser.qrSceneStr, user.get("qr_scene_str"));
                                String openid = user.getString(TableWeChatUser.openid);
                                ModelObject u = getUserByOpenId(config.getType().getCode(), config.getAccountId(), openid);
                                if (u == null) {
                                    addUser(user);
                                } else {
                                    user.put(TableWeChatUser.id, u.getIntValue(TableWeChatUser.id));
                                    updateUser(user);
                                }

                                if (user != null) {
                                    GlobalService.userService.updateUserByOpenid(user);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void setRemark(long id, String remark) throws IOException, ModuleException {
        ModelObject user = sessionTemplate.get(TableWeChatUser.class, id);
        int openType = user.getIntValue(TableWeChatUser.openType);
        int accountId = user.getIntValue(TableWeChatUser.accountId);
        String openid = user.getString(TableWeChatUser.openid);
        AccountConfig config = WeChatServiceManagerUtils.createAccountConfig(openType, accountId);

        ModelObject update = ModelObject.builder(TableWeChatUser.class);
        update.put(TableWeChatUser.id, id);
        update.put(TableWeChatUser.remark, remark);
        WeChatUserConnector connector = GlobalService.weChatService.getUserConnector();
        connector.setUserRemark(config, openid, remark);
        sessionTemplate.update(update);
    }

    @Override
    public void setUserLabel(int openType, int accountId, int tagid, List<Long> uids, String text) throws IOException, ModuleException {
        List<ModelObject> objects = sessionTemplate.list(Criteria.query(TableWeChatUser.class).in(TableWeChatUser.id, uids));
        if (objects != null) {
            List<String> openids = new ArrayList<>();
            for (ModelObject o : objects) {
                ModelObject delete = ModelObject.builder(TableWeChatUserTag.class);
                delete.put(TableWeChatUserTag.wcuId, o.getLongValue(TableWeChatUser.id));
                delete.put(TableWeChatUserTag.tagid, tagid);
                delete.put(TableWeChatUserTag.tagName, text);
                sessionTemplate.saveAndUpdate(delete);
                openids.add(o.getString(TableWeChatUser.openid));
            }
            WeChatUserConnector connector = GlobalService.weChatService.getUserConnector();
            connector.setUserlabel(AccountConfig.builder(openType, accountId), openids, tagid);
        }
    }

    @Override
    public void cancelUserLabel(int openType, int accountId, int tagid, List<Long> uids) throws IOException, ModuleException {
        List<ModelObject> objects = sessionTemplate.list(Criteria.query(TableWeChatUser.class).in(TableWeChatUser.id, uids));
        if (objects != null) {
            List<String> openids = new ArrayList<>();
            for (ModelObject o : objects) {
                ModelObject delete = ModelObject.builder(TableWeChatUserTag.class);
                delete.put(TableWeChatUserTag.wcuId, o.getLongValue(TableWeChatUser.id));
                delete.put(TableWeChatUserTag.tagid, tagid);
                sessionTemplate.delete(delete);
                openids.add(o.getString(TableWeChatUser.openid));
            }
            WeChatUserConnector connector = GlobalService.weChatService.getUserConnector();
            connector.cancelUserLabel(AccountConfig.builder(openType, accountId), openids, tagid);
        }
    }

    @Override
    public void updateUserRelId(long id, long uid) {
        ModelObject update = new ModelObject(TableWeChatUser.class);
        update.put(TableWeChatUser.id, id);
        update.put(TableWeChatUser.userId, uid);
        sessionTemplate.update(update);
    }
}
