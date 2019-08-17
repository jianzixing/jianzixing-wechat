package com.jianzixing.webapp.tables.shopcart;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableShoppingCart {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class, comment = "主键")
    id,
    @Column(type = long.class, nullable = false, comment = "用户ID")
    userId,
    @Column(type = long.class, nullable = false, comment = "商品ID")
    gid,
    @Column(type = long.class, defaultValue = "0", comment = "商品SKU ID")
    skuId,
    @Column(type = int.class, defaultValue = "1", comment = "购买数量")
    amount,
    @Column(type = int.class, defaultValue = "1", comment = "是否选中")
    isChecked,
    @Column(type = int.class, comment = "促销ID")
    discountId,
    @Column(type = Date.class, comment = "创建时间")
    createTime,
    @Column(type = Date.class, timeForUpdate = true, comment = "修改时间")
    modifyTime
}
