package com.jianzixing.webapp.service.comment;

import com.jianzixing.webapp.tables.comment.TableSensitiveWords;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class DefaultSensitiveWordsService implements SensitiveWordsService {
    private static final Log logger = LogFactory.getLog(DefaultSensitiveWordsService.class);
    private static final List<String> WORDS = new CopyOnWriteArrayList<>();

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addWord(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableSensitiveWords.class);
        object.checkAndThrowable();

        sessionTemplate.save(object);
    }

    @Override
    public void delWord(int id) {
        sessionTemplate.delete(TableSensitiveWords.class, id);
    }

    @Override
    public void updateWord(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableSensitiveWords.class);
        object.checkUpdateThrowable();

        sessionTemplate.update(object);
    }

    @Override
    public Paging getWords(Query query, int start, int limit) {
        if (query == null) {
            query = Criteria.query(TableSensitiveWords.class);
        }
        query.order(TableSensitiveWords.id, false);
        query.limit(start, limit);
        return sessionTemplate.paging(query);
    }

    @Override
    public void setEnable(List<Integer> ids) {
        sessionTemplate.update(Criteria.update(TableSensitiveWords.class).in(TableSensitiveWords.id, ids).value(TableSensitiveWords.isEnable, 1));
    }

    @Override
    public void setDisable(List<Integer> ids) {
        sessionTemplate.update(Criteria.update(TableSensitiveWords.class).in(TableSensitiveWords.id, ids).value(TableSensitiveWords.isEnable, 0));
    }


    @Override
    public boolean isContains(String word) {
        ModelObject object = sessionTemplate.get(Criteria.query(TableSensitiveWords.class)
                .eq(TableSensitiveWords.isEnable, 1)
                .eq(TableSensitiveWords.text, word.trim()).limit(0, 1));

        if (object == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean isContainsByText(String text) {
        for (String w : WORDS) {
            if (text.contains(w)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void syncWords() {
        try {
            WORDS.clear();
            List<ModelObject> objects = sessionTemplate.list(Criteria.query(TableSensitiveWords.class).eq(TableSensitiveWords.isEnable, 1));
            if (objects != null) {
                for (ModelObject object : objects) {
                    WORDS.add(object.getString(TableSensitiveWords.text));
                }
            }
        } catch (Exception e) {
            logger.error("同步敏感字出错", e);
        }
    }
}
