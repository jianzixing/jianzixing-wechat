package com.jianzixing.webapp.tables.log;


import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableRequestAddress {
    @Column(pk = true, type = long.class, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 50, nullable = false, comment = "IP地址")
    ip,
    @Column(length = 64, comment = "唯一标识")
    sessionid,
    @Column(length = 500, comment = "访问页面")
    uri,
    @Column(length = 500, comment = "访问GET参数")
    query,
    @Column(type = Date.class)
    createTime
}
