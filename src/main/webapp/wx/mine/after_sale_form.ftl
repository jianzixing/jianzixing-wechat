<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>提交售后</title>
    <meta name="keywords" content="">
    <meta name="description" content="">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no, viewport-fit=cover">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/common.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/mine/after_sale_form.css">
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/velocity.min.js"></script>
</head>
<body>
<div class="wx_wrap" id="wrapBody">
    <div class="m_header" style="">
        <div class="m_header_bar">
            <div class="m_header_bar_back"></div>
            <div class="m_header_bar_title">
                <#if type?? && type==1>
                    退货
                <#elseif type?? && type ==2>
                    换货
                <#elseif type?? && type == 3>
                    维修
                <#else>
                    提交售后
                </#if>
            </div>
        </div>
        <div id="m_common_header_key" class="jzx-header-icon-new-shortcut">
            <span></span>
        </div>
    </div>

    <#include "../include/header_shortcut.ftl"/>

    <#if applyForAmount lte 0>
        <div class="no_more_data">申请数量必须大于1且小于购买数量</div>
    <#elseif !orderGoods??>
        <div class="no_more_data">申请售后商品不能为空</div>
    </#if>

    <#if orderGoods?? && applyForAmount gt 0>
        <div class="react-root homePageBody">
            <div class="react-view goods">
                <div class="react-view gt">
                    <div class="react-view img">
                        <img src="${JZXFile(Request,orderGoods['fileName'])}">
                    </div>
                    <div class="react-view gtn">
                        <span>${orderGoods['goodsName']}</span>
                        <div class="react-view amount">
                        <span class="gtda">
                            <span class="gtda_label">单价:</span>
                            ¥${orderGoods['unitPrice']}
                        </span>

                            <span class="gtda">
                            <span class="gtda_label">购买数量:</span>
                            ${orderGoods['amount']}
                        </span>

                            <span class="gtda">
                            <span class="gtda_label">申请数量:</span>
                            ${applyForAmount}
                        </span>
                        </div>
                    </div>
                </div>
            </div>

            <div class="react-view as_type">
                <div class="react-view reason">
                    <div class="react-view title">
                        <div class="react-view">
                            <span class="left">申请原因</span>
                        </div>
                        <div class="react-view right">
                            <span class="value">其他</span>
                            <div class="react-view">
                                <div class="react-view icon"></div>
                            </div>
                        </div>
                    </div>
                    <span class="detail">请您描述问题并上传收到商品照片</span>
                </div>

                <div class="react-view problem">
                    <span class="detail">问题描述</span>
                    <div class="react-view editor">
                        <textarea maxlength="500" placeholder="请描述申请售后服务的具体原因，除了文字您还可上传最多5张图片哦~"></textarea>
                        <div class="react-view word_tips">
                        <span class="df">
                            <span class="has">0</span>
                            /500
                        </span>
                        </div>
                        <div class="react-view pics">
                            <div class="react-view inner"></div>
                        </div>
                    </div>
                    <div class="react-view pic_tool">
                        <div class="react-view icon">
                            <img src="${JZXUrl(Request)}/wx/images/icons/uploadImgEn-icon.png"/>
                        </div>
                        <input style="display: none" type="file" name="file" accept="image/*" multiple="multiple"/>
                    </div>
                </div>

                <div class="react-view delivery">
                    <div class="react-view" style="padding-bottom: 15px;">
                        <div class="react-view title">
                            <span class="name">退货方式</span>
                            <div class="react-view dl">
                                <div class="react-view btn cur">
                                    <span>快递至卖家</span>
                                </div>
                            </div>
                        </div>
                        <div class="react-view tips">
                            <span>发快递需您自行承担物流费用，审核通过后售后详情中有寄回卖家的地址，然后请将商品发给卖家并填写物流单号。</span>
                        </div>
                    </div>

                    <#if type==2 || type==3>
                        <div class="react-view split"></div>
                        <div class="react-view">
                            <div class="react-view title">
                                <span class="name">收货信息<span class="nd">&nbsp;（该地址是卖家回寄给您的地址）</span></span>
                            </div>

                            <div class="react-view form">
                                <span class="label" type="Normal">联系人</span>
                                <div class="react-view value">
                                    <input maxlength="20" type="text" value="${address['realName']}"
                                           placeholder="请输入收货人姓名">
                                </div>
                            </div>
                            <div class="react-view form">
                                <span class="label" type="Normal">联系电话</span>
                                <div class="react-view value">
                                    <input maxlength="20" value="${address['phoneNumber']}"
                                           placeholder="请输入收货人电话号码">
                                </div>
                            </div>
                            <div class="react-view form" style="border-width: 0px">
                                <span class="label" type="Normal">收货地址</span>
                                <div class="react-view value">
                                    <textarea maxlength="500" type="text"
                                              placeholder="请输入收货人地址">${address['province']}${address['city']}${address['county']}${address['address']}</textarea>
                                </div>
                            </div>
                        </div>
                    </#if>
                </div>


                <div class="react-view submit_tool">
                    <div class="react-view s1">
                        <span type="Normal">&nbsp;</span>
                    </div>
                    <div class="react-view" style="background-color: transparent; cursor: pointer;">
                        <div class="react-view">
                            <div class="react-view serviceInfo-linear-gradient-red s2">
                                <span type="Normal">提交</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="myreact-pop-overlay"></div>
            <div class="myreact-popup-content" style="max-height: 1000px;">
                <div class="react-view">
                    <div class="react-view reason_list">
                        <div class="react-view reason_title">
                            <div class="react-view reason_title_f1">
                                <span type="Normal" class="reason_title_f2">&nbsp;</span>
                            </div>
                            <div class="react-view reason_title_f3">
                                <span type="Normal" class="reason_title_f4">申请原因</span>
                            </div>
                            <div class="react-view reason_title_f5">
                                <div class="react-view">
                                    <div class="react-view reason_title_option">
                                        <img class="close_img" src="../images/icon-close.png"/>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="react-view scroll-view hide-vertical-indicator react_content">
                            <div class="react-view scroll-content-container" style="width: 100%;">
                                <div class="react-view">
                                    <div class="react-view react_item_1">
                                        <div class="react-view">
                                            <div class="react-view react_item_2">
                                                <span type="Normal" class="react_item_3">商品故障</span>
                                                <div class="react-view">
                                                    <img src="../images/selectButton-default.png"
                                                         style="height: 19px; width: 19px;"></div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="react-view react_item_1">
                                        <div class="react-view">
                                            <div class="react-view react_item_2">
                                                <span type="Normal" class="react_item_3">其他</span>
                                                <div class="react-view">
                                                    <img src="../images/selectButton-default.png"
                                                         style="height: 19px; width: 19px;"></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
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
        $('.as_type .reason').click(function () {
            $('.myreact-pop-overlay').addClass('myreact-popup-overlay-show');
            $('.myreact-popup-content').addClass('myreact-popup-content-show');
        });
        $('.myreact-popup-content .reason_title_option').click(function () {
            $('.myreact-pop-overlay').removeClass('myreact-popup-overlay-show');
            $('.myreact-popup-content').removeClass('myreact-popup-content-show');
        });
        $('.myreact-pop-overlay').click(function () {
            $('.myreact-pop-overlay').removeClass('myreact-popup-overlay-show');
            $('.myreact-popup-content').removeClass('myreact-popup-content-show');
        });

        $('.myreact-popup-content .react_item_1').click(function () {
            var html = $(this).find('.react_item_3').html();
            $('.as_type .reason .value').html(html);
            $('.myreact-popup-content .react_item_1 img').attr('src', '../images/selectButton-default.png');
            $(this).find('img').attr('src', '../images/selectButton-selected.png');

            $('.myreact-pop-overlay').removeClass('myreact-popup-overlay-show');
            $('.myreact-popup-content').removeClass('myreact-popup-content-show');
        });

        $('.as_type .problem textarea').keyup(function () {
            var val = $('.as_type .problem textarea').val();
            $('.word_tips .df .has').html(val.length);
        });

        {
            function setDelPicEvent() {
                $('.as_type .editor .pics .inner .item .close').click(function () {
                    $(this).parent().remove();
                });
            }

            var input = $('.as_type .problem .pic_tool').find('input[name="file"]');
            input.on('change', function (e) {
                var count = $('.as_type .editor .pics .inner .item');
                if (count.length >= 5) {
                    CommentUtils.alert('图片最多只能上传5张');
                    return false;
                }
                var files = e.target.files;//拿到原始对象
                for (var i = 0; i < files.length; i++) {
                    var file = files[i],
                        thisType = file.type,//获取到表面的名称，可判断文件类型
                        thisSize = file.size,//文件的大小
                        thisSrc = URL.createObjectURL(file),//当前对象的地址
                        img = $('<div class="react-view item">' +
                            '    <div class="react-view">' +
                            '        <img class="cnt">' +
                            '    </div>' +
                            '    <div class="react-view close">' +
                            '        <img src="../images/icons/delete-image.png">' +
                            '    </div>' +
                            '</div>');
                    img.data('file', file);
                    img.find('.cnt').attr('src', thisSrc);//创建img对象
                    $('.as_type .editor .pics .inner').append(img);
                    //文件加载成功以后，渲染到页面
                    img.load(function () {
                        URL.revokeObjectURL(thisSrc);//释放内存
                    });
                    if ((count.length + i + 1) >= 5) break;
                }

                setDelPicEvent();
            });
            $('.as_type .problem .pic_tool .icon').click(function () {
                var count = $('.as_type .editor .pics .inner .item');
                if (count.length >= 5) {
                    CommentUtils.alert('上传图片限制', '图片最多只能上传5张');
                    return false;
                }
                input.trigger('click');
            });
        }

        {
            var url = '${JZXUrl(Request)}/wx/mine/after_sale_form_submit.jhtml';
            $('.serviceInfo-linear-gradient-red').click(function () {
                var type = '${type}';
                var og = '${orderGoods['id']}';
                var imgItems = $('.as_type .editor .pics .inner .item');
                var reason = $('.as_type .reason .value').html();
                var detail = $('.as_type .problem textarea').val();
                var files = [];
                for (var i = 0; i < imgItems.length; i++) {
                    files.push($(imgItems[i]).data('file'));
                }
                var formData = new FormData();
                formData.append("type", type);
                formData.append("og", og);
                formData.append("amount", '${applyForAmount}');
                if (files) {
                    for (var i = 0; i < files.length; i++) {
                        formData.append("file" + i, files[i]);
                    }
                }
                formData.append("reason", reason);
                formData.append("detail", detail);
                if (!detail || detail.length < 10) {
                    CommentUtils.alert('缺少描述', '问题描述必须填写且大于10个文字');
                    return false;
                }

                CommentUtils.confirm('确定信息无误并提交？', function () {
                    CommentUtils.wait('正在提交...');
                    $.ajax({
                        url: url,
                        type: "POST",
                        dataType: 'json',
                        data: formData,
                        processData: false,
                        contentType: false,
                        success: function (data) {
                            CommentUtils.closeWait();
                            if (data['success'] == 1) {
                                window.location.href = '${JZXUrl(Request)}/wx/mine/after_sale_detail.jhtml?n=' + data['number'];
                            } else {
                                if (data['code'] == 'status_error_nf') {
                                    CommentUtils.alert('', '您当前还有未完成的售后单');
                                } else if (data['code'] == 'not_support_type') {
                                    CommentUtils.alert('', '不支持的售后类型');
                                } else if (data['code'] == 'status_error_f') {
                                    CommentUtils.alert('', '当前订单商品已经退货');
                                } else if (data['code'] == 'order_not_exist') {
                                    CommentUtils.alert('', '售后订单不存在');
                                } else if (data['code'] == 'user_not_you') {
                                    CommentUtils.alert('', '售后订单必须本人提交');
                                } else if (data['code'] == 'order_goods_empty') {
                                    CommentUtils.alert('', '售后商品未找到');
                                } else if (data['code'] == 'order_goods_not_rel') {
                                    CommentUtils.alert('', '售后商品不在当前订单中');
                                } else if (data['code'] == 'not_found_order_goods') {
                                    CommentUtils.alert('', '不存在的订单商品');
                                } else if (data['code'] == 'after_sale_amount') {
                                    CommentUtils.alert('', '售后商品数量必须大于零');
                                } else {
                                    CommentUtils.alert('', '提交售后申请失败');
                                }
                            }
                        },
                        error: function (xhr) {
                            CommentUtils.closeWait();
                            CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
                        }
                    });
                }, '确认提交');
            });
        }
    })
</script>
</body>
</html>
