package com.jianzixing.webapp.tables.wechat;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.platform.Text;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

/**
 * 这里保存回复的素材，这个素材不是mediaId
 * 来源是从mediaId获取的素材信息，或者自己填写的素材地址
 */
@Table
public enum TableWeChatImageText {
    @Column(pk = true, type = long.class, strategy = AutoIncrementStrategy.class, comment = "主键")
    id,
    @Column(type = byte.class, defaultValue = "1", comment = "第三方平台(公众号)账号类型 参考 WeChatOpenType类")
    openType,
    @Column(type = int.class, nullable = false, comment = "账号数据库ID")
    accountId,
    @Column(length = 200, comment = "当前图文上传到微信图文后获取的mediaId,图文mediaId")
    mediaId,
    @Column(type = Date.class, nullable = false, comment = "创建时间")
    createTime
}
