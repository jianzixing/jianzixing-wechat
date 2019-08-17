package com.jianzixing.webapp.tables.wechat;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableWeChatMass {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = byte.class, defaultValue = "1", comment = "第三方平台(公众号)账号类型 参考 WeChatOpenType类")
    openType,
    @Column(type = int.class, nullable = false, comment = "账号数据库ID")
    accountId,
    @Column(length = 30, nullable = false, comment = "回复规则名称")
    name,
    @Column(type = short.class, nullable = false, comment = "回复类型 1图文消息 2文字 3图片 4语音 5视频")
    type,
    @Column(type = long.class, defaultValue = "0", comment = "0不使用标签 定时群发给某个标签的人")
    tagid,
    @Column(length = 3000, comment = "media_id素材ID")
    mediaId,
    @Column(length = 2000, comment = "群发内容text类型")
    text,
    @Column(length = 200, comment = "定时群发描述")
    detail,
    @Column(type = byte.class, defaultValue = "1", comment = "是否启用 0禁用 1启用")
    enable,
    @Column(type = Date.class, comment = "定时群发触发时间")
    triggerTime,
    @Column(type = byte.class, defaultValue = "0", comment = "执行状态 0未执行  1已经执行 2执行失败")
    status,
    @Column(length = 1000, comment = "错误信息")
    error,
    @Column(type = Date.class, comment = "创建时间")
    createTime
}
