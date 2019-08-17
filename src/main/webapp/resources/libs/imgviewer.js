var ImgViewer = function (items, options) {
    if (!ImgViewer.totalCount) {
        ImgViewer.totalCount = 1;
    } else {
        ImgViewer.totalCount++;
    }
    if (!ImgViewer.items) ImgViewer.items = [];
    ImgViewer.items.push(this);
    var tpl =
        '    <div class="pswp__bg"></div>\n' +
        '    <div class="pswp__scroll-wrap">\n' +
        '        <div class="pswp__container">\n' +
        '            <div class="pswp__item"></div>\n' +
        '            <div class="pswp__item"></div>\n' +
        '            <div class="pswp__item"></div>\n' +
        '        </div>\n' +
        '        <div class="pswp__ui pswp__ui--hidden">\n' +
        '            <div class="pswp__top-bar">\n' +
        '                <div class="pswp__counter"></div>\n' +
        '                <button class="pswp__button pswp__button--close" title="Close (Esc)"></button>\n' +
        '                <button class="pswp__button pswp__button--fs" title="Toggle fullscreen"></button>\n' +
        '                <button class="pswp__button pswp__button--zoom" title="Zoom in/out"></button>\n' +
        '                <div class="pswp__preloader">\n' +
        '                    <div class="pswp__preloader__icn">\n' +
        '                        <div class="pswp__preloader__cut">\n' +
        '                            <div class="pswp__preloader__donut"></div>\n' +
        '                        </div>\n' +
        '                    </div>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '            <div class="pswp__share-modal pswp__share-modal--hidden pswp__single-tap">\n' +
        '                <div class="pswp__share-tooltip"></div>\n' +
        '            </div>\n' +
        '            <button class="pswp__button pswp__button--arrow--left" title="Previous (arrow left)">\n' +
        '            </button>\n' +
        '            <button class="pswp__button pswp__button--arrow--right" title="Next (arrow right)">\n' +
        '            </button>\n' +
        '            <div class="pswp__caption">\n' +
        '                <div class="pswp__caption__center"></div>\n' +
        '            </div>\n' +
        '        </div>\n' +
        '    </div>\n';

    var dialog = document.createElement("div");
    dialog.id = "pswp_browser_" + ImgViewer.totalCount;
    dialog.className = "pswp";
    dialog.setAttribute("tabindex", "-1");
    dialog.setAttribute("role", "dialog");
    dialog.setAttribute("aria-hidden", "true");
    dialog.innerHTML = tpl;

    if (items) {
        for (var i = 0; i < items.length; i++) {
            items[i].w = 0;
            items[i].h = 0;
        }
    }

    document.body.appendChild(dialog);
    this.dialog_el = dialog;
    this.checkRequires(items, options);
};

ImgViewer.resources = {
    r1: 'photoswipe/photoswipe.css',
    r2: 'photoswipe/default-skin/default-skin.css',
    r3: 'photoswipe/photoswipe-ui-default.min.js',
    r4: 'photoswipe/photoswipe.min.js'
};

ImgViewer.getImageSize = function (src) {
    var img = new Image();
    img.src = src;
    return {src: src, w: img.width, h: img.height};
};

ImgViewer.prototype.destroy = function () {
    if (this.gallery && this.gallery.destroy) {
        this.gallery.destroy();
    }
};
ImgViewer.prototype.close = function () {
    if (this.gallery) this.gallery.close();
};
ImgViewer.prototype.prev = function () {
    if (this.gallery) this.gallery.prev();
};
ImgViewer.prototype.next = function () {
    if (this.gallery) this.gallery.next();
};
ImgViewer.prototype.goTo = function (index) {
    if (this.gallery) this.gallery.goTo(index);
};
ImgViewer.prototype.addImg = function (item) {
    if (this.gallery) {
        item.w = 0;
        item.h = 0;
        this.gallery.items.push(item);
    }
};

ImgViewer.prototype.createGallery = function (items, options) {
    var scripts = document.getElementsByTagName("script");
    var r3 = 0, r4 = 0;
    for (var i = 0; i < scripts.length; i++) {
        var script = scripts[i];
        var src = script.src;
        if (src.indexOf(ImgViewer.resources.r3) >= 0) r3 = 1;
        if (src.indexOf(ImgViewer.resources.r4) >= 0) r4 = 1;
    }
    if (r3 + r4 == 2 && window['PhotoSwipe'] && window['PhotoSwipeUI_Default']) {
        var self = this;
        this.gallery = new PhotoSwipe(this.dialog_el, PhotoSwipeUI_Default, items, options);
        this.gallery.listen('gettingData', function (index, item) {
            if (item.w < 1 || item.h < 1) {
                var img = new Image();
                img.onload = function () {
                    item.w = this.width;
                    item.h = this.height;
                    self.gallery.invalidateCurrItems();
                    self.gallery.updateSize(true);
                };
                img.src = item.src; // let's download image
            }
        });
        this.gallery.listen('destroy', function () {
            self.dialog_el.parentNode.removeChild(self.dialog_el);
        });
        this.gallery.init();
        return true;
    }
    return false;
};
ImgViewer.prototype.checkRequires = function (items, options) {
    var self = this;
    var scripts = document.getElementsByTagName("script");
    var currUrl, isLoaded = self.createGallery(items, options);
    if (!isLoaded) {
        for (var i = 0; i < scripts.length; i++) {
            var script = scripts[i];
            var src = script.src;
            if (src.indexOf("imgviewer.js") >= 0) {
                currUrl = src;
            }
        }

        var addScript = function (src, callback) {
            var script = document.createElement("script");
            script.type = "text/javascript";
            script.src = src;
            document.getElementsByTagName("head")[0].appendChild(script);
            script.onload = callback;
        };

        var addCss = function (href, callback) {
            var link = document.createElement("link");
            link.rel = "stylesheet";
            link.href = href;
            document.getElementsByTagName("head")[0].appendChild(link);
            link.onload = callback;
        };
        currUrl = currUrl.substring(0, currUrl.indexOf("imgviewer.js"));
        addCss(currUrl + ImgViewer.resources.r1);
        addCss(currUrl + ImgViewer.resources.r2);
        addScript(currUrl + ImgViewer.resources.r3, function () {
            self.createGallery(items, options);
        });
        addScript(currUrl + ImgViewer.resources.r4, function () {
            self.createGallery(items, options);
        });
    }
};