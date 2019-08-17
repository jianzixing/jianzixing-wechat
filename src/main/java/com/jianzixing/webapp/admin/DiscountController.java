package com.jianzixing.webapp.admin;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.discount.DiscountUtils;
import com.jianzixing.webapp.tables.discount.TableDiscount;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

import java.util.List;

@APIController
public class DiscountController {

    @Printer
    public ResponseMessage addDiscount(ModelObject object) {
        try {
            object.setObjectClass(TableDiscount.class);
            object.checkAndThrowable();
            int type = object.getIntValue("type");
            if (type == 0) { // 商品分类
                // 排除的商品
                String fileName = object.getString("cidExcludeFile");
                List<ModelObject> goods = DiscountUtils.getGoodsFromFileBySerialNumber(fileName);
                object.put("cidExcludes", goods);
            }
            if (type == 1) { // 商品
                // 添加的商品
                String fileName = object.getString("gidIncludeFile");
                List<ModelObject> goods = DiscountUtils.getGoodsFromFileBySerialNumber(fileName);
                object.put("gidIncludes", goods);
            }
            if (type == 2) { // 品牌
                // 排除的商品
                String fileName = object.getString("bidExcludeFile");
                List<ModelObject> goods = DiscountUtils.getGoodsFromFileBySerialNumber(fileName);
                object.put("bidExcludes", goods);
            }
            GlobalService.discountService.addDiscount(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage deleteDiscount(List<Long> ids) {
        if (ids != null) {
            for (long id : ids) {
                GlobalService.discountService.deleteDiscount(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponsePageMessage getDiscounts(ModelObject search, long start, long limit) {
        Paging paging = GlobalService.discountService.getDiscounts(search, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponsePageMessage getDiscountGoods(long did, int start, int limit) {
        Paging paging = GlobalService.discountService.getDiscountGoods(did, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage addDiscountGoods(long did, List<ModelObject> gids) {
        try {
            GlobalService.discountService.addDiscountGoods(did, gids);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage removeDiscountGoods(long id, List<ModelObject> gids) {
        if (gids != null && gids.size() > 0) {
            for (ModelObject gidObj : gids) {
                long gid = gidObj.getLongValue("gid");
                long skuId = gidObj.getLongValue("skuId");
                if (gid > 0) {
                    GlobalService.discountService.removeDiscountGoods(id, gid, skuId);
                }
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage getDiscount(long id) {
        ModelObject object = GlobalService.discountService.getDiscountById(id);
        return new ResponseMessage(object);
    }

    @Printer
    public ResponsePageMessage getSimpleDiscountGoods(long did, long start, long limit) {
        Paging paging = GlobalService.discountService.getSimpleDiscountGoods(did, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage updateDiscount(ModelObject object) {
        try {
            GlobalService.discountService.updateDiscount(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage getDiscountInit() {
        List<ModelObject> impls = GlobalService.discountService.getImpls();
        List<ModelObject> groups = GlobalService.goodsGroupService.getGroups();
        List<ModelObject> levels = GlobalService.userLevelService.getLevels();
        List<ModelObject> platforms = GlobalService.discountService.getPlatforms();

        if (levels != null) {
            ModelObject allLevel = new ModelObject();
            allLevel.put("detail", "全部等级");
            allLevel.put("endAmount", 0);
            allLevel.put("id", 0);
            allLevel.put("name", "全部等级");
            allLevel.put("startAmount", 0);
            levels.add(0, allLevel);
        }

        ModelObject init = new ModelObject();
        init.put("impls", impls);
        init.put("groups", groups);
        init.put("levels", levels);
        init.put("platforms", platforms);
        return new ResponseMessage(init);
    }

    @Printer
    public ResponseMessage enableDiscount(List<Long> ids) {
        if (ids != null) {
            for (long id : ids) {
                try {
                    GlobalService.discountService.enableDiscount(id);
                } catch (Exception e) {
                    return new ResponseMessage(e);
                }
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage disableDiscount(List<Long> ids) {
        if (ids != null) {
            for (long id : ids) {
                try {
                    GlobalService.discountService.disableDiscount(id);
                } catch (Exception e) {
                    return new ResponseMessage(e);
                }
            }
        }
        return new ResponseMessage();
    }
}
