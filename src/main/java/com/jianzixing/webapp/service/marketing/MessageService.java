package com.jianzixing.webapp.service.marketing;

import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;

/**
 * 站内信服务
 */
public interface MessageService {
    void send(long from, long to, String title, String content);

    void delete(long id);

    Paging getMessages(ModelObject search, long start, long limit);

    ModelObject getMessageById(long id);

    Paging getMessages(long uid, long start, long limit);
}
