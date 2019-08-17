package com.jianzixing.webapp.tables.refund;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

/**
 * 退款单表，所有退款都需要创建一个退款单
 * 包括主动退款转账，订单取消，订单售后等等
 */
@Table
public enum TableRefundOrder {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 32, nullable = false, unique = true, comment = "退款单号")
    number,
    @Column(type = long.class, defaultValue = "0", comment = "如果和用户有关")
    userId,
    @Column(type = long.class, defaultValue = "0", comment = "如果和订单有关系")
    orderId,
    @Column(length = 20, nullable = false, comment = "退款来源, 查看RefundFrom")
    from,
    @Column(type = long.class, defaultValue = "0", comment = "外部来源ID，比如售后单ID")
    fromId,
    @Column(type = byte.class, defaultValue = "0", comment = "审核状态 0未审核 1审核拒绝 2审核通过 查看RefundAuditStatus")
    auditStatus,
    @Column(type = byte.class, defaultValue = "10", comment = "退款单状态，查看RefundStatus")
    status,
    @Column(type = byte.class, nullable = false, comment = "退款类型，查看RefundType")
    type,
    @Column(type = long.class, defaultValue = "0", comment = "原路退还时必须，支付单ID，表TablePaymentTransaction")
    transId,
    @Column(type = double.class, defaultValue = "0", comment = "原金额")
    sourceMoney,
    @Column(type = double.class, defaultValue = "0", comment = "扣款金额")
    chargeMoney,
    @Column(type = double.class, defaultValue = "0", comment = "退款金额")
    money,
    @Column(length = 64, comment = "原路退还时必须，第三方外部退款单号,如果存在的话")
    outRefundNumber,
    @Column(length = 1000, comment = "退款备注")
    remark,
    @Column(length = 1000, nullable = false, comment = "退款原因")
    detail,
    @Column(length = 1000, comment = "退款原因")
    log,
    @Column(type = Date.class, comment = "退款单执行退款日期")
    refundTime,
    @Column(type = Date.class, nullable = false, comment = "退款单创建日期")
    createTime
}
