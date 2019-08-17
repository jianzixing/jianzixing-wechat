package com.jianzixing.webapp.admin;

import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.wechat.WeChatOpenType;
import com.jianzixing.webapp.service.wechat.model.AccountConfig;
import com.jianzixing.webapp.tables.wechat.TableWeChatWebSite;
import com.jianzixing.webapp.web.AbstractWeChatController;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@APIController
public class WeChatWebSiteController extends AbstractWeChatController {

    @Printer
    public ResponseMessage addWebSite(ModelObject object) {
        try {
            GlobalService.weChatWebSiteService.addWebSite(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage delWebSites(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.weChatWebSiteService.delWebSite(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateWebSite(ModelObject object) {
        try {
            GlobalService.weChatWebSiteService.updateWebSite(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponsePageMessage getWebSites(HttpServletRequest request, ModelObject search, int start, int limit) {
        Paging paging = GlobalService.weChatWebSiteService.getWebSites(search, start, limit);
        if (paging != null) {
            List<ModelObject> list = paging.getObjects();
            if (list != null) {
                for (ModelObject obj : list) {
                    String code = obj.getString(TableWeChatWebSite.code);
                    AccountConfig config = AccountConfig.builder(WeChatOpenType.WEBSITE.getCode(), code);
                    String url = GlobalService.weChatService.getWebSiteConnector().getAuthUrl(
                            config, getHomePage(request, config), code);
                    obj.put("authUrl", url);
                }
            }
        }
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage getDefaultAccount() {
        ModelObject defaultObj = GlobalService.weChatWebSiteService.getDefaultAccount();
        return new ResponseMessage(defaultObj);
    }

    @Printer
    public ResponseMessage setDefaultAccount(int accountId) {
        GlobalService.weChatWebSiteService.setDefaultAccount(accountId);
        return new ResponseMessage();
    }
}
