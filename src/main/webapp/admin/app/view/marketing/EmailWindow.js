Ext.define('App.marketing.EmailWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.form.Panel',
        'Ext.form.field.Text'
    ],

    height: 500,
    width: 600,
    layout: 'fit',
    title: '编辑邮箱服务',
    defaultListenerScope: true,

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
    items: [
        {
            xtype: 'form',
            name: 'form',
            bodyPadding: 10,
            header: false,
            items: [
                {
                    xtype: 'hiddenfield',
                    name: 'id',
                    anchor: '100%'
                },
                {
                    xtype: 'textfield',
                    name: 'name',
                    anchor: '100%',
                    fieldLabel: '邮件服务名称',
                    labelWidth: 120,
                    allowBlank: false,
                    blankText: '邮箱服务名称不能为空'
                },
                {
                    xtype: 'radiogroup',
                    labelWidth: 120,
                    anchor: '100%',
                    fieldLabel: '是否SSL',
                    items: [
                        {
                            xtype: 'radiofield',
                            name: 'ssl',
                            boxLabel: '否',
                            inputValue: '0',
                            checked: true
                        },
                        {
                            xtype: 'radiofield',
                            name: 'ssl',
                            inputValue: '1',
                            boxLabel: '是'
                        }
                    ]
                },
                {
                    xtype: 'textfield',
                    name: 'smtpAddress',
                    anchor: '100%',
                    fieldLabel: 'SMTP服务器地址',
                    labelWidth: 120,
                    allowBlank: false,
                    value: 'smtp.domain.com',
                    blankText: '服务器地址不能为空'
                },
                {
                    xtype: 'textfield',
                    name: 'smtpPort',
                    anchor: '100%',
                    fieldLabel: 'SMTP服务器端口',
                    labelWidth: 120,
                    value: '25',
                    regex: /^[0-9]{1,6}$/,
                    regexText: '请输入整数',
                    allowBlank: false,
                    inputType: 'number',
                    blankText: '服务器端口不能为空'
                },
                {
                    xtype: 'textfield',
                    name: 'smtpUserName',
                    anchor: '100%',
                    fieldLabel: '邮箱账号',
                    labelWidth: 120,
                    // regex: /^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,6}$/,
                    // regexText: '请输入正确的邮箱地址',
                    allowBlank: false,
                    blankText: '邮箱用户名不能为空'
                },
                {
                    xtype: 'textfield',
                    name: 'smtpPassword',
                    anchor: '100%',
                    fieldLabel: '邮箱密码',
                    labelWidth: 120,
                    allowBlank: false,
                    blankText: '邮箱密码不能为空'
                },
                {
                    xtype: 'radiogroup',
                    labelWidth: 120,
                    anchor: '100%',
                    fieldLabel: '邮件编码',
                    items: [
                        {
                            xtype: 'radiofield',
                            name: 'encoding',
                            inputValue: 'UTF-8',
                            boxLabel: 'UTF-8',
                            checked: true
                        },
                        {
                            xtype: 'radiofield',
                            name: 'encoding',
                            inputValue: 'GBK',
                            boxLabel: 'GBK'
                        },
                        {
                            xtype: 'radiofield',
                            name: 'encoding',
                            inputValue: 'GB2312',
                            boxLabel: 'GB2312'
                        }
                    ]
                },
                {
                    xtype: 'textareafield',
                    name: 'remark',
                    anchor: '100%',
                    fieldLabel: '备注',
                    labelWidth: 120
                }
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
                self.apis.Email.updateEmail
                    .wait(self, '正在保存邮箱...')
                    .call({object: data}, function () {
                        if (self._callback) {
                            self._callback();
                        }
                    });
            } else {
                self.apis.Email.addEmail
                    .wait(self, '正在保存邮箱...')
                    .call({object: data}, function () {
                        if (self._callback) {
                            self._callback();
                        }
                    });
            }
            self.close();
        }
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    onTextfieldChange: function (field, newValue, oldValue, eOpts) {

    },

    setValues: function (data) {
        this._data = data;
        var form = this.find('form').getForm();
        form.setValues(data);
    }

});