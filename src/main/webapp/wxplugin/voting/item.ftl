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
<div class="item-nav">
    <a class="back" href="${JZXUrl(Request)}/wxplugin/voting/${groupCode}/index.jhtml"></a>
    <div class="title"><span>投票信息</span></div>
</div>
<div class="box">
    <div class="title"><span>${item['subName']}</span></div>
    <div class="detail">
        ${item['detail']}
    </div>
</div>
</body>
</html>