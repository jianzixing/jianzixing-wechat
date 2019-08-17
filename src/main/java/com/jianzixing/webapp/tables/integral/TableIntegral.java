package com.jianzixing.webapp.tables.integral;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;

@Table
public enum TableIntegral {
    @Column(type = long.class, pk = true, nullable = false, comment = "用户ID")
    userId,
    @Column(type = long.class, defaultValue = "0", comment = "用户积分数量")
    amount
}
