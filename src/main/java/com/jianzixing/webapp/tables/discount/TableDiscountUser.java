package com.jianzixing.webapp.tables.discount;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableDiscountUser {
    @Column(pk = true, type = long.class, strategy = AutoIncrementStrategy.class, comment = "主键")
    id,
    @Column(type = long.class, nullable = false, comment = "使用优惠的用户")
    userId,
    @Column(type = long.class, nullable = false, comment = "使用的优惠ID")
    did,
    @Column(length = 32, nullable = false, comment = "订单号")
    orderNumber,
    @Column(type = Date.class, comment = "创建时间")
    createTime
}
