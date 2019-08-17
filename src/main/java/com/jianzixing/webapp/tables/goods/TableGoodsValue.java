package com.jianzixing.webapp.tables.goods;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

/**
 * @author yangankang
 */
@Table
public enum TableGoodsValue {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = long.class, nullable = false, comment = "属性名称")
    parameterId,
    @Column(length = 100, nullable = false, comment = "属性值")
    value,
    @Column(length = 7, comment = "如果是颜色值，则记录16位颜色值")
    color,
    @Column(type = int.class, defaultValue = "0")
    pos
}
