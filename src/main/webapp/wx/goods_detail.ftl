<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>商品详情页</title>
    <meta name="keywords" content="">
    <meta name="description" content="">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no, viewport-fit=cover">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/common.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/goods_detail.css">
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/velocity.min.js"></script>
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/imgviewer.js"></script>
</head>
<body>
<div class="m_header" id="m_header" style="height: 45px;"
     comment-url="${JZXUrl(Request)}/wx/mine/goods_comment_list.jhtml"
     cart-url="${JZXUrl(Request)}/wx/add_shopping_cart.jhtml">
    <div class="header_content">
        <div id="m_common_header" class="m_item_header">
            <header class="jzx-header">
                <div class="jzx-header-new-bar">
                    <div id="m_common_header_goback" class="jzx-header-icon-back">
                        <span></span>
                    </div>
                    <div class="jzx-header-new-title">
                        <div class="detail_anchor_wrap" style="" id="detailAnchor">
                            <nav class="detail_anchor">
                                <a href="javascript:" dtype="item" class="detail_anchor_item cur">
                                    <span>商品</span>
                                </a>
                                <a href="javascript:" dtype="detail" class="detail_anchor_item">
                                    <span>详情</span>
                                </a>
                                <a href="javascript:" dtype="comment" class="detail_anchor_item">
                                    <span>评价</span>
                                </a>
                            </nav>
                        </div>
                    </div>
                    <div id="m_common_header_key" class="jzx-header-icon-new-shortcut">
                        <span></span>
                    </div>
                </div>
            </header>
        </div>
    </div>
</div>

<#include "include/header_shortcut.ftl"/>

