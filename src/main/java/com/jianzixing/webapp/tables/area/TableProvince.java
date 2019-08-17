package com.jianzixing.webapp.tables.area;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

@Table
public enum TableProvince {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    code,
    @Column(type = byte.class, defaultValue = "0", comment = "比如 华北 华南")
    type,
    @Column(length = 100, nullable = false, defaultValue = "CHN")
    country,
    @Column(length = 100, nullable = false)
    name
}
