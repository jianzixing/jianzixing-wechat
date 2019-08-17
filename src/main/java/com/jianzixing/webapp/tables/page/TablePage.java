package com.jianzixing.webapp.tables.page;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

/**
 * @author qinmingtao
 */
@Table
public enum TablePage {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = int.class, nullable = false,comment = "页面类型 1首页 2活动页", defaultValue = "1")
    type,
    @Column(length = 50, comment = "后台标题", nullable = false)
    name,
    @Column(length = 50, comment = "前台显示标题", nullable = false)
    title,
    @Column(length = 200, comment = "seo关键字")
    keyword,
    @Column(length = 500, comment = "seo描述")
    description,
    @Column(length = 20, comment = "背景色")
    background,
    @Column(type = int.class, length = 1, comment = "是否启用 1启用 2不启用")
    enable,
    @Column(type = int.class, comment = "创建用户")
    createUser,
    @Column(type = Date.class, comment = "创建时间")
    createTime,
    @Column(type = Date.class, timeForUpdate = true, comment = "修改时间")
    modifyTime
}
