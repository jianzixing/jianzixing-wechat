Ext.define('App.wechat.WeChatAppWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.TextArea',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    autoShow: true,
    height: 420,
    width: 500,
    layout: 'fit',
    title: '添加移动应用',
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
                    fieldLabel: 'APP名称',
                    allowBlank: false,
                    blankText: 'APP名称必须填写'
                },
                {
                    xtype: 'textfield',
                    name: 'code',
                    anchor: '100%',
                    fieldLabel: '唯一标识码',
                    regex: /^[A-Za-z0-9]+$/,
                    regexText: '唯一标识码只能是字母或者数字或者字母数字组合'
                },
                {
                    xtype: 'textfield',
                    name: 'appId',
                    anchor: '100%',
                    fieldLabel: 'AppID',
                    allowBlank: false,
                    blankText: 'appid必须填写'
                },
                {
                    xtype: 'textfield',
                    name: 'appSecret',
                    anchor: '100%',
                    fieldLabel: 'AppSecret',
                    allowBlank: false,
                    blankText: 'secret必须填写'
                },
                {
                    xtype: 'radiogroup',
                    fieldLabel: '是否启用',
                    items: [
                        {
                            xtype: 'radiofield',
                            name: 'enable',
                            boxLabel: '启用',
                            checked: true,
                            inputValue: '1'
                        },
                        {
                            xtype: 'radiofield',
                            name: 'enable',
                            boxLabel: '禁用',
                            inputValue: '0'
                        }
                    ]
                },
                {
                    xtype: 'textareafield',
                    name: 'detail',
                    anchor: '100%',
                    fieldLabel: '描述'
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
                data['id'] = this._data['id'];
                this.apis.WeChatApp.updateApp
                    .wait(self, '正在修改移动应用...')
                    .call({object: data}, function () {
                        self.close();
                        if (self._callback) self._callback();
                    })
            } else {
                this.apis.WeChatApp.addApp
                    .wait(self, '正在添加移动应用...')
                    .call({object: data}, function () {
                        self.close();
                        if (self._callback) self._callback();
                    })
            }
        }
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    setValue: function (data) {
        this._data = data;
        this.setTitle('修改移动应用');
        var form = this.find('form').getForm();
        form.setValues(data);
    }

});