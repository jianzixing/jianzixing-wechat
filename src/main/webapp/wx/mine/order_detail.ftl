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
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/mine/order_detail.css">
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/velocity.min.js"></script>
</head>
<body>
<div class="wx_wrap" id="wrapBody">
    <div class="m_header" style="">
        <div class="m_header_bar">
            <div class="m_header_bar_back"></div>
            <div class="m_header_bar_title">订单详情</div>
        </div>
        <div id="m_common_header_key" class="jzx-header-icon-new-shortcut">
            <span></span>
        </div>
    </div>

    <#include "../include/header_shortcut.ftl"/>

    <div class="wrapper">
        <#if order??>
            <div>
                <div class="order_state">
                    <#if order['status'] == 0>
                        <div class="icon state_time"></div>
                        <div class="state_cont">
                            <div class="state_txt">
                                <span class="desc">未支付</span>
                            </div>
                            <div class="state_tip">${statusText}</div>
                        </div>
                    <#elseif order['status']==10 || order['status'] == 20>
                        <div class="icon state_time"></div>
                        <div class="state_cont">
                            <div class="state_txt">
                                <span class="desc">已支付</span>
                            </div>
                            <div class="state_tip">${statusText}</div>
                        </div>
                    <#elseif order['status']==30 || order['status'] == 40>
                        <div class="icon state_time"></div>
                        <div class="state_cont">
                            <div class="state_txt">
                                <span class="desc">已发货</span>
                            </div>
                            <div class="state_tip">${statusText}</div>
                        </div>
                    <#elseif order['status']==41>
                        <div class="icon state_error"></div>
                        <div class="state_cont">
                            <div class="state_txt">
                                <span class="desc">已拒收</span>
                            </div>
                            <div class="state_tip">${statusText}</div>
                        </div>
                    <#elseif order['status']==50 || order['status'] == 60>
                        <div class="icon state_suc"></div>
                        <div class="state_cont">
                            <div class="state_txt">
                                <span class="desc">已签收</span>
                            </div>
                            <div class="state_tip">${statusText}</div>
                        </div>
                    <#elseif order['status']==90>
                        <div class="icon state_error"></div>
                        <div class="state_cont">
                            <div class="state_txt">
                                <span class="desc">已取消</span>
                            </div>
                            <div class="state_tip">${statusText}</div>
                        </div>
                    <#else>
                        <div class="icon state_error"></div>
                        <div class="state_cont">
                            <div class="state_txt">
                                <span class="desc">未知状态</span>
                            </div>
                            <div class="state_tip">您的订单处于未知状态!</div>
                        </div>
                    </#if>
                </div>

                <div class="order_wuliu">
                    <div data-href="" class="wuliu_cont type_arrow" style="display: none">
                        <span class="icon wl_icon"></span>
                        <div class="cont_text">
                            <div class="wl_text">您的订单已由本人签收。</div>
                            <span class="wl_tip">${JZXDateFormat(order['createTime'],'yyyy-MM-d HH:mm:ss')}</span>
                        </div>
                    </div>
                    <#if order['TableOrderAddress']??>
                        <#assign address = order['TableOrderAddress']/>
                        <div class="wuliu_cont"><span class="icon wl_pos_icon"></span>
                            <div class="cont_text">
                                <div class="wl_text">${address['realName']} ${address['phoneNumber']}</div>
                                <span class="wl_tip">${address['province']}${address['city']}${address['county']}${address['address']}</span>
                            </div>
                        </div>
                    </#if>
                </div>

                <div id="shops">
                    <#if order['TableOrderGoods']??>
                        <#assign goods = order['TableOrderGoods']/>
                        <div class="order_goods">
                            <div data-id="shop" data-url="" class="order_shopBar shop_jd">
                                <span class="shop_icon" style="display: none"></span>
                                <div class="shop_name">
                                    <span class="shop_title">订单商品</span>
                                </div>
                                <span class="shop_desc">共计 ${goods?size} 件商品</span>
                            </div>
                            <div class="order_good">
                                <#list order['TableOrderGoods'] as goods>
                                    <#assign price = JZXPrice(goods['payPrice'])/>
                                    <div data-url="${JZXUrl(Request)}/wx/goods_detail.jhtml?id=${goods['goodsId']}"
                                         class="good_item">
                                        <div class="good_cover">
                                            <div class="wqvue-image good_cover_img">
                                                <img src="${JZXFile(Request,goods['fileName'])}"
                                                     style="min-width: 1px;">
                                            </div>
                                        </div>
                                        <div class="good_cont">
                                            <div class="good_info">
                                                <div class="good_name oneline">${goods['goodsName']}</div>
                                                <#if goods['goodsSkuName']??>
                                                    <span class="sku_coll">${goods['goodsSkuName']}</span>
                                                </#if>
                                            </div>
                                            <div class="info_price">
                                                <span class="price">¥
                                                    <span class="price_yuan">${price.getYuan()}</span>.${price.getFen()}
                                                </span>
                                                <span class="count">x${goods['amount']}</span>
                                            </div>
                                            <div class="good_btns">
                                                <#if order['status']==50>
                                                    <div class="good_btn"
                                                         data-url="${JZXUrl(Request)}/wx/mine/after_sale_start.jhtml?og=${goods['id']}">
                                                        申请售后
                                                    </div>
                                                    <#if goods['isCommented']?? && goods['isCommented'] ==0>
                                                        <div class="good_btn red"
                                                             data-url="${JZXUrl(Request)}/wx/mine/goods_comment.jhtml?og=${goods['id']}">
                                                            去评价
                                                        </div>
                                                    </#if>
                                                </#if>
                                            </div>
                                        </div>
                                    </div>
                                </#list>
                            </div>
                            <div class="my_links" style="display: none">
                                <div data-url="" class="my_links_item">
                                    <div class="link_dd">联系客服</div>
                                </div>
                            </div>
                        </div>
                    </#if>
                </div>

                <#if order['TableOrderInvoice']??>
                    <#assign invoice = order['TableOrderInvoice']/>
                    <div id="invoice">
                        <div class="order_invoice_new">
                            <div class="invoice_detail">
                                <div class="detail">
                                    <div class="status">
                                        发票类型：
                                        <span class="black">
                                            <#if invoice['type']==0>
                                                不开发票
                                            <#elseif invoice['type'] == 1>
                                                普通发票
                                            <#elseif invoice['type'] == 2>
                                                增值税发票
                                            <#else>
                                                电子发票
                                            </#if>
                                        </span>
                                    </div>
                                </div>
                            </div>
                            <#if invoice['type']!=0 && invoice['type']!=2>
                                <div class="invoice_detail">
                                    <div class="detail">
                                        <div class="status">
                                            发票抬头：
                                            <span class="black">
                                                <#if invoice['headType']==1>个人<#else>公司</#if>
                                            </span>
                                        </div>
                                    </div>
                                </div>
                                <div class="invoice_detail">
                                    <div class="detail">
                                        <div class="status">
                                            发票内容：
                                            <span class="black">
                                                <#if invoice['cntType']==0>商品明细<#else>商品类别</#if>
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </#if>
                        </div>
                    </div>
                </#if>

                <div class="order_summary">
                    <div class="inner_line"><span class="title">订单编号：</span>
                        <div class="content">${order['number']}</div>
                    </div>
                    <div class="inner_line"><span class="title">下单时间：</span>
                        <div class="content">${JZXDateFormat(order['createTime'],'yyyy-MM-d HH:mm:ss')}</div>
                    </div>
                    <#if order['paymentName']??>
                        <div class="inner_line"><span class="title">支付方式：</span>
                            <div class="content">${order['paymentName']}</div>
                        </div>
                    </#if>
                    <#if order['lgsCompanyName']?? && order['trackingNumber']??>
                        <div class="inner_line"><span class="title">配送方式：</span>
                            <div class="content">${order['lgsCompanyName']} ${order['trackingNumber']}</div>
                        </div>
                    </#if>
                </div>

                <#assign orderGoodsPrice = JZXPrice(order['totalGoodsPrice'])/>
                <#assign orderDiscountPrice = JZXPrice(order['discountPrice'])/>
                <#assign orderPayPrice = JZXPrice(order['payPrice'])/>
                <#assign orderFreightPrice = JZXPrice(order['freightPrice'])/>
                <div class="order_total">
                    <div class="total_item">商品金额<span class="price">¥ ${orderGoodsPrice}</span></div>
                    <div>
                        <div class="total_item">优惠 <span class="price">- ¥ ${orderDiscountPrice}</span></div>
                        <div class="total_item">运费 <span class="price">+ ¥ ${orderFreightPrice}</span></div>
                    </div>
                    <div class="total">实付金额：<span class="price_tip">¥ ${orderPayPrice}</span></div>
                </div>

                <div class="order_btn fixed_btn">
                    <#--如果有SURE确认状态则，order['status']==10也可以取消订单-->
                    <#if order['status']==0>
                        <div id="btn_pay_order" data-id="${order['id']}" class="oh_btn bg_red">支付订单</div>
                        <div id="btn_cancel_order" data-id="${order['id']}" class="oh_btn bg_write">取消订单</div>
                    </#if>
                    <#if order['status']==10>
                        <div id="btn_cancel_order" data-id="${order['id']}" class="oh_btn bg_red">取消订单</div>
                    </#if>
                    <#if order['status']==30 || order['status']==40>
                        <div id="btn_confirm_order" data-id="${order['id']}" class="oh_btn bg_red">确认收货</div>
                    </#if>
                    <#if order['status']==90 || order['status']==60 || order['status']==50>
                        <div id="btn_del_order" data-id="${order['id']}" class="oh_btn bg_write">删除订单</div>
                        <div id="btn_buy_again" data-id="${order['id']}"
                             data-url="${JZXUrl(Request)}/wx/mine/myorder_rebuy.jhtml?oid=${order['id']}"
                             class="oh_btn bg_red">再次购买
                        </div>
                    </#if>
                </div>
            </div>
        <#else>
            <div class="args_null">
                <span>订单不存在或已删除</span>
            </div>
        </#if>
    </div>
