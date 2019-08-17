$(function () {
    var winWidth = $(window).width();
    var winHeight = $(window).height();

    {
        // 计算轮播图高宽
        var imgWidth = winWidth < 640 ? winWidth : 640;
        $('#loopImgDiv').width(imgWidth);
        $('#loopImgDiv').height(imgWidth);
        $('#loopImgUl').width(imgWidth * parseInt($('#loopImgUl li').length));
        $('#loopImgUl').height(imgWidth);
        $('#loopImgUl').css({left: '0px'});
        $('#loopImgUl li').width(imgWidth);
        $('#loopImgUl li').height(imgWidth);
        $('#loopImgUl li').css({
            transition: 'all 300ms ease 0s',
            transform: 'translate3d(0px, 0px, 0px)',
            zIndex: 10
        });
        // 左右滑动图
        $('#loopImgDiv').on('touchstart', function (e1) {
            var touch = e1.originalEvent.targetTouches[0];
            var startX = touch.pageX;
            var startLeft = parseInt($('#loopImgUl').css('left'));

            $('#loopImgDiv').on('touchmove', function (e2) {
                var touch = e2.originalEvent.targetTouches[0];
                var x = touch.pageX;
                var left = startLeft + (x - startX);
                $('#loopImgUl').css("left", left + "px");
            });

            $('#loopImgDiv').on('touchend', function (e3) {
                var count = $('#loopImgUl li').length;
                var endLeft = parseInt($('#loopImgUl').css('left'));
                var diffLeft = endLeft - startLeft;
                var idx = Math.round(startLeft / imgWidth);
                var nidx = idx;
                if (diffLeft > 0) nidx = idx + 1;
                if (diffLeft < 0) nidx = idx - 1;
                if (nidx <= -count) nidx = -count + 1;
                if (nidx >= 0) nidx = 0;
                if (imgWidth * 0.3 <= Math.abs(diffLeft)) {
                    $('#loopImgUl').animate({left: imgWidth * nidx + "px"}, 500);
                } else {
                    $('#loopImgUl').animate({left: imgWidth * idx + "px"}, 500);
                }

                $('#loopImgBar li').removeClass('cur');
                $($('#loopImgBar li')[Math.abs(nidx)]).addClass('cur');

                $('#loopImgDiv').off('touchmove');
                $('#loopImgDiv').off('touchend');
            });
        });
    }

    {
        // 监控滚动条
        $(window).scroll(function (e) {
            var commentOffset = $('.detail_extra').offset();
            var detailOffset = $('.mod_fix_wrap').offset();
            if (e.currentTarget.scrollY >= (commentOffset.top - winHeight / 2)) {
                $('#detailAnchor .detail_anchor_item').removeClass('cur');
                $('#detailAnchor .detail_anchor_item[dtype=comment]').addClass('cur');
            }
            if (e.currentTarget.scrollY < (commentOffset.top - winHeight / 2)) {
                $('#detailAnchor .detail_anchor_item').removeClass('cur');
                $('#detailAnchor .detail_anchor_item[dtype=item]').addClass('cur');
            }

            if (e.currentTarget.scrollY >= (detailOffset.top - winHeight / 2)) {
                $('#detailAnchor .detail_anchor_item').removeClass('cur');
                $('#detailAnchor .detail_anchor_item[dtype=detail]').addClass('cur');
            }
            if (e.currentTarget.scrollY >= (commentOffset.top - winHeight / 2) && e.currentTarget.scrollY < (detailOffset.top - winHeight / 2)) {
                $('#detailAnchor .detail_anchor_item').removeClass('cur');
                $('#detailAnchor .detail_anchor_item[dtype=comment]').addClass('cur');
            }
        });

        $('#detailAnchor .detail_anchor_item[dtype=item]').click(function () {
            $(window).scrollTop(0);
        });
        $('#detailAnchor .detail_anchor_item[dtype=comment]').click(function () {
            var commentOffset = $('.detail_extra').offset();
            $(window).scrollTop(commentOffset.top - $('#m_header').height());
        });
        $('#detailAnchor .detail_anchor_item[dtype=detail]').click(function () {
            var detailOffset = $('.mod_fix_wrap').offset();
            $(window).scrollTop(detailOffset.top - $('#m_header').height() + 3);
        });

        $('#goTop').click(function () {
            $(window).scrollTop(0);
        });
    }

    {
        // 商品详情切换
        $('#detailTab .mod_tab .item').click(function () {
            $('#detailTab .mod_tab .item').removeClass('cur');
            $(this).addClass('cur');
            var no = $(this).attr('no');
            $('#detailCont').css("transform", "translate3d(-" + (parseInt(no) - 1) * 100 + "vw, 0px, 0px)");
            $('#detailCont').height($('#detail' + no).height());
        });
    }

    {

        function addCartAnim(buyNum) {
            $('#popone').html("+" + buyNum);
            $('#popone').show();
            $('#popone').animate({top: "-30px", opacity: 0}, 300, function () {
                $('#popone').hide();
                $('#popone').css({top: "-10px", opacity: 1});
            });
        }

        // 加入购物车
        $('#popupConfirm').click(function () {
            if ($('#popupConfirm').parent().data('type') == 'add_cart') {
                var btnType = $(this).attr('btn-type');
                var gid = $('#part_main').attr('data-id');
                var skuId = $('#popupBuyArea').data('skuId');
                var buyNum = $('#buyNum1').val();
                if (skuId) {
                    buyNum = buyNum || 1;
                    $('#popupBuyArea').removeClass('show');
                    if (btnType == '2') {
                        addCartAnim(buyNum);
                        $.ajax({
                            url: $('#m_header').attr('cart-url'),
                            data: {gid: gid, skuId: skuId, amount: buyNum},
                            success: function () {

                            },
                            error: function () {

                            }
                        })
                    }
                    $('#popupConfirm').parent().hide();
                    console.log(gid, skuId, buyNum);
                    if (btnType == '1') {
                        window.location.href = $('#part_main').attr('buy-now-url')
                            + "?goods=" + gid + "," + skuId + "," + buyNum;
                    }

                } else {
                    $('#priceSale2').shake(2, 5, 300);
                }
            } else {
                $('#popupBuyArea').removeClass('show');
            }
        });
        $('#addCart').click(function () {
            var hasSku = $('#part_main').attr('data-sku');
            $('#popupConfirm').parent().show();
            $('#popupConfirm').parent().data('type', 'add_cart');
            if (hasSku == '1') {
                $('#popupBuyArea').toggleClass('show');
                $('#popupConfirm').attr('btn-type', '2')
            } else {
                var gid = $('#part_main').attr('data-id');
                var buyNum = $('#buyNum1').val();
                buyNum = buyNum || 1;
                addCartAnim(buyNum);
                $.ajax({
                    url: $('#m_header').attr('cart-url'),
                    data: {gid: gid, skuId: 0, amount: buyNum},
                    success: function () {

                    },
                    error: function () {

                    }
                })
            }
        });

        $('#buyBtn').click(function () {
            var gid = $('#part_main').attr('data-id');
            var hasSku = $('#part_main').attr('data-sku');
            var buyNum = $('#buyNum1').val();
            $('#popupConfirm').parent().data('type', 'add_cart');
            $('#popupConfirm').parent().show();
            if (hasSku == '1') {
                $('#popupBuyArea').toggleClass('show');
                $('#popupConfirm').attr('btn-type', '1')
            } else {
                buyNum = buyNum || 1;
                window.location.href = $('#part_main').attr('buy-now-url') + "?goods=" + gid + ",0," + buyNum;
            }
        });
    }
    {
        // 添加浏览记录
        var gid = $('#part_main').attr('data-id');
        $.ajax({
            url: 'see_history_goods.jhtml',
            data: {gid: gid},
            success: function (resp) {
            }
        });
    }

    {
        // 收藏关注
        function setPageFav(curr) {
            var gid = $('#part_main').attr('data-id');
            var skuId = $('#popupBuyArea').data('skuId');
            CommentUtils.wait();
            $.ajax({
                url: 'collect_goods.jhtml', data: {gid: gid, skuId: skuId}, success: function (resp) {
                    CommentUtils.closeWait();
                    if (resp && resp.trim() == 'ok') {
                        if (curr == $('#fav')[0]) {
                            $('#shareMod').addClass('show');
                        }
                        $('#fav').html('已关注');
                        $('#fav').addClass('yes');

                        $('#gotoFav .txt').html('已收藏');
                        $('#gotoFav').addClass('icon_fav_fit');
                        $('#gotoFav').removeClass('icon_fav');
                    }
                    if (resp && resp.trim() == 'not_login') {
                        CommentUtils.confirm('请先登录后再收藏', function () {
                            window.location.href = "login.jhtml?r=" + encodeURIComponent(window.location.href);
                        }, '立即登录')
                    }
                },
                error: function (xhr) {
                    CommentUtils.closeWait();
                    CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
                }
            });
        }

        function removePageFav() {
            var gid = $('#part_main').attr('data-id');
            CommentUtils.wait();
            $.ajax({
                url: 'rm_collect_goods.jhtml', data: {gid: gid}, success: function (resp) {
                    CommentUtils.closeWait();
                    if (resp && resp.trim() == 'ok') {
                        $('#fav').html('关注');
                        $('#fav').removeClass('yes');

                        $('#gotoFav .txt').html('收藏');
                        $('#gotoFav').addClass('icon_fav');
                        $('#gotoFav').removeClass('icon_fav_fit');
                    }
                    if (resp && resp.trim() == 'not_login') {
                        CommentUtils.confirm('请先登录后再取消收藏', function () {
                            window.location.href = "login.jhtml?r=" + encodeURIComponent(window.location.href);
                        }, '立即登录')
                    }
                },
                error: function (xhr) {
                    CommentUtils.closeWait();
                    CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
                }
            });
        }

        $('#fav').click(function () {
            if (!$(this).hasClass('yes')) {
                setPageFav(this);
            } else {
                removePageFav();
            }
        });
        $('#shareMod .close').click(function (e) {
            e.stopPropagation();
            $('#shareMod').removeClass('show');
        });
        $('#shareMod').click(function () {
            $('#shareMod').removeClass('show');
        });
        $('#gotoFav').click(function () {
            if (!$(this).hasClass('icon_fav_fit')) {
                setPageFav();
            } else {
                removePageFav();
            }
        });
    }

    {
        // 优惠券弹框
        $('#couponListDiv').click(function () {
            $('#popupDom').toggleClass('show');
        });
        $('#popupDom').click(function () {
            $('#popupDom').toggleClass('show');
        });
        $('#popupDom .header .close').click(function (e) {
            $('#popupDom').toggleClass('show');
            e.stopPropagation();
        });
        $('#popupDom .coupon_voucher3_info_btn').click(function (e) {
            e.stopPropagation();
            var self = this;
            var cid = $(this).attr('data-id');
            if (!$(this).hasClass('disabled')) {
                $.ajax({
                    url: $('#popupDom').attr('url'),
                    data: {cid: cid},
                    success: function (data) {
                        if (data == 'ok') {
                            $(self).html('已领取');
                            $(self).addClass('disabled');
                            CommentUtils.alert('', '当前优惠券领取成功');
                        } else if (data == 'channel_error') {
                            CommentUtils.alert('', '该渠道不允许获取当前优惠券');
                        } else if (data == 'coupon_empty') {
                            CommentUtils.alert('', '当前优惠券已领完');
                        } else if (data == 'out_count') {
                            CommentUtils.alert('', '每人限领' + $(self).attr('data-count') + '张');
                        } else if (data == 'user_level_low') {
                            CommentUtils.alert('', '当前用户等级无法领取优惠券');
                        } else if (data == 'coupon_expire') {
                            CommentUtils.alert('', '当前优惠券已过期');
                        } else {
                            CommentUtils.alert('', '服务器错误');
                        }
                    },
                    error: function (xhr) {
                        CommentUtils.closeWait();
                        CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
                    }
                })
            }
        });
    }

    {
        // 促销弹框
        $('#promoteChoice').click(function () {
            $('#promotePopup').toggleClass('show');
        });
        $('#promotePopup .header .close').click(function (e) {
            $('#promotePopup').toggleClass('show');
            e.stopPropagation();
        });
        $('#promotePopup').click(function () {
            $('#promotePopup').toggleClass('show');
        });
        $('#promotePopup .body').click(function (e) {
            e.stopPropagation();
        });
    }

    {
        // 商品规格弹框
        $('#skuWindow').click(function () {
            $('#popupConfirm').parent().show();
            $('#popupConfirm').parent().data('type', 'sel');
            $('#popupBuyArea').toggleClass('show');
        });
        $('#popupBuyArea .header .close').click(function (e) {
            $('#popupBuyArea').toggleClass('show');
            e.stopPropagation();
        });
        $('#popupBuyArea').click(function () {
            $('#popupBuyArea').toggleClass('show');
        });
        $('#popupBuyArea .main').click(function (e) {
            e.stopPropagation();
        });

        function resetSkuChooseInfo() {
            var skuChooses = $('#popupSkuArea .sku_choose');
            var items = $('#popupSkuArea .sku_choose .item.active');
            var names = [];
            items.each(function (i, v) {
                names.push($(v).html());
            });
            if (names.length > 0 && names.length == skuChooses.length) {
                var count = $('#buyNum1').val();
                names.push(count + $('#buyNum1').attr('goodsUnit'));
                $('#skuChoose1').html(names.join(","));
                $('#skuChoose1Title').html('已选');
                $('#skuChoose1').removeClass('sku_choose_info_empty');
                $('#popupSkuChoose').html('<span>已选</span>' + names.join(","));
            }
        }

        function resetSkuPrice() {
            var skuChooses = $('#popupSkuArea .sku_choose');
            var items = $('#popupSkuArea .sku_choose .item.active');
            var ids = [];
            items.each(function (i, v) {
                ids.push($(v).attr('data-id'));
            });
            var skus = $('#popupBuyArea input[type=hidden]');
            skus.each(function (i, v) {
                var val = $(v).val();
                var skuId = $(v).attr('skuId');
                var s1 = val.split(",");
                var isMatch = true;
                if (skuChooses.length == ids.length) {
                    for (var j = 0; j < ids.length; j++) {
                        var isInList = false;
                        for (var k = 0; k < s1.length; k++) {
                            if (ids[j] == s1[k]) {
                                isInList = true;
                            }
                        }
                        if (!isInList) {
                            isMatch = false;
                        }
                    }
                } else {
                    isMatch = false;
                }
                if (isMatch) {
                    var price = $(v).attr('price');
                    var originalPrice = $(v).attr('originalPrice');
                    if (price) {
                        var s2 = price.split(".");
                        if (s2.length == 1) s2.push("00");
                        $('#priceSale').html('¥<em>' + s2[0] + '</em>.' + s2[1]);
                        $('#priceSale2').html('¥<em>' + s2[0] + '</em>.' + s2[1]);
                    }
                    if (parseFloat(price) === parseFloat(originalPrice)) {
                        $('#oldPriceSale').hide();
                        $('#oldPriceSale2').hide();
                    } else if (originalPrice) {
                        var s2 = originalPrice.split(".");
                        if (s2.length == 1) s2.push("00");
                        $('#oldPriceSale').html('¥<em>' + s2[0] + '</em>.' + s2[1]);
                        $('#oldPriceSale2').html('¥<em>' + s2[0] + '</em>.' + s2[1]);
                    }
                    $('#popupBuyArea').data('skuId', skuId);
                }
            });
        }

        $('#popupSkuArea .sku_choose .item').click(function (e) {
            $(this).parent().find('.item').removeClass('active');
            $(this).addClass('active');
            resetSkuChooseInfo();
            resetSkuPrice();
        });

        $('#minus1').click(function () {
            var count = parseInt($('#buyNum1').val());
            if (count > 1) {
                $('#buyNum1').val(count - 1);
                resetSkuChooseInfo();
            }
        });
        $('#plus1').click(function () {
            var count = parseInt($('#buyNum1').val());
            $('#buyNum1').val(count + 1);
            resetSkuChooseInfo();
        });
    }


    {
        // 商品评价相关代码
        var currScrollTop = 0;

        function openCommentDialog() {
            currScrollTop = $(window).scrollTop();
            $("#detailAnchor").hide();
            $("#part_summary").show();
            $("#goTop").hide();
            $("#part_main").hide();
        }

        function closeCommentDialog() {
            $(window).scrollTop(currScrollTop);
            $("#detailAnchor").show();
            $("#part_summary").hide();
            $("#goTop").show();
            $("#part_main").show();
        }

        $('#m_common_header_goback').click(function (e) {
            if (!$("#part_summary").is(':hidden')) {
                e.stopPropagation();
                closeCommentDialog();
            } else {
                // 返回上一页
            }
        });

        $('#summaryEnter,#summaryEnter3 .cmt_more_lnk').click(function (e) {
            e.stopPropagation();
            openCommentDialog();

            $('#part_summary').data('page', 1);
            loadComments(1);
        });

        $('.m_header_bar_close').click(function (e) {
            e.stopPropagation();
            closeCommentDialog();
        });

        // 打开评论图片
        $('.cmt_list_wrap .cmt_list .cmt_att .img').click(function () {
            var imgs = $('.cmt_list_wrap .cmt_list .cmt_att .img img');
            var items = [], index = 0;
            for (var i = 0; i < imgs.length; i++) {
                var img = imgs[i];
                items.push({src: img.getAttribute('prview')});
                if ($(this).find('img')[0] == img) {
                    index = i;
                }
            }

            var imgViewer = new ImgViewer(items, {index: index});
        });

        function renderGoodsComments(data, url) {
            var userName = data['userName'];
            var avatar = data['avatar'];
            var nick = data['TableUser']['nick'];
            if (!userName) userName = '匿名用户';
            if (!avatar) avatar = 'images/empty_header.png';

            if (nick) {
                nick = decodeURIComponent(nick);
            }

            var imgs = [];
            if (data['images']) {
                for (var i = 0; i < data['images'].length; i++) {
                    imgs.push('<span class="img"><img src="' + url + data['images'][i]['fileName'] + '"></span>');
                }
            }
            var skus = [];
            if (data['goodsSku']) {
                for (var i = 0; i < data['goodsSku'].length; i++) {
                    skus.push('<span>' + data['goodsSku'][i]['key'] + "：" + data['goodsSku'][i]['value'] + '</span>');
                }
            }

            var comment = '这个人很懒什么都没留下';
            if (data['comment']) comment = data['comment'];
            var html =
                '<li>' +
                '    <div class="cmt_user">' +
                '        <img src="' + avatar + '">' +
                '        <span class="user">' + (nick ? nick : userName) + '</span>' +
                '        <span class="credit star-' + data['score'] + '"><span></span></span>' +
                '        <span class="date">' + data['createTime'] + '</span>' +
                '    </div>' +
                '    <div class="cmt_cnt adjust">' + comment + '</div>' +
                '    <div class="cmt_att">' + imgs.join(',') + '</div>' +
                '    <div class="cmt_sku">' + skus.join('') + '</div>' +
                '</li>';
            return html;
        }

        function loadComments(page) {
            var cur = $('#cur').is(":checked");
            var gid = $('#part_main').attr('data-id');
            var skuId = $('#popupBuyArea').data('skuId');
            var type = $('#evalTag2 span.selected').attr('no');

            var data = {page: page};
            if (cur) {
                data['gid'] = gid;
                data['skuId'] = skuId;
            }
            data['type'] = type;

            if (page == 1) {
                $('#evalDet_summary').html('');
            }
            $('#part_summary').data('loading', true);
            $.ajax({
                url: $('#m_header').attr('comment-url'),
                dataType: 'json',
                data: data,
                success: function (data) {
                    $('#part_summary').data('loading', false);
                    $('.cmt_list_loading').hide();
                    if (data['success'] == 1) {
                        var url = data['url'];
                        var dts = data['data'];
                        if (dts && dts.length > 0) {
                            var htmls = [];
                            for (var i = 0; i < dts.length; i++) {
                                htmls.push(renderGoodsComments(dts[i], url));
                            }
                            $('#evalDet_summary').append(htmls.join(''));
                        } else {
                            $('#part_summary').data('loading', true)
                        }
                    } else {

                    }
                },
                error: function (xhr) {
                    $('#part_summary').data('loading', false);
                    $('.cmt_list_loading').hide();
                    CommentUtils.closeWait();
                    CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
                }
            })
        }

        $('#evalTag2 span').click(function () {
            $('#evalTag2 span').removeClass('selected');
            $(this).addClass('selected');

            $('#part_summary').data('page', 1);
            loadComments(1);
        });

        $('#cur').change(function () {
            $('#part_summary').data('page', 1);
            loadComments(1);
        });

        $('#part_summary').scroll(function () {
            var scrollTop = $('#part_summary').scrollTop();
            var top = scrollTop + $('#part_summary').height();
            var scrollHeight = $('#part_summary')[0].scrollHeight;
            if (Math.abs(top - scrollHeight) <= 1) {
                if (!$('#part_summary').data('loading')) {
                    $('.cmt_list_loading').show();
                    var page = parseInt($('#part_summary').data('page')) + 1;
                    $('#part_summary').data('page', page);
                    loadComments(page);
                }
            }
        });
    }
});
