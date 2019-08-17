package com.jianzixing.webapp.tables.marketing;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableMarketEmailRecord {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 100, nullable = false, comment = "发送人")
    from,
    @Column(length = 100, nullable = false, comment = "接收人")
    to,
    @Column(type = int.class, nullable = false, comment = "所属邮件服务")
    emailId,
    @Column(length = 1000, nullable = false, comment = "发送内容标题")
    subject,
    @Column(length = 200, comment = "执行结果描述")
    result,
    @Column(type = Date.class, comment = "创建时间")
    createTime
}
