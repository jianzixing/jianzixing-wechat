<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge, chrome=1"/>
    <meta name="renderer" content="webkit"/>
    <meta name="viewport"
          content="width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1, user-scalable=no"/>
    <meta name="format-detection" content="telephone=no"/>

    <title>${title}</title>
    <meta name="description" content=""/>
    <meta name="keywords" content=""/>
</head>
<body style="margin: 0px;padding: 0px">
<div style="width: 100%;height: 100%;position: fixed;left: 0px;top: 0px;">
    <div style="width: 100%;margin-top: 30%;overflow: hidden">
        <img style="display:block;width: 128px;height: 128px;margin: auto"
             src="${JZXUrl(Request)}/common/img/failure.png"/>
    </div>
    <div style="text-align: center;overflow: hidden;font-size: 18px;padding: 20px">
        <span>${desc}</span>
    </div>
    <#if url??>
        <a href="${url}"
           style="display:block;margin: 30px 10% 0;background-color: #3eb94e;border-radius: 5px;padding: 10px;color: #ffffff;text-align: center;text-decoration: none">
            <span>
                <#if button??>
                    ${button}
                <#else>
                    返回首页
                </#if>
            </span>
        </a>
    </#if>
</div>
</body>