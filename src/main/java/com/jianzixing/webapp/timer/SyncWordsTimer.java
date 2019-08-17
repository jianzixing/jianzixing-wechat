package com.jianzixing.webapp.timer;

import com.jianzixing.webapp.service.GlobalService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class SyncWordsTimer {
    // 每30秒执行一次
    @Scheduled(cron = "0/30 * * * * ?")
    public void run() {
        GlobalService.sensitiveWordsService.syncWords();
    }
}
