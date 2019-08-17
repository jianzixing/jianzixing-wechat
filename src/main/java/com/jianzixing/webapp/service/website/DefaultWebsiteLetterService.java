package com.jianzixing.webapp.service.website;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.tables.website.TableWebsiteLetter;
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
public class DefaultWebsiteLetterService implements WebsiteLetterService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void sendLetter(ModelObject object) throws ModelCheckerException, ModuleException {
        object.setObjectClass(TableWebsiteLetter.class);
        object.checkAndThrowable();
        object.put(TableWebsiteLetter.createTime, new Date());
        sessionTemplate.save(object);
    }

    @Override
    public void deleteFlagLetter(long id) {
        ModelObject update = new ModelObject(TableWebsiteLetter.class);
        update.put(TableWebsiteLetter.id, id);
        update.put(TableWebsiteLetter.isDel, 1);
        sessionTemplate.update(update);
    }

    @Override
    public void deleteLetter(long id) {
        sessionTemplate.delete(TableWebsiteLetter.class, id);
    }

    @Override
    public Paging getLetters(Query query, long start, long limit) {
        if (query == null) {
            query = Criteria.query(TableWebsiteLetter.class);
        }
        query.setTableClass(TableWebsiteLetter.class);
        query.order(TableWebsiteLetter.id, false);
        query.limit(start, limit);
        return sessionTemplate.paging(query);
    }

    @Override
    public void setLetterRead(long id) {
        ModelObject update = new ModelObject(TableWebsiteLetter.class);
        update.put(TableWebsiteLetter.id, id);
        update.put(TableWebsiteLetter.isRead, 1);
        sessionTemplate.update(update);
    }

    @Override
    public Paging getUserLetters(Query query, long userId, long start, long limit) {
        if (query == null) {
            query = Criteria.query(TableWebsiteLetter.class);
        }
        query.eq(TableWebsiteLetter.userId, userId);
        query.limit(start, limit);
        query.order(TableWebsiteLetter.id, false);
        return sessionTemplate.paging(query);
    }


}
