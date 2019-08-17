package com.jianzixing.webapp.tables.hotsearch;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

@Table
public enum TableHotSearch {
    @Column(pk = true, strategy = AutoIncrementStrategy.class, comment = "主键")
    id,
    @Column(length = 10, nullable = false, comment = "区分站点类型 wx微信站点")
    type,
    @Column(length = 100, nullable = false, comment = "关键字")
    name,
    @Column(type = int.class, defaultValue = "0", comment = "排序")
    pos,
    @Column(type = byte.class, defaultValue = "1", comment = "是否有效")
    enable
}
