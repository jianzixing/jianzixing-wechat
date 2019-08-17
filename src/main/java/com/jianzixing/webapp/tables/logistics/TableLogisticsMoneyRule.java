package com.jianzixing.webapp.tables.logistics;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

/**
 * 邮费规则
 */
@Table
public enum TableLogisticsMoneyRule {
    @Column(pk = true, type = long.class, nullable = false, comment = "运费模板")
    templateId,
    @Column(pk = true, type = int.class, nullable = false, comment = "一个排序值作为联合主键")
    index,
    @Column(type = byte.class, defaultValue = "0", comment = "是否默认运费，如果不是的话国家省市区必须填写 1是 0否")
    isDefault,
    @Column(type = byte.class, nullable = false, comment = "10快递 11EMS 12平邮")
    deliveryType,
    @Column(type = double.class, nullable = false, comment = "第一件")
    first,
    @Column(type = double.class, nullable = false, comment = "第一件价格")
    firstMoney,
    @Column(type = double.class, nullable = false, comment = "第二个")
    next,
    @Column(type = double.class, nullable = false, comment = "第二件价格")
    nextMoney
}
