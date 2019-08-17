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
public class AdvertisingController {

    @Printer(name = "添加广告配置")
    public ResponseMessage addAdvertising(ModelObject object) {
        try {
            GlobalService.advertisingService.addAdvertising(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer(name = "删除广告配置")
    public ResponseMessage delAdvertising(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.advertisingService.delAdvertising(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer(name = "更新广告配置")
    public ResponseMessage updateAdvertising(ModelObject object) {
        try {
            GlobalService.advertisingService.updateAdvertising(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer(name = "查看广告配置")
    public ResponsePageMessage getAdvertisings(SearchForm form, int start, int limit) {
        Paging paging = GlobalService.advertisingService.getAdvertising(form != null ? form.getQuery() : null, start, limit);
        return new ResponsePageMessage(paging);
    }
}
