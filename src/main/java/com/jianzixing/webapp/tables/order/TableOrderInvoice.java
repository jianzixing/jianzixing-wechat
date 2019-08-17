package com.jianzixing.webapp.tables.order;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

/**
 * @author yangankang
 */
@Table
public enum TableOrderInvoice {
    @Column(type = long.class, pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = long.class, nullable = false, comment = "订单ID")
    orderId,
    @Column(type = byte.class, defaultValue = "1", comment = "发票类型 0不开发票 1普通发票 2增值税发票 3电子发票")
    type,
    @Column(type = byte.class, defaultValue = "0", comment = "抬头类型 0个人 1公司")
    headType,
    @Column(length = 100, comment = "发票抬头或者公司名称")
    companyName,
    @Column(length = 100, comment = "纳税人识别号")
    taxNumber,
    @Column(length = 300, comment = "注册地址,如果是电子发票则是Email地址")
    address,
    @Column(length = 20, comment = "注册电话,如果是电子发票则是手机号码")
    phone,
    @Column(length = 100, comment = "开户行")
    bank,
    @Column(length = 100, comment = "开户账号")
    bankAccount,
    @Column(type = byte.class, defaultValue = "0", comment = "发票类型：比如电器电子产品及配件 0:商品明细 1:商品类别")
    cntType,
    @Column(type = byte.class, defaultValue = "0", comment = "是否开票 0否  1是")
    isMake
}
