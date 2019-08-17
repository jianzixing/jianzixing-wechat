package com.jianzixing.webapp.admin;

import com.jianzixing.webapp.service.GlobalService;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;

import java.util.List;

@APIController
public class HotSearchController {

    @Printer
    public ResponsePageMessage getHotSearch(int start, int limit) {
        Paging paging = GlobalService.hotSearchService.getHotSearch(start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage addHotSearch(ModelObject object) {
        try {
            GlobalService.hotSearchService.addHotSearch(object);
        } catch (Exception e) {
            return new ResponsePageMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage delHotSearch(List<Integer> ids) {
        if (ids != null) {
            for (Integer id : ids) {
                GlobalService.hotSearchService.delHotSearch(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateHotSearch(ModelObject object) {
        try {
            GlobalService.hotSearchService.updateHotSearch(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }
}
