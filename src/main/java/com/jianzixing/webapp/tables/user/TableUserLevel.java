package com.jianzixing.webapp.tables.user;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

@Table
public enum TableUserLevel {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 30, nullable = false, comment = "等级名称")
    name,
    @Column(type = long.class, defaultValue = "0", comment = "等级开始数值")
    startAmount,
    @Column(type = long.class, defaultValue = "0", comment = "等级结束数值")
    endAmount,
    @Column(length = 500, nullable = false, comment = "用户等级小图标")
    logo,
    @Column(length = 300, comment = "等级描述")
    detail
}
