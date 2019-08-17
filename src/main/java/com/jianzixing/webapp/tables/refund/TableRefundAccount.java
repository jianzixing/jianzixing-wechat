package com.jianzixing.webapp.tables.refund;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

/**
 * 退款银行卡账户,如果需要退款至银行卡
 */
@Table
public enum TableRefundAccount {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = long.class, nullable = false, comment = "退款单ID")
    roid,
    @Column(length = 200, comment = "银行卡账号")
    account,
    @Column(length = 100, comment = "开户行名称")
    realName,
    @Column(length = 200, comment = "开户行地址")
    address,
    @Column(length = 20, comment = "开户行手机号")
    phone,
    @Column(type = Date.class, comment = "创建时间")
    createTime
}
