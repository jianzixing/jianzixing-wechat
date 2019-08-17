package com.jianzixing.webapp.tables.cooperation;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableFriendLink {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 20, comment = "显示的名称", nullable = false)
    name,
    @Column(length = 200, comment = "链接地址", nullable = false)
    link,
    @Column(type = Integer.class, comment = "排序", defaultValue = "99")
    order,
    @Column(type = Date.class)
    createTime,
    @Column(type = Date.class, timeForUpdate = true)
    modifiedTime,
    @Column(type = Integer.class, comment = "最后的操作用户")
    adminId
}
