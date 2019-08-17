package com.jianzixing.webapp.tables.wechat;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.platform.Text;

/**
 * 如果一个图文有多个图文信息,所以单独把图文信息拿出来
 */
@Table
public enum TableWeChatImageTextSub {
    @Column(pk = true, type = long.class, comment = "图文id")
    imageTextId,
    @Column(type = int.class, pk = true, comment = "组合下标")
    index,
    @Column(length = 1500, nullable = false, comment = "封面url或者低质音频url等等")
    coverUrl,
    @Column(length = 300, comment = "封面url在微信服务里的mediaId")
    thumbMediaId,
    @Column(length = 100, nullable = false, comment = "当前系统图文的host")
    host,
    @Column(length = 500, nullable = false, comment = "图文url或者高质音频url等等,原文地址")
    resUrl,
    @Column(length = 500, comment = "保存图文后同步到微信的url地址")
    url,
    @Column(length = 200, nullable = false, comment = "素材标题")
    title,
    @Column(length = 200, comment = "作者")
    author,
    @Column(length = 1000, nullable = false, comment = "素材描述")
    desc,
    @Column(type = Text.class, nullable = false, comment = "图文内容")
    content
}
