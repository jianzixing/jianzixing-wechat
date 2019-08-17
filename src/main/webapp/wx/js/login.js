$(function () {
    $('#username').on('keyup', function () {
        var username = $('#username').val();
        if (username && username.length > 0) {
            $('#username').parent().find('.icon-clear').show();
            $('#loginBtn').css({opacity: 1});
        } else {
            $('#loginBtn').css({opacity: 0.3});
        }
    });
    $('#username').parent().find('.icon-clear').click(function () {
        $('#username').val('');
        $('#loginBtn').css({opacity: 0.3});
        $('#username').parent().find('.icon-clear').hide();
    });
    $('#password').on('keyup', function () {
        var password = $('#password').val();
        if (password && password.length > 0) {
            $('#password').parent().find('.icon-clear').show();
        }
    });
    $('#password').parent().find('.icon-clear').click(function () {
        $('#password').val('');
        $('#password').parent().find('.icon-clear').hide();
    });

    $('#onOff_pwd').change(function () {
        if ($('#onOff_pwd').is(':checked')) {
            $('#password').replaceWith(
                '<input id="password" type="text" ' +
                'class="acc-input password txt-input" ' +
                'value="' + $('#password').val() + '" ' +
                'placeholder="请输入密码" autocomplete="off">')
        } else {
            $('#password').replaceWith(
                '<input id="password" type="password" ' +
                'class="acc-input password txt-input" ' +
                'value="' + $('#password').val() + '" ' +
                'placeholder="请输入密码" autocomplete="off">')
        }

        $('#password').on('keyup', function () {
            var password = $('#password').val();
            if (password && password.length > 0) {
                $('#password').parent().find('.icon-clear').show();
            }
        });
    });

    $('#loginBtn').click(function () {
        var opacity = $(this).css('opacity');
        if (opacity == '1') {
            var username = $('#username').val();
            var password = $('#password').val();
            var redirect = $('#wrapBody').attr('data-back');

            if (username && password) {
                CommentUtils.wait();
                $.ajax({
                    url: 'login_submit.jhtml',
                    data: {userName: username, password: password},
                    success: function (resp) {
                        CommentUtils.closeWait();
                        if (resp == 'ok') {
                            if (redirect && redirect.trim() != '') {
                                window.location.href = redirect;
                            } else {
                                window.location.href = "index.jhtml";
                            }
                        } else if (resp == 'user_disable') {
                            CommentUtils.alert('登录', '该用户被禁用');
                        } else if (resp == 'pwd_valid') {
                            CommentUtils.alert('登录', '密码输入错误');
                        } else if (resp == 'user_empty') {
                            CommentUtils.alert('登录', '该用户不存在');
                        } else if (resp == 'fail') {
                            CommentUtils.alert('登录', '服务器错误');
                        }
                    },
                    error: function (xhr) {
                        CommentUtils.closeWait();
                        CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
                    }
                });
            } else {
                CommentUtils.alert('缺少参数', '请输入完整的用户名和密码');
            }
        }
    });
});
