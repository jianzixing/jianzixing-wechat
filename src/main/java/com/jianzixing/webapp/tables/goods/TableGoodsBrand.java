package com.jianzixing.webapp.tables.goods;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.platform.Text;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

/**
 * @author yangankang
 */
@Table
public enum TableGoodsBrand {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 100, nullable = false)
    name,
    @Column(length = 100, comment = "品牌图片文件的ID")
    logo,
    @Column(type = int.class, defaultValue = "0")
    pos,
    @Column(type = Text.class, comment = "品牌描述")
    detail
}
