package com.jianzixing.webapp.tables.file;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

/**
 * @author yangankang
 */
@Table
public enum TableFileGroup {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = int.class, defaultValue = "0")
    pid,
    @Column(length = 100, nullable = false)
    groupName,
    @Column(type = byte.class, defaultValue = "1")
    expanded,
    @Column(type = byte.class, defaultValue = "0")
    leaf,
    @Column(type = byte.class, defaultValue = "0", comment = "文件分组类型 0 虚拟目录  1 磁盘目录")
    groupSourceType
}
