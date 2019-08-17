<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <title>收货地址</title>
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no, viewport-fit=cover">

    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/common.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/mine/address_list.css">
<body>

<div class="wx_wrap" id="wrapBody">
    <div class="m_header" style="">
        <div class="m_header_bar">
            <div class="m_header_bar_back"></div>
            <div class="m_header_bar_title">收货地址</div>
        </div>
        <div id="m_common_header_key" class="jzx-header-icon-new-shortcut">
            <span></span>
        </div>
    </div>

    <#include "../include/header_shortcut.ftl"/>

    <div id="addressList" style="">
        <div class="address_list <#if isSelMod??>al_sel_mod</#if>">
            <#if addresses??>
                <#list addresses as addr>
                    <div class="address"
                         <#if isSelMod??>data-url="${JZXUrl('&',r,'addrid',addr['id'])}"</#if>>
                        <ul adid="${addr['id']}" <#if isSelMod??&&addr['selected']>class="selected"</#if>>
                            <li><strong>${addr['realName']}</strong><strong>${addr['phoneNumber']}</strong></li>
                            <li>
                                <#if addr['isDefault'] == 1>
                                    <span class="tag tag_red">默认</span>
                                <#else>
                                    <#if addr['label']??>
                                        <span class="tag">${addr['label']}</span>
                                    </#if>
                                </#if>
                                ${addr['province']}${addr['city']}${addr['county']}${addr['address']}
                            </li>
                            <li class="edit" type="1"
                                data-url="${JZXUrl(Request)}/wx/mine/address_modify.jhtml?r=${JZXUrl(Request,'$$')}&aid=${addr['id']}">
                                编辑
                            </li>
                        </ul>
                        <p class="act" adid="${addr['id']}" type="1"><span class="del">删除</span></p>
                    </div>
                </#list>
            </#if>
        </div>

        <div class="mod_btns fixed" tag="jd" style="">
            <a href="${JZXUrl(Request)}/wx/mine/address_add.jhtml?r=${JZXUrl(Request,'$$')}"
               class="mod_btn bg_1">新增收货地址</a>
        </div>
    </div>

    <div class="mod_alert fixed">
        <i class="icon"></i>
        <p>确认删除该地址吗?</p>
        <p class="btns">
            <a href="javascript:;" id="ui_btn_cancel" class="btn ">取消</a>
            <a href="javascript:;" id="ui_btn_confirm" class="btn btn_1">确定</a>
        </p>
    </div>
</div>

<!-- 确认浮层 -->
<div class="wx_loading" id="wxloading" style="display: none;">
    <div class="wx_loading_inner">menuitem
        <i class="wx_loading_icon"></i>
        <span>请求加载中...</span>
    </div>
</div>
<div class="mod_alert_mask"></div>
</body>
<script src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>
<script>
    $(function () {
        var x = 0, y = 0;
        var ul = $("#addressList .address ul");

        ul.on('touchstart', function (e) {
            x = e.originalEvent.changedTouches[0].pageX;
            y = e.originalEvent.changedTouches[0].pageY;
        });
        ul.on('touchmove', function (e) {
            console.log('touchmove');

            var moveEndX = e.originalEvent.changedTouches[0].pageX;
            // var moveEndY = e.originalEvent.changedTouches[0].pageY;
            if (moveEndX - x > 70) {
                $(this).css('transform', 'translateX(0px)');
                $(this).css('transition', '-webkit-transform 0.5s ease 0s');
            }

            if (x - moveEndX > 70) {
                $(this).css('transform', 'translateX(-70px)');
                $(this).css('transition', '-webkit-transform 0.5s ease 0s');
            }
        });


        $(".act").click(function () {
            $(".mod_alert").addClass('show');
            $('.mod_alert_mask').addClass('show');
        });

        $('#ui_btn_cancel').click(function () {
            $(".mod_alert").removeClass('show');
            $('.mod_alert_mask').removeClass('show');
        });
    })
</script>

</html>
