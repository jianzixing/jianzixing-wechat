package com.jianzixing.webapp.tables.order;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;

@Table
public enum TableOrderAddress {
    @Column(pk = true, type = long.class)
    orderId,
    @Column(length = 12, nullable = false, comment = "用户真实姓名")
    realName,
    @Column(length = 16, comment = "手机号码")
    phoneNumber,
    @Column(length = 16, comment = "电话号码")
    telNumber,
    @Column(length = 100, nullable = false, comment = "国家")
    country,
    @Column(length = 100, nullable = false, comment = "省")
    province,
    @Column(length = 100, nullable = false, comment = "市")
    city,
    @Column(length = 100, nullable = false, comment = "县")
    county,
    @Column(length = 500, comment = "详细地址")
    address,
    @Column(length = 100, comment = "电子邮件")
    email,
    @Column(length = 10, comment = "邮编号码")
    postcode
}
