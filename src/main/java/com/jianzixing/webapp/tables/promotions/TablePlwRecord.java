package com.jianzixing.webapp.tables.promotions;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TablePlwRecord {
    @Column(pk = true, type = long.class, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = long.class, nullable = false, comment = "抽奖用户ID")
    uid,
    @Column(type = long.class, nullable = false, comment = "所属规则ID")
    rid,
    @Column(type = long.class, nullable = false, comment = "当前抽奖获得奖品ID")
    aid,
    @Column(type = byte.class, defaultValue = "0", comment = "奖品状态 0未兑奖 1已兑奖")
    status,
    @Column(length = 8, comment = "中奖当天日期yyyyMMdd")
    dayTime,
    @Column(type = Date.class, comment = "创建时间")
    createTime
}
