package com.jianzixing.webapp.tables.marketing;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

@Table
public enum TableMarketEmail {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 100, nullable = false, comment = "邮件服务名称")
    name,
    @Column(length = 200, nullable = false, comment = "SMTP服务器地址")
    smtpAddress,
    @Column(length = 10, nullable = false, comment = "SMTP服务器端口")
    smtpPort,
    @Column(length = 200, nullable = false, comment = "邮箱用户名")
    smtpUserName,
    @Column(length = 100, nullable = false, comment = "邮箱密码")
    smtpPassword,
    @Column(type = byte.class, defaultValue = "1", comment = "是否使用 0否 1是")
    enable,
    @Column(length = 20, defaultValue = "UTF-8", comment = "邮件编码")
    encoding,
    @Column(type = byte.class, defaultValue = "0", comment = "是否使用SSL链接 0否 1是")
    ssl,
    @Column(length = 500, comment = "备注")
    remark,
}