</div>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>
<script type="text/javascript">
    $(function () {
        $('#btn_del_order').click(function () {
            var oid = $(this).attr('data-id');
            CommentUtils.confirm('确定删除订单？', function () {
                CommentUtils.wait('正在删除...');
                $.ajax({
                    url: '${JZXUrl(Request)}/wx/mine/myorder_del.jhtml',
                    dataType: 'json',
                    data: {oid: oid},
                    success: function (data) {
                        CommentUtils.closeWait();
                        if (data['success'] == 1) {
                            window.location.reload();
                        } else {
                            if (data['code'] == 'not_login') {
                                CommentUtils.alert('', '您已经退出登录');
                            }
                        }
                    },
                    error: function (xhr) {
                        CommentUtils.closeWait();
                        CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
                    }
                })
            })
        });

        $('#btn_cancel_order').click(function () {
            var oid = $(this).attr('data-id');
            CommentUtils.confirm('确定取消订单？', function () {
                CommentUtils.wait('正在取消...');
                $.ajax({
                    url: '${JZXUrl(Request)}/wx/mine/myorder_cancel.jhtml',
                    dataType: 'json',
                    data: {oid: oid},
                    success: function (data) {
                        CommentUtils.closeWait();
                        if (data['success'] == 1) {
                            window.location.reload();
                        } else {
                            if (data['code'] == 'not_login') {
                                CommentUtils.alert('', '您已经退出登录');
                            }
                        }
                    },
                    error: function (xhr) {
                        CommentUtils.closeWait();
                        CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
                    }
                })
            })
        });

        $('#btn_confirm_order').click(function () {
            var oid = $(this).attr('data-id');
            CommentUtils.confirm('确认收货？', function () {
                CommentUtils.wait('正在设置...');
                $.ajax({
                    url: '${JZXUrl(Request)}/wx/mine/myorder_confirm.jhtml',
                    dataType: 'json',
                    data: {oid: oid},
                    success: function (data) {
                        CommentUtils.closeWait();
                        if (data['success'] == 1) {
                            window.location.reload();
                        } else {
                            if (data['code'] == 'not_login') {
                                CommentUtils.alert('', '您已经退出登录');
                            }
                        }
                    },
                    error: function (xhr) {
                        CommentUtils.closeWait();
                        CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
                    }
                })
            })
        });

        <#if order??>
        $('#btn_pay_order').click(function () {
            window.location.href = '${JZXUrl(Request)}/wx/payment.jhtml?oid=${order['number']}';
        });
        </#if>

        $('#btn_buy_again').click(function () {
            var oid = $(this).attr('data-id');
            CommentUtils.wait('正在加载...');
            $.ajax({
                url: '${JZXUrl(Request)}/wx/mine/myorder_buy_again.jhtml',
                dataType: 'json',
                data: {oid: oid},
                success: function (data) {
                    CommentUtils.closeWait();
                    if (data['success'] == 1) {
                        window.location.href = '${JZXUrl(Request)}/wx/order.jhtml?c=' + data['data']
                    } else {
                        if (data['code'] == 'not_login') {
                            CommentUtils.alert('', '您已经退出登录');
                        }
                    }
                },
                error: function (xhr) {
                    CommentUtils.closeWait();
                    CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
                }
            });
        })
    })
</script>
</body>
</html>
