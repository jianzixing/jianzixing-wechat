package com.jianzixing.webapp.admin;

import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import com.jianzixing.webapp.service.GlobalService;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

@APIController
public class BalanceController {

    @Printer
    public ResponsePageMessage getBalances(ModelObject search, long start, long limit) {
        Paging paging = GlobalService.balanceService.getBalances(search, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponsePageMessage getRecords(ModelObject search, long start, long limit) {
        Paging paging = GlobalService.balanceService.getRecords(search, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponsePageMessage getRecordsByUid(long uid, long start, long limit) {
        Paging paging = GlobalService.balanceService.getRecordsByUid(uid, start, limit);
        return new ResponsePageMessage(paging);
    }
}
