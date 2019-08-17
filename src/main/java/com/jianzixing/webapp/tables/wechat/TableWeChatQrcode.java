package com.jianzixing.webapp.tables.wechat;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableWeChatQrcode {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = byte.class, defaultValue = "0", comment = "是否是第三方账号")
    openType,
    @Column(type = int.class, nullable = false, comment = "TableWeChatAccount所属账号如果是第三方平台就是TableWeChatTpAccount表的id")
    accountId,
    @Column(length = 100, nullable = false, comment = "所属APP")
    appid,
    @Column(length = 30, nullable = false, comment = "二维码名称")
    name,
    @Column(length = 64, nullable = false, unique = true, comment = "场景值ID")
    sceneId,
    @Column(length = 20, nullable = false, comment = "二维码类型，QR_SCENE为临时的整型参数值，QR_STR_SCENE为临时的字符串参数值，QR_LIMIT_SCENE为永久的整型参数值，QR_LIMIT_STR_SCENE为永久的字符串参数值")
    actionName,
    @Column(type = int.class, comment = "该二维码有效时间，以秒为单位。 最大不超过2592000（即30天），此字段如果不填，则默认有效期为30秒。")
    expireSeconds,
    @Column(length = 200, comment = "获取的二维码ticket，凭借此ticket可以在有效时间内换取二维码。")
    ticket,
    @Column(length = 200, comment = "二维码图片解析后的地址，开发者可根据该地址自行生成需要的二维码图片,https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=TICKET")
    url,
    @Column(type = int.class, defaultValue = "0", comment = "扫描次数")
    scanCount,
    @Column(type = int.class, defaultValue = "0", comment = "关注次数")
    focusCount,
    @Column(type = int.class, defaultValue = "0", comment = "关注人数")
    userCount,
    @Column(type = int.class, defaultValue = "0", comment = "留存粉丝")
    keepCount,
    @Column(type = Date.class, comment = "创建时间")
    createTime
}
