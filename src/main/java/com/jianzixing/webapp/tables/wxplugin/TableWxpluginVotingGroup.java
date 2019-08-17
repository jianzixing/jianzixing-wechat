package com.jianzixing.webapp.tables.wxplugin;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.platform.Text;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

/**
 * 投票分组
 */
@Table
public enum TableWxpluginVotingGroup {
    @Column(pk = true, strategy = AutoIncrementStrategy.class, comment = "主键")
    id,
    @Column(length = 100, nullable = false, comment = "组名称")
    name,
    @Column(length = 100, unique = true, nullable = false, comment = "组编码,作为url的一部分")
    code,
    @Column(type = byte.class, defaultValue = "0", comment = "每个投票项目是否一个用户可以多次投票 0一项一票  1一项多票")
    type,
    @Column(type = Date.class, nullable = false, comment = "投票开始时间")
    startTime,
    @Column(type = Date.class, nullable = false, comment = "投票结束时间")
    finishTime,
    @Column(type = int.class, defaultValue = "1", comment = "每个人最多投票几次")
    count,
    @Column(type = Text.class, comment = "组描述")
    detail,
    @Column(type = byte.class, defaultValue = "1", comment = "是否启用 0否 1是")
    enable,
    @Column(type = Date.class, comment = "创建时间")
    createTime
}
