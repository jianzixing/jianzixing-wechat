package com.jianzixing.webapp.tables.goods;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;

@Table
public enum TableGoodsParameterRel {
    @Column(type = long.class, pk = true, comment = "商品属性ID")
    parameterId,
    @Column(type = int.class, pk = true, comment = "商品属性模板ID")
    groupId
}
