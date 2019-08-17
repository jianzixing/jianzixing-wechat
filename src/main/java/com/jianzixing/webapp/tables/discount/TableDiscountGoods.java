package com.jianzixing.webapp.tables.discount;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;

/**
 * 关联的商品ID
 */
@Table
public enum TableDiscountGoods {
    @Column(type = long.class, pk = true, comment = "活动ID")
    did,
    @Column(length = 64, pk = true, index = true, comment = "字母+id, B表示品牌  C表示商品分类  G表示商品ID  G..S..表示商品ID加SKU  E表示排除的商品ID")
    tid,
    @Column(length = 3, index = true, comment = "数据类型 B,C,G,GS,E")
    type,
    @Column(type = byte.class, defaultValue = "1", comment = "是否启用 0禁用 1启用")
    enable
}
