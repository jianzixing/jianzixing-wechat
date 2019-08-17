$(function () {
    function getOrderForm() {
        var goodsItem = $('#venderList li.hproduct');
        var cart = $('#order_params_goods').val();
        var addrid = $('#order_params_addrid').val();
        var logistics = $('#shipItem').attr('data-id');
        var coupon = $('#couponItem').attr('data-id');
        var invoice = $('#invoicesItem').data('invoice');
        var msg = $('#buy_msg_input').val();

        var goods = [];
        goodsItem.each(function (i, v) {
            var disEl = $(v).find('.buy_additional');
            var amount = $(v).find('input.num').val();
            goods.push({
                gid: $(v).attr('gid'),
                skuId: $(v).attr('skuId'),
                discountId: $(disEl).attr('data-id'),
                amount: amount
            })
        });

        return {
            cart: cart,
            goods: JSON.stringify(goods),
            addrId: addrid,
            deliveryType: logistics,
            couponId: coupon,
            msg: msg,
            invoice: invoice
        }
    }

    function calOrderPrice() {
        var data = getOrderForm();
        CommentUtils.wait('计算中...');
        $.ajax({
            url: $('#wrapBody').attr('cal-url'),
            dataType: 'json',
            data: data,
            success: function (data) {
                CommentUtils.closeWait();
                if (data) {
                    if (data['success'] == 1) {
                        var orderPrice = data['prices']['orderPrice'];
                        var freightPrice = data['prices']['freightPrice'];
                        var discountPrice = data['prices']['discountPrice'];
                        var couponPrice = data['prices']['couponPrice'];
                        var goodsPrice = data['prices']['goodsPrice'];
                        var useCouponCount = data['prices']['useCouponCount'];

                        $('#feeDetail .goods_price').html('¥&nbsp;' + (goodsPrice || '0.00'));
                        $('#feeDetail .freight_price').html('+&nbsp;¥' + (freightPrice || '0.00'));
                        $('#feeDetail .coupon_price').html('-&nbsp;¥' + (couponPrice || '0.00'));
                        $('#feeDetail .discount_price').html('-&nbsp;¥' + (discountPrice || '0.00'));
                        $('#pageTotalPrice').html('¥' + orderPrice);

                        $('#discountItem p').html('已优惠¥' + discountPrice);
                        $('#couponItem p').html('已使用' + useCouponCount + '张共抵扣¥' + couponPrice);
                    }
                }
            },
            error: function (xhr) {
                CommentUtils.closeWait();
                CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
            }
        })
    }

    function submitOrder() {
        var data = getOrderForm();

        if (!data['deliveryType']) {
            CommentUtils.alert('', '没有配送方式信息');
            return;
        }

        CommentUtils.wait('正在提交订单...');
        $.ajax({
            url: $('#wrapBody').attr('sub-url'),
            dataType: 'json',
            data: data,
            success: function (data) {
                CommentUtils.closeWait();
                if (data) {
                    if (data['success'] == 1) {
                        window.location.href = $('#wrapBody').attr('pay-url') + "?oid=" + data['order']['number'];
                    } else {
                        CommentUtils.alert('', '提交订单失败');
                    }
                }
            },
            error: function (xhr) {
                CommentUtils.closeWait();
                CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
            }
        })
    }

    $('#btnConfirmOrder .mod_btn').click(function () {
        submitOrder();
    });

    $('#ui_btn_cancel').click(function () {
        $('.mod_alert_mask').removeClass('show');
        $('.mod_alert').removeClass('show');
    });

    $('.mod_alert .close').click(function () {
        $(this).parent().removeClass('show');
        $('.mod_alert_mask').removeClass('show');
    });
    $('.mod_alert .btn_1').click(function () {
        $(this).parent().parent().removeClass('show');
        $('.mod_alert_mask').removeClass('show');
    });

    // 处理全部弹出框关闭事件
    $('.detail_dialog_main .close').click(function (e) {
        $('.detail_dialog_main').removeClass('show');
    });
    $('.detail_dialog_main').click(function (e) {
        $('.detail_dialog_main').removeClass('show');
    });
    $('.detail_dialog_main .main').click(function (e) {
        e.stopPropagation();
    });

    // 打开运送方式
    $('#shipItem').click(function (e) {
        $('#shipPopup').addClass('show')
    });
    $('#shipPopup li').click(function () {
        $('#shipPopup li').removeClass('selected');
        $(this).addClass('selected');
    });
    $('#shipPopup .btn_item').click(function () {
        $('#shipItem .shipping_content p').html($('#shipPopup li.selected').html());
        $('#shipItem').attr('data-id', $('#shipPopup li.selected').attr('dt'));
        $('#shipPopup').removeClass('show');
        calOrderPrice();
    });
    $('#shipPopup li:first').addClass('selected');
    $('#shipPopup .btn_item').trigger('click');

    // 打开优惠活动弹框
    $('#venderList li .buy_additional').click(function (e) {
        var gid = $(this).attr('gid');
        $('#promotePopup' + gid).addClass('show')
    });
    // 默认优惠活动设置
    var goods = $('#venderList .order_info li');
    if (goods) {
        for (var i = 0; i < goods.length; i++) {
            var discountid = $(goods[i]).attr('discountid');
            var gid = $(goods[i]).attr('gid');
            if (discountid != '0') {
                var li = $('#promotePopup' + gid + ' li[did=' + discountid + ']');
                li.addClass('selected');
                $('#proArea' + gid).attr('data-id', discountid);
                $('#proArea' + gid).find('.buy_additional_kind_item_text').html(li.attr('disname'));
            }
        }
    }
    $('#discountConfirm .btn_item').click(function () {
        var discountDialog = $(this).parents('.detail_dialog_main');
        discountDialog.removeClass('show');
        var gid = discountDialog.attr('gid');
        var did = discountDialog.find('.promo_list li.selected').attr('did');
        var disName = discountDialog.find('.promo_list li.selected').attr('disName');
        if (did == 0 || did == '0' || !did) {
            $('#proArea' + gid).attr('data-id', 0);
            $('#proArea' + gid).find('.buy_additional_kind_item_text').html('不使用优惠活动');
        } else {
            $('#proArea' + gid).attr('data-id', did);
            $('#proArea' + gid).find('.buy_additional_kind_item_text').html(disName);
        }
        calOrderPrice();
    });
    $('.detail_dialog_main .promo_list li').click(function () {
        $('.detail_dialog_main .promo_list li').removeClass('selected');
        $(this).addClass('selected');
    });

    // 打开优惠券弹框
    $('#couponItem').click(function () {
        $('#couponPopup').toggleClass('show');
    });
    $('#couponConfirm .btn_item').click(function () {
        $('#couponPopup').removeClass('show');
        var cid = $('#couponPopup .order_coupons .order_coupons_item.selected').attr('cid');
        $('#couponItem').attr('data-id', cid);
        calOrderPrice();
    });

    // 商品数量变更
    $('#modifyNumDom .minus').click(function () {
        var num = $('#modifyNumDom input').val();
        if (parseInt(num) - 1 > 0) {
            $('#modifyNumDom input').val(parseInt(num) - 1);
            calOrderPrice();
        }
    });
    $('#modifyNumDom .plus').click(function () {
        var num = $('#modifyNumDom input').val();
        if (parseInt(num) + 1) {
            $('#modifyNumDom input').val(parseInt(num) + 1)
            calOrderPrice();
        }
    });

    // 打开发票窗口
    $('#invoicesItem').click(function () {
        $('#wrapBody').hide();
        $('#wrapInvoiceBody').show();
    });
    $('#wrapInvoiceBody .m_header_bar_back').click(function () {
        $('#wrapBody').show();
        $('#wrapInvoiceBody').hide();
    });
    $('#btnBottomConfirmBar').click(function () {
        var invoiceType = $('#invoinceTypes li.selected').attr('data-type');
        var headType = $('#fpType li.selected').attr('head-type');
        var cntType = $('#fpType .type_addtips.selected').attr('cnt-type');
        var compName = $('#compName').val();
        var taxerId = $('#taxerId').val();

        if (headType == '1') {
            if (!compName) {
                CommentUtils.alert('公司名称', '请填写公司名称');
                return;
            }
            if (!taxerId) {
                CommentUtils.alert('纳税人识别号', '请填写纳税人识别号');
                return;
            }
        }

        $('#wrapBody').show();
        $('#wrapInvoiceBody').hide();

        $('#invoicesItem').data('invoice', {
            type: invoiceType,
            headType: headType,
            companyName: compName,
            taxNumber: taxerId,
            cntType: cntType
        });
        $('#invoicesItem p').html(
            $('#fpTypeBar').html() + "&nbsp;" +
            $('#fpContent dt:first em').html());
    });

});
