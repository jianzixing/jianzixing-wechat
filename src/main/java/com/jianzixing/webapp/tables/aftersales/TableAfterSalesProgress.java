package com.jianzixing.webapp.tables.aftersales;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

/**
 * 审核进度表，售后进度每一步的反馈日志
 */
@Table
public enum TableAfterSalesProgress {
    @Column(pk = true, type = long.class, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 32, nullable = false, comment = "唯一售后单号,如：2017111013123141")
    number,
    @Column(type = long.class, nullable = false, comment = "售后ID")
    asid,
    @Column(type = byte.class, defaultValue = "0", comment = "售后状态")
    status,
    @Column(type = long.class, nullable = false, comment = "后台处理人")
    adminId,
    @Column(length = 2000, nullable = false, comment = "问题描述 必须")
    detail,
    @Column(type = Date.class, comment = "创建时间")
    createTime
}
