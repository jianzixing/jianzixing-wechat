package com.jianzixing.webapp.service.wxplugin;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;

public interface WXPluginVotingService {
    void addGroup(ModelObject group) throws ModelCheckerException;

    void delGroup(int id);

    void updateGroup(ModelObject group) throws ModelCheckerException;

    Paging getGroups(ModelObject search, int start, int limit);

    void addItem(ModelObject item) throws ModelCheckerException, ModuleException;

    void delItem(int id);

    void updateItem(ModelObject item) throws ModelCheckerException, ModuleException;

    Paging getItems(ModelObject search, int start, int limit);

    int voting(int iid, long uid);

    ModelObject getGroupByCode(String groupCode);

    int isGroupTimeout(ModelObject group);

    ModelObject getFrontVotingModel(ModelObject group, long uid);

    ModelObject getItemById(int id);
}
