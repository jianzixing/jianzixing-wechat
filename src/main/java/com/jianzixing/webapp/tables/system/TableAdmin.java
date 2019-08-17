package com.jianzixing.webapp.tables.system;


import org.mimosaframework.orm.annotation.Column;
import org.mimosaframework.orm.annotation.Table;
import org.mimosaframework.orm.strategy.AutoIncrementStrategy;

import java.util.Date;

@Table
public enum TableAdmin {
    @Column(pk = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(nullable = false, length = 50, comment = "登录的用户名")
    userName,
    @Column(nullable = false, length = 50, comment = "登录密码")
    password,
    @Column(length = 100, comment = "管理员头像")
    logo,
    @Column(type = int.class, comment = "用户的角色")
    roleId,
    @Column(length = 20, comment = "真实姓名")
    realName,
    @Column(type = Integer.class, defaultValue = "0", comment = "性别 0 未知 1男 2女")
    gender,
    @Column(length = 64, comment = "工号")
    jobNumber,
    @Column(length = 16, comment = "昵称")
    nickName,
    @Column(type = Date.class, comment = "生日")
    birthday,
    @Column(length = 16, comment = "办公电话")
    homeNumber,
    @Column(length = 14, comment = "手机电话")
    phoneNumber,
    @Column(length = 64, comment = "邮件地址")
    email,
    @Column(length = 16, comment = "教育程度")
    education,
    @Column(length = 63, comment = "最后一次登录IP")
    lastLoginIp,
    @Column(type = Date.class, comment = "最后一次登录时间")
    lastLoginTime,
    @Column(type = Integer.class, comment = "员工状态 0:试用期 1:正常 2:离职")
    status,
    @Column(type = Integer.class, comment = "部门")
    departmentId,
    @Column(type = Integer.class, comment = "职位")
    positionId,
    @Column(type = Date.class, comment = "最后编辑时间")
    editTime,
    @Column(type = Integer.class, comment = "最后编辑人")
    editUser,
    @Column(type = Date.class, comment = "入职时间")
    checkInTime,
    @Column(comment = "分机号", length = 20)
    extension,
    @Column(type = Integer.class, comment = "是否有效 0:否 1是", defaultValue = "1")
    isValid,
    @Column(comment = "简介")
    description,
}
