package com.jianzixing.webapp.tables.system;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

@Table
public enum TableModule {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(nullable = false, type = int.class, defaultValue = "0")
    pid,
    @Column(comment = "模块实现")
    module,
    @Column(nullable = false, comment = "所属模块")
    linkModule,
    @Column(nullable = false, comment = "模块名称")
    text,
    @Column(comment = "附加值用于区分同模块的不同状态")
    type,
    @Column(comment = "模块的图标")
    tabIcon,
    @Column(type = byte.class, defaultValue = "0", comment = "如果模块是书目录模块那么是否展开 1:true 0:false")
    expanded,
    @Column(type = byte.class, defaultValue = "0", comment = "是否是叶子节点 1:true 0:false")
    leaf,
    @Column(type = byte.class, defaultValue = "0", comment = "是否是顶级节点 1:true 0:false")
    top,
    @Column(type = byte.class, defaultValue = "0", comment = "是否是系统菜单,系统菜单不允许删除")
    isSystem,
    @Column(type = int.class, defaultValue = "0", comment = "排序")
    pos,
    @Column(length = 300, comment = "如果type=extension则需要url去获取子菜单模块")
    url,
    @Column(length = 100)
    detail,
}
