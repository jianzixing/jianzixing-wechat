<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>添加收货地址</title>
    <meta name="keywords" content="">
    <meta name="description" content="">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no, viewport-fit=cover">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/common.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/mine/address.css">
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/velocity.min.js"></script>
</head>
<body>
<div class="wx_wrap" id="wrapBody"
     url="<#if r??>${r}<#else>${JZXUrl(Request)}/wx/address_list.jhtml</#if>"
     <#if address??>data-id="${address['id']}"</#if>>

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

    <div id="pageAddAddress" class="address_new" style="transform: translate(0px, 0px); padding-bottom: 10px;">
        <p>
            <label for="">
                <span class="tit">收货人</span>
                <input id="name" type="text" value="${(address.realName)!''}" placeholder="姓名">
            </label>
        </p>
        <p class="label_flex">
            <label for="">
                <span class="tit">联系方式</span>
                <span class="foreign_prefix" id="areaCode" style="display:none;"></span>
                <input id="mobile" value="${(address.phoneNumber)!''}" type="tel" placeholder="手机号码" maxlength="17">
            </label>
        </p>
        <p class="street_detail selAddr" id="selAddr">
            <label for="">
                <span class="tit">所在地区</span>
                <input type="text" placeholder="选择所在地区" readonly="readonly">
            </label>
        </p>
        <p class="street_detail" id="adinfoP">
            <label for="">
                <span class="tit">详细地址</span>
                <textarea placeholder="详细地址需填写楼栋楼层或房间号信息" id="adinfo" value=""
                          rows="2">${(address.address)!''}</textarea>
                <i class="close" id="info_clear"></i>
            </label>
        </p>
        <p id="g_emailP" style="display:none;">
            <label for="">
                <span class="tit">邮箱</span>
                <input id="g_email" value="" type="text" placeholder="必填"></label>
        </p>
        <p id="g_postcodeP" style="display:none;">
            <label for="">
                <span class="tit">邮编</span>
                <input id="g_postcode" value="" type="text" placeholder="必填"></label>
        </p>
        <p id="g_phoneP" style="display:none;">
            <label for="">
                <span class="tit">固定电话</span>
                <input id="g_phone" value="" type="text" placeholder="选填"></label>
        </p>
        <p class="address_tags" id="adlabelP" value="">
            <span class="tit">地址标签</span>
            <span class="address_tags_tag <#if address??&&address['label']=='公司'>cur</#if>" value="公司">公司</span>
            <span class="address_tags_tag <#if address??&&address['label']=='家'>cur</#if>" value="家">家</span>
            <span class="address_tags_tag <#if address??&&address['label']=='学校'>cur</#if>" value="学校">学校</span>
            <!--自定义后这个标签隐藏-->
            <span class="address_tags_tag user_tag" value="">
                自定义<i class="address_tags_close"></i>
            </span>
            <#if address?? && address['label']!='公司' && address['label']!='家' &&address['label']!='学校'>
                <span class="address_tags_tag_wrap">
                    <span class="address_tags_tag cur" id="userLabelVal"
                          value="${address['label']}">${address['label']}</span>
                    <span class="address_tags_edit"></span>
                </span>
            <#else>
                <span class="address_tags_tag_wrap" style="display: none">
                    <span class="address_tags_tag" id="userLabelVal" value="自定义">自定义</span>
                    <span class="address_tags_edit"></span>
                </span>
            </#if>
        </p>
        <#if address?? && address['isDefault'] == 1>
            <div class="checkbox_select selected" id="editAddrSetDef">设为默认地址</div>
        <#else>
            <div class="checkbox_select" id="editAddrSetDef">设为默认地址</div>
        </#if>
        <div class="mod_btns">
            <#if isEdit?? && isEdit>
                <a href="javascript:void(0);" class="mod_btn bg_1" id="submitAddress"
                   url="${JZXUrl(Request)}/wx/mine/address_modify_submit.jhtml">
                    保存
                </a>
            <#else>
                <a href="javascript:void(0);" class="mod_btn bg_1" id="submitAddress"
                   url="${JZXUrl(Request)}/wx/mine/address_add_submit.jhtml">
                    确认并使用该地址
                </a>
            </#if>
        </div>
        <#if address?? && isEdit?? && isEdit>
            <div class="mod_btns">
                <a href="javascript:void(0);" class="mod_btn bg_" id="delAddress"
                   url="${JZXUrl(Request)}/wx/mine/address_del_submit.jhtml?addrid=${address['id']}">删除收货地址</a>
            </div>
        </#if>
    </div>
</div>

<!--地址选择器-->
<div style="display: none" id="addrSelector"
     province-url="${JZXUrl(Request)}/wx/mine/address_provinces.jhtml"
     city-url="${JZXUrl(Request)}/wx/mine/address_city.jhtml"
     county-url="${JZXUrl(Request)}/wx/mine/address_county.jhtml"
     <#if address??>pid="${address['provinceCode']}" cid="${address['cityCode']}" tid="${address['countyCode']}"</#if>>
    <div class="mod_address_slide show">
        <div class="mod_address_slide_main type_flex">
            <div class="mod_address_slide_head">所在地区<i class="close"></i></div>
            <div class="mod_address_slide_body">
                <ul class="mod_address_slide_tabs_1">
                    <li tag="plsSelProvince" addrid="0" class="cur"><span>请选择</span></li>
                </ul>
                <ul class="mod_address_slide_tabs_1">
                    <li tag="province" addrid="" class="cur">
                        <span>请选择</span>
                    </li>
                    <li tag="city" addrid="" class="">
                        <span>请选择</span>
                    </li>
                    <li tag="county" addrid="" class="">
                        <span>请选择</span>
                    </li>
                </ul>
                <ul class="mod_address_slide_list_2"></ul>
            </div>
        </div>
    </div>
</div>

<!--自定义标签-->
<div class="mod_alert_mask" style=""></div>
<div class="mod_alert mod_alert_info fixed" id="userLabelDiv">
    <h3 class="title">自定义标签名称</h3>
    <div class="input_wrap">
        <input type="text" id="labelInput" maxlength="8" placeholder="标签最多填写八个字">
    </div>
    <p class="btns">
        <a href="javascript:void(0)" id="labelCancel" class="btn btn_2">取消</a>
        <a href="javascript:void(0)" id="labelSure" class="btn btn_1">确认</a>
    </p>
</div>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/mine/address.js"></script>
</body>
</html>
