package com.jianzixing.webapp.tables.wechat;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableWeChatReply {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 30, nullable = false, comment = "回复规则名称")
    name,
    @Column(type = byte.class, defaultValue = "1", comment = "第三方平台(公众号)账号类型 参考 WeChatOpenType类")
    openType,
    @Column(type = int.class, nullable = false, comment = "账号数据库ID")
    accountId,
    @Column(type = short.class, nullable = false, comment = "回复条件 1全匹配关键字 2半匹配关键字 5关注公众号 6取消关注 7扫描带参数二维码 8自定义菜单事件")
    type,
    @Column(type = short.class, nullable = false, comment = "回复类型 1图文消息 2文字 3图片 4语音 5视频 6消息模板")
    replyType,
    @Column(length = 100, comment = "比如关键字或者参数二维码的场景号或者自定义菜单名称")
    value,
    @Column(length = 300, comment = "media_id 回复的素材ID 优先回复素材 消息模板ID ，只能拥有一个id")
    mediaId,
    @Column(length = 2000, comment = "回复内容")
    text,
    @Column(length = 200, comment = "回复描述")
    detail,
    @Column(type = byte.class, defaultValue = "1", comment = "是否启用")
    enable,
    @Column(type = int.class, defaultValue = "0", comment = "排序,会影响自动回复内容")
    pos,
    @Column(type = Date.class, comment = "创建时间")
    createTime
}
