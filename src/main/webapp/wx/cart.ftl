<!DOCTYPE html>
<html lang="zh-CN">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>购物车</title>
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no, viewport-fit=cover">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/common.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/cart.css">

</head>

<body>

<div class="wx_wrap" id="wxWrapCont">
    <div class="m_header" style="">
        <div class="m_header_bar">
            <div class="m_header_bar_back"></div>
            <div class="m_header_bar_title">购物车</div>
        </div>
        <div id="m_common_header_key" class="jzx-header-icon-new-shortcut">
            <span></span>
        </div>
    </div>

    <#include "include/header_shortcut.ftl"/>

    <div class="shopcart_empty_wrap" style="<#if carts??>display: none;</#if>" id="emptyDiv">
        <img src="${JZXUrl(Request)}/wx/images/cart/empty_cart.png" class="empty_icon">
        <p class="empty_txt">购物车空空如也，快去选购商品吧~</p>
    </div>
    <!-- 购物车主页面容器 -->
    <#if carts??>
    <div id="c_jdshopcart_showList" class="c_showJs">
        <div id="listContent">
            <div id="huangtiao" primary="0" style="display:none;"></div>
            <!-- 购物车商品列表容器 #list -->
            <div id="cmdtylist" attr-tag="cmdtylist">
                <div class="section">
                    <#if coupons??>
                        <div class="head_wrap">
                            <div class="head selected">
                                <a class="btn" href="javascript:void(0);" attr-tag="shopCouponEnter">优惠券</a>
                            </div>
                        </div>
                    </#if>
                    <#list carts as cartItem>
                        <#assign goodsDetailUrl='${JZXUrl(Request)}/wx/goods_detail.jhtml?id=${cartItem.gid}&skuId=${cartItem.skuId}'>
                        <div class="item <#if cartItem.discount??>item_connect</#if>" gid="${cartItem.gid}"
                             skuId="${cartItem.skuId}" id="item-${cartItem.gid}-${cartItem.skuId}">
                            <#if cartItem.discount?? && cartItem.discount?size gt 0>
                                <#assign discount=cartItem.discount[0]>
                                <a class="head  head_act"> <span class="tag">${discount.implName}</span>
                                    <p class="title">${discount.name}</p>
                                    <span class="a_head_right_text"
                                          data-url="${JZXUrl(Request)}/wx/discount_goods.jhtml?did=${discount.id}">
                                    去凑单
                                </span>
                                </a>
                            </#if>
                            <#if cartItem.TableGoods.hasSku==1>
                                <#assign goods= cartItem.TableGoodsSku>
                            <#else>
                                <#assign goods= cartItem.TableGoods>
                            </#if>

                            <div class="goods goods_last <#if cartItem.isChecked==1>selected</#if>"
                                 cartId="${cartItem.id}">
                                <i class="icon_select"></i>
                                <img class="image" src="${JZXFile(cartItem.TableGoods.fileName)}"
                                     alt="${cartItem.TableGoods.name}" data-url="${goodsDetailUrl}">
                                <#if goods.amount<=0 >
                                    <p class="image_tag" lowestbuy="0">无货</p>
                                </#if>
                                <div class="content" <#if goods.amount<=0> data-url="${goodsDetailUrl}"</#if>>
                                    <div class="name">
                                        <span class="proNameJs">${cartItem.TableGoods.name}</span>
                                    </div>
                                    <#if cartItem.TableGoods.hasSku==1 && cartItem.TableGoodsProperty??>
                                        <p class="sku" attr-tag="skuChange" isdist="" attr-weight="480">
                                            <#list 0..cartItem.TableGoodsProperty!?size-1 as i>
                                                <#if i==0>
                                                    ${cartItem.TableGoodsProperty[i].valueName}
                                                <#else>
                                                    ,${cartItem.TableGoodsProperty[i].valueName}
                                                </#if>
                                            </#list>
                                        </p>
                                    </#if>
                                    <div class="goods_line">
                                        <p class="price" attr-tag="">
                                            <span class="priceJs">¥ <em class="int">${goods.price}</em>.00</span>
                                        </p>
                                        <div class="num_and_more">
                                            <div class="num_wrap" skuId="${cartItem.skuId}" gid="${cartItem.gid}">
                                                <span class="minus <#if cartItem.amount==1>disabled</#if>"></span>
                                                <div class="input_wrap">
                                                    <input class="num" type="tel" value="${cartItem.amount}"
                                                           max="<#if 200< goods.amount>${goods.amount}<#else>200</#if>"
                                                           prevalue="1" lowestbuy="0">
                                                </div>
                                                <span class="plus "></span>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="goods_sub_line">
                                        <div class="m_action" attr-tag="action">
                    <span class="m_action_item fav-btn" gid="${cartItem.gid}"
                          skuId="${cartItem.skuId}">移入关注</span>
                                            <span class="m_action_item del-btn" gid="${cartItem.gid}"
                                                  skuId="${cartItem.skuId}">删除</span>
                                        </div>
                                    </div>
                                    <div class="goods_sub_line" style="display:none;">
                                        <div class="m_action"><span class="m_action_item" attr-xiangou=""></span></div>
                                    </div>
                                </div>
                                <#if cartItem.discount?? && cartItem.discount?size gt 0>
                                    <div class="shopcart_additional">
                                        <div class="shopcart_additional_inner">
                                            <div class="shopcart_additional_kind">
                                                <span class="shopcart_additional_kind_label">促销</span>
                                                <ul class="shopcart_additional_kind_list">
                                                    <li class="shopcart_additional_kind_item type_select"
                                                        gid="${cartItem.gid}" skuId="${cartItem.skuId}"
                                                        id="promotionChoose">
                                                        <div class="shopcart_additional_kind_item_block">
                                                            <p class="shopcart_additional_kind_item_text"
                                                               id="promotionName">${discount.name}</p>
                                                            <span class="shopcart_additional_kind_item_action">${cartItem.discount!?size+1}个可选</span>
                                                        </div>
                                                    </li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                </#if>
                            </div>
                        </div>
                    </#list>
                </div>
            </div>
            <div class="fixBar" name="checkgroup" id="fixBarBot">
                <i class="icon_select" attr-chktye="1" attr-tag="iconChkEve">全选</i>
                <div class="total" id="totalConfirmDiv">
                    <p>总计：<strong id="totalPrice">¥0.00</strong>
                        <small><span id="totalBackMoney">总额¥0.00 立减¥0.00</span></small>
                    </p>
                    <a href="javascript:;" class="buy buyJs" id="checkoutBtn" attr-tag="confirmEve">去结算
                        <em attr-tag="confirmEve" id="totalNum"></em>
                    </a>
                </div>
                <#--<div class="btns" id="operateDiv">-->
                <#--<a href="javascript:;" class="btn_3" id="deleteBtn" attr-tag="delBtnEve">删除</a>-->
                <#--<a href="javascript:;" class="btn_2" id="addFavor" attr-tag="favBtnEve">移至收藏</a>-->
                <#--</div>-->
            </div>
        </div>
        <#else>
            <div class="shopcart_banner" id="emptyContent" style="display: none;">

            </div>
        </#if>
    </div>
    <!-- 弹层-优惠券容器 -->
    <#if coupons?? && coupons?size gt 0>
        <div class="mod_coupon_voucher3" id="c_jdshopcoupon_show">
            <div class="main">
                <div class="header">优惠券<i class="close"></i></div>
                <div class="body">
                    <div class="coupon_voucher3_hr"><p class="text">可领优惠券</p></div>
                    <#list coupons as coupon>
                        <div class="coupon_voucher3  ">
                            <a href="javascript:;" class="coupon_voucher3_main ">
                                <div class="coupon_voucher3_view"><p class="coupon_voucher3_view_price">
                                        <i>¥</i><strong>${coupon.couponPrice}</strong></p>
                                    <p class="coupon_voucher3_view_des">满${coupon.orderPrice}元可用</p>
                                </div>
                                <div class="coupon_voucher3_info">
                                    <p class="coupon_voucher3_info_text">${coupon.name}
                                    </p>  <span class="coupon_voucher3_info_btn" cid="${coupon.id}">领取</span>
                                    <p class="coupon_voucher3_info_date">${coupon.startTime} - ${coupon.finishTime}</p>
                                </div>
                            </a>
                        </div>
                    </#list>
                </div>
            </div>
        </div>
    </#if>
    <div class="shop_gift" id="nonEmptyExcluCouponDetail"></div>
