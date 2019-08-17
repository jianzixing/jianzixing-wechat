package com.jianzixing.webapp.tables.wechat;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableWeChatOpen {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 30, nullable = false, comment = "第三方平台名称")
    name,
    @Column(length = 100, comment = "logo")
    logo,
    @Column(length = 30, unique = true, comment = "账号Code，唯一主键")
    code,
    @Column(length = 30, nullable = false, comment = "AppID")
    appId,
    @Column(length = 60, nullable = false, comment = "AppSecret")
    appSecret,
    @Column(length = 200, comment = "消息校验Token")
    appToken,
    @Column(length = 200, comment = "消息加解密Key")
    appKey,

    @Column(length = 200, comment = "预授权码")
    preAuthCode,
    @Column(type = int.class, comment = "Token有效期 7200s")
    preExpires,
    @Column(type = Date.class, comment = "最后一次更新preAuthCode的时间")
    lastPreTime,

    @Column(length = 200, comment = "推送component_verify_ticket协议")
    componentVerifyTicket,

    @Column(length = 200, comment = "获取第三方平台component_access_token")
    componentAccessToken,
    @Column(type = int.class, comment = "Token有效期 7200s")
    accessTokenExpires,
    @Column(type = Date.class, comment = "最后一次更新componentAccessToken的时间")
    lastTokenTime,

    @Column(type = byte.class, defaultValue = "1", comment = "是否有效 0无效 1有效")
    enable,
    @Column(type = byte.class, defaultValue = "0", comment = "微信服务器是否请求本系统成功 0否 1是")
    checked,
    @Column(length = 300, comment = "描述")
    detail,
    @Column(type = Date.class)
    createTime
}
