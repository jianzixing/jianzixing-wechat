package com.jianzixing.webapp.tables.comment;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.sql.Timestamp;
import java.util.Date;

@Table
public enum TableDiscussComment {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = int.class, nullable = false, comment = "评论分类ID")
    classifyId,
    @Column(type = int.class, nullable = false, comment = "用户ID")
    userId,
    @Column(type = int.class, defaultValue = "0", comment = "回复的评论")
    replyDiscussId,
    @Column(type = long.class, nullable = false, comment = "评论内容的主键 必须是数字主键")
    outId,
    @Column(length = 1000, nullable = false, comment = "评论内容")
    text,
    @Column(type = byte.class, defaultValue = "1", comment = "是否可见 0不可见 1可见")
    isShow,
    @Column(type = Date.class)
    createTime,
    @Column(type = Timestamp.class, timeForUpdate = true)
    modifiedTime
}
