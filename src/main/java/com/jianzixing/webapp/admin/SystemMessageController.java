package com.jianzixing.webapp.admin;

import com.jianzixing.webapp.handler.RequestAdminWrapper;
import com.jianzixing.webapp.handler.AuthSkipCheck;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.message.TableSystemMessage;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;
import org.mimosaframework.springmvc.SearchForm;

import java.util.List;

@APIController
public class SystemMessageController {

    @Printer(name = "发送系统消息")
    public ResponseMessage sendMessage(RequestAdminWrapper wrapper, ModelObject object, List<Integer> ids) {
        int fromAdminId = wrapper.getId();
        object.put(TableSystemMessage.fromAdminId, fromAdminId);
        try {
            GlobalService.systemMessageService.sendSystemMessage(object, ids);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer(name = "给所有人发送系统消息")
    public ResponseMessage sendAllMessage(RequestAdminWrapper wrapper, ModelObject object) {
        int fromAdminId = wrapper.getId();
        object.put(TableSystemMessage.fromAdminId, fromAdminId);
        try {
            GlobalService.systemMessageService.sendAllSystemMessage(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer(name = "删除系统消息")
    public ResponseMessage delMessage(List<Integer> ids) {
        GlobalService.systemMessageService.delSystemMessage(ids);
        return new ResponseMessage();
    }

    @Printer(name = "查看系统消息列表")
    public ResponsePageMessage getMessages(RequestAdminWrapper wrapper, ModelObject search, int start, int limit) {
        int fromAdminId = wrapper.getId();
        Paging paging = GlobalService.systemMessageService.getSystemMessages(search, fromAdminId, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer(name = "标记系统消息已读")
    public ResponseMessage markRead(RequestAdminWrapper wrapper, List<Integer> ids) {
        int fromAdminId = wrapper.getId();
        GlobalService.systemMessageService.markRead(fromAdminId, ids);
        return new ResponseMessage();
    }

    @Printer(name = "标记系统消息已读")
    public ResponseMessage markAllRead(RequestAdminWrapper wrapper) {
        int fromAdminId = wrapper.getId();
        GlobalService.systemMessageService.markAllRead(fromAdminId);
        return new ResponseMessage();
    }

    @Printer(name = "查看消息并已读")
    @AuthSkipCheck
    public ResponseMessage getReadMessage(RequestAdminWrapper wrapper, int id) {
        ModelObject object = GlobalService.systemMessageService.getReadMessage(wrapper.getId(), id);
        return new ResponseMessage(object);
    }
}
