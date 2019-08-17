/**
 * 验证码相关的js文件
 * 包含验证码样式、提交判断
 * 验证码几种方式：
 * 1、点击图片文字类型
 * @type {{template: string, templateWaitButton: string, templateOkButton: string, init: DragCode.init}}
 */
var webPathUri = "/resources/code";
var DragCode = {
    template: '<div class="yoosal_indicator">' +
    '            <div class="yoosal_bubble_container" style="transform: rotate(38deg);">' +
    '                <div class="yoosal_bubble"></div>' +
    '            </div>' +
    '            <div class="yoosal_point"></div>' +
    '        </div>' +
    '        <div class="yoosal_indicator yoosal_hide">' +
    '            <img class="yoosal_wait" src="'+webPathUri+'/images/more.png">' +
    '        </div>' +
    '' +
    '        <div class="yoosal_text">' +
    '            <span>点击按钮进行验证</span>' +
    '        </div>' +
    '        <div class="yoosal_logo">' +
    '            <img src="'+webPathUri+'/images/logo.png">' +
    '        </div>' +
    '' +
    '        <div class="yoosal_dialog" style="width: 350px;height: 440px;left: 0px;top: 0px;">' +
    '            <div class="yoosal_arrow_out"></div>' +
    '            <div class="yoosal_arrow_in"></div>' +
    '            <div class="yoosal_ahead">' +
    '                <div class="yoosal_ahead_text">' +
    '                    请在下图<span style="color: #1c75e4">依次</span>点击：' +
    '                </div>' +
    '                <div class="yoosal_ahead_image"></div>' +
    '            </div>' +
    '            <div class="yoosal_aimg"></div>' +
    '            <div class="yoosal_fail">' +
    '                <span>验证失败&nbsp;请按照提示重新操作</span>' +
    '            </div>' +
    '            <div class="yoosal_abottom">' +
    '                <div class="yoosal_abottom_btns">' +
    '                    <div class="yoosal_abottom_btn yoosal_abottom_btn1"></div>' +
    '                    <div class="yoosal_abottom_btn yoosal_abottom_btn3"></div>' +
    '                    <div class="yoosal_abottom_btn yoosal_abottom_btn2"></div>' +
    '                </div>' +
    '                <div class="yoosal_ok">' +
    '                    <span>确定</span>' +
    '                </div>' +
    '            </div>' +
    '        </div>',

    templateWaitButton:
    '<div class="yoosal_wait_button_l">' +
    '            <div class="yoosal_wait_button_css yoosal_wait_button_one"></div>' +
    '            <div class="yoosal_wait_button_css yoosal_wait_button_two"></div>' +
    '            <div class="yoosal_wait_button_css yoosal_wait_button_three"></div>' +
    '        </div>',
    templateOkButton:
        '<span>确定</span>',

    init: function (el) {
        var object = new Object();
        object.pointCount = 0;
        object.points = 0;
        object.isCheckCode = false;
        object.url = "/web/valcode/check.action";
        object.mouseTrack = [];
        object.init = function (el) {
            el.innerHTML = DragCode.template;

            var elArray = el.querySelectorAll('.yoosal_indicator');
            var dialogEl = el.querySelector('.yoosal_dialog');
            var waitImageEl = el.querySelector('.yoosal_wait');
            var logoImageEl = el.querySelector('.yoosal_logo img');
            var bodyImageEl = el.querySelector('.yoosal_aimg');
            var headImageEl = el.querySelector('.yoosal_ahead_image');
            var submitEl = el.querySelector('.yoosal_ok');
            var failTextEl = el.querySelector('.yoosal_fail');
            var elTextEl = el.querySelector('.yoosal_text');
            var aButtonEl1 = el.querySelector('.yoosal_abottom_btns .yoosal_abottom_btn1');
            var aButtonEl2 = el.querySelector('.yoosal_abottom_btns .yoosal_abottom_btn2');
            var aButtonEl3 = el.querySelector('.yoosal_abottom_btns .yoosal_abottom_btn3');
            var clickEl = elArray[0];
            var waitEl = elArray[1];

            var self = this;
            this.dialogEl = dialogEl;
            this.clickEl = clickEl;
            this.waitEl = waitEl;
            this.waitImageEl = waitImageEl;
            this.bodyImageEl = bodyImageEl;
            this.headImageEl = headImageEl;
            this.submitEl = submitEl;
            this.elTextEl = elTextEl;
            this.logoImageEl = logoImageEl;
            this.failTextEl = failTextEl;
            this.el = el;

            this.onRefreshCode();

            self._bodyCloseFun = function (e) {
                self.onCloseDialog(e);
            };
            document.body.addEventListener("click", self._bodyCloseFun);

            el.onclick = function (e) {
                var left = el.offsetLeft,
                    top = el.offsetTop,
                    height = 0;

                el.style.backgroundColor = "#eaeaea";
                el.style.backgroundImage = "none";
                clickEl.style.display = "none";
                waitEl.style.display = "block";

                dialogEl.style.display = "block";
                height = dialogEl.offsetHeight;
                dialogEl.style.left = left + 45;
                dialogEl.style.top = top - height / 2 + 42 - 9;

                document.body.addEventListener("click", self._bodyCloseFun);
                e.stopPropagation();
            };


            var bubble = el.querySelector(".yoosal_bubble_container");
            window.addEventListener("mousemove", function (e) {
                var offsetX = e.clientX;
                var offsetY = e.clientY;
                var offsetLeft = bubble.offsetLeft;
                var offsetTop = bubble.offsetTop;

                var angle = self.getAngle(offsetLeft, offsetTop, offsetX, offsetY);
                bubble.style.transform = "rotate(" + angle + "deg)";

                self.mouseTrack.push({x: offsetX, y: offsetY});
                if (self.mouseTrack.length >= 100) {
                    self.mouseTrack = [];
                }
            });

            aButtonEl1.addEventListener("click", function (e) {
                self.onCloseDialog(e);
                e.stopPropagation();
            });

            aButtonEl3.addEventListener("click", function (e) {
                self.onRefreshCode(e);
                e.stopPropagation();
            });

            aButtonEl2.addEventListener("click", function (e) {
                self.onInfoClick(e);
                e.stopPropagation();
            });

            bodyImageEl.addEventListener("click", function (e) {
                var x = e.offsetX, y = e.offsetY;

                var number = document.createElement("div");
                var count = document.createElement("div");
                count.innerHTML = ++self.pointCount;
                number.append(count);
                number.className = "yoosal_number";
                number.style.left = x - 13 + "px";
                number.style.top = y - 13 + "px";
                number.$x = x;
                number.$y = y;

                number.addEventListener("click", function (e) {
                    self.onRemoveNumbers();
                    e.stopPropagation();
                });

                bodyImageEl.append(number);

                setTimeout(function () {
                    number.className = "yoosal_number yoosal_number_anim";
                }, 0.1);

                self.points.push(number);
            });

            submitEl.addEventListener("click", function (e) {
                var points = [];
                if (!self.isCheckCode) {
                    for (var i in self.points) {
                        var number = self.points[i];
                        points.push({x: number.$x, y: number.$y})
                    }
                    self.submitEl.innerHTML = DragCode.templateWaitButton;
                    self.isCheckCode = true;

                    self.post({point: JSON.stringify(points)}, function (isSuccess, response) {
                        self.isCheckCode = false;
                        if (isSuccess && self.checkNetResponse(response)) {
                            self.onSuccess(e);
                        } else {
                            self.onFailure(e);
                        }
                        self.submitEl.innerHTML = DragCode.templateOkButton;
                    });
                    e.stopPropagation();
                }
            });
        };

        object.onRemoveNumbers = function () {
            var self = this;
            self.pointCount = 0;
            setTimeout(function () {
                for (var i in self.points) {
                    var number = self.points[i];
                    number.className = "yoosal_number";
                    var transitionEvent = self.whichTransitionEvent();
                    transitionEvent && number.addEventListener(transitionEvent, function () {
                        self.bodyImageEl.innerHTML = "";
                        number.removeEventListener(transitionEvent, arguments.callee, false);//销毁事件
                    });
                }
                self.points = [];
            }, 0.1);
        };

        object.whichTransitionEvent = function () {
            var self = this;
            var t,
                el = document.createElement('surface'),
                transitions = {
                    'transition': 'transitionend',
                    'OTransition': 'oTransitionEnd',
                    'MozTransition': 'transitionend',
                    'WebkitTransition': 'webkitTransitionEnd'
                };

            for (t in transitions) {
                if (el.style[t] !== undefined) {
                    return transitions[t];
                }
            }
        };

        object.onCloseDialog = function (e) {
            var self = this;
            document.body.removeEventListener("click", self._bodyCloseFun);
            self.dialogEl.style.display = "none";
            self.el.style.backgroundColor = null;
            self.el.style.backgroundImage = null;
            self.clickEl.style.display = "block";
            self.waitEl.style.display = "none";

            self.onRemoveNumbers();
        };

        object.onRefreshCode = function (e) {
            var self = this;
            var date = new Date();
            self.bodyImageEl.style.backgroundImage = 'url("/valcode/image.action?t=' + date.getTime() + '")';
            self.headImageEl.style.backgroundImage = 'url("/valcode/image.action?t=' + date.getTime() + '")';

            self.onRemoveNumbers();
        };

        object.onInfoClick = function (e) {

        };

        object.onSuccess = function (e) {
            var self = this;
            self.onCloseDialog();

            self.elTextEl.style.color = "#18A452";
            self.el.style.borderColor = "#26C267";
            self.el.style.background = "#EEFFF5";

            self.clickEl.innerHTML = "";
            var success = document.createElement("div");
            self.clickEl.append(success);
            success.style.width = "24px";
            success.style.height = "24px";
            success.style.marginLeft = "auto";
            success.style.marginRight = "auto";
            success.style.marginTop = "9px";
            success.style.background = "url('"+webPathUri+"/images/2.png') 0 -88px";

            self.logoImageEl.src = webPathUri+"/images/logo_success.png";
            self.el.onclick = null;
        };

        object.onFailure = function (e) {
            var self = this;
            self.onRemoveNumbers();
            self.failTextEl.style.display = "block";
            self.failTextEl.style.height = "28px";

            self.dialogEl.style.animation = "shake 0.82s cubic-bezier(.36, .07, .19, .97) both";

            self.onRefreshCode();

            setTimeout(function () {
                self.dialogEl.style.animation = null;
                self.failTextEl.style.height = 0;
            }, 2000);
        };

        object.getAngle = function (px, py, mx, my) {
            var x = Math.abs(px - mx);
            var y = Math.abs(py - my);
            var z = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
            var cos = y / z;
            var radina = Math.acos(cos);//用反三角函数求弧度
            var angle = Math.floor(180 / (Math.PI / radina));//将弧度转换成角度

            if (mx <= px && my >= py) {//鼠标在第四象限
                angle = 180 + angle;
            }
            if (mx <= px && my <= py) {//鼠标在第三象限
                angle = 270 + 90 - angle;
            }
            if (mx >= px && my <= py) {//鼠标在第二象限
                angle = angle;
            }
            if (mx >= px && my >= py) {//鼠标在第一象限
                angle = 90 + 90 - angle;
            }

            return angle;
        };

        object.checkNetResponse = function (response) {
            var text = response.responseText;
            var json = JSON.parse(text);
            if (json['code'] === 0) {
                return true;
            } else {
                return false;
            }
        };

        object.post = function (data, fn) {
            var xmlHttp, str = "";
            var self = this;
            if (window.XMLHttpRequest) {
                xmlHttp = new XMLHttpRequest();
            }
            else {
                xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
            }
            xmlHttp.onreadystatechange = function () {
                if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
                    fn(true, xmlHttp);
                } else if (xmlHttp.status != 200 && xmlHttp.status != 0) {
                    fn(false, xmlHttp);
                }
            };
            xmlHttp.open("POST", self.url, true);
            xmlHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
            for (var i in data) {
                str += "&" + i + "=" + encodeURIComponent(data[i]);
            }
            if (str.length > 0) {
                str = str.substring(1, str.length);
            }
            xmlHttp.send(str);
        };

        object.init(el);

        return object;
    }
};


window.addEventListener("load", function () {
    var els = document.body.querySelectorAll(".yoosal_box");
    if (els.length > 0) {
        for (var i = 0; i < els.length; i++) {
            DragCode.init(els[i]);
        }
    }
});