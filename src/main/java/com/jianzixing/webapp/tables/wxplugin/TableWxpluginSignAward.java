package com.jianzixing.webapp.tables.wxplugin;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

/**
 * 微信签到奖励表
 */
@Table
public enum TableWxpluginSignAward {
    @Column(pk = true, strategy = AutoIncrementStrategy.class, comment = "主键")
    id,
    @Column(type = int.class, nullable = false, comment = "所属组")
    gid,
    @Column(type = byte.class, defaultValue = "0", comment = "奖励类型 0默认非积累类奖励，比如优惠券实物商品等  1虚拟积分,比如每次签到赠送积分")
    type,
    @Column(type = byte.class, defaultValue = "0", comment = "0连续签到赠送 1每天赠送 2总计签到次数赠送")
    everyday,
    @Column(length = 30, nullable = false, comment = "奖励短名称")
    name,
    @Column(length = 150, nullable = false, comment = "奖励长名称")
    subName,
    @Column(length = 200, comment = "奖励图片，可以是商品可以是优惠券之类的，如果是积累类可以不设置")
    icon,
    @Column(type = int.class, defaultValue = "0", comment = "连续签到次数可获得该奖励，如果是积累类型奖励则表示赠送数量")
    count,
    @Column(length = 500, comment = "奖励描述")
    detail,
    @Column(type = Date.class, comment = "创建时间")
    createTime
}
