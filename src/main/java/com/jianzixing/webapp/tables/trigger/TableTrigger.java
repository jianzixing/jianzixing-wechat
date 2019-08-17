package com.jianzixing.webapp.tables.trigger;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.platform.Text;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableTrigger {
    @Column(pk = true, type = long.class, strategy = AutoIncrementStrategy.class, comment = "主键")
    id,
    @Column(length = 100, nullable = false, comment = "触发器名称")
    name,
    @Column(length = 50, nullable = false, comment = "触发器事件")
    event,

    @Column(length = 300, nullable = false, comment = "处理器实现类名 Class.forName")
    processor,
    @Column(length = 200, comment = "如果是邮件站内信则是信件标题")
    title,
    @Column(type = Text.class, comment = "如果是邮件，短信，站内信这里是内容")
    content,
    @Column(type = long.class, comment = "关联其他配置的值ID 比如，邮件服务ID，短信服务ID")
    sid,
    @Column(type = byte.class, nullable = false, comment = "处理器值类型，比如邮件，短信，站内信")
    processorType,
    @Column(type = byte.class, defaultValue = "1", comment = "是否使用规则，0否 1是")
    useRule,

    @Column(type = byte.class, defaultValue = "3", comment = "时间内触发次数 1年 2月 3天 4时 5分 6秒 7永久")
    timeType,
    @Column(type = long.class, defaultValue = "0", comment = "允许触发次数，比如1天内可以触发3次")
    triggerCount,
    @Column(type = long.class, defaultValue = "0", comment = "最大触发次数，限制总触发次数，比如一个用户只能赠送10次积分")
    totalCount,
    @Column(type = byte.class, defaultValue = "0", comment = "是否不限制次数 0否 1是")
    triggerInfinite,
    @Column(type = Date.class, comment = "开始时间")
    startTime,
    @Column(type = Date.class, comment = "结束时间")
    finishTime,
    @Column(type = byte.class, defaultValue = "1", comment = "是否启用 0禁用 1启用")
    enable,
    @Column(type = Date.class, comment = "创建时间")
    createTime
}
