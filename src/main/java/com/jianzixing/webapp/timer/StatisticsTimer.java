package com.jianzixing.webapp.timer;

import com.jianzixing.webapp.service.GlobalService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StatisticsTimer {
    // 每5分钟执行一次
    @Scheduled(cron = "0 0/5 * * * ?")
    public void run() {
        GlobalService.statisticsService.statistics();
    }
}
