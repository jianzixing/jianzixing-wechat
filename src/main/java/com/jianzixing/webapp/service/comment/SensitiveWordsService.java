package com.jianzixing.webapp.service.comment;

import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

import java.util.List;

public interface SensitiveWordsService {
    void addWord(ModelObject object) throws ModelCheckerException;

    void delWord(int id);

    void updateWord(ModelObject object) throws ModelCheckerException;

    Paging getWords(Query query, int start, int limit);

    void setEnable(List<Integer> ids);

    void setDisable(List<Integer> ids);

    boolean isContains(String word);

    boolean isContainsByText(String text);

    void syncWords();
}
