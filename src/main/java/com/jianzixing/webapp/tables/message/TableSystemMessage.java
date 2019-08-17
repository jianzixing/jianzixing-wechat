package com.jianzixing.webapp.tables.message;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.platform.Text;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableSystemMessage {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = byte.class, defaultValue = "0", comment = "消息类型")
    type,
    @Column(type = int.class, nullable = false, comment = "管理员ID")
    fromAdminId,
    @Column(type = int.class, nullable = false, comment = "管理员ID")
    toAdminId,
    @Column(length = 100, nullable = false, comment = "系统消息标题")
    title,
    @Column(type = Text.class, nullable = false, comment = "系统消息内容")
    content,
    @Column(type = byte.class, defaultValue = "1", comment = "是否已读 0未读 1已读")
    isRead,
    @Column(type = byte.class, defaultValue = "0", comment = "是否已经提醒用户 0未提醒 1已提醒")
    isShow,
    @Column(type = Date.class)
    createTime
}
