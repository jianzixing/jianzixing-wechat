package com.jianzixing.webapp.admin;

import com.jianzixing.webapp.service.GlobalService;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;
import org.mimosaframework.springmvc.SearchForm;

import java.util.List;

@APIController
public class DiscussCommentController {

    @Printer(name = "添加评论分类")
    public ResponseMessage addClassify(ModelObject object) {
        try {
            GlobalService.discussCommentService.addClassify(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer(name = "删除评论分类")
    public ResponseMessage delClassify(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.discussCommentService.delClassify(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer(name = "更新评论分类")
    public ResponseMessage updateClassify(ModelObject object) {
        try {
            GlobalService.discussCommentService.updateClassify(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer(name = "查看评论分类")
    public ResponseMessage getClassifies() {
        List<ModelObject> objects = GlobalService.discussCommentService.getClassify();
        return new ResponseMessage(objects);
    }

    @Printer(name = "添加评论内容")
    public ResponseMessage addComment(ModelObject object) {
        try {
            GlobalService.discussCommentService.addComment(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer(name = "删除评论内容")
    public ResponseMessage delComment(List<Long> ids) {
        if (ids != null) {
            for (long id : ids) {
                GlobalService.discussCommentService.delComment(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer(name = "更新评论内容")
    public ResponseMessage updateComment(ModelObject object) {
        try {
            GlobalService.discussCommentService.updateComment(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer(name = "查看评论内容")
    public ResponsePageMessage getComments(SearchForm form, int cid, long start, long limit) {
        Paging paging = GlobalService.discussCommentService.getComments(form != null ? form.getQuery() : null, cid, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer(name = "设置评论可见")
    public ResponseMessage setShow(List<Long> ids) {
        GlobalService.discussCommentService.setShow(ids);
        return new ResponseMessage();
    }

    @Printer(name = "设置评论不可见")
    public ResponseMessage setHide(List<Long> ids) {
        GlobalService.discussCommentService.setHide(ids);
        return new ResponseMessage();
    }
}
