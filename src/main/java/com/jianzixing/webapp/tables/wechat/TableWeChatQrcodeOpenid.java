package com.jianzixing.webapp.tables.wechat;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;

import java.util.Date;

@Table
public enum TableWeChatQrcodeOpenid {
    @Column(pk = true, type = long.class, comment = "二维码ID")
    qrcodeId,
    @Column(pk = true, length = 100, comment = "openid")
    openid,
    @Column(type = byte.class, defaultValue = "1", comment = "是否关注")
    mark,
    @Column(type = Date.class)
    createTime
}
