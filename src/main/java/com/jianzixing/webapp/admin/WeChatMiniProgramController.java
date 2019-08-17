package com.jianzixing.webapp.admin;

import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.wechat.WeChatOpenType;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;
import org.mimosaframework.springmvc.SearchForm;

import java.util.List;

@APIController
public class WeChatMiniProgramController {

    @Printer
    public ResponseMessage addMiniProgram(ModelObject object) {
        try {
            GlobalService.weChatMiniProgramService.addMiniProgram(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage delMiniPrograms(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.weChatMiniProgramService.delMiniProgram(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateMiniProgram(ModelObject object) {
        try {
            GlobalService.weChatMiniProgramService.updateMiniProgram(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponsePageMessage getMiniPrograms(SearchForm form, int start, int limit) {
        Paging paging = GlobalService.weChatMiniProgramService.getMiniPrograms(form != null ? form.getQuery() : null, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage getDefaultAccount() {
        ModelObject object = GlobalService.weChatMiniProgramService.getDefaultAccount();
        return new ResponseMessage(object);
    }

    @Printer
    public ResponseMessage setDefaultAccount(int accountId) {
        GlobalService.weChatMiniProgramService.setDefaultAccount(WeChatOpenType.MINI_PROGRAM.getCode(), accountId);
        return new ResponseMessage();
    }
}
