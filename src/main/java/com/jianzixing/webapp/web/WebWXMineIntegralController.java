package com.jianzixing.webapp.web;

import com.jianzixing.webapp.handler.WebLoginHolder;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.integral.TableIntegralRecord;
import org.mimosaframework.core.json.ModelObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
public class WebWXMineIntegralController {
    @RequestMapping("/wx/mine/integral")
    public ModelAndView mineIntegral(HttpServletRequest request,
                                     ModelAndView view) {
        if (WebLoginHolder.isLogin(view, request)) {
            view.setViewName("wx/mine/integral");

            ModelObject integral = GlobalService.integralService.getIntegralByUid(WebLoginHolder.getUid());
            view.addObject("integral", integral);
        }
        return view;
    }

    @RequestMapping("/wx/mine/integral_list")
    @ResponseBody
    public String getIntegralList(@RequestParam(defaultValue = "1") int page) {
        ModelObject json = new ModelObject();
        json.put("success", 1);
        if (WebLoginHolder.isLogin()) {
            List<ModelObject> objects = GlobalService.integralService.getUserIntegralRecord(WebLoginHolder.getUid(), page);
            if (objects != null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (ModelObject object : objects) {
                    if (object.isNotEmpty(TableIntegralRecord.createTime))
                        object.put(TableIntegralRecord.createTime, format.format(object.getDate(TableIntegralRecord.createTime)));
                }
            }
            json.put("data", objects);
        } else {
            json.put("success", 0);
            json.put("code", "not_login");
        }
        return json.toJSONString();
    }
}
