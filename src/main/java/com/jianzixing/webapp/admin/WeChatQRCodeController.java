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
public class WeChatQRCodeController {


    @Printer
    public ResponseMessage addQRCode(ModelObject object) {
        try {
            GlobalService.weChatQRCodeService.addQRCode(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateQRCode(ModelObject object) {
        GlobalService.weChatQRCodeService.updateQRCode(object);
        return new ResponseMessage();
    }

    @Printer
    public ResponsePageMessage getQRCodes(SearchForm form, int openType, int accountId, int start, int limit) {
        Paging paging = GlobalService.weChatQRCodeService.getQRCodes(form != null ? form.getQuery() : null, openType, accountId, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage deleteQRCodes(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.weChatQRCodeService.deleteQRCode(id);
            }
        }
        return new ResponseMessage();
    }
}
