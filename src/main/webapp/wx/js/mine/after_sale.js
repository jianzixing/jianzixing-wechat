$(function () {

    $('#list_body').height($(window).height() - 45 * 2);
    $(window).resize(function () {
        $('#list_body').height($(window).height() - 45 * 2);
    });

    $('.navs .item').click(function () {
        var type = $(this).attr('data-id');
        $('.navs .item').removeClass('cur');
        $(this).addClass('cur');
        $('.as_contents').hide();
        $('.as_contents.list_' + type).show();
        if (type == 1) loadList1();
        if (type == 2) loadList2();
        if (type == 3) loadList3();
    });


    $('.as_contents .search .clear').click(function (e) {
        var input = $(this).parents('.search').find('input');
        input.val('');
        $(this).parents('.search').find('.clear').css({display: 'none'});
        $('.as_contents .search input').trigger('keyup');
        e.stopPropagation();
        var type = $(this).parents('.as_contents').attr('type-id');
        if (type == '1') loadList1();
        if (type == '2') loadList2();
        if (type == '3') loadList3();
    });
    $('.as_contents .hd_bar .filter').click(function (e) {
        e.stopPropagation();
        $('#wrapBody').trigger('click');
    });
    $('.as_contents .hd_bar').click(function (e) {
        e.stopPropagation();
    });
    $('.as_contents .search input').focus(function () {
        $('#wrapBody').addClass('onsearch');
        $(this).parents('.search').find('.clear').css({display: 'flex'});
        $(this).parents('.hd_bar').find('.filter').css({display: 'flex'});
    });
    $('.as_contents .search input').on('keyup', function () {
        var val = $(this).val();
        if (val && val.length > 0) {
            $(this).parents('.hd_bar').find('.hd_bar_btn').addClass('red');
            $(this).parents('.hd_bar').find('.hd_bar_btn').html('搜索');
        } else {
            $(this).parents('.hd_bar').find('.hd_bar_btn').removeClass('red');
            $(this).parents('.hd_bar').find('.hd_bar_btn').html('取消');
        }
    });
    $('#wrapBody').click(function () {
        $('#wrapBody').removeClass('onsearch');
        $('.as_contents .search .clear').css({display: 'none'});
        $('.as_contents .hd_bar .filter').css({display: 'none'});
    });


    function renderApplyFor(order) {
        var number = order['number'];
        var imgUrl = $('#wrapBody').attr('img-url');
        var goods = order['TableOrderGoods'];
        var goodsHtml = [];
        for (var i = 0; i < goods.length; i++) {
            var g = goods[i];
            goodsHtml.push(
                '<div class="react-view body">' +
                '    <div class="react-view goods_info">' +
                '        <div class="react-view">' +
                '            <img src="' + imgUrl + g['fileName'] + '">' +
                '        </div>' +
                '        <div class="react-view name">' +
                '            <span class="gn">' + g['goodsName'] + '</span>' +
                '            <div class="react-view amount">' +
                '                <span type="Normal">数量：' + g['amount'] + '</span>' +
                '            </div>' +
                '        </div>' +
                '    </div>' +
                '    <div class="react-view btn_tools">' +
                '        <div class="react-view btns">' +
                '            <div class="react-view btn" data-url="http://localhost:8080/wx/mine/after_sale_start.jhtml?og=' + g['id'] + '">' +
                '                <span>申请售后</span>' +
                '            </div>' +
                '        </div>' +
                '    </div>' +
                '</div>'
            );
        }

        var html =
            '<div class="react-view goods_item">' +
            '    <div class="react-view title">' +
            '        <div class="react-view text">' +
            '            <span>订单编号：' + number + '</span>' +
            '        </div>' +
            '    </div>' + goodsHtml.join("") +
            '</div>';

        return html;
    }

    function renderAfterSale(data) {
        var number = data['number'];
        var imgUrl = $('#wrapBody').attr('img-url');
        var detailUrl = $('#wrapBody').attr('detail-url');
        var goods = data['TableOrderGoods'];

        var typeHtml = '';
        if (data['type'] == 10) {
            typeHtml =
                '<img class="type_icon" src="http://localhost:8080/wx/images/icons/tuiHuo-icon.png">' +
                '<span class="type">退货退款</span>';
        } else if (data['type'] == 20) {
            typeHtml =
                '<img class="type_icon" src="http://localhost:8080/wx/images/icons/huanHuo-icon.png">' +
                '<span class="type">返修换新</span>';
        } else if (data['type'] == 30) {
            typeHtml =
                '<img class="type_icon" src="http://localhost:8080/wx/images/icons/weiXiu-icon.png">' +
                '<span class="type">售后维修</span>';
        }

        var progressDetail = '';
        if (data['TableAfterSalesProgress']) {
            progressDetail = data['TableAfterSalesProgress']['detail'];
        }
        var statusName = '';
        if (data['status'] == 0) {
            statusName = '新建';
        } else if (data['status'] == 10) {
            statusName = '提交审核';
        } else if (data['status'] == 20) {
            statusName = '审核通过';
        } else if (data['status'] == 21) {
            statusName = '审核拒绝';
        } else if (data['status'] == 30) {
            statusName = '寄回商品';
        } else if (data['status'] == 31) {
            statusName = '买家寄回拒收';
        } else if (data['status'] == 40) {
            statusName = '卖家已收货';
        } else if (data['status'] == 41) {
            statusName = '验收失败';
        } else if (data['status'] == 42) {
            statusName = '验收通过';
        } else if (data['status'] == 50) {
            statusName = '维修中...';
        } else if (data['status'] == 51) {
            statusName = '无法维修';
        } else if (data['status'] == 52) {
            statusName = '维修完成';
        } else if (data['status'] == 60) {
            statusName = '卖家寄回商品';
        } else if (data['status'] == 61) {
            statusName = '买家拒收商品';
        } else if (data['status'] == 80) {
            statusName = '正在退款';
        } else if (data['status'] == 81) {
            statusName = '退款失败';
        } else if (data['status'] == 90) {
            statusName = '取消售后';
        } else if (data['status'] == 100) {
            statusName = '完成';
        }

        var html =
            '<div class="react-view goods_item" data-url="' + detailUrl + number + '">' +
            '    <div class="react-view title">' +
            '        <div class="react-view text ex">' +
            '            <span>售后单号：' + number + '</span>' +
            '            <div class="react-view type_info">' + typeHtml +
            '            </div>' +
            '        </div>' +
            '    </div>' +
            '    <div class="react-view body">' +
            '        <div class="react-view goods_info">' +
            '            <div class="react-view">' +
            '                <img src="' + imgUrl + goods['fileName'] + '">' +
            '            </div>' +
            '            <div class="react-view name">' +
            '                <span class="gn">' + goods['goodsName'] + '</span>' +
            '                <div class="react-view amount">' +
            '                    <span type="Normal">数量：' + data['amount'] + '</span>' +
            '                </div>' +
            '            </div>' +
            '        </div>' +
            '        <div class="react-view status">' +
            '            <span class="status_name">' + statusName + '</span>' +
            '            <div class="react-view status_detail" style="flex: 1 1 0%;">' +
            '                <span>' + progressDetail + '</span>' +
            '            </div>' +
            '            <div class="react-view more">' +
            '                <img src="http://localhost:8080/wx/images/icons/right-arrow.png">' +
            '            </div>' +
            '        </div>' +
            '    </div>' +
            '</div>';

        return html;
    }

    function loadList1() {
        $('.as_contents.list_1').find('.goods_item').remove();
        var keyword = $('.as_contents.list_1 .hd_bar').find('input').val();
        var data = {};
        if (keyword) data = {keyword: keyword};

        CommentUtils.wait('正在加载...');
        $.ajax({
            url: $('#wrapBody').attr('load1-url'),
            dataType: 'json',
            data: data,
            success: function (data) {
                CommentUtils.closeWait();
                if (data['success'] == 1) {
                    var html = [];
                    var data = data['data'];
                    for (var i = 0; i < data.length; i++) {
                        html.push(renderApplyFor(data[i]));
                    }
                    $('.as_contents.list_1').append(html.join(''));
                    $('.as_contents.list_1').resetLink();
                }

            },
            error: function (xhr) {
                CommentUtils.closeWait();
                CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
            }
        })
    }

    loadList1();


    function loadList2() {
        $('.as_contents.list_2').find('.goods_item').remove();
        var data = {};

        CommentUtils.wait('正在加载...');
        $.ajax({
            url: $('#wrapBody').attr('load2-url'),
            dataType: 'json',
            data: data,
            success: function (data) {
                CommentUtils.closeWait();
                if (data['success'] == 1) {
                    var html = [];
                    var data = data['data'];
                    if (data) {
                        for (var i = 0; i < data.length; i++) {
                            html.push(renderAfterSale(data[i]));
                        }
                        $('.as_contents.list_2').append(html.join(''));
                        $('.as_contents.list_2').resetLink();
                    }
                }

            },
            error: function (xhr) {
                CommentUtils.closeWait();
                CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
            }
        })
    }

    function loadList3() {
        $('.as_contents.list_3').find('.goods_item').remove();
        var keyword = $('.as_contents.list_3 .hd_bar').find('input').val();
        var data = {};
        if (keyword) data = {keyword: keyword};

        CommentUtils.wait('正在加载...');
        $.ajax({
            url: $('#wrapBody').attr('load3-url'),
            dataType: 'json',
            data: data,
            success: function (data) {
                CommentUtils.closeWait();
                if (data['success'] == 1) {
                    var html = [];
                    var data = data['data'];
                    for (var i = 0; i < data.length; i++) {
                        html.push(renderAfterSale(data[i]));
                    }
                    $('.as_contents.list_3').append(html.join(''));
                    $('.as_contents.list_3').resetLink();
                }

            },
            error: function (xhr) {
                CommentUtils.closeWait();
                CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
            }
        })
    }

    $('.homePageBody .as_contents .hd_bar .hd_bar_btn').click(function () {
        if ($(this).hasClass('red')) {
            var typeId = $(this).parents('.as_contents').attr('type-id');
            if (typeId == '1') loadList1();
            if (typeId == '2') loadList2();
            if (typeId == '3') loadList3();
        }
    });
});
