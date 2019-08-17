package com.jianzixing.webapp.web;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.handler.WebLoginHolder;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.aftersales.AfterSalesStatus;
import com.jianzixing.webapp.service.aftersales.AfterSalesType;
import com.jianzixing.webapp.service.order.OrderStatus;
import com.jianzixing.webapp.tables.aftersales.TableAfterSales;
import com.jianzixing.webapp.tables.aftersales.TableAfterSalesAddress;
import com.jianzixing.webapp.tables.aftersales.TableAfterSalesImages;
import com.jianzixing.webapp.tables.order.TableOrder;
import com.jianzixing.webapp.tables.order.TableOrderGoods;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.exception.TransactionException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WebWXMineAfterSaleController {


    @RequestMapping("/wx/mine/after_sale")
    public ModelAndView addressAfterSale(HttpServletRequest request,
                                         ModelAndView view) {
        if (WebLoginHolder.isLogin(view, request)) {
            view.setViewName("wx/mine/after_sale");
            long processCount = GlobalService.afterSalesService.getAfterSaleCount(1);
            view.addObject("processCount", processCount);
        }
        return view;
    }

    @RequestMapping("/wx/mine/after_sale_order")
    @ResponseBody
    public String getOrderByAfterSales(String keyword,
                                       @RequestParam(defaultValue = "1") int page) {
        ModelObject object = new ModelObject();
        object.put("success", 1);
        if (WebLoginHolder.isLogin()) {
            List<ModelObject> orders =
                    GlobalService.afterSalesService.getOrderByAfterSales(WebLoginHolder.getUid(), keyword, page);
            object.put("data", orders);
        } else {
            object.put("success", 0);
            object.put("code", "not_login");
        }
        return object.toJSONString();
    }

    @RequestMapping("/wx/mine/after_sale_list_proc")
    @ResponseBody
    public String getProcessByAfterSales(@RequestParam(defaultValue = "1") int page) {
        ModelObject object = new ModelObject();
        object.put("success", 1);
        if (WebLoginHolder.isLogin()) {
            List<ModelObject> orders =
                    GlobalService.afterSalesService.getProcessAfterSaleList(WebLoginHolder.getUid(), page);
            object.put("data", orders);
        } else {
            object.put("success", 0);
            object.put("code", "not_login");
        }
        return object.toJSONString();
    }

    @RequestMapping("/wx/mine/after_sale_list")
    @ResponseBody
    public String getListByAfterSales(String keyword,
                                      @RequestParam(defaultValue = "1") int page) {
        ModelObject object = new ModelObject();
        object.put("success", 1);
        if (WebLoginHolder.isLogin()) {
            List<ModelObject> orders =
                    GlobalService.afterSalesService.getAfterSaleList(WebLoginHolder.getUid(), keyword, page);
            object.put("data", orders);
        } else {
            object.put("success", 0);
            object.put("code", "not_login");
        }
        return object.toJSONString();
    }

    @RequestMapping("/wx/mine/after_sale_detail")
    public ModelAndView addressAfterSaleDetail(HttpServletRequest request,
                                               ModelAndView view,
                                               String n) {
        if (WebLoginHolder.isLogin(view, request)) {
            view.setViewName("wx/mine/after_sale_detail");
            ModelObject detail = GlobalService.afterSalesService.getUserAfterSaleDetail(WebLoginHolder.getUid(), n);
            view.addObject("afterSale", detail);
            List<ModelObject> companies = GlobalService.logisticsService.getCompanies();
            view.addObject("companies", companies);
            ModelObject refunds = GlobalService.afterSalesService.getAfterSalesRefund(detail.getLongValue(TableAfterSales.id));
            view.addObject("refunds", refunds);

            // 将售后状态简化为4中状态
            view.addObject("status_1", false);
            view.addObject("status_2", false);
            view.addObject("status_3", false);
            view.addObject("status_4", false);
            int status = detail.getIntValue(TableAfterSales.status);
            if (status >= 0) {
                view.addObject("status_1", true);
            }
            if (status == 20 || (status >= 30 && status <= 81) || status == 100) {
                view.addObject("status_2", true);
            }
            if ((status >= 40 && status <= 81) || status == 100) {
                view.addObject("status_3", true);
            }
            if (status == 100) {
                view.addObject("status_4", true);
            }
        }
        return view;
    }

    @RequestMapping("/wx/mine/after_sale_process")
    public ModelAndView addressAfterSaleProcess(HttpServletRequest request,
                                                ModelAndView view,
                                                String n) {
        if (WebLoginHolder.isLogin(view, request)) {
            view.setViewName("wx/mine/after_sale_process");

            List<ModelObject> objects = GlobalService.afterSalesService.getAfterSaleProcess(WebLoginHolder.getUid(), n);
            view.addObject("processes", objects);
        }
        return view;
    }

    @RequestMapping("/wx/mine/after_sale_start")
    public ModelAndView addressAfterSaleStart(HttpServletRequest request,
                                              ModelAndView view,
                                              @RequestParam(defaultValue = "0") long og) {
        if (WebLoginHolder.isLogin(view, request)) {
            view.setViewName("wx/mine/after_sale_start");
            ModelObject orderGoods = GlobalService.afterSalesService.getAfterSaleOrderGoods(WebLoginHolder.getUid(), og);
            if (orderGoods != null) {
                view.addObject("orderGoods", orderGoods);
                ModelObject order = orderGoods.getModelObject(TableOrder.class);
                if (order.getIntValue(TableOrder.status) == OrderStatus.RECEIVE.getCode()) {
                    ModelObject supports = GlobalService.supportService.getAfterSalesByOrder(order, orderGoods.getLongValue(TableOrderGoods.goodsId));
                    view.addObject("supports", supports);
                } else {
                    view.addObject("statusError", true);
                }
            }
        }
        return view;
    }

    @RequestMapping("/wx/mine/after_sale_form")
    public ModelAndView addressAfterSaleForm(HttpServletRequest request,
                                             ModelAndView view,
                                             @RequestParam(defaultValue = "0") long og,
                                             @RequestParam(defaultValue = "0") int type,
                                             @RequestParam(defaultValue = "0") int amount) {
        if (WebLoginHolder.isLogin(view, request)) {
            view.addObject("type", type);
            view.addObject("applyForAmount", amount);
            view.setViewName("wx/mine/after_sale_form");

            ModelObject orderGoods = GlobalService.afterSalesService.getAfterSaleOrderGoods(WebLoginHolder.getUid(), og);
            view.addObject("orderGoods", orderGoods);

            int buyAmount = orderGoods.getIntValue(TableOrderGoods.amount);
            if (buyAmount < amount) {
                view.addObject("applyForAmount", 0);
            }

            if (type == 2 || type == 3) {
                ModelObject address = GlobalService.orderService.getOrderAddress(orderGoods.getLongValue(TableOrderGoods.orderId));
                view.addObject("address", address);
            }
        }
        return view;
    }

    @RequestMapping("/wx/mine/after_sale_form_submit")
    @ResponseBody
    public String submitAfterSaleForm(HttpServletRequest request,
                                      @RequestParam(defaultValue = "0") long og,
                                      @RequestParam(defaultValue = "0") int type,
                                      @RequestParam(defaultValue = "0") int amount,
                                      String reason,
                                      String detail) {
        ModelObject object = new ModelObject();
        object.put("success", 1);
        if (WebLoginHolder.isLogin()) {
            List<String> files = GlobalService.fileService.uploadFiles(request);
            ModelObject afterSales = new ModelObject();
            afterSales.put(TableAfterSales.orderGoodsId, og);
            afterSales.put(TableAfterSales.type, AfterSalesType.get(type * 10).getCode());
            afterSales.put(TableAfterSales.userId, WebLoginHolder.getUid());
            afterSales.put(TableAfterSales.amount, amount);
            afterSales.put(TableAfterSales.reason, reason);
            afterSales.put(TableAfterSales.detail, detail);
            if (files != null && files.size() > 0) {
                List<ModelObject> images = new ArrayList<>();
                for (String file : files) {
                    ModelObject img = new ModelObject();
                    img.put(TableAfterSalesImages.fileName, file);
                    images.add(img);
                }
                afterSales.put("images", images);
            }

            try {
                GlobalService.afterSalesService.addAfterSales(afterSales);
                object.put("number", afterSales.getString(TableAfterSales.number));
            } catch (ModuleException e) {
                e.printStackTrace();
                object.put("success", 0);
                object.put("code", e.getCode().toString());
            } catch (Exception e) {
                e.printStackTrace();
                object.put("success", 0);
                object.put("code", "server_error");
            }
        } else {
            object.put("success", 0);
            object.put("code", "not_login");
        }
        return object.toJSONString();
    }

    @RequestMapping("/wx/mine/after_sale_cancel")
    @ResponseBody
    public String cancelAfterSales(String n) {
        ModelObject object = new ModelObject();
        object.put("success", 1);
        if (WebLoginHolder.isLogin()) {
            try {
                GlobalService.afterSalesService.cancelUserAfterSalesByNumber(WebLoginHolder.getUid(), n);
            } catch (ModuleException e) {
                e.printStackTrace();
                object.put("success", 0);
                object.put("code", e.getCode().toString());
            }
        } else {
            object.put("success", 0);
            object.put("code", "not_login");
        }
        return object.toJSONString();
    }


    @RequestMapping("/wx/mine/after_sale_reback")
    @ResponseBody
    public String rebackAfterSales(String n,
                                   String code,
                                   String name,
                                   String number) {
        ModelObject object = new ModelObject();
        object.put("success", 1);
        if (WebLoginHolder.isLogin()) {
            try {
                ModelObject as = new ModelObject();
                as.put(TableAfterSales.number, n);
                as.put(TableAfterSalesAddress.lgsCompanyCode, code);
                as.put(TableAfterSalesAddress.lgsCompanyName, name);
                as.put(TableAfterSalesAddress.trackingNumber, number);
                GlobalService.afterSalesService.deliveryGoodsByUser(WebLoginHolder.getUid(), as);
            } catch (ModuleException e) {
                e.printStackTrace();
                object.put("success", 0);
                object.put("code", e.getCode().toString());
            } catch (TransactionException e) {
                e.printStackTrace();
                object.put("success", 0);
                object.put("code", "server_error");
            }
        } else {
            object.put("success", 0);
            object.put("code", "not_login");
        }
        return object.toJSONString();
    }
}
