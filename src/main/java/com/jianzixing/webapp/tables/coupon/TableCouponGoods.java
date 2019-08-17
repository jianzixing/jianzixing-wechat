package com.jianzixing.webapp.tables.coupon;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;

@Table
public enum TableCouponGoods {
    @Column(type = long.class, pk = true, comment = "优惠券ID")
    cid,
    @Column(length = 64, pk = true, index = true, comment = "字母+id, B表示品牌  C表示商品分类  G表示商品ID  G..S..表示商品ID加SKU  E表示排除的商品ID")
    tid,
    @Column(length = 3, index = true, comment = "数据类型 B,C,G,GS,E")
    type,
    @Column(type = byte.class, defaultValue = "1", comment = "是否有效(如果优惠券删除或者状态不是获取中就是无效的) 0无效 1有效")
    enable
}
