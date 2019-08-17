package com.jianzixing.webapp.web;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.handler.WebLoginHolder;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.collect.CollectType;
import com.jianzixing.webapp.service.history.HistoryType;
import com.jianzixing.webapp.tables.collect.TableCollect;
import com.jianzixing.webapp.tables.goods.TableGoodsComment;
import com.jianzixing.webapp.tables.order.TableOrder;
import com.jianzixing.webapp.tables.order.TableOrderGoods;
import com.jianzixing.webapp.tables.user.TableUser;
import com.jianzixing.webapp.template.JZXFile;
import com.jianzixing.webapp.template.JZXHideString;
import freemarker.template.TemplateModelException;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.exception.TransactionException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
public class WebWXMineGoodsController {
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @RequestMapping("/wx/mine/goods_comment")
    public ModelAndView mineGoodsComment(HttpServletRequest request,
                                         ModelAndView view,
                                         @RequestParam(defaultValue = "0") long og) {
        if (WebLoginHolder.isLogin(view, request)) {
            view.setViewName("wx/mine/goods_comment");
            ModelObject orderGoods = GlobalService.orderService.getOrderGoodsById(og);
            if (orderGoods != null) {
                ModelObject order = GlobalService.orderService.getSimpleOrderById(orderGoods.getLongValue(TableOrderGoods.orderId));
                if (order != null && order.getLongValue(TableOrder.userId) == WebLoginHolder.getUid()) {
                    view.addObject("orderGoods", orderGoods);
                }
            }
        }
        return view;
    }

    @RequestMapping("/wx/mine/goods_mark")
    public ModelAndView mineGoodsMark(HttpServletRequest request,
                                      ModelAndView view) {
        if (WebLoginHolder.isLogin(view, request)) {
            view.setViewName("wx/mine/goods_mark");
            long count = GlobalService.collectService.getUserCollectCount(WebLoginHolder.getUid(), CollectType.GOODS);
            view.addObject("collectCount", count);
        }
        return view;
    }

    @RequestMapping("/wx/mine/goods_mark_list")
    @ResponseBody
    public String getGoodsMarkList(@RequestParam(defaultValue = "0") int page) {
        ModelObject json = new ModelObject();
        json.put("success", 1);
        if (WebLoginHolder.isLogin()) {
            List<ModelObject> goods =
                    GlobalService.collectService.getCollectGoods(WebLoginHolder.getUid(), CollectType.GOODS, page);
            json.put("data", goods);
        } else {
            json.put("success", 0);
            json.put("code", "not_login");
        }
        return json.toJSONString();
    }

    @RequestMapping("/wx/mine/goods_mark_cancel")
    @ResponseBody
    public String cancelGoodsMarkList(String gid) {
        ModelObject json = new ModelObject();
        json.put("success", 1);
        if (WebLoginHolder.isLogin()) {
            if (StringUtils.isNotBlank(gid)) {
                String[] gids = gid.split(",");
                for (String g : gids) {
                    GlobalService.collectService.delCollect(WebLoginHolder.getUid(), Long.parseLong(g), CollectType.GOODS);
                }
            }
        } else {
            json.put("success", 0);
            json.put("code", "not_login");
        }
        return json.toJSONString();
    }

    @RequestMapping("/wx/mine/goods_history")
    public ModelAndView mineGoodsHistory(HttpServletRequest request,
                                         ModelAndView view) {
        if (WebLoginHolder.isLogin(view, request)) {
            view.setViewName("wx/mine/goods_history");
            long count = GlobalService.historyService.getUserHistoryCount(WebLoginHolder.getUid(), HistoryType.GOODS);
            view.addObject("collectCount", count);
        }
        return view;
    }

    @RequestMapping("/wx/mine/history_mark_list")
    @ResponseBody
    public String getHistoryMarkList(@RequestParam(defaultValue = "0") int page) {
        ModelObject json = new ModelObject();
        json.put("success", 1);
        if (WebLoginHolder.isLogin()) {
            List<ModelObject> goods =
                    GlobalService.historyService.getHistoryGoods(WebLoginHolder.getUid(), HistoryType.GOODS, page);
            json.put("data", goods);
        } else {
            json.put("success", 0);
            json.put("code", "not_login");
        }
        return json.toJSONString();
    }

