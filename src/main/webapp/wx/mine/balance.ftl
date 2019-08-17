<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>我的余额</title>
    <meta name="keywords" content="">
    <meta name="description" content="">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no, viewport-fit=cover">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/common.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/mine/integral.css">
    <style type="text/css">
        .balance_btn {
            position: fixed;
            height: 50px;
            line-height: 50px;
            color: #fff;
            font-size: 16px;
            text-align: center;
            background-color: #e4393c;
            width: 100%;
            bottom: 0px;
        }
        .recharge_input {
            background-color: #F0F0F0;
            border: 0px;
            height: 36px;
            width: 90%;
            padding: 2px 8px;
            border-radius: 5px;
            text-align: center;
            font-size: 16px;
        }
    </style>
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/velocity.min.js"></script>
</head>
<body>
<div class="wx_wrap" id="wrapBody"
     load-url="${JZXUrl(Request)}/wx/mine/balance_list.jhtml"
     create-url="${JZXUrl(Request)}/wx/mine/balance_recharge.jhtml"
     payment-url="${JZXUrl(Request)}/wx/mine/balance_recharge_payment.jhtml">
    <div class="m_header" style="">
        <div class="m_header_bar">
            <div data-url="${JZXUrl(Request)}/wx/mine/index.jhtml" class="m_header_bar_back"></div>
            <div class="m_header_bar_title">我的余额</div>
        </div>
        <div id="m_common_header_key" class="jzx-header-icon-new-shortcut">
            <span></span>
        </div>
    </div>

    <#include "../include/header_shortcut.ftl"/>

    <div class="my_info" id="jingdouInfo">
        <p class="total" id="num">
            <span class="tips" style="margin-right: 0px">您的余额为</span>
            <em style="margin-left: 0;margin-right: 0px">￥</em>
            <#if balance??>${balance['balance']!'0'}<#else>0</#if>
            <em>元</em>
        </p>
        <div>
            <div class="my_detail">
                <span>余额账单明细</span>
            </div>
            <div class="detail_list">
                <ul></ul>
            </div>
        </div>
    </div>
    <a href="javascript:;" class="WX_backtop" style="bottom: 60px;/* display: none; */">返回顶部</a>
    <div class="balance_btn">
        <span>充值</span>
    </div>
</div>

<div id="recharge_dialog" class="mod_alert mod_alert_info fixed">
    <span class="close"></span>
    <h3 class="title">请输入充值金额</h3>
    <div class="inner">
        <input class="recharge_input" type="text" placeholder="请输入要充值的金额"/>
    </div>
    <p class="btns">
        <a href="javascript:void(0);" class="btn btn_2">取消</a>
        <a href="javascript:void(0);" class="btn btn_1">确定</a>
    </p>
</div>
<div id="recharge_dialog_mask" class="mod_alert_mask"></div>

<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>
<script type="text/javascript">
    $(function () {
        function renderCouponItem(data) {
            var amount = data['changeBalance'];
            if (amount > 0) amount = "+" + amount;
            var html =
                '<li>' +
                '    <div class="dl_text">' +
                '        <div class="dl_text_title">' + data['detail'] + '</div>' +
                '        <div class="time">变更时间：' + data['createTime'] + '</div>' +
                '    </div>' +
                '    <div class="dl_amount ' + (amount < 0 ? 'sub' : '') + '">' +
                '        <span>' + amount + '</span>' +
                '    </div>' +
                '</li>';

            return html;
        }

        function loadBalances(page) {
            $('#wrapBody').data('loading', true);
            $.ajax({
                url: $('#wrapBody').attr('load-url'),
                dataType: 'json',
                data: {page: page},
                success: function (data) {
                    $('#wrapBody').data('loading', false);
                    if (data['success'] == 1) {
                        var records = data['data'];
                        if (records) {
                            var html = [];
                            for (var i = 0; i < records.length; i++) {
                                html.push(renderCouponItem(records[i]));
                            }
                            $('.detail_list ul').append(html.join(''));
                        } else {
                            $('.detail_list ul').append('<div class="no_more_data">无更多变更记录</div>');
                            $('#wrapBody').data('loading', true);
                        }
                    } else {
                        CommentUtils.alert('', '获取变更记录失败');
                    }
                },
                error: function (xhr) {
                    $('#wrapBody').data('loading', false);
                    CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
                }
            })
        }

        $(document).scroll(function () {
            var scrollTop = $(document).scrollTop();
            var height = $(document).height();
            if (parseInt(scrollTop + $(window).height()) >= parseInt(height)) {
                if (!$('#wrapBody').data('loading')) {
                    var page = $('#wrapBody').data('page');
                    $('#wrapBody').data('page', parseInt(page) + 1);
                    loadBalances($('#wrapBody').data('page'));
                }
            }
        });
        $('#wrapBody').data('page', 1);
        loadBalances($('#wrapBody').data('page'));

        $('.balance_btn').click(function () {
            $('#recharge_dialog').addClass('show');
            $('#recharge_dialog_mask').show();
        });
        $('#recharge_dialog').find('.btns .btn_2').click(function () {
            $('#recharge_dialog').removeClass('show');
            $('#recharge_dialog_mask').hide();
        });
        $('#recharge_dialog').find('.close').click(function () {
            $('#recharge_dialog').removeClass('show');
            $('#recharge_dialog_mask').hide();
        });
        $('#recharge_dialog').find('.btns .btn_1').click(function () {
            var money = $('#recharge_dialog').find('input').val();
            $('#recharge_dialog').removeClass('show');
            $('#recharge_dialog_mask').hide();
            CommentUtils.wait('正在提交充值单...');
            $.ajax({
                url: $('#wrapBody').attr('create-url'),
                dataType: 'json',
                data: {money: money},
                success: function (data) {
                    CommentUtils.closeWait();
                    if (data['success'] == 1) {
                        var number = data['number'];
                        window.location.href = $('#wrapBody').attr('payment-url') + "?oid=" + number;
                    } else {
                        CommentUtils.closeWait();
                        CommentUtils.alert('', '创建充值订单失败');
                    }
                },
                error: function (xhr) {
                    CommentUtils.closeWait();
                    CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
                }
            })
        });
    })
</script>
</body>
</html>
