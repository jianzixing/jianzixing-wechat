Ext.define('App.logistics.LogisticsCompanyWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.RadioGroup',
        'Ext.form.field.Radio',
        'Ext.form.field.TextArea',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 350,
    width: 500,
    layout: 'fit',
    title: '添加物流公司',
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
                    xtype: 'hidden',
                    name: 'id'
                },
                {
                    xtype: 'textfield',
                    name: 'name',
                    anchor: '100%',
                    fieldLabel: '物流公司名称',
                    allowBlank: false
                },
                {
                    xtype: 'textfield',
                    name: 'code',
                    anchor: '100%',
                    fieldLabel: '标识码',
                    regex: /^[a-zA-Z]\w{2,30}$/,
                    regexText: '只能输入字母或者数字',
                    listeners: {
                        change: 'onTextfieldChange'
                    },
                    allowBlank: false
                },
                {
                    xtype: 'radiogroup',
                    fieldLabel: '是否启用',
                    items: [
                        {
                            xtype: 'radiofield',
                            name: 'enable',
                            inputValue: '1',
                            checked: true,
                            boxLabel: '启用'
                        },
                        {
                            xtype: 'radiofield',
                            name: 'enable',
                            inputValue: '0',
                            boxLabel: '禁用'
                        }
                    ]
                },
                {
                    xtype: 'textareafield',
                    name: 'detail',
                    anchor: '100%',
                    fieldLabel: '物流公司描述'
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
                    text: '确定保存',
                    listeners: {
                        click: 'onSaveClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'cancel'),
                    text: '取消关闭',
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
                this.apis.Logistics.updateCompany
                    .wait(self, '正在修改物流公司...')
                    .call({object: data}, function () {
                        self.close();
                        if (self._callback) {
                            self._callback();
                        }
                    })
            } else {
                this.apis.Logistics.addCompany
                    .wait(self, '正在添加物流公司...')
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
        this.setTitle('修改物流公司');
        var form = this.find('form').getForm();
        form.setValues(data);
    },

    onTextfieldChange: function (field, newValue, oldValue, eOpts) {
        field.setValue(newValue.toUpperCase());
    }

});