<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>支付订单</title>
    <meta name="keywords" content="">
    <meta name="description" content="">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no, viewport-fit=cover">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/common.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/payment.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/mine/spcart.css">
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/velocity.min.js"></script>
</head>
<body>
<div class="wx_wrap" id="wrapBody"
     oid="${RequestParameters['oid']}"
     cal-url="${JZXUrl(Request)}/wx/cal_payment.jhtml"
     pay-url="${JZXUrl(Request)}/wx/submit_payment.jhtml"
     succ-url="${JZXUrl(Request)}/wx/payment_succ.jhtml"
     payment-type="${paymentType}">

    <div class="m_header" style="">
        <div class="m_header_bar">
            <div class="m_header_bar_back"></div>
            <#if paymentType==1>
                <div class="m_header_bar_title">余额充值</div>
            <#else>
                <div class="m_header_bar_title">支付订单</div>
            </#if>
        </div>
        <div id="m_common_header_key" class="jzx-header-icon-new-shortcut">
            <span></span>
        </div>
    </div>

    <#include "include/header_shortcut.ftl"/>

    <#if order??>
        <#assign orderPrice = JZXPrice(order['payPrice'])/>
        <div class="order-bar-wrap" style="display: block;">
            <div class="order-info">
                <#if order['TableOrderGoods']??>
                    <div class="order-title">
                        <span class="order-num">订单号&nbsp;${order['number']}</span>
                        <#if order['TableOrderGoods']??>
                            <span class="order-cut">共计${order['TableOrderGoods']?size}件商品</span>
                        </#if>
                    </div>
                    <div class="order_goods">
                        <#list order['TableOrderGoods'] as goods>
                            <#assign price = JZXPrice(goods['payPrice'])/>
                            <div class="order_good">
                                <div class="good_item">
                                    <div class="good_cover">
                                        <div class="wqvue-image good_cover_img">
                                            <img src="${JZXFile(Request,goods['fileName'])}" style="min-width: 1px;">
                                        </div>
                                    </div>
                                    <div class="good_cont">
                                        <div class="good_info">
                                            <div class="good_name oneline">
                                                ${goods['goodsName']}
                                            </div>
                                            <#if goods['goodsSkuName']??>
                                                <span class="sku_coll">${goods['goodsSkuName']}</span>
                                            </#if>
                                        </div>
                                        <div class="info_price">
                                        <span class="price">
                                            ¥<span class="price_yuan">${price.getYuan()}</span>.${price.getFen()}
                                        </span>
                                            <span class="count">x${goods['amount']}</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </#list>
                    </div>
                </#if>
            </div>
            <div class="order-bar">
                <span class="pay-tip">需支付:</span>
                <span class="pay-total">
                <strong class="pay-total-strong">¥${orderPrice.getYuan()}<em>.${orderPrice.getFen()}</em></strong></span>
            </div>
        </div>
        <div class="page">
            <#if hasDelay>
                <div id="pay_way_list" class="pay-way">
                    <h2 class="title">支付方式</h2>
                    <ul>
                        <#list delayChannels as delay>
                            <li class="pay-list" pay-id="${delay['id']}">
                                <div class="payicon">
                                    <#if delay['code'] == 'wechat'>
                                        <img src="${JZXUrl(Request)}/wx/images/payment/wx_pay.png"/>
                                    </#if>
                                </div>
                                <div class="paymain">
                                    <span class="pay-title">${delay['name']}</span>
                                    <span class="pay-detail">${delay['detail']}<#--仅安装微信6.0.2 及以上版本客户端使用--></span>
                                </div>
                            </li>
                        </#list>
                    </ul>
                </div>
            </#if>

            <#if hasTimely>
                <div class="pay-way">
                    <h2 class="title">其他支付方式</h2>
                    <ul>
                        <#list timelyChannels as timely>
                            <li class="pay-list ${timely['code']}_pay" pay-id="${timely['id']}">
                                <div class="payicon">
                                    <#if timely['code'] == 'integral'>
                                        <img src="${JZXUrl(Request)}/wx/images/payment/integral.png"/>
                                    </#if>
                                    <#if timely['code'] == 'spcard'>
                                        <img src="${JZXUrl(Request)}/wx/images/payment/card.png"/>
                                    </#if>
                                    <#if timely['code'] == 'balance'>
                                        <img src="${JZXUrl(Request)}/wx/images/payment/balance.png"/>
                                    </#if>
                                </div>
                                <div class="paymain">
                                    <span class="pay-title">${timely['name']}</span>
                                    <span class="pay-detail">
                                        <#if timely['code'] == 'integral'>
                                            <#if integral??>
                                                <span>您拥有${integral['amount']}积分,${rateMoney}积分兑换1元</span>
                                                <span class="discount_price"
                                                      dprice="${integralPayedPrice}">抵扣${integralPayedPrice}元</span>
                                            <#else>
                                                <span>您拥有0积分,${rateMoney}积分兑换1元</span>
                                                <span class="discount_price"
                                                      dprice="${integralPayedPrice}">已抵扣${integralPayedPrice}元</span>
                                            </#if>
                                        </#if>
                                        <#if  timely['code'] == 'spcard'>
                                            <#if spcards??>
                                                <span class="pay_way_desc">勾选后选择您要使用的购物卡</span>
                                                <span class="discount_price"
                                                      dprice="${spcardsPayedPrice}">抵扣${spcardsPayedPrice}元</span>
                                            <#else>
                                                <span class="pay_way_desc">您没有可用的购物卡</span>
                                                <span class="discount_price"
                                                      dprice="${spcardsPayedPrice}">已抵扣${spcardsPayedPrice}元</span>
                                            </#if>
                                        </#if>
                                        <#if  timely['code'] == 'balance'>
                                            <#if balance??>
                                                <span class="pay_way_desc">您的账户余额为${balance['balance']}元</span>
                                                <span class="discount_price"
                                                      dprice="${balancePayedPrice}">抵扣${balancePayedPrice}元</span>
                                            <#else>
                                                <span class="pay_way_desc">您没有余额可供支付</span>
                                                <span class="discount_price"
                                                      dprice="${balancePayedPrice}">已抵扣${balancePayedPrice}元</span>
                                            </#if>
                                        </#if>
                                    </span>
                                </div>
                            </li>
                        </#list>
                    </ul>
                </div>
            </#if>
        </div>
        <a href="javascript:void(0);"
           class="btn pay-next confirm-pay"
           style="display: inline;"
           order-price="${orderPayPrice}">您需支付 ¥${orderPayPrice}</a>
    <#else>
        <div class="args_null">
            <span>当前订单无效或已支付</span>
        </div>
    </#if>
</div>


<div class="wx_wrap spcard_body" id="wrapSpcardBody" style="display: none">
    <div class="m_header" style="">
        <div class="m_header_bar">
            <div class="m_header_bar_back back_close"></div>
            <div class="m_header_bar_title">购物卡</div>
        </div>
    </div>

    <div class="spcard_list" id="spcard_list">
        <div class="buy_ecards_operating">
            <a href="http://localhost:8080/wx/mine/bind_spcard.jhtml?r=${JZXUrl(Request,'$$')}"
               class="buy_ecards_operating_banding">绑卡</a>
        </div>
        <div class="ecard_list">
            <div id="usable_list">
                <#if spcards?? && spcards?size gt 0>
                    <#list spcards as spcard>
                        <#assign price = JZXPrice(spcard['money'])/>
                        <#assign balance = JZXPrice(spcard['balance'])/>
                        <div class="ecard_item" data-id="${spcard['cardNumber']}" balance="${balance}">
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
            <div class="footer">
                <div class="button_bind_card">
                    <p>确定选择</p>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/payment.js"></script>
</body>
</html>
