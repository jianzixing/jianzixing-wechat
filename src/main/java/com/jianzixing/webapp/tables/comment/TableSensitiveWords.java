package com.jianzixing.webapp.tables.comment;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

@Table
public enum TableSensitiveWords {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 100, nullable = false, index = true, comment = "铭感字")
    text,
    @Column(type = byte.class, defaultValue = "1", comment = "是否启用 1启用 0禁用")
    isEnable
}
