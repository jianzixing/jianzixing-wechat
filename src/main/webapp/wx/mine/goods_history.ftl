<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>浏览记录</title>
    <meta name="keywords" content="">
    <meta name="description" content="">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no, viewport-fit=cover">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/common.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/mine/goods_mark.css">
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/velocity.min.js"></script>
</head>
<body>
<div class="wx_wrap" id="wrapBody"
     page-type="history"
     img-url="${JZXFile(Request,'')}"
     load-url="${JZXUrl(Request)}/wx/mine/history_mark_list.jhtml"
     cancel-url="${JZXUrl(Request)}/wx/mine/history_mark_cancel.jhtml"
     clear-url="${JZXUrl(Request)}/wx/mine/history_mark_clear.jhtml"
     goods-url="${JZXUrl(Request)}/wx/goods_detail.jhtml?id=">
    <div class="m_header" style="">
        <div class="m_header_bar">
            <div class="m_header_bar_back"></div>
            <div class="m_header_bar_title">浏览记录</div>
        </div>
        <div id="m_common_header_key" class="jzx-header-icon-new-shortcut">
            <span></span>
        </div>
    </div>

    <#include "../include/header_shortcut.ftl"/>

    <p class="fav_count" style="">
        <span class="fav_shoucang_guanzhu">共</span>
        <em id="fav_total_num">${collectCount}</em>件商品
        <span class="fav_edit" id="edit_btn">编辑</span>
        <span class="fav_clear" id="clear_btn">清空</span>
    </p>

    <div class="fav_items" id="favlist"></div>

    <div id="loadingPanel" class="wx_loading2" style="font-size: 10px;/* display: none; */">
        <i class="wx_loading_icon"></i>
    </div>

    <div class="fav_fixbar" style="position: fixed;display: none">
        <span class="fav_select" id="selectAllBtn">全选</span>
        <a href="javascript:void(0);" class="btn" id="multiCancle">删除历史</a>
    </div>
</div>

<div class="mod_alert_v2_mask"></div>
<div class="mod_alert_v2 fixed">
    <i class="icon"></i>
    <p>确认要删除历史么？</p>
    <p class="btns">
        <a href="javascript:;" id="ui_btn_confirm" class="btn btn_1">删除历史</a>
        <a href="javascript:;" id="ui_btn_cancel" class="btn btn_1">再想想</a>
    </p>
</div>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/mine/goods_mark.js"></script>
</body>
</html>
