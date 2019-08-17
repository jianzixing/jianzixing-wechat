package com.jianzixing.webapp.tables.aftersales;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableAfterSales {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = long.class, nullable = false, comment = "售后单申请的用户ID")
    userId,
    @Column(type = long.class, nullable = false, comment = "订单ID")
    orderId,
    @Column(length = 32, nullable = false, unique = true, comment = "唯一售后单号,如：2017111013123141")
    number,
    @Column(type = byte.class, defaultValue = "0", comment = "售后状态")
    status,
    @Column(type = byte.class, defaultValue = "0", comment = "售后类型 10退货 20换货 30维修")
    type,
    @Column(type = long.class, nullable = false, comment = "订单商品的ID关联表 TableOrderGoods")
    orderGoodsId,
    @Column(type = int.class, defaultValue = "0", comment = "售后商品数量")
    amount,
    @Column(type = byte.class, defaultValue = "0", comment = "是否有发票 0无 1有")
    hasInvoice,
    @Column(type = byte.class, defaultValue = "0", comment = "是否有检测报告 0无 1有")
    hasTestReport,
    @Column(length = 100, nullable = false, comment = "售后原因")
    reason,
    @Column(length = 500, nullable = false, comment = "问题描述 必须")
    detail,
    @Column(type = byte.class, defaultValue = "0", comment = "商品返还方式 0快递")
    deliveryType,
    @Column(length = 32, comment = "用户提交的IP地址")
    ip,
    @Column(length = 200, comment = "客服给出的备注")
    remark,
    @Column(type = Date.class, nullable = false, comment = "创建时间")
    createTime
}
