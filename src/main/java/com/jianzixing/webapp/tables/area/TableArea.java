package com.jianzixing.webapp.tables.area;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

@Table
public enum TableArea {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    code,
    @Column(length = 100, nullable = false)
    name,
    @Column(type = int.class, nullable = false)
    cityCode,
    @Column(type = int.class, nullable = false)
    provinceCode
}
