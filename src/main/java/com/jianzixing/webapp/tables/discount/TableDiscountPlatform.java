package com.jianzixing.webapp.tables.discount;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;

@Table
public enum TableDiscountPlatform {
    @Column(pk = true, type = long.class, comment = "活动ID")
    did,
    @Column(pk = true, type = byte.class, comment = "平台类型 0全平台 1pc 2app 3wap 4微信 5支付宝")
    ptype
}
