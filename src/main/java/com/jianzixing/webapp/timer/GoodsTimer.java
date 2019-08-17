package com.jianzixing.webapp.timer;

import com.jianzixing.webapp.service.GlobalService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GoodsTimer {
    @Scheduled(cron = "0 0/1 * * * ?")
    public void run() {
        try {
            GlobalService.goodsService.checkGoodsValidTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
