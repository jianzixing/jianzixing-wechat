package com.jianzixing.webapp.tables.goods;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.platform.Text;

/**
 * @author yangankang
 */
@Table
public enum TableGoodsDescribe {
    @Column(pk = true, type = long.class, comment = "商品ID")
    goodsId,
    @Column(type = Text.class, nullable = false, comment = "商品描述")
    desc
}
