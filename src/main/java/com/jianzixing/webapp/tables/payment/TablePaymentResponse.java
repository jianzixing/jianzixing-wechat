package com.jianzixing.webapp.tables.payment;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.platform.Text;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TablePaymentResponse {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 32, nullable = false, comment = "订单号,如：2017111013123141")
    orderNumber,
    @Column(length = 64, nullable = false, comment = "交易单号")
    paymentNumber,
    @Column(length = 64, nullable = false, comment = "外部系统支付单号")
    outPaymentNumber,
    @Column(type = Text.class, nullable = false, comment = "接口回调原始返回值")
    body,
    @Column(type = Date.class, nullable = false, comment = "创建时间")
    createTime
}
