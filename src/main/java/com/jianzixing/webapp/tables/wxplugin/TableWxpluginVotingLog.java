package com.jianzixing.webapp.tables.wxplugin;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableWxpluginVotingLog {
    @Column(pk = true, type = long.class, strategy = AutoIncrementStrategy.class, comment = "主键")
    id,
    @Column(type = int.class, nullable = false, comment = "所属组")
    gid,
    @Column(type = long.class, nullable = false, comment = "签到用户")
    userId,
    @Column(type = int.class, nullable = false, comment = "投给的项目")
    iid,
    @Column(type = Date.class, comment = "签到时间")
    createTime
}
