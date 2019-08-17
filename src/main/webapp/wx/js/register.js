$(function () {
    $('.input-container input').on('keyup', function () {
        var val = $(this).val();
        if (val && val.length > 0) {
            $(this).parent().find('.icon-clear').show();
            if (this.id == 'telphone') {
                $('#loginBtn').css({opacity: 1});
            }
        } else {
            if (this.id == 'telphone') {
                $('#loginBtn').css({opacity: 0.3});
            }
        }
    });

    $('.input-container input').parent().find('.icon-clear').click(function () {
        $(this).parent().find('input').val('');
        $(this).hide();
        if ($(this).parent().find('input')[0].id == 'telphone') {
            $('#loginBtn').css({opacity: 0.3});
        }
    });

    var ms = 120, handler = 0;

    function sendMessage() {
        $('.mesg-code').html("剩余" + (ms--) + "s");
        if (ms < 0) {
            $('.mesg-code').html("获取验证码");
            clearInterval(handler);
            ms = 120;
        }
    }

    $('.mesg-code').click(function () {
        if (ms >= 120) {
            var phone = $('#telphone').val();
            if (phone && phone.length >= 11) {
                CommentUtils.wait();
                $.ajax({
                    url: 'register_sms.jhtml', data: {phone: phone}, success: function (resp) {
                        CommentUtils.closeWait();
                        if (resp == 'ok') {
                            sendMessage();
                            handler = setInterval(sendMessage, 1000);
                        } else if (resp == 'time_in') {
                            CommentUtils.alert('验证码', '验证码发送过于频繁');
                        } else if (resp == 'args_null') {
                            CommentUtils.alert('验证码', '请输入完整手机号码');
                        }
                    },
                    error: function (xhr) {
                        CommentUtils.closeWait();
                        CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
                    }
                });
            } else {
                CommentUtils.alert('缺少参数', '请输入完整手机号码');
            }
        }
    });

    $('#loginBtn').click(function () {
        var opacity = $(this).css('opacity');
        if (opacity == '1') {
            var phone = $('#telphone').val();
            var password = $('#password').val();
            var code = $('#telCode').val();

            if (phone && phone.length >= 11 && password && code) {
                CommentUtils.wait();
                $.ajax({
                    url: 'register_submit.jhtml',
                    data: {phone: phone, password: password, code: code},
                    success: function (resp) {
                        CommentUtils.closeWait();
                        if (resp == 'ok') {
                            CommentUtils.confirm('注册成功，是否马上登录？', function () {
                                window.location.href = "login.jhtml";
                            }, '立即登录');
                        } else if (resp == 'empty_code') {
                            CommentUtils.alert('验证码', '请先发送手机验证码');
                        } else if (resp == 'code_expire') {
                            CommentUtils.alert('验证码', '验证码已过期');
                        } else if (resp == 'code_ne') {
                            CommentUtils.alert('验证码', '验证码输入不正确');
                        } else if (resp == 'user_exist') {
                            CommentUtils.confirm('该用户已经注册，是否立即登录？', function () {
                                window.location.href = "login.jhtml";
                            }, '立即登录');
                        }
                    },
                    error: function (xhr) {
                        CommentUtils.closeWait();
                        CommentUtils.alert('请求出错', '网络错误:' + xhr.status);
                    }
                });
            } else {
                CommentUtils.alert('缺少参数', '请输入完整手机号码、登录密码以及验证码');
            }
        }
    });
});
