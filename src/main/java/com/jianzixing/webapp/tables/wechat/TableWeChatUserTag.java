package com.jianzixing.webapp.tables.wechat;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;

/**
 * "用户被打上的标签ID列表"
 */
@Table
public enum TableWeChatUserTag {
    @Column(pk = true, type = int.class, nullable = false, comment = "TableWeChatUser的ID")
    wcuId,
    @Column(pk = true, type = int.class, nullable = false, comment = "标签ID")
    tagid,
    @Column(length = 200, comment = "标签名称")
    tagName
}