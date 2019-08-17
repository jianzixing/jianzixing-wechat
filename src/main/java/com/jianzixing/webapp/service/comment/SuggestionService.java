package com.jianzixing.webapp.service.comment;

import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

public interface SuggestionService {
    void addSuggestion(ModelObject object) throws ModelCheckerException;

    void delSuggestion(int id);

    Paging getSuggestions(Query query, int start, int limit);

    void replyContent(int id, String reply);
}