<#if goods??>
    <div id="part_main" data-id="${goods['id']}" data-sku="${goods['hasSku']}"
         buy-now-url="${JZXUrl(Request)}/wx/cart_buy_now.jhtml">
        <div class="mod_slider mod_slider_s1 " id="loopImgDiv">
            <div class="inner">
                <ul class="pic_list" id="loopImgUl">
                    <#if goods['TableGoodsImage']??>
                        <#list goods['TableGoodsImage'] as img>
                            <li>
                                <img src="${JZXFile(Request,img['fileName'])}">
                            </li>
                        </#list>
                    <#else>
                    </#if>
                </ul>
                <div class="tittup" id="tittup">
                <span class="inner">
                    <em class="arrow"></em>
                    <span class="txt">滑动查看详情</span>
                </span>
                </div>
            </div>
            <#if goods['TableGoodsImage']??>
                <#assign imgs=goods['TableGoodsImage']/>
                <#if imgs?size gt 1>
                    <div class="bar_wrap">
                        <ul class="bar" id="loopImgBar">
                            <#list goods['TableGoodsImage'] as img>
                                <li no="${img_index+1}" class="<#if img_index==0>cur</#if>"></li>
                            </#list>
                        </ul>
                    </div>
                </#if>
            </#if>
        </div>

        <#assign price = JZXPrice(goods['price'])/>
        <#assign originalPrice = JZXPrice(goods['originalPrice'])/>

        <div class="buy_area" id="buyArea">
            <div class="fn_wrap">
                <h1 class="fn fn_goods_name" id="favWrap">
                    <div class="fn_text_wrap" id="itemName">${goods['name']}</div>
                    <#if isCollect?? && isCollect>
                        <a class="favour heart yes" href="javascript:void(0)" id="fav">关注</a>
                    <#else>
                        <a class="favour heart" href="javascript:void(0)" id="fav">关注</a>
                    </#if>
                </h1>
                <#if goods['subtitle']??>
                    <div class="desc right_shorter" id="itemDesc">${goods['subtitle']}</div>
                </#if>
            </div>
            <div id="priceWrap">
                <div class="price_wrap" id="priceBlock">
                    <span class="price large_size" id="priceSale">¥<em>${price.getYuan()}</em>.${price.getFen()}</span>
                    <span class="old_price"
                          id="oldPriceSale">¥<em>${originalPrice.getYuan()}</em>.${originalPrice.getFen()}</span>
                    <#if commentCount??>
                        <span class="col_right" id="headEval">
                            <span class="sale_num">评价：<b id="evalNo1">${commentCount}</b>条</span>
                        </span>
                    </#if>
                </div>
            </div>

            <div class="de_pm">
                <!--优惠券-->
                <#if coupons?? && coupons?size gt 0>
                    <div class="detail_coupons" id="couponListDiv">
                        <span class="num">共${coupons?size}张</span>
                        <span class="title">领券</span>
                        <#list coupons as coupon>
                            <#if coupon['orderPrice'] gt 0>
                                <span class="coupon">立减${coupon['couponPrice']}元</span>
                            <#else>
                                <span class="coupon">满${coupon['orderPrice']}减${coupon['couponPrice']}</span>
                            </#if>
                        </#list>
                        <span class="coupon bg_red" style="display: none">新用户专享</span>
                    </div>
                </#if>
                <!--促销-->
                <#if discounts?? && discounts?size gt 0>
                    <#assign discount = discounts[0]/>
                    <div class="detail_promote_typeA" style="" id="promoteChoice">
                        <span class="detail_promote_typeA_title">促销</span>
                        <div class="detail_prom">
                            <div class="de_row prom_item">
                                <div class="de_tag" tag="0">
                                    <em class="hl_red_bg">${discount['implName']}</em>
                                </div>
                                <div class="de_span line1">
                                    <span>${discount['name']}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </#if>
                <#if goods['skuAttrs']??>
                    <div class="detail_gap"></div>
                    <div class="sku_window" id="skuWindow">
                        <div class="sku_choose_info">
                            <h3 id="skuChoose1Title">选择</h3>
                            <span id="skuChoose1" class="sku_choose_info_empty">请选择商品规格/颜色分类</span>
                        </div>
                    </div>
                </#if>

                <#if supports??>
                    <ul class="detail_serve" id="serviceArea" ptag="7001.1.24">
                        <#list supports as support>
                            <li class="detail_serve_item">${support['supportName']}</li>
                        </#list>
                    </ul>
                </#if>
                <div class="detail_gap"></div>
            </div>
        </div>

        <!--评论-->
        <div class="detail_extra">
            <div class="detail_row detail_row_cmt" id="summaryEnter">
                <h3 class="tit" id="summaryTitle">评价</h3>
                <#if commentCount??>
                    <i class="icon_promote" id="summaryEnterIco"></i>
                    <p class="good" id="evalRateP">好评 <strong id="evalRate">${commentGoodRate}%</strong></p>
                    <p class="count">共 <span class="num" id="evalNo2">${commentCount}</span> 条</p>
                <#else>
                    <p class="cmt_none_tips" id="evalNone">暂无评价，欢迎您购买之后留下宝贵的评价</p>
                </#if>
            </div>
            <#if comments?? && comments?size gt 0>
                <div class="detail_row detail_cmt" id="mainCmt">
                    <div class="cmt_list_wrap">
                        <ul class="cmt_list" id="evalDet_main">
                            <#list comments as comment>
                                <li>
                                    <div class="cmt_user">
                                        <#if comment['TableUser']['avatar']??>
                                            <img src="${JZXFile(comment['TableUser']['avatar'])}">
                                        <#else>
                                            <img src="${JZXUrl(Request)}/wx/images/empty_header.png">
                                        </#if>
                                        <#if comment['anonymity']==1>
                                            <span class="user">匿名用户</span>
                                        <#else>
                                            <span class="user">${JZXUrlDecode(comment['TableUser']['nick'])!JZXHideString(comment['TableUser']['userName'])}</span>
                                        </#if>
                                        <span class="credit star-${comment['score']}"><span></span></span>
                                        <span class="date">${JZXDateFormat(comment['createTime'],'yyyy-MM-dd')}</span>
                                    </div>
                                    <div class="cmt_cnt">
                                        <#if comment['comment']??>
                                            ${comment['comment']}
                                        <#else>
                                            这个人很懒什么都没留下
                                        </#if>
                                    </div>
                                    <#if comment['TableGoodsCommentImage']??>
                                        <div class="cmt_att">
                                            <#list comment['TableGoodsCommentImage'] as commentImage>
                                                <span class="img">
                                                    <img src="${JZXFile(commentImage['fileName'])}">
                                                </span>
                                            </#list>
                                        </div>
                                    </#if>
                                    <#if comment['goodsSku']??>
                                        <div class="cmt_sku">
                                            <#list comment['goodsSku'] as gs>
                                                <span>${gs['key']}：${gs['value']}</span>
                                            </#list>
                                        </div>
                                    </#if>
                                </li>
                            </#list>
                        </ul>
                        <div id="summaryEnter3" class="cmt_more" style="">
                            <a href="javascript:;" class="cmt_more_lnk">
                                查看全部评价
                                <i class="icon_arrow"></i>
                            </a>
                        </div>
                    </div>
                </div>
            </#if>
            <div class="detail_gap"></div>
        </div>

        <div class="mod_fix_wrap">
            <div class="mod_fix" id="detailTab">
                <div class="mod_tab">
                    <div class="item cur" no="1">商品介绍</div>
                    <div class="item" no="2">规格参数</div>
                </div>
            </div>
        </div>
        <div class="detail_info_wrap" id="detail">
            <div class="detail_list" id="detailCont"
                 style="transform: translate3d(0px, 0px, 0px); transition: all 0.3s ease 0s;">

                <!-- 商品介绍 -->
                <div class="detail_item p_desc" id="detail1" style="position: relative; padding: 0px;">
                    <div class="detail_pc">
                        <#if goods['TableGoodsDescribe']??>
                            ${goods['TableGoodsDescribe']['desc']}
                        </#if>
                    </div>
                </div>
                <!-- 商品参数 -->
                <div class="detail_item p_prop" id="detail2">
                    <div>
                        <div class="mod_tit_line">
                            <h3>商品参数</h3>
                        </div>
                        <div id="detParam">
                            <table cellpadding="0" cellspacing="1" width="100%" border="0" class="Ptable param_table">
                                <tbody>
                                <tr>
                                    <td>商品编号</td>
                                    <td>${goods['serialNumber']}</td>
                                </tr>
                                </tbody>
                                <tbody>
                                <#if goods['weight']?? && goods['weight']!=0 && goods['volume']?? && goods['volume']!=0>
                                    <tr>
                                        <th class="tdTitle" colspan="2">规格</th>
                                    </tr>
                                    <#if goods['weight']?? && goods['weight']!=0>
                                        <tr>
                                            <td class="tdTitle">重量</td>
                                            <td>${goods['weight']}kg</td>
                                        </tr>
                                    </#if>
                                    <#if goods['volume']?? && goods['volume']!=0>
                                        <tr>
                                            <td class="tdTitle">体积</td>
                                            <td>${goods['volume']}m³</td>
                                        </tr>
                                    </#if>
                                <#else>
                                    <tr>
                                        <th class="tdTitle" colspan="2" style="text-align: center">无规格信息</th>
                                    </tr>
                                </#if>
                                <#if goods['attrs']??>
                                    <tr>
                                        <th class="tdTitle" colspan="2">参数</th>
                                    </tr>
                                    <#list goods['attrs'] as attr>
                                        <tr>
                                            <td class="tdTitle">${attr['attrName']}</td>
                                            <td>
                                                <#list attr['values'] as attrValue>
                                                    <#if attrValue_has_next>
                                                        ${attrValue['valueName']},
                                                    <#else>
                                                        ${attrValue['valueName']}
                                                    </#if>
                                                </#list>
                                            </td>
                                        </tr>
                                    </#list>
                                <#else>
                                    <tr>
                                        <th class="tdTitle" colspan="2" style="text-align: center">无参数信息</th>
                                    </tr>
                                </#if>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!--底部购物工具栏-->
    <div class="de_btn_wrap fixed" id="btnTools">
        <div class="de_row de_btn_bar">
            <div class="icon_btn icon_dong" id="shopIM">
                <i class="icon"></i>
                <span class="txt">联系客服</span>
            </div>
            <div class="icon_btn  <#if isCollect?? && isCollect>icon_fav_fit<#else>icon_fav</#if>" id="gotoFav">
                <i class="icon"></i>
                <span class="txt">收藏</span>
            </div>
            <div class="icon_btn icon_cart" id="gotoCart"
                 data-url="${JZXUrl(Request)}/wx/cart.jhtml">
                <span class="add_num" id="popone">+1</span>
                <i class="icon"><span class="num" id="cartNum" style="display: none;">6</span></i>
                <span class="txt">购物车</span>
            </div>
            <div class="de_span btn_group">
                <div class="de_row">
                    <div class="btn btn_orange" id="addCart">
                        <span class="txt">加入购物车</span>
                    </div>
                    <div class="btn btn_buy" id="buyBtn"><span class="txt">立即购买</span></div>
                    <div class="btn btn_blue" id="arrivalNotice" style="display: none;"><span class="txt">到货通知</span>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!--回到顶部-->
    <div class="bottom-to-top" id="goTop"></div>


    <!--S以下是全部弹框-->
    <!--优惠券弹框-->
