package com.jianzixing.webapp.tables.website;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.platform.Text;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableWebsiteLetter {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = long.class, nullable = false, comment = "发送人")
    adminId,
    @Column(type = long.class, nullable = false, comment = "收信人")
    userId,
    @Column(type = byte.class, nullable = false, comment = "站内信类型")
    type,
    @Column(type = Text.class, nullable = false, comment = "站内信内容")
    content,
    @Column(type = byte.class, defaultValue = "0", comment = "是否已读 0未读 1已读")
    isRead,
    @Column(type = Date.class, comment = "已读时间")
    readTime,
    @Column(type = byte.class, defaultValue = "0", comment = "是否删除 0未删除 1已删除")
    isDel,
    @Column(type = Date.class, comment = "发送时间")
    createTime
}
