package com.jianzixing.webapp.tables.marketing;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.platform.Text;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

/**
 * 站内信
 */
@Table
public enum TableMarketMessage {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class, comment = "主键")
    id,
    @Column(type = long.class, defaultValue = "0", comment = "消息来源0表示系统发送 大于0表示用户发送")
    from,
    @Column(type = long.class, defaultValue = "0", comment = "发送目标0表示发送给系统 大于0表示发送给用户")
    target,
    @Column(length = 200, nullable = false, comment = "消息标题")
    title,
    @Column(type = Text.class, nullable = false, comment = "消息内容")
    content,
    @Column(type = Date.class, nullable = false, comment = "创建时间")
    createTime
}
