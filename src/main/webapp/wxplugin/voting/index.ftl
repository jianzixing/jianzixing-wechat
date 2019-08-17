<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport"
          content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
    <title>简子行投票管理</title>
    <link rel="stylesheet" href="${JZXUrl(Request)}/wxplugin/voting/css/index.css">
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
</head>
<body>
<div class="box">
    <div class="title"><span><#if voting??>${voting['name']}</#if></span></div>
    <div class="sub-title">
        <#if voting??>
            <span>投票时间 ${JZXDateFormat(voting['startTime'])} 到 ${JZXDateFormat(voting['finishTime'])}</span>
        <#else>
            <span>投票时间无法确定</span>
        </#if>
    </div>

    <#if timeout==1>
        <div class="empty"><span>投票活动还未开始</span></div>
    <#elseif timeout==2>
        <div class="empty"><span>投票活动已经结束</span></div>
    <#else>
        <ul class="list">
            <#if voting??>
                <#if voting['items']??>
                    <#list voting['items'] as item>
                        <li class="item">
                            <div class="name"><span>${item['name']}</span></div>
                            <a href="${JZXUrl(Request)}/wxplugin/voting/${groupCode}/item.jhtml?id=${item['id']}">
                                <div class="pic"><img src="${JZXFile(item['icon'])}"/></div>
                            </a>
                            <div class="info">
                                <div class="text">${item['count']} 票</div>
                                <#if item['userVoting']?? && item['userVoting']>
                                    <div class="btn disable"><span>已投票</span></div>
                                <#else>
                                    <div class="btn" data-id="${item['id']}" data-name="${item['name']}">
                                        <span>我要投票</span>
                                    </div>
                                </#if>
                            </div>
                        </li>
                    </#list>
                </#if>
            </#if>
        </ul>
    </#if>
    <div class="detail">
        <#if voting??>
            ${voting['detail']}
        </#if>
    </div>
</div>
<script type="text/javascript">
    $(function () {
        $(".box .list .info .btn").click(function () {
            var $self = $(this);
            if (!$self.hasClass('disable')) {
                var iid = $self.attr('data-id');
                var name = $self.attr('data-name');
                var btn = confirm("确定投票给\"" + name + "\"吗？");
                if (btn) {
                    $.post('${JZXUrl(Request)}/wxplugin/voting/${groupCode}/click.jhtml', {iid: iid}, function (r) {
                        if (r == 'ok') {
                            window.location.reload();
                        } else if (r == 'auth_error') {
                            alert('登录信息检查失败')
                        } else if (r == 'countout') {
                            alert('投票次数已用完')
                        } else if (r == 'already') {
                            alert('您已经投过票')
                        }
                    });
                }
            }
        });
    })
</script>
</body>
</html>