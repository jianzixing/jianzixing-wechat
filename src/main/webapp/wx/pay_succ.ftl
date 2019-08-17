<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>支付成功</title>
    <meta name="keywords" content="">
    <meta name="description" content="">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no, viewport-fit=cover">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/common.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/pay_succ.css">
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/velocity.min.js"></script>
</head>
<body>
<div class="wx_wrap" id="wrapBody">
    <div class="m_header" style="">
        <div class="m_header_bar">
            <div class="m_header_bar_back back_close" data-url="${JZXUrl(Request)}/wx/mine/index.jhtml"></div>
            <#if type?? && type=='1'>
                <div class="m_header_bar_title">充值成功</div>
            <#else>
                <div class="m_header_bar_title">支付成功</div>
            </#if>
        </div>
        <div id="m_common_header_key" class="jzx-header-icon-new-shortcut">
            <span></span>
        </div>
    </div>

    <#include "include/header_shortcut.ftl"/>

    <div class="pay_succ">
        <img src="images/pay_succ.png"/>
        <#if type?? && type=='1'>
            <span class="ps_title">充值成功</span>
            <span class="ps_dsc">您的充值请求处理完成，请可以在<span class="tips">我的</span>中查看余额</span>
        <#else>
            <span class="ps_title">支付成功</span>
            <span class="ps_dsc">订单已支付成功，详细信息请在<span class="tips">全部订单</span>中查看</span>
        </#if>
    </div>
    <#if type?? && type=='1'>
        <a href="${JZXUrl(Request)}/wx/index.jhtml" class="pay_succ_btn">返回主页</a>
        <a href="${JZXUrl(Request)}/wx/mine/index.jhtml" class="pay_succ_btn white">返回"我的"</a>
    <#else>
        <a href="${JZXUrl(Request)}/wx/index.jhtml" class="pay_succ_btn">返回主页</a>
        <a href="${JZXUrl(Request)}/wx/mine/myorder.jhtml?type=2" class="pay_succ_btn white">我的订单</a>
        <a href="${JZXUrl(Request)}/wx/mine/index.jhtml" class="pay_succ_btn white">返回"我的"</a>
    </#if>
</div>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>
</body>
</html>