<#if coupons?? && coupons?size gt 0>
    <div id="popupDom" class="mod_coupon_voucher3"
         url="${JZXUrl(Request)}/wx/get_goods_coupon.jhtml">
        <div class="main">
            <div class="header">优惠券<i class="close"></i></div>
            <div class="body">
                <div class="coupon_list cols_1" style="overflow: visible;">
                    <#list coupons as coupon>
                        <div class="coupon_voucher3 coupon_voucher3_spec_tag">
                            <a href="javascript:;" class="coupon_voucher3_main">
                                <span class="coupon_voucher3_tag" style="display: none">
                                    <i>新用户专享</i>
                                </span>
                                <div class="coupon_voucher3_view">
                                    <p class="coupon_voucher3_view_price">
                                        <i>¥</i>
                                        <strong>${coupon['couponPrice']}</strong>
                                    </p>
                                    <#if coupon['orderPrice'] gt 0>
                                        <p class="coupon_voucher3_view_des">满${coupon['orderPrice']}元可用</p>
                                    <#else>
                                        <p class="coupon_voucher3_view_des">无条件减免</p>
                                    </#if>
                                </div>
                                <div class="coupon_voucher3_info">
                                    <p class="coupon_voucher3_info_text">
                                        <i class="coupon_voucher3_info_type">优惠券</i>
                                        ${coupon['name']}
                                    </p>
                                    <#if coupon['take']?? && coupon['take']==1>
                                        <span class="coupon_voucher3_info_btn disabled" data-id="${coupon['id']}">
                                            已领取
                                        </span>
                                    <#else>
                                        <span class="coupon_voucher3_info_btn"
                                              data-id="${coupon['id']}"
                                              data-count="${coupon['count']}">
                                            领取
                                        </span>
                                    </#if>
                                    <p class="coupon_voucher3_info_date ">
                                        ${JZXDateFormat(coupon['startTime'],'yyyy.MM.dd')}
                                        -
                                        ${JZXDateFormat(coupon['finishTime'],'yyyy.MM.dd')}
                                    </p>
                                </div>
                            </a>
                        </div>
                    </#list>
                </div>
            </div>
            <div class="mod_btns" style="display: block;">
                <div class="mod_btn bg_1">确定</div>
            </div>
        </div>
    </div>
