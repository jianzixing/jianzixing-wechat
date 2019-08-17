package com.jianzixing.webapp.tables.promotions;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

/**
 * 幸运转盘规则表
 */
@Table
public enum TablePlwRule {
    @Column(pk = true, type = long.class, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 30, nullable = false, comment = "幸运转盘规则名称")
    name,
    @Column(type = byte.class, defaultValue = "0", comment = "0永久次数 1每天次数")
    type,
    @Column(type = int.class, defaultValue = "1", comment = "每个用户不同类型下的抽奖次数")
    count,
    @Column(type = byte.class, defaultValue = "0", comment = "是否删除 0否 1是")
    isDel,
    @Column(type = byte.class, defaultValue = "1", comment = "是否启用 0否 1是")
    enable,
    @Column(type = Date.class, nullable = false, comment = "活动开始时间")
    startTime,
    @Column(type = Date.class, nullable = false, comment = "活动结束时间")
    finishTime,
    @Column(length = 200, comment = "描述")
    detail
}
