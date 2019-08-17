package com.jianzixing.webapp.tables.coupon;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

/**
 * 用户已领取优惠券
 */
@Table
public enum TableCouponUser {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = long.class, comment = "优惠券ID")
    cid,
    @Column(type = long.class, comment = "用户ID")
    uid,
    @Column(type = byte.class, defaultValue = "0", comment = "优惠券状态 0正常 1已使用 2已过期 3已作废")
    status,
    @Column(type = Date.class, comment = "优惠券使用时间")
    useTime,
    @Column(length = 32, comment = "优惠券使用订单")
    orderNumber,
    @Column(type = Date.class, nullable = false, comment = "领取时间")
    createTime
}
