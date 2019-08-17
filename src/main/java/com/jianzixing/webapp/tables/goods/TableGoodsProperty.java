package com.jianzixing.webapp.tables.goods;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

/**
 * @author yangankang
 */
@Table
public enum TableGoodsProperty {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = long.class, nullable = false, comment = "商品ID")
    goodsId,
    @Column(type = long.class, defaultValue = "0", comment = "对应的规格商品ID")
    skuId,
    @Column(type = long.class, nullable = false, comment = "对应TableGoodsParameter表中的属性名称ID,如果没有为0")
    attrId,
    @Column(length = 100, nullable = false, comment = "属性名称")
    attrName,
    @Column(type = long.class, nullable = false, comment = "对应TableGoodsValue表中的属性值ID,如果没有为0")
    valueId,
    @Column(length = 100, nullable = false, comment = "属性值名称")
    valueName,
    @Column(type = int.class, defaultValue = "0", comment = "排序")
    pos
}
