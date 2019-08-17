<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>选择售后类型</title>
    <meta name="keywords" content="">
    <meta name="description" content="">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no, viewport-fit=cover">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/common.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/mine/after_sale_start.css">
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/velocity.min.js"></script>
</head>
<body>
<div class="wx_wrap" id="wrapBody">
    <div class="m_header" style="">
        <div class="m_header_bar">
            <div data-url="${JZXUrl(Request)}/wx/mine/after_sale.jhtml" class="m_header_bar_back"></div>
            <div class="m_header_bar_title">选择售后类型</div>
        </div>
        <div id="m_common_header_key" class="jzx-header-icon-new-shortcut">
            <span></span>
        </div>
    </div>

    <#include "../include/header_shortcut.ftl"/>

    <#if orderGoods??>
        <div class="react-root homePageBody" goods-amount="${orderGoods['amount']}">
            <div class="react-view goods">
                <div class="react-view gt">
                    <div class="react-view img">
                        <img src="${JZXFile(Request,orderGoods['fileName'])}"/>
                    </div>
                    <div class="react-view gtn">
                        <span>${orderGoods['goodsName']}</span>
                        <div class="react-view amount">
                            <span class="gtda">
                                <span class="gtda_label">单价:</span>
                                ¥${orderGoods['unitPrice']}
                            </span>

                            <span class="gtda ba">
                            <span class="gtda_label">购买数量:</span>
                            ${orderGoods['amount']}
                        </span>
                        </div>
                    </div>
                </div>

                <div class="react-view edit_amount">
                    <span class="ea_label">申请数量</span>
                    <div class="react-view edit_tool">
                        <div class="react-view sub">
                            <div class="react-view icon"></div>
                        </div>
                        <div class="react-view input">
                            <input type="number" value="1"/>
                        </div>
                        <div class="react-view add">
                            <div class="react-view icon"></div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="react-view as_type">
                <div class="react-view item"
                     data-url2="${JZXUrl(Request)}/wx/mine/after_sale_form.jhtml?type=1&og=${orderGoods['id']}">
                    <div class="disable_mask <#if supports['SALES_RETURN']==1>show</#if>"></div>
                    <div class="react-view name">
                        <div class="react-view img">
                            <img src="${JZXUrl(Request)}/wx/images/icons/tuiHuo-icon.png"/>
                        </div>
                        <span class="text">退货</span>
                    </div>

                    <div class="react-view detail">
                        <div class="react-view">
                            <span class="detail_span">退回收到的商品</span>
                        </div>
                        <div class="react-view">
                            <div class="react-view icon"></div>
                        </div>
                    </div>
                </div>
                <div class="react-view split"></div>
                <div class="react-view item"
                     data-url2="${JZXUrl(Request)}/wx/mine/after_sale_form.jhtml?type=2&og=${orderGoods['id']}">
                    <div class="disable_mask <#if supports['EXCHANGE_GOODS']==1>show</#if>"></div>
                    <div class="react-view name">
                        <div class="react-view img">
                            <img src="${JZXUrl(Request)}/wx/images/icons/huanHuo-icon.png"/>
                        </div>
                        <span class="text">换货</span>
                    </div>

                    <div class="react-view detail">
                        <div class="react-view">
                            <span class="detail_span">更换收到的商品</span>
                        </div>
                        <div class="react-view">
                            <div class="react-view icon"></div>
                        </div>
                    </div>
                </div>
                <div class="react-view split"></div>
                <div class="react-view item"
                     data-url2="${JZXUrl(Request)}/wx/mine/after_sale_form.jhtml?type=3&og=${orderGoods['id']}">
                    <div class="disable_mask <#if supports['MAINTAIN']==1>show</#if>"></div>
                    <div class="react-view name">
                        <div class="react-view img">
                            <img src="${JZXUrl(Request)}/wx/images/icons/weiXiu-icon.png"/>
                        </div>
                        <span class="text">维修</span>
                    </div>

                    <div class="react-view detail">
                        <div class="react-view">
                            <span class="detail_span">维修收到的商品</span>
                        </div>
                        <div class="react-view">
                            <div class="react-view icon"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </#if>
</div>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>
<script type="text/javascript">
    $(function () {
        var amount = $('.homePageBody').attr('goods-amount');
        $('.disable_mask').click(function (e) {
            e.stopPropagation();
        });

        function setIcons(amount) {
            var val = $('.homePageBody .edit_amount .input input').val();
            $('.homePageBody .edit_amount .add').removeClass('dark');
            $('.homePageBody .edit_amount .sub').removeClass('dark');
            if (parseInt(amount) > parseInt(val)) {
                $('.homePageBody .edit_amount .add').addClass('dark');
            }
            if (parseInt(val) > 1) {
                $('.homePageBody .edit_amount .sub').addClass('dark');
            }
        }

        setIcons(amount);


        $('.homePageBody .edit_amount .add').click(function () {
            var val = $('.homePageBody .edit_amount .input input').val();
            if (parseInt(val) < parseInt(amount)) {
                $('.homePageBody .edit_amount .input input').val(parseInt(val) + 1);
            }
            setIcons(amount);
        });
        $('.homePageBody .edit_amount .sub').click(function () {
            var val = $('.homePageBody .edit_amount .input input').val();
            if (parseInt(val) > 1) {
                $('.homePageBody .edit_amount .input input').val(parseInt(val) - 1);
            }
            setIcons(amount);
        });

        $('.homePageBody .edit_amount .input input').change(function () {
            var val = $('.homePageBody .edit_amount .input input').val();
            if (parseInt(amount) < parseInt(val)) {
                $('.homePageBody .edit_amount .input input').val(amount)
            }
            if (parseInt(val) <= 0) {
                $('.homePageBody .edit_amount .input input').val(1)
            }
        });

        $('.homePageBody').find('*[data-url2]').click(function () {
            var val = $('.homePageBody .edit_amount .input input').val();
            var url = $(this).attr('data-url2');
            window.location.href = url + "&amount=" + val;
        })
    })
</script>
</body>
</html>