    @RequestMapping("/wx/mine/history_mark_cancel")
    @ResponseBody
    public String cancelHistoryMarkList(String gid) {
        ModelObject json = new ModelObject();
        json.put("success", 1);
        if (WebLoginHolder.isLogin()) {
            if (StringUtils.isNotBlank(gid)) {
                String[] gids = gid.split(",");
                for (String g : gids) {
                    GlobalService.historyService.delHistory(WebLoginHolder.getUid(), Long.parseLong(g), HistoryType.GOODS);
                }
            }
        } else {
            json.put("success", 0);
            json.put("code", "not_login");
        }
        return json.toJSONString();
    }

    @RequestMapping("/wx/mine/history_mark_clear")
    @ResponseBody
    public String clearHistoryMarkList() {
        ModelObject json = new ModelObject();
        json.put("success", 1);
        if (WebLoginHolder.isLogin()) {
            GlobalService.historyService.clearHistory(WebLoginHolder.getUid(), HistoryType.GOODS);
        } else {
            json.put("success", 0);
            json.put("code", "not_login");
        }
        return json.toJSONString();
    }

    @RequestMapping("/wx/mine/goods_comment_submit")
    @ResponseBody
    public String submitGoodsComment(HttpServletRequest request,
                                     @RequestParam(defaultValue = "0") int score,
                                     @RequestParam(defaultValue = "0") int logisticsScore,
                                     @RequestParam(defaultValue = "0") int speedScore,
                                     @RequestParam(defaultValue = "0") int serviceScore,
                                     String comment,
                                     @RequestParam(defaultValue = "0") int anonymity,
                                     @RequestParam(defaultValue = "0") long og) {
        ModelObject json = new ModelObject();
        json.put("success", 1);
        if (WebLoginHolder.isLogin()) {
            List<String> files = GlobalService.fileService.uploadFiles(request);
            ModelObject commentObj = new ModelObject();
            commentObj.put(TableGoodsComment.userId, WebLoginHolder.getUid());
            commentObj.put(TableGoodsComment.orderGoodsId, og);
            commentObj.put(TableGoodsComment.score, score);
            commentObj.put(TableGoodsComment.logisticsScore, logisticsScore);
            commentObj.put(TableGoodsComment.speedScore, speedScore);
            commentObj.put(TableGoodsComment.serviceScore, serviceScore);
            commentObj.put(TableGoodsComment.comment, comment);
            commentObj.put(TableGoodsComment.anonymity, anonymity);
            commentObj.put("images", files);
            try {
                GlobalService.goodsCommentService.addComment(commentObj);
            } catch (ModelCheckerException e) {
                e.printStackTrace();
            } catch (ModuleException e) {
                e.printStackTrace();
            } catch (TransactionException e) {
                e.printStackTrace();
            }
        } else {
            json.put("success", 0);
            json.put("code", "not_login");
        }
        return json.toJSONString();
    }

    @RequestMapping("/wx/mine/goods_comment_list")
    @ResponseBody
    public String getGoodsCommentList(HttpServletRequest request,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "0") long gid,
                                      @RequestParam(defaultValue = "0") long skuId,
                                      @RequestParam(defaultValue = "0") int type) {
        ModelObject json = new ModelObject();
        json.put("success", 1);
        if (WebLoginHolder.isLogin()) {
            List<ModelObject> comments = GlobalService.goodsCommentService.getComments(page, type, gid, skuId);
            if (comments != null) {
                for (ModelObject comment : comments) {
                    ModelObject user = comment.getModelObject(TableUser.class);
                    if (user != null) {
                        String nick = user.getString(TableUser.nick);
                        String avatar = user.getString(TableUser.avatar);
                        if (StringUtils.isBlank(nick)) {
                            nick = JZXHideString.getHideString(user.getString(TableUser.userName));
                        }
                        comment.put("userName", nick);
                        if (StringUtils.isNotBlank(avatar)) {
                            avatar = JZXFile.getFileUrl(request, avatar);
                            comment.put("avatar", avatar);
                        }
                    }
                    Date createTime = comment.getDate(TableGoodsComment.createTime);
                    if (createTime != null) {
                        comment.put(TableGoodsComment.createTime, format.format(createTime));
                    }
                }
            }
            json.put("data", comments);
            json.put("url", JZXFile.getFileDownloadUrl(request));
        } else {
            json.put("success", 0);
            json.put("code", "not_login");
        }
        return json.toJSONString();
    }
}
