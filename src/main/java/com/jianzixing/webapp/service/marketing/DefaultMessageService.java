package com.jianzixing.webapp.service.marketing;

import com.jianzixing.webapp.tables.marketing.TableMarketMessage;
import com.jianzixing.webapp.tables.user.TableUser;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DefaultMessageService implements MessageService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void send(long from, long to, String title, String content) {
        ModelObject message = new ModelObject(TableMarketMessage.class);
        message.put(TableMarketMessage.from, from);
        message.put(TableMarketMessage.target, to);
        message.put(TableMarketMessage.title, title);
        message.put(TableMarketMessage.content, content);
        message.put(TableMarketMessage.createTime, new Date());
        sessionTemplate.save(message);
    }

    @Override
    public void delete(long id) {
        sessionTemplate.delete(Criteria.delete(TableMarketMessage.class)
                .eq(TableMarketMessage.id, id));
    }

    @Override
    public Paging getMessages(ModelObject search, long start, long limit) {
        Query query = Criteria.query(TableMarketMessage.class);
        query.subjoin(TableUser.class).eq(TableUser.id, TableMarketMessage.from).single().aliasName("fromUser");
        query.subjoin(TableUser.class).eq(TableUser.id, TableMarketMessage.target).single().aliasName("targetUser");
        query.excludes(TableMarketMessage.content);
        query.fields(TableUser.class, TableUser.id, TableUser.userName, TableUser.nick);
        query.limit(start, limit);
        return sessionTemplate.paging(query);
    }

    @Override
    public ModelObject getMessageById(long id) {
        return sessionTemplate.get(Criteria.query(TableMarketMessage.class)
                .eq(TableMarketMessage.id, id));
    }

    @Override
    public Paging getMessages(long uid, long start, long limit) {
        return sessionTemplate.paging(Criteria.query(TableMarketMessage.class)
                .eq(TableMarketMessage.target, uid)
                .limit(start, limit));
    }
}
