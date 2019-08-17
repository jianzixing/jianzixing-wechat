package com.jianzixing.webapp.admin;

import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import com.jianzixing.webapp.service.GlobalService;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

import java.util.List;

@APIController
public class MessageController {

    @Printer
    public ResponsePageMessage getMessages(long start, long limit) {
        Paging paging = GlobalService.messageService.getMessages(null, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage delMessage(List<Long> ids) {
        if (ids != null && ids.size() > 0) {
            for (Long id : ids) {
                GlobalService.messageService.delete(id);
            }
        }
        return new ResponseMessage();
    }
}
