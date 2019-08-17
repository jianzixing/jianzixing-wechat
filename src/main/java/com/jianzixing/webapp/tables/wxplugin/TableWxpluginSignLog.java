package com.jianzixing.webapp.tables.wxplugin;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableWxpluginSignLog {
    @Column(pk = true, type = long.class, strategy = AutoIncrementStrategy.class, comment = "主键")
    id,
    @Column(type = int.class, nullable = false, comment = "所属组")
    gid,
    @Column(type = long.class, nullable = false, comment = "签到用户")
    userId,
    @Column(type = int.class, defaultValue = "0", comment = "签到总次数")
    count,
    @Column(type = int.class, defaultValue = "0", comment = "连续签到次数")
    cntCount,
    @Column(type = Date.class, comment = "签到时间")
    createTime
}
