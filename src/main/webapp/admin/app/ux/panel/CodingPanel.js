Ext.require(["lib.ace.ace", "lib.ace.mode-javascript", "lib.ace.theme-monokai"]);
Ext.define('UXApp.panel.CodingPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.codingpanel',

    afterRender: function () {
        this.callParent(arguments);

        var me = this;
        this.loadMask = new Ext.LoadMask({
            msg: '正在加载编辑器...',
            target: this
        });
        this.loadMask.show();

        this._intervalId = setInterval(function () {
            if (ace) {
                clearInterval(me._intervalId);
                me.finishLoadLib();
            }
        }, 100);

    },

    finishLoadLib: function () {
        this.loadMask.hide();
        var dom = this.getTargetEl();
        dom.setHtml(this.code || "function fun(){\r\n\t\r\n}");
        this._codeEditor = ace.edit(dom.dom);
        //editor.setTheme("ace/theme/monokai");
        var JavaScriptMode = ace.require("ace/mode/javascript").Mode;
        this._codeEditor.session.setMode(new JavaScriptMode());
    },

    getValue: function () {
        return this._codeEditor.getValue();
    }
});