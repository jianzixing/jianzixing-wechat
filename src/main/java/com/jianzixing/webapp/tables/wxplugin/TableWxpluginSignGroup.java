package com.jianzixing.webapp.tables.wxplugin;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.platform.Text;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

/**
 * 一个活动组，每个组都是一个独立的签到活动
 */
@Table
public enum TableWxpluginSignGroup {
    @Column(pk = true, strategy = AutoIncrementStrategy.class, comment = "主键")
    id,
    @Column(type = byte.class, defaultValue = "1", comment = "第三方平台(公众号)账号类型 参考 WeChatOpenType类")
    openType,
    @Column(type = int.class, nullable = false, comment = "账号数据库ID")
    accountId,
    @Column(length = 100, nullable = false, comment = "组名称")
    name,
    @Column(length = 100, unique = true, nullable = false, comment = "组编码,作为url的一部分")
    code,
    @Column(type = Date.class, nullable = false, comment = "签到开始时间")
    startTime,
    @Column(type = Date.class, nullable = false, comment = "签到结束时间")
    finishTime,
    @Column(type = Text.class, comment = "组描述")
    detail,
    @Column(type = byte.class, defaultValue = "0", comment = "是否启用 0否 1是")
    enable,
    @Column(type = Date.class, comment = "创建时间")
    createTime
}
