package com.jianzixing.webapp.web;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.marketing.EmailParams;
import com.jianzixing.webapp.service.marketing.SmsAction;
import com.jianzixing.webapp.service.trigger.EventType;
import com.jianzixing.webapp.tables.marketing.TableMarketSmsAuthCode;
import com.jianzixing.webapp.tables.user.TableUser;
import org.mimosaframework.core.utils.CookieUtils;
import org.mimosaframework.core.utils.RequestUtils;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelBuilder;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.RandomUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
public class WebWXUserController {

    @RequestMapping("/wx/login")
    public ModelAndView login(ModelAndView view, String r) {
        view.setViewName("wx/login");
        view.addObject("r", r != null ? r : "");
        return view;
    }

    @RequestMapping("/wx/go_auth")
    public ModelAndView goAuth(HttpServletRequest request,
                               ModelAndView view,
                               String r) {
        view.setViewName("wx/go_auth");

        try {
            if (StringUtils.isBlank(r)) {
                r = RequestUtils.getWebUrl(request) + "/wx/index.jhtml";
            }
            String url = AbstractWeChatController.getDefaultUserAuthUser(request, r);
            view.addObject("r", url);
        } catch (Exception e) {
            e.printStackTrace();
            view.addObject("error_msg", e.getMessage());
        }
        return view;
    }

    @RequestMapping("/wx/login_submit")
    @ResponseBody
    public String loginSubmitUser(HttpServletRequest request,
                                  HttpServletResponse response,
                                  String userName,
                                  String password) {
        try {
            ModelObject user = GlobalService.userService.login(userName, password);
            if (user != null) {
                int enable = user.getIntValue(TableUser.enable);
                if (enable == 1) {
                    ModelObject info = new ModelObject();
                    info.put(TableUser.id, user.getLongValue(TableUser.id));
                    info.put("uid", user.getLongValue(TableUser.id));
                    info.put(TableUser.token, user.getString(TableUser.token));
//                    CookieUtils.addCookie("user", info.toJSONString(), Integer.MAX_VALUE, "/", response);
                    CookieUtils.addCookie("user", URLEncoder.encode(info.toJSONString(), "utf-8"), Integer.MAX_VALUE, "/", response);
                    return "ok";
                } else {
                    return "user_disable";
                }
            }
        } catch (ModuleException e) {
            e.printStackTrace();
            if (e.getCode() == StockCode.ARG_VALID) {
                return "pwd_valid";
            }
            if (e.getCode() == StockCode.ARG_NULL) {
                return "user_empty";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "fail";
    }

    @RequestMapping("/wx/register")
    public ModelAndView register(ModelAndView view,
                                 String r) {
        view.setViewName("wx/register");
        view.addObject("r", r != null ? r : "");
        return view;
    }

    @RequestMapping("/wx/forget_pwd")
    public ModelAndView forgetPassword(ModelAndView view,
                                       String r) {
        view.setViewName("wx/forget_pwd");
        view.addObject("r", r != null ? r : "");
        return view;
    }

    @RequestMapping("/wx/register_sms")
    @ResponseBody
    public String sendRegisterSms(String phone) {
        if (StringUtils.isNotBlank(phone)) {
            phone = phone.trim();
            ModelObject record = GlobalService.smsService.getSmsAuthCode("REGISTER", phone);
            if (record == null
                    || record.getDate(TableMarketSmsAuthCode.createTime).getTime() < (System.currentTimeMillis() - 120 * 1000l)) {
                long number = RandomUtils.randomNumber(6);
                GlobalService.triggerService.trigger(
                        0,
                        EventType.REGISTER_SEND_SMS,
                        ModelBuilder.create()
                                .put("phones", phone)
                                .put("code", number)
                                .put("action", SmsAction.CODE.getCode())
                                .toRootObject()
                );
                GlobalService.smsService.addSmsAuthCode("REGISTER", phone, String.valueOf(number));
                return "ok";
            } else {
                return "time_in";
            }
        }
        return "args_null";
    }

    @RequestMapping("/wx/register_submit")
    @ResponseBody
    public String registerSubmitUser(String phone, String password, String code) {

        ModelObject user = GlobalService.userService.getUserByPhone(phone);
        if (user == null) {
            ModelObject record = GlobalService.smsService.getSmsAuthCode("REGISTER", phone);
            if (record == null) {
                return "empty_code";
            }
            if (record.getDate(TableMarketSmsAuthCode.createTime).getTime()
                    < (System.currentTimeMillis() - 10 * 60 * 1000l)) {
                return "code_expire";
            }
            if (!record.getString(TableMarketSmsAuthCode.code).equals(code)) {
                return "code_ne";
            }
            GlobalService.userService.registerByPhone(phone, password);
            return "ok";
        } else {
            return "user_exist";
        }
    }

    @RequestMapping("/wx/forget_pwd_sms")
    @ResponseBody
    public String sendForgetPwdSms(String phone) {
        if (StringUtils.isNotBlank(phone)) {
            phone = phone.trim();
            ModelObject record = GlobalService.smsService.getSmsAuthCode("FORGET_PWD", phone);
            if (record == null
                    || record.getDate(TableMarketSmsAuthCode.createTime).getTime() < (System.currentTimeMillis() - 120 * 1000l)) {

                ModelObject user = GlobalService.userService.getUserByPhone(phone);
                if (user != null) {
                    long number = RandomUtils.randomNumber(6);
                    GlobalService.triggerService.trigger(
                            user.getLongValue(TableUser.id),
                            EventType.FORGET_PWD_SEND_SMS,
                            ModelBuilder.create()
                                    .put("userId", user.getLongValue(TableUser.id))
                                    .put("phones", phone)
                                    .put("code", number)
                                    .put("action", SmsAction.CODE.getCode())
                                    .toRootObject()
                    );
                    GlobalService.smsService.addSmsAuthCode("FORGET_PWD", phone, String.valueOf(number));
                }
                return "ok";
            } else {
                return "time_in";
            }
        }
        return "args_null";
    }

    @RequestMapping("/wx/forget_pwd_submit")
    @ResponseBody
    public String forgetPwdSubmitUser(String phone, String password, String code) {

        ModelObject user = GlobalService.userService.getUserByPhone(phone);
        if (user != null) {
            ModelObject record = GlobalService.smsService.getSmsAuthCode("FORGET_PWD", phone);
            if (record == null) {
                return "empty_code";
            }
            if (record.getDate(TableMarketSmsAuthCode.createTime).getTime()
                    < (System.currentTimeMillis() - 10 * 60 * 1000l)) {
                return "code_expire";
            }
            if (!record.getString(TableMarketSmsAuthCode.code).equals(code)) {
                return "code_ne";
            }
            GlobalService.userService.updateUserPassword(user.getLongValue(TableUser.id), password);
            return "ok";
        } else {
            return "user_not_exit";
        }
    }
}
