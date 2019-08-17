Ext.define('App.order.OrderSendOutWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.toolbar.Toolbar'
    ],
    height: 300,
    width: 500,
    layout: 'fit',
    title: '订单发货',
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
                    text: '确定发货',
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
                    xtype: 'hidden',
                    name: 'oid'
                },
                {
                    xtype: 'fieldcontainer',
                    name: 'detail',
                    html: '',
                    fieldLabel: '配送信息'
                },
                {
                    xtype: 'combo',
                    name: 'cid',
                    anchor: '100%',
                    fieldLabel: '快递公司',
                    displayField: 'name',
                    valueField: 'code',
                    editable: false,
                    allowBlank: false
                },
                {
                    xtype: 'textfield',
                    name: 'number',
                    anchor: '100%',
                    fieldLabel: '快递单号',
                    allowBlank: false
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
                Dialog.confirm('提示', '确定提交订单物流信息吗？', function (btn) {
                    if (btn == 'yes') {
                        if (self._is_edit) {
                            self.apis.Order.updateOrderDelivery
                                .wait(self, '正在保存物流信息...')
                                .call(data, function () {
                                    self.close();
                                    if (self._callback) {
                                        self._callback();
                                    }
                                });
                        } else {
                            self.apis.Order.sendOutOrder
                                .wait(self, '正在保存物流信息...')
                                .call(data, function () {
                                    self.close();
                                    if (self._callback) {
                                        self._callback();
                                    }
                                });
                        }
                    }
                });
            } else {
                Dialog.alert('配送订单未知')
            }
        }
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    setEdit: function (is) {
        this._is_edit = is;
    },

    setValue: function (order) {
        if (order) {
            var number = order['number'];
            this.setTitle('订单发货 - ' + number);
            this._data = order;
            this.find('oid').setValue(order['id']);

            var address = order['TableOrderAddress'];
            var detailView = this.find('detail');
            detailView.setHtml(
                address['province'] + address['city'] + address['county'] + address['address']
                + '<span style="margin-left: 20px"></span>' + address['realName']
                + '<span style="margin-left: 20px"></span>' + (address['phoneNumber'] || address['telNumber']));
        }

        var store = this.apis.Logistics.getLogisticsCompany.createListStore();
        this.find('cid').setStore(store);
        store.load();

        if (this._is_edit) {
            this.find('cid').setValue(order['lgsCompanyCode']);
            this.find('number').setValue(order['trackingNumber']);
        } else {
            var index = store.find('isDefault', 1);
            if (index > -1) {
                var model = store.getAt(index);
                this.find('cid').setValue(model.get('code'));
            }
        }
    }
});