Ext.define('UXApp.window.IFrameWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.iframewindow',

    afterRender: function () {
        this.callParent(arguments);

        this.loadMask = new Ext.LoadMask({
            msg: '正在加载商品...',
            target: this
        });
        this.loadMask.show();

        this.iframe = document.createElement("iframe");
        this.iframe.window = this;
        this.iframe.src = this.src || undefined;
        this.iframe.width = '100%';
        this.iframe.height = '100%';
        this.iframe.style.border = '0px';
        this.iframe.onload = this._iframeLoad;
        document.getElementById(this.id + "-body").innerHTML = "";
        document.getElementById(this.id + "-body").appendChild(this.iframe);
    },

    setSrc: function (url) {
        this.iframe.src = url;
        this.loadMask.show();
    },

    _iframeLoad: function () {
        var win = this.window;
        win.loadMask.hide();
    }
});