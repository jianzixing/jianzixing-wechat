package com.jianzixing.webapp.web;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.SystemConfig;
import com.jianzixing.webapp.service.wechat.WeChatCookieUtils;
import com.jianzixing.webapp.service.wechat.model.AccountConfig;
import com.jianzixing.webapp.service.wechatsm.WeChatServiceManagerUtils;
import com.jianzixing.webapp.tables.wechat.TableWeChatImageText;
import com.jianzixing.webapp.tables.wxplugin.TableWxpluginSignGroup;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Controller
public class WebWCPluginController extends AbstractWeChatController {

    @RequestMapping("/wxplugin/sign/{groupCode}/index")
    public ModelAndView pluginSignIndex(HttpServletRequest request,
                                        ModelAndView view,
                                        @PathVariable("groupCode") String groupCode) {
        view.addObject("groupCode", groupCode);
        ModelObject group = GlobalService.pluginSignService.getGroupByCode(groupCode);
        if (group == null) {
            return this.toErrorPage(view, "访问签到页面出错", "您访问的签到活动不存在");
        }

        try {
            long uid;
            ModelObject user;
            if (SystemConfig.isDebug) {
                uid = WeChatCookieUtils.getUidByDebug(request);
                user = GlobalService.userService.getUser(uid);
                view.setViewName("wxplugin/sign/index");
            } else {
                AccountConfig config = WeChatServiceManagerUtils.createAccountConfig(
                        group.getIntValue(TableWxpluginSignGroup.openType),
                        group.getIntValue(TableWxpluginSignGroup.accountId)
                );

                AccessTokenValid accessTokenValid = this.isAccessTokenValid(request, config);
                if (accessTokenValid.isValid()) {
                    view.setViewName("wxplugin/sign/index");
                } else {
                    view.setViewName("redirect:" + this.getPublicUserAuthUrl(request, config, null));
                }
                uid = accessTokenValid.getUid();
                user = accessTokenValid.getUser();
            }
            ModelObject sign = GlobalService.pluginSignService.getFrontSignModel(group, uid);
            view.addObject("sign", sign);
            view.addObject("user", user);
            view.addObject("timeout", GlobalService.pluginSignService.isGroupTimeout(group));
        } catch (Exception e) {
            e.printStackTrace();
            return this.toErrorPage(view, "访问签到页面出错", e);
        }
        return view;
    }

    @RequestMapping("/wxplugin/sign/{groupCode}/award")
    public ModelAndView pluginSignAward(HttpServletRequest request,
                                        ModelAndView view,
                                        @PathVariable("groupCode") String groupCode) {
        view.addObject("groupCode", groupCode);
        ModelObject group = GlobalService.pluginSignService.getGroupByCode(groupCode);
        try {
            int gid = group.getIntValue(TableWxpluginSignGroup.id);
            long uid;
            if (SystemConfig.isDebug) {
                uid = WeChatCookieUtils.getUidByDebug(request);
                view.setViewName("wxplugin/sign/award");
            } else {
                AccountConfig config = WeChatServiceManagerUtils.createAccountConfig(
                        group.getIntValue(TableWxpluginSignGroup.openType),
                        group.getIntValue(TableWxpluginSignGroup.accountId)
                );
                AccessTokenValid accessTokenValid = this.isAccessTokenValid(request, config);
                uid = accessTokenValid.getUid();
                if (accessTokenValid.isValid()) {
                    view.setViewName("wxplugin/sign/award");
                } else {
                    view.setViewName("redirect:" + this.getPublicUserAuthUrl(request, config, null));
                }
            }

            Map<String, List<ModelObject>> awards = GlobalService.pluginSignService.getUserStatusGets(gid, uid);
            view.addObject("nu", awards.get("nu"));
            view.addObject("ud", awards.get("ud"));
        } catch (Exception e) {
            e.printStackTrace();
            return this.toErrorPage(view, "访问签到页面出错", e);
        }
        return view;
    }

    @RequestMapping("/wxplugin/sign/{groupCode}/click")
    @ResponseBody
    public String pluginSignClick(HttpServletRequest request,
                                  @PathVariable("groupCode") String groupCode) {
        try {
            ModelObject group = GlobalService.pluginSignService.getGroupByCode(groupCode);
            long uid;
            if (SystemConfig.isDebug) {
                uid = WeChatCookieUtils.getUidByDebug(request);
            } else {
                AccountConfig config = WeChatServiceManagerUtils.createAccountConfig(
                        group.getIntValue(TableWxpluginSignGroup.openType),
                        group.getIntValue(TableWxpluginSignGroup.accountId)
                );
                AccessTokenValid accessTokenValid = this.isAccessTokenValid(request, config);
                if (!accessTokenValid.isValid()) {
                    return "auth_error";
                }
                uid = accessTokenValid.getUid();
            }

            int timeout = GlobalService.pluginSignService.isGroupTimeout(group);
            if (timeout == 0) {
                GlobalService.pluginSignService.sign(groupCode, uid);
            } else {
                return "timeout";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "failure";
        }
        return "ok";
    }

    @RequestMapping("/wxplugin/voting/{groupCode}/index")
    public ModelAndView pluginVotingIndex(HttpServletRequest request,
                                          ModelAndView view,
                                          @PathVariable("groupCode") String groupCode) {
        view.addObject("groupCode", groupCode);
        ModelObject group = GlobalService.pluginVotingService.getGroupByCode(groupCode);
        if (group == null) {
            return this.toErrorPage(view, "访问投票页面出错", "您访问的投票活动不存在");
        }

        try {
            long uid;
            ModelObject user;
            if (SystemConfig.isDebug) {
                uid = WeChatCookieUtils.getUidByDebug(request);
                user = GlobalService.userService.getUser(uid);
                view.setViewName("wxplugin/voting/index");
            } else {
                AccountConfig config = WeChatServiceManagerUtils.createAccountConfig(
                        group.getIntValue(TableWxpluginSignGroup.openType),
                        group.getIntValue(TableWxpluginSignGroup.accountId)
                );

                AccessTokenValid accessTokenValid = this.isAccessTokenValid(request, config);
                if (accessTokenValid.isValid()) {
                    view.setViewName("wxplugin/voting/index");
                } else {
                    view.setViewName("redirect:" + this.getPublicUserAuthUrl(request, config, null));
                }
                uid = accessTokenValid.getUid();
                user = accessTokenValid.getUser();
            }
            ModelObject sign = GlobalService.pluginVotingService.getFrontVotingModel(group, uid);
            view.addObject("voting", sign);
            view.addObject("user", user);
            view.addObject("timeout", GlobalService.pluginVotingService.isGroupTimeout(group));
        } catch (Exception e) {
            e.printStackTrace();
            return this.toErrorPage(view, "访问投票页面出错", e);
        }
        return view;
    }

    @RequestMapping("/wxplugin/voting/{groupCode}/item")
    public ModelAndView pluginVotingItem(HttpServletRequest request,
                                         ModelAndView view,
                                         @PathVariable("groupCode") String groupCode,
                                         String id) {
        view.addObject("groupCode", groupCode);
        ModelObject item = GlobalService.pluginVotingService.getItemById(Integer.parseInt(id));
        view.addObject("item", item);
        view.setViewName("/wxplugin/voting/item");
        return view;
    }

    @RequestMapping("/wxplugin/voting/{groupCode}/click")
    @ResponseBody
    public String pluginVotingClick(HttpServletRequest request,
                                    @PathVariable("groupCode") String groupCode,
                                    String iid) {
        try {
            ModelObject group = GlobalService.pluginVotingService.getGroupByCode(groupCode);
            long uid;
            if (SystemConfig.isDebug) {
                uid = WeChatCookieUtils.getUidByDebug(request);
            } else {
                AccountConfig config = WeChatServiceManagerUtils.createAccountConfig(
                        group.getIntValue(TableWxpluginSignGroup.openType),
                        group.getIntValue(TableWxpluginSignGroup.accountId)
                );
                AccessTokenValid accessTokenValid = this.isAccessTokenValid(request, config);
                if (!accessTokenValid.isValid()) {
                    return "auth_error";
                }
                uid = accessTokenValid.getUid();
            }

            int timeout = GlobalService.pluginVotingService.isGroupTimeout(group);
            if (timeout == 0) {
                int r = GlobalService.pluginVotingService.voting(Integer.parseInt(iid), uid);
                if (r == -100) {
                    return "countout";
                }
                if (r == -200) {
                    return "already";
                }
            } else {
                return "timeout";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "failure";
        }
        return "ok";
    }

    @RequestMapping("/wxplugin/imagetext")
    public ModelAndView goImageText(ModelAndView view, String id, String i) {
        view.addObject("obj", null);
        if (StringUtils.isNotBlank(id)) {
            ModelObject object = GlobalService.weChatMaterialService.getImageTextSub(id, i);
            if (object != null) {
                ModelObject it = object.getModelObject(TableWeChatImageText.class);
                object.put("time", (new SimpleDateFormat("yyyy-MM-dd").format(it.getDate(TableWeChatImageText.createTime))));
                view.addObject("obj", object);
            }
        }
        view.setViewName("/wxplugin/imagetext/index");
        return view;
    }
}
