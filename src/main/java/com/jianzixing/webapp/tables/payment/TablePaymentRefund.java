package com.jianzixing.webapp.tables.payment;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

/**
 * 退款记录，只和支付相关的退款记录
 */
@Table
public enum TablePaymentRefund {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class, comment = "主键")
    id,
    @Column(type = byte.class, nullable = false, comment = "退款类型 比如退货退款")
    type,
    @Column(type = long.class, nullable = false, comment = "退款单ID，TablePaymentRefund必须存在")
    refundOrderId,
    @Column(type = long.class, defaultValue = "0", comment = "售后单ID，如果是售后单退款则有")
    afterSalesId,
    @Column(type = long.class, comment = "支付方式ID")
    payChannelId,
    @Column(length = 100, nullable = false, comment = "支付方式名称")
    payChannelName,
    @Column(type = double.class, defaultValue = "0", comment = "当前支付方式退款的金额")
    refundPrice,
    @Column(type = double.class, defaultValue = "0", comment = "当前支付方式退款的价格或者数量(比如积分)")
    refundAmount,
    @Column(length = 64, comment = "外部(第三方系统)退款单号")
    outPaymentNumber,
    @Column(type = Date.class, nullable = false, comment = "创建时间")
    createTime
}
