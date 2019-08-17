<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport"
          content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
    <title>幸运转盘</title>
    <link rel="stylesheet" href="css/index.css">
    <link rel="stylesheet" href="http://icono-49d6.kxcdn.com/icono.min.css">
    <style>
        body {
            margin: 0px;
            padding: 0px;
            background-color: #e84144;
            display: table-cell;
            vertical-align: middle;
        }

        .bg {
            width: 0px;
            height: 0px;
            margin: auto;
        }

        .bg img {
            width: 100%;
        }

        .lp {
            position: absolute;
            width: 100%;
            top: 0px;
            left: 0px;
        }

        .md {
            position: absolute;
            width: 100%;
            color: #ffe5aa;
            line-height: 30px;
            font-size: 15px;
        }

        .md span {
            display: block;
            text-align: center;
        }
    </style>
</head>
<body ontouchstart>
<div id="app" class="bg">
    <img id="app_bg" src="img/bg.png"/>
    <div class="lp">
        <section id="gbWheel" class="gb-wheel-container">
            <div class="gb-wheel-content gb-wheel-run">
                <ul class="gb-wheel-line"></ul>
                <div class="gb-wheel-list"></div>
            </div>
            <a href="javascript:;" class="gb-wheel-btn" id="gbLottery">抽奖</a>
        </section>
    </div>
    <div id="gbLuck" class="md">
        <span>谢二抽中一台MAC笔记本</span>
        <span>谢一抽中一个键盘</span>
        <span>谢六抽中一包纸巾</span>
    </div>
</div>
<script type="text/javascript">
    (function () {
        var width = window.screen.width;
        var height = window.screen.height;
        var app = document.getElementById("app");
        var appImgBg = document.getElementById("app_bg");
        var gbWheel = document.getElementById("gbWheel");
        var gbLuck = document.getElementById("gbLuck");
        appImgBg.style.width = width + "px";
        app.style.width = width + "px";
        app.style.height = height + "px";

        var imgHeight = appImgBg.offsetHeight;
        gbWheel.style.transform = "scale(" + (imgHeight / 835) + ")";
        gbWheel.style.top = (imgHeight * 0.25) * (imgHeight / 835) + "px";
        gbLuck.style.top = (imgHeight * 0.83) + "px";

        // 奖品配置
        var awards = [
                {'index': 0, 'text': '耳机', 'name': 'icono-headphone'},
                {'index': 1, 'text': 'iPhone', 'name': 'icono-iphone'},
                {'index': 2, 'text': '相机', 'name': 'icono-camera'},
                {'index': 3, 'text': '咖啡杯', 'name': 'icono-cup'},
                {'index': 4, 'text': '日历', 'name': 'icono-calendar'},
                {'index': 5, 'text': '键盘', 'name': 'icono-keyboard'},
                {'index': 6, 'text': '键盘', 'name': 'icono-keyboard'}
            ],
            len = awards.length,
            turnNum = 1 / len;  // 文字旋转 turn 值

        var gbWheel = $('gbWheel'),
            lineList = gbWheel.querySelector('ul.gb-wheel-line'),
            itemList = gbWheel.querySelector('.gb-wheel-list'),
            lineListHtml = [],
            itemListHtml = [];

        var transform = preTransform();

        awards.forEach(function (v, i, a) {
            // 分隔线
            lineListHtml.push('<li class="gb-wheel-litem" style="' + transform + ': rotate(' + (i * turnNum + turnNum / 2) + 'turn)"></li>');

            // 奖项
            itemListHtml.push('<div class="gb-wheel-item">');
            itemListHtml.push('<div class="gb-wheel-icontent" style="' + transform + ': rotate(' + (i * turnNum) + 'turn)">');
            itemListHtml.push('<p class="gb-wheel-iicon">');
            itemListHtml.push('<i class="' + v.name + '"></i>');
            itemListHtml.push('</p>');
            itemListHtml.push('<p class="gb-wheel-itext">');
            itemListHtml.push(v.text);
            itemListHtml.push('</p>');
            itemListHtml.push('</div>');
            itemListHtml.push('</div>');
        });

        lineList.innerHTML = lineListHtml.join('');
        itemList.innerHTML = itemListHtml.join('');

        function $(id) {
            return document.getElementById(id);
        };


        // 旋转
        var i = 0;
        $('gbLottery').onclick = function () {
            i++;
            gbWheel.querySelector('.gb-wheel-content').style[transform] = 'rotate(' + i * 3600 + 'deg)';
        };

        // console.log(preTransform());

        // transform兼容
        function preTransform() {
            var cssPrefix,
                vendors = {
                    '': '',
                    Webkit: 'webkit',
                    Moz: '',
                    O: 'o',
                    ms: 'ms'
                },
                testEle = document.createElement('p'),
                cssSupport = {};

            // 嗅探特性
            Object.keys(vendors).some(function (vendor) {
                if (testEle.style[vendor + (vendor ? 'T' : 't') + 'ransform'] !== undefined) {
                    cssPrefix = vendor ? '-' + vendor.toLowerCase() + '-' : '';
                    return true;
                }
            });

            /**
             * [兼容CSS前缀]
             * @param  {[type]} name [description]
             * @return {[type]}      [description]
             */
            function normalizeCss(name) {
                name = name.toLowerCase();
                return cssPrefix ? cssPrefix + name : name;
            }

            cssSupport = {
                transform: normalizeCss('Transform'),
            }

            return cssSupport.transform;
        }

    }());

</script>
</body>
</html>