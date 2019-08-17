package com.jianzixing.webapp.tables.order;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

@Table
public enum TableOrderGoodsProperty {
    @Column(pk = true, type = long.class, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = long.class, nullable = false, comment = "订单ID")
    orderId,
    @Column(type = long.class, nullable = false, comment = "商品ID")
    goodsId,
    @Column(type = long.class, nullable = false, comment = "对应的规格商品ID")
    skuId,
    @Column(length = 100, nullable = false, comment = "属性名称")
    attrName,
    @Column(length = 100, nullable = false, comment = "属性值名称")
    valueName
}
