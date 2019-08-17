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
public class LogisticsController {

    @Printer
    public ResponseMessage getTemplatesByKeyword(String keyword) {
        List<ModelObject> objects = GlobalService.logisticsService.getSimpleTemplates(keyword);
        return new ResponseMessage(objects);
    }

    @Printer
    public ResponseMessage getLogisticsCompany() {
        List<ModelObject> objects = GlobalService.logisticsService.getCompanies();
        return new ResponseMessage(objects);
    }

    @Printer
    public ResponsePageMessage getTemplates(ModelObject search, long start, long limit) {
        Paging objects = GlobalService.logisticsService.getTemplates(search, start, limit);
        return new ResponsePageMessage(objects);
    }

    @Printer
    public ResponseMessage addTemplates(ModelObject object) {
        try {
            GlobalService.logisticsService.saveTemplate(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage delTemplates(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.logisticsService.deleteTemplate(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateTemplate(ModelObject object) {
        try {
            GlobalService.logisticsService.saveTemplate(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage getTemplate(int tid) {
        ModelObject obj = GlobalService.logisticsService.getTemplateById(tid);
        return new ResponseMessage(obj);
    }

    @Printer
    public ResponseMessage addCompany(ModelObject object) {
        try {
            GlobalService.logisticsService.addCompany(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage deleteCompany(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.logisticsService.deleteCompany(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateCompany(ModelObject object) {
        try {
            GlobalService.logisticsService.updateCompany(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage getCompany() {
        List<ModelObject> objects = GlobalService.logisticsService.getCompany();
        return new ResponseMessage(objects);
    }

    @Printer
    public ResponseMessage setDefaultCompany(int id) {
        GlobalService.logisticsService.setDefaultCompany(id);
        return new ResponseMessage();
    }
}
