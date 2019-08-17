Ext.define('App.aftersales.AfterSalesYourAddressWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.form.Panel',
        'Ext.form.field.Display',
        'Ext.form.field.TextArea'
    ],

    height: 400,
    width: 600,
    layout: 'fit',
    title: '快递寄回卖家的地址',
    defaultListenerScope: true,

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            items: [
                '->',
                {
                    xtype: 'button',
                    text: '确认审核通过',
                    icon: Resource.png('jet', 'saveTempConfig'),
                    listeners: {
                        click: 'onSaveAuditAddressClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'cancel'),
                    text: '取消关闭',
                    listeners: {
                        click: 'onWinCancel'
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
            border: false,
            bodyPadding: 10,
            header: false,
            items: [
                {
                    xtype: 'displayfield',
                    anchor: '100%',
                    height: 50,
                    fieldLabel: '说明',
                    value: '在线售后需要买家将货物寄回给您，您检验通过后再进行相应流程，以下需要您的收货地址给买家寄回问题商品' +
                        '。您可以在 系统->系统配置->系统参数配置->售后信息配置 中修改默认配置信息。'
                },
                {
                    xtype: 'textfield',
                    name: 'realName',
                    anchor: '100%',
                    fieldLabel: '您的收货名称',
                    allowBlank: false
                },
                {
                    xtype: 'textfield',
                    name: 'phoneNumber',
                    anchor: '100%',
                    fieldLabel: '您的收货手机号',
                    allowBlank: false
                },
                {
                    xtype: 'textareafield',
                    name: 'address',
                    anchor: '100%',
                    height: 80,
                    fieldLabel: '您的收货地址',
                    allowBlank: false
                }
            ]
        }
    ],

    onSaveAuditAddressClick: function (button, e, eOpts) {
        var form = this.find('form').getForm();
        if (form.isValid()) {
            var data = form.getValues();
            try {
                if (this._callback) {
                    this._callback(data);
                }
            } catch (e) {
            }
            this.close();
        }
    },

    onWinCancel: function (button, e, eOpts) {
        this.close();
    },

    setAuditBackAddress: function (address) {
        if (address) {
            this.find('realName').setValue(address['name'] || '');
            this.find('phoneNumber').setValue(address['phone'] || '');
            this.find('address').setValue(address['address'] || '');
        }
    }

});
