<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>订单页</title>
    <meta name="keywords" content="">
    <meta name="description" content="">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no, viewport-fit=cover">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/common.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/order.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/item_coupon.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/invoice.css">
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/velocity.min.js"></script>
</head>
<body>
<div class="wx_wrap" id="wrapBody"
     cal-url="${JZXUrl(Request)}/wx/cal_order.jhtml"
     sub-url="${JZXUrl(Request)}/wx/add_order.jhtml"
     pay-url="${JZXUrl(Request)}/wx/payment.jhtml">
    <#if address??>
        <input id="order_params_addrid" type="hidden" value="${address['id']}"/>
    </#if>
    <div class="m_header" style="">
        <div class="m_header_bar">
            <div class="m_header_bar_back"></div>
            <div class="m_header_bar_title">确认订单</div>
        </div>
        <div id="m_common_header_key" class="jzx-header-icon-new-shortcut">
            <span></span>
        </div>
    </div>

    <#include "include/header_shortcut.ftl"/>

    <#if goods?? && goods?size gt 0>
        <input id="order_params_goods" type="hidden" value="${RequestParameters['c']}"/>
        <div id="pagePay">
            <div class="address_defalut_wrap" id="topFixedDiv">
                <div id="addressDefault" class="address_defalut address_border">
                    <#if address??>
                        <ul data-url="${JZXUrl(Request)}/wx/mine/address_list.jhtml?r=${JZXUrl(Request,'$$')}&addrid=${address['id']}">
                            <li><strong>${address['realName']} ${address['phoneNumber']}</strong></li>
                            <li>
                                <#if address['isDefault'] == 1>
                                    <span class="tag tag_red">默认</span>
                                <#else>
                                    <#if address['label']??>
                                        <span class="tag">${address['label']}</span>
                                    </#if>
                                </#if>
                                ${address['province']}${address['city']}${address['county']}${address['address']}
                            </li>
                        </ul>
                    <#else>
                        <div class="add_address_link" style="margin: 18px 0px;">
                            <button class="add_address_btn" id="addAddressBtn"
                                    data-url="${JZXUrl(Request)}/wx/mine/address_add.jhtml?r=${JZXUrl(Request,'$$')}">
                                <i class="address_arrow"></i>
                                请先添加收货地址
                            </button>
                        </div>
                    </#if>
                </div>
            </div>
        </div>
        <div id="venderOrderList">
            <div id="venderList">
                <div class="order_info">
                    <ul>
                        <#list goods as g>
                            <li class="hproduct noclick" gid="${g['id']}"
                                skuId="<#if g['TableGoodsSku']??>${g.TableGoodsSku['id']}<#else>0</#if>"
                                discountId="${g['discountId']!'0'}">

                                <div class="pbox">
                                    <img class="photo" src="${JZXFile(Request,g['fileName'])}">
                                    <div class="fn"><strong>${g['name']}</strong></div>
                                    <#if g['TableGoodsSku']?? && g.TableGoodsSku['propertyNamesString']??>
                                        <p class="sku_coll">${g.TableGoodsSku['propertyNamesString']}</p>
                                    </#if>
                                    <#assign skuPrice = JZXPrice(g['price'])/>
                                    <p class="sku_price">¥<span>${skuPrice.getYuan()}</span>.${skuPrice.getFen()} </p>
                                    <div class="sku">
                                        <div id="modifyNumDom" class="num_wrap">
                                            <span class="minus" num_tag="minus"></span>
                                            <input class="num" num_tag="input" type="tel" value="${g['buyAmount']}">
                                            <span class="plus" num_tag="plus"></span>
                                        </div>
                                        <div class="sku_num" style="display: none">×${g['buyAmount']}</div>
                                    </div>
                                </div>
                                <#if g['TableDiscount']??>
                                    <div class="buy_additional" id="proArea${g['id']}" gid="${g['id']}">
                                        <div class="buy_additional_kind">
                                            <span class="buy_additional_kind_label">促销</span>
                                            <ul class="buy_additional_kind_list">
                                                <li class="buy_additional_kind_item">
                                                    <div class="buy_additional_kind_item_block">
                                                        <p class="buy_additional_kind_item_text">请选择促销活动</p>
                                                        <span class="buy_additional_kind_item_action">${g['TableDiscount']?size}个可选</span>
                                                    </div>
                                                </li>
                                            </ul>
                                        </div>
                                    </div>
                                </#if>
                            </li>
                        </#list>
                    </ul>
                    <ul class="order_info_list">
                        <#if deliveryTypes?? && deliveryTypes?size gt 0>
                            <li id="shipItem" class="shipping">
                                <strong>配送方式</strong>
                                <div class="shipping_content">
                                    <p> 请选择配送方式 </p>
                                </div>
                            </li>
                        </#if>
                        <#if discounts?? && discounts?size gt 0>
                            <li id="discountItem" class="shipping">
                                <strong>促销活动</strong>
                                <div class="shipping_content">
                                    <p class="red"> 请选择促销活动 </p>
                                </div>
                            </li>
                        </#if>
                        <#if coupons?? && coupons?size gt 0>
                            <li id="couponItem" class="shipping">
                                <strong>优惠券</strong>
                                <div class="shipping_content">
                                    <p class="red"> 请选择优惠券 </p>
                                </div>
                            </li>
                        </#if>
                        <li id="invoicesItem" class="shipping">
                            <strong>发票信息</strong>
                            <div class="shipping_content">
                                <p class="">个人&nbsp;商品明细</p>
                            </div>
                        </li>
                    </ul>
                    <div class="buy_msg_v2" style="">
                        <div class="buy_msg_v2_tit">商家留言</div>
                        <input id="buy_msg_input" type="text" placeholder="选填，给商家留言">
                        <div class="tip">0/45</div>
                    </div>
                </div>
            </div>
        </div>

        <div class="buy_section" id="feeDetail">
            <ul class="buy_chart">
                <li class="buy_chart_item">
                    <p class="buy_chart_item_text">商品金额</p>
                    <p class="buy_chart_item_price goods_price">¥&nbsp;0.00</p>
                </li>
                <li class="buy_chart_item">
                    <p class="buy_chart_item_text">运费
                        <small class="buy_chart_item_tip"></small>
                    </p>
                    <p class="buy_chart_item_price freight_price">+&nbsp;¥0.00</p>
                </li>
                <li class="buy_chart_item"><p class="buy_chart_item_text">优惠券</p>
                    <p class="buy_chart_item_price coupon_price">-&nbsp;¥0.00</p>
                </li>
                <li class="buy_chart_item"><p class="buy_chart_item_text">促销活动</p>
                    <p class="buy_chart_item_price discount_price">-&nbsp;¥0.00</p>
                </li>
            </ul>
        </div>

        <div id="payArea" class="pay_area">
            <p class="price">总价：
                <strong id="pageTotalPrice" price="442.80">¥0.00</strong>
            </p>
            <div id="payBtnList" style="">
                <div class="mod_btns" id="btnConfirmOrder">
                    <a href="javascript:void(0);" class="mod_btn bg_2">提交订单</a>
                </div>
            </div>
        </div>
    <#else>
        <div class="args_null">
            <span>购物车被清空或者商品已下架</span>
        </div>
    </#if>
