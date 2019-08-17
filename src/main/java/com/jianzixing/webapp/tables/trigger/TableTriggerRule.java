package com.jianzixing.webapp.tables.trigger;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

@Table
public enum TableTriggerRule {
    @Column(type = long.class, pk = true, nullable = false, comment = "触发器ID")
    triggerId,
    @Column(type = int.class, pk = true, comment = "参数下标索引")
    index,
    @Column(length = 200, comment = "参数名称")
    name,
    @Column(length = 100, nullable = false, comment = "参数字段名称,一般是定义的表名称")
    code,
    @Column(length = 10, nullable = false, comment = "判断符号 大于小于等等")
    symbol,
    @Column(length = 100, nullable = false, comment = "判断的值")
    value
}
