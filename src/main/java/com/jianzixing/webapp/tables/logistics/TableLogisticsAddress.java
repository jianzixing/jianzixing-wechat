package com.jianzixing.webapp.tables.logistics;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;

@Table
public enum TableLogisticsAddress {
    @Column(pk = true, type = long.class, nullable = false, comment = "所属表运费模板值")
    templateId,
    @Column(pk = true, type = int.class, nullable = false, comment = "所属表的排序值")
    index,
    @Column(pk = true, type = byte.class, nullable = false, comment = "所属表 1表示TableLogisticsMoneyRule表 2表示TableLogisticsFreeRule表")
    type,
    @Column(length = 200, comment = "国家")
    country,
    @Column(pk = true, length = 100, comment = "国家代码")
    countryCode,
    @Column(length = 200, comment = "省")
    province,
    @Column(pk = true, type = int.class, comment = "省代码")
    provinceCode,
    @Column(length = 200, comment = "市")
    city,
    @Column(pk = true, type = int.class, comment = "市代码")
    cityCode
}
