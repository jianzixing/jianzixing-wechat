package com.jianzixing.webapp.web;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.handler.WebLoginHolder;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.order.TableUserAddress;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.StringTools;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class WebWXMineAddressController {

    @RequestMapping("/wx/mine/address_list")
    public ModelAndView addressList(HttpServletRequest request,
                                    ModelAndView view,
                                    @RequestParam(defaultValue = "0") long addrid) {
        if (WebLoginHolder.isLogin(view, request)) {
            List<ModelObject> objects = GlobalService.userAddressService.getUserAddressByUid(WebLoginHolder.getUid());
            if (objects != null) {
                for (ModelObject addr : objects) {
                    addr.put(TableUserAddress.phoneNumber, StringTools.replace(addr.getString(TableUserAddress.phoneNumber), 3, 7, "****"));
                    if (addrid > 0) {
                        addr.put("selected", false);
                        if (addrid > 0) {
                            if (addr.getLongValue(TableUserAddress.id) == addrid) {
                                addr.put("selected", true);
                            }
                        } else {
                            if (addr.getIntValue(TableUserAddress.isDefault) == 1) {
                                addr.put("selected", true);
                            }
                        }
                    }
                }
            }
            if (addrid > 0) {
                view.addObject("isSelMod", true);
            }
            view.addObject("addresses", objects);
            view.setViewName("wx/mine/address_list");
        }
        return view;
    }

    @RequestMapping("/wx/mine/address_add")
    public ModelAndView addressAdd(HttpServletRequest request, ModelAndView view) {
        if (WebLoginHolder.isLogin(view, request)) {
            view.addObject("isEdit", false);
            view.setViewName("wx/mine/address");
        }
        return view;
    }

    @RequestMapping("/wx/mine/address_modify")
    public ModelAndView addressModify(HttpServletRequest request,
                                      ModelAndView view,
                                      @RequestParam(defaultValue = "0") long aid) {
        if (WebLoginHolder.isLogin(view, request)) {
            ModelObject address = GlobalService.userAddressService.getUserAddressById(WebLoginHolder.getUid(), aid);
            view.addObject("address", address);
            view.addObject("isEdit", true);
            view.setViewName("wx/mine/address");
        }
        return view;
    }

    @RequestMapping("/wx/mine/address_provinces")
    @ResponseBody
    public String getProvinces() {
        List<ModelObject> provinces = GlobalService.areaService.getChinaProvince();
        return ModelObject.toJSONString(provinces);
    }

    @RequestMapping("/wx/mine/address_city")
    @ResponseBody
    public String getCities(String pid) {
        if (StringUtils.isNotBlank(pid)) {
            List<ModelObject> provinces = GlobalService.areaService.getChinaCity(Integer.parseInt(pid));
            return ModelObject.toJSONString(provinces);
        }
        return null;
    }

    @RequestMapping("/wx/mine/address_county")
    @ResponseBody
    public String getCounties(String cid) {
        if (StringUtils.isNotBlank(cid)) {
            List<ModelObject> provinces = GlobalService.areaService.getChinaArea(Integer.parseInt(cid));
            return ModelObject.toJSONString(provinces);
        }
        return null;
    }

    @RequestMapping("/wx/mine/address_add_submit")
    @ResponseBody
    public String addressAddSubmit(String data) {
        if (WebLoginHolder.isLogin()) {
            ModelObject address = ModelObject.parseObject(data);
            try {
                address.put(TableUserAddress.userId, WebLoginHolder.getUid());
                GlobalService.userAddressService.addUserAddress(address);
                return "ok";
            } catch (ModelCheckerException e) {
                e.printStackTrace();
                return "miss_params";
            } catch (ModuleException e) {
                e.printStackTrace();
                return "miss_params";
            }
        }
        return "not_login";
    }

    @RequestMapping("/wx/mine/address_modify_submit")
    @ResponseBody
    public String addressModifySubmit(String data) {
        if (WebLoginHolder.isLogin()) {
            ModelObject address = ModelObject.parseObject(data);
            try {
                if (address.isNotEmpty(TableUserAddress.id)) {
                    address.put(TableUserAddress.userId, WebLoginHolder.getUid());
                    GlobalService.userAddressService.updateUserAddress(address);
                    return "ok";
                }
                return "miss_pk";
            } catch (ModelCheckerException e) {
                e.printStackTrace();
                return "miss_params";
            } catch (ModuleException e) {
                e.printStackTrace();
                return "miss_params";
            }
        }
        return "not_login";
    }

    @RequestMapping("/wx/mine/address_del_submit")
    @ResponseBody
    public String addressDelSubmit(@RequestParam(defaultValue = "0") long addrid) {
        if (WebLoginHolder.isLogin()) {
            GlobalService.userAddressService.deleteUserAddress(WebLoginHolder.getUid(), addrid);
            return "ok";
        }
        return "not_login";
    }
}
