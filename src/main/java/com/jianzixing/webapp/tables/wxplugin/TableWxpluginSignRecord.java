package com.jianzixing.webapp.tables.wxplugin;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

/**
 * 微信签到记录表
 */
@Table
public enum TableWxpluginSignRecord {
    @Column(pk = true, type = long.class, nullable = false, comment = "签到用户")
    userId,
    @Column(pk = true, type = int.class, nullable = false, comment = "所属组")
    gid,
    @Column(type = int.class, defaultValue = "0", comment = "签到总次数")
    count,
    @Column(type = int.class, defaultValue = "0", comment = "连续签到次数")
    cntCount,
    @Column(length = 8, comment = "上一次签到时间 yyyyMMdd")
    lastTime,
    @Column(type = Date.class, comment = "创建时间")
    createTime
}
