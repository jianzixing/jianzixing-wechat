package com.jianzixing.webapp.tables.system;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

@Table
public enum TableSystemConfigGroup {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 30, nullable = false, comment = "分组名称")
    name,
    @Column(type = byte.class, defaultValue = "0", comment = "0不是系统参数  1是系统参数 2是隐藏参数")
    isSystem
}
