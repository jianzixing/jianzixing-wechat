<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no, viewport-fit=cover">
    <title>分类页</title>
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/common.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/mine/index.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/catalog.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/header.css">
</head>
<body>
<div>
    <div class="m_header" style="">
        <div class="m_header_bar">
            <div class="m_header_bar_back"></div>
            <div class="m_header_bar_title">分类</div>
        </div>
        <div id="m_common_header_key" class="jzx-header-icon-new-shortcut">
            <span></span>
        </div>
    </div>

    <#include "include/header_shortcut.ftl"/>

    <div class="catalog">
        <#list catalogList as currentCategory>
            <div class="cate">
                <div class="hd">
                    <span class="line"></span>
                    <span class="txt">${currentCategory.name}</span>
                    <span class="line"></span>
                </div>
                <div class="bd">
                    <#list currentCategory.children as item>
                        <a href="${JZXUrl(Request)}/wx/item_list.jhtml?gId=${item.id}"
                           class="item <#if (item_index+1) % 4 == 0>'last'<#else>''</#if>}">
                            <img class="icon" src="<#if item.logo??>${JZXFile(item.logo)}<#else>${JZXUrl(Request)}/admin/image/exicon/nopic_100.gif</#if>"/>
                            <span class="txt">${item.name}</span>
                        </a>
                    </#list>
                </div>
            </div>
        </#list>
    </div>
</div>
<#import "include/tab.ftl" as tab/>
<@tab.tab index=2/>
</body>
<script src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>

<script>
    $(function () {
        var isSearch = false;
        $(".search .back").click(function () {
            if (isSearch) {
                isSearch = false;
                $(".search .search-btn").hide();
                $(".search .menu").show();
                $(".search-tip").hide();
            } else {
                location.href = "/wx/index.jhtml";
            }
        });

        $("#keyword").click(function () {
            isSearch = true;
            $(".search .search-btn").show();
            $(".search .menu").hide();
            $(".search-tip").show();
        });
    });
</script>
</html>
