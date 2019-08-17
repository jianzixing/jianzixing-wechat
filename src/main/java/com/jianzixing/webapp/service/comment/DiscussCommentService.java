package com.jianzixing.webapp.service.comment;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

import java.util.List;

public interface DiscussCommentService {
    void addClassify(ModelObject object) throws ModelCheckerException, ModuleException;

    void delClassify(int id);

    void updateClassify(ModelObject object) throws ModelCheckerException, ModuleException;

    List<ModelObject> getClassify();

    void addComment(ModelObject object) throws ModelCheckerException;

    void delComment(long id);

    void updateComment(ModelObject object) throws ModelCheckerException;

    Paging getComments(Query query, int cid, long start, long limit);

    void setShow(List<Long> ids);

    void setHide(List<Long> ids);
}
