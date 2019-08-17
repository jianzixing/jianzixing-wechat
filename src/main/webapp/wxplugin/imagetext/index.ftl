<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport"
          content="width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=0,viewport-fit=cover">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">

    <title></title>
    <link rel="stylesheet" href="/wxplugin/imagetext/css/index.css">
</head>
<body>
<div class="page-content">
    <h2 class="rich_media_title"><#if obj??>${obj['title']}<#else>图文消息标题</#if></h2>
    <div class="rich_media_meta_list">
        <span class="rich_media_meta rich_media_meta_text">作者</span>
        <span class="rich_media_meta rich_media_meta_nickname" id="profileBt">
            <a href="javascript:void(0);" id="js_name"><#if obj??>${obj['author']}<#else>简子行科技</#if></a>
        </span>
        <em id="publish_time"
            class="rich_media_meta rich_media_meta_text"><#if obj??>${obj['time']}<#else>2019-01-01</#if></em>
    </div>
    <div class="rich_media_content">
        <#if obj??>${obj['content']}<#else><p>内容</p></#if>
    </div>
</div>
</body>
</html>

