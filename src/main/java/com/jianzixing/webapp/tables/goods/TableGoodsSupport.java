package com.jianzixing.webapp.tables.goods;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;

@Table
public enum TableGoodsSupport {
    @Column(type = int.class, pk = true, comment = "商品ID")
    goodsId,
    @Column(type = int.class, pk = true, comment = "商品服务ID")
    supportId,
    @Column(length = 100, nullable = false, comment = "商品服务名称")
    supportName
}
