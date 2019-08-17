package com.jianzixing.webapp.tables.file;

import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableFiles {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = int.class, defaultValue = "0", comment = "文件分组ID")
    gid,
    @Column(type = byte.class, defaultValue = "0", comment = "path是否是相对目录 0全路径 1WEB可访问路径 2上传配置的相对路径")
    isRelativePath,
    @Column(length = 1000, comment = "文件存放目录磁盘地址")
    path,
    @Column(length = 1000, comment = "文件网络访问地址,不一定存在")
    uri,
    @Column(length = 100, comment = "文件上传后磁盘真实名称")
    fileName,
    @Column(length = 100, comment = "文件上传前的原始名称")
    originalName,
    @Column(length = 20, comment = "文件后缀名称")
    type,
    @Column(type = long.class, defaultValue = "0", comment = "文件大小")
    size,
    @Column(length = 32, comment = "文件MD5值")
    md5,
    @Column(length = 64, comment = "文件的SHA1值")
    sha1,
    @Column(type = byte.class, defaultValue = "1", comment = "是否可下载 0不可下载 1可下载")
    isDwn,
    @Column(type = Date.class, comment = "创建时间")
    createTime
}
