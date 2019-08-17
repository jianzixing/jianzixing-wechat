package com.jianzixing.webapp.admin;

import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import com.jianzixing.webapp.service.GlobalService;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

import java.util.List;

@APIController
public class CouponController {

    @Printer
    public ResponseMessage getCouponInit() {
        List<ModelObject> levels = GlobalService.userLevelService.getLevels();

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
        init.put("levels", levels);
        return new ResponseMessage(init);
    }

    @Printer
    public ResponseMessage addCoupon(ModelObject object) {
        try {
            GlobalService.couponService.saveCoupon(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage delCoupons(List<Integer> ids) {
        try {
            if (ids != null) {
                for (int id : ids) {
                    GlobalService.couponService.delCoupon(id);
                }
            }
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateCoupon(ModelObject object) {
        try {
            GlobalService.couponService.updateCoupon(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponsePageMessage getCoupons(ModelObject search, int start, int limit) {
        Paging paging = GlobalService.couponService.getCoupons(search, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage enableCoupons(List<Long> ids) {
        try {
            if (ids != null) {
                for (long id : ids) {
                    GlobalService.couponService.enableCoupon(id);
                }
            }
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage finishCoupons(List<Long> ids) {
        try {
            if (ids != null) {
                for (long id : ids) {
                    GlobalService.couponService.finishCoupon(id);
                }
            }
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage addCouponGoods(long id, List<ModelObject> gids) {
        try {
            GlobalService.couponService.addCouponGoods(id, gids);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage removeCouponGoods(long id, List<ModelObject> gids) {
        if (gids != null && gids.size() > 0) {
            for (ModelObject gidObj : gids) {
                long gid = gidObj.getLongValue("gid");
                long skuId = gidObj.getLongValue("skuId");
                if (gid > 0) {
                    GlobalService.couponService.removeCouponGoods(id, gid, skuId);
                }
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponsePageMessage getCouponGoods(long cid, int start, int limit) {
        Paging paging = GlobalService.couponService.getCouponGoods(cid, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage getUserCoupons(ModelObject search, long cid, int start, int limit) {
        Paging paging = GlobalService.couponService.getUserCoupons(search, cid, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage declareUserCoupons(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.couponService.declareUserCoupon(id);
            }
        }
        return new ResponseMessage();
    }
}
