<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>售后单详情</title>
    <meta name="keywords" content="">
    <meta name="description" content="">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no, viewport-fit=cover">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/common.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/mine/after_sale_detail.css">
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/velocity.min.js"></script>
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/imgviewer.js"></script>
</head>
<body>
<div class="wx_wrap" id="wrapBody"
     cancel-url="${JZXUrl(Request)}/wx/mine/after_sale_cancel.jhtml"
     reback-url="${JZXUrl(Request)}/wx/mine/after_sale_reback.jhtml">
    <div class="m_header" style="">
        <div class="m_header_bar">
            <div data-url="${JZXUrl(Request)}/wx/mine/after_sale.jhtml" class="m_header_bar_back"></div>
            <div class="m_header_bar_title">售后单详情</div>
        </div>
        <div id="m_common_header_key" class="jzx-header-icon-new-shortcut">
            <span></span>
        </div>
    </div>

    <#include "../include/header_shortcut.ftl"/>

    <#if afterSale??>
        <div class="flow">
            <div class="view">
                <div class="status">
                    <#if afterSale['status'] == 0>
                        <span>新建</span>
                    <#elseif afterSale['status'] == 10>
                        <span>提交审核</span>
                    <#elseif afterSale['status'] == 20>
                        <span>审核通过</span>
                    <#elseif afterSale['status'] == 21>
                        <span>审核拒绝</span>
                    <#elseif afterSale['status'] == 30>
                        <span>寄回商品</span>
                    <#elseif afterSale['status'] == 31>
                        <span>买家寄回拒收</span>
                    <#elseif afterSale['status'] == 40>
                        <span>卖家已收货</span>
                    <#elseif afterSale['status'] == 41>
                        <span>验收失败</span>
                    <#elseif afterSale['status'] == 42>
                        <span>验收通过</span>
                    <#elseif afterSale['status'] == 50>
                        <span>维修中...</span>
                    <#elseif afterSale['status'] == 51>
                        <span>无法维修</span>
                    <#elseif afterSale['status'] == 52>
                        <span>维修完成</span>
                    <#elseif afterSale['status'] == 60>
                        <span>卖家寄回商品</span>
                    <#elseif afterSale['status'] == 61>
                        <span>买家拒收商品</span>
                    <#elseif afterSale['status'] == 80>
                        <span>正在退款</span>
                    <#elseif afterSale['status'] == 81>
                        <span>退款失败</span>
                    <#elseif afterSale['status'] == 90>
                        <span>取消售后</span>
                    <#elseif afterSale['status'] == 100>
                        <span>完成</span>
                    </#if>
                </div>
                <div class="step">
                    <#if afterSale['type'] == 10>
                        <div class="item">
                            <div class="<#if status_1>finish<#else>line</#if>"></div>
                            <div class="line"></div>
                            <div class="words left"><span>提交审核</span></div>
                        </div>
                        <div class="item big">
                            <div class="line"></div>
                            <div class="<#if status_2>finish<#else>line</#if>"></div>
                            <div class="line"></div>
                            <div class="words center"><span>审核通过</span></div>
                        </div>
                        <div class="item big">
                            <div class="line"></div>
                            <div class="<#if status_3>finish<#else>line</#if>"></div>
                            <div class="line"></div>
                            <div class="words center"><span>卖家收货</span></div>
                        </div>
                        <div class="item">
                            <div class="line"></div>
                            <div class="<#if status_4>finish<#else>line</#if>"></div>
                            <div class="words right"><span>退款</span></div>
                        </div>
                    <#elseif afterSale['type'] == 20>
                        <div class="item">
                            <div class="<#if status_1>finish<#else>line</#if>"></div>
                            <div class="line"></div>
                            <div class="words left"><span>提交审核</span></div>
                        </div>
                        <div class="item big">
                            <div class="line"></div>
                            <div class="<#if status_2>finish<#else>line</#if>"></div>
                            <div class="line"></div>
                            <div class="words center"><span>审核通过</span></div>
                        </div>
                        <div class="item big">
                            <div class="line"></div>
                            <div class="<#if status_3>finish<#else>line</#if>"></div>
                            <div class="line"></div>
                            <div class="words center"><span>卖家收货</span></div>
                        </div>
                        <div class="item">
                            <div class="line"></div>
                            <div class="<#if status_4>finish<#else>line</#if>"></div>
                            <div class="words right"><span>换新</span></div>
                        </div>
                    <#elseif afterSale['type'] == 30>
                        <div class="item">
                            <div class="<#if status_1>finish<#else>line</#if>"></div>
                            <div class="line"></div>
                            <div class="words left"><span>提交审核</span></div>
                        </div>
                        <div class="item big">
                            <div class="line"></div>
                            <div class="<#if status_2>finish<#else>line</#if>"></div>
                            <div class="line"></div>
                            <div class="words center"><span>审核通过</span></div>
                        </div>
                        <div class="item big">
                            <div class="line"></div>
                            <div class="<#if status_3>finish<#else>line</#if>"></div>
                            <div class="line"></div>
                            <div class="words center"><span>卖家收货</span></div>
                        </div>
                        <div class="item">
                            <div class="line"></div>
                            <div class="<#if status_4>finish<#else>line</#if>"></div>
                            <div class="words right"><span>维修</span></div>
                        </div>
                    </#if>
                </div>
            </div>
        </div>

        <#if afterSale['TableAfterSalesProgress']??>
            <div class="hs_bar">
                <div class="history_status"
                     data-url="${JZXUrl(Request)}/wx/mine/after_sale_process.jhtml?n=${afterSale['number']}">
                    <span>${afterSale['TableAfterSalesProgress']['detail']}</span>
                </div>
            </div>
        </#if>
        <div class="first_tell_split"></div>
        <#if afterSale['type'] == 10 && afterSale['status'] == 100>
            <#if refunds??>
                <div class="tell refund">
                    <span class="t1">已退款</span>
                    <span class="t2">￥${JZXPrice(refunds['totalPrice'])}</span>
                    <span class="t3">明细</span>
                </div>
            </#if>
        </#if>

        <div class="tell goods">
            <div class="title">
                <span>商品信息</span>
            </div>
            <#assign orderGoods = afterSale['TableOrderGoods']/>
            <div class="gls">
                <div class="img">
                    <img src="${JZXFile(Request,orderGoods['fileName'])}"/>
                </div>
                <div class="goods_right">
                    <div class="name">
                        <span>${orderGoods['goodsName']}</span>
                    </div>
                    <div class="amount">
                        <span>单价：</span>
                        <span class="black mr20">¥${orderGoods['unitPrice']}</span>
                        <span>申请数量：</span>
                        <span class="black">${afterSale['amount']}</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="tell dt_mark display_form">
            <div class="group split">
                <div class="item">
                    <span class="label">服务单号：</span>
                    <span class="text">${afterSale['number']}</span>
                </div>
                <div class="item">
                    <span class="label">申请时间：</span>
                    <span class="text">${JZXDateFormat(afterSale['createTime'],'yyyy-MM-dd HH:mm:ss')}</span>
                </div>
                <div class="item">
                    <span class="label">服务类型：</span>
                    <#if afterSale['type'] == 10>
                        <span class="text">退货</span>
                    <#elseif afterSale['type'] == 20>
                        <span class="text">换货</span>
                    <#elseif afterSale['type'] == 30>
                        <span class="text">维修</span>
                    </#if>
                </div>
            </div>

            <#if afterSale['type'] == 10>
                <div class="group split">
                    <div class="item">
                        <span class="label">退款方式：</span>
                        <span class="text">原路返还</span>
                    </div>
                </div>
            </#if>

            <#--有地址且状态为审核通过-->
            <#if afterSale['TableAfterSalesAddress']?? && afterSale['status'] gte 20>
                <div class="group split">
                    <#list afterSale['TableAfterSalesAddress'] as sa>
                        <#if sa['type']==1 && afterSale['deliveryType'] == 0>
                            <div class="item">
                                <span class="label">商品退回：</span>
                                <span class="text">快递至商家</span>
                            </div>
                            <div class="item">
                                <span class="label">联&nbsp;系&nbsp;人&nbsp;：</span>
                                <span class="text">${sa['realName']}</span>
                            </div>
                            <div class="item">
                                <span class="label">联系电话：</span>
                                <span class="text">${sa['phoneNumber']}</span>
                            </div>
                            <div class="item">
                                <span class="label">寄送地址：</span>
                                <span class="text">${sa['address']}</span>
                            </div>
                        </#if>
                    </#list>
                </div>
            </#if>

            <div class="group split">
                <#if afterSale['reason']??>
                    <div class="item">
                        <span class="label">售后原因：</span>
                        <span class="text">${afterSale['reason']}</span>
                    </div>
                </#if>
                <#if afterSale['detail']??>
                    <div class="item">
                        <span class="label">问题描述：</span>
                        <span class="text">${afterSale['detail']}</span>
                    </div>
                </#if>
                <#if afterSale['TableAfterSalesImages']??>
                    <div class="item">
                        <span class="label" style="display: none">售后图片：</span>
                        <div class="text">
                            <#list afterSale['TableAfterSalesImages'] as img>
                                <div class="img_item">
                                    <img src="${JZXFile(Request,img['fileName'])}"/>
                                </div>
                            </#list>
                        </div>
                    </div>
                </#if>
            </div>
        </div>

        <#if afterSale['status'] == 0 || afterSale['status'] == 10>
            <div class="btm_tool">
                <div id="cancel_as_btn" class="tool_btn red">
                    <span>取消售后单</span>
                </div>
            </div>
        <#elseif afterSale['status'] == 20>
            <div class="btm_tool">
                <div id="reback_as_btn" class="tool_btn red">
                    <span>寄回商品</span>
                </div>
            </div>
        <#elseif afterSale['status'] == 100 || afterSale['status'] == 90>
            <div class="btm_tool">
                <div class="tool_btn">
                    <span>删除售后单</span>
                </div>
            </div>
        </#if>
    </#if>
