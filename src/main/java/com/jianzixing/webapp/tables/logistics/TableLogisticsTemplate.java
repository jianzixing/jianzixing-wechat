package com.jianzixing.webapp.tables.logistics;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableLogisticsTemplate {
    @Column(pk = true, type = long.class, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 30, nullable = false, comment = "物流模板名称")
    name,
    @Column(type = byte.class, defaultValue = "0", comment = "是否包邮 0否 1是 如果否可以指定条件包邮")
    free,
    @Column(type = byte.class, defaultValue = "10", comment = "计价方式 10包邮 11按件数 12按重量 13按体积")
    type,
    @Column(length = 30, nullable = false, comment = "包含的快递类型 10快递 11EMS 12平邮 格式如:10,11,12")
    deliveryType,
    @Column(type = Date.class, nullable = false, comment = "创建时间")
    createTime
}
