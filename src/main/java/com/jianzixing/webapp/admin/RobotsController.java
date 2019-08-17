package com.jianzixing.webapp.admin;

import com.jianzixing.webapp.service.GlobalService;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

import java.util.List;

@APIController
public class RobotsController {

    @Printer(name = "添加爬虫协议")
    public ResponseMessage addRobots(ModelObject object) {
        try {
            GlobalService.systemRobotsService.addRobots(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer(name = "删除爬虫协议")
    public ResponseMessage delRobots(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.systemRobotsService.delRobots(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer(name = "更新爬虫协议")
    public ResponseMessage updateRobots(ModelObject object) {
        try {
            GlobalService.systemRobotsService.updateRobots(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer(name = "查看爬虫协议列表")
    public ResponsePageMessage getRobots() {
        return new ResponsePageMessage(GlobalService.systemRobotsService.getRobotsObject());
    }

    @Printer(name = "更新爬虫协议排序")
    public ResponseMessage updateRobotsPos(int id, int pos) {
        GlobalService.systemRobotsService.updateRobotsPos(id, pos);
        return new ResponseMessage();
    }
}
