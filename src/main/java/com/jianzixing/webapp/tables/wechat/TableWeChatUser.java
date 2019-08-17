package com.jianzixing.webapp.tables.wechat;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableWeChatUser {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = long.class)
    userId,
    @Column(type = byte.class, defaultValue = "1", comment = "第三方平台(公众号)账号类型 参考 WeChatOpenType类")
    openType,
    @Column(type = int.class, nullable = false, comment = "账号数据库ID")
    accountId,
    @Column(length = 100, unique = true, comment = "微信登录用户id")
    openid,
    @Column(length = 300, comment = "昵称")
    nickname,
    @Column(type = byte.class, defaultValue = "0", comment = "性别 0未知  1男 2女")
    sex,
    @Column(length = 20, comment = "用户的语言，简体中文为zh_CN")
    language,
    @Column(length = 200, comment = "城市")
    city,
    @Column(length = 200, comment = "省")
    province,
    @Column(length = 200, comment = "国家")
    country,
    @Column(length = 500, comment = "用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。")
    headimgurl,
    @Column(type = int.class, comment = "用户关注时间，为时间戳。如果用户曾多次关注，则取最后关注时间")
    subscribeTime,
    @Column(length = 200, comment = "用户唯一标识,只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段")
    unionid,
    @Column(length = 500, comment = "公众号运营者对粉丝的备注，公众号运营者可在微信公众平台用户管理界面对粉丝添加备注")
    remark,
    @Column(type = int.class, comment = "用户所在的分组ID（兼容旧的用户分组接口）")
    groupid,
    @Column(length = 30, comment = "返回用户关注的渠道来源，ADD_SCENE_SEARCH 公众号搜索，ADD_SCENE_ACCOUNT_MIGRATION 公众号迁移，ADD_SCENE_PROFILE_CARD 名片分享，ADD_SCENE_QR_CODE 扫描二维码，ADD_SCENEPROFILE LINK 图文页内名称点击，ADD_SCENE_PROFILE_ITEM 图文页右上角菜单，ADD_SCENE_PAID 支付后关注，ADD_SCENE_OTHERS 其他")
    subscribeScene,
    @Column(length = 100, comment = "二维码扫码场景（开发者自定义）")
    qrScene,
    @Column(length = 300, comment = "二维码扫码场景描述（开发者自定义）")
    qrSceneStr,
    @Column(length = 200, comment = "用户accessToken")
    accessToken,
    @Column(type = int.class, comment = "access_token接口调用凭证超时时间，单位（秒)")
    expiresIn,
    @Column(length = 200, comment = "用户刷新access_token")
    refreshToken,
    @Column(type = Date.class, comment = "获取token的时间")
    lastTokenTime,
    @Column(type = Date.class, nullable = false, extCanUpdate = false)
    createTime
}
