package com.jianzixing.webapp.tables.goods;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

/**
 * @author yangankang
 */
@Table
public enum TableGoodsSku {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = long.class, comment = "商品ID")
    goodsId,
    @Column(type = double.class, defaultValue = "0", extDecimalFormat = "#.00", comment = "商品销售价格")
    price,
    @Column(type = double.class, defaultValue = "0", extDecimalFormat = "#.00", comment = "商品会员价格")
    vipPrice,
    @Column(type = double.class, defaultValue = "0", extDecimalFormat = "#.00", comment = "商品原价格")
    originalPrice,
    @Column(type = double.class, defaultValue = "0", extDecimalFormat = "#.00", comment = "商品成本价格")
    costPrice,
    @Column(type = int.class, defaultValue = "0", comment = "商品数量")
    amount,
    @Column(length = 100, unique = true, comment = "商品编号,如果没有会自动生成")
    serialNumber,
    @Column(length = 30, comment = "商品条码")
    barcode,
    @Column(type = int.class, comment = "售卖总量")
    sellTotal
}
