package com.jianzixing.webapp.admin;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import com.jianzixing.webapp.service.GlobalService;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.exception.TransactionException;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

import java.util.List;

@APIController
public class RefundOrderController {

    @Printer
    public ResponsePageMessage getRefundOrders(ModelObject search, long start, long limit) {
        Paging paging = GlobalService.refundOrderService.getRefundOrders(search, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage setRefundPass(long id, String remark) {
        try {
            GlobalService.refundOrderService.setRefundAuditPass(id, remark);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage setRefundReject(long id, String remark) {
        try {
            GlobalService.refundOrderService.setRefundAuditReject(id, remark);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage startRefund(List<Long> ids) {
        try {
            for (long id : ids) {
                GlobalService.refundOrderService.startRefund(id);
            }
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }
}
