<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>商品评价</title>
    <meta name="keywords" content="">
    <meta name="description" content="">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no, viewport-fit=cover">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/common.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/mine/goods_comment.css">
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/velocity.min.js"></script>
</head>
<body>
<div class="wx_wrap" id="wrapBody">
    <div class="m_header" style="">
        <div class="m_header_bar">
            <div class="m_header_bar_back"></div>
            <div class="m_header_bar_title">商品评价</div>
        </div>
        <div id="m_common_header_key" class="jzx-header-icon-new-shortcut">
            <span></span>
        </div>
    </div>

    <#include "../include/header_shortcut.ftl"/>

    <div class="wrapper" orderGoodsId="${orderGoods['id']}">
        <div class="comment_detail">
            <div class="comment_item has-border">
                <div class="comment_item_product">
                    <img class="image ll_fadeIn"
                         src="${JZXFile(Request,orderGoods['fileName'])}"
                         loaded="1" style="opacity: 1;">
                    <span class="label">商品评分</span>
                    <div class="stars">
                        <i data-score="1" class=""></i>
                        <i data-score="2" class=""></i>
                        <i data-score="3" class=""></i>
                        <i data-score="4" class=""></i>
                        <i data-score="5" class=""></i>
                    </div>
                </div>
                <p class="comment-text" style="display: none;"></p>
            </div>
            <div class="comment_textarea">
                <span class="label">心得：</span>
                <div class="comment_textarea">
                    <div class="textarea_wrap">
                        <textarea placeholder="请输入您的商品评价，最大支持500个字符"
                                  maxlength="500"></textarea>
                        <span>0/500</span>
                    </div>
                </div>
            </div>
            <div class="comment_images" store="[object Object]">
                <span class="label">添加图片评论<small>(0/9)</small></span>
                <ul class="images">
                    <li class="type_bg">
                        <a href="javascript:;" class="btn_add">
                            <p>添加图片</p>
                            <input type="file" accept="image/*"
                                   multiple="multiple"
                                   style="position: absolute; top: 0px; left: 0px; width: 75px; height: 75px; opacity: 0;">
                        </a>
                    </li>
                </ul>
            </div>
            <div class="comment_gwq">
                <span class="select">匿名发布</span>
            </div>
        </div>

        <div class="comment_action">
            <div class="comment_action_tit">
                <span></span>
                <div class="comment_action_tit_txt">
                    <p>购物服务评价</p>
                </div>
            </div>
            <ul class="stars_list type_margin">
                <li>
                    <span>物流服务</span>
                    <div class="stars" data-type="1">
                        <i data-score="1" class=""></i>
                        <i data-score="2" class=""></i>
                        <i data-score="3" class=""></i>
                        <i data-score="4" class=""></i>
                        <i data-score="5" class=""></i>
                    </div>
                </li>
                <li>
                    <span>发货速度</span>
                    <div class="stars" data-type="2">
                        <i data-score="1" class=""></i>
                        <i data-score="2" class=""></i>
                        <i data-score="3" class=""></i>
                        <i data-score="4" class=""></i>
                        <i data-score="5" class=""></i>
                    </div>
                </li>
                <li>
                    <span>服务态度</span>
                    <div class="stars" data-type="3">
                        <i data-score="1" class=""></i>
                        <i data-score="2" class=""></i>
                        <i data-score="3" class=""></i>
                        <i data-score="4" class=""></i>
                        <i data-score="5" class=""></i>
                    </div>
                </li>
            </ul>
        </div>

        <div class="comment_btns">
            <a href="javascript:;" class="comment_btn">提交</a>
        </div>
    </div>
</div>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>
<script type="text/javascript">
    $(function () {
        $('.comment_action .stars_list .stars i,.comment_item_product .stars i').click(function () {
            var score = $(this).attr('data-score');
            score = parseInt(score);
            $(this).parent().find('i').removeClass('cur');
            for (var i = 1; i <= score; i++) {
                $(this).parent().find('i[data-score="' + i + '"]').addClass('cur');
            }
        });

        $('.comment_textarea textarea').keyup(function () {
            var val = $(this).val();
            $('.comment_textarea .textarea_wrap span').html(val.length + "/500");
        });

        $('.comment_gwq span').click(function () {
            $('.comment_gwq span').toggleClass('selected');
        });

        $('.images .type_bg .btn_add').click(function () {
            $('.images .type_bg .btn_add input').click();
        });
        $('.images .type_bg .btn_add input').change(function () {
            var files = this.files;
            var count = $('.comment_images .images li');

            for (var i = 0; i < files.length; i++) {
                var file = files[i],
                    thisType = file.type,//获取到表面的名称，可判断文件类型
                    thisSize = file.size,//文件的大小
                    thisSrc = URL.createObjectURL(file),//当前对象的地址
                    img = $(
                        '<li class="type_video">' +
                        '    <img src="">' +
                        '    <span></span>' +
                        '</li>');
                img.data('file', file);
                img.find('img').attr('src', thisSrc);//创建img对象
                $('.comment_images .images .type_bg').before(img);
                //文件加载成功以后，渲染到页面
                img.load(function () {
                    URL.revokeObjectURL(thisSrc);//释放内存
                });
                img.find('span').click(function () {
                    $(this).parent().remove();
                });
                if ((count.length + i) >= 9) break;
            }
        });

        $('.comment_btns .comment_btn').click(function () {
            var files = $('.comment_images .images li');
            var formData = new FormData();
            if (files.length > 0) {
                for (var i = 0; i < files.length; i++) {
                    formData.append("file" + i, $(files[i]).data('file'));
                }
            }

            if (scoreCount == 0) {
                CommentUtils.alert('缺少参数', '请选择商品评价分数');
                return false;
            }

            var scoreCount = $('.comment_item_product .stars i.cur');
            formData.append("score", scoreCount.length);
            var logisticsScore = $('.comment_action .stars_list .stars[data-type="1"] i.cur');
            formData.append("logisticsScore", logisticsScore.length);
            var speedScore = $('.comment_action .stars_list .stars[data-type="2"] i.cur');
            formData.append("speedScore", speedScore.length);
            var serviceScore = $('.comment_action .stars_list .stars[data-type="3"] i.cur');
            formData.append("serviceScore", serviceScore.length);

            formData.append("comment", $('.comment_textarea textarea').val());
            formData.append("anonymity", $('.comment_gwq span').hasClass('selected') ? 1 : 0);
            formData.append("og", $('.wrapper').attr('orderGoodsId'));

            CommentUtils.confirm('确定提交商品评价吗？', function () {
                CommentUtils.wait('正在提交...');
                $.ajax({
                    url: "${JZXUrl(Request)}/wx/mine/goods_comment_submit.jhtml",
                    type: "POST",
                    dataType: 'json',
                    data: formData,
                    processData: false,
                    contentType: false,
                    success: function (data) {
                        CommentUtils.closeWait();
                        if (data['success'] == 1) {
                            window.history.go(-1);
                        } else {
                            if (data['code'] == 'not_login') {
                                CommentUtils.alert('', '您已经退出登录！');
                            } else {
                                CommentUtils.alert('', '提交商品评价失败');
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
    })
</script>
</body>
</html>
