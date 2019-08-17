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
public class GoodsCommentController {

    @Printer
    public ResponsePageMessage getComments(ModelObject search, long start, long limit) {
        Paging paging = GlobalService.goodsCommentService.getComments(search, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage deleteComments(List<Long> ids) {
        if (ids != null) {
            for (long id : ids) {
                GlobalService.goodsCommentService.deleteComment(id);
            }
        }
        return new ResponseMessage();
    }
}
