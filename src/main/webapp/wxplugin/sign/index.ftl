<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport"
          content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
    <title>简子行签到管理</title>
    <link rel="stylesheet" href="${JZXUrl(Request)}/wxplugin/sign/css/index.css">
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
</head>
<body>
<div class="user-info" style="background-color: #37b05a;color: #D9EADE">
    <div class="ui-header">
        <div class="ui-logo">
            <img src="${JZXFile(Request,user['avatar'])}"/>
        </div>
        <div class="ui-day">
            <div>您已连续签到</div>
            <div>
                <span style="color: #ffffff">
                    <#if sign['record']??>
                        ${sign['record']['cntCount']}
                    <#else>
                        0
                    </#if>
                </span>
                天
            </div>
        </div>
    </div>
    <div class="ui-text">
        <span>您好
            <span class="ui-text-hl">
                <#if user['nick']??>
                    ${JZXUrlDecode(user['nick'])}
                <#else>
                    用户
                </#if>
            </span>
            ，您可以在下方链接"查看奖励"
        </span>
    </div>
</div>
<div class="sign-body">
    <div class="sb-labels">
        <#if timeout==1>
            <div class="empty"><span>签到活动还未开始</span></div>
        <#elseif timeout==2>
            <div class="empty"><span>签到活动已经结束</span></div>
        <#else>
            <ul>
                <#if sign??>
                    <#list sign['days'] as day>
                        <#if day['signed']?? && day['signed']>
                            <li class="signed">
                                <span>${day['day']}</span>
                                <div class="sb-end"></div>
                            </li>
                        <#else>
                            <#if day['hasRealAward']??>
                                <li>
                                    <span>${day['day']}</span>
                                    <div class="gift"></div>
                                </li>
                            <#else>
                                <li>
                                    <span>${day['day']}</span>
                                </li>
                            </#if>
                        </#if>
                    </#list>
                </#if>
                <#--
                <li class="curr">
                    <div class="curr-top">第180天</div>
                    <div class="curr-line"></div>
                    <div class="curr-bottom big">+40<span class="name">财富值</span></div>
                </li>
                -->
            </ul>
        </#if>
    </div>

    <div class="sb-box">
        <div>
            <span>
                <#if timeout==0>
                    <#if sign['nextAward']??>
                        <#if sign['nextAward']['dayCount']==0>
                            <#if sign['nextAward']['type']==1>
                                今日奖励${sign['nextAward']['count']}${sign['nextAward']['name']}
                            <#else>
                                今日签到可获得${sign['nextAward']['name']}
                            </#if>
                        <#else>
                            <#if sign['nextAward']['type']==1>
                                ${sign['nextAward']['dayCount']}天后签到奖励${sign['nextAward']['count']}${sign['nextAward']['name']}
                            <#else>
                                ${sign['nextAward']['dayCount']}天后签到可获得${sign['nextAward']['name']}
                            </#if>
                        </#if>
                    <#else>
                        今日签到无奖励
                    </#if>
                <#else>
                    今日签到无奖励
                </#if>
            </span>
        </div>
        <div class="button"><span>签到</span></div>
        <div class="link"><a href="${JZXUrl(Request)}/wxplugin/sign/${groupCode}/award.jhtml">查看奖励</a></div>
    </div>
    <div class="sb-detail">
        <span>${sign['detail']}</span>
    </div>
</div>
<script type="text/javascript">
    $(function () {
        $(".sign-body .sb-box .button").click(function () {
            $.post('${JZXUrl(Request)}/wxplugin/sign/${groupCode}/click.jhtml', {}, function (r) {
                if (r == 'ok') {
                    window.location.reload();
                } else if (r == 'auth_error') {
                    alert('登录信息检查失败')
                }
            });
        });
    })
</script>
</body>
</html>