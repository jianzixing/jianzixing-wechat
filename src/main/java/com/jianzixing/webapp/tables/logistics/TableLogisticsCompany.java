package com.jianzixing.webapp.tables.logistics;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

/**
 * 这里配置物流公司一般价格,或者默认价格，仅做参考使用
 * 一般如果配置了物流运费模板这个价格会被覆盖
 */
@Table
public enum TableLogisticsCompany {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 30, nullable = false, comment = "物流公司名称")
    name,
    @Column(length = 30, unique = true, comment = "全局唯一的编码,如果被删除可以重新添加")
    code,
    @Column(length = 500, comment = "物流公司LOGO")
    logo,
    @Column(type = byte.class, defaultValue = "0", comment = "是否默认物流")
    isDefault,
    @Column(type = byte.class, defaultValue = "1", comment = "是否有效 0无效 1有效")
    enable,
    @Column(length = 200, comment = "物流公司描述")
    detail
}
