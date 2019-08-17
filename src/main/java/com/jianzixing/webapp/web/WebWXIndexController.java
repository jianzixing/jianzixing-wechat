package com.jianzixing.webapp.web;

import com.jianzixing.webapp.handler.WebLoginHolder;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.collect.CollectType;
import com.jianzixing.webapp.service.history.HistoryType;
import com.jianzixing.webapp.service.hotsearch.HotSearchType;
import com.jianzixing.webapp.tables.balance.TableBalance;
import com.jianzixing.webapp.tables.goods.TableGoods;
import com.jianzixing.webapp.tables.integral.TableIntegral;
import com.jianzixing.webapp.tables.page.TablePage;
import com.jianzixing.webapp.tables.page.TablePageContent;
import com.jianzixing.webapp.tables.user.TableUser;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.CookieUtils;
import org.mimosaframework.core.utils.calculator.CalcNumber;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class WebWXIndexController {

    @RequestMapping("/wx/index")
    public ModelAndView index(ModelAndView view, HttpServletRequest request) {
        ModelObject search = new ModelObject();
        search.put(TablePage.type, 1);
        ModelObject indexPage = GlobalService.pageService.getFrontPage(search);
        loadData(indexPage, view);

        List<ModelObject> objects = GlobalService.hotSearchService.getHotSearchByType(HotSearchType.WE_CHAT, 8);
        view.addObject("hotSearch", objects);

        //判断登录
        view.addObject("isLogin", StringUtils.isNotBlank(CookieUtils.getCookieValue(request, "user")));
        view.setViewName("wx/index");
        return view;
    }

    @RequestMapping("/wx/promotion/{pageId}")
    public ModelAndView promotion(ModelAndView view, @PathVariable("pageId") String pageId) {
        ModelObject search = new ModelObject();
        search.put(TablePage.type, 2);
        search.put(TablePage.id, pageId);
        ModelObject indexPage = GlobalService.pageService.getFrontPage(search);
        loadData(indexPage, view);
        view.setViewName("wx/promotion");
        return view;
    }

    private void loadData(ModelObject indexPage, ModelAndView view) {
        view.addObject("page", indexPage == null ? 0 : 1);
        view.addObject("pageInfo", indexPage);
        if (indexPage != null) {
            List<ModelObject> pageContents = GlobalService.pageService.getPageAndPageContentById(indexPage.getInteger(TablePage.id));
            view.addObject("page", pageContents == null ? 0 : 1);
            if (pageContents != null && pageContents.size() > 0) {
                //排序
                for (ModelObject item : pageContents) {
                    int type = item.getIntValue(TablePageContent.type);
                    if (type == 6) { //商品推荐
                        ModelObject data = ModelObject.parseObject(item.getString(TablePageContent.data));
                        if (data != null && data.containsKey("data")) { //提取id，查询，然后排序
                            List<ModelObject> dataArray = data.getArray("data");
                            data.remove("data");
                            if (dataArray != null && dataArray.size() > 0) {
                                List<Long> goodsIds = new ArrayList<>();
                                for (ModelObject dataItem : dataArray) {
                                    goodsIds.add(dataItem.getLong("goodsId"));
                                }
                                List<ModelObject> goodsDataList = GlobalService.goodsService.getSimpleGoods(goodsIds);
                                if (goodsDataList != null && goodsDataList.size() > 0) {
                                    for (ModelObject goodsData : goodsDataList) {
                                        for (ModelObject dataItem : dataArray) {
                                            if (dataItem.getLong("goodsId") == goodsData.getIntValue(TableGoods.id)) {
                                                goodsData.put("pos", dataItem.getIntValue("pos"));
                                                break;
                                            }
                                        }
                                    }

                                    Collections.sort(goodsDataList, Comparator.comparingInt(o -> o.getIntValue("pos")));
                                    data.put("data", goodsDataList);
                                    item.put("data", data);
                                }
                            }
                        }
                    } else if (type == 1 || type == 2 || type == 3) {
                        item.put("data", ModelObject.parseArray(item.getString("data")));
                    } else if (type == 4 || type == 5 || type == 7) {
                        item.put("data", ModelObject.parseObject(item.getString("data")));
                    }
                }

                view.addObject("pageContents", pageContents);
            }
        }
    }

    @RequestMapping("/wx/mine/index")
    public ModelAndView mineIndex(HttpServletRequest request, ModelAndView view) {
        if (WebLoginHolder.isLogin(view, request)) {
            view.setViewName("wx/mine/index");
            ModelObject user = WebLoginHolder.getUser();
            view.addObject("userLevelAmount", user.getLongValue(TableUser.levelAmount));
            ModelObject integral = GlobalService.integralService.getIntegralByUid(WebLoginHolder.getUid());
            view.addObject("userIntegralAmount", integral == null ? 0 : integral.getLongValue(TableIntegral.amount));
            view.addObject("user", user);
            long couponCount = GlobalService.couponService.getUserValidCouponCount(WebLoginHolder.getUid());
            view.addObject("couponCount", couponCount);
            ModelObject balance = GlobalService.balanceService.getBalanceByUid(WebLoginHolder.getUid());
            view.addObject("balanceAmount", balance == null ? 0 : CalcNumber.as(balance.getBigDecimal(TableBalance.balance)));
            long spcardCount = GlobalService.shoppingCardService.getValidSPCountByUid(WebLoginHolder.getUid());
            view.addObject("spcardCount", spcardCount);

            long userCollectCount = GlobalService.collectService.getUserCollectCount(WebLoginHolder.getUid(), CollectType.GOODS);
            view.addObject("userCollectCount", userCollectCount);

            long addressCount = GlobalService.userAddressService.getUserAddressCount(WebLoginHolder.getUid());
            view.addObject("addressCount", addressCount);

            long historyCount = GlobalService.historyService.getUserHistoryCount(WebLoginHolder.getUid(), HistoryType.GOODS);
            view.addObject("historyCount", historyCount);

            ModelObject level = GlobalService.userLevelService.getUserLevel(user);
            view.addObject("level", level);

            List<ModelObject> goods = GlobalService.goodsService.getRecommendGoods(2);
            view.addObject("goods", goods);
        }
        return view;
    }
}
