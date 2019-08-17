package com.jianzixing.webapp.service.wechatsm;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

import java.util.List;

public interface WeChatReplyService {
    void addReply(ModelObject object) throws ModelCheckerException, ModuleException;

    void deleteReply(int id);

    Paging getReplys(Query query, int openType, int accountId, int start, int limit);

    List<ModelObject> getTypeCount();

    void updateReply(ModelObject object) throws ModelCheckerException, ModuleException;

    ModelObject getReplyMaterial(String id);
}
