package com.jianzixing.webapp.tables.collect;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;

import java.util.Date;

@Table
public enum TableCollect {
    @Column(type = long.class, pk = true, comment = "用户ID")
    uid,
    @Column(type = long.class, pk = true, comment = "收藏数据的ID，比如商品ID")
    cid,
    @Column(type = long.class, defaultValue = "0", comment = "子关联ID，比如商品sku")
    sid,
    @Column(type = byte.class, pk = true, comment = "收藏类型 1:商品")
    type,
    @Column(type = Date.class, nullable = false, comment = "收藏时间")
    createTime
}
