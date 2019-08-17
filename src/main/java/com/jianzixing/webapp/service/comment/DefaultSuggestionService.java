package com.jianzixing.webapp.service.comment;

import com.jianzixing.webapp.tables.comment.TableSuggestionComment;
import com.jianzixing.webapp.tables.user.TableUser;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DefaultSuggestionService implements SuggestionService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addSuggestion(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableSuggestionComment.class);
        object.checkAndThrowable();

        object.put(TableSuggestionComment.createTime, new Date());
        sessionTemplate.save(object);
    }

    @Override
    public void delSuggestion(int id) {
        sessionTemplate.delete(TableSuggestionComment.class, id);
    }

    @Override
    public Paging getSuggestions(Query query, int start, int limit) {
        if (query == null) {
            query = Criteria.query(TableSuggestionComment.class);
        }
        query.subjoin(TableUser.class).eq(TableUser.id, TableSuggestionComment.userId).single();
        query.order(TableSuggestionComment.id, false);
        query.limit(start, limit);
        return sessionTemplate.paging(query);
    }

    @Override
    public void replyContent(int id, String reply) {
        ModelObject object = new ModelObject(TableSuggestionComment.class);
        object.put(TableSuggestionComment.id, id);
        object.put(TableSuggestionComment.reply, reply);
        sessionTemplate.update(object);
    }
}
