<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>用户登录</title>
    <meta name="keywords" content="">
    <meta name="description" content="">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no, viewport-fit=cover">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/common.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/login.css">
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/velocity.min.js"></script>
</head>
<body>
<div class="wx_wrap" id="wrapBody" data-back="${r}">
    <div class="m_header" style="">
        <div class="m_header_bar">
            <div class="m_header_bar_back"></div>
            <div class="m_header_bar_title">用户登录</div>
        </div>
    </div>

    <div class="page">
        <div id="username_login" class="login-wrap login-wrap-active">
            <div class="input-container">
                <input id="username" class="acc-input username txt-input" type="text" value=""
                       placeholder="用户名/邮箱/已验证手机" autocomplete="off">
                <i class="icon icon-clear"></i>
            </div>
            <div class="input-container">
                <input id="password" type="password" class="acc-input password txt-input" placeholder="请输入密码"
                       autocomplete="off">
                <i class="icon icon-clear"></i>
                <label class="label-checkbox">
                    <input id="onOff_pwd" type="checkbox">
                    <div class="checkbtn">
                    </div>
                </label>
                <button class="findpwd" data-url="${JZXUrl(Request,'/wx/forget_pwd.jhtml?r='+r)}">
                    忘记密码
                </button>
            </div>
        </div>

        <!--短信验证登录-->
        <div id="sms_login" class="login-wrap">
            <div class="sms-input-box">
                <div class="input-container">
                    <label class="area-box">
                        <span class="area_code" code="86">+86</span>
                        <i class="area_icon"></i>
                    </label>
                    <input type="tel" id="telphone" class="acc-input telphone sms-txt-input"
                           placeholder="请输入手机号">
                    <i class="icon icon-clear"></i>
                </div>
                <button class="mesg-code mesg-disable">
                    获取验证码
                </button>
            </div>
            <div class="input-container">
                <input id="telCode" class="acc-input telCode sms-txt-input" type="tel"
                       oninput="if(value.length>6) value=value.slice(0,6);" placeholder="请输入收到的验证码"
                       autocomplete="off">
                <i class="icon icon-clear"></i>
            </div>
        </div>

        <div class="notice">&nbsp;</div>
        <a href="javascript:;" id="loginBtn" class="btn">登 录</a>
        <div class="quick-nav clearfix">
            <a content-id="sms_login" href="javascript:;" class="planBLogin" style="display:none">
                <span class="txt-planBLogin">短信验证码登录</span>
            </a>
            <a style="display:" href="${JZXUrl(Request,'/wx/register.jhtml?r='+r)}" class="quickReg">
                <span class="txt-quickReg">手机快速注册</span>
            </a>
        </div>
        <div class="login-type">
            <div class="agreement-tips">
                <p>登录即代表您已同意<a href="">网站隐私政策</a></p>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/login.js"></script>
</body>
</html>