</div>


<div class="refund_detail">
    <div class="mask_content">
        <div class="title">
            <span>退款明细</span>
            <i class="close"></i>
        </div>
        <div class="display_form">
            <#if refunds??>
                <#list refunds['ways'] as way>
                    <div class="group split">
                        <div class="item">
                            <span class="label">退款总额：</span>
                            <span class="text">¥${JZXPrice(way['refundPrice'])}</span>
                        </div>
                        <div class="item">
                            <span class="label">退回至${way['payChannelName']}：</span>
                            <#if way['isDiffAmount'] == 1>
                                <span class="text price">${way['refundAmount']}</span>
                            <#else>
                                <span class="text price">¥${JZXPrice(way['refundPrice'])}</span>
                            </#if>
                        </div>
                        <div class="item" style="display: none">
                            <span class="label">差额原因：</span>
                            <span class="text">其他</span>
                        </div>
                    </div>
                </#list>
            </#if>
        </div>
    </div>
</div>

<div id="reback_as_btn_modal" class="modal_full">
    <div class="m_header" style="">
        <div class="m_header_bar">
            <div class="m_header_bar_title">
                寄回商品
            </div>
            <div class="m_header_bar_close"></div>
        </div>
        <div class="jzx-header-icon-new-shortcut" style="display: none">
            <span></span>
        </div>
    </div>
    <div class="wx_wrap">
        <div id="reback_as_btn_box" class="address_new" style="transform: translate(0px, 0px); padding-bottom: 10px;">
            <#if companies??>
                <p class="reback_as_btn_box_1">
                    <label for="">
                        <span class="tit">物流公司</span>
                        <select>
                            <#list companies as c>
                                <option value="${c['code']}">${c['name']}</option>
                            </#list>
                            <option value="0">自定义物流</option>
                        </select>
                    </label>
                </p>
            </#if>
            <p class="reback_as_btn_box_2" style="display: none">
                <label for="">
                    <span class="tit">物流公司</span>
                    <input type="text" value="" placeholder="物流公司">
                </label>
            </p>
            <p class="reback_as_btn_box_3">
                <label for="">
                    <span class="tit">物流单号</span>
                    <input type="text" value="" placeholder="物流单号">
                </label>
            </p>
            <div class="mod_btns">
                <a href="javascript:void(0);" class="mod_btn bg_1" id="reback_as_btn_box_submit">确认</a>
            </div>
        </div>
    </div>


    <script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>
    <script type="text/javascript">
        $(function () {
            $('.tell.refund').click(function () {
                $('.refund_detail').show();
                $('.refund_detail .mask_content').addClass('show');
            });

            $('.refund_detail .close').click(function () {
                $('.refund_detail').hide();
                $('.refund_detail .mask_content').removeClass('show');
            });
            $('.refund_detail').click(function () {
                $('.refund_detail').show();
                $('.refund_detail .mask_content').addClass('show');
            });

            $('.mask_content').click(function (e) {
                e.stopPropagation();
            });

            $('.display_form .item .img_item img').click(function () {
                var imgs = $('.display_form .item .img_item img');
                var items = [], index = 0;
                for (var i = 0; i < imgs.length; i++) {
                    var img = imgs[i];
                    items.push({src: img.getAttribute('src')});
                    if ($(this)[0] == img) {
                        index = i;
                    }
                }

                var imgViewer = new ImgViewer(items, {index: index});
            });

            $('#cancel_as_btn').click(function () {
                CommentUtils.confirm('确定取消售后单？', function () {
                    CommentUtils.wait('正在取消...');
                    $.ajax({
                        url: $('#wrapBody').attr('cancel-url'),
                        dataType: 'json',
                        data: {n: '${afterSale['number']}'},
                        success: function (data) {
                            if (data['success'] == 1) {
                                window.location.reload();
                            } else {
                                CommentUtils.alert('', '取消售后单失败');
                            }
                        },
                        error: function (xhr) {
                            CommentUtils.closeWait();
                            CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
                        }
                    })
                })
            });

            $('#reback_as_btn').click(function () {
                $('#reback_as_btn_modal').show();
            });

            var select = $('#reback_as_btn_box').find('select');
            if (select.length > 0) {
                $('#reback_as_btn_box').find('select').change(function () {
                    var val = $('#reback_as_btn_box').find('select').val();
                    if (val == '0') {
                        $('#reback_as_btn_box').find('.reback_as_btn_box_2').show();
                    } else {
                        $('#reback_as_btn_box').find('.reback_as_btn_box_2').hide();
                    }
                })
            } else {
                $('#reback_as_btn_box').find('.reback_as_btn_box_2').show();
            }
            $('#reback_as_btn_box_submit').click(function () {
                var val = $('#reback_as_btn_box').find('select').val();
                var valName = $('.reback_as_btn_box_2 input').val();
                var valNumber = $('.reback_as_btn_box_3 input').val();

                CommentUtils.wait('正在提交...');
                CommentUtils.confirm('确定提交发货信息吗？', function () {
                    $.ajax({
                        url: $('#wrapBody').attr('reback-url'),
                        dataType: 'json',
                        data: {n: '${afterSale['number']}', code: val, name: valName, number: valNumber},
                        success: function (data) {
                            CommentUtils.closeWait();
                            if (data['success'] == 1) {
                                window.location.reload();
                            } else {
                                if (data['code'] == 'not_found_sale_addr') {
                                    CommentUtils.alert('提交失败', '没有找到卖家收货地址');
                                } else {
                                    CommentUtils.alert('请求出错', '提交失败');
                                }
                            }
                        },
                        error: function (xhr) {
                            CommentUtils.closeWait();
                            CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
                        }
                    })
                });
            });
        })
    </script>
</body>
</html>
