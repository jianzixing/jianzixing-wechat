package com.jianzixing.webapp.web;

import com.alibaba.fastjson.JSON;
import com.jianzixing.webapp.handler.WebLoginHolder;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.goods.TableGoodsBrand;
import com.jianzixing.webapp.tables.goods.TableGoodsGroup;
import com.jianzixing.webapp.tables.goods.TableGoodsParameter;
import com.jianzixing.webapp.tables.goods.TableGoodsValue;
import org.mimosaframework.core.utils.StringTools;
import org.mimosaframework.core.json.ModelObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class WebWXGoodsSearchController {

    @RequestMapping("/wx/catalog")
    public ModelAndView catalog(ModelAndView view, ModelMap modelMap) {
        List<ModelObject> goodsGroupList = GlobalService.goodsGroupService.getGroups();
        if (goodsGroupList != null && goodsGroupList.size() > 0) {
            modelMap.put("catalogList", goodsGroupList);
        }
        view.setViewName("wx/catalog");
        return view;
    }

    @RequestMapping("/wx/item_list")
    public ModelAndView ItemList(ModelAndView view,
                                 @RequestParam(required = false, defaultValue = "0") int gId,
                                 @RequestParam(required = false) String keyword,
                                 @RequestParam(required = false, defaultValue = "1") String sort,
                                 @RequestParam(required = false) String parameter,
                                 @RequestParam(required = false) String brandId,
                                 @RequestParam(required = false) String minPrice,
                                 @RequestParam(required = false) String maxPrice) {
        ModelObject search = new ModelObject();
        if (gId > 0) {
            List<Integer> cateIds = new ArrayList();
            cateIds.add(gId);
            List<ModelObject> goodsGroups = GlobalService.goodsGroupService.getGroups(cateIds);
            if (goodsGroups != null && goodsGroups.size() > 0) {
                search.put("gId", gId);
                ModelObject goodsGroup = goodsGroups.get(0);
                //用于显示标题上的名称
                view.addObject("keyword", goodsGroup.getString(TableGoodsGroup.name));
                if (goodsGroup.isNotEmpty(TableGoodsGroup.attrGroupId)) {
                    int attrGroupId = goodsGroup.getIntValue(TableGoodsGroup.attrGroupId);
                    view.addObject("parameter", GlobalService.goodsService.getParameterByGroupId(attrGroupId));
                }
            }
        }

        if (StringTools.isNotEmpty(keyword)) {
            view.addObject("keyword", keyword);
            view.addObject("name", keyword);
            search.put("name", keyword);
        }

        if (StringTools.isNotEmpty(brandId)) {
            search.put("brandId", brandId);
        }

        if (StringTools.isNotEmpty(parameter)) {
            search.put("parameter", parameter);
            if (view.getModel().get("parameter") != null) { //选中
                List<ModelObject> parameterList = (List<ModelObject>) view.getModel().get("parameter");
                String[] ps = parameter.split("[_]");
                if (parameterList.size() > 0) {
                    parameterList.forEach(pItem -> {
                        for (String p : ps) {
                            String[] pArray = p.split("-");
                            if (pArray.length == 2) {
                                if (pItem.getIntValue(TableGoodsParameter.id) == StringTools.toInt(pArray[0])) {
                                    String[] pValueArray = pArray[1].split(",");
                                    if (pValueArray.length > 0) {
                                        for (String pValue : pValueArray) {
                                            List<ModelObject> goodsValueList = pItem.getArray("TableGoodsValue");
                                            for (ModelObject goodsValue : goodsValueList) {
                                                if (goodsValue.getIntValue(TableGoodsValue.id) == StringTools.toInt(pValue)) {
                                                    goodsValue.put("selected", 1);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }
        }

        if (StringTools.isNotEmpty(minPrice)) {
            search.put("minPrice", minPrice);
            view.addObject("minPrice", minPrice);
        }

        if (StringTools.isNotEmpty(maxPrice)) {
            search.put("maxPrice", maxPrice);
            view.addObject("maxPrice", maxPrice);
        }

        search.put("sort", sort);
        view.addObject("sort", sort);

        if (WebLoginHolder.isLogin()) {
            search.put("user", GlobalService.userService.getUser(WebLoginHolder.getUid()));
        }
        ModelObject searchResult = GlobalService.goodsService.searchGoods(search, 0, 10);
        view.addObject("data", searchResult.get("paging"));
        if (searchResult.isNotEmpty("brandList")) {
            List<ModelObject> brandList = (List<ModelObject>) searchResult.get("brandList");
            if (brandList != null && brandList.size() > 0 && StringTools.isNotEmpty(brandId)) {
                String[] brandIdArray = brandId.split(",");
                brandList.forEach(brand -> {
                    for (String brandIdItem : brandIdArray) {
                        if (brand.getIntValue(TableGoodsBrand.id) == StringTools.toInt(brandIdItem)) {
                            brand.put("selected", 1);
                            break;
                        }
                    }
                });
            }
            view.addObject("brandList", brandList);
        }
        view.setViewName("wx/item_list");
        return view;
    }

    @RequestMapping("/wx/item_list_data")
    @ResponseBody
    public String ItemListData(
            @RequestParam(required = false, defaultValue = "0") int gId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") String sort,
            @RequestParam(required = false) String parameter,
            @RequestParam(required = false) String brandId,
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String maxPrice,
            @RequestParam(required = false, defaultValue = "1") int page) {
        ModelObject search = new ModelObject();
        if (gId > 0) {
            List<Integer> cateIds = new ArrayList();
            cateIds.add(gId);
            List<ModelObject> goodsGroups = GlobalService.goodsGroupService.getGroups(cateIds);
            if (goodsGroups != null && goodsGroups.size() > 0) {
                search.put("gId", gId);
            }
        }

        if (StringTools.isNotEmpty(keyword)) {
            search.put("name", keyword);
        }

        if (StringTools.isNotEmpty(brandId)) {
            search.put("brandId", brandId);
        }

        if (StringTools.isNotEmpty(parameter)) {
            search.put("parameter", parameter);
        }

        if (StringTools.isNotEmpty(minPrice)) {
            search.put("minPrice", minPrice);
        }

        if (StringTools.isNotEmpty(maxPrice)) {
            search.put("maxPrice", maxPrice);
        }

        search.put("sort", sort);

        if (WebLoginHolder.isLogin()) {
            search.put("user", GlobalService.userService.getUser(WebLoginHolder.getUid()));
        }
        int start = (page - 1) * 10;
        ModelObject searchResult = GlobalService.goodsService.searchGoods(search, start, 10);
        return JSON.toJSONString(searchResult.get("paging"));
    }
}