</#if>
<#if discounts?? && discounts?size gt 0>
    <!--促销弹框-->
    <div class="detail_promote_A_main" id="promotePopup">
        <div class="main">
            <div class="header">
                促销
                <i class="close"></i>
            </div>
            <div class="body">
                <div class="detail_row detail_prom">
                    <div id="promoteList2" style="">
                        <#list discounts as discount>
                            <div class="de_row prom_item" id="" data-url="">
                                <div class="de_tag" tag="3">
                                    <em class="hl_red_bg">${discount['implName']}</em>
                                </div>
                                <div class="de_span">
                                    <span>${discount['name']}</span>
                                </div>
                                <div class="de_point">
                                    <i class="icon_point"></i>
                                </div>
                            </div>
                        </#list>
                    </div>
                </div>
            </div>
        </div>
    </div>
</#if>

<#if goods['skuAttrs']??>
    <!--商品规格和保障服务弹框-->
    <div class="detail_sku_v1_main" id="popupBuyArea">
        <#if goods['TableGoodsSku']??>
            <#list goods['TableGoodsSku'] as sku>
                <input type="hidden" value="${sku['attrIds']}"
                       skuId="${sku['id']}"
                       price="${JZXPrice(sku['price'])}"
                       originalPrice="${JZXPrice(sku['originalPrice'])}"/>
            </#list>
        </#if>
        <div class="main" id="popupMain">
            <div class="header">
                <img alt="${goods['name']}" src="${JZXFile(goods['fileName'])}" class="avt" id="popupImg">
                <p class="price" id="priceSale2"><em>选择规格</em></p>
                <span class="old_price" id="oldPriceSale2" style="display: none"></span>
                <p class="prop" id="popupSkuChoose">请选择商品规格/颜色分类</p>
                <i class="close" id="popupClose"></i>
            </div>
            <div class="body">
                <div id="popupSkuArea">
                    <#if goods['skuAttrs']??>
                        <#list goods['skuAttrs'] as attr>
                            <div class="sku_kind">${attr['attrName']}</div>
                            <div class="sku_choose" data-id="${attr['attrId']}">
                                <#list attr['values'] as attrValue>
                                    <span class="item <#if false>active</#if>"
                                          <#if attrValue['img']??>data-img="${JZXFile(Request,attrValue['img'])}"</#if>
                                          data-id="${attrValue['valueId']}">${attrValue['valueName']}</span>
                                </#list>
                            </div>
                        </#list>
                    </#if>
                </div>
                <div class="count_choose" id="popupCount">
                    <div class="num_wrap_v2">
                        <span class="minus disable" id="minus1"><i class="row"></i></span>
                        <div class="text_wrap">
                            <input class="text" type="tel" value="1" id="buyNum1" goodsUnit="${goods['unit']}">
                        </div>
                        <span class="plus" id="plus1"><i class="row"></i><i class="col"></i></span>
                    </div>
                    <p class="count">
                        数量<em class="store" style="display:none;"></em>
                    </p>
                </div>
            </div>
            <div class="btns show" style="display: none">
                <div class="btn yellow" id="addCartPopup">加入购物车</div>
                <div class="btn red" id="buyBtnPopup">立即购买</div>
            </div>
            <div class="btns show">
                <div class="btn red" id="popupConfirm">确认</div>
            </div>
        </div>
    </div>
