package com.jianzixing.webapp.tables.wechat;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableWeChatApp {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 30, nullable = false, comment = "小程序名称")
    name,
    @Column(length = 30, nullable = false, unique = true, comment = "唯一标识码，唯一主键")
    code,
    @Column(length = 100, nullable = false, comment = "AppID")
    appId,
    @Column(length = 100, nullable = false, comment = "AppSecret")
    appSecret,
    @Column(length = 300, comment = "描述")
    detail,
    @Column(type = byte.class, defaultValue = "1", comment = "是否有效 0无效 1有效")
    enable,
    @Column(type = byte.class, defaultValue = "0", comment = "是否是默认账号")
    isDefault,
    @Column(type = Date.class)
    createTime
}
