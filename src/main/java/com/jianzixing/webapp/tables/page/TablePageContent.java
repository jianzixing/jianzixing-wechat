package com.jianzixing.webapp.tables.page;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.platform.Text;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

/**
 * @author qinmingtao
 */
@Table
public enum TablePageContent {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = long.class, comment = "页面id", nullable = false)
    pageId,
    @Column(length = 50, comment = "后台标题", nullable = false)
    name,
    @Column(type = int.class, nullable = false,comment = "组件类型 1轮播 2快报 3活动入口 4楼层(上2下4) 5楼层(上4下4) 6商品推荐 7单行图文")
    type,
    @Column(type = Text.class, comment = "组件数据")
    data,
    @Column(type = int.class, comment = "排序 越小越靠前", defaultValue = "99")
    pos,
    @Column(type = Date.class, timeForUpdate = true, comment = "修改时间")
    modifyTime
}
