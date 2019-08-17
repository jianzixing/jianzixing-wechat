package com.jianzixing.webapp.tables.order;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

/**
 * @author yangankang
 */
@Table
public enum TableOrder {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = long.class, nullable = false, comment = "购买用户的ID")
    userId,
    @Column(length = 32, nullable = false, unique = true, comment = "唯一订单号,如：2017111013123141")
    number,
    @Column(type = long.class, defaultValue = "0", comment = "收货地址ID，如果是虚拟商品则不需要")
    addressId,
    @Column(type = int.class, defaultValue = "0", comment = "配送信息ID,对应商品里的运费模板运送方式DeliveryType中查看，如果是虚拟商品则没有")
    deliveryId,
    @Column(type = double.class, nullable = false, comment = "原价，不包含优惠的商品价格")
    costPrice,
    @Column(type = double.class, nullable = false, comment = "商品价格总计,去除优惠后的应付金额")
    totalGoodsPrice,
    @Column(type = double.class, defaultValue = "0", comment = "优惠扣除价格")
    discountPrice,
    @Column(type = double.class, nullable = false, comment = "支付价格总计，必须包含totalGoodsPrice，额外的比如运费等")
    payPrice,
    @Column(type = double.class, defaultValue = "0", comment = "运费价格,免运费为0")
    freightPrice,
    @Column(type = byte.class, defaultValue = "0", comment = "订单状态 0创建")
    status,
    @Column(type = byte.class, defaultValue = "0", comment = "支付状态 0未支付 1已支付")
    payStatus,
    @Column(type = byte.class, defaultValue = "0", comment = "支付状态 0未评价 1已评价")
    discussStatus,
    @Column(type = byte.class, defaultValue = "0", comment = "订单退款状态 0未退款 1全部退款 2部分退款")
    refundStatus,
    @Column(length = 50, comment = "物流公司名称")
    lgsCompanyName,
    @Column(length = 50, comment = "全局唯一的物流公司编码")
    lgsCompanyCode,
    @Column(length = 100, comment = "物流单号")
    trackingNumber,
    @Column(type = Date.class, nullable = false, comment = "创建订单时间")
    createTime,
    @Column(type = Date.class, comment = "支付订单时间")
    payTime,
    @Column(type = Date.class, comment = "订单发货时间")
    sendTime,
    @Column(type = Date.class, comment = "确认收货时间")
    getTime,
    @Column(type = Date.class, comment = "订单取消时间时间")
    cancelTime,
    @Column(length = 300, comment = "用户下单留言")
    message,
    @Column(type = byte.class, defaultValue = "0", comment = "是否标记删除 0否 1是")
    isDel
}
