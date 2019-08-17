package com.jianzixing.webapp.tables.payment;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TablePaymentTransaction {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class, comment = "主键")
    id,
    @Column(length = 32, nullable = false, unique = true, comment = "支付的支付交易单单号")
    paymentNumber,
    @Column(type = long.class, nullable = false, comment = "用户ID")
    userId,
    @Column(type = byte.class, nullable = false, comment = "支付类型查看PaymentTransactionType，比如支付订单，余额充值")
    type,
    @Column(type = long.class, nullable = false, comment = "订单ID，比如订单ID，充值单ID")
    rid,
    @Column(length = 32, nullable = false, comment = "订单号，比如订单号，充值单号")
    number,
    @Column(type = long.class, comment = "支付方式ID")
    payChannelId,
    @Column(length = 100, nullable = false, comment = "支付方式名称")
    payChannelName,
    @Column(type = double.class, nullable = false, comment = "当前支付方式支付的价格")
    payPrice,
    @Column(type = double.class, nullable = false, comment = "当前支付方式支付的价格或者数量(比如积分)")
    payAmount,
    @Column(type = byte.class, nullable = false, defaultValue = "0", comment = "支付状态 0未支付 1完全支付 2部分支付")
    payStatus,
    @Column(type = byte.class, nullable = false, defaultValue = "0", comment = "支付状态 0未退款 1完全退款 2部分退款")
    refundStatus,
    @Column(type = double.class, nullable = false, defaultValue = "0", comment = "当前支付方式退款的金额")
    refundPrice,
    @Column(type = double.class, nullable = false, defaultValue = "0", comment = "当前支付方式退款的价格或者数量(比如积分)")
    refundAmount,
    @Column(length = 64, comment = "外部(第三方系统)支付单号")
    outPaymentNumber,
    @Column(type = Date.class, nullable = false, comment = "创建时间")
    createTime
}
