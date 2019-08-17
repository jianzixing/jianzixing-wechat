<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>我的</title>
    <meta name="keywords" content="">
    <meta name="description" content="">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no, viewport-fit=cover">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/common.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/mine/index.css">
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/velocity.min.js"></script>
</head>
<body>
<div class="wx_wrap" id="wrapBody">
    <div class="m_header" style="">
        <div class="m_header_bar">
            <div class="m_header_bar_back"></div>
            <div class="m_header_bar_title">我的</div>
        </div>
        <div id="m_common_header_key" class="jzx-header-icon-new-shortcut">
            <span></span>
        </div>
    </div>

    <#include "../include/header_shortcut.ftl"/>

    <div id="app" class="content">
        <div class="header_con" id="myHeader">
            <div class="my_header shadow">
                <div class="user_info">
                    <div class="avatar_wrap">
                        <div class="avatar">
                            <#if user['avatar']??>
                                <div class="image_info"
                                     style="background: url(${user['avatar']}) 0px 0px / 100% 100% no-repeat;"></div>
                            <#else>
                                <div class="image_info"
                                     style="background: url(${JZXUrl(Request)}/wx/images/icons/header.png) 0px 0px / 100% 100% no-repeat;"></div>
                            </#if>
                        </div>
                        <div class="hor-tips" style="z-index: -1;">
                            <div class="login line1">等级 ${userLevelAmount}</div>
                            <div class="login line1 goto_arrow"
                                 data-url="${JZXUrl(Request)}/wx/mine/integral.jhtml">
                                当前积分 ${userIntegralAmount}
                            </div>
                        </div>
                    </div>
                    <div class="personal_wrap">
                        <div class="name line1">
                            <span class="line1">${JZXUrlDecode(user['nickName']!'[无昵称]')}</span>
                            <span class="my_header_v4_name_edit"></span>
                            <#if level??>
                                <div class="vip">
                                    <img src="${JZXFile(Request,level['logo'])}"/>
                                    ${level['name']}
                                </div>
                            </#if>
                        </div>
                        <span class="pin line1">用户名：${user['userName']}</span>
                    </div>
                    <div class="account_wrap" data-url="${JZXUrl(Request)}/wx/mine/setting.jhtml">
                        <div class="account_wrap_content" style="display: none;">
                            <span class="account-icon"
                                  style="background: url(${JZXUrl(Request)}/wx/images/icons/setting.png) 0px 0px / 100% 100% no-repeat;"></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <!--订单工具-->
        <div class="xlist_group my_cell" id="myOrder">
            <div class="rel_container">
                <div class="top_line_box">
                    <div data-page="1" class="my_order_entrance type_unpay"
                         data-url="${JZXUrl(Request)}/wx/mine/myorder.jhtml?type=1">
                        <div class="entrance_text">待付款</div>
                    </div>
                    <div data-page="2" class="my_order_entrance type_unrecieve"
                         data-url="${JZXUrl(Request)}/wx/mine/myorder.jhtml?type=2">
                        <div class="entrance_text">待收货</div>
                    </div>
                    <div class="my_order_entrance type_consult"
                         data-url="${JZXUrl(Request)}/wx/mine/after_sale.jhtml">
                        <div class="entrance_text">退换／售后</div>
                    </div>
                    <div class="my_order_entrance type_orders"
                         data-url="${JZXUrl(Request)}/wx/mine/myorder.jhtml">
                        <div class="entrance_text">
                            <div class="">全部订单</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!--虚拟资产-->
        <div class="xlist_group my_cell" id="myAsset" style="display: ">
            <div class="rel_container">
                <div class="top_line_box">
                    <div class="assets_item" data-url="${JZXUrl(Request)}/wx/mine/coupon.jhtml">
                        <div class="assets_item_val">
                            <div>
                                <span class="assets_val">${couponCount}</span>
                                <span class="assets_unit">张</span>
                            </div>
                        </div>
                        <span class="assets_item_key">优惠券</span>
                    </div>
                    <div class="assets_item" data-url="${JZXUrl(Request)}/wx/mine/balance.jhtml">
                        <div class="assets_item_val">
                            <div>
                                <span class="assets_val">${balanceAmount}</span>
                                <span class="assets_unit">元</span>
                            </div>
                        </div>
                        <span class="assets_item_key">账户余额</span>
                    </div>
                    <div class="assets_item" data-url="${JZXUrl(Request)}/wx/mine/integral.jhtml">
                        <div class="assets_item_val">
                            <div>
                                <span class="assets_val">${userIntegralAmount}</span>
                                <span class="assets_unit">个</span>
                            </div>
                        </div>
                        <span class="assets_item_key">积分</span>
                    </div>
                    <div class="assets_item" data-url="${JZXUrl(Request)}/wx/mine/spcard.jhtml">
                        <div class="assets_item_val">
                            <div>
                                <span class="assets_val">${spcardCount}</span>
                                <span class="assets_unit">张</span>
                            </div>
                        </div>
                        <span class="assets_item_key">购物卡</span>
                    </div>
                </div>
            </div>
        </div>

        <!--收藏记录-->
        <div class="xlist_group my_cell" id="myFav">
            <div class="rel_container">
                <div class="top_line_box">
                    <div class="assets_item" data-url="${JZXUrl(Request)}/wx/mine/goods_mark.jhtml">
                        <div class="assets_item_val take_place">
                            <span class="store_val">${userCollectCount}</span>
                        </div>
                        <span class="assets_item_key">商品收藏</span>
                    </div>
                    <div class="assets_item" data-url="${JZXUrl(Request)}/wx/mine/address_list.jhtml">
                        <div class="assets_item_val take_place">
                            <span class="store_val <#--show_red_point-->">${addressCount}</span>
                        </div>
                        <span class="assets_item_key">收货地址</span>
                    </div>
                    <div class="assets_item" data-url="${JZXUrl(Request)}/wx/mine/goods_history.jhtml">
                        <div class="assets_item_val take_place">
                            <span class="store_val">${historyCount}</span>
                        </div>
                        <span class="assets_item_key">浏览记录</span>
                    </div>
                </div>
            </div>
        </div>

        <!--服务列表-->
        <div class="activity-wrap" id="myActivity" style="display: none">
            <div class="tools_container" style="height: 130px;">
                <div data-idx="0" class="tools_item" style="display: none">
                    <div class="tools_icon"
                         style="/*background: url() 0px 0px / 100% 100% no-repeat;*/"></div>
                    <span class="tools_item_key">服务列表</span>
                </div>
            </div>
        </div>

        <#if goods??>
            <div id="recommend">
                <div id="cnxhTitle" class="recomm_mod_title">
                    <span class="recomm_mod_title_text">为您推荐</span>
                </div>
                <div class="mod_recommend_v2">
                    <ul class="list">
                        <#list goods as g>
                            <#assign price = JZXPrice(g['price'])/>
                            <li class="" data-url="${JZXUrl(Request)}/wx/goods_detail.jhtml?id=${g['id']}">
                                <div class="cover">
                                    <img src="${JZXFile(Request,g['fileName'])}">
                                    <div class="mod_good_decoration"></div>
                                </div>
                                <div class="info">
                                    <div class="name">${g['name']}</div>
                                    <div class="flex_wrap">
                                        <div class="price_info">
                                            <div class="price">¥
                                                <em>${price}</em></div>
                                        </div>
                                    </div>
                                </div>
                            </li>
                        </#list>
                    </ul>
                </div>
            </div>
        </#if>
    </div>
    <div id="mCommonFooter" class="m-common-footer">
        <div class="common-copyright">
            Copyright © 2018-2019 简子行科技版权所有
        </div>
    </div>
</div>

<div class="back2top" style="display: none"></div>

<#import "../include/tab.ftl" as tab/>
<@tab.tab index=5/>

<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>
<script type="text/javascript">
    $(function () {
        $(window).scroll(function () {
            var scrollTop = $(window).scrollTop();
            if (scrollTop > 50) {
                $('.back2top').show();
            } else {
                $('.back2top').hide();
            }
        });

        $('.back2top').click(function () {
            $(window).scrollTop(0);
        });
    })
</script>
</body>
</html>
