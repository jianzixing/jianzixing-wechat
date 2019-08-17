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
public class PaymentController {

    @Printer
    public ResponseMessage addChannel(ModelObject object) {
        try {
            GlobalService.paymentService.addChannel(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage deleteChannel(List<Long> ids) {
        if (ids != null) {
            for (long id : ids) {
                GlobalService.paymentService.deleteChannel(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateChannel(ModelObject object) {
        try {
            GlobalService.paymentService.updateChannel(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponsePageMessage getChannels(String keyword, long start, long limit) {
        Paging paging = GlobalService.paymentService.getChannels(keyword, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage getModels() {
        List<ModelObject> models = GlobalService.paymentService.getModels();
        return new ResponseMessage(models);
    }
}
