package com.jianzixing.webapp.tables.marketing;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableMarketSmsAuthCode {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 18, nullable = false, comment = "发送手机号")
    phone,
    @Column(length = 16, nullable = false, comment = "发送验证码的类型")
    type,
    @Column(length = 8, nullable = false, comment = "验证码")
    code,
    @Column(type = Date.class, nullable = false, comment = "创建时间")
    createTime
}