</div>

<!-- ********************相关的一些容器和弹框********************** -->
<div id="dialogDel" class="mod_alert fixed"><i class="icon"></i>
    <p>是否确认删除此商品？</p>
    <p class="btns">
        <a href="javascript:;" class="btn dialog-back-btn">取消</a>
        <a href="javascript:;" class="btn dialog-del-btn btn_1" attr-tag="ok">删除</a>
    </p>
</div>
<div id="dialogFav" class="mod_alert fixed"><i class="icon"></i>
    <p>是否确认将此商品移至收藏？</p>
    <p class="btns">
        <a href="javascript:;" class="btn dialog-back-btn">取消</a>
        <a href="javascript:;" class="btn dialog-del-btn btn_1" attr-tag="ok">收藏</a>
    </p>
</div>
<div class="mod_alert_mask" id="dialogMask"></div>

<a href="javascript:void(0);" class="WX_backtop" id="goTop" style="bottom: 120px; display: none;">返回顶部</a>
<div class="shopcart_aside" style="display: none;bottom: 172px;z-index: 88;" id="asideImg">
    <a href="javascript:;" class="shopcart_aside_image"><img></a>
</div>
<!-- loading  -->
<div class="wx_loading" id="wxloading" style="z-index: 110; display: none;">
    <div class="wx_loading_inner"><i class="wx_loading_icon"></i> 请求加载中...</div>
