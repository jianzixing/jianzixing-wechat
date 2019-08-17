package com.jianzixing.webapp.admin;

import org.mimosaframework.springmvc.utils.ResponseMessage;
import com.jianzixing.webapp.service.GlobalService;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

import java.util.List;

@APIController
public class SmsController {

    @Printer
    public ResponseMessage getSmsImpls() {
        List<ModelObject> impls = GlobalService.smsService.getSmsImpls();
        return new ResponseMessage(impls);
    }

    @Printer
    public ResponseMessage getEnableSms(String keyword) {
        List<ModelObject> sms = GlobalService.smsService.getEnableSms(keyword);
        return new ResponseMessage(sms);
    }

    @Printer
    public ResponseMessage getSms() {
        List<ModelObject> objects = GlobalService.smsService.getSms();
        return new ResponseMessage(objects);
    }

    @Printer
    public ResponseMessage addSms(ModelObject object) {
        try {
            GlobalService.smsService.addSms(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateSms(ModelObject object) {
        try {
            GlobalService.smsService.updateSms(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage delSms(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.smsService.delSms(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage enableSms(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.smsService.enableSms(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage disableSms(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.smsService.disableSms(id);
            }
        }
        return new ResponseMessage();
    }
}
