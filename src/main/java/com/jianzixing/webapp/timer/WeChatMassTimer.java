package com.jianzixing.webapp.timer;

import com.jianzixing.webapp.service.GlobalService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WeChatMassTimer {
    private static final Log logger = LogFactory.getLog(WeChatMassTimer.class);

    @Scheduled(cron = "0/1 * *  * * ? ")   //每1秒执行一次
    public void run() {
        try {
            GlobalService.weChatMassService.triggerTimerMass();
        } catch (Exception e) {
            logger.error("执行微信定时群发消息出错", e);
        }
    }
}
