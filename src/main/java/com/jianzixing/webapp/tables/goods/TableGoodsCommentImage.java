package com.jianzixing.webapp.tables.goods;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;

@Table
public enum TableGoodsCommentImage {
    @Column(pk = true, type = long.class, nullable = false, comment = "商品评论ID")
    cmtId,
    @Column(pk = true, type = int.class, nullable = false, comment = "所属表的排序值")
    index,
    @Column(length = 100, comment = "商品品类图片的文件名称")
    fileName
}
