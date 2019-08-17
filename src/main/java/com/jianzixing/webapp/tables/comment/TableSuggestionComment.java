package com.jianzixing.webapp.tables.comment;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableSuggestionComment {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = int.class, comment = "用户ID")
    userId,
    @Column(length = 15, comment = "QQ号码")
    qq,
    @Column(length = 256, comment = "邮箱地址")
    email,
    @Column(length = 18, comment = "手机号码")
    phone,
    @Column(length = 10, defaultValue = "DEFAULT", comment = "意见类型")
    type,
    @Column(length = 2000, comment = "建议内容")
    text,
    @Column(length = 2000, comment = "回复内容")
    reply,
    @Column(type = Date.class)
    createTime
}
