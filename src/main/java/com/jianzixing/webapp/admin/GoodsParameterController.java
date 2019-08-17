package com.jianzixing.webapp.admin;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import com.jianzixing.webapp.service.GlobalService;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

import java.util.List;

@APIController
public class GoodsParameterController {

    @Printer
    public List<ModelObject> getGroups() {
        return GlobalService.goodsGroupService.getGroups();
    }

    @Printer
    public ResponseMessage addParameter(ModelObject object) {
        try {
            GlobalService.goodsParameterService.addParameter(object);
        } catch (ModuleException e) {
            e.printStackTrace();
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage deleteParameters(List<Integer> ids) {
        for (int id : ids) {
            GlobalService.goodsParameterService.deleteParameter(id);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponsePageMessage getParameters(int gid) {
        return new ResponsePageMessage(0, GlobalService.goodsParameterService.getParameter(gid));
    }

    @Printer
    public ResponsePageMessage getParameterList(String keyword, long start, long limit) {
        Paging paging = GlobalService.goodsParameterService.getParameterList(keyword, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage relParameters(int gid, List<Long> ids) {
        GlobalService.goodsParameterService.setParameterRel(gid, ids);
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage removeRelParameters(int gid, List<Long> ids) {
        GlobalService.goodsParameterService.removeParameterRel(gid, ids);
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateParameter(ModelObject object) {
        try {
            GlobalService.goodsParameterService.updateSimpleParameter(object);
        } catch (ModuleException e) {
            e.printStackTrace();
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponsePageMessage getValues(int pid) {
        return new ResponsePageMessage(0, GlobalService.goodsParameterService.getValues(pid));
    }

    @Printer
    public ResponseMessage addValue(ModelObject object) {
        try {
            GlobalService.goodsParameterService.addValue(object);
        } catch (ModuleException e) {
            e.printStackTrace();
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateValue(ModelObject object) {
        try {
            GlobalService.goodsParameterService.updateValue(object);
        } catch (ModuleException e) {
            e.printStackTrace();
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage deleteValues(List<Integer> ids) {
        for (int id : ids) {
            GlobalService.goodsParameterService.deleteValue(id);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage getGroupSet(int gid) {
        return new ResponseMessage(GlobalService.goodsParameterService.getGroupSet(gid));
    }
}
