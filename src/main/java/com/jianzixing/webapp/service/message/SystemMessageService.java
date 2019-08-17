package com.jianzixing.webapp.service.message;

import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

import java.util.List;

/**
 * 后台管理平台，管理员之间或者系统消息
 */
public interface SystemMessageService {
    void sendSystemMessage(ModelObject object, List<Integer> adminIds) throws ModelCheckerException;

    void sendAllSystemMessage(ModelObject object) throws ModelCheckerException;

    void delSystemMessage(List<Integer> ids);

    Paging getSystemMessages(ModelObject search, int adminId, int start, int limit);

    void markRead(int adminId, List<Integer> ids);

    void markAllRead(int adminId);

    ModelObject getNotShows(int adminId, int count);

    ModelObject getReadMessage(int adminId, int id);
}
