package com.jianzixing.webapp.web;

import com.jianzixing.webapp.service.GlobalService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RobotsContentController {

    @RequestMapping("/robots.txt")
    @ResponseBody
    public String robots() {
        return GlobalService.systemRobotsService.getRobots();
    }
}
