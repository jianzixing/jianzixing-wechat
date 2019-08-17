package com.jianzixing.webapp.tables.coupon;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableCoupon {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 100, nullable = false, comment = "优惠活动名称")
    name,
    @Column(type = byte.class, defaultValue = "1", comment = "类型 0商品分类 1商品 2品牌")
    type,
    @Column(type = byte.class, defaultValue = "0", comment = "类型 0标准优惠券")
    couponType,
    @Column(type = byte.class, defaultValue = "0", comment = "类型 0其他渠道 1网站领取")
    channel,
    @Column(type = int.class, defaultValue = "0", comment = "发行量")
    amount,
    @Column(type = int.class, defaultValue = "1", comment = "优惠活动每个用户限制次数")
    count,
    @Column(type = int.class, defaultValue = "0", comment = "已领取数量")
    prepareAmount,
    @Column(type = double.class, nullable = false, comment = "满足价格，一个订单内相同优惠券可用商品价格总和")
    orderPrice,
    @Column(type = double.class, nullable = false, comment = "扣减价格")
    couponPrice,
    @Column(type = Date.class, nullable = false, comment = "开始时间")
    startTime,
    @Column(type = Date.class, nullable = false, comment = "结束时间")
    finishTime,
    @Column(length = 300, comment = "活动描述")
    detail,
    @Column(type = byte.class, defaultValue = "0", comment = "是否可以叠加 0不可叠加 1可叠加")
    overlay,
    @Column(type = byte.class, defaultValue = "0", comment = "优惠券状态 0未启用 1未开始 2获取中 3已结束")
    status,
    @Column(type = byte.class, defaultValue = "0", comment = "是否删除 0否 1是")
    isDel,
    @Column(type = Date.class, comment = "创建时间")
    createTime
}
