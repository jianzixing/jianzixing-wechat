package com.jianzixing.webapp.admin;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import com.jianzixing.webapp.service.GlobalService;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

import java.util.List;

@APIController
public class GoodsGroupController {

    @Printer
    public List<ModelObject> getGoodsGroups() {
        return GlobalService.goodsGroupService.getGroups();
    }

    @Printer
    public ResponseMessage deleteGoodsGroups(List<Integer> ids) {
        try {
            GlobalService.goodsGroupService.deleteGroups(ids);
        } catch (ModuleException e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage addGoodsGroup(ModelObject object) {
        try {
            GlobalService.goodsGroupService.addGroup(object);
        } catch (ModuleException e) {
            e.printStackTrace();
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateGoodsGroup(ModelObject object) {
        try {
            GlobalService.goodsGroupService.updateGroup(object);
        } catch (ModuleException e) {
            e.printStackTrace();
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }
}
