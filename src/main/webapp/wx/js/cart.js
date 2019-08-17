$(function () {
    var fixBarBot = $('#fixBarBot'); //固定栏

    $(".goods .num_wrap .minus").click(function () { //数量减
        var minBtn = $(this);
        if (minBtn.hasClass('disabled')) {
            return false;
        }
        showLoadingDialog();
        var numInput = $(minBtn.siblings('.input_wrap').children(':input')[0]);
        var num = parseInt(numInput.val()) - 1;
        numInput.val(num);
        if (num <= 1) {
            minBtn.addClass('disabled');
        }

        var numWrap = minBtn.parent('.num_wrap');
        var gid = numWrap.attr('gid');
        var skuId = numWrap.attr('skuId');
        $.post(setShopCart, {gid: gid, skuId: skuId, amount: num}, function (res) {
            res = JSON.parse(res);
            if (res.code === 100) {
                getCartPrice();
            }
            hideLoadingDialog();
        });
    });
    $(".goods .num_wrap .plus").click(function () { //数量加
        if (!$(this).hasClass('disabled')) {
            showLoadingDialog();
            var minBtn = $(this);
            var numWrap = minBtn.parent('.num_wrap');
            var gid = numWrap.attr('gid');
            var skuId = numWrap.attr('skuId');
            var numInput = $(minBtn.siblings('.input_wrap').children(':input')[0]);
            var num = parseInt(numInput.val()) + 1;
            $.post(setShopCart, {gid: gid, skuId: skuId, amount: num}, function (res) {
                res = JSON.parse(res);
                if (res.code === 100) {
                    numInput.val(num);
                    if (num > 1) {
                        minBtn.siblings('.minus').removeClass('disabled');
                    }
                    getCartPrice();
                }
                hideLoadingDialog();
            });

        }
    });
    $(".goods .icon_select").click(function () { //商品选择与取消选择
        showLoadingDialog();
        var goodsNode = $(this).parent('.goods');
        var sku = goodsNode.attr('attr-sku');
        if (goodsNode.hasClass('selected')) { //已选择
            goodsNode.removeClass('selected');

            $($(goodsNode.parent('.item').siblings('.head_wrap')).children('.head')[0]).removeClass('selected'); //取消店铺选中
            if (fixBarBot.hasClass('selected')) { //取消外部全选
                fixBarBot.removeClass('selected');
            }
            updateCheckState([{id: goodsNode.attr('cartId'), isChecked: 0}]);
        } else {
            goodsNode.addClass('selected');

            var noHas = false;
            goodsNode.parent('.item').siblings('.item').children('.goods').each(function (index, item) {
                if (!$(item).hasClass('selected')) {
                    noHas = true;
                }
            });
            if (!noHas) {
                var head_wrap = $(goodsNode.parent('.item').siblings('.head_wrap'));
                $(head_wrap.children('.head')[0]).addClass('selected'); ////添加店铺选中

                var noHasSelected = false;
                $(head_wrap.parent('.section').siblings('.section').children('.head_wrap')[0]).siblings('.head').each(function (index, item) {
                    if (!$(item).hasClass('selected')) {
                        noHasSelected = true;
                    }
                });
                if (!noHasSelected) {
                    fixBarBot.addClass('selected');
                }
            }
            updateCheckState([{id: goodsNode.attr('cartId'), isChecked: 1}]);
        }
        hideLoadingDialog();
    });

    $('.head_wrap .head .icon_select').click(function () { //店铺的全选和取消全选
        if ($(this).parent('.head').hasClass('selected')) {//已选中，改为不选中
            $(this).parent('.head').removeClass('selected');
            var checkState = [];
            $(this).parent('.head').parent('.head_wrap').siblings('.item').each(function (index, item) {
                var goods = $(item).children('.goods')[0];
                var sku = $(goods).attr('attr_sku');
                $(goods).removeClass('selected');
                checkState.push({id: $(goods).attr('cartId'), isChecked: 0});
            });
            updateCheckState(checkState);
            if (fixBarBot.hasClass('selected')) { //取消外部全选
                fixBarBot.removeClass('selected');
            }
        } else {
            $(this).parent('.head').addClass('selected');
            var checkState = [];
            $(this).parent('.head').parent('.head_wrap').siblings('.item').each(function (index, item) {
                var goods = $(item).children('.goods')[0];
                var sku = $(goods).attr('attr_sku');
                $(goods).addClass('selected');
                checkState.push({id: $(goods).attr('cartId'), isChecked: 1});
            });
            updateCheckState(checkState);
            var noHasSelected = false;
            $(this).parent('.head').parent('.head_wrap').parent('.section').siblings('.section').children('.head').each(function (index, item) {
                if (!$(item).hasClass('selected')) {
                    noHasSelected = true;
                }
            });
            if (!noHasSelected) {
                fixBarBot.addClass('selected');
            }
        }
        getCartPrice();
    });

    $("#fixBarBot .icon_select").click(function () { //全选，全不选
        if (fixBarBot.hasClass('selected')) {
            $('#cmdtylist .head').each(function (index, item) {
                if ($(item).hasClass('selected')) {
                    $(item).removeClass('selected');
                }
            });

            $("#cmdtylist .item .goods").each(function (index, item) {
                if ($(item).hasClass('selected')) {
                    $(item).removeClass('selected');
                }
            });

            fixBarBot.removeClass('selected');
            var checkState = [];
            $(".goods").each(function () {
                checkState.push({id: $(this).attr('cartId'), isChecked: 0});
            });
            updateCheckState(checkState);
        } else {
            $('#cmdtylist .head').each(function (index, item) {
                if (!$(item).hasClass('selected')) {
                    $(item).addClass('selected');
                }
            });

            $("#cmdtylist .item .goods").each(function (index, item) {
                if (!$(item).hasClass('selected')) {
                    $(item).addClass('selected');
                }
            });

            fixBarBot.addClass('selected');
            var checkState = [];
            $(".goods").each(function () {
                checkState.push({id: $(this).attr('cartId'), isChecked: 1});
            });
            updateCheckState(checkState);
        }
    });

    $('.quanJs').click(function () { //优惠券开
        $('#c_jdshopcoupon_show').addClass('show');
    });

    $('#c_jdshopcoupon_show .close').click(function () { //优惠券关
        $('#c_jdshopcoupon_show').removeClass('show');
    });

    $('#c_jdshopcoupon_show').click(function () { //优惠券空白区域关
        $('#c_jdshopcoupon_show').removeClass('show');
    });

    $('.m_header_bar_menu').click(function () {
        $('.m_header_nav').toggle();
    });

    var lastPromotionParam = '';
    var promoteCont = $("#promoteCont");
    $("#promotionChoose").click(function () {
        var li = $(this);
        var gid = parseInt(li.attr("gid"));
        var skuId = parseInt(li.attr("skuId"));
        var param = gid + "&" + skuId;
        if (param == lastPromotionParam) {
            promoteCont.addClass("show");
            return false;
        }
        lastPromotionParam = param;
        if (carts) {
            var discount = null;
            var discountId = null;
            for (var i = 0; i < carts.length; i++) {
                var cart = carts[i];
                if (skuId && skuId > 0) {
                    if (cart.gid == gid && cart.skuId == skuId) {
                        discount = cart.discount;
                        discountId = cart.discountId;
                        break;
                    }
                } else {
                    if (cart.gid == gid) {
                        discount = cart.discount;
                        discountId = cart.discountId;
                        break;
                    }
                }
            }
            if (discount) {
                var promotionList = Et.template($("#promotionList").html(), {
                    discount: discount,
                    gid: gid,
                    skuId: skuId,
                    discountId: discountId
                });
                $(".promotion_list").html(promotionList);
                promoteCont.addClass("show");
            }
        }
    });

    $("#promoteCont .close").click(function () {
        promoteCont.removeClass("show");
    });

    $(".promotion_item").live("click", function (event) {
        showLoadingDialog();
        var item = $(this);
        var gid = item.attr('gid');
        var skuId = item.attr("skuId");
        var did = item.attr("did");
        $.post(setShopCart, {gid: gid, skuId: skuId, did: did}, function (res) {
            res = JSON.parse(res);
            if (res.code === 100) {
                item.addClass("selected");
                item.siblings().removeClass("selected");
                promoteCont.removeClass("show");
                var promotionName = "";
                if (did == 0) {
                    promotionName = "不参加促销";
                } else {
                    promotionName = item.text().replace("<i class=\"icon_select\"></i>", '');
                }
                $("#promotionName").text(promotionName);
                for (var i = 0; i < carts.length; i++) {
                    var cart = carts[i];
                    if (skuId && skuId > 0) {
                        if (cart.gid == gid && cart.skuId == skuId) {
                            carts[i].discountId = did;
                            break;
                        }
                    } else {
                        if (cart.gid == gid) {
                            carts[i].discountId = did;
                            break;
                        }
                    }
                }
                getCartPrice();
            }
            hideLoadingDialog();
        });
    });

    var dialogDel = $("#dialogDel");
    var dialogMask = $("#dialogMask");
    var deleteGid = 0;
    var deleteSkuId = 0;
    $(".m_action .del-btn").click(function () {
        dialogDel.addClass("show");
        dialogMask.addClass("show");
        deleteGid = $(this).attr("gid");
        deleteSkuId = $(this).attr("skuId");
    });

    $('#dialogDel .dialog-back-btn').click(function () {
        dialogDel.removeClass("show");
        dialogMask.removeClass("show");
        deleteGid = 0;
        deleteSkuId = 0;
    });

    $('#dialogDel .dialog-del-btn').click(function () {
        $.post(removeShopCart, {gid: deleteGid, skuId: deleteSkuId}, function (res) {
            res = JSON.parse(res);
            dialogDel.removeClass("show");
            dialogMask.removeClass("show");
            if (res.code === 100) {
                $("#item-" + deleteGid + "-" + deleteSkuId).remove();
                if (!$(".section .item") || $(".section .item").length == 0) {
                    $("#emptyDiv").show();
                    $("#fixBarBot").hide();
                }
                getCartPrice();
            } else {
                if (res.code === 999) {
                    CommentUtils.alert("提示", "请重新登录后操作");
                } else {
                    CommentUtils.alert("提示", res.msg);
                }
            }

        });

    });

    var dialogFav = $("#dialogFav");
    var favGid = 0;
    var favSkuId = 0;
    $('.m_action .fav-btn').click(function () {
        dialogFav.addClass("show");
        dialogMask.addClass("show");
        favGid = $(this).attr("gid");
        favSkuId = $(this).attr("skuId");
    });

    $('#dialogFav .dialog-back-btn').click(function () {
        dialogDel.removeClass("show");
        dialogMask.removeClass("show");
        dialogFav.removeClass("show");
        deleteGid = 0;
        deleteSkuId = 0;
    });

    $('#dialogFav .dialog-del-btn').click(function () {
        $.post(moveToCollect, {gid: favGid, skuId: favSkuId}, function (res) {
            res = JSON.parse(res);
            dialogDel.removeClass("show");
            dialogMask.removeClass("show");
            if (res.code === 100) {
                $("#item-" + favGid + "-" + favSkuId).remove();
                getCartPrice();
            } else {
                if (res.code === 999) {
                    CommentUtils.alert("提示", "请重新登录后操作");
                } else {
                    CommentUtils.alert("提示", res.msg);
                }
            }

        });
    });

    $(".coupon_voucher3_info_btn").click(function () {
        var cid = $(this).attr("cid");
        $.post(userGetCoupon, {cid: cid}, function (res) {
            res = JSON.parse(res);
            if (res.code === 100) {
                CommentUtils.alert("提示", "领取成功");
            } else {
                if (res.code === 999) {
                    CommentUtils.alert("提示", "请重新登录后操作");
                } else {
                    CommentUtils.alert("提示", res.msg);
                }
            }
        });
    });

    $("#checkoutBtn").click(function () {
        var btn = $(this);
        if (!btn.hasClass("disabled")) {
            var cartIds = '';
            $(".goods.selected").each(function () {
                cartIds += $(this).attr('cartId') + ',';
            });
            if (cartIds != '') {
                location.href = baseUrl + '/wx/order.jhtml?c=' + cartIds;
            }
        }
    });
});


