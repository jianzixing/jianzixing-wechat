Ext.define('UXApp.editor.CodeEditor', {
    alias: 'widget.codeditor',
    extend: 'Ext.container.Container',
    scrollable: 'y',

    afterRender: function () {
        this.callParent();

        this.initCodeEditor(this.value);
    },

    initCodeEditor: function () {
        var self = this;
        var cel = document.getElementById(self.id + "-outerCt");
        cel.style.width = "100%";
        var id = this.id + "_code_content";
        self.setHtml("<textarea style='overflow:hidden;' id='" + id + "'></textarea>");

        var myCodeMirror = CodeMirror.fromTextArea(document.getElementById(id), {
            value: "",
            mode: "application/x-ejs",
            indentUnit: 4,
            indentWithTabs: true,
            lineNumbers: true,
            lineWrapping: true
        });

        this._CodeMirror = myCodeMirror;
        this._TextArea = document.getElementById(id);
    },

    setValue: function (value) {
        if (this._CodeMirror) {
            this._CodeMirror.setValue(value);
        }
    },

    getValue: function () {
        return this._CodeMirror.getValue();
    }
});