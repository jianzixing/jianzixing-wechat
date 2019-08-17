package com.jianzixing.webapp.tables.discount;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.platform.Text;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableDiscount {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 100, nullable = false, comment = "优惠活动名称")
    name,
    @Column(type = byte.class, nullable = false, comment = "类型 0商品分类 1商品 2品牌")
    type,
    @Column(length = 200, nullable = false, comment = "活动实现类")
    impl,
    @Column(length = 200, nullable = false, comment = "活动实现名称，比如 满减")
    implName,
    @Column(length = 300, comment = "活动描述")
    detail,
    @Column(type = byte.class, defaultValue = "1", comment = "是否启用 0禁用 1启用 3已结束")
    enable,
    @Column(type = byte.class, defaultValue = "0", comment = "是否删除 0否 1是")
    isDel,
    @Column(type = int.class, defaultValue = "-1", comment = "优惠活动每个用户限制次数")
    count,
    @Column(length = 200, comment = "只作为前端展示时服务的参数")
    view,
    @Column(type = Text.class, comment = "活动配置参数")
    params,
    @Column(type = Date.class, nullable = false, comment = "开始时间")
    startTime,
    @Column(type = Date.class, nullable = false, comment = "结束时间")
    finishTime,
    @Column(type = Date.class, comment = "创建时间")
    createTime
}
