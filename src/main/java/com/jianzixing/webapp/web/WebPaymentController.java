package com.jianzixing.webapp.web;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.payment.PaymentModeInterface;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class WebPaymentController {
    private static final Log logger = LogFactory.getLog(WebPaymentController.class);

    @RequestMapping("/payment/{name}")
    @ResponseBody
    public String paymentCallback(HttpServletRequest request,
                                  HttpServletResponse response,
                                  @PathVariable("name") String name,
                                  @RequestBody(required = false) String body) {
        PaymentModeInterface modeInterface = GlobalService.paymentService.getCanCallbackPaymentMode(name);
        if (modeInterface != null) {
            try {
                String resp = modeInterface.paymentCallback(request, response, body);
                logger.info("支付回调成功,返回值: " + resp);
                return resp;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "failure";
    }

    @RequestMapping("/payment/refund/{name}")
    @ResponseBody
    public String paymentRefundCallback(HttpServletRequest request,
                                        HttpServletResponse response,
                                        @PathVariable("name") String name,
                                        @RequestBody(required = false) String body) {
        PaymentModeInterface modeInterface = GlobalService.paymentService.getCanCallbackPaymentMode(name);
        if (modeInterface != null) {
            try {
                String resp = modeInterface.refundCallback(request, response, body);
                logger.info("退款回调成功,返回值: " + resp);
                return resp;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "failure";
    }
}
