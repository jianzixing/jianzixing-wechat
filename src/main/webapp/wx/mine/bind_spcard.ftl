<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>绑定购物卡</title>
    <meta name="keywords" content="">
    <meta name="description" content="">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no, viewport-fit=cover">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/common.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/mine/bind_spcart.css">
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/velocity.min.js"></script>
</head>
<body>
<div class="wx_wrap" id="wrapBody"
     url="<#if r??>${r}<#else>${JZXUrl(Request)}/wx/mine/spcard.jhtml</#if>">
    <div class="m_header" style="">
        <div class="m_header_bar">
            <div class="m_header_bar_back"></div>
            <div class="m_header_bar_title">绑定购物卡</div>
        </div>
        <div id="m_common_header_key" class="jzx-header-icon-new-shortcut">
            <span></span>
        </div>
    </div>

    <#include "../include/header_shortcut.ftl"/>

    <div class="bind_ecard">
        <div class="ecard_notice" style="display: none">非法渠道获得的购物卡可能无法使用</div>
        <div class="ecard_form">
            <p id="ecard_form_title" class="ecard_form_title" style="text-align: left;">
                请输入购物卡密码
                <i class="icon_info"></i>
            </p>
            <div class="ecard_form_input">
                <input type="text" placeholder="卡密码不区分大小写" maxlength="19" class="input_text">
            </div>
            <div class="mod_btns">
                <a href="javascript:;" class="mod_btn bg_2 J_ping bind_new_btn">立即绑定</a>
            </div>
            <div style="display: none;">
                <p class="ecard_form_title" style="margin: 30px 0px;">
                    <span class="line"></span>
                    &nbsp;或 &nbsp;<span class="line"></span>
                </p>
            </div>
        </div>
    </div>


    <div class="mod_alert_mask"></div>
    <div class="mod_alert mod_alert_info w100 fixed">
        <h3 class="title">绑定提示</h3>
        <div class="inner">
            <dl>
                <dt>1、礼品卡密码</dt>
                <dd>密码是16位大写英文字母及数字的组合</dd>
            </dl>
            <dl>
                <dt>2、密码获取方式</dt>
                <dd>实体卡密码：刮开卡片背面图层即可查看</dd>
                <dd>电子卡密码：订单详情页内查看</dd>
            </dl>
            <dl>
                <dt>3、请在英文输入法下输入卡密码</dt>
            </dl>
        </div>
        <div class="btns">
            <a href="javascript:;" class="btn btn_1">我知道了</a>
        </div>
    </div>
</div>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>
<script type="text/javascript">
    $(function () {
        $('#ecard_form_title .icon_info').click(function () {
            $('.mod_alert').addClass('show');
            $('.mod_alert_mask').addClass('show');
        });

        $('.mod_alert .btn_1').click(function () {
            $('.mod_alert').removeClass('show');
            $('.mod_alert_mask').removeClass('show');
        });

        $('.mod_btns .bind_new_btn').click(function () {
            var pwd = $('.ecard_form_input input').val();
            if (!pwd) {
                CommentUtils.alert('', '请输入卡密码');
                return;
            }
            $.ajax({
                url: '${JZXUrl(Request)}/wx/mine/bind_spacard_submit.jhtml',
                data: {pwd: pwd},
                success: function (data) {
                    if (data == 'ok') {
                        var url = $('#wrapBody').attr('url');
                        window.location.href = url;
                    }
                    if (data == 'card_bind_was') {
                        CommentUtils.alert('', '改卡已被绑定');
                    }
                    if (data == 'card_empty') {
                        CommentUtils.alert('', '该卡不存在');
                    }
                    if (data == 'not_login') {
                        CommentUtils.alert('', '您已退出登录');
                    }
                },
                error: function (xhr) {
                    CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
                }
            })
        });
    });
</script>
</body>
</html>
