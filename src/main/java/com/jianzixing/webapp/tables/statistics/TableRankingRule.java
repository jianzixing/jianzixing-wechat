package com.jianzixing.webapp.tables.statistics;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableRankingRule {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 30, nullable = false, comment = "排行分类名称")
    name,
    @Column(length = 30, nullable = false, comment = "获取排行的排行码")
    code,
    @Column(type = byte.class, nullable = false, comment = "10标准内容 11新闻 12产品 13解决方案  14文档")
    type,
    @Column(length = 150, nullable = false, comment = "算法规则类实现")
    algorithm,
    @Column(type = int.class, defaultValue = "10", comment = "统计N条记录,剩下的记录不在统计 负数表示统计所有")
    count,
    @Column(type = Date.class, nullable = false, comment = "开始计算时间")
    startTime,
    @Column(type = int.class, nullable = false, comment = "计算周期,比如24小时 或者 10分钟")
    cycle,
    @Column(type = byte.class, nullable = false, comment = "周期单位,比如 3天 4小时 5分钟")
    unit,
    @Column(type = Date.class, comment = "上次统计时间")
    lastRunTime,
    @Column(type = byte.class, defaultValue = "1", comment = "是否有效 0无效 1有效")
    enable,
    @Column(type = Date.class)
    createTime
}
