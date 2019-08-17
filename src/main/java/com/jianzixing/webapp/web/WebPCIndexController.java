package com.jianzixing.webapp.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WebPCIndexController {

    @RequestMapping("/index")
    public ModelAndView index(ModelAndView view) {
        view.setViewName("index");
        return view;
    }
}
