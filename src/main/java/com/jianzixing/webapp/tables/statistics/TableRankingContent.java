package com.jianzixing.webapp.tables.statistics;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

@Table
public enum TableRankingContent {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = int.class, nullable = false, comment = "规则ID")
    ruleId,
    @Column(type = long.class, nullable = false, comment = "关联类型的ID比如新闻")
    value,
    @Column(type = long.class, defaultValue = "0", comment = "评分根据不同的算法值不一样")
    score,
    @Column(length = 14, comment = "周期时间时间格式 yyyyMMddHHmmss")
    cycleTime,
    @Column(type = byte.class, defaultValue = "0", comment = "是否已经过期 0否 1是")
    isPassed,
    @Column(type = byte.class, defaultValue = "0", comment = "是否固定,固定后会跳过不在更新分数 0否 1是")
    isFixed
}
