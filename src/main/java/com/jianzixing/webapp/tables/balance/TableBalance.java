package com.jianzixing.webapp.tables.balance;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;

@Table
public enum TableBalance {
    @Column(type = long.class, pk = true, nullable = false, comment = "用户ID")
    userId,
    @Column(type = double.class, defaultValue = "0", comment = "用户余额数")
    balance
}
