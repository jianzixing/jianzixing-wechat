package com.jianzixing.webapp.tables.user;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

/**
 * @author yangankang
 */
@Table
public enum TableUser {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(extMinLength = 6, length = 30, unique = true, nullable = false)
    userName,
    @Column(length = 32, nullable = false)
    password,
    @Column(length = 256, unique = true, extRegExp = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$")
    email,
    @Column(length = 14, unique = true, extRegExp = "^1[123456789]\\d{9}$")
    phone,
    @Column(length = 32, comment = "用户登录后的登录token")
    token,

    @Column(type = byte.class, defaultValue = "1", comment = "第三方平台(公众号)账号类型 参考 WeChatOpenType类")
    openType,
    @Column(type = int.class, comment = "账号数据库ID")
    accountId,
    @Column(length = 110, unique = true, comment = "微信登录用户id,分两部分前部分是应用类型和应用ID后部分是openid")
    openid,
    @Column(length = 200, comment = "用户唯一标识,只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段")
    unionid,

    @Column(type = byte.class, defaultValue = "0", comment = "是否验证邮箱 0否 1是")
    validEmail,
    @Column(type = byte.class, defaultValue = "0", comment = "是否验证手机 0否 1是")
    validPhone,
    @Column(type = long.class, defaultValue = "0", comment = "用户积分等级数值,比如 0-100青铜级别")
    levelAmount,
    @Column(type = int.class, comment = "等级ID")
    levelId,
    @Column(length = 500, comment = "昵称")
    nick,
    @Column(type = short.class, defaultValue = "0", comment = "年龄")
    age,
    @Column(type = byte.class, defaultValue = "0", comment = "性别 0未知  1男 2女")
    gender,
    @Column(length = 500, comment = "头像")
    avatar,
    @Column(length = 16, nullable = false, comment = "密码盐值")
    signature,
    @Column(type = Date.class, comment = "生日")
    birthday,
    @Column(type = short.class, defaultValue = "0", comment = "是否结婚 0:结婚 1:没结婚")
    isMarried,
    @Column(type = short.class, defaultValue = "1")
    enable,
    @Column(type = Date.class, comment = "最后一次登录时间")
    lastLoginTime,
    @Column(type = Date.class, extCanUpdate = false)
    registerTime
}
