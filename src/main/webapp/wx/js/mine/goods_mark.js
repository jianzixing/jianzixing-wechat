$(function () {
    var pageType = $('#wrapBody').attr('page-type');
    $('#edit_btn').click(function () {
        if ($('#edit_btn').attr('sel') == '1') {
            $('#edit_btn').html('编辑');
            $('#edit_btn').attr('sel', '0');
            $('#favlist').removeClass('fav_items_edit');
            $('.fav_fixbar').hide();
            $('.fav_item .fav_select').hide();
        } else {
            $('#edit_btn').html('完成');
            $('#edit_btn').attr('sel', '1');
            $('#favlist').addClass('fav_items_edit');
            $('.fav_fixbar').show();
            $('.fav_item .fav_select').show();
        }
    });

    $('#selectAllBtn').click(function () {
        if ($(this).hasClass('selected')) {
            $('.fav_item .fav_select').removeClass('selected');
            $(this).removeClass('selected');
        } else {
            $('.fav_item .fav_select').addClass('selected');
            $(this).addClass('selected');
        }
    });

    function setSelectEvent() {
        $('.fav_item .fav_select').click(function () {
            if ($(this).hasClass('selected')) {
                $(this).removeClass('selected');
            } else {
                $(this).addClass('selected');
            }
        });
    }


    $('#multiCancle').click(function () {
        var gidEls = $('#favlist .fav_item .fav_select.selected');
        var $self = gidEls;
        var gids = [];
        if (gidEls) {
            for (var i = 0; i < gidEls.length; i++) {
                gids.push($(gidEls[i]).parent().attr('gid'));
            }
        }

        if (pageType == 'history') {
            CommentUtils.confirm('确定删除历史吗？', function () {
                delMarkItems(gids, $self);
            })
        } else {
            CommentUtils.confirm('确定取消关注吗？', function () {
                delMarkItems(gids, $self);
            })
        }
    });
    $('#ui_btn_confirm').click(function () {
        $('.mod_alert_v2').removeClass('show');
        $('.mod_alert_v2_mask').removeClass('show');
        $('#edit_btn').trigger('click');

        var itemEl = $('#favlist .fav_item');
        var gids = [];
        for (var i = 0; i < itemEl.length; i++) {
            gids.push($(itemEl[i]).attr('gid'));
        }

        delMarkItems(gids.join(","), itemEl);
    });
    $('#ui_btn_cancel').click(function () {
        $('.mod_alert_v2').removeClass('show');
        $('.mod_alert_v2_mask').removeClass('show');
    });
    $('.mod_alert_v2_mask').click(function () {
        $('.mod_alert_v2').removeClass('show');
        $('.mod_alert_v2_mask').removeClass('show');
    });
    $('.mod_alert_v2').click(function (e) {
        e.stopPropagation();
    });


    function renderMarkItem(data) {
        var imgUrl = $('#wrapBody').attr('img-url');
        var skuNameList = data['skuNameList'];
        var skuHtml = [];
        if (skuNameList) {
            for (var i = 0; i < skuNameList.length; i++) {
                skuHtml.push('<span>' + skuNameList[i]['attrName'] + '：</span>');
                skuHtml.push('<span>' + skuNameList[i]['valueName'] + '：</span>');
            }
        }
        var status = "";
        if (data['status'] == 20) {
            status = '<p class="image_tag">已下架</p>';
        }
        var price = ("" + data['price']).split('.');
        var yuan = price[0], fen = price.length > 1 ? price[1] : '00';
        if (fen.length == 1) fen = fen + "0";

        var text = '';
        if (pageType == 'history') {
            text = "删除历史";
        } else {
            text = "取消收藏";
        }

        var html =
            '<div class="fav_item" gid="' + data['id'] + '">' +
            '    <span class="fav_select select" style="display: none;"></span>' +
            '    <div class="move_div" style="">' +
            '        <a href="javascript:void(0)" data-url="' + $('#wrapBody').attr('goods-url') + data['id'] + '" class="fav_link fav_link_goods">' +
            '            <img class="image" src="' + imgUrl + data['fileName'] + '" width="100" height="100">' +
            '            ' + status +
            '            <p class="name">' + data['name'] + '</p>' +
            '            <p class="sku">' + skuHtml.join('') + '</p>' +
            '            <p class="price seperator" id="price_box_5677229" style="overflow: hidden;">' +
            '                <span class="price_value" style="color: #e4393c;float: none;float: left">' +
            '                    <b style="font-size:10px;">¥&nbsp;</b>' + yuan + '.<b style="font-size:10px;">' + fen + '</b>' +
            '                </span>' +
            '                <span class="sale_notice btn" style="display:none;"></span>' +
            '                <span class="more_notice btn" gid="' + data['id'] + '">' + text + '</span>' +
            '            </p>' +
            '            <p class="price_plus" style="font-family: arial;"></p></a>' +
            '        <div class="sale_items hidden" id="saleItems5677229" style=""></div>' +
            '    </div>' +
            '</div>';

        return html;
    }

    function loadMarks(page) {
        $('#loadingPanel').show();
        $.ajax({
            url: $('#wrapBody').attr('load-url'),
            dataType: 'json',
            data: {page: page},
            success: function (data) {
                $('#loadingPanel').hide();
                $('#wrapBody').data('loading', false);
                if (data['success'] == 1) {
                    var goods = data['data'];
                    if (goods) {
                        var html = [];
                        for (var i = 0; i < goods.length; i++) {
                            html.push(renderMarkItem(goods[i]));
                        }
                        $('#favlist').append(html.join(''));

                        setMarkItemEvent();
                        setSelectEvent();
                        $('*[data-url]').click(function () {
                            window.location.href = $(this).attr('data-url');
                        });
                    } else {
                        if (pageType == 'history') {
                            $('#favlist').append('<div class="no_more_data">无更多历史</div>');
                        } else {
                            $('#favlist').append('<div class="no_more_data">无更多收藏</div>');
                        }
                        $('#wrapBody').data('loading', true);
                    }
                } else {
                    CommentUtils.alert('', '获取数据失败');
                }
            },
            error: function (xhr) {
                $('#wrapBody').data('loading', false);
                $('#loadingPanel').hide();
                CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
            }
        })
    }

    $('#wrapBody').data('page', 1);
    var lastScrollTop = 0;
    $(document).scroll(function () {
        var scrollTop = $(document).scrollTop();
        if (Math.abs(scrollTop - lastScrollTop) <= 5) return;
        lastScrollTop = scrollTop;
        var height = $(document).height();
        if (parseInt(scrollTop + $(window).height()) >= parseInt(height)) {
            if (!$('#wrapBody').data('loading')) {
                var page = $('#wrapBody').data('page');
                $('#wrapBody').data('page', parseInt(page) + 1);
                loadMarks($('#wrapBody').data('page'));
            }
        }
    });
    loadMarks(1);

    function delMarkItems(gidArr, $self) {
        CommentUtils.wait('正在取消...');
        var gid = gidArr;
        var len = 1;
        if (gidArr instanceof Array) {
            gid = gidArr.join(",");
            len = gidArr.length;
        }
        $.ajax({
            url: $('#wrapBody').attr('cancel-url'),
            dataType: 'json',
            data: {gid: gid},
            success: function (data) {
                CommentUtils.closeWait();
                $self.each(function (i, v) {
                    if ($(v).hasClass('fav_item')) {
                        $(v).remove();
                    } else {
                        $(v).parents('.fav_item').remove();
                    }
                });
                var totalCount = parseInt($('#fav_total_num').html()) - len;
                $('#fav_total_num').html(totalCount);
                if (totalCount <= 0) {
                    if (pageType == 'history') {
                        $('#favlist').append('<div class="no_more_data">无更多历史</div>');
                    } else {
                        $('#favlist').append('<div class="no_more_data">无更多收藏</div>');
                    }
                }
            },
            error: function (xhr) {
                CommentUtils.closeWait();
                CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
            }
        })

    }

    function setMarkItemEvent() {
        $('#favlist .fav_item .more_notice').click(function (e) {
            var $self = $(this);
            e.stopPropagation();
            var gid = $(this).attr('gid');
            if (pageType == 'history') {
                CommentUtils.confirm('确定删除历史吗？', function () {
                    delMarkItems(gid, $self);
                })
            } else {
                CommentUtils.confirm('确定取消关注吗？', function () {
                    delMarkItems(gid, $self);
                })
            }
        });
    }

    $('#clear_btn').click(function () {
        if (pageType == 'history') {
            CommentUtils.confirm('确定清空历史吗？', function () {
                CommentUtils.wait('正在清空...');
                $.ajax({
                    url: $('#wrapBody').attr('clear-url'),
                    dataType: 'json',
                    success: function (data) {
                        CommentUtils.closeWait();
                        if (data['success'] == 1) {
                            window.location.reload();
                        } else {
                            CommentUtils.alert('请求出错', '清空失败');
                        }
                    },
                    error: function (xhr) {
                        CommentUtils.closeWait();
                        CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
                    }
                })
            }, null, true)
        }
    });
});
