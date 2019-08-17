package com.jianzixing.webapp.tables.user;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableUserLevelRecord {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = long.class, nullable = false, comment = "用户ID")
    userId,
    @Column(type = long.class, defaultValue = "0", comment = "积分变动数量，正数是增加负数是减少")
    changeAmount,
    @Column(type = long.class, defaultValue = "0", comment = "积分变动数量之前数量")
    beforeAmount,
    @Column(type = long.class, defaultValue = "0", comment = "积分变动数量之后数量")
    afterAmount,
    @Column(length = 200, comment = "变动描述")
    detail,
    @Column(type = Date.class, comment = "变动时间")
    createTime
}
