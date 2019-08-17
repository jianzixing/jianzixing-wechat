package com.jianzixing.webapp.tables.system;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

@Table
public enum TableRoles {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(nullable = false, length = 20)
    roleName,
    @Column(type = byte.class, defaultValue = "0")
    isSystemRole,
    @Column(type = int.class, defaultValue = "0")
    pos,
    @Column(length = 200)
    detail
}
