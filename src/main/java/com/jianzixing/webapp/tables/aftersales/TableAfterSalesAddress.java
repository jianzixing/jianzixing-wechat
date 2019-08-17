package com.jianzixing.webapp.tables.aftersales;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;

import java.util.Date;

/**
 * 售后商品返还地址，可以是取货地址也可以是寄送地址
 */
@Table
public enum TableAfterSalesAddress {
    @Column(pk = true, type = long.class, comment = "售后ID")
    asid,
    @Column(type = byte.class, pk = true, defaultValue = "0", comment = "售后地址类型 0买家收货地址  1卖家快递地址")
    type,
    @Column(length = 50, nullable = false, comment = "收货人姓名")
    realName,
    @Column(length = 16, nullable = false, comment = "手机号码")
    phoneNumber,
    @Column(length = 1000, nullable = false, comment = "详细地址")
    address,
    @Column(length = 30, comment = "全局唯一的物流公司编码")
    lgsCompanyCode,
    @Column(length = 30, comment = "物流公司名称")
    lgsCompanyName,
    @Column(length = 100, comment = "物流单号")
    trackingNumber,
    @Column(type = Date.class, nullable = false, comment = "创建时间")
    createTime
}
