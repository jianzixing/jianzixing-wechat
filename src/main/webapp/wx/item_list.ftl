<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><#if keyword??>${keyword}-</#if>商品搜索-简子行商城</title>
    <meta name="keywords" content="">
    <meta name="description" content="">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no, viewport-fit=cover">
    <link type="text/css" rel="stylesheet" href="${JZXUrl(Request)}/wx/css/common.css">
    <link type="text/css" rel="stylesheet" href="${JZXUrl(Request)}/wx/css/header.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/item_list.css">
</head>
<body>
<div class="m_header" style="">
    <div class="m_header_bar search">
        <div class="m_header_bar_back"></div>
        <div class="input">
            <span class="icon"></span>
            <form action="${JZXUrl(Request)}/wx/item_list.jhtml" method="get" id="searchForm">
                <input placeholder="请输入商品关键字" name="keyword" value="${name?if_exists}" id="keyword">
                <input style="display:none"/>
            </form>
        </div>
        <span class="search-btn">搜索</span>
        <div id="m_common_header_key" class="jzx-header-icon-new-shortcut">
            <span></span>
        </div>
    </div>
</div>

<#include "include/header_shortcut.ftl"/>

<div class="search-tip">
    <div class="search-history">
        <div class="title-top">
            <span class="title">搜索历史</span>
            <span style="flex: 1"></span>
            <span class="del"></span>
        </div>
        <div class="history-list">

        </div>
    </div>
    <div class="search-recommend">
        <div class="title-top">
            <span>热门搜索</span>
        </div>
        <div class="recommend-list">
            <a href="${JZXUrl(Request)}/wx/item_list.jhtml?keyword=华为P30">华为P30</a>
        </div>
    </div>

</div>

<div class="wx_wrap">
    <div class="pro_filter_mask" id="pFilterMask"></div>
    <div class="search_head" id="searchHead" style="height: 40px;">
        <div class="search_head_fixer" id="searchHeadFixer" style="transition: transform 0.5s ease 0s;">
            <div class="pro_filter_wrap" id="proFilterWrap">
                <div class="inner" id="sortBlock">
                    <div class="pro_filter" id="sortProBlock">
                        <div class="pro_filter_items" id="barTabs">
                            <a href="javascript:" class="item <#if sort?? && sort='1'>has</#if>" sort="1">
                                <span>综合</span>
                            </a>
                            <a href="javascript:" class="item J_ping <#if sort?? && sort='2'>has</#if>" sort="2">销量</a>
                            <a href="javascript:"
                               class="item J_ping <#if sort?? && sort='3'>has <#elseif sort?? && sort='4'> has cur</#if>"
                               sort="3">
                                <span>价格<i class="icon_tri"></i></span>
                            </a>
                            <a href="javascript:" class="item btn_sf J_ping">筛选</a>
                        </div>
                    </div>

                    <div class="btns" id="barSureBtnBlock">
                        <a href="javascript:" class="btn btn_1 J_ping" id="barResetBtn"
                           report-eventid="MList_SecondaryFilterFloatReset">重置</a>
                        <a href="javascript:" class="btn btn_2 J_ping" id="barSureBtn"
                           report-eventid="MList_SecondaryFilterFloatConfirm">确认</a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div id="searchResBlock" class="">
        <div class="sf_layer_bg" id="sfLayerBg"></div>
        <div class="sf_layer" id="filterBlock">
            <div class="sf_layer_sub_title hide" id="filterSelBlock">
                <strong>已选择：</strong><span id="filterSelTips" class="words_10"></span>
            </div>
            <ul class="sf_layer_tabs hide" id="filterTabs">
                <li class="J_ping" data-order="alphabet">字母排序</li>
                <li class="J_ping" data-order="recommend">推荐排序</li>
            </ul>
            <div class="sf_layer_tips hide" id="filterTips">A</div>
            <div class="sf_alphabet hide" id="filterAlphabet"></div>
            <div class="sf_layer_con" id="filterInner">
                <ul class="mod_list">
                    <li class="super_li no_arrow">
                        <div class="list_inner">
                            <div class="li_line">
                                <div class="big">价格</div>
                                <div class="right"></div>
                            </div>
                        </div>
                    </li>
                    <li class="filterlayer_price">
                        <div class="filterlayer_price_area">
                            <input type="tel" class="filterlayer_price_area_input J_ping" id="minPrice"
                                   placeholder="最低价" value="${minPrice?if_exists}">
                            <div class="filterlayer_price_area_hyphen"></div>
                            <input type="tel" class="filterlayer_price_area_input J_ping" id="maxPrice"
                                   placeholder="最高价" value="${maxPrice?if_exists}">
                        </div>
                    </li>
                </ul>
                <#if (brandList?? && brandList?size!= 0) >
                    <ul class="mod_list brandFilter">
                        <li>
                            <div class="list_inner li_line">
                                <div class="big">品牌</div>
                                <div class="right">
                                    <span class="words_10" r-mark="brand" slen="1" style="max-width: 203px;"></span>
                                </div>
                            </div>
                        </li>
                        <div class="tags_selection">
                            <#list brandList as brand>
                                <div class="J_ping option <#if (brand.selected?? && brand.selected==1)>selected</#if>"
                                     bid="${brand.id}">
                                    <a href="javascript:void 0;">${brand.name}</a>
                                </div>
                            </#list>
                        </div>
                    </ul>
                </#if>
                <#if (parameter?? && parameter?size!=0)>
                    <#list parameter as item>
                        <ul class="mod_list parameterFilter" parameterId="${item.id}">
                            <li>
                                <div class="list_inner li_line">
                                    <div class="big">${item.name}</div>
                                    <div class="right">
                                        <span class="words_10" r-mark="244" slen="1" style="max-width: 171px;"></span>
                                    </div>
                                </div>
                            </li>
                            <div class="tags_selection">
                                <#list item.TableGoodsValue as goodsValue>
                                    <div class="J_ping option <#if (goodsValue.selected?? && goodsValue.selected==1)>selected</#if>"
                                         valueId="${goodsValue.id}">
                                        <a href="javascript:void 0;">${goodsValue.value}</a>
                                    </div>
                                </#list>
                            </div>
                        </ul>
                    </#list>
                </#if>
            </div>
            <div class="filterlayer_bottom_buttons">
                <span class="filterlayer_bottom_button bg_1" id="filterCBtn">取消</span>
                <span class="filterlayer_bottom_button bg_2 J_ping" id="filterFinishBtn">确认
                <span class="filterlayer_bottom_button_small_text"></span></span>
            </div>
        </div>

        <#if (data?? && data.count>0)>
            <div class="search_prolist cols_1" id="itemList">
                <#list data.objects as goods>
                    <#if goods??>
                        <div class="search_prolist_item">
                            <div class="search_prolist_item_inner J_ping"
                                 data-url="${JZXUrl(Request)}/wx/goods_detail.jhtml?id=${goods.id}">
                                <div class="search_prolist_cover">
                                    <img class="photo" src="${JZXFile(goods.fileName)}">
                                </div>
                                <div class="search_prolist_info">
                                    <div class="search_prolist_title">
                                        ${goods.name}
                                    </div>
                                    <div class="search_prolist_index">
                                        ${goods.subtitle!''}
                                    </div>
                                    <div class="price-d">
                                        <div class="search_prolist_price">
                                            <strong>
                                                <em>¥ <span class="int">${goods.price}</span>.00</em>
                                            </strong>
                                        </div>
                                        <div class="search_prolist_line">
                                            <#if (goods.TableDiscount?? && goods.TableDiscount?size != 0)>
                                                <#list goods.TableDiscount as discount>
                                                    <i class="mod_tag">
                                                        ${discount.implName}
                                                    </i>
                                                </#list>
                                            </#if>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </#if>
                </#list>
            </div>
            <!--加载-->
            <div class="wx_loading2 hide" id="loadingLogo"><i class="wx_loading_icon"></i></div>
            <div class="s_empty hide" id="noMoreTips">已无更多商品，您可以换一个关键字搜一下哦~</div>
        <#else>
            <div class="s_null" id="sNull02">
                <h5 style="text-align: center;">抱歉，没有找到符合条件的商品。</h5>
            </div>
        </#if>
    </div>

</body>
<script type="text/tmpl" id="moreItem">
    %{ for(var i in goods){ }%
        <div class="search_prolist_item">
            <div class="search_prolist_item_inner J_ping" data-url="%{out(baseUrl)}%/wx/goods_detail.jhtml?id={=goods[i].id}">
                <div class="search_prolist_cover">
                    <img class="photo" src="%{ out(fileBaseUrl+goods[i].fileName) }%">
                </div>
                <div class="search_prolist_info">
                <div class="search_prolist_title">
                    {=goods[i].name}
                </div>
                <div class="search_prolist_index">
                    {=goods[i].subtitle}
                </div>
                    <div class="search_prolist_price">
                        <strong>
                        <em>¥ <span class="int">{=goods[i].price}</span>.00</em>
                        </strong>
                    </div>
                    <div class="search_prolist_line">
                        %{
                        if(goods.TableDiscount){
                             for(var j in goods.TableDiscount) {
                        }%
                            <i class="mod_tag">
                                {=goods.TableDiscount[j].implName}
                            </i>
                        %{
                           }
                        }
                        }%
                    </div>
                    <div class="search_prolist_other text_small">
                        <i class="mod_tag">
                            <img src="%{out(baseUrl)}%/wx/images/item_list/c5ab4d78f8bf4d90.png">
                        </i>
                        <i class="mod_tag">
                            <img src="%{out(baseUrl)}%/wx/images/item_list/5bfb58faNfdbaaf9e.png">
                        </i>
                    </div>
                </div>
            </div>
         </div>
 	%{ } }%

</script>
<script type="text/tmpl" id="searchHistoryTmpl">
    %{ for(var i in history){ }%
        <a href="%{out(baseUrl)}%/wx/item_list.jhtml?keyword={=history[i]}">{=history[i]}</a>
 	%{ } }%

</script>
<script src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>
<script>
    var total = parseInt("<#if (data?? && data.count>0)>${data.count}<#else >0</#if>");
    var page = 1;
    var baseUrl = "${JZXUrl(Request)}";
    var fileBaseUrl = "${JZXUrl(Request)}/web/image/load.jhtml?f=";
</script>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/easy.templatejs.min.js"></script>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/search_history.js"></script>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/item_list.js"></script>
</html>
