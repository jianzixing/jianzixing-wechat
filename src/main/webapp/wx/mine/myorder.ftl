<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>我的订单</title>
    <meta name="keywords" content="">
    <meta name="description" content="">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no, viewport-fit=cover">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/common.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/mine/myorder.css">
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/velocity.min.js"></script>
</head>
<body>
<div class="wx_wrap" id="wrapBody"
     load-img="${JZXFile(Request,'')}"
     load-url="${JZXUrl(Request)}/wx/mine/myorder_list.jhtml"
     del-url="${JZXUrl(Request)}/wx/mine/myorder_del.jhtml"
     detail-url="${JZXUrl(Request)}/wx/mine/order_detail.jhtml"
     order-type="${type}"
     payment-url="${JZXUrl(Request)}/wx/payment.jhtml?oid="
     comment-url="${JZXUrl(Request)}/wx/mine/goods_comment.jhtml?og="
     rebuy-url="${JZXUrl(Request)}/wx/mine/myorder_rebuy.jhtml?oid=">

    <div class="m_header" style="">
        <div class="m_header_bar">
            <div class="m_header_bar_back"></div>
            <div class="m_header_bar_title">我的订单</div>
        </div>
        <div id="m_common_header_key" class="jzx-header-icon-new-shortcut">
            <span></span>
        </div>
    </div>

    <#include "../include/header_shortcut.ftl"/>

    <div>
        <div class="hd_bar_wrap">
            <div class="my_nav_mask"></div>
            <div class="hd_bar">
                <div action="#" class="hd_search_frm">
                    <input id="" name="" type="text" maxlength="140"
                           placeholder="商品名称/商品编号/订单号" class="hd_search_txt">
                    <div class="hd_search_clear hide"></div>
                </div>
                <div class="hd_me">
                    <div class="hd_search_btn">取消</div>
                    <div class="hd_search_btn_red"
                         search-url="${JZXUrl(Request)}/wx/mine/search_order.jhtml">
                        搜索
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div>
        <div class="my_nav">
            <div class="my_nav_inner">
                <div class="my_nav_list">
                    <div data-tabid="tab_all_order_list" class="my_nav_list_item cur">
                        <div class="nav_item_link">所有订单</div>
                    </div>
                    <div data-tabid="tab_readypay_order_list" class="my_nav_list_item">
                        <div class="nav_item_link">待付款</div>
                    </div>
                    <div data-tabid="tab_readytake_order_list" class="my_nav_list_item">
                        <div class="nav_item_link">待收货</div>
                    </div>
                    <div data-tabid="tab_finish_order_list" class="my_nav_list_item">
                        <div class="nav_item_link">已完成</div>
                    </div>
                </div>
                <i class="my_nav_arrow"></i>
                <div class="my_nav_board">
                    <div class="my_nav_cate">
                        <div class="my_nav_cate_item">
                            <div data-url="//newcz.m.jd.com/newcz/list.action?sid=1"
                                 class="my_nav_cate_item_inner text_center">
                                <div class="my_nav_cate_item_text">已取消</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="my_order_wrap">
        <div id="tabWapper" class="my_order_inner">
            <div id="tab_all_order_list" class="my_order">
                <div id="order-list">
                    <div class="empty_order_flag loaded hide">你暂时没有订单</div>
                    <div class="loading_order_flag loaded hide">
                        <div class="wqvue-image img_loading">
                            <img src="${JZXUrl(Request)}/wx/images/loading.gif">
                        </div>
                    </div>
                </div>
            </div>
            <div id="tab_readypay_order_list" class="my_order" style="display: none">
                <div id="order-list">
                    <div class="empty_order_flag loaded hide">你暂时没有待付款订单</div>
                    <div class="loading_order_flag loaded hide">
                        <div class="wqvue-image img_loading">
                            <img src="${JZXUrl(Request)}/wx/images/loading.gif">
                        </div>
                    </div>
                </div>
            </div>
            <div id="tab_readytake_order_list" class="my_order" style="display: none">
                <div id="order-list">
                    <div class="empty_order_flag loaded hide">你暂时没有待收货订单</div>
                    <div class="loading_order_flag loaded hide">
                        <div class="wqvue-image img_loading">
                            <img src="${JZXUrl(Request)}/wx/images/loading.gif">
                        </div>
                    </div>
                </div>
            </div>
            <div id="tab_finish_order_list" class="my_order" style="display: none">
                <div id="order-list">
                    <div class="empty_order_flag loaded hide">你暂时没有已完成订单</div>
                    <div class="loading_order_flag loaded hide">
                        <div class="wqvue-image img_loading">
                            <img src="${JZXUrl(Request)}/wx/images/loading.gif">
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!--S 所有弹框-->
<div id="wq-dialog" style="display: none">
    <div class="mod_alert_mask show"></div>
    <div class="mod_alert fixed show">
        <div class="icon"></div>
        <div class="regular">您确认要删除该订单？</div>
        <div class="medium"></div>
        <div class="small">删除订单后您将无法再查看到该订单信息。</div>
        <div></div>
        <div class="btns">
            <div data-btn-index="0" class="btn btn_2">取消</div>
            <div data-btn-index="1" class="btn btn_1">删除</div>
        </div>
    </div>
</div>
<!--E 所有弹框-->

<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/mine/myorder.js"></script>
</body>
</html>
