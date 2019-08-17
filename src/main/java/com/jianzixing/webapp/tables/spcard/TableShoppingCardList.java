package com.jianzixing.webapp.tables.spcard;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;

import java.util.Date;

@Table
public enum TableShoppingCardList {
    @Column(type = int.class, pk = true, comment = "所属批次")
    scid,
    @Column(length = 19, pk = true, index = true, comment = "卡号")
    cardNumber,
    @Column(length = 200, nullable = false, comment = "密码，使用AES加密请牢记加密密码")
    password,
    @Column(type = long.class, nullable = false, comment = "绑定用户")
    uid,
    @Column(type = double.class, nullable = false, comment = "购物卡面额")
    money,
    @Column(type = double.class, nullable = false, comment = "购物卡余额")
    balance,
    @Column(type = Date.class, nullable = false, comment = "购物卡绑定时间")
    bindTime,
    @Column(type = Date.class, comment = "购物卡使用时间")
    useTime,
    @Column(type = byte.class, defaultValue = "0", comment = "购物卡状态 0有效或使用中  1作废 2已使用")
    status,
    @Column(type = Date.class, nullable = false, comment = "购物卡创建时间")
    createTime
}
