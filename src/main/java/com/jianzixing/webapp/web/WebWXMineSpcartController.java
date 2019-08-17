package com.jianzixing.webapp.web;

import com.jianzixing.webapp.handler.WebLoginHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class WebWXMineSpcartController {

    @RequestMapping("/wx/mine/spcart")
    public ModelAndView mineSpcart(HttpServletRequest request,
                                   ModelAndView view) {
        if (WebLoginHolder.isLogin(view, request)) {
            view.setViewName("wx/mine/spcart");
        }
        return view;
    }

    @RequestMapping("/wx/mine/bind_spcart")
    public ModelAndView mineBindSpcart(HttpServletRequest request,
                                       ModelAndView view) {
        if (WebLoginHolder.isLogin(view, request)) {
            view.setViewName("wx/mine/bind_spcart");
        }
        return view;
    }
}
