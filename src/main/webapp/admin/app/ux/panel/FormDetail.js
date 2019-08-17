Ext.define('UXApp.panel.FormDetail', {
    extend: 'Ext.Component',
    alias: 'widget.formdetail',

    requires: [],
    autoScroll: true,
    style: {
        // backgroundColor: '#EFEFEF'
        backgroundColor: '#FFF'
    },
    formMarginNumber: 20,

    onRender: function () {
        this.callParent(arguments);

        if (this.datas) {
            this.setBodyContent(this.datas);
        } else {
            this.setBodyContent([]);
        }
    },

    setBodyContent: function (datas) {
        var html = [];
        var title = this.title;

        html.push('<div class="form-detail" style="width:100%;min-height: 300px">');
        html.push('<div class="fd-title">');
        html.push('<span>' + title + '</span>');
        html.push('</div>');
        html.push('<div class="fd-body">');
        for (var i = 0; i < datas.length; i++) {
            var name = datas[i]['name'];
            var value = datas[i]['value'];
            html.push('<div class="fd-item">');
            html.push('<div class="fd-item-left">');
            html.push('<span>' + name + '</span>');
            html.push('</div>');
            html.push('<div class="fd-item-right">');
            html.push('<span>' + value + '</span>');
            html.push('</div>');
            html.push('</div>');
        }

        html.push('</div>');
        html.push('</div>');

        this.setHtml(html.join(""));
    },

    setValues: function (datas) {
        this.setBodyContent(datas)
    },

    setTitle: function (title) {
        var node = this.getEl().selectNode(".fd-title span");
        node.innerHTML = title;
    },

    onResize: function (width, height) {
        this.callParent(arguments);

        var el = this.getEl();
        var node = el.selectNode(".form-detail");
        if (node) {
            node.style.width = width - this.formMarginNumber * 2 + 'px';
            node.style.minHeight = height - this.formMarginNumber * 2 + 'px';
        }
    }
});