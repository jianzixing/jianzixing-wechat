package com.jianzixing.webapp.tables.marketing;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.platform.Text;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableMarketSms {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 100, nullable = false, comment = "短信服务名称")
    name,
    @Column(length = 100, nullable = false, comment = "短信服务实现名称")
    implName,
    @Column(length = 200, nullable = false, comment = "短信服务实现类")
    impl,
    @Column(type = byte.class, defaultValue = "0", comment = "短信内容类型 0文本格式 1模板格式")
    type,
    @Column(type = Text.class, comment = "短信服务配置")
    params,
    @Column(type = byte.class, defaultValue = "0", comment = "是否使用 0否 1是")
    enable,
    @Column(type = Date.class, comment = "创建时间")
    createTime,
}
