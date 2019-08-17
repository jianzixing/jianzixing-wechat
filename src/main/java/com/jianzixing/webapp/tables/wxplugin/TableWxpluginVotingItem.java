package com.jianzixing.webapp.tables.wxplugin;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.platform.Text;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

/**
 * 每一个投票参与者
 */
@Table
public enum TableWxpluginVotingItem {
    @Column(pk = true, strategy = AutoIncrementStrategy.class, comment = "主键")
    id,
    @Column(pk = true, type = int.class, nullable = false, comment = "所属组")
    gid,
    @Column(length = 100, nullable = false, comment = "投票标题或者名称")
    name,
    @Column(length = 500, nullable = false, comment = "投票标题或者名称")
    subName,
    @Column(length = 500, comment = "投票图片")
    icon,
    @Column(type = int.class, defaultValue = "0", comment = "获得总票数")
    count,
    @Column(type = Text.class, comment = "投票项目描述")
    detail,
    @Column(type = Date.class, comment = "创建时间")
    createTime
}
