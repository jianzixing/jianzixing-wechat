package com.jianzixing.webapp.tables.goods;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

/**
 * @author yangankang
 */
@Table
public enum TableGoodsGroup {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = int.class, defaultValue = "0")
    pid,
    @Column(length = 100, nullable = false)
    name,
    @Column(length = 200, comment = "商品分类图标")
    logo,
    @Column(type = byte.class, defaultValue = "0", comment = "是否展开树节点,默认不展开")
    expanded,
    @Column(type = byte.class, defaultValue = "0", comment = "是否是叶子节点,叶子节点不允许有子节点,默认不是")
    leaf,
    @Column(type = long.class, defaultValue = "0", comment = "当前分类的商品数量")
    count,
    @Column(length = 500, nullable = false, comment = "商品分类ID包含父类，如 10,20,21")
    list,
    @Column(length = 2000, comment = "商品分类名称包含父类，如 服装,上衣,连衣裙")
    listName,
    @Column(length = 300, comment = "描述")
    detail,
    @Column(type = int.class, comment = "商品分类和属性模板的关联ID")
    attrGroupId,
    @Column(type = int.class, defaultValue = "0")
    pos
}
