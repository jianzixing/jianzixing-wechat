package com.jianzixing.webapp.tables.discount;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;

@Table
public enum TableDiscountUserLevel {
    @Column(type = long.class, pk = true, comment = "活动ID")
    did,
    @Column(pk = true, type = int.class, comment = "用户等级ID")
    ulid
}
