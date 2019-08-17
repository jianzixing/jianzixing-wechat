package com.jianzixing.webapp.tables.order;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

/**
 * @author yangankang
 */
@Table
public enum TableUserAddress {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = long.class, nullable = false, comment = "用户ID")
    userId,
    @Column(length = 50, nullable = false, comment = "用户真实姓名")
    realName,
    @Column(length = 16, comment = "手机号码")
    phoneNumber,
    @Column(length = 16, comment = "电话号码")
    telNumber,
    @Column(length = 200, nullable = false, comment = "国家")
    country,
    @Column(length = 100, nullable = false, comment = "国家代码")
    countryCode,
    @Column(length = 200, nullable = false, comment = "省")
    province,
    @Column(type = int.class, nullable = false, comment = "省代码")
    provinceCode,
    @Column(length = 200, nullable = false, comment = "市")
    city,
    @Column(type = int.class, nullable = false, comment = "市代码")
    cityCode,
    @Column(length = 200, nullable = false, comment = "县")
    county,
    @Column(type = int.class, nullable = false, comment = "县代码")
    countyCode,
    @Column(length = 500, comment = "详细地址")
    address,
    @Column(length = 200, comment = "电子邮件")
    email,
    @Column(length = 20, comment = "邮编号码")
    postcode,
    @Column(type = byte.class, defaultValue = "0", comment = "是否是默认收货地址 0:不是 1:是")
    isDefault,
    @Column(length = 500, comment = "备注信息")
    detail,
    @Column(length = 10, comment = "地址标签 公司 家 学校 自定义")
    label,
    @Column(type = Date.class, comment = "创建日期")
    createTime
}
