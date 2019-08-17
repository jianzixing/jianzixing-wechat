package com.jianzixing.webapp.tables.system;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

@Table
public enum TableSystemDict {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = int.class, nullable = false)
    dictType,
    @Column(length = 50, nullable = false)
    table,
    @Column(length = 50, nullable = false)
    field,
    @Column(length = 20, nullable = false, comment = "字段对应的值")
    value,
    @Column(length = 50, nullable = false, comment = "字典名称")
    name
}
