package com.jianzixing.webapp.timer;

import com.jianzixing.webapp.service.GlobalService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GoodsGroupTimer {
    @Scheduled(cron = "0 0 * * * ?")
    public void run() {
        try {
            GlobalService.goodsService.checkGoodsGroupsCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
