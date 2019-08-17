package com.jianzixing.webapp.admin;

import com.jianzixing.webapp.service.GlobalService;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

import java.util.List;

@APIController
public class IntegralController {

    @Printer
    public ResponsePageMessage getIntegrals(ModelObject search, long start, long limit) {
        Paging paging = GlobalService.integralService.getIntegrals(search, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponsePageMessage getRecords(ModelObject search, long start, long limit) {
        Paging paging = GlobalService.integralService.getRecords(search, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage setUserIntegralZero(List<Long> ids) {
        GlobalService.integralService.clearUserIntegrals(ids);
        return new ResponseMessage();
    }

    @Printer
    public ResponsePageMessage getRecordsByUid(long uid, long start, long limit) {
        Paging paging = GlobalService.integralService.getRecordsByUid(uid, start, limit);
        return new ResponsePageMessage(paging);
    }
}
