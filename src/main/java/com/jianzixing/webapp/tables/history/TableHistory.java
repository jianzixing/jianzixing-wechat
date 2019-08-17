package com.jianzixing.webapp.tables.history;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;

import java.util.Date;

/**
 * 浏览记录
 */
@Table
public enum TableHistory {
    @Column(type = long.class, pk = true, comment = "用户ID")
    uid,
    @Column(type = long.class, pk = true, comment = "收藏数据的ID，比如商品ID")
    hid,
    @Column(type = byte.class, pk = true, comment = "收藏类型 1:商品")
    type,
    @Column(type = Date.class, nullable = false, comment = "最后浏览时间")
    lastTime,
    @Column(type = Date.class, nullable = false, comment = "第一次浏览时间")
    createTime
}
