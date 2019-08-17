jQuery.fn.shake = function (
    intShakes /*Amount of shakes*/,
    intDistance /*Shake distance*/,
    intDuration /*Time duration*/) {
    this.each(function () {
        var jqNode = $(this);
        jqNode.css({position: 'relative'});
        for (var x = 1; x <= intShakes; x++) {
            jqNode.animate({left: (intDistance * -1)}, (((intDuration / intShakes) / 4)))
                .animate({left: intDistance}, ((intDuration / intShakes) / 2))
                .animate({left: 0}, (((intDuration / intShakes) / 4)));
        }
    });
    return this;
};

jQuery.fn.resetLink = function () {
    $(this).find('*[data-url]').click(function (e) {
        window.location.href = $(this).attr('data-url');
        e.stopPropagation();
    });
};

$(function () {
    $('*[data-url]').click(function (e) {
        window.location.href = $(this).attr('data-url');
        e.stopPropagation();
    });

    {
        // 弹出式气泡
        var commonHeaderJdkey = $('#m_common_header_key');
        var headerMask = $('#header-mask');
        var headerShortUl = $('#header-shortcut-ul');

        commonHeaderJdkey.click(function () {
            headerMask.toggle();
            headerShortUl.toggle();
        });

        headerMask.click(function () {
            headerMask.toggle();
            headerShortUl.toggle();
        });
    }

    {
        // 返回按钮
        $('.m_header_bar .m_header_bar_back').click(function () {
            if (!$(this).hasClass('back_close')) {
                window.history.back();
            }
        });
        $('.jzx-header-icon-back').click(function () {
            if (!$(this).hasClass('back_close')) {
                window.history.back();
            }
        });

        $('.m_header_bar .m_header_bar_close').click(function () {
            $(this).parents('.modal_full').hide();
        });
    }


    {
        $('.WX_backtop').click(function () {
            $(document).scrollTop(0);
        });
    }
});

