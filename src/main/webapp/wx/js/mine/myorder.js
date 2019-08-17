var orderNotPayStatus = [0];
var orderPayedStatus = [10, 20];
var orderWaitReciveStatus = [30, 40, 41];
var orderTakeStatus = [50, 60];
var orderCancelStatus = [90];

function renderOrderItem(data) {
    var imgUrl = $('#wrapBody').attr('load-img'),
        detailUrl = $('#wrapBody').attr('detail-url'),
        rebuyUrl = $('#wrapBody').attr('rebuy-url'),
        id = data['id'],
        os = data['status'],
        number = data['number'],
        payPrice = data['payPrice'],
        createTime = data['createTime'],
        count = data['TableOrderGoods'].length,
        firstGoods = data['TableOrderGoods'][0],
        status = "其它",
        btnHtml = [];

    if (orderNotPayStatus.indexOf(os) >= 0) {
        status = "未支付";
        btnHtml.push('<div class="oh_btn bg_red" data-url="' + $('#wrapBody').attr('payment-url') + number + '">立即支付</div>');
    }
    if (orderPayedStatus.indexOf(os) >= 0 || orderWaitReciveStatus.indexOf(os) >= 0) {
        status = "待收货";
        btnHtml.push('<div oid="' + data['id'] + '" data-url="' + rebuyUrl + data['id'] + '" class="oh_btn bg_red">再次购买</div>');
    }
    if (orderTakeStatus.indexOf(os) >= 0) {
        status = "已收货";
        btnHtml.push('<div oid="' + data['id'] + '" data-url="' + rebuyUrl + data['id'] + '" class="oh_btn bg_red">再次购买</div>');
        // btnHtml.push('<div oid="' + data['id'] + '" class="oh_btn bg_white">看相似</div>');
    }
    if (orderCancelStatus.indexOf(os) >= 0) {
        status = "已取消";
        btnHtml.push('<div oid="' + data['id'] + '" data-url="' + rebuyUrl + data['id'] + '" class="oh_btn bg_red">再次购买</div>');
        // btnHtml.push('<div oid="' + data['id'] + '" class="oh_btn bg_white">看相似</div>');
    }

    var multiGoodsHtml = [];
    if (count > 1) {
        for (var i = 0; i < count; i++) {
            var goods = data['TableOrderGoods'][i];
            multiGoodsHtml.push(
                '<div class="cover">' +
                '    <div class="wqvue-image img">' +
                '        <img src="' + imgUrl + goods['fileName'] + '" style="min-width: 1px;">' +
                '    </div>' +
                '    <div class="img_tag">数量: ' + goods['amount'] + '</div>' +
                '</div>'
            )
        }
    }

    detailUrl = detailUrl + "?oid=" + id;
    var html =
        '<div class="order_list_item" data-id="' + id + '"' +
        '   data-url="' + detailUrl + '">' +
        '    <div data-id="' + id + '" data-parentid="">' +
        '        <div class="order_box">' +
        '            <div class="order_head">' +
        '                <div class="icon_self"></div>' +
        '                <div data-href="" class="shop_title">' +
        '                    <span class="title light">下单日期 ' + createTime + '</span>' +
        '                </div>' +
        '                <div class="order_state">' + status + '</div>' +
        '                <div data-id="' + id + '" class="order_box_hd_del"></div>' +
        '            </div>' +
        '            <div>' +
        '                <div class="order_item">' +
        '                    <div class="oi_content">' +
        (count == 1 ?
            (
                '                        <div class="cover">' +
                '                            <div class="wqvue-image img">' +
                '                            <img src="' + imgUrl + firstGoods['fileName'] + '" style="min-width: 1px;"></div>' +
                '                        </div>' +
                '                        <div class="content">' +
                '                            <div class="desc">' + firstGoods['goodsName'] + '</div>' +
                '                        </div>'
            ) : (multiGoodsHtml.join(""))) +

        '                    </div>' +
        '                    <div class="order_total_bar">' +
        '                        <div class="total_count">共' + count + '件商品</div>' +
        '                        <div class="payment">实付金额：<span class="price">¥' + payPrice + '</span></div>' +
        '                    </div>' +
        '                </div>' +
        '            </div>' +
        '            <div class="order_btn">' + btnHtml.join('') + '</div>' +
        '        </div>' +
        '    </div>' +
        '</div>';

    return html;
}

function delOrderItem($el) {
    var delbtn = $el.find('.order_box_hd_del');
    delbtn.click(function (e) {
        e.stopPropagation();
        $('#wq-dialog').show();
        $('#wq-dialog').data('orderDel', {
            oid: $(this).attr('data-id'),
            orderItem: $(this).parents('.order_list_item')
        });
    })
}

