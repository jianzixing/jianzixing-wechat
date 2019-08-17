package com.jianzixing.webapp.web;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.handler.WebLoginHolder;
import com.jianzixing.webapp.service.GlobalService;
import org.mimosaframework.core.json.ModelObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class WebWXMineCardController {

    @RequestMapping("/wx/mine/spcard")
    public ModelAndView mineSpcard(HttpServletRequest request,
                                   ModelAndView view) {
        if (WebLoginHolder.isLogin(view, request)) {
            long uid = WebLoginHolder.getUid();
            view.setViewName("wx/mine/spcard");
            List<ModelObject> spcards = GlobalService.shoppingCardService.getValidShoppingCardsByUid(uid);
            List<ModelObject> invalidSpcards = GlobalService.shoppingCardService.getInvalidUserShoppingCards(uid, 1);
            long validSpcardCount = GlobalService.shoppingCardService.getValidShoppingCardsCount(uid);
            long invalidSpcardCount = GlobalService.shoppingCardService.getInvalidUserShoppingCardsCount(uid);
            view.addObject("validSpcards", spcards);
            view.addObject("invalidSpcards", invalidSpcards);
            view.addObject("validSpcardCount", validSpcardCount);
            view.addObject("invalidSpcardCount", invalidSpcardCount);
            view.addObject("cardDetail", GlobalService.systemConfigService.getValue("shopping_card_detail"));
        }
        return view;
    }

    @RequestMapping("/wx/mine/bind_spcard")
    public ModelAndView mineBindSpcard(ModelAndView view, HttpServletRequest request) {
        if (WebLoginHolder.isLogin(view, request)) {
            view.setViewName("wx/mine/bind_spcard");
        }
        return view;
    }

    @RequestMapping("/wx/mine/bind_spacard_submit")
    @ResponseBody
    public String mineBindSpacardSubmit(String pwd) {
        if (WebLoginHolder.isLogin()) {
            try {
                GlobalService.shoppingCardService.bindSpcard(WebLoginHolder.getUid(), pwd);
            } catch (ModuleException e) {
                e.printStackTrace();
                return e.getCode().toString();
            }
            return "ok";
        }
        return "not_login";
    }
}
