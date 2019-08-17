<html>
<head>
    <title>简子行微信管理平台登陆</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="../resources/code/css/yoosal.css" type="text/css">
    <script type="text/javascript" src="../resources/code/js/yoosal.js"></script>
    <script type="text/javascript" src="../resources/code/js/jianzixing.js"></script>

    <script src="lib/jquery/jquery.min.js"></script>
    <script src="lib/jquery/jquery.cookie.min.js"></script>
    <style type="text/css">
        body {
            background: url("image/login_bg.jpeg") no-repeat fixed;
            /* set background tensile */
            background-size: 100% 100%;
            -moz-background-size: 100% 100%;
            margin: 0;
            padding: 0;
        }

        #content {
            background-color: rgba(255, 255, 255, 0.95);
            width: 420px;
            height: 370px;
            border: 1px solid #FAFAFA;
            border-radius: 6px;
            padding: 10px;
            margin-top: 10%;
            margin-left: auto;
            margin-right: auto;
            display: block;
        }

        .login-header {
            width: 100%;
            height: 48px;
            margin-bottom: 20px;
            border-bottom: 1px solid #dcdcdc;
            text-align: center;
            line-height: 40px;
            color: #666666;
            font-size: 22px;
        }

        .login-header img {
            width: 120px;
            margin-left: auto;
            margin-right: auto;
            display: block;
        }

        .login-input-box {
            margin-top: 12px;
            width: 100%;
            margin-left: auto;
            margin-right: auto;
            display: inline-block;
        }

        .login-input-box input {
            width: 385px;
            height: 42px;
            margin-left: 18px;
            border: 1px solid #dcdcdc;
            border-radius: 4px;
            padding-left: 42px;
            font-size: 13px;
        }

        .login-input-box input:hover {
            border: 1px solid #ff7d0a;
        }

        .login-input-box input:after {
            border: 1px solid #ff7d0a;
        }

        .login-input-box .icon {
            width: 24px;
            height: 24px;
            margin: 11px 4px 11px 24px;
            background-color: #ff7d0a;
            display: inline-block;
            position: absolute;
            border-right: 1px solid #dcdcdc;
        }

        .login-input-box .icon.icon-user {
            background: url("image/user.png") no-repeat;
        }

        .login-input-box .icon.icon-password {
            background: url("image/password.png") no-repeat;
        }

        .remember-box {
            width: auto;
            height: auto;
            margin-left: 18px;
            margin-top: 12px;
            font-size: 12px;
            color: #6a6765;
        }

        .login-button-box {
            margin-top: 12px;
            width: 100%;
            margin-left: auto;
            margin-right: auto;
            display: inline-block;
        }

        .login-button-box button {
            background-color: #ff7d0a;
            color: #ffffff;
            font-size: 16px;
            width: 386px;
            height: 40px;
            margin-left: 18px;
            border: 1px solid #ff7d0a;
            border-radius: 4px;
        }

        .login-button-box button:hover {
            background-color: #ee7204;
        }

        .login-button-box button:active {
            background-color: #ee7204;
        }

        .logon-box {
            margin-top: 20px;
            text-align: center;
        }

        .logon-box a {
            margin: 30px;
            color: #4a4744;
            font-size: 13px;
            text-decoration: none;
        }

        .logon-box a:hover {
            color: #ff7d0a;
        }

        .logon-box a:active {
            color: #ee7204;
        }
    </style>
    <script type="text/javascript">
        $(function () {
            function checkCookie() {
                if ($('input[type=checkbox]').is(':checked')) {
                    $.cookie('_admin_name', $('input[name=userName]').val(), {expires: 365})
                    $.cookie('_is_remember', 1, {expires: 365})
                } else {
                    $.cookie('_admin_name', null, {expires: 0});
                    $.cookie('_is_remember', null, {expires: 0})
                }
            }

            $('input[type=checkbox]').change(function () {
                checkCookie();
            });

            if ($.cookie('_admin_name')) {
                $('input[name=userName]').val($.cookie('_admin_name'));
            }
            if ($.cookie('_is_remember') == 1) {
                $('input[type=checkbox]').attr('checked', true);
            } else {
                $('input[type=checkbox]').attr('checked', false);
            }

            $('#submit').click(function () {
                checkCookie();
                $.post("/admin/admin/login.jhtml",
                    {
                        userName: $('input[name=userName]').val(),
                        password: $('input[name=password]').val()
                    },
                    function (result) {
                        if (result.code == 100) {
                            window.location.href = "index.jhtml"
                        } else if (result.code == -130) {
                            alert("验证码输入错误,请重新点击按钮验证");
                        } else {
                            alert(result.msg);
                        }
                    },
                    'json'
                );
            })
        })
    </script>
</head>
<body>
<div id="content">
    <div class="login-header">
        <#--<img src="assets/images/logo">-->
        <span>简子行微信管理平台登陆</span>
    </div>
    <form>
        <div class="login-input-box">
            <span class="icon icon-user"></span>
            <input name="userName" type="text" placeholder="请输入您的用户名">
        </div>
        <div class="login-input-box">
            <span class="icon icon-password"></span>
            <input name="password" type="password" placeholder="请输入您的密码">
        </div>
        <div class="login-input-box">
            <div class="yoosal_box" style="width:380px;margin-left: auto;margin-right: auto"></div>
        </div>
    </form>
    <div class="remember-box">
        <label>
            <input type="checkbox"><span>记住我</span>
        </label>
    </div>
    <div id="submit" class="login-button-box">
        <button>登陆</button>
    </div>
    <div class="logon-box">
        <div style="color: #BBBBBB;font-size: 13px">如忘记密码请联系管理员(不支持低版本IE)</div>
    </div>
</div>
</body>
</html>
