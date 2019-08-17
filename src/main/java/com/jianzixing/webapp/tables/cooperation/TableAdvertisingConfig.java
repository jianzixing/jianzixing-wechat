package com.jianzixing.webapp.tables.cooperation;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.sql.Timestamp;
import java.util.Date;

@Table
public enum TableAdvertisingConfig {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 30, nullable = false, comment = "广告名称")
    name,
    @Column(length = 30, nullable = false, comment = "广告码用来获取广告数据,唯一")
    code,
    @Column(length = 100, comment = "广告文字")
    text,
    @Column(length = 100, comment = "广告封面图片")
    cover,
    @Column(length = 500, comment = "广告URL链接地址")
    url,
    @Column(length = 500, comment = "广告脚本文件")
    script,
    @Column(type = byte.class, defaultValue = "1", comment = "是否有效 1有效 0无效")
    enable,
    @Column(type = Date.class)
    createTime,
    @Column(type = Timestamp.class, timeForUpdate = true)
    modifiedTime
}
