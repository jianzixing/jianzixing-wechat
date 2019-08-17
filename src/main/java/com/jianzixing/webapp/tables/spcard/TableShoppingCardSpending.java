package com.jianzixing.webapp.tables.spcard;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

/**
 * 购物卡消费日志
 */
@Table
public enum TableShoppingCardSpending {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = int.class, nullable = false, comment = "所属批次")
    scid,
    @Column(type = byte.class, nullable = false, comment = "购物卡消费类型 0下单消费  1退还消费")
    type,
    @Column(length = 19, nullable = false, index = true, comment = "卡号")
    cardNumber,
    @Column(length = 32, nullable = false, comment = "订单号")
    orderNumber,
    @Column(type = long.class, nullable = false, comment = "消费用户")
    uid,
    @Column(type = double.class, nullable = false, comment = "消费金额")
    money,
    @Column(length = 300, nullable = false, comment = "消费日志描述")
    detail,
    @Column(type = Date.class, nullable = false, comment = "消费时间")
    createTime
}
