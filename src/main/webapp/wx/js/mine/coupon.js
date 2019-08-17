$(function () {

    function renderCouponItem(data, isTypeDisabled) {
        data = data['TableCoupon'];
        var html =
            '<div class="coupon_list_item">' +
            '    <div class="coupon_voucher2 ' + (isTypeDisabled ? 'type_disabled' : '') + '">' +
            '        <span class="coupon_voucher2_tag" style="display:none;"><i></i></span>' +
            '        <a href="javascript:;" class="coupon_voucher2_main">' +
            '            <div class="coupon_voucher2_view">' +
            '                <p class="coupon_voucher2_view_price">' +
            '                    <i>¥</i><strong>' + data['couponPrice'] + '</strong>' +
            '                </p>' +
            '                <p class="coupon_voucher2_view_des">满' + data['orderPrice'] + '元可用</p>' +
            '                <p class="coupon_voucher2_view_tips" style="display: none;">false</p>' +
            '            </div>' +
            '            <div class="coupon_voucher2_info">' +
            '                <p class="coupon_voucher2_info_text">' +
            '                    <i class="coupon_voucher2_info_type">优惠券</i>' + data['name'] +
            '                </p>' +
            '                <p class="coupon_voucher2_info_label">' +
            '                    <span>全平台</span>' +
            '                </p>' +
            '                <p class="coupon_voucher2_info_date">' + data['startTime'] + '-' + data['finishTime'] + '</p></div>' +
            '        </a>' +
            (isTypeDisabled ? '' :
                '        <div class="coupon_voucher2_foot">' +
                '            <div>' +
                '                <div class="coupon_voucher2_hr"></div>' +
                '                <div class="coupon_voucher2_btns">' +
                '                    <a class="coupon_voucher2_btns_item color_red">去使用</a>' +
                '                </div>' +
                '            </div>' +
                '        </div>') +
            '    </div>' +
            '</div>';

        return html;
    }

    function loadCoupons(sid, page) {
        $('#useableList').data('loading', true);
        $.ajax({
            url: $('#wrapBody').attr('load-url'),
            dataType: 'json',
            data: {type: sid, page: page},
            success: function (data) {
                $('#useableList').data('loading', false);
                if (data['success'] == 1) {
                    var coupons = data['data'];
                    if (coupons) {
                        var html = [];
                        for (var i = 0; i < coupons.length; i++) {
                            html.push(renderCouponItem(coupons[i], sid != '0' ? true : false));
                        }
                        $('#useableList').append(html.join(''));
                    } else {
                        $('#useableList').append('<div class="no_more_data">无更多优惠券</div>');
                        $('#useableList').data('loading', true);
                    }
                } else {
                    CommentUtils.alert('', '获取优惠券失败');
                }
            },
            error: function (xhr) {
                $('#useableList').data('loading', false);
                CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
            }
        })
    }

    var couponType = $('#wrapBody').attr('coupon-type');

    $('.coupon_nav .coupon_nav_item').click(function () {
        $('.coupon_nav .coupon_nav_item').removeClass('cur');
        $(this).addClass('cur');

        $('#useableList').html('');
        $('#useableList').data('page', 1);
        var sid = $(this).attr('sid');
        loadCoupons(sid, $('#useableList').data('page'));
    });

    $('.coupon_nav .coupon_nav_item.cur').trigger('click');
    $(document).scroll(function () {
        var scrollTop = $(document).scrollTop();
        var height = $(document).height();
        if (parseInt(scrollTop + $(window).height()) >= parseInt(height)) {
            if (!$('#useableList').data('loading')) {
                var sid = $('.coupon_nav .coupon_nav_item.cur').attr('sid');
                var page = $('#useableList').data('page');
                $('#useableList').data('page', parseInt(page) + 1);
                loadCoupons(sid, $('#useableList').data('page'));
            }
        }
    });
});
