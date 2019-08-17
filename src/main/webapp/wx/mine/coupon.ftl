<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>我的优惠券</title>
    <meta name="keywords" content="">
    <meta name="description" content="">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no, viewport-fit=cover">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/common.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/mine/coupon.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/item_coupon.css">
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/velocity.min.js"></script>
</head>
<body>
<div class="wx_wrap" id="wrapBody"
     coupon-type="${type}"
     load-url="${JZXUrl(Request)}/wx/mine/coupon_list.jhtml">
    <div class="m_header" style="">
        <div class="m_header_bar">
            <div data-url="${JZXUrl(Request)}/wx/mine/index.jhtml" class="m_header_bar_back"></div>
            <div class="m_header_bar_title">我的优惠券</div>
        </div>
        <div id="m_common_header_key" class="jzx-header-icon-new-shortcut">
            <span></span>
        </div>
    </div>

    <#include "../include/header_shortcut.ftl"/>

    <div>
        <div id="app-wrapper">
            <!--挂数据的div1-->
            <div id="store-element" style="display: none;"></div>
            <!--真正展示的div2-->
            <div id="app" class="wx_wrap">
                <div>
                    <div class="coupon_nav">
                        <a href="javascript:;" class="coupon_nav_item cur" sid="0">
                            <span>待使用 ${notUseCount}</span>
                        </a>
                        <a href="javascript:;" class="coupon_nav_item" sid="1">
                            <span>已使用 ${usedCount}</span>
                        </a>
                        <a href="javascript:;" class="coupon_nav_item" sid="2">
                            <span>已过期 ${expiredCount}</span>
                        </a>
                    </div>
                    <div id="useableList" class="coupon_list">
                        <div class="no_more_data">暂无优惠券</div>
                    </div>
                </div>
                <a href="javascript:;" class="WX_backtop" style="bottom: 60px;/* display: none; */">返回顶部</a>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/mine/coupon.js"></script>
</body>
</html>
