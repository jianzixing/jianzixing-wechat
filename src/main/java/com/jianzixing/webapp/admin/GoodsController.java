package com.jianzixing.webapp.admin;

import com.jianzixing.webapp.handler.AdminSkipLoginCheck;
import com.jianzixing.webapp.handler.AuthSkipCheck;
import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.goods.TableGoodsGroup;
import com.jianzixing.webapp.tables.system.TableAdmin;
import com.jianzixing.webapp.tables.system.TableModule;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.utils.ModelUtils;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yangankang
 */
@APIController
public class GoodsController {

    @Printer
    public ResponseMessage addGoods(HttpServletRequest request, ModelObject object) {
        try {
            ModelObject user = AdminController.getLoginUer(request);
            int adminId = user.getIntValue(TableAdmin.id);
            GlobalService.goodsService.addGoods(adminId, object, true);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponsePageMessage getGoods(ModelObject search, int start, int limit, String gid) {
        int intGid = 0;
        if (StringUtils.isNotBlank(gid)) intGid = Integer.parseInt(gid);
        Paging paging = GlobalService.goodsService.getGoods(search, start, limit, intGid);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponsePageMessage getRecycleGoods(int start, int limit, String name) {
        return new ResponsePageMessage(GlobalService.goodsService.getRecycleGoods(start, limit, name));
    }

    @Printer
    public ResponseMessage deleteRecycleGoods(List<Integer> ids) {
        for (int id : ids) {
            try {
                GlobalService.goodsService.deleteEntityGoods(id);
            } catch (Exception e) {
                return new ResponseMessage(e);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage resetRecycleGoods(List<Integer> ids) {
        for (int id : ids) {
            GlobalService.goodsService.recoverGoods(id);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage deleteGoods(List<Integer> ids) {
        for (int id : ids) {
            GlobalService.goodsService.deleteGoods(id);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage setGoodsPutAway(List<Integer> ids) {
        try {
            for (int id : ids) {
                GlobalService.goodsService.setGoodsSaleStatus(id, true);
            }
        } catch (ModuleException e) {
            e.printStackTrace();
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage setGoodSoldOut(List<Integer> ids) {
        try {
            for (int id : ids) {
                GlobalService.goodsService.setGoodsSaleStatus(id, false);
            }
        } catch (ModuleException e) {
            e.printStackTrace();
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateGoodsSales(ModelObject object, List<ModelObject> sku) {
        try {
            GlobalService.goodsService.updateGoodsSales(object, sku);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateGoodsTitle(ModelObject object) {
        try {
            GlobalService.goodsService.updateGoodsTitle(object);
        } catch (ModuleException e) {
            e.printStackTrace();
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateGoodsBrand(List<Long> ids, int bid) {
        GlobalService.goodsService.updateGoodsBrand(ids, bid);
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateGoodsValidTime(List<Integer> ids, long time) {
        GlobalService.goodsService.updateGoodsValidTime(ids, time);
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage getSingleGoods(int id) {
        return new ResponseMessage(GlobalService.goodsService.getGoodsById(id));
    }

    @Printer
    public ResponseMessage updateGoods(HttpServletRequest request, ModelObject object) {
        ModelObject user = AdminController.getLoginUer(request);
        int adminId = user.getIntValue(TableAdmin.id);
        try {
            GlobalService.goodsService.updateGoods(adminId, object, true);
            return new ResponseMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseMessage(e);
        }
    }

    @AuthSkipCheck
    @AdminSkipLoginCheck
    @Printer
    public ResponseMessage checkGoodsValidTime() {
        GlobalService.goodsService.checkGoodsValidTime();
        return new ResponseMessage();
    }

    @Printer
    @AuthSkipCheck
    public ResponseMessage getExtModules() {
        List<ModelObject> objects = GlobalService.goodsGroupService.getListGroups();
        if (objects != null) {
            List<ModelObject> modules = new ArrayList<>();
            for (ModelObject object : objects) {
                ModelObject module = new ModelObject();
                module.put(TableModule.id, "goods_" + object.getIntValue(TableGoodsGroup.id));
                module.put(TableModule.pid, "goods_" + object.getIntValue(TableGoodsGroup.pid));
                module.put(TableModule.text, object.get(TableGoodsGroup.name)
                        + " <span style='color:red'>(" + object.getLongValue(TableGoodsGroup.count) + ")</span>");
                module.put("originalId", object.getIntValue(TableGoodsGroup.id));
                module.put("isSplitName", true);
                module.put("firstName", "商品列表管理");
                module.put("lastName", object.get(TableGoodsGroup.name));
                module.put(TableModule.expanded, object.get(TableGoodsGroup.expanded));
                module.put(TableModule.leaf, object.get(TableGoodsGroup.leaf));
                module.put(TableModule.type, "normal");
                module.put(TableModule.module, "App.goods.GoodsManager");
                module.put(TableModule.top, 0);
                module.put(TableModule.tabIcon, "image/micon/product.png");
                modules.add(module);
            }
            List<ModelObject> trees = ModelUtils.getListToTree(modules, "id", "pid", "children");
            return new ResponseMessage(trees);
        }
        return new ResponseMessage();
    }
}
