Ext.define('App.wechat.WeChatQRCodeWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.Text',
        'Ext.form.FieldContainer',
        'Ext.Img',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 280,
    width: 800,
    layout: 'fit',
    title: '添加二维码',
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
                    fieldLabel: '二维码名称',
                    allowBlank: false,
                    maxLength: 20
                },
                {
                    xtype: 'combobox',
                    name: 'actionName',
                    anchor: '100%',
                    fieldLabel: '二维码类型',
                    displayField: 'name',
                    valueField: 'id',
                    editable: false,
                    store: {
                        data: [
                            {id: 'QR_STR_SCENE', name: '临时字符串'},
                            {id: 'QR_LIMIT_STR_SCENE', name: '永久字符串'}
                        ]
                    },
                    listeners: {
                        change: 'onTypeChange'
                    }
                },
                {
                    xtype: 'numberfield',
                    hidden: true,
                    name: 'expireSeconds',
                    anchor: '100%',
                    fieldLabel: '过期时间(秒)'
                },
                {
                    xtype: 'fieldcontainer',
                    name: 'qr_code',
                    hidden: true,
                    height: 220,
                    fieldLabel: '二维码',
                    items: [
                        {
                            xtype: 'image',
                            name: 'image',
                            height: 220,
                            width: 220
                        }
                    ]
                },
                {
                    xtype: 'textareafield',
                    hidden: true,
                    anchor: '100%',
                    name: 'link_code',
                    fieldLabel: '二维码链接'
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
            var values = form.getValues();
            values['openType'] = this.openType;
            values['accountId'] = this.accountData['id'];
            if (this._data) {
                values['id'] = this._data['id'];
                self.apis.WeChatQRCode.updateQRCode
                    .wait(this, '正在保存...')
                    .call({object: values}, function () {
                        self.close();
                        if (self._callback) {
                            self._callback();
                        }
                    });
            } else {
                self.apis.WeChatQRCode.addQRCode
                    .wait(this, '正在保存...')
                    .call({object: values}, function () {
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

    onTypeChange: function (field, newValue, oldValue, eOpts) {
        if (newValue == 'QR_STR_SCENE' || newValue == 'QR_SCENE') {
            this.find('expireSeconds').show();
        } else {
            this.find('expireSeconds').hide();
        }
    },

    setValue: function (data) {
        this._data = data;
        this.find('expireSeconds').hide();
        this.setTitle('修改二维码');
        this.setHeight(550);
        this.find('qr_code').show();
        this.find('link_code').show();
        this.find('name').setValue(data['name']);
        this.find('actionName').setValue(data['actionName']);
        this.find('expireSeconds').setValue(data['expireSeconds']);
        if (data['ticket']) {
            this.find('image').setSrc('https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=' + encodeURIComponent(data['ticket']));
            this.find('link_code').setValue('https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=' + encodeURIComponent(data['ticket']));
        }
    }
});
