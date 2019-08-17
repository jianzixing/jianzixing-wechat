package com.jianzixing.webapp.tables.wxplugin;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableWxpluginSignGet {
    @Column(pk = true, strategy = AutoIncrementStrategy.class, comment = "主键")
    id,
    @Column(type = int.class, nullable = false, comment = "所属组")
    gid,
    @Column(type = byte.class, defaultValue = "0", comment = "奖励类型 0默认非积累类奖励，比如优惠券实物商品等  1积累类奖励,比如每次签到赠送积分")
    type,
    @Column(type = long.class, nullable = false, comment = "签到用户")
    userId,
    @Column(type = int.class, defaultValue = "0", comment = "签到总次数")
    count,
    @Column(type = int.class, defaultValue = "0", comment = "连续签到次数")
    cntCount,
    @Column(type = int.class, nullable = false, comment = "获得奖品")
    said,
    @Column(type = int.class, defaultValue = "0", comment = "如果是积累类奖励则每次增加数量")
    totalAmount,
    @Column(type = byte.class, defaultValue = "0", comment = "是否使用，0否 1是")
    isUsed,
    @Column(type = Date.class, comment = "创建时间")
    createTime
}
