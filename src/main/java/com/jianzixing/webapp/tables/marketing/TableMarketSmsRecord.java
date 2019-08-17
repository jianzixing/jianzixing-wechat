package com.jianzixing.webapp.tables.marketing;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableMarketSmsRecord {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 18, nullable = false, comment = "发送手机号")
    phone,
    @Column(type = int.class, nullable = false, comment = "所属短信服务")
    smsId,
    @Column(type = byte.class, nullable = false, comment = "发送短信类型，比如验证码，营销短信  0其它  1验证码")
    action,
    @Column(length = 2000, nullable = false, comment = "发送内容或者发送的短信模板")
    content,
    @Column(length = 300, comment = "执行结果描述")
    result,
    @Column(type = Date.class, nullable = false, comment = "创建时间")
    createTime
}