</div>

<div class="mod_alert_mask">
    <div class="mod_alert mod_alert_info fixed" id="shopcoupontip" style="display: none;">
        <h3 class="title">优惠券展示说明</h3>
        <div class="inner">
            <dl>
                <dt></dt>
                <dd>1、优惠券领取成功后，将在1分钟内到账，券状态更新有延时，您领取后可稍作等候；</dd>
            </dl>
            <dl>
                <dt></dt>
                <dd>2、可用优惠券是您账户中，适用于在当前店铺下加车的商品的券；</dd>
            </dl>
            <dl>
                <dt></dt>
                <dd>3、优惠券使用后即过期，退货换货不会退还优惠券领取次数。</dd>
            </dl>
        </div>
        <p class="btns"><a href="javascript:void(0);" class="btn btn_1" id="shopcoupontipclose">知道了</a></p>
    </div>
</div>

<!-- 续重运费弹框容器 -->
<div class="mod_alert_mask">
    <div class="mod_alert ship_info fixed" style="display:none;" id="extraYfTip" attr-tag="closeAlert">
        <h3 class="ship_info_title">运费凑单说明</h3>
        <div class="ship_info_inner">

        </div>
        <div class="btns"><a href="javascript:;" class="btn btn_1 closeJs">知道了</a></div>
    </div>
</div>

<!-- 加价购、赠品领取弹框 -->
<div class="additional_buying_btm" style="display:none;">
    <div class="btn red" id="giftConfirm">确定</div>
</div>
<div class="additional_buying" style="display:none;" id="giftView"></div>
<div class="guide_mask"></div>

<div class="shopcart_promotion_main" id="promoteCont">
    <div class="main">
        <div class="header">可选促销<i class="close"></i></div>
        <div class="body">
            <ul class="promotion_list" style="z-index: 99999;">
            </ul>
        </div>
    </div>
</div>

</body>
<script src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/easy.templatejs.min.js"></script>

<script type="text/tmpl" id="promotionList">
    %{ for(var i in discount){ }%
 		<li class="promotion_item %{ if((discountId && discount[i].id==discountId) || (!discountId && discountId!=0 && i==0)){ out('selected')}}%" skuid="{=skuId}" gid="{=gid}" did="{=discount[i].id}">
 		<i class="icon_select"></i>{=discount[i].name}</li>
 	%{ } }%
 	<li class="promotion_item %{ if(!discountId && discountId==0){ out('selected')}}%" skuid="{=skuId}" gid="{=gid}" did="0">
 	    <i class="icon_select"></i>不参加促销
    </li>

</script>
<script>
    //https://github.com/ushelp/EasyTemplateJS/blob/master/doc/readme_zh_CN.md

    var setShopCart = '${JZXUrl(Request)}/wx/modify_shopping_cart.jhtml';
    var removeShopCart = '${JZXUrl(Request)}/wx/remove_shopping_cart.jhtml';
    var getCartPriceUrl = '${JZXUrl(Request)}/wx/get_cart_price.jhtml';
    var moveToCollect = '${JZXUrl(Request)}/wx/move_to_collect_goods.jhtml';
    var userGetCoupon = '${JZXUrl(Request)}/wx/user_get_coupon.jhtml';
    var cartUpdateCheck = '${JZXUrl(Request)}/wx/cart_update_check.jhtml';
    var carts = JSON.parse('${cartsStr}');
    var baseUrl = '${JZXUrl(Request)}';
</script>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/cart.js"></script>
</html>