</div>

<!--S以下是全部弹框-->
<!--快递服务-->
<#if goods??>
    <#if deliveryTypes?? && deliveryTypes?size gt 0>
        <div class="detail_dialog_main" style="" id="shipPopup">
            <div class="main">
                <div class="header">
                    配送方式
                    <i class="close"></i>
                </div>
                <div class="body">
                    <div class="detail_row detail_prom">
                        <div class="promo_list">
                            <ul class="man">
                                <#list deliveryTypes as dt>
                                    <li dt="${dt['type']}" class="<#if dt_index==0>selected</#if>">
                                        ${dt['name']} <#if dt['free']==1>(免邮)</#if>
                                    </li>
                                </#list>
                            </ul>
                        </div>
                    </div>
                    <div id="shipConfirm" class="btns_group fixed">
                        <a href="javascript:void(0);" class="btn_item bg_1">确认</a>
                    </div>
                </div>
            </div>
        </div>
    </#if>
    <!--选择促销弹框-->
    <#if discounts?? && discounts?size gt 0>
        <#list discounts as dm>
            <#assign ds = dm['TableDiscount']/>
            <div class="detail_dialog_main" id="promotePopup${dm['id']}" gid="${dm['id']}">
                <div class="main">
                    <div class="header">
                        优惠活动
                        <i class="close"></i>
                    </div>
                    <div class="body">
                        <div class="detail_row detail_prom">
                            <div class="promo_list">
                                <ul class="man">
                                    <#list ds as discount>
                                        <li did="${discount['id']}"
                                            <#if discount_index == 0>class="selected"</#if>
                                            disName="${discount['name']}">
                                            ${discount['name']}
                                            <#--满-->
                                            <#--<span class="price">¥0.00</span>-->
                                            <#--减-->
                                            <#--<span class="price">¥0.00</span>-->
                                        </li>
                                    </#list>
                                    <li did="0">不使用优惠活动</li>
                                </ul>
                            </div>
                        </div>
                        <div id="discountConfirm" class="btns_group fixed">
                            <a href="javascript:void(0);" class="btn_item bg_1">确认</a>
                        </div>
                    </div>
                </div>
            </div>
        </#list>
    </#if>

    <!--优惠券-->
    <#if coupons?? && coupons?size gt 0>
        <div class="detail_dialog_main" id="couponPopup">
            <div class="main">
                <div class="header">
                    优惠券
                    <i class="close"></i>
                </div>
                <div class="body" style="background-color: #f7f7f7">
                    <div class="detail_row detail_prom">
                        <div class="order_coupons">
                            <div class="order_coupons_auto type_disabled" style="">
                                <span class="order_coupons_auto_right color_red couponRule">使用规则</span>
                            </div>
                            <ul class="order_coupons_list type_radio" id="couponlistPage" style="">
                                <#list coupons as cu>
                                    <#if cu['TableCoupon']??>
                                        <#assign coupon = cu['TableCoupon']/>
                                        <#assign couponPrice = JZXPrice(coupon['couponPrice'])/>
                                        <#assign orderPrice = JZXPrice(coupon['orderPrice'])/>
                                        <li class="order_coupons_item selected" cid="${cu['id']}">
                                            <div class="coupon_voucher2 <#if coupon['enable']==0>type_disabled</#if>">
                                            <span class="coupon_voucher2_tag color_red" style="display:none;">
                                                <i>优惠券</i>
                                            </span>
                                                <a href="javascript:;" class="coupon_voucher2_main">
                                                    <div class="coupon_voucher2_view">
                                                        <p class="coupon_voucher2_view_price">
                                                            <i>¥</i>
                                                            <strong>${couponPrice.getNumber()}</strong>
                                                        </p>
                                                        <p class="coupon_voucher2_view_des">
                                                            满${orderPrice.getNumber()}元可用
                                                        </p>
                                                    </div>
                                                    <div class="coupon_voucher2_info">
                                                        <p class="coupon_voucher2_info_text">
                                                            <i class="coupon_voucher2_info_type">优惠券</i>${coupon['name']}
                                                        </p>
                                                        <p class="coupon_voucher2_info_date">
                                                            ${JZXDateFormat(coupon['startTime'],'yyyy-MM-dd')}
                                                            -
                                                            ${JZXDateFormat(coupon['finishTime'],'yyyy-MM-dd')} </p>
                                                    </div>
                                                </a>
                                                <div class="coupon_voucher2_foot" style="/*display:none;*/">
                                                    <div class="coupon_voucher2_hr"></div>
                                                    <a href="javascript:void(0);" class="coupon_voucher2_description">
                                                        <p class="coupon_voucher2_description_title"> 不可与已勾选券叠加使用 </p>
                                                    </a>
                                                </div>
                                            </div>
                                        </li>
                                    </#if>
                                </#list>
                            </ul>
                        </div>
                    </div>
                    <div id="couponConfirm" class="btns_group fixed">
                        <a href="javascript:void(0);" class="btn_item bg_1">确认</a>
                    </div>
                </div>
            </div>
        </div>
    </#if>

    <!--如果没有收获地址提示添加收货地址-->
    <#if !address??>
        <div class="mod_alert show fixed">
            <i class="icon"></i>
            <p>您当前登录的账号无收货地址，无法下单，请添加收货地址。</p>
            <p class="btns">
                <a href="javascript:;" id="ui_btn_cancel" class="btn ">取消</a>
                <a href="${JZXUrl(Request)}/wx/mine/address_add.jhtml?r=${JZXUrl(Request,'$$')}"
                   id="ui_btn_confirm" class="btn btn_1">
                    添加地址
                </a>
            </p>
        </div>
    </#if>

    <div class="wx_wrap invoice_body" id="wrapInvoiceBody">
        <div class="m_header" style="">
            <div class="m_header_bar">
                <div class="m_header_bar_back back_close"></div>
                <div class="m_header_bar_title">发票</div>
            </div>
        </div>

        <div class="invoice_list" id="invoices" style="">
            <!--顶部提示-->
            <a href="javascript:void(0);" class="mod_blockTips pure_text" style="display: none" id="fapiaoTip">
                <p></p>
            </a>
            <dl class="on">
                <dt>
                    <strong>发票类型</strong>
                    <em id="fp_type_bar">普通发票</em>
                </dt>
                <dd id="invoinceTypes">
                    <ul>
                        <li class="type_addtips" id="fp_selectNone" data-type="0">
                            不开发票
                        </li>
                        <li class="type_addtips selected" id="fp_selectGeneral" data-type="1">
                            普通发票
                            <div class="tips">发票内容会显示商品名称和价格，如有疑问可以咨询客服</div>
                        </li>
                    </ul>
                </dd>
            </dl>
            <div id="fp_general" style="">
                <dl class="on">
                    <dt>
                        <strong>发票抬头</strong>
                        <em id="fpTypeBar">个人</em>
                    </dt>
                    <dd>
                        <ul id="fpType">
                            <li head-type="0" class="selected">
                                个人
                                <input style="display: none;" id="personName" maxlength="50"
                                       class="input_text" type="text" placeholder="">
                            </li>
                            <li head-type="1" class="">
                                发票抬头
                                <input id="compName" maxlength="50" class="input_text" type="text"
                                       placeholder="公司全称" style="display: none;">
                            </li>
                            <li class="type_noselected" id="taxLi" style="display: none;">
                                纳税人识别号
                                <input class="input_text"
                                       type="text"
                                       id="taxerId"
                                       placeholder="请填写纳税人识别号">
                            </li>
                        </ul>
                    </dd>
                </dl>
                <dl class="on" id="fpContent">
                    <dt>
                        <strong>发票内容</strong>
                        <em>商品明细</em>
                    </dt>
                    <dd>
                        <div class="order_additional_tips type_invoice" style="z-index: 0;display: none">
                            <p class="order_additional_tips_text"> 根据国家相关规定，发票的开票内容需与购买的商品一致，如有疑问请联系客服。 </p>
                            <i class="order_additional_tips_close"></i>
                        </div>
                    </dd>
                    <dd>
                        <ul>
                            <li class="type_addtips selected" cnt-type="0" content_name="商品明细">
                                商品明细
                                <div class="tips">发票内容显示详细商品名称及价格信息。</div>
                            </li>
                            <li class="type_addtips" cnt-type="1" content_name="商品类别">
                                商品类别
                                <div class="tips">发票内容将填写您购买的商品类别</div>
                            </li>
                        </ul>
                    </dd>
                </dl>
            </div>
            <div style="height: 46px;"></div>
        </div>

        <div id="bottomConfirmBar" class="mod_btns fixed" style="">
            <a href="javascript:void(0);" class="mod_btn bg_1" id="btnBottomConfirmBar">确认</a>
        </div>
    </div>

    <!--等待加载-->
    <div class="wx_loading" id="wxloading" style="display: none;">
        <div class="wx_loading_inner">
            <i class="wx_loading_icon"></i>
            <span>请求加载中...</span>
        </div>
    </div>

    <!--E以上是全部弹框-->
    <#if !address??>
        <div class="mod_alert_mask show"></div>
    <#else >
        <div class="mod_alert_mask"></div>
    </#if>
</#if>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/order.js"></script>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/invoice.js"></script>
</body>
</html>
