package com.jianzixing.webapp.tables.goods;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableGoodsComment {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class, comment = "主键")
    id,
    @Column(type = long.class, nullable = false, comment = "评论用户")
    userId,
    @Column(extMinLength = 6, length = 30, nullable = false)
    userName,
    @Column(length = 110, unique = true, comment = "微信登录用户id,分两部分前部分是应用类型和应用ID后部分是openid")
    openid,
    @Column(type = long.class, nullable = false, comment = "订单ID")
    orderId,
    @Column(type = long.class, nullable = false, comment = "订单内的商品ID")
    orderGoodsId,
    @Column(type = long.class, nullable = false, comment = "商品ID")
    goodsId,
    @Column(type = long.class, defaultValue = "0", comment = "商品SKU ID")
    skuId,
    @Column(length = 100, unique = true, comment = "商品编号,如果没有填写会自动生成")
    serialNumber,
    @Column(type = byte.class, defaultValue = "0", comment = "总体购物评分")
    score,
    @Column(type = byte.class, defaultValue = "0", comment = "发货速度")
    speedScore,
    @Column(type = byte.class, defaultValue = "0", comment = "物流评分")
    logisticsScore,
    @Column(type = byte.class, defaultValue = "0", comment = "服务评分")
    serviceScore,
    @Column(length = 500, comment = "评论")
    comment,
    @Column(length = 500, comment = "购买商品简短说明，比如购买颜色:红色;尺码:L")
    goodsSku,
    @Column(type = byte.class, defaultValue = "0", comment = "是否包含图片 0否 1是")
    hasImg,
    @Column(type = byte.class, defaultValue = "0", comment = "是否匿名 0否 1是")
    anonymity,
    @Column(type = Date.class, comment = "评论时间")
    createTime
}
