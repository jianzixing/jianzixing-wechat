package com.jianzixing.webapp.tables.wechat;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableWeChatAccount {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 30, nullable = false, comment = "公众号名称")
    name,
    @Column(length = 100, comment = "logo")
    logo,
    @Column(length = 30, unique = true, comment = "唯一标识码，唯一主键")
    code,
    @Column(length = 100, nullable = false, comment = "AppID")
    appId,
    @Column(length = 100, nullable = false, comment = "AppSecret")
    appSecret,
    @Column(length = 32, nullable = false, comment = "微信->基本配置->Token")
    appToken,
    @Column(length = 50, comment = "EncodingAESKey")
    appEncodingKey,
    @Column(type = byte.class, defaultValue = "0", comment = "消息加密方式 0明文 1兼容模式 2安全模式")
    encodingType,
    @Column(length = 512, comment = "accessToken")
    accessToken,

    @Column(length = 30, comment = "公众号类型")
    type,
    @Column(length = 100, comment = "原始ID")
    originalId,
    @Column(length = 100, comment = "微信号")
    weAccount,
    @Column(length = 100, comment = "二维码文件")
    qrCode,
    @Column(type = Date.class, comment = "最后一次更新token的时间")
    lastTokenTime,
    @Column(type = int.class, comment = "Token有效期 7200s")
    tokenExpires,

    @Column(type = byte.class, defaultValue = "1", comment = "是否有效 0无效 1有效")
    enable,
    @Column(type = byte.class, defaultValue = "0", comment = "是否是默认账号，和第三方平台账号一起只能有一个")
    isDefault,
    @Column(length = 300, comment = "描述")
    detail,
    @Column(type = byte.class, defaultValue = "0", comment = "微信服务器是否请求本系统成功")
    checked,
    @Column(type = Date.class, comment = "微信服务器连接消息验证时间")
    checkedTime,
    @Column(type = Date.class, nullable = false, comment = "创建时间")
    createTime
}
