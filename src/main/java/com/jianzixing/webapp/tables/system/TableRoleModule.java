package com.jianzixing.webapp.tables.system;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;

/**
 * @author yangankang
 */
@Table
public enum TableRoleModule {
    @Column(pk = true, type = int.class)
    roleId,
    @Column(pk = true, type = int.class)
    moduleId
}
