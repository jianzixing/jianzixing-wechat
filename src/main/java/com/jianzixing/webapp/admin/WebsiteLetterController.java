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
public class WebsiteLetterController {

    @Printer
    public ResponseMessage send(ModelObject object) {
        try {
            GlobalService.websiteLetterService.sendLetter(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage deleteLettter(List<Long> ids) {
        if (ids != null) {
            for (long id : ids) {
                GlobalService.websiteLetterService.deleteLetter(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponsePageMessage getLetters(SearchForm form, long start, long limit) {
        Paging paging = GlobalService.websiteLetterService.getLetters(form != null ? form.getQuery() : null, start, limit);
        return new ResponsePageMessage(paging);
    }
}
