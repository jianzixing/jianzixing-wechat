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
public class WeChatAppController {

    @Printer
    public ResponseMessage addApp(ModelObject object) {
        try {
            GlobalService.weChatAppService.addApp(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage delApps(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.weChatAppService.delApp(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateApp(ModelObject object) {
        try {
            GlobalService.weChatAppService.updateApp(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponsePageMessage getApps(SearchForm form, int start, int limit) {
        Paging paging = GlobalService.weChatAppService.getApps(form != null ? form.getQuery() : null, start, limit);
        return new ResponsePageMessage(paging);
    }


    @Printer
    public ResponseMessage getDefaultAccount() {
        ModelObject defaultObj = GlobalService.weChatAppService.getDefaultAccount();
        return new ResponseMessage(defaultObj);
    }

    @Printer
    public ResponseMessage setDefaultAccount(int accountId) {
        GlobalService.weChatAppService.setDefaultAccount(accountId);
        return new ResponseMessage();
    }
}
