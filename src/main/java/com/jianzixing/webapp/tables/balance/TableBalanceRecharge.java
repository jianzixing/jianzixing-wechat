package com.jianzixing.webapp.tables.balance;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableBalanceRecharge {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = long.class, nullable = false, comment = "用户ID")
    userId,
    @Column(length = 32, nullable = false, unique = true, comment = "充值单号")
    number,
    @Column(type = double.class, nullable = false, comment = "充值金额")
    money,
    @Column(type = long.class, comment = "充值渠道")
    channelId,
    @Column(length = 100, comment = "充值渠道名称")
    channelName,
    @Column(type = byte.class, defaultValue = "0", comment = "充值状态 0未成功 1充值成功")
    status,
    @Column(type = Date.class, nullable = false, comment = "充值时间")
    createTime
}
