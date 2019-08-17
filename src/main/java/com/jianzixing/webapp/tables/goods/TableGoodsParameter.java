package com.jianzixing.webapp.tables.goods;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

/**
 * @author yangankang
 */
@Table
public enum TableGoodsParameter {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 100, nullable = false)
    name,
    @Column(type = byte.class, defaultValue = "0", comment = "参数类型 0:表示输入框  1:多项选择  2:下拉列表 4:URL (假设isOnlySelf为true,type为4则是跳转url)")
    type,
    @Column(type = byte.class, defaultValue = "0", comment = "是否是主要的属性 0:不是 1:是 主要属性是用来区分不同的商品规格的")
    isPrimary,
    @Column(type = byte.class, defaultValue = "0", comment = "是否是颜色属性  0:不是 1:是  如果是的话那么可以设置相关颜色")
    isColor,
    @Column(type = int.class, defaultValue = "0")
    pos
}
