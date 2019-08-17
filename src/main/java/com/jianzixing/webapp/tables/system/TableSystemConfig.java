package com.jianzixing.webapp.tables.system;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;

/**
 * @author yangankang
 */
@Table
public enum TableSystemConfig {
    @Column(length = 200, nullable = false, pk = true)
    key,
    @Column(length = 1000, nullable = false, comment = "系统参数值")
    value,
    @Column(type = int.class, defaultValue = "1", comment = "分组ID")
    gid,
    @Column(length = 100, nullable = false, comment = "配置名称")
    name,
    @Column(type = int.class, defaultValue = "0", comment = "系统参数类型 0文字 1图片")
    type,
    @Column(length = 1000, comment = "配置描述")
    detail,
    @Column(type = byte.class, defaultValue = "0", comment = "0不是系统参数  1是系统参数 2是隐藏参数")
    isSystem,
    @Column(type = int.class, defaultValue = "0")
    pos
}
