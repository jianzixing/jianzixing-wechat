Ext.define('App.comment.DiscussClassifyWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.Text',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 210,
    width: 500,
    layout: 'fit',
    title: '添加评论分类',
    defaultListenerScope: true,

    items: [
        {
            xtype: 'form',
            name: 'form',
            border: false,
            bodyPadding: 10,
            items: [
                {
                    xtype: 'textfield',
                    name: 'name',
                    anchor: '100%',
                    fieldLabel: '分类名称',
                    labelWidth: 150,
                    allowBlank: false
                },
                {
                    xtype: 'textfield',
                    name: 'code',
                    anchor: '100%',
                    fieldLabel: '分类代码(大写字母)',
                    regex: /^[A-Za-z0-9]+$/,
                    labelWidth: 150,
                    listeners: {
                        change: 'onTextfieldChange'
                    }
                }
            ]
        }
    ],
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            items: [
                '->',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'saveTempConfig'),
                    text: '保存',
                    listeners: {
                        click: 'onSaveClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'cancel'),
                    text: '取消',
                    listeners: {
                        click: 'onCancelClick'
                    }
                },
                '->'
            ]
        }
    ],

    onSaveClick: function (button, e, eOpts) {
        var self = this;
        var form = this.find('form').getForm();
        if (form.isValid()) {
            var data = form.getValues();
            if (this._data) {
                data['id'] = this._data['id'];
                this.apis.DiscussComment.updateClassify
                    .wait(self, '正在修改评论分类...')
                    .call({object: data}, function () {
                        self.close();
                        if (self._callback) {
                            self._callback();
                        }
                    });
            } else {
                this.apis.DiscussComment.addClassify
                    .wait(self, '正在添加评论分类...')
                    .call({object: data}, function () {
                        self.close();
                        if (self._callback) {
                            self._callback();
                        }
                    });
            }
        }
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    onTextfieldChange: function (field, newValue, oldValue, eOpts) {
        field.setValue(field.getValue().toUpperCase());
    },

    setValue: function (value) {
        this._data = value;
        this.setTitle('修改评论分类');
        this.find('form').getForm().setValues(value);
    }

});