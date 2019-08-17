package com.jianzixing.webapp.timer;

import com.jianzixing.webapp.service.GlobalService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DiscountTimer {
    private static final Log logger = LogFactory.getLog(DiscountTimer.class);

    @Scheduled(cron = "0/5 * *  * * ? ")   //每5秒执行一次
    public void run() {
        try {
            GlobalService.discountService.checkDiscountValid();
        } catch (Exception e) {
            logger.error("执行优惠活动上下线出错", e);
        }
    }
}
