package com.jianzixing.webapp.service.comment;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.tables.comment.TableDiscussClassify;
import com.jianzixing.webapp.tables.comment.TableDiscussComment;
import com.jianzixing.webapp.tables.user.TableUser;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DefaultDiscussCommentService implements DiscussCommentService {
    private static final Map<String, Class> CODE_CLASS = new HashMap() {{
        // this.put("PRODUCT", TableProduct.class);
    }};

    private static final Map<String, Class> CODE_CLASS_PK = new HashMap() {{
        // this.put("PRODUCT", TableProduct.id);
    }};

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addClassify(ModelObject object) throws ModelCheckerException, ModuleException {
        object.setObjectClass(TableDiscussClassify.class);
        object.checkAndThrowable();

        String code = object.getString(TableDiscussClassify.code);
        ModelObject old = sessionTemplate.get(Criteria.query(TableDiscussClassify.class).eq(TableDiscussClassify.code, code));
        if (old != null) {
            throw new ModuleException(StockCode.EXIST_OBJ, "当前分类码已经存在,请重新填写!");
        }

        sessionTemplate.save(object);
    }

    @Override
    public void delClassify(int id) {
        sessionTemplate.delete(TableDiscussClassify.class, id);
    }

    @Override
    public void updateClassify(ModelObject object) throws ModelCheckerException, ModuleException {
        object.setObjectClass(TableDiscussClassify.class);
        object.checkUpdateThrowable();

        String code = object.getString(TableDiscussClassify.code);
        if (StringUtils.isNotBlank(code)) {
            int id = object.getIntValue(TableDiscussClassify.id);
            ModelObject old = sessionTemplate.get(Criteria.query(TableDiscussClassify.class).eq(TableDiscussClassify.code, code).ne(TableDiscussClassify.id, id));
            if (old != null) {
                throw new ModuleException(StockCode.EXIST_OBJ, "当前分类码已经存在,请重新填写!");
            }
        }

        sessionTemplate.update(object);
    }

    @Override
    public List<ModelObject> getClassify() {
        return sessionTemplate.list(Criteria.query(TableDiscussClassify.class));
    }

    @Override
    public void addComment(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableDiscussComment.class);
        object.checkAndThrowable();
        sessionTemplate.save(object);
    }

    @Override
    public void delComment(long id) {
        sessionTemplate.delete(TableDiscussComment.class, id);
    }

    @Override
    public void updateComment(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableDiscussComment.class);
        object.checkUpdateThrowable();

        sessionTemplate.update(object);
    }

    @Override
    public Paging getComments(Query query, int cid, long start, long limit) {
        ModelObject classify = sessionTemplate.get(TableDiscussClassify.class, cid);
        String code = classify.getString(TableDiscussClassify.code);
        Class c = CODE_CLASS.get(code);
        if (query == null) {
            query = Criteria.query(TableDiscussComment.class);
        }
        if (c != null) {
            query.subjoin(c).eq(CODE_CLASS_PK.get(code), TableDiscussComment.outId).aliasName("outValue").single();
        }
        query.subjoin(TableUser.class).eq(TableUser.id, TableDiscussComment.userId).aliasName("User").single();
        query.order(TableDiscussComment.id, false);
        query.limit(start, limit);
        return sessionTemplate.paging(query);
    }

    @Override
    public void setShow(List<Long> ids) {
        sessionTemplate.update(Criteria.update(TableDiscussComment.class).in(TableDiscussComment.id, ids).value(TableDiscussComment.isShow, 1));
    }

    @Override
    public void setHide(List<Long> ids) {
        sessionTemplate.update(Criteria.update(TableDiscussComment.class).in(TableDiscussComment.id, ids).value(TableDiscussComment.isShow, 0));
    }
}
