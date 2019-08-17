package com.jianzixing.webapp.tables.comment;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

@Table
public enum TableDiscussClassify {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 30, nullable = false)
    name,
    @Column(length = 30, nullable = false)
    code,
    @Column(type = byte.class, defaultValue = "0", comment = "是否自动审核通过评论")
    isAutoShow,
    @Column(type = byte.class, defaultValue = "0", comment = "是否是系统参数 0否 1是")
    isSystem
}
