package com.jianzixing.webapp.tables.recommend;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableRecommendContent {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = int.class, nullable = false, comment = "分组ID")
    groupId,
    @Column(length = 50, nullable = false)
    admin,
    @Column(type = byte.class, nullable = false, comment = "0自定义URL 10标准内容 11新闻 12产品 13解决方案 14文档")
    type,
    @Column(length = 30, comment = "子类型,用于区分内容的不同类型的,表必须有type字段")
    subType,
    @Column(length = 100, comment = "产品封面")
    cover,
    @Column(length = 150, nullable = false, comment = "推荐标题,独立于内容的")
    title,
    @Column(length = 500, comment = "推荐的URL")
    url,
    @Column(type = long.class, comment = "推荐表的主键 必须是数字主键")
    value,
    @Column(type = int.class, defaultValue = "0", comment = "是否置顶,正数是置顶并排序")
    top,
    @Column(type = Date.class)
    createTime
}
