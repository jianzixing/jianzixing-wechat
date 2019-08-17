package com.jianzixing.webapp.tables.aftersales;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;

/**
 * 如果当前售后单有第三方支付的退款记录
 * 则记录退款单信息
 */
@Table
public enum TableAfterSalesRefundOrder {
    @Column(type = long.class, pk = true, comment = "售后单ID")
    afterSalesId,
    @Column(type = long.class, pk = true, comment = "退款单ID")
    refundOrderId
}
