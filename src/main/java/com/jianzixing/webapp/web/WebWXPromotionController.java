package com.jianzixing.webapp.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/promotion")
public class WebWXPromotionController {

    @RequestMapping("/luckywheel/index")
    public String luckyWheel() {
        return "promotion/luckywheel/index";
    }
}
