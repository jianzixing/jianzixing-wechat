package com.jianzixing.webapp.admin;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.wechat.model.AccountConfig;
import com.jianzixing.webapp.service.wechatsm.WeChatServiceManagerUtils;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;
import org.mimosaframework.springmvc.SearchForm;

import java.util.List;

@APIController
public class WeChatMassController {

    @Printer
    public ResponseMessage getWeChatLabels(int openType, int accountId) {
        try {
            AccountConfig config = WeChatServiceManagerUtils.createAccountConfig(openType, accountId);
            if (config == null) {
                throw new IllegalArgumentException("没有找到当前公众号信息");
            }
            List<ModelObject> objects = GlobalService.weChatService.getUserConnector().getLabels(config);
            ModelObject first = new ModelObject();
            first.put("id", 0);
            first.put("name", "全部用户");
            objects.add(0, first);
            return new ResponseMessage(objects);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
    }

    @Printer
    public ResponseMessage addMass(ModelObject object) {
        try {
            GlobalService.weChatMassService.addMass(object);
            return new ResponseMessage();
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
    }

    @Printer
    public ResponseMessage delMasses(List<Integer> ids) {
        try {
            if (ids != null) {
                for (int id : ids) {
                    GlobalService.weChatMassService.delMass(id);
                }
            }
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateMass(ModelObject object) {
        try {
            GlobalService.weChatMassService.updateMass(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponsePageMessage getMesses(SearchForm form, int openType, int accountId, int start, int limit) {
        Paging paging = null;
        try {
            paging = GlobalService.weChatMassService.getMasses(form != null ? form.getQuery() : null, openType, accountId, start, limit);
        } catch (ModuleException e) {
            return new ResponsePageMessage(e);
        }
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage enableMasses(List<Integer> ids) {
        try {
            if (ids != null) {
                for (int id : ids) {
                    GlobalService.weChatMassService.enableMass(id);
                }
            }
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage disableMasses(List<Integer> ids) {
        try {
            if (ids != null) {
                for (int id : ids) {
                    GlobalService.weChatMassService.disableMass(id);
                }
            }
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }
}
