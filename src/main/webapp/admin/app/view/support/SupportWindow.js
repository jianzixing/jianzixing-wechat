Ext.define('App.support.SupportWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.ComboBox',
        'Ext.form.field.Number',
        'Ext.form.field.TextArea',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 400,
    width: 600,
    layout: 'fit',
    title: '添加服务',
    defaultListenerScope: true,

    items: [
        {
            xtype: 'form',
            name: 'form',
            border: false,
            bodyPadding: 10,
            header: false,
            items: [
                {
                    xtype: 'textfield',
                    name: 'name',
                    anchor: '100%',
                    fieldLabel: '服务名称',
                    allowBlank: false
                },
                {
                    xtype: 'combobox',
                    name: 'type',
                    anchor: '100%',
                    editable: false,
                    fieldLabel: '服务类型',
                    displayField: 'name',
                    valueField: 'id',
                    store: {
                        data: [
                            {id: 1, name: '退货服务'},
                            {id: 2, name: '换货服务'},
                            {id: 3, name: '维修服务'},
                            {id: 0, name: '其他服务'}
                        ]
                    },
                    allowBlank: false
                },
                {
                    xtype: 'numberfield',
                    name: 'serTime',
                    anchor: '100%',
                    fieldLabel: '有效期(小时)',
                    allowBlank: false,
                    value: '0'
                },
                {
                    xtype: 'textareafield',
                    name: 'detail',
                    anchor: '100%',
                    height: 80,
                    fieldLabel: '服务描述',
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
                    text: '保存服务',
                    icon: Resource.png('jet', 'saveTempConfig'),
                    listeners: {
                        click: 'onSaveClick'
                    }
                },
                {
                    xtype: 'button',
                    text: '取消关闭',
                    icon: Resource.png('jet', 'cancel'),
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
                self.apis.Support.updateSupport
                    .wait(self, '正在修改...')
                    .call({object: data}, function () {
                        self.close();
                        if (self._callback) {
                            self._callback();
                        }
                    })
            } else {
                self.apis.Support.addSupport
                    .wait(self, '正在添加...')
                    .call({object: data}, function () {
                        self.close();
                        if (self._callback) {
                            self._callback();
                        }
                    })
            }
        }
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    setValue: function (data) {
        this._data = data;
        this.setTitle('修改商品服务');
        var form = this.find('form').getForm();
        form.setValues(data);
        this.find('type').setDisabled(true);
    }

});
