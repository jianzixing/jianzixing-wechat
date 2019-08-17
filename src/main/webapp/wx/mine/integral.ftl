<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>我的积分</title>
    <meta name="keywords" content="">
    <meta name="description" content="">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no, viewport-fit=cover">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/common.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/mine/integral.css">
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/velocity.min.js"></script>
</head>
<body>
<div class="wx_wrap" id="wrapBody"
     load-url="${JZXUrl(Request)}/wx/mine/integral_list.jhtml">
    <div class="m_header" style="">
        <div class="m_header_bar">
            <div data-url="${JZXUrl(Request)}/wx/mine/index.jhtml" class="m_header_bar_back"></div>
            <div class="m_header_bar_title">我的积分</div>
        </div>
        <div id="m_common_header_key" class="jzx-header-icon-new-shortcut">
            <span></span>
        </div>
    </div>

    <#include "../include/header_shortcut.ftl"/>

    <div class="my_info" id="jingdouInfo">
        <p class="total" id="num">
            <span class="tips">您的积分总计</span>
            <#if integral??>${integral['amount']}<#else>0</#if>
            <em>个</em>
        </p>
        <div>
            <div class="my_detail">
                <span>积分收支明细</span>
            </div>
            <div class="detail_list">
                <ul></ul>
            </div>
        </div>
    </div>
    <a href="javascript:;" class="WX_backtop" style="bottom: 60px;/* display: none; */">返回顶部</a>
</div>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>
<script type="text/javascript">
    $(function () {
        function renderCouponItem(data) {
            var amount = data['changeAmount'];
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

        function loadCoupons(page) {
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
                            $('.detail_list ul').append('<div class="no_more_data">无更多消费记录</div>');
                            $('#wrapBody').data('loading', true);
                        }
                    } else {
                        CommentUtils.alert('', '获取消费记录失败');
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
                    loadCoupons($('#wrapBody').data('page'));
                }
            }
        });
        $('#wrapBody').data('page', 1);
        loadCoupons($('#wrapBody').data('page'));
    })
</script>
</body>
</html>
