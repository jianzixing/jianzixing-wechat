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
public class EmailController {

    @Printer
    public ResponseMessage addEmail(ModelObject object) {
        try {
            GlobalService.emailService.addEmail(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateEmail(ModelObject object) {
        try {
            GlobalService.emailService.updateEmail(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage enableEmails(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                try {
                    GlobalService.emailService.enableEmail(id);
                } catch (Exception e) {
                    return new ResponseMessage(e);
                }
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage disableEmails(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                try {
                    GlobalService.emailService.disableEmail(id);
                } catch (Exception e) {
                    return new ResponseMessage(e);
                }
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage delEmails(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                try {
                    GlobalService.emailService.delEmail(id);
                } catch (Exception e) {
                    return new ResponseMessage(e);
                }
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage getEmails() {
        List<ModelObject> emails = GlobalService.emailService.getEmails();
        return new ResponseMessage(emails);
    }

    @Printer
    public ResponseMessage getEnableEmails(String keyword) {
        List<ModelObject> emails = GlobalService.emailService.getEnableEmails(keyword);
        return new ResponseMessage(emails);
    }
}
