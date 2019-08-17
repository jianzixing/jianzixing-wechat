package com.jianzixing.webapp.admin;

import com.jianzixing.webapp.handler.AuthSkipCheck;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import com.jianzixing.webapp.service.GlobalService;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

import java.util.List;

@AuthSkipCheck
@APIController
public class WxpluginSignController {

    @Printer
    public ResponseMessage addGroup(ModelObject object) {
        try {
            GlobalService.pluginSignService.addGroup(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage delGroup(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.pluginSignService.delGroup(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateGroup(ModelObject object) {
        try {
            GlobalService.pluginSignService.updateGroup(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage getGroups(String keyword) {
        List<ModelObject> objects = GlobalService.pluginSignService.getGroups(keyword);
        return new ResponseMessage(objects);
    }

    @Printer
    public ResponseMessage enableGroups(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.pluginSignService.enableGroup(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage disableGroups(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.pluginSignService.disableGroup(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage addAward(ModelObject object) {
        try {
            GlobalService.pluginSignService.addAward(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage delAward(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.pluginSignService.delAward(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateAward(ModelObject object) {
        try {
            GlobalService.pluginSignService.updateAward(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponsePageMessage getAwards(ModelObject search, int start, int limit) {
        Paging paging = GlobalService.pluginSignService.getAwards(search, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponsePageMessage getRecords(ModelObject search, int start, int limit) {
        Paging paging = GlobalService.pluginSignService.getRecords(search, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponsePageMessage getGets(ModelObject search, int start, int limit) {
        Paging paging = GlobalService.pluginSignService.getGets(search, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponsePageMessage getLogs(int gid, long uid, int start, int limit) {
        Paging paging = GlobalService.pluginSignService.getLogs(gid, uid, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage setGetUsed(int id, String useCount) {
        try {
            GlobalService.pluginSignService.setGetUsed(id, StringUtils.isBlank(useCount) ? 0 : Integer.parseInt(useCount));
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }
}
