<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>用户登录</title>
    <meta name="keywords" content="">
    <meta name="description" content="">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no, viewport-fit=cover">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/common.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/login.css">
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/velocity.min.js"></script>
</head>
<body>
<div class="wx_wrap" id="wrapBody" data-redirect="${r}">
    <div class="m_header" style="">
        <div class="m_header_bar">
            <div class="m_header_bar_back"></div>
            <div class="m_header_bar_title">修改密码</div>
        </div>
        <div id="m_common_header_key" class="jzx-header-icon-new-shortcut">
            <span></span>
        </div>
    </div>

    <#include "include/header_shortcut.ftl"/>

    <div class="page">
        <!--短信验证登录-->
        <div id="sms_login" class="login-wrap" style="display: block">

            <div class="input-container">
                <label class="area-box" style="display: none">
                    <span class="area_code" code="86">+86</span>
                    <i class="area_icon"></i>
                </label>
                <input type="tel" id="telphone" class="acc-input telphone sms-txt-input"
                       style="padding-left: 0px;padding-right: 0px"
                       placeholder="请输入手机号">
                <i class="icon icon-clear" style="right: .06rem"></i>
            </div>
            <div class="input-container">
                <input type="password" id="password" class="acc-input telphone sms-txt-input"
                       style="padding-left: 0px;padding-right: 0px"
                       placeholder="请输入密码">
                <i class="icon icon-clear" style="right: .06rem"></i>
            </div>
            <div class="sms-input-box">
                <div class="input-container">
                    <input id="telCode" class="acc-input telCode sms-txt-input" type="tel"
                           oninput="if(value.length>6) value=value.slice(0,6);" placeholder="请输入收到的验证码"
                           style="padding-right: 1.5rem"
                           autocomplete="off">
                    <i class="icon icon-clear" style="right: 1.12rem"></i>
                </div>
                <button class="mesg-code mesg-disable">
                    获取验证码
                </button>
            </div>
        </div>

        <div class="notice">&nbsp;</div>
        <a href="javascript:;" id="loginBtn" class="btn">确 定</a>
    </div>
</div>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/forget_pwd.js"></script>
</body>
</html>
