package com.jianzixing.webapp.web;

import com.jianzixing.webapp.handler.WebLoginHolder;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.coupon.CouponUserStatus;
import com.jianzixing.webapp.tables.coupon.TableCoupon;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.calculator.CalcNumber;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
public class WebWXMineCouponController {

    @RequestMapping("/wx/mine/coupon")
    public ModelAndView mineCoupon(HttpServletRequest request,
                                   ModelAndView view) {
        if (WebLoginHolder.isLogin(view, request)) {
            view.setViewName("wx/mine/coupon");
            long notUseCount = GlobalService.couponService.getCouponsCountByUser(WebLoginHolder.getUid(), CouponUserStatus.NORMAL);
            long usedCount = GlobalService.couponService.getCouponsCountByUser(WebLoginHolder.getUid(), CouponUserStatus.USED);
            long expiredCount = GlobalService.couponService.getCouponsCountByUser(WebLoginHolder.getUid(), CouponUserStatus.EXPIRED);

            view.addObject("notUseCount", notUseCount);
            view.addObject("usedCount", usedCount);
            view.addObject("expiredCount", expiredCount);
            view.addObject("type", 0);
        }
        return view;
    }

    @RequestMapping("/wx/mine/coupon_list")
    @ResponseBody
    public String getCouponList(@RequestParam(defaultValue = "0") String type,
                                @RequestParam(defaultValue = "1") int page) {
        ModelObject json = new ModelObject();
        json.put("success", 1);
        if (WebLoginHolder.isLogin()) {
            List<ModelObject> objects = null;
            if (type.equals("0")) {
                objects = GlobalService.couponService.getCouponsByUser(WebLoginHolder.getUid(), page, CouponUserStatus.NORMAL);
            }
            if (type.equals("1")) {
                objects = GlobalService.couponService.getCouponsByUser(WebLoginHolder.getUid(), page, CouponUserStatus.USED);
            }
            if (type.equals("2")) {
                objects = GlobalService.couponService.getCouponsByUser(WebLoginHolder.getUid(), page, CouponUserStatus.EXPIRED);
            }
            if (objects != null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
                for (ModelObject object : objects) {
                    ModelObject coupon = object.getModelObject(TableCoupon.class);
                    BigDecimal orderPrice = coupon.getBigDecimal(TableCoupon.orderPrice);
                    BigDecimal couponPrice = coupon.getBigDecimal(TableCoupon.couponPrice);
                    coupon.put(TableCoupon.orderPrice, CalcNumber.as(orderPrice).toPrice());
                    coupon.put(TableCoupon.couponPrice, CalcNumber.as(couponPrice).toPrice());
                    if (coupon.isNotEmpty(TableCoupon.startTime))
                        coupon.put(TableCoupon.startTime, format.format(coupon.getDate(TableCoupon.startTime)));
                    if (coupon.isNotEmpty(TableCoupon.finishTime))
                        coupon.put(TableCoupon.finishTime, format.format(coupon.getDate(TableCoupon.finishTime)));
                    object.put(TableCoupon.class, coupon);
                }
            }
            json.put("data", objects);
        } else {
            json.put("success", 0);
            json.put("code", "not_login");
        }
        return json.toJSONString();
    }
}
