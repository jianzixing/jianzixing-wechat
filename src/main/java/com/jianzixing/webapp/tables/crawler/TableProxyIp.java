package com.jianzixing.webapp.tables.crawler;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

/**
 * @author yangankang
 */
@Table
public enum TableProxyIp {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 32)
    ip,
    @Column(type = int.class)
    port,
    @Column(length = 8)
    protocol,
    @Column(type = long.class)
    surviveTime,
    @Column(type = long.class)
    endTime
}
