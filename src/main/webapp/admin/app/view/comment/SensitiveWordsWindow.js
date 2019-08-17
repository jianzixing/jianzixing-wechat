Ext.define('App.comment.SensitiveWordsWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.Text',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 150,
    width: 500,
    layout: 'fit',
    title: '添加敏感字',
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
                    name: 'text',
                    anchor: '100%',
                    fieldLabel: '敏感字',
                    labelWidth: 150,
                    allowBlank: false
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
                this.apis.SensitiveWords.updateWord
                    .wait(self, '正在修改敏感字...')
                    .call({object: data}, function () {
                    self.close();
                    if (self._callback) {
                        self._callback();
                    }
                });
            } else {
                this.apis.SensitiveWords.addWord
                    .wait(self, '正在添加敏感字...')
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

    setValue: function (value) {
        this._data = value;
        this.setTitle('修改敏感字');
        this.find('form').getForm().setValues(value);
    }

});