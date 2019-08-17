package com.jianzixing.webapp.tables.statistics;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;

@Table
public enum TableStatisticsDay {
    @Column(pk = true, length = 8)
    dayTime,
    @Column(length = 32, pk = true, comment = "作为主键,区分统计类型")
    md5,
    @Column(length = 500, comment = "访问的URL地址")
    uri,
    @Column(length = 100, comment = "统计类型")
    type,
    @Column(type = int.class, defaultValue = "0", comment = "PV")
    pv,
    @Column(type = int.class, defaultValue = "0", comment = "UV")
    uv,
    @Column(type = int.class, defaultValue = "0", comment = "IV")
    iv
}
