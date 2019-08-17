package com.jianzixing.webapp.service.website;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

public interface WebsiteLetterService {

    void sendLetter(ModelObject object) throws ModelCheckerException, ModuleException;

    void deleteFlagLetter(long id);

    void deleteLetter(long id);

    Paging getLetters(Query query, long start, long limit);

    void setLetterRead(long id);

    Paging getUserLetters(Query query, long userId, long start, long limit);
}
