package com.jianzixing.webapp.tables.wechat;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

@Table
public enum TableWeChatTrees {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = int.class, nullable = false, comment = "标识所属模块")
    type,
    @Column(type = int.class, defaultValue = "0")
    pid,
    @Column(length = 30, nullable = false, comment = "名称")
    text,
    @Column(length = 100, nullable = false, comment = "页面模块名称")
    module,
    @Column(type = byte.class, defaultValue = "1", comment = "是否是叶子节点")
    leaf,
    @Column(type = byte.class, defaultValue = "0", comment = "如果模块是书目录模块那么是否展开 1:true 0:false")
    expanded,
    @Column(type = byte.class, defaultValue = "1", comment = "是否启用 1是 0否")
    enable
}
