package com.jianzixing.webapp.tables.coupon;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;

@Table
public enum TableCouponUserLevel {
    @Column(type = long.class, pk = true, comment = "优惠券ID")
    cid,
    @Column(pk = true, type = int.class, comment = "用户等级ID")
    ulid
}
