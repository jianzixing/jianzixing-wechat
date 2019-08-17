package com.jianzixing.webapp.tables.wechat;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableWeChatOpenAccount {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = int.class, nullable = false, comment = "第三方平台ID")
    tpId,
    @Column(length = 30, nullable = false, comment = "第三方平台Code码")
    tpCode,
    @Column(length = 200, nullable = false, comment = "授权码authorization_code")
    authCode,
    @Column(type = int.class, comment = "Token有效期 7200s")
    authCodeExpires,
    @Column(type = Date.class, comment = "最后一次更新authCode的时间")
    lastAuthTime,

    @Column(length = 100, comment = "授权的微信公众号APPID")
    authorizerAppid,
    @Column(length = 200, comment = "调用凭据")
    authorizerAccessToken,
    @Column(type = int.class, comment = "Token有效期 7200s")
    authorizerExpires,
    @Column(type = Date.class, comment = "最后一次更新authCode的时间")
    lastAuthorizerTime,
    @Column(length = 200, comment = "接口调用凭据刷新令牌")
    authorizerRefreshToken,

    @Column(length = 100, comment = "账号昵称")
    nickName,
    @Column(type = int.class, comment = "授权方公众号类型，0代表订阅号，1代表由历史老帐号升级后的订阅号，2代表服务号")
    serviceTypeInfo,
    @Column(type = int.class, comment = "授权方认证类型，-1代表未认证，0代表微信认证，1代表新浪微博认证，" +
            "2代表腾讯微博认证，3代表已资质认证通过但还未通过名称认证，" +
            "4代表已资质认证通过、还未通过名称认证，但通过了新浪微博认证，" +
            "5代表已资质认证通过、还未通过名称认证，但通过了腾讯微博认证")
    verifyTypeInfo,
    @Column(length = 200, comment = "账号头像")
    headImg,
    @Column(length = 200, comment = "原始ID")
    userName,
    @Column(length = 300, comment = "主体名称一般是公司名称")
    principalName,
    @Column(length = 500, comment = "格式 0:0:1:0:0 按照分号分隔顺序分别是 " +
            "open_store:是否开通微信门店功能 " +
            "open_scan:是否开通微信扫商品功能 " +
            "open_pay:是否开通微信支付功能 " +
            "open_card:是否开通微信卡券功能 " +
            "open_shake:是否开通微信摇一摇功能 " +
            "0代表未开通，1代表已开通")
    businessInfo,
    @Column(length = 200, comment = "二维码地址")
    qrCodeUrl,
    @Column(length = 500, comment = "格式 0:0:0... 按照分号分隔顺序分别是" +
            "ID为1到15时分别代表：" +
            " 1.消息管理权限 2.用户管理权限 3.帐号服务权限 " +
            "4.网页服务权限 5.微信小店权限 6.微信多客服权限 " +
            "7.群发与通知权限 8.微信卡券权限 9.微信扫一扫权限 " +
            "10.微信连WIFI权限 11.素材管理权限 12.微信摇周边权限 " +
            "13.微信门店权限 14.微信支付权限 15.自定义菜单权限 " +
            "请注意： 1）该字段的返回不会考虑公众号是否具备该权限集的权限（因为可能部分具备），" +
            "请根据公众号的帐号类型和认证情况，来判断公众号的接口权限。")
    funcInfo,
    @Column(type = byte.class, defaultValue = "0", comment = "是否是小程序 0公众号 1小程序")
    isMiniProgram,
    @Column(type = byte.class, defaultValue = "0", comment = "是否是默认账号，和公众账号一起只能有一个")
    isDefault,
    @Column(type = Date.class)
    createTime
}
