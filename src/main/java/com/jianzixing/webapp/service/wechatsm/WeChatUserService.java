package com.jianzixing.webapp.service.wechatsm;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

import java.io.IOException;
import java.util.List;

public interface WeChatUserService {
    Paging getUsers(Query query, int openType, int accountId, long start, long limit);

    void addUser(ModelObject wcuser) throws ModelCheckerException, ModuleException, IOException;

    void updateUser(ModelObject wcuser) throws ModelCheckerException, IOException, ModuleException;

    /**
     * 更新TableWeChatUser表并返回
     *
     * @param wcuser
     * @return
     * @throws ModelCheckerException
     * @throws ModuleException
     */
    ModelObject updateUserByOpenid(ModelObject wcuser) throws ModelCheckerException, ModuleException;

    /**
     * todo:这里是否将公众号和开放平台管理的公众号区分开
     *
     * @param openType
     * @param accountId
     * @param openid
     * @return
     */
    ModelObject getUserByOpenId(int openType, int accountId, String openid);

    void syncFromWeChat(int openType, int accountId) throws Exception;

    void setRemark(long id, String remark) throws IOException, ModuleException;

    void setUserLabel(int openType, int accountId, int tagid, List<Long> uids, String text) throws IOException, ModuleException;

    void cancelUserLabel(int openType, int accountId, int tagid, List<Long> uids) throws IOException, ModuleException;

    void updateUserRelId(long id, long uid);
}
