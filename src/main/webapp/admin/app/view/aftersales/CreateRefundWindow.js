Ext.define('App.aftersales.CreateRefundWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.form.Panel',
        'Ext.form.RadioGroup',
        'Ext.form.field.Radio',
        'Ext.form.field.Number'
    ],

    height: 400,
    width: 600,
    layout: 'fit',
    title: '创建退款单',
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
    items: [
        {
            xtype: 'form',
            name: 'form',
            border: false,
            bodyPadding: 10,
            header: false,
            items: [
                {
                    xtype: 'radiogroup',
                    fieldLabel: '退款金额',
                    items: [
                        {
                            xtype: 'radiofield',
                            name: 'default',
                            boxLabel: '默认金额',
                            inputValue: '1',
                            checked: true
                        },
                        {
                            xtype: 'radiofield',
                            name: 'default',
                            boxLabel: '自定义金额',
                            inputValue: '0'
                        }
                    ],
                    listeners: {
                        change: 'onRadiogroupChange'
                    }
                },
                {
                    xtype: 'displayfield',
                    name: 'text',
                    fieldLabel: '金额'
                },
                {
                    xtype: 'numberfield',
                    name: 'money',
                    anchor: '100%',
                    fieldLabel: '金额',
                    disabled: true
                }
            ]
        }
    ],

    onSaveClick: function (button, e, eOpts) {
        var self = this;
        var data = this.find('form').getForm().getValues();
        data['asid'] = this._data['id'];
        Dialog.confirm('确定创建退款单', '确定创建退款单吗？', function (btn) {
            if (btn == 'yes') {
                self.apis.AfterSales.createRefundOrder
                    .wait(self, '正在创建退款单...')
                    .call({object: data}, function () {
                        self.close();
                        if (self._callback) {
                            self._callback();
                        }
                    })
            }
        })
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    setValue: function (data, prices) {
        this._data = data;
        if (prices) {
            var payPrice = prices['payPrice'];
            var goodsName = prices['goodsName'];
            var serialNumber = prices['serialNumber'];

            this.find('text').setValue("注意！创建退款单后需要在“退款单管理”中审核并执行才可以退款！默认退款方式是原路返还！" +
                "<span style='font-weight: bold'>如果使用的支付方式没有在线支付，则不会创建退款单直接退款成功！</span>" +
                "</br>商品 " + goodsName + " " +
                (serialNumber ? serialNumber : '') +
                "</br>支付价格:  ￥" + payPrice +
                "</br>退款额:  ￥" + prices['refundPrice']);
            this.find('money').setValue(prices['refundPrice'])
        } else {
            this.find('text').hide();
        }
    },

    onRadiogroupChange: function (field, newValue, oldValue, eOpts) {
        if (newValue['default'] == 1) {
            this.find('money').disable();
        } else {
            this.find('money').enable();
        }
    }

});
