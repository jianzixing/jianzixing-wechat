package com.jianzixing.webapp.admin;

import org.mimosaframework.springmvc.utils.ResponseMessage;
import com.jianzixing.webapp.service.GlobalService;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

import java.util.List;

@APIController
public class SupportController {

    @Printer
    public ResponseMessage addSupport(ModelObject object) {
        try {
            GlobalService.supportService.addSupport(object);
            return new ResponseMessage();
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
    }

    @Printer
    public ResponseMessage delSupport(int id) {
        GlobalService.supportService.delSupport(id);
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateSupport(ModelObject object) {
        try {
            GlobalService.supportService.updateSupport(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage getSupports() {
        List<ModelObject> objects = GlobalService.supportService.getSupports();
        return new ResponseMessage(objects);
    }

    @Printer
    public ResponseMessage getSupportByGroup(long groupId) {
        List<ModelObject> objects = GlobalService.supportService.getSupportsByGroup(groupId);
        return new ResponseMessage(objects);
    }
}
