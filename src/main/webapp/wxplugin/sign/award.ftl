<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport"
          content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
    <title>简子行签到奖励</title>
    <link rel="stylesheet" href="${JZXUrl(Request)}/wxplugin/sign/css/award.css">
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
</head>
<body>
<div class="award-body">
    <div class="award-nav">
        <a class="back" href="${JZXUrl(Request)}/wxplugin/sign/${groupCode}/index.jhtml"></a>
        <div class="title"><span>我的奖励</span></div>
    </div>
    <div class="award-list">
        <div class="tab">
            <div id="not_use" class="item curr">
                <div class="text">未使用</div>
            </div>
            <div id="is_use" class="item">
                <div class="text">已使用</div>
            </div>
        </div>
        <div id="not_use_list" class="contents">
            <#if nu?? && ud?size gt 0>
                <#list nu as item>
                    <div class="item">
                        <div class="goods">
                            <div class="img">
                                <img src="${JZXFile(item['award']['icon'])}"/>
                            </div>
                            <div class="title">
                                <span>${item['award']['subName']}</span>
                            </div>
                        </div>
                        <div class="amount">
                            <div class="div1">
                                总计数量
                                <span class="number"><#if item['type']==1>${item['totalAmount']}<#else>1</#if></span>
                            </div>
                            <div class="div2">未使用</div>
                        </div>
                    </div>
                </#list>
            <#else>
                <div class="empty_contents"><span>没有获得奖励</span></div>
            </#if>
        </div>
        <div id="is_use_list" class="contents" style="display: none">
            <#if ud?? && ud?size gt 0>
                <#list ud as item>
                    <div class="item">
                        <div class="goods">
                            <div class="img">
                                <img src="${JZXFile(item['award']['icon'])}"/>
                            </div>
                            <div class="title">
                                <span>${item['award']['subName']}</span>
                            </div>
                        </div>
                        <div class="amount">
                            <div class="div1">
                                总计数量
                                <span class="number"><#if item['type']==1>${item['totalAmount']}<#else>1</#if></span>
                            </div>
                            <div class="div2" style="color:#aa2222;">已使用</div>
                        </div>
                    </div>
                </#list>
            <#else>
                <div class="empty_contents"><span>没有获得奖励</span></div>
            </#if>
        </div>
    </div>
</div>
<script type="text/javascript">
    $(function () {
        $("#not_use").click(function () {
            $("#not_use_list").show();
            $("#is_use_list").hide();
            $("#not_use").addClass('curr');
            $("#is_use").removeClass('curr');
        });
        $("#is_use").click(function () {
            $("#not_use_list").hide();
            $("#is_use_list").show();
            $("#not_use").removeClass('curr');
            $("#is_use").addClass('curr');
        });
    })
</script>
</body>
</html>