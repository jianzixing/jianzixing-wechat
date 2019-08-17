package com.jianzixing.webapp.web;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.handler.WebLoginHolder;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.collect.CollectType;
import com.jianzixing.webapp.service.coupon.CouponChannelType;
import com.jianzixing.webapp.service.history.HistoryType;
import com.jianzixing.webapp.service.order.PlatformType;
import org.mimosaframework.core.utils.StringTools;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.exception.TransactionException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class WebWXGoodsController {

    @RequestMapping("/wx/goods_detail")
    public ModelAndView detail(ModelAndView view,
                               @RequestParam(defaultValue = "0") long id,
                               @RequestParam(defaultValue = "0") long skuId) {
        if (id > 0) {
            long uid = WebLoginHolder.getUid();
            ModelObject goods = GlobalService.goodsService.getViewGoodsById(id);
            view.addObject("goods", goods);
            List<ModelObject> comments = GlobalService.goodsCommentService.getTopPriorityCommentByGid(id, 2, 5);
            view.addObject("comments", comments);

            long commentCount = GlobalService.goodsCommentService.getCommentCountByGid(id);
            long goodCommentCount = GlobalService.goodsCommentService.getGoodCommentCount(id);
            long middleCommentCount = GlobalService.goodsCommentService.getMiddleCommentCount(id);
            long badCommentCount = GlobalService.goodsCommentService.getBadCommentCount(id);
            long imageCommentCount = GlobalService.goodsCommentService.getImageCommentCount(id);
            view.addObject("commentCount", StringTools.getDimNumber(commentCount, "万条+", "+"));
            view.addObject("goodCommentCount", StringTools.getDimNumber(goodCommentCount, "万条+", "+"));
            view.addObject("middleCommentCount", StringTools.getDimNumber(middleCommentCount, "万条+", "+"));
            view.addObject("badCommentCount", StringTools.getDimNumber(badCommentCount, "万条+", "+"));
            view.addObject("imageCommentCount", StringTools.getDimNumber(imageCommentCount, "万条+", "+"));
            view.addObject("commentGoodRate", goodCommentCount == 0 ? 0 : ((goodCommentCount * 100) / commentCount));

            List<ModelObject> coupons = GlobalService.couponService.getCouponsByGid(uid, id, skuId);
            view.addObject("coupons", coupons);
            List<ModelObject> discounts = GlobalService.discountService.getUserDiscountByGoods(uid, id, skuId, PlatformType.ALL);
            view.addObject("discounts", discounts);
            List<ModelObject> supports = GlobalService.supportService.getSupportsByGoods(id);
            view.addObject("supports", supports);

            if (uid != 0) {
                boolean isCollect = GlobalService.collectService.isCollect(uid, id, CollectType.GOODS);
                view.addObject("isCollect", isCollect);
            }
        }
        view.setViewName("wx/goods_detail");
        return view;
    }

    @RequestMapping("/wx/collect_goods")
    @ResponseBody
    public String setCollectGoods(@RequestParam(defaultValue = "0") long gid,
                                  @RequestParam(defaultValue = "0") long skuId) {
        if (WebLoginHolder.isLogin()) {
            long uid = WebLoginHolder.getUid();
            GlobalService.collectService.addCollect(uid, gid, skuId, CollectType.GOODS);
            return "ok";
        }
        return "not_login";
    }

    @RequestMapping("/wx/see_history_goods")
    @ResponseBody
    public String setHistoryGoods(@RequestParam(defaultValue = "0") long gid) {
        if (WebLoginHolder.isLogin()) {
            long uid = WebLoginHolder.getUid();
            GlobalService.historyService.saveHistory(uid, gid, HistoryType.GOODS);
            return "ok";
        }
        return "not_login";
    }

    @RequestMapping("/wx/rm_collect_goods")
    @ResponseBody
    public String removeCollectGoods(@RequestParam(defaultValue = "0") long gid) {
        long uid = WebLoginHolder.getUid();
        if (uid == 0) return "not_login";
        GlobalService.collectService.delCollect(uid, gid, CollectType.GOODS);
        return "ok";
    }

    @RequestMapping("/wx/get_goods_coupon")
    @ResponseBody
    public String getGoodsCoupon(@RequestParam(defaultValue = "0") long cid) {
        long uid = WebLoginHolder.getUid();
        if (uid == 0) return "not_login";
        try {
            GlobalService.couponService.userGetCoupon(uid, cid, CouponChannelType.WEB);
            return "ok";
        } catch (TransactionException e) {
            e.printStackTrace();
            return "server_error";
        } catch (ModuleException e) {
            e.printStackTrace();
            return e.getCode().toString();
        }
    }
}
