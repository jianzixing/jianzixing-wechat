Ext.define('UXApp.editor.CKEditorField', {
    alias: 'widget.ckeditorfield',
    extend: 'Ext.form.FieldContainer',
    mixins: {
        field: 'Ext.form.field.Field'
    },

    layout: 'fit',
    ckeditor: null,
    fieldSubTpl: [
        '<div id="{id}-containerEl" data-ref="containerEl" class="{containerElCls}"',
        '<tpl if="ariaAttributes">',
        '<tpl foreach="ariaAttributes"> {$}="{.}"</tpl>',
        '<tpl else>',
        ' role="presentation"',
        '</tpl>',
        '>',
        '{%this.renderContainer(out,values)%}', //这里是布局添加的元素
        '<div id="{id}-ckEl" style="width: 100%;height: 100%;"></div>',
        '</div>'
    ],


    initComponent: function () {
        var me = this;
        me.callParent();
    },

    onResize: function (width, height, oldWidth, oldHeight) {
        this.callParent();
        var ckel = Ext.get(this.id + '-ckEl');
        ckel.setHeight(height);
    },

    afterRender: function () {
        this.callParent();

        this.initCKEditor(this.value);
    },

    initCKEditor: function () {
        var self = this;
        var id = this.id + "-ckEl";
        this._ckEditor = CKEDITOR.replace(id, {
            height: this.height,
            sysimageCallback: function (editor) {
                self.openFileWindow(editor);
            }
        });
        if (this.value) {
            this._ckEditor.setData(this.value);
        }
    },

    getSubmitData: function () {
        var self = this;
        var data = {};
        data[self.name] = this.getValue();
        return data;
    },

    setValue: function (value) {
        if (this._ckEditor) {
            this._ckEditor.setData(value)
        } else {
            this.value = value;
        }
    },

    getValue: function () {
        return this._ckEditor.getData();
    }
});