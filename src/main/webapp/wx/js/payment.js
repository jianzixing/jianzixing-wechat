$(function () {

    function getPaymentData() {
        var oid = $('#wrapBody').attr('oid');
        var payment = [];
        var selectedPayList = $('.pay-way .pay-list.selected');
        for (var i = 0; i < selectedPayList.length; i++) {
            var p = selectedPayList[i];
            var paymentData = $(p).data('data');
            if (paymentData) {
                payment.push(paymentData);
            }
        }
        return {oid: oid, type: $('#wrapBody').attr('payment-type'), payment: JSON.stringify(payment)}
    }

    function numAdd(value1, value2) {
        var r1, r2, m;
        try {
            r1 = value1.toString().split(".")[1].length;
        } catch (e) {
            r1 = 0;
        }
        try {
            r2 = value2.toString().split(".")[1].length;
        } catch (e) {
            r2 = 0;
        }
        m = Math.pow(10, Math.max(r1, r2));
        return (value1 * m + value2 * m) / m;
    }

    function numSub(num1, num2) {
        var baseNum, baseNum1, baseNum2;
        var precision;// 精度
        try {
            baseNum1 = num1.toString().split(".")[1].length;
        } catch (e) {
            baseNum1 = 0;
        }
        try {
            baseNum2 = num2.toString().split(".")[1].length;
        } catch (e) {
            baseNum2 = 0;
        }
        baseNum = Math.pow(10, Math.max(baseNum1, baseNum2));
        precision = (baseNum1 >= baseNum2) ? baseNum1 : baseNum2;
        return ((num1 * baseNum - num2 * baseNum) / baseNum).toFixed(precision);
    }

    function calPayment() {
        CommentUtils.wait('正在计算...');
        $.ajax({
            url: $('#wrapBody').attr('cal-url'),
            dataType: 'json',
            data: getPaymentData(),
            success: function (data) {
                CommentUtils.closeWait();
                if (data['message'] == 1) {
                    var data = data['data'];
                    for (var i in data) {
                        if (i != 'orderPayPrice') {
                            var dprice = $('.pay-list[pay-id="' + i + '"] .pay-detail .discount_price').attr('dprice');
                            if (!dprice) dprice = 0;
                            $('.pay-list[pay-id="' + i + '"] .pay-detail .discount_price').show();
                            $('.pay-list[pay-id="' + i + '"] .pay-detail .discount_price')
                                .html('抵扣¥' + numAdd(data[i]['price'], dprice) + '元');
                        }
                    }

                    var orderPayPrice = data['orderPayPrice'];
                    $('.confirm-pay').html('您需支付 ¥' + orderPayPrice);
                } else {

                }
            },
            error: function (xhr) {
                CommentUtils.closeWait();
                CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
            }
        })
    }

    window['onPaymentSuccessCallback'] = function (res) {
        window.location.href = $('#wrapBody').attr('succ-url') + "?type=" + $('#wrapBody').attr('payment-type');
    };

    $('.confirm-pay').click(function () {
        CommentUtils.wait('正在支付...');
        $.ajax({
            url: $('#wrapBody').attr('pay-url'),
            dataType: 'json',
            data: getPaymentData(),
            success: function (data) {
                CommentUtils.closeWait();
                if (data['message'] == 1) {
                    var payResult = data['data'];
                    if (payResult && payResult['params']) {
                        var params = payResult['params'];
                        var type = payResult['type'];

                        if (type == 'JS_API') {
                            var jsCode = params['jsCode'];
                            eval(jsCode);
                            if (onPaymentCall) {
                                onPaymentCall();
                            } else {
                                alert('未找到JSAPI代码块');
                            }
                            return true;
                        }
                        alert('支付失败缺少支付参数');
                    } else {
                        window.location.href = $('#wrapBody').attr('succ-url');
                    }
                } else {
                    if (data['code'] == 'payment_not_exist') {
                        CommentUtils.alert('支付失败', '支付方式不存在');
                    }
                    if (data['code'] == 'allow_one_delay') {
                        CommentUtils.alert('支付失败', '在线支付方式重复');
                    }
                    if (data['code'] == 'payment_not_enough') {
                        CommentUtils.alert('支付失败', '抵扣金额无法支付当前订单');
                    }
                    if (data['code'] == 'server_error') {
                        CommentUtils.alert('支付失败', '服务器错误');
                    }
                    if (data['code'] == 'args_empty') {
                        CommentUtils.alert('支付失败', '请选择支付方式');
                    }
                    if (data['code'].indexOf('wechat_pay_') >= 0) {
                        if (data['code'].indexOf('wechat_pay_args_') >= 0) {
                            CommentUtils.alert('支付失败', '微信支付参数为空');
                        } else {
                            CommentUtils.alert('支付失败', '微信支付失败');
                        }
                    }
                }
            },
            error: function (xhr) {
                CommentUtils.closeWait();
                CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
            }
        });
    });

    // 如果已有优惠则计算
    var discountPrices = $('.pay-list .pay-detail .discount_price');
    for (var j = 0; j < discountPrices.length; j++) {
        var el = discountPrices[j],
            dprice = $(el).attr('dprice'),
            orderPrice = $('.confirm-pay').attr('order-price');
        if (dprice && dprice != '0') {
            $(el).show();
            orderPrice = numSub(orderPrice, dprice);
        }
    }
    $('#pay_way_list .pay-list').click(function () {
        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
        } else {
            $('#pay_way_list .pay-list').removeClass('selected');
            $(this).addClass('selected');
            $(this).data('data', {channelId: $(this).attr('pay-id')});
        }
    });

    $('.integral_pay').click(function () {
        $(this).toggleClass('selected');
        $(this).data('data', {channelId: $(this).attr('pay-id')});
        calPayment();
    });

    $('.balance_pay').click(function () {
        $(this).toggleClass('selected');
        $(this).data('data', {channelId: $(this).attr('pay-id')});
        calPayment();
    });

    $('.spcard_pay').click(function () {
        $(this).toggleClass('selected');
        if ($(this).hasClass('selected')) {
            $('#wrapSpcardBody').show();
            $('#wrapBody').hide();
        } else {
            $('.spcard_pay .pay-detail .pay_way_desc').html('勾选后选择您要使用的购物卡');
            calPayment();
        }
    });

    $('.spcard_list .ecard_item').click(function () {
        $(this).find('.ecard_lower').toggleClass('selected');
    });
    $('.button_bind_card').click(function () {
        $('#wrapSpcardBody').hide();
        $('#wrapBody').show();
        var items = $('.spcard_list .ecard_item .ecard_lower.selected').parent().parent();
        var money = 0;
        var relIds = [];
        items.each(function (i, v) {
            relIds.push($(v).attr('data-id'));
            var balance = $(v).attr('balance');
            if (balance) {
                money = numAdd(money, balance);
            }
        });
        if (money > 0) {
            $('.spcard_pay .pay-detail .pay_way_desc').html('您勾选总额为¥' + money + '元购物卡');
        }
        $('.spcard_pay').data('data', {channelId: $('.spcard_pay').attr('pay-id'), relIds: relIds.join(',')});
        calPayment();
    });

    $('#wrapSpcardBody .back_close').click(function () {
        $('#wrapSpcardBody').hide();
        $('#wrapBody').show();
    });

    $('.spcard_list').height($(window).height() - 45 - 50);
    $(window).on('resize', function () {
        $('.spcard_list').height($(window).height() - 45 - 50);
    });
});
