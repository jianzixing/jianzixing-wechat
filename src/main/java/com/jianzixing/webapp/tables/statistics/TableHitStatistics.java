package com.jianzixing.webapp.tables.statistics;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableHitStatistics {
    @Column(pk = true, type = long.class, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 30, comment = "外部需要统计的类型")
    outType,
    @Column(type = long.class, comment = "外部需要统计的表主键,比如新闻 12")
    outId,
    @Column(type = Date.class)
    createTime
}
