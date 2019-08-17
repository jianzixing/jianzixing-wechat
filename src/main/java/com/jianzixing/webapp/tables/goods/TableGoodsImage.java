package com.jianzixing.webapp.tables.goods;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

/**
 * @author yangankang
 */
@Table
public enum TableGoodsImage {
    @Column(pk = true, type = long.class, nullable = false, comment = "商品ID")
    goodsId,
    @Column(pk = true, type = int.class, nullable = false, comment = "所属表的排序值")
    index,
    @Column(type = long.class, defaultValue = "0", comment = "属性ID,如果有值表示某个属性的图集比如:红色衣服")
    attrId,
    @Column(type = long.class, defaultValue = "0", comment = "属性ID,如果有值表示某个属性的图集比如:红色衣服")
    valueId,
    @Column(length = 100, comment = "商品图片的文件名称")
    fileName
}
