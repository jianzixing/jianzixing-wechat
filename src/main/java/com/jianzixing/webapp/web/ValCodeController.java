package com.jianzixing.webapp.web;

import com.jianzixing.webapp.valcode.multipoint.ImageMultiPointFactory;
import com.jianzixing.webapp.valcode.multipoint.MultiPointCode;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.core.json.ModelArray;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author yangankang
 */
@APIController
public class ValCodeController {
    public static final String SUCCESS_VAL = "isValCodeSuccess";
    private static final String POINTS_KEY = "points";

    @RequestMapping("/valcode/image")
    public void getCodeImage(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("image/png");
        try {
            MultiPointCode.Result result = ImageMultiPointFactory.buildDefault();
            request.getSession().setAttribute(POINTS_KEY, result.getPoints());
            request.getSession().setAttribute(SUCCESS_VAL, false);
            ImageIO.write(result.getImage(), "png", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Printer
    public ResponseMessage check(HttpServletRequest request, String point) {
        boolean isSuccess = false;
        try {
            List<MultiPointCode.Point> points = (List<MultiPointCode.Point>) request.getSession().getAttribute(POINTS_KEY);
            isSuccess = ImageMultiPointFactory.checkDefault(points, ModelArray.parseArray(point).toJavaObjects(MultiPointCode.Point.class));
            request.getSession().removeAttribute(POINTS_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isSuccess) {
            request.getSession().setAttribute(SUCCESS_VAL, true);
            return new ResponseMessage(0, "校验验证码成功");
        } else {
            request.getSession().setAttribute(SUCCESS_VAL, false);
            return new ResponseMessage(-1, "校验验证码失败");
        }
    }
}
