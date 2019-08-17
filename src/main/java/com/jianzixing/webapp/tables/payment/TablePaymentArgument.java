package com.jianzixing.webapp.tables.payment;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

@Table
public enum TablePaymentArgument {
    @Column(pk = true, type = long.class, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = long.class, nullable = false, comment = "所属支付渠道")
    channelId,
    @Column(length = 50, nullable = false, comment = "支付参数名称")
    key,
    @Column(length = 500, nullable = false, comment = "支付参数值")
    value
}