</#if>

    <!--点击关注成功-->
    <div class="mod_share" id="shareMod">
        <div class="favor_share_content">
            <div class="tip">关注成功，试试分享商品给好友</div>
            <div class="btn">
                确定关闭
            </div>
            <i class="close"></i>
        </div>
    </div>


    <div class="wx_wrap spcard_body" id="part_summary" style="display: none">
        <div class="m_header" style="">
            <div class="m_header_bar">
                <div class="m_header_bar_title">商品评价</div>
                <div class="m_header_bar_close"></div>
            </div>
        </div>

        <!--评论弹出框无限列表-->
        <div id="part_summary_inner" style="">
            <div class="cmt_header">
                <div class="cmt_cur" id="evalCurOption">
                    <div class="checkbox">
                        <input type="checkbox" id="cur">
                        <label class="info_label" for="cur"></label>
                    </div>
                    <label for="cur" class="cmt_cur_label">
                        只看当前商品
                        <span id="evalRateP2">好评度<i id="evalRate2">${commentGoodRate}%</i></span>
                    </label>
                </div>
            </div>
            <div class="detail_row detail_cmt">
                <div class="cmt_list_wrap">
                    <div class="cmt_tag_container">
                        <div class="cmt_tag cmt_tag_new threeLine" id="evalTag2" style="max-height: 95px;">
                            <span no="0" class="selected">全部(${commentCount})</span>
                            <span no="1" class="">最新</span>
                            <span no="2" class="">好评(${goodCommentCount})</span>
                            <span no="3" class="">中评(${middleCommentCount})</span>
                            <span no="4" class="bad">差评(${badCommentCount})</span>
                            <span no="5" class="">有图(${imageCommentCount})</span>
                        </div>
                    </div>
                    <ul class="cmt_list" id="evalDet_summary"></ul>
                    <div class="cmt_list_loading" style="display: none">
                        <span class="tip">加载更多</span>
                        <div class="wx_loading2" id="eveaLoading"><i class="wx_loading_icon"></i>
                        </div>
                    </div>
                </div>
                <div id="appdlCon2"></div>
            </div>
        </div>
    </div>

    <!--E以上是全部弹框-->
    <script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>
    <script type="text/javascript" src="${JZXUrl(Request)}/wx/js/goods_detail.js"></script>
<#else>
    <script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>
    <div class="args_null">
        <span>当前商品不存在或已下架</span>
    </div>
</#if>
</body>
</html>
