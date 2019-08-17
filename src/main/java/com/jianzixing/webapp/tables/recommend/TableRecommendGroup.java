package com.jianzixing.webapp.tables.recommend;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableRecommendGroup {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = int.class, defaultValue = "0")
    pid,
    @Column(length = 50, nullable = false)
    admin,
    @Column(type = byte.class, nullable = false, comment = "0链接地址 10标准内容 11新闻 12产品 13解决方案  14文档 200混合推荐")
    type,
    @Column(length = 50, nullable = false, comment = "分组名称")
    name,
    @Column(length = 30, nullable = false, comment = "获取推荐的推荐码")
    code,
    @Column(type = byte.class, defaultValue = "1")
    expanded,
    @Column(type = byte.class, defaultValue = "0")
    leaf,
    @Column(type = Date.class)
    createTime
}