var wxLoading = $("#wxloading");

function showLoadingDialog() {
    wxLoading.show();
}

function hideLoadingDialog() {
    wxLoading.hide();
}


var payPrice = $("#totalPrice");
var totalBackMoney = $("#totalBackMoney");

function getCartPrice() {
    $.post(getCartPriceUrl, {}, function (result) {
        result = JSON.parse(result);
        if (result.code === 100) {
            result = result.data;
            payPrice.text("¥" + result.payPrice);
            totalBackMoney.text("总额¥" + result.totalPrice + " 立减¥" + result.discountPrice);
        }else{
            payPrice.text("¥0.00");
            totalBackMoney.text("总额¥0.00 立减¥0.00");
        }
    });
    getProductNum();
}

function getProductNum() {
    var total = 0;
    $(".goods.selected .num").each(function () {
        total += parseInt($(this).val());
    });
    $("#totalNum").text("(" + total + "件)");
    if (total == 0) {
        $(".buyJs").addClass("disabled");
    } else {
        $(".buyJs").removeClass("disabled");
    }
}

function updateCheckState(o) {
    $.post(cartUpdateCheck, {o: JSON.stringify(o)}, function (res) {
        res = JSON.parse(res);
        if (res.code === 100) {
            getCartPrice();
        }
    });
}

getCartPrice();

//判断是否要全选
if($(".goods").length==$(".goods.selected").length){
    $("#fixBarBot").addClass("selected");
}