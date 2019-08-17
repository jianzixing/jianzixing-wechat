package com.jianzixing.webapp.admin;

import com.jianzixing.webapp.service.GlobalService;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;
import org.mimosaframework.springmvc.SearchForm;

@APIController
public class LogController {

    @Printer(name = "查看访问日志")
    public ResponsePageMessage getRequestAddressLog(SearchForm form, int start, int limit) {
        Paging paging = GlobalService.requestAddressService.getAddresses(form != null ? form.getQuery() : null, start, limit);
        return new ResponsePageMessage(paging);
    }
}