// 通用的选择地址框
var CommentAddressSelector = {
    show: function () {
        $('#addrSelector').show();
    },
    init: function (callback) {
        $('#addrSelector .close').off('click');
        $('#addrSelector .close').on('click', function () {
            $('#addrSelector').hide();
        });
        $('#addrSelector .mod_address_slide').off('click');
        $('#addrSelector .mod_address_slide').on('click', function () {
            $('#addrSelector').hide();
        });
        $('#addrSelector .mod_address_slide_main').off('click');
        $('#addrSelector .mod_address_slide_main').on('click', function (e) {
            e.stopPropagation();
        });

        $('#addrSelector .mod_address_slide_tabs_1 li[tag="province"]').click(function () {
            $('#addrSelector .mod_address_slide_tabs_1 li').removeClass('cur');
            $(this).addClass('cur');
            $('#addrSelector .mod_address_slide_list_2 li').hide();
            $('#addrSelector .mod_address_slide_list_2 li[tag="province"]').show();
        });
        $('#addrSelector .mod_address_slide_tabs_1 li[tag="city"]').click(function () {
            $('#addrSelector .mod_address_slide_tabs_1 li').removeClass('cur');
            $(this).addClass('cur');
            $('#addrSelector .mod_address_slide_list_2 li').hide();
            $('#addrSelector .mod_address_slide_list_2 li[tag="city"]').show();
        });
        $('#addrSelector .mod_address_slide_tabs_1 li[tag="county"]').click(function () {
            $('#addrSelector .mod_address_slide_tabs_1 li').removeClass('cur');
            $(this).addClass('cur');
            $('#addrSelector .mod_address_slide_list_2 li').hide();
            $('#addrSelector .mod_address_slide_list_2 li[tag="city"]').show();
        });

        var addressListEl = $('#addrSelector .mod_address_slide_list_2');

        $('#addrSelector .mod_address_slide_tabs_1 li[tag="province"] span').html('加载中...');
        $.ajax({
            url: $('#addrSelector').attr('province-url'),
            dataType: 'json',
            success: function (data) {
                $('#addrSelector .mod_address_slide_tabs_1 li[tag="province"] span').html('请选择');
                if (data) {
                    var html = [];
                    for (var i = 0; i < data.length; i++) {
                        html.push('<li tag="province" addrid="' + data[i]['code'] + '">' + data[i]['name'] + '</li>');
                    }
                    $('#addrSelector .mod_address_slide_list_2 li[tag="province"]').remove();
                    addressListEl.append(html.join(""));
                    $('#addrSelector .mod_address_slide_list_2 li[tag="province"]').click(function () {
                        var pid = $(this).attr('addrid');
                        var pname = $(this).html();
                        $('#addrSelector .mod_address_slide_list_2 li[tag="province"]').removeClass('on');
                        $(this).addClass('on');
                        $('#addrSelector .mod_address_slide_tabs_1 li[tag="province"] span').html(pname);
                        $('#addrSelector .mod_address_slide_tabs_1 li[tag="province"]').attr('addrid', pid);
                        $('#addrSelector .mod_address_slide_tabs_1 li').removeClass('cur');
                        $('#addrSelector .mod_address_slide_tabs_1 li[tag="city"]').addClass('cur');

                        $('#addrSelector .mod_address_slide_tabs_1 li[tag="city"] span').html('加载中...');
                        $.ajax({
                            url: $('#addrSelector').attr('city-url'),
                            dataType: 'json',
                            data: {pid: pid},
                            success: function (data) {
                                $('#addrSelector .mod_address_slide_tabs_1 li[tag="city"] span').html('请选择');
                                if (data) {
                                    var html2 = [];
                                    for (var i = 0; i < data.length; i++) {
                                        html2.push('<li tag="city" addrid="' + data[i]['code'] + '">' + data[i]['name'] + '</li>');
                                    }
                                    $('#addrSelector .mod_address_slide_list_2 li[tag="province"]').hide();
                                    $('#addrSelector .mod_address_slide_list_2 li[tag="city"]').remove();
                                    addressListEl.append(html2.join(""));
                                    $('#addrSelector .mod_address_slide_list_2 li[tag="city"]').click(function () {
                                        var cid = $(this).attr('addrid');
                                        var cname = $(this).html();
                                        $('#addrSelector .mod_address_slide_list_2 li[tag="city"]').removeClass('on');
                                        $(this).addClass('on');
                                        $('#addrSelector .mod_address_slide_tabs_1 li[tag="city"] span').html(cname);
                                        $('#addrSelector .mod_address_slide_tabs_1 li[tag="city"]').attr('addrid', cid);
                                        $('#addrSelector .mod_address_slide_tabs_1 li').removeClass('cur');
                                        $('#addrSelector .mod_address_slide_tabs_1 li[tag="county"]').addClass('cur');

                                        $('#addrSelector .mod_address_slide_tabs_1 li[tag="county"] span').html('加载中...');
                                        $.ajax({
                                            url: $('#addrSelector').attr('county-url'),
                                            dataType: 'json',
                                            data: {cid: cid},
                                            success: function (data) {
                                                $('#addrSelector .mod_address_slide_tabs_1 li[tag="county"] span').html('请选择');
                                                if (data) {
                                                    var html3 = [];
                                                    for (var i = 0; i < data.length; i++) {
                                                        html3.push('<li tag="county" addrid="' + data[i]['code'] + '">' + data[i]['name'] + '</li>');
                                                    }

                                                    $('#addrSelector .mod_address_slide_list_2 li[tag="city"]').hide();
                                                    $('#addrSelector .mod_address_slide_list_2 li[tag="county"]').remove();
                                                    addressListEl.append(html3.join(""));
                                                    $('#addrSelector .mod_address_slide_list_2 li[tag="county"]').click(function () {
                                                        var tid = $(this).attr('addrid');
                                                        var tname = $(this).html();
                                                        $('#addrSelector .mod_address_slide_list_2 li[tag="county"]').removeClass('on');
                                                        $(this).addClass('on');
                                                        $('#addrSelector .mod_address_slide_tabs_1 li[tag="county"] span').html(tname);
                                                        $('#addrSelector .mod_address_slide_tabs_1 li[tag="county"]').attr('addrid', tid);
                                                        $('#addrSelector .mod_address_slide_tabs_1 li').removeClass('cur');
                                                        $('#addrSelector .mod_address_slide_tabs_1 li[tag="county"]').addClass('cur');


                                                        $('#addrSelector').hide();
                                                        if (callback) {
                                                            var data = {
                                                                province: {code: pid, name: pname},
                                                                city: {code: cid, name: cname},
                                                                county: {code: tid, name: tname}
                                                            };
                                                            callback(data);
                                                        }
                                                    });
                                                    var initTid = $('#addrSelector').attr('tid');
                                                    if (initTid) {
                                                        $('#addrSelector .mod_address_slide_list_2 li[tag="county"][addrid="' + initTid + '"]').trigger('click');
                                                        $('#addrSelector').attr('tid', null);
                                                    }
                                                }
                                            },
                                            error: function (xhr) {
                                                CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
                                            }
                                        });
                                    });

                                    var initCid = $('#addrSelector').attr('cid');
                                    if (initCid) {
                                        $('#addrSelector .mod_address_slide_list_2 li[tag="city"][addrid="' + initCid + '"]').trigger('click');
                                        $('#addrSelector').attr('cid', null);
                                    }
                                }
                            },
                            error: function (xhr) {
                                CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
                            }
                        });
                    });

                    var initPid = $('#addrSelector').attr('pid');
                    if (initPid) {
                        $('#addrSelector .mod_address_slide_list_2 li[tag="province"][addrid="' + initPid + '"]').trigger('click');
                        $('#addrSelector').attr('pid', null);
                    }
                }
            },
            error: function (xhr) {
                CommentUtils.closeWait();
                CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
            }
        })
    }
};

