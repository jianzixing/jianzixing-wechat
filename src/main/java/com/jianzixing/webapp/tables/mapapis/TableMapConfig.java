package com.jianzixing.webapp.tables.mapapis;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;

@Table("t_map_config")
public enum TableMapConfig {
    @Column(pk = true)
    type,
    @Column(pk = true)
    name,
    @Column
    value,
    @Column(type = int.class)
    pos
}
