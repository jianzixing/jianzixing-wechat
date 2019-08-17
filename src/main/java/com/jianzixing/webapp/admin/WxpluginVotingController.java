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
public class WxpluginVotingController {
    @Printer
    public ResponseMessage addGroup(ModelObject object) {
        try {
            GlobalService.pluginVotingService.addGroup(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage delGroup(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.pluginVotingService.delGroup(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateGroup(ModelObject object) {
        try {
            GlobalService.pluginVotingService.updateGroup(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponsePageMessage getGroups(String keyword, int start, int limit) {
        ModelObject search = null;
        if (StringUtils.isNotBlank(keyword)) {
            search = new ModelObject();
            search.put("name", keyword);
        }
        Paging paging = GlobalService.pluginVotingService.getGroups(search, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage addItem(ModelObject object) {
        try {
            GlobalService.pluginVotingService.addItem(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage delItem(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.pluginVotingService.delItem(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateItem(ModelObject object) {
        try {
            GlobalService.pluginVotingService.updateItem(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponsePageMessage getItems(ModelObject search, int start, int limit) {
        Paging paging = GlobalService.pluginVotingService.getItems(search, start, limit);
        return new ResponsePageMessage(paging);
    }
}
