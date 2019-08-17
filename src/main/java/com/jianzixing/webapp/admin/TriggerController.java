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
public class TriggerController {

    @Printer
    public ResponseMessage getEvents() {
        List<ModelObject> objects = GlobalService.triggerService.getEvents();
        return new ResponseMessage(objects);
    }

    @Printer
    public ResponseMessage addTrigger(ModelObject object) {
        try {
            GlobalService.triggerService.addTrigger(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateTrigger(ModelObject object) {
        try {
            GlobalService.triggerService.updateTrigger(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponsePageMessage getTriggers(SearchForm form, long start, long limit) {
        Paging paging = GlobalService.triggerService.getTriggers(form != null ? form.getQuery() : null, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage deleteTriggers(List<Long> ids) {
        if (ids != null) {
            for (Long id : ids) {
                GlobalService.triggerService.deleteTrigger(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage getProcessorImpls() {
        List<ModelObject> impls = GlobalService.triggerService.getProcessorImpls();
        return new ResponseMessage(impls);
    }
}
