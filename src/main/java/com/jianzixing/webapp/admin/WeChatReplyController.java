package com.jianzixing.webapp.admin;

import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import com.jianzixing.webapp.service.GlobalService;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;
import org.mimosaframework.springmvc.SearchForm;

import java.util.List;

@APIController
public class WeChatReplyController {

    @Printer
    public ResponsePageMessage getReplys(SearchForm form, int openType, int accountId, int start, int limit) {
        Paging paging = GlobalService.weChatReplyService.getReplys(form != null ? form.getQuery() : null, openType, accountId, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage addReply(ModelObject object) {
        try {
            GlobalService.weChatReplyService.addReply(object);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage delReply(List<Integer> ids) {
        if (ids != null && ids.size() > 0) {
            for (int id : ids) {
                GlobalService.weChatReplyService.deleteReply(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateReply(ModelObject object) {
        try {
            GlobalService.weChatReplyService.updateReply(object);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }
}
