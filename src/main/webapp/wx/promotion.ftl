<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><#if pageInfo??>${pageInfo.title}</#if></title>
    <meta name="viewport"
          content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
    <meta content="yes" name="apple-mobile-web-app-capable">
    <meta content="black" name="apple-mobile-web-app-status-bar-style">
    <meta content="telephone=no" name="format-detection">
    <link href="${JZXUrl(Request)}/wx/css/common.css" rel="stylesheet" type="text/css">
    <link href="${JZXUrl(Request)}/wx/css/header.css" rel="stylesheet" type="text/css">
    <link href="${JZXUrl(Request)}/wx/css/index.css" rel="stylesheet" type="text/css">
</head>
<body>
<#if (!page?? || page==0)>
   页面内容尚未配置或未启用
<#else>
    <div class="m_header" style="">
        <div class="m_header_bar">
            <div class="m_header_bar_back"></div>
            <div class="m_header_bar_title"><#if pageInfo??>${pageInfo.title}</#if></div>
        </div>
        <div id="m_common_header_key" class="jzx-header-icon-new-shortcut">
            <span></span>
        </div>
    </div>

    <#include "include/header_shortcut.ftl"/>

    <div id="floorContent" style="background:<#if pageInfo.background??>${pageInfo.background}</#if>">
        <#list pageContents as content>
            <#if content.type==1>
                <div class="floor slider-wrapper" floor_order="" floor_index="0" id="slideWrapper" style="height:9.15rem;">
                    <div class="slider-bg"></div>
                    <ul class="new-slide j_slide_list" style="margin-top: -0.7rem; backface-visibility: hidden;" data-slide_time="4000">
                        <#list content.data as item>
                            <li class="slide-li j_slide_li" id="" onload="" style="left: ${item?index*100}%;">
                                <a href="javascript:void(0);" data-url="${item.link}">
                                    <img src="${JZXFile(Request, item.img)}"  class="opa1 ll_fadeIn">
                                </a>
                            </li>
                        </#list>
                    </ul>
                    <div class="focus-btn j_slide_nav">
                        <#list 1..content.data?size as index>
                            <span <#if index==1>class="active"</#if> no="${index}"></span>
                        </#list>
                    </div>
                </div>
            <#elseif content.type==2>
                <div class="scroll_news">
                    <a class="jd-news-tit" href="javascript:void(0);">
                        <img src="${JZXUrl(Request)}/wx/images/index/kuaibao.png">
                    </a>
                    <div class="news_list_wrapper">
                        <ul class="news-list j_scroll_news"
                            style="transform: translate3d(0px, 0px, 0px); transition: none 0s ease 0s;">
                            <#list content.data as item>
                                <li class="news_item">
                                    <a href="javascript:void(0);" data-url="${item.link}">
                                        <span class="red">推荐</span>${item.name}
                                    </a>
                                </li>
                            </#list>
                        </ul>
                    </div>
                    <a class="news_more" href="javascript:void(0);" data-url="${JZXUrl(Request)}/wx/article-list.html"><i class="line"></i>更多</a>
                </div>
            <#elseif content.type==3>
                <div class="floor box_wrapper" style="background:<#if pageInfo.background??>${pageInfo.background}</#if>">
                    <div class="position-r">
                        <div class="floor_item">
                            <div class="box_list position-ab">
                                <#list content.data as item>
                                    <a href="javascript:void(0);" data-url="${item.link}">
                                        <img src="${JZXFile(Request, item.img)}">
                                        <span style="color: ">${item.name}</span>
                                    </a>
                                </#list>
                            </div>
                        </div>
                    </div>
                </div>
            <#elseif content.type==4>
                <div class="floor j_expo expo_loaded" style="background:<#if pageInfo.background??>${pageInfo.background}</#if>">
                    <div class="flsPit fls-tit">
                        <div class="floor-title floor-tit-img" style="display: flex; align-items: center; justify-content: center; font-size: 15px;">
                            ${content.data.title?if_exists}
                        </div>
                    </div>

                    <div class="floor-the-container">
                        <div class="floor-container bdr-bottom">
                            <div class="flsPit fls06008">
                                <div class="floor-graphic-item">
                                    <div class="bdr-r graphic-separation  graphic-col02-bg">
                                        <div href="javascript:void(0);" data-url="${content.data.link1?if_exists}">
                                            <div class="real-show">
                                                <div class="graphic-text">
                                                    <span class="graphic-tit j_linear_color">${content.data.title1}</span>
                                                </div>
                                                <div class="graphic-img graphic-img02 ">
                                                    <a href="javascript:void(0)" data-url="${content.data.link1?if_exists}">
                                                        <img src="${JZXFile(Request, content.data.img1)}" class="opa1">
                                                    </a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class=" graphic-separation  graphic-col02-bg">
                                        <div href="javascript:void(0);" data-url="${content.data.link2?if_exists}">
                                            <div class="real-show">
                                                <div class="graphic-text">
                                                    <span class="graphic-tit j_linear_color">${content.data.title2}</span>
                                                </div>
                                                <div class="graphic-img graphic-img02 ">
                                                    <a href="javascript:void(0)" data-url="${content.data.link2?if_exists}">
                                                        <img src="${JZXFile(Request, content.data.img2)}" class="opa1">
                                                    </a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="floor-container bdr-bottom" img_type="1">
                            <div class="flsPit fls06013">
                                <div class="floor-graphic-item">
                                    <div class="bdr-r graphic-separation graphic-col04-bg" img_type="1">
                                        <a data-url="${content.data.link3?if_exists}">
                                            <div class="real-show">
                                                <div class="graphic-text">
                                                    <strong class="graphic-tit j_linear_color">${content.data.title3}</strong>
                                                </div>
                                                <div class="graphic-img">
                                                    <img src="${JZXFile(Request, content.data.img3)}" class="opa1">
                                                </div>
                                            </div>
                                        </a>
                                    </div>

                                    <div class="bdr-r graphic-separation graphic-col04-bg" img_type="1">
                                        <a data-url="${content.data.link4?if_exists}">
                                            <div class="real-show">
                                                <div class="graphic-text">
                                                    <strong class="graphic-tit j_linear_color">${content.data.title4}</strong>
                                                </div>
                                                <div class="graphic-img">
                                                    <img src="${JZXFile(Request, content.data.img4)}" class="opa1">
                                                </div>
                                            </div>
                                        </a>
                                    </div>

                                    <div class="bdr-r graphic-separation graphic-col04-bg" img_type="1">
                                        <a data-url="${content.data.link5?if_exists}">
                                            <div class="real-show">
                                                <div class="graphic-text">
                                                    <strong class="graphic-tit j_linear_color">${content.data.title5}</strong>
                                                </div>
                                                <div class="graphic-img">
                                                    <img src="${JZXFile(Request, content.data.img5)}" class="opa1">
                                                </div>
                                            </div>
                                        </a>
                                    </div>

                                    <div class="bdr-r graphic-separation graphic-col04-bg" img_type="1">
                                        <a data-url="${content.data.link6?if_exists}">
                                            <div class="real-show">
                                                <div class="graphic-text">
                                                    <strong class="graphic-tit j_linear_color">${content.data.title6}</strong>
                                                </div>
                                                <div class="graphic-img">
                                                    <img src="${JZXFile(Request, content.data.img6)}" class="opa1">
                                                </div>
                                            </div>
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            <#elseif content.type==5>
                <div class="floor j_expo expo_loaded" style="background:<#if pageInfo.background??>${pageInfo.background}</#if>">
                    <div class="flsPit fls-tit">
                        <div class="floor-title floor-tit-img" style="display: flex; align-items: center; justify-content: center; font-size: 15px;">
                            ${content.data.title?if_exists}
                        </div>
                    </div>

                    <div class="floor-container bdr-bottom" img_type="1">
                        <div class="flsPit fls06013">
                            <div class="floor-graphic-item">
                                <div class="bdr-r graphic-separation graphic-col04-bg">
                                    <a data-url="${content.data.link1?if_exists}">
                                        <div class="real-show">
                                            <div class="graphic-text">
                                            <strong class="graphic-tit j_linear_color">${content.data.title1?if_exists}</strong>
                                            <#--<p class="graphic-wz" style="color:#222222">-->
                                            <#--低价抢大牌-->
                                            <#--</p>-->
                                            </div>
                                            <div class="graphic-img">
                                                <img class="opa1 ll_fadeIn" src="${JZXFile(Request, content.data.img1?if_exists)}">
                                            </div>
                                        </div>
                                    </a>
                                </div>

                                <div class="bdr-r graphic-separation graphic-col04-bg">
                                    <a data-url="${content.data.link2?if_exists}">
                                        <div class="real-show">
                                            <div class="graphic-text">
                                            <strong class="graphic-tit j_linear_color">${content.data.title2?if_exists}</strong>
                                            <#--<p class="graphic-wz" style="color:#222222">-->
                                            <#--低价抢大牌-->
                                            <#--</p>-->
                                            </div>
                                            <div class="graphic-img">
                                                <img class="opa1 ll_fadeIn" src="${JZXFile(Request, content.data.img2?if_exists)}">
                                            </div>
                                        </div>
                                    </a>
                                </div>

                                <div class="bdr-r graphic-separation graphic-col04-bg">
                                     <a data-url="${content.data.link3?if_exists}">
                                        <div class="real-show">
                                            <div class="graphic-text">
                                                <strong class="graphic-tit j_linear_color">${content.data.title3}</strong>
                                                <#--<p class="graphic-wz" style="color:#222222">-->
                                                    <#--精选1元购-->
                                                <#--</p>-->
                                            </div>
                                            <div class="graphic-img">
                                                <img class="opa1 ll_fadeIn" src="${JZXFile(Request, content.data.img3?if_exists)}">
                                            </div>
                                        </div>
                                    </a>
                                </div>

                                <div class="bdr-r graphic-separation graphic-col04-bg">
                                    <a data-url="${content.data.link4?if_exists}">
                                        <div class="real-show">
                                            <div class="graphic-text">
                                                <strong class="graphic-tit j_linear_color">${content.data.title4?if_exists}</strong>
                                            </div>
                                            <div class="graphic-img">
                                                <img class="opa1 ll_fadeIn" src="${JZXFile(Request, content.data.img4?if_exists)}">
                                            </div>
                                        </div>
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="floor-container bdr-bottom">
                        <div class="flsPit fls06013">
                            <div class="floor-graphic-item">
                                <div class="bdr-r graphic-separation graphic-col04-bg">
                                    <a data-url="${content.data.link5?if_exists}">
                                        <div class="real-show">
                                            <div class="graphic-text">
                                                <strong class="graphic-tit j_linear_color">${content.data.title5?if_exists}</strong>
                                            </div>
                                            <div class="graphic-img">
                                                <img class="opa1 ll_fadeIn" src="${JZXFile(Request, content.data.img5?if_exists)}">
                                            </div>
                                        </div>
                                    </a>
                                </div>


                                <div class="bdr-r graphic-separation graphic-col04-bg" img_type="2">
                                    <a data-url="${content.data.link6?if_exists}">
                                        <div class="real-show">
                                            <div class="graphic-text">
                                            <strong class="graphic-tit j_linear_color">${content.data.title6?if_exists}</strong>
                                            </div>
                                            <div class="graphic-img">
                                                <img class="opa1 ll_fadeIn" src="${JZXFile(Request, content.data.img6?if_exists)}">
                                            </div>
                                        </div>
                                    </a>
                                </div>

                                <div class="bdr-r graphic-separation graphic-col04-bg" img_type="2">
                                    <a data-url="${content.data.link7?if_exists}">
                                        <div class="real-show">
                                            <div class="graphic-text">
                                            <strong class="graphic-tit j_linear_color">${content.data.title7?if_exists}</strong>
                                            </div>
                                            <div class="graphic-img">
                                                <img class="opa1 ll_fadeIn" src="${JZXFile(Request, content.data.img7?if_exists)}">
                                            </div>
                                        </div>
                                    </a>
                                </div>

                                <div class="bdr-r graphic-separation graphic-col04-bg" img_type="2">
                                    <a data-url="${content.data.link8?if_exists}">
                                        <div class="real-show">
                                            <div class="graphic-text">
                                                <strong class="graphic-tit j_linear_color">${content.data.title8?if_exists}</strong>
                                            </div>
                                            <div class="graphic-img">
                                                <img class="opa1 ll_fadeIn" src="${JZXFile(Request, content.data.img8?if_exists)}">
                                            </div>
                                        </div>
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            <#elseif content.type==6> <!--商品推荐-->

                <div class="floor love-floor" style="background:<#if pageInfo.background??>${pageInfo.background}</#if>">
                    <#if content.data.title??>
                        <div class="flsPit fls-tit">
                            <div class="floor-title floor-tit-img" style="display: flex; align-items: center; justify-content: center; font-size: 15px;">
                                ${content.data.title}
                            </div>
                        </div>
                    </#if>
                    <div class="floor-the-container">
                        <ul class="find-similar-ul j_rec_goods_list">
                            <#list content.data.data as item>
                                <li class="similar-li j_similar_li j_similar_goods">
                                    <a href="javascript:void(0);"
                                       data-url="${JZXUrl(Request)}/wx/goods_detail.jhtml?id=${item.id}">
                                        <div class="similar-product">
                                            <div class="similar-posre">
                                                <img src="${JZXFile(Request, item.fileName)}" class="j_rec_goods_pic opa1 ll_fadeIn" style="opacity: 1; width: 176px; height: 176px;">
                                            </div>
                                            <span class="similar-product-text">${item.name}</span>
                                            <p class="similar-product-info">
                                                <span class="similar-product-price">
                                                    ¥&nbsp;<span class="big-price">${item.price}</span>
                                                </span>
                                            </p>
                                            <p class="similar-product-info"></p>
                                        </div>
                                    </a>
                                </li>
                            </#list>
                        </ul>
                        <div style="clear: left;"></div>
                    </div>
                </div>
            <#elseif content.type==7>
                <div class="floor j_expo expo_loaded" style="background:<#if pageInfo.background??>${pageInfo.background}</#if>">
                    <div class="floor-the-container-outer floor-7148">
                        <div class="floor-the-container">
                            <div class="floor-container floor06051">
                                <div class="catchimg" data-url="${content.data.link}">
                                    <img class="catchimg-bg" src="${JZXFile(Request, content.data.img)}" />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </#if>

        </#list>

    </div>
</#if>
</body>
<script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/index.js"></script>
</html>
