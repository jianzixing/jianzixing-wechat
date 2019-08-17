package com.jianzixing.webapp.tables.promotions;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

@Table
public enum TablePlwAward {
    @Column(pk = true, type = long.class, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = long.class, nullable = false, comment = "所属规则ID")
    rid,
    @Column(length = 10, nullable = false, comment = "奖品名称")
    name,
    @Column(length = 100, nullable = false, comment = "抽奖图片")
    img,
    @Column(type = byte.class, defaultValue = "0", comment = "中奖几率默认是0,多个奖品几率和是100%")
    odds,
    @Column(type = int.class, defaultValue = "0", comment = "剩余数量")
    amount
}
