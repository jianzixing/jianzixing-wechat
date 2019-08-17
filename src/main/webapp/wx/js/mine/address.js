$(function () {
    // 自定义标签
    $('#adlabelP .user_tag').click(function () {
        $('.mod_alert_mask').addClass('show');
        $('#userLabelDiv').addClass('show');
    });

    $('#labelCancel').click(function () {
        $('.mod_alert_mask').removeClass('show');
        $('#userLabelDiv').removeClass('show');
    });

    $('#labelSure').click(function () {
        $('.mod_alert_mask').removeClass('show');
        $('#userLabelDiv').removeClass('show');
    });

    $('#adlabelP  .address_tags_tag_wrap .address_tags_edit').click(function () {
        $('.mod_alert_mask').addClass('show');
        $('#userLabelDiv').addClass('show');
        $('#labelInput').val($('#userLabelVal').attr('value'));
    });

    $('#adlabelP .address_tags_tag').click(function () {
        $('#adlabelP .address_tags_tag').removeClass('cur');
        if (!$(this).hasClass('user_tag')) {
            $(this).addClass('cur');
        }
    });
    $('#userLabelDiv .btn_1').click(function () {
        var name = $('#labelInput').val();
        $('#userLabelVal').parent().show();
        $('#userLabelVal').attr('value', name);
        $('#userLabelVal').html(name);
        $('#adlabelP .address_tags_tag').removeClass('cur');
        $('#userLabelVal').addClass('cur');
        $('#adlabelP .user_tag').hide();
    });


    CommentAddressSelector.init(function (data) {
        $('#selAddr').find('input[type="text"]').val(
            data['province']['name'] +
            data['city']['name'] +
            data['county']['name']
        );
        $('#selAddr').data('data', data);
    });

    // 选择地区
    $('#selAddr').click(function () {
        CommentAddressSelector.show();
    });

    // 输入框
    $('#info_clear').click(function () {
        $('#adinfo').val('');
    });

    $('#editAddrSetDef').click(function () {
        $('#editAddrSetDef').toggleClass('selected');
    });

    $('#delAddress').click(function () {
        CommentUtils.confirm('确定删除收货地址？', function () {
            $.ajax({
                url: $('#delAddress').attr('url'),
                success: function (data) {
                    if (data) {
                        if (data == 'not_login') {
                            CommentUtils.alert('请求出错', '请登录后再添加收货地址');
                        }
                        if (data == 'ok') {
                            window.location.href = $('#wrapBody').attr('url');
                        }
                    }
                },
                error: function (xhr) {
                    CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
                }
            });
        }, '删除地址')
    });

    $('#submitAddress').click(function () {
        var name = $('#name').val();
        var mobile = $('#mobile').val();
        var addrObj = $('#selAddr').data('data');
        var info = $('#adinfo').val();
        var label = $('#adlabelP .address_tags_tag.cur').attr('value');


        if (!name) {
            CommentUtils.alert('缺少内容', '请填写收货人');
            return;
        }
        if (!mobile) {
            CommentUtils.alert('缺少内容', '请填写联系方式');
            return;
        }
        if (!addrObj) {
            CommentUtils.alert('缺少内容', '请选择所在地区');
            return;
        }
        if (!info) {
            CommentUtils.alert('缺少内容', '请填写详细地址');
            return;
        }

        var data = {
            realName: name,
            phoneNumber: mobile,
            provinceCode: addrObj['province']['code'],
            cityCode: addrObj['city']['code'],
            countyCode: addrObj['county']['code'],
            address: info,
            label: label,
            isDefault: $('#editAddrSetDef').hasClass('selected') ? 1 : 0
        };
        if ($('#wrapBody').attr('data-id')) {
            data['id'] = $('#wrapBody').attr('data-id');
        }

        $.ajax({
            url: $(this).attr('url'),
            data: {data: JSON.stringify(data)},
            success: function (data) {
                if (data) {
                    if (data == 'not_login') {
                        CommentUtils.alert('请求出错', '请登录后再添加收货地址');
                    }
                    if (data == 'miss_params') {
                        CommentUtils.alert('请求出错', '请将选项填写完整');
                    }
                    if (data == 'miss_pk') {
                        CommentUtils.alert('请求出错', '缺少更新主键');
                    }
                    if (data == 'ok') {
                        window.location.href = $('#wrapBody').attr('url');
                    }
                }
            },
            error: function (xhr) {
                CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
            }
        });
    });
});
