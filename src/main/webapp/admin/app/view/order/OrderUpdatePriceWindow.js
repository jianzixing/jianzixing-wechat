Ext.define('App.order.OrderUpdatePriceWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.FieldContainer',
        'Ext.form.field.Number',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 300,
    width: 500,
    layout: 'fit',
    title: '修改订单价格',
    defaultListenerScope: true,

    items: [
        {
            xtype: 'form',
            border: false,
            bodyPadding: 10,
            header: false,
            items: [
                {
                    xtype: 'fieldcontainer',
                    name: 'detail',
                    fieldLabel: '订单价格信息'
                },
                {
                    xtype: 'numberfield',
                    name: 'price',
                    anchor: '100%',
                    fieldLabel: '修改后价格'
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
                    text: '确定修改',
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
        var price = this.find('price').getValue();
        Dialog.confirm('确定修改价格', '确定修改订单' + this._order['number'] + '的价格吗？', function () {
            self.apis.Order.updateOrderPrice
                .wait(self, '正在修改订单价格...')
                .call({id: self._order['id'], price: price}, function () {
                    self.close();
                    if (self._callback) {
                        self._callback();
                    }
                })
        })
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    setValue: function (order) {
        if (order) {
            this._order = order;
            var number = order['number'];
            this.setTitle('修改订单价格 - ' + number);

            var detailView = this.find('detail');
            detailView.setHtml('￥' + order['payPrice']);
            this.find('price').setValue(order['payPrice']);
        }
    }

});