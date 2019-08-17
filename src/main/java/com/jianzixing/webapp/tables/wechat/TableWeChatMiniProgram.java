package com.jianzixing.webapp.tables.wechat;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableWeChatMiniProgram {
    @Column(pk = true, strategy = AutoIncrementStrategy.class, comment = "主键")
    id,
    @Column(length = 30, nullable = false, comment = "小程序名称")
    name,
    @Column(length = 30, nullable = false, unique = true, comment = "唯一标识码，唯一主键")
    code,
    @Column(length = 100, nullable = false, comment = "AppID")
    appId,
    @Column(length = 100, nullable = false, comment = "AppSecret")
    appSecret,
    @Column(length = 512, comment = "accessToken")
    accessToken,
    @Column(type = int.class, comment = "Token有效期 7200s")
    expiresIn,
    @Column(type = Date.class, comment = "最后一次更新token的时间")
    lastTokenTime,
    @Column(length = 300, comment = "描述")
    detail,
    @Column(type = byte.class, defaultValue = "1", comment = "是否有效 0无效 1有效")
    enable,
    @Column(type = byte.class, defaultValue = "0", comment = "是否是默认账号")
    isDefault,
    @Column(type = Date.class)
    createTime
}
