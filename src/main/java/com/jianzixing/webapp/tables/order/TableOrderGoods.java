package com.jianzixing.webapp.tables.order;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

/**
 * @author yangankang
 */
@Table
public enum TableOrderGoods {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = long.class, nullable = false, comment = "订单ID")
    orderId,
    @Column(type = long.class, nullable = false, comment = "商品ID")
    goodsId,
    @Column(length = 100, comment = "商品封面图片文件名称")
    fileName,
    @Column(nullable = false, length = 200, comment = "商品名称")
    goodsName,
    @Column(type = byte.class, defaultValue = "10", comment = "商品类型 10:实物商品  11:虚拟商品 20:第三方服务")
    type,
    @Column(type = long.class, defaultValue = "0", comment = "商品SKU信息,如果商品没有SKU可以为空")
    skuId,
    @Column(type = double.class, defaultValue = "0", extDecimalFormat = "#.00", comment = "商品销售价格")
    price,
    @Column(type = double.class, defaultValue = "0", extDecimalFormat = "#.00", comment = "商品会员价格")
    vipPrice,
    @Column(type = double.class, defaultValue = "0", extDecimalFormat = "#.00", comment = "商品原价格")
    originalPrice,
    @Column(type = double.class, defaultValue = "0", extDecimalFormat = "#.00", comment = "商品成本价格")
    costPrice,
    @Column(type = double.class, nullable = false, defaultValue = "0", extDecimalFormat = "#.00", comment = "支付价格,(商品价格*数量)-优惠价格")
    payPrice,
    @Column(type = double.class, defaultValue = "0", extDecimalFormat = "#.00", comment = "优惠扣减的钱数(仅商品本身)")
    discountPrice,
    @Column(type = long.class, comment = "如果有优惠对应的优惠活动ID")
    did,
    @Column(type = int.class, defaultValue = "0", comment = "购买的数量")
    amount,
    @Column(length = 100, comment = "商品编号")
    serialNumber,
    @Column(type = byte.class, defaultValue = "0", comment = "如果有售后，则记录售后类型(最后一次售后记录)")
    afterSaleType
}
