package com.jianzixing.webapp.tables.aftersales;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

@Table
public enum TableAfterSalesImages {
    @Column(pk = true, type = long.class, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = long.class, nullable = false, comment = "售后ID")
    asid,
    @Column(length = 100, comment = "售后图片")
    fileName
}
