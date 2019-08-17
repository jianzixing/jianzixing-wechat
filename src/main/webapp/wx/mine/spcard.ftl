<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>我的购物卡</title>
    <meta name="keywords" content="">
    <meta name="description" content="">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no, viewport-fit=cover">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/common.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/mine/spcart.css">
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/velocity.min.js"></script>
</head>
<body>
<div class="wx_wrap" id="wrapBody">
    <div class="m_header" style="">
        <div class="m_header_bar">
            <div data-url="${JZXUrl(Request)}/wx/mine/index.jhtml" class="m_header_bar_back"></div>
            <div class="m_header_bar_title">我的购物卡</div>
        </div>
        <div id="m_common_header_key" class="jzx-header-icon-new-shortcut">
            <span></span>
        </div>
    </div>

    <#include "../include/header_shortcut.ftl"/>

    <div id="wxEcardWrap" class="wx_wrap">
        <div class="available_unavailable_title" style="position: relative;">
            <div class="available">
                <p class="selected">可用(${validSpcardCount!0})</p>
            </div>
            <div class="unavailable">
                <p class="not_selected">不可用(${invalidSpcardCount!0})</p>
            </div>
        </div>
        <div class="card-list-scroller">
            <div class="use_help">
                <div class="use_help_msg">
                    <div class="use_help_msg_icon">?</div>
                    使用帮助
                </div>
            </div>
            <div class="ecard_list">
                <div id="usable_list">
                    <#if validSpcards?? && validSpcards?size gt 0>
                        <#list validSpcards as spcard>
                            <#assign price = JZXPrice(spcard['money'])/>
                            <#assign balance = JZXPrice(spcard['balance'])/>
                            <div class="ecard_item">
                                <div class="ecard">
                                    <div class="ecard_upper">
                                        <div class="ecard_upper_logo"></div>
                                        <div class="ecard_upper_msg">
                                            <p class="ecard_upper_msg_ecard">购物卡</p>
                                            <p class="ecard_upper_msg_num">面值：¥${price}</p>
                                            <p class="ecard_upper_msg_expire">
                                                ${JZXDateFormat(spcard['TableShoppingCard']['finishTime'],'yyyy.MM.dd')}
                                                到期
                                            </p>
                                        </div>
                                    </div>
                                    <div class="ecard_lower">
                                        <div class="ecard_lower_msg">
                                            <div class="ecard_lower_msg_balance">
                                                <p class="ecard_lower_msg_balance_yue">余额：</p>
                                                <p class="ecard_lower_msg_balance_sym">¥</p>
                                                <p class="ecard_lower_msg_balance_num">${balance}</p>
                                            </div>
                                            <p class="ecard_lower_msg_describe">${cardDetail}</p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </#list>
                    <#else>
                        <div class="no_card">
                            <div class="no_card_msg">暂无可用的购物卡</div>
                        </div>
                    </#if>
                    <div class="wx_loading2" style="display: none;">
                        <i class="wx_loading_icon"></i>
                    </div>
                </div>
                <div id="unusable_list" style="display: none;">
                    <#if invalidSpcards?? && invalidSpcards?size gt 0>
                        <#list invalidSpcards as spcard>
                            <#assign price = JZXPrice(spcard['money'])/>
                            <#assign balance = JZXPrice(spcard['balance'])/>
                            <div class="ecard_item">
                                <div class="expired_ecard">
                                    <div class="expired_ecard_upper">
                                        <div class="expired_ecard_upper_logo"></div>
                                        <div class="expired_ecard_upper_msg">
                                            <p class="expired_ecard_upper_msg_ecard">购物卡</p>
                                            <p class="expired_ecard_upper_msg_num">面值：¥${price}</p>
                                            <p class="expired_ecard_upper_msg_expire">
                                                ${JZXDateFormat(spcard['TableShoppingCard']['finishTime'],'yyyy.MM.dd')}
                                                到期
                                            </p>
                                        </div>
                                    </div>
                                    <div class="expired_ecard_lower">
                                        <div class="expired_ecard_lower_msg">
                                            <div class="expired_ecard_lower_msg_balance">
                                                <p class="expired_ecard_lower_msg_balance_yue">余额：</p>
                                                <p class="expired_ecard_lower_msg_balance_sym">¥</p>
                                                <p class="expired_ecard_lower_msg_balance_num">${balance}</p>
                                            </div>
                                            <p class="expired_ecard_lower_msg_describe">${cardDetail}</p>
                                        </div>
                                    </div>
                                    <#if spcard['status'] == 1>
                                        <div class="expired_ecard_corner">已作废</div>
                                    </#if>
                                    <#if spcard['status'] == 2>
                                        <div class="expired_ecard_corner">已使用</div>
                                    </#if>
                                </div>
                            </div>
                        </#list>
                    <#else>
                        <div class="no_card" style="display: none;">
                            <div class="no_card_msg">暂无不可用的购物卡</div>
                        </div>
                    </#if>
                    <div class="wx_loading2" style="display: none;">
                        <i class="wx_loading_icon"></i>
                    </div>
                </div>
                <div class="footer">
                    <div class="button_bind_card"
                         data-url="${JZXUrl(Request)}/wx/mine/bind_spcard.jhtml?r=${JZXUrl(Request,'$$')}">
                        <p>绑定新卡</p>
                    </div>
                </div>
            </div>
        </div>
        <div class="mod_alert_mask" style="display: none;">
            <div class="mod_alert mod_alert_info">
                <span class="close"></span>
                <h3 class="title">购物卡使用说明</h3>
                <div class="scrollbox">
                    <div class="inner">
                        <dl>
                            <dd>购物卡种类分为记名卡及不记名卡，单张记名卡资金限额为RMB5,000 元（含），单张不记名卡资金限额为 RMB1,000 元（含）。</dd>
                            <dd>记名卡不设有效期，不记名卡有效期一般为36个月。</dd>
                        </dl>
                    </div>
                </div>
                <div class="btns">
                    <a class="btn btn_1">确认</a>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>
<script type="text/javascript">
    $(function () {
        $('.card-list-scroller').height($(window).height() - 45 - 45 - 50);
        $(window).on('resize', function () {
            $('.card-list-scroller').height($(window).height() - 45 - 45 - 50);
        })

        $('.use_help_msg_icon').click(function () {
            $('.mod_alert_mask').show();
        });

        $('.mod_alert_mask .btn_1,.close').click(function () {
            $('.mod_alert_mask').hide();
        });

        $('.mod_alert_mask').click(function () {
            $('.mod_alert_mask').hide();
        });

        $('.mod_alert_mask .mod_alert').click(function (e) {
            e.stopPropagation();
        });

        $('.available_unavailable_title .available').click(function () {
            $('#usable_list').show();
            $('#unusable_list').hide();
            $('.available_unavailable_title p').removeClass();
            $('.available_unavailable_title .available p').addClass('selected');
            $('.available_unavailable_title .unavailable p').removeClass('not_selected');
        });
        $('.available_unavailable_title .unavailable').click(function () {
            $('#unusable_list').show();
            $('#usable_list').hide();
            $('.available_unavailable_title p').removeClass();
            $('.available_unavailable_title .available p').removeClass('not_selected');
            $('.available_unavailable_title .unavailable p').addClass('selected');
        });
    })
</script>
</body>
</html>
