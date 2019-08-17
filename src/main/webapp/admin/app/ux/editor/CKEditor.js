Ext.define('UXApp.editor.CKEditor', {
    alias: 'widget.ckeditor',
    extend: 'Ext.container.Container',

    renderTpl: '<div id="{id}-ckBoxEl" style="width:100%;height:100%;overflow: auto">' +
        '<div id="{id}-ckEl"></div>' +
        '</div>' +
        '<tpl if="hasTabGuard">' +
        '{% this.renderTabGuard(out, values, \'before\'); %}' +
        '</tpl>' + '{% this.renderContainer(out,values) %}' +
        '<tpl if="hasTabGuard">{% this.renderTabGuard(out, values, \'after\'); %}</tpl>',

    afterRender: function () {
        this.callParent();

        this.initCKEditor(this.value);
    },

    onResize: function (width, height, oldWidth, oldHeight) {
        this.callParent();
        if (this._ckEditor && this._ckEditor.resize) {
            try {
                this._ckEditor.resize(this.getWidth(), this.getHeight());
            } catch (e) {
            }
        }
    },

    initCKEditor: function () {
        var self = this;
        var id = this.id + "-ckEl";
        this._ckEditor = CKEDITOR.replace(id, {
            sysimageCallback: function (editor) {
                self.openFileWindow(editor);
            }
        });
        this._ckEditor.on('instanceReady', function () {
            self._ckEditor.resize(self.getWidth(), self.getHeight());
        });

        if (this.value) {
            this._ckEditor.setData(this.value);
        }
    },

    setValue: function (value) {
        if (this._ckEditor) {
            this._ckEditor.setData(value)
        } else {
            this.value = value;
        }
    },

    getValue: function () {
        if (this._ckEditor) {
            return this._ckEditor.getData();
        }
        return this.value;
    }
});