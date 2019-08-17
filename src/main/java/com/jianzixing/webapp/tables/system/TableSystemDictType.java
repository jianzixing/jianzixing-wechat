package com.jianzixing.webapp.tables.system;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

@Table
public enum TableSystemDictType {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 50, nullable = false)
    name,
    @Column(length = 50, nullable = false)
    table,
    @Column(length = 50, nullable = false)
    field,
    @Column(type = byte.class, defaultValue = "1", comment = "字典类别 1文字  2状态")
    type,
    @Column(type = byte.class, defaultValue = "0", comment = "0不是  1是(不允许删除和修改DictType但是可以修改Dict)")
    isSystem
}
