package com.jianzixing.webapp.tables.spcard;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

/**
 * 用户购物卡表
 */
@Table
public enum TableShoppingCard {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 100, nullable = false, comment = "购物卡名称")
    name,
    @Column(length = 32, nullable = false, unique = true, comment = "购物卡批次号")
    number,
    @Column(type = double.class, nullable = false, comment = "该批次购物卡面额")
    money,
    @Column(type = int.class, defaultValue = "1", comment = "该批次购物卡生成总数")
    count,
    @Column(length = 32, nullable = false, comment = "批次密码")
    password,
    @Column(type = Date.class, nullable = false, comment = "该批次购物卡有效时间")
    finishTime,
    @Column(type = byte.class, defaultValue = "0", comment = "批次状态 0未创建 1已创建 2作废")
    status,
    @Column(length = 300, comment = "购物卡描述")
    detail,
    @Column(type = Date.class, nullable = false, comment = "创建时间")
    createTime
}
