package com.jianzixing.webapp.tables.system;


import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

@Table
public enum TableAdminDepartment {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(nullable = false, length = 100)
    name,
    @Column
    detail
}
