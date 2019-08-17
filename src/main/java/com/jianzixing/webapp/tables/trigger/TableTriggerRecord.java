package com.jianzixing.webapp.tables.trigger;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableTriggerRecord {
    @Column(pk = true, type = long.class, strategy = AutoIncrementStrategy.class, comment = "主键")
    id,
    @Column(type = long.class, nullable = false, comment = "触发器ID")
    triggerId,
    @Column(length = 50, nullable = false, index = true, comment = "触发器事件")
    event,
    @Column(type = long.class, defaultValue = "0", nullable = false, comment = "用户ID")
    userId,
    @Column(length = 100, nullable = false, comment = "处理器,比如赠送积分")
    triggerName,
    @Column(length = 4, nullable = false, comment = "年")
    year,
    @Column(length = 6, nullable = false, comment = "月")
    month,
    @Column(length = 8, nullable = false, comment = "日")
    day,
    @Column(length = 10, nullable = false, comment = "时")
    hour,
    @Column(length = 12, nullable = false, comment = "分")
    minute,
    @Column(length = 14, nullable = false, comment = "秒")
    second,
    @Column(type = Date.class, nullable = false, comment = "触发时间")
    createTime
}
