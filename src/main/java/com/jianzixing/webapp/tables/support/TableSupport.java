package com.jianzixing.webapp.tables.support;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableSupport {
    @Column(pk = true, strategy = AutoIncrementStrategy.class, comment = "主键")
    id,
    @Column(length = 100, nullable = false, comment = "服务名称")
    name,
    @Column(type = byte.class, nullable = false, comment = "服务类型，SupportServiceType")
    type,
    @Column(type = byte.class, defaultValue = "0", comment = "是否选购服务 0固定服务  1选购服务")
    fixed,
    @Column(type = int.class, defaultValue = "0", comment = "服务有效时间,购买之日起，单位小时")
    serTime,
    @Column(type = double.class, defaultValue = "0", comment = "暂时不支持，选购服务时价格，下单时额外金额")
    price,
    @Column(length = 500, comment = "服务描述")
    detail,
    @Column(type = Date.class, nullable = false, comment = "创建时间")
    createTime
}
