package com.jianzixing.webapp.controller;

import com.jianzixing.webapp.valcode.multipoint.ImageMultiPointFactory;
import com.jianzixing.webapp.valcode.multipoint.MultiPointCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author yangankang
 */
@Controller
public class ValCodeController {

    @RequestMapping("/valcode/image")
    public void getCodeImage(HttpServletResponse response) throws IOException {
        response.setContentType("image/png");
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            MultiPointCode.Result result = ImageMultiPointFactory.buildDefault();
            ImageIO.write(result.getImage(), "png", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
}
