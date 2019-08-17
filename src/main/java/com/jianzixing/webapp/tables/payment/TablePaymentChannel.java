package com.jianzixing.webapp.tables.payment;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

@Table
public enum TablePaymentChannel {
    @Column(pk = true, type = long.class, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 100, nullable = false, comment = "支付实现名称")
    name,
    @Column(length = 300, nullable = false, comment = "支付实现的包")
    impl,
    @Column(length = 500, comment = "如果需要证书文件则上传")
    certFile,
    @Column(length = 512, comment = "如果上传证书文件则需要填写证书密码")
    certPassword,
    @Column(type = byte.class, defaultValue = "1", comment = "是否可用 0否 1是")
    enable,
    @Column(type = int.class, defaultValue = "0", comment = "排序")
    pos,
    @Column(type = byte.class, defaultValue = "0", comment = "是否已删除 0否 1是")
    isDel,
    @Column(length = 500, comment = "支付实现描述")
    detail
}