var CommentUtils = {
    wait: function (html) {
        if (!html) html = "请求加载中...";
        $(document.body).append(
            $('<div class="wx_loading">' +
                '    <div class="wx_loading_inner">' +
                '        <i class="wx_loading_icon"></i>' +
                '        <span>' + html + '</span>' +
                '    </div>' +
                '</div>'));
    },
    closeWait: function () {
        $('.wx_loading').remove();
    },
    alert: function (title, html) {
        if (!title) title = "提示";
        var modAlert = $(
            '<div class="mod_alert mod_alert_info fixed show">' +
            '    <span class="close"></span>' +
            '    <h3 class="title">' + title + '</h3>' +
            '    <div class="inner">' + html + '</div>' +
            '    <p class="btns">' +
            '        <a href="javascript:void(0);" class="btn btn_1">确定</a>' +
            '    </p>' +
            '</div>' +
            '<div class="mod_alert_mask" style="display: block"></div>');
        $(document.body).append(modAlert);
        modAlert.find('.close').on('click', function () {
            modAlert.remove();
            $('.mod_alert_mask').remove();
        });
        modAlert.find('.btn_1').on('click', function () {
            modAlert.remove();
            $('.mod_alert_mask').remove();
        });
    },
    confirm: function (html, callback, btn, icon) {
        if (!btn) btn = '确定';
        var modAlert = $(
            '<div class="mod_alert show fixed">' +
            (icon ? '    <i class="icon"></i>' : '') +
            '    <p>' + html + '</p>' +
            '    <p class="btns">' +
            '        <a href="javascript:;" class="btn btn_close">取消</a>' +
            '        <a href="javascript:;" class="btn btn_1">' + btn + '</a>' +
            '    </p>' +
            '</div>' +
            '<div class="mod_alert_mask" style="display: block"></div>'
        );
        $(document.body).append(modAlert);
        modAlert.find('.btn_close').on('click', function () {
            modAlert.remove();
            $('.mod_alert_mask').remove();
        });
        modAlert.find('.btn_1').on('click', function () {
            modAlert.remove();
            $('.mod_alert_mask').remove();
            callback(this);
        });
    }
};
