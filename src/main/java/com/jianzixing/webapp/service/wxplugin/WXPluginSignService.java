package com.jianzixing.webapp.service.wxplugin;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.exception.TransactionException;

import java.util.List;
import java.util.Map;

public interface WXPluginSignService {
    void addGroup(ModelObject object) throws ModelCheckerException;

    void delGroup(int id);

    void updateGroup(ModelObject object) throws ModelCheckerException;

    List<ModelObject> getGroups(String keyword);

    void addAward(ModelObject object) throws ModelCheckerException, ModuleException;

    void delAward(int id);

    void updateAward(ModelObject object) throws ModelCheckerException;

    Paging getAwards(ModelObject search, int start, int limit);

    Paging getRecords(ModelObject search, int start, int limit);

    void sign(String groupCode, long uid) throws TransactionException, ModuleException;

    Paging getLogs(int gid, long uid, int start, int limit);

    Paging getGets(ModelObject search, int start, int limit);

    void setGetUsed(int id, int useCount) throws ModuleException;

    /**
     * 提供前端数据使用
     *
     * @param group 签到分组码
     * @param uid   用户id
     * @return
     */
    ModelObject getFrontSignModel(ModelObject group, long uid);

    ModelObject getGroupByCode(String groupCode);

    int isGroupTimeout(ModelObject group);

    void enableGroup(int id);

    void disableGroup(int id);

    List<ModelObject> getUserGets(int gid, long uid);

    Map<String, List<ModelObject>> getUserStatusGets(int gid, long uid);
}
