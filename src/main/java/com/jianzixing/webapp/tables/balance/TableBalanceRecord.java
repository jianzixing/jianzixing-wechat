package com.jianzixing.webapp.tables.balance;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

/**
 * 用户余额变化记录
 */
@Table
public enum TableBalanceRecord {
    @Column(pk = true, type = long.class, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = long.class, nullable = false, comment = "用户ID")
    userId,
    @Column(type = double.class, defaultValue = "0", comment = "余额变动数量，正数是增加负数是减少")
    changeBalance,
    @Column(type = double.class, defaultValue = "0", comment = "余额变动数量之前数量")
    beforeBalance,
    @Column(type = double.class, defaultValue = "0", comment = "余额变动数量之后数量")
    afterBalance,
    @Column(length = 200, comment = "变动描述")
    detail,
    @Column(type = Date.class, comment = "变动时间")
    createTime
}
