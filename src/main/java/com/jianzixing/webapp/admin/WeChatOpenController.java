package com.jianzixing.webapp.admin;

import com.jianzixing.webapp.service.GlobalService;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import com.jianzixing.webapp.tables.wechat.TableWeChatOpen;
import org.mimosaframework.core.utils.RequestUtils;
import com.jianzixing.webapp.web.WebWCThirdPartyController;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;
import org.mimosaframework.springmvc.SearchForm;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@APIController
public class WeChatOpenController {

    @Printer
    public ResponseMessage addOpen(ModelObject object) {
        try {
            GlobalService.weChatOpenService.addOpen(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage deleteOpens(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.weChatOpenService.deleteOpen(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateOpen(ModelObject object) {
        try {
            GlobalService.weChatOpenService.updateOpenBase(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponsePageMessage getOpens(SearchForm form, int start, int limit) {
        Paging paging = GlobalService.weChatOpenService.getOpens(form != null ? form.getQuery() : null, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage enableOpens(List<Integer> ids) {
        try {
            GlobalService.weChatOpenService.enableOpens(ids);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage disableOpens(List<Integer> ids) {
        try {
            GlobalService.weChatOpenService.disableOpens(ids);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponsePageMessage getOpenAccounts(SearchForm form, int opid, int start, int limit) {
        Paging paging = GlobalService.weChatOpenService.getOpenAccounts(form != null ? form.getQuery() : null, opid, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage getOpenUrls(HttpServletRequest request, int id) {
        ModelObject obj = GlobalService.weChatOpenService.getOpenById(id);
        if (obj != null) {
            String code = obj.getString(TableWeChatOpen.code);
            ModelObject urls = new ModelObject();
            try {
                urls.put("mobile", WebWCThirdPartyController.getMobileUrlByAdminAuth(request, code));
                urls.put("pc", WebWCThirdPartyController.getPCUrlByAdminAuth(request, code));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new ResponseMessage(urls);
        } else {
            return new ResponseMessage(-110, "没有找到第三方平台信息");
        }
    }

    @Printer
    public ResponseMessage getOpenDetail(HttpServletRequest request, int id) {
        try {
            ModelObject obj = GlobalService.weChatOpenService.getOpenById(id);
            if (obj != null) {
                String code = obj.getString(TableWeChatOpen.code);
                try {
                    obj.put("mobile", WebWCThirdPartyController.getMobileUrlByAdminAuth(request, code));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    obj.put("pc", WebWCThirdPartyController.getPCUrlByAdminAuth(request, code));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                obj.put("domain", RequestUtils.getWebDomain(request));
                obj.put("auth_url", WebWCThirdPartyController.getUrlByAuthEvent(request, code));
                obj.put("auth_msg_url", WebWCThirdPartyController.getUrlByEventMessage(request, code));
                return new ResponseMessage(obj);
            } else {
                return new ResponseMessage(-110, "没有找到第三方平台信息");
            }
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
    }


    @Printer
    public ResponseMessage getDefaultMiniProgramAccount() {
        ModelObject object = GlobalService.weChatMiniProgramService.getDefaultAccount();
        return new ResponseMessage(object);
    }

    @Printer
    public ResponseMessage setDefaultMiniProgramAccount(int openType, int accountId) {
        GlobalService.weChatMiniProgramService.setDefaultAccount(openType, accountId);
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage getDefaultPublicAccount() {
        ModelObject object = GlobalService.weChatPublicService.getDefaultAccount();
        return new ResponseMessage(object);
    }

    @Printer
    public ResponseMessage setDefaultPublicAccount(int openType, int accountId) {
        GlobalService.weChatPublicService.setDefaultAccount(openType, accountId);
        return new ResponseMessage();
    }
}
