package com.jianzixing.webapp.tables.trigger;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.platform.Text;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

@Table
public enum TableTriggerValue {
    @Column(type = long.class, pk = true, nullable = false, comment = "处理器ID")
    triggerId,
    @Column(type = int.class, pk = true, comment = "参数下标索引")
    index,
    @Column(length = 200, comment = "参数名称")
    name,
    @Column(length = 100, nullable = false, comment = "参数Code码")
    code,
    @Column(length = 100, nullable = false, comment = "值")
    value
}
