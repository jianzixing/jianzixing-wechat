package com.jianzixing.webapp.tables.notice;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.platform.Text;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableNotice {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 150, nullable = false, comment = "标题")
    title,
    @Column(type = Integer.class, length = 1, defaultValue = "0", comment = "是否置顶")
    up,
    @Column(type = Text.class, nullable = false, comment = "公告内容")
    content,
    @Column(type = Date.class, timeForUpdate = true)
    editTime,
    @Column(type = Integer.class, nullable = false, comment = "操作人")
    adminId,
}
