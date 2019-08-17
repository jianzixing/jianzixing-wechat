package com.jianzixing.webapp.admin;

import org.mimosaframework.springmvc.utils.ResponseMessage;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.wechat.WeChatUserConnector;
import com.jianzixing.webapp.service.wechat.model.AccountConfig;
import com.jianzixing.webapp.tables.wechat.TableWeChatUser;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.utils.ModelUtils;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;
import org.mimosaframework.springmvc.SearchForm;

import java.util.List;

@APIController
public class WeChatUserController {
    //    标签管理
    @Printer
    public ResponseMessage getLabels(int openType, int accountId) {
        WeChatUserConnector connector = GlobalService.weChatService.getUserConnector();
        try {
            List<ModelObject> objects = connector.getLabels(AccountConfig.builder(openType, accountId));
            return new ResponseMessage(objects);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
    }

    @Printer
    public ResponseMessage delLabel(int openType, int accountId, List<Integer> ids) {
        WeChatUserConnector connector = GlobalService.weChatService.getUserConnector();
        try {
            if (ids != null) {
                for (int id : ids) {
                    connector.deleteLabel(AccountConfig.builder(openType, accountId), id);
                }
            }
            return new ResponseMessage();
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
    }

    @Printer
    public ResponseMessage updateLabel(int openType, int accountId, int id, String tagName) {
        WeChatUserConnector connector = GlobalService.weChatService.getUserConnector();
        try {
            connector.updateLabel(AccountConfig.builder(openType, accountId), id, tagName);
            return new ResponseMessage();
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
    }

    @Printer
    public ResponseMessage createLabel(int openType, int accountId, String tagName) {
        WeChatUserConnector connector = GlobalService.weChatService.getUserConnector();
        try {
            connector.createLabel(AccountConfig.builder(openType, accountId), tagName);
            return new ResponseMessage();
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
    }

    @Printer
    public ResponseMessage setUserLabel(int openType, int accountId, int tagid, List<Long> uids, String text) {
        try {
            GlobalService.weChatUserService.setUserLabel(openType, accountId, tagid, uids, text);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage cancelUserLabel(int openType, int accountId, int tagid, List<Long> uids) {
        try {
            GlobalService.weChatUserService.cancelUserLabel(openType, accountId, tagid, uids);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

//    用户管理

    @Printer
    public ResponsePageMessage getUsers(SearchForm form, int openType, int accountId, long start, long limit) {
        Paging paging = GlobalService.weChatUserService.getUsers(form != null ? form.getQuery() : null, openType, accountId, start, limit);
        ModelUtils.removeValues(paging, TableWeChatUser.accessToken, TableWeChatUser.refreshToken);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage syncWeChatUsers(int openType, int accountId) {
        try {
            GlobalService.weChatUserService.syncFromWeChat(openType, accountId);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage setUserRemark(long uid, String remark) {
        try {
            GlobalService.weChatUserService.setRemark(uid, remark);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }
}