$(function () {

    // 搜索订单
    $('.hd_bar_wrap .hd_search_frm input').focus(function () {
        $('.hd_bar_wrap .hd_search_frm').addClass('hd_search_frm_focus');
        $('.my_nav_mask').show();
    });
    $('.hd_bar_wrap .hd_search_frm input').on('change', function () {
        var val = $('.hd_bar_wrap .hd_search_frm input').val();
        if (val && val.length > 0) {
            $('.hd_bar_wrap .hd_search_clear').removeClass('hide');
        } else {
            $('.hd_bar_wrap .hd_search_clear').addClass('hide');
        }
    });

    function closeSearch() {
        $('.hd_bar_wrap .hd_search_frm').removeClass('hd_search_frm_focus');
        $('.hd_bar_wrap .hd_search_frm').removeClass('hd_search_frm_input');
        $('.hd_bar_wrap .hd_search_clear').addClass('hide');
        $('.my_nav_mask').hide();
    }

    function calValueSize() {
        var val = $('.hd_bar_wrap .hd_search_frm input').val();
        if (val && val.length > 0) {
            $('.hd_bar_wrap .hd_search_clear').removeClass('hide');
            $('.hd_bar_wrap .hd_search_frm').removeClass('hd_search_frm_focus');
            $('.hd_bar_wrap .hd_search_frm').addClass('hd_search_frm_input');
        } else {
            $('.hd_bar_wrap .hd_search_frm').addClass('hd_search_frm_focus');
            $('.hd_bar_wrap .hd_search_frm').removeClass('hd_search_frm_input');
            $('.hd_bar_wrap .hd_search_clear').addClass('hide');
        }
    }

    $('.hd_bar_wrap .hd_search_clear').on('click', function (e) {
        $('.hd_bar_wrap .hd_search_frm input').val('');
        calValueSize();
        e.stopPropagation();
    });
    $('.hd_bar_wrap .hd_search_frm input').on('keyup', function () {
        calValueSize();
    });
    $('.hd_bar_wrap .hd_search_btn').click(function () {
        closeSearch();
    });
    $('.hd_bar_wrap .hd_search_btn_red').click(function () {
        var url = $(this).attr('search-url');
        var keyword = $('.hd_bar_wrap .hd_search_frm input').val();
        closeSearch();
        if (url) {
            window.location.href = url + "?keyword=" + encodeURIComponent(keyword);
        }
    });
    $('.hd_bar_wrap .hd_bar').click(function (e) {
        e.stopPropagation();
    });
    $('.my_nav_mask').click(function () {
        closeSearch();
    });

    // 删除订单
    $('#wq-dialog .btn_2').click(function () {
        $('#wq-dialog').hide();
    });
    $('#wq-dialog .btn_1').click(function () {
        $('#wq-dialog').hide();
        var orderDel = $('#wq-dialog').data('orderDel');
        CommentUtils.wait('正在删除...');
        $.ajax({
            url: $('#wrapBody').attr('del-url'),
            dataType: 'json',
            data: {oid: orderDel.oid},
            success: function (data) {
                CommentUtils.closeWait();
                if (data['success'] == 1) {
                    orderDel.orderItem.remove();
                } else {
                    if (data['code'] == 'not_login') {
                        CommentUtils.alert('', '您已经退出登录');
                    }
                }
            },
            error: function (xhr) {
                CommentUtils.closeWait();
                CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
            }
        })
    });

    function loadOrderList($el) {
        // 加载订单数据
        var data = {page: 1};
        if ($el.attr('id') == 'tab_all_order_list') data['status'] = null;
        if ($el.attr('id') == 'tab_readypay_order_list') data['status'] = orderNotPayStatus.join(",");
        if ($el.attr('id') == 'tab_readytake_order_list') data['status'] = orderPayedStatus.join(",") + "," + orderWaitReciveStatus.join(",");
        if ($el.attr('id') == 'tab_finish_order_list') data['status'] = orderTakeStatus.join(",");

        $.ajax({
            url: $('#wrapBody').attr('load-url'),
            dataType: 'json',
            data: data,
            success: function (data) {
                if (data['success'] == 1) {
                    var data = data['data'];
                    if (data && data.length > 0) {
                        var orders = [];
                        for (var i = 0; i < data.length; i++) {
                            orders.push(renderOrderItem(data[i]));
                        }

                        $el.find('.loading_order_flag').before(orders.join(""));
                        delOrderItem($el);
                        $el.find('.loading_order_flag').addClass('hide');
                        $('*[data-url]').click(function (e) {
                            window.location.href = $(this).attr('data-url');
                            e.stopPropagation();
                        });
                    } else {
                        $el.find('.empty_order_flag').removeClass('hide');
                        $el.find('.loading_order_flag').addClass('hide');
                    }
                } else {
                    if (data['code'] == 'not_login') {
                        CommentUtils.alert('', '您已经退出登录');
                    }
                }
            },
            error: function (xhr) {
                CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
            }
        })
    }

    // 切换订单
    $('.my_nav .my_nav_list .my_nav_list_item').click(function () {
        $('.my_nav .my_nav_list .my_nav_list_item').removeClass('cur');
        $(this).addClass('cur');
        $('#tabWapper .my_order').hide();
        $('#' + $(this).attr('data-tabid')).show();

        $('#tabWapper .my_order').hide();
        var tabOrder = $('#' + $(this).attr('data-tabid'));
        $('#tabWapper').find('.order_list_item').remove();
        tabOrder.show();
        tabOrder.find('.empty_order_flag').addClass('hide');
        tabOrder.find('.loading_order_flag').removeClass('hide');

        loadOrderList(tabOrder);
    });

    var type = $('#wrapBody').attr('order-type');
    if (type == '1') {
        $('.my_nav_list .my_nav_list_item[data-tabid="tab_readypay_order_list"]').trigger('click');
    } else if (type == '2') {
        $('.my_nav_list .my_nav_list_item[data-tabid="tab_readytake_order_list"]').trigger('click');
    } else if (type == '3') {
        $('.my_nav_list .my_nav_list_item[data-tabid="tab_finish_order_list"]').trigger('click');
    } else {
        $('.my_nav_list .my_nav_list_item[data-tabid="tab_all_order_list"]').trigger('click');
    }
});
