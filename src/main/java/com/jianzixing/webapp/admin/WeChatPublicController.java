package com.jianzixing.webapp.admin;

import com.jianzixing.webapp.service.GlobalService;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import com.jianzixing.webapp.service.wechat.WeChatOpenType;
import com.jianzixing.webapp.service.wechat.model.AccountConfig;
import com.jianzixing.webapp.tables.wechat.TableWeChatAccount;
import com.jianzixing.webapp.web.AbstractWeChatController;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;
import org.mimosaframework.springmvc.SearchForm;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@APIController
public class WeChatPublicController {

    @Printer
    public ResponseMessage addAccount(ModelObject object) {
        try {
            GlobalService.weChatPublicService.addAccount(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage deleteAccount(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.weChatPublicService.deleteAccount(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateAccount(ModelObject object) {
        try {
            GlobalService.weChatPublicService.updateAccount(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponsePageMessage getAccounts(HttpServletRequest request,
                                           SearchForm form, int start, int limit) {
        Paging paging = GlobalService.weChatPublicService.getAccounts(form != null ? form.getQuery() : null, start, limit);
        if (paging != null) {
            List<ModelObject> objects = paging.getObjects();
            if (objects != null) {
                for (ModelObject object : objects) {
                    String code = object.getString(TableWeChatAccount.code);
                    object.put("authUrl", AbstractWeChatController
                            .getServerUrl(request, AccountConfig.builder(WeChatOpenType.PUBLIC.getCode(), code)));
                }
            }
        }
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage getAccountTrees() {
        return new ResponseMessage(
                GlobalService.weChatPublicService.getAccountChildTree()
        );
    }

    @Printer
    public ResponseMessage enableAccounts(List<Integer> ids) {
        try {
            GlobalService.weChatPublicService.enableAccounts(ids);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage disableAccounts(List<Integer> ids) {
        try {
            GlobalService.weChatPublicService.disableAccounts(ids);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage getDefaultAccount() {
        ModelObject object = GlobalService.weChatPublicService.getDefaultAccount();
        return new ResponseMessage(object);
    }

    @Printer
    public ResponseMessage setDefaultAccount(int accountId) {
        GlobalService.weChatPublicService.setDefaultAccount(WeChatOpenType.PUBLIC.getCode(), accountId);
        return new ResponseMessage();
    }
}
