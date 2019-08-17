package com.jianzixing.webapp.tables.logistics;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

/**
 * 免邮规则
 */
@Table
public enum TableLogisticsFreeRule {
    @Column(pk = true, type = long.class, nullable = false, comment = "运费模板")
    templateId,
    @Column(pk = true, type = int.class, nullable = false, comment = "一个排序值作为联合主键")
    index,
    @Column(type = byte.class, nullable = false, comment = "10快递 11EMS 12平邮")
    deliveryType,
    @Column(type = byte.class, defaultValue = "10", comment = "快递类型 11按件数 12按重量 13按体积")
    freeRule,
    @Column(type = byte.class, defaultValue = "0", comment = "包邮条件 1依照类型(件数，体积，重量) 2按照金额 3类型加金额")
    condition,
    @Column(type = double.class, defaultValue = "0", comment = "类型值，比如件数，体积，重量")
    value1,
    @Column(type = double.class, defaultValue = "0", comment = "金额值，就是钱")
    value2
}
