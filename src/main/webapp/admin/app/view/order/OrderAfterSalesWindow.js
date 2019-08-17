Ext.define('App.order.OrderAfterSalesWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.form.Panel',
        'Ext.form.RadioGroup',
        'Ext.form.field.Radio',
        'Ext.form.field.ComboBox',
        'Ext.form.field.Number',
        'Ext.form.field.TextArea'
    ],

    height: 600,
    width: 800,
    layout: 'fit',
    title: '订单发起售后',
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
                    fieldLabel: '售后类型',
                    allowBlank: false,
                    items: [
                        {
                            xtype: 'radiofield',
                            name: 'type',
                            boxLabel: '退货',
                            inputValue: '10'
                        },
                        {
                            xtype: 'radiofield',
                            name: 'type',
                            boxLabel: '换货',
                            inputValue: '20'
                        },
                        {
                            xtype: 'radiofield',
                            name: 'type',
                            boxLabel: '维修',
                            inputValue: '30'
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    height: 35,
                    html: '<span style="color:#999999">售后商品数量不能大于购买数量，寄回后请检查商品数量是否一致,' +
                        '后台发起售后不限制订单状态请谨慎填写</span>',
                    fieldLabel: '<span style="color:#999999">注意</span>'
                },
                {
                    xtype: 'combobox',
                    name: 'orderGoodsId',
                    anchor: '100%',
                    fieldLabel: '售后商品',
                    editable: false,
                    allowBlank: false,
                    displayField: 'selectName',
                    valueField: 'id',
                    listeners: {
                        change: 'onComboboxChange'
                    }
                },
                {
                    xtype: 'numberfield',
                    name: 'amount',
                    anchor: '100%',
                    allowBlank: false,
                    fieldLabel: '售后数量'
                },
                {
                    xtype: 'radiogroup',
                    fieldLabel: '有无发票',
                    allowBlank: false,
                    items: [
                        {
                            xtype: 'radiofield',
                            name: 'hasInvoice',
                            boxLabel: '无',
                            checked: true,
                            inputValue: '0'
                        },
                        {
                            xtype: 'radiofield',
                            name: 'hasInvoice',
                            boxLabel: '有',
                            inputValue: '1'
                        }
                    ]
                },
                {
                    xtype: 'radiogroup',
                    fieldLabel: '有无检测报告',
                    allowBlank: false,
                    items: [
                        {
                            xtype: 'radiofield',
                            name: 'hasTestReport',
                            boxLabel: '无',
                            checked: true,
                            inputValue: '0'
                        },
                        {
                            xtype: 'radiofield',
                            name: 'hasTestReport',
                            boxLabel: '有',
                            inputValue: '1'
                        }
                    ]
                },
                {
                    xtype: 'combobox',
                    name: 'deliveryType',
                    anchor: '100%',
                    allowBlank: false,
                    fieldLabel: '商品退还方式',
                    displayField: 'name',
                    valueField: 'id',
                    store: {data: [{id: 0, name: '快递寄回'}]}
                },
                {
                    xtype: 'textareafield',
                    name: 'detail',
                    allowBlank: false,
                    anchor: '100%',
                    height: 50,
                    fieldLabel: '售后原因'
                },
                {
                    xtype: 'textareafield',
                    name: 'remark',
                    allowBlank: false,
                    anchor: '100%',
                    height: 50,
                    fieldLabel: '客服备注(用户不可见)'
                }
            ]
        }
    ],

    onSaveClick: function (button, e, eOpts) {
        var self = this;
        var form = this.find('form').getForm(0);
        if (form.isValid()) {
            var data = form.getValues();
            data['orderId'] = this._order['id'];
            Dialog.confirm('确定发起售后', '确定要发起售后吗？', function (btn) {
                if (btn == 'yes') {
                    self.apis.AfterSales.addAfterSales
                        .wait(self, '正在保存售后信息...')
                        .call({object: data}, function () {
                            self.close();
                            if (self._callback) {
                                self._callback();
                            }
                        })
                }
            });
        }
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    onComboboxChange: function (field, newValue, oldValue, eOpts) {
        var sel = field.getSelection();
        if (sel) {
            this.find('amount').setValue(sel.get('amount'))
        }
    },

    setValue: function (order) {
        if (order) {
            this._order = order;
            this.setTitle('订单发起售后-' + order['number']);
            this.find('deliveryType').setValue(0);
            var goods = order['TableOrderGoods'];
            if (goods && Ext.isArray(goods)) {
                var goodsStoreData = [];
                for (var i = 0; i < goods.length; i++) {
                    var skuName = this.orderManager.getSkuName(order, goods[i]);
                    var selectName = goods[i]['goodsName'];
                    if (skuName && Ext.isArray(skuName) && skuName.length > 0) {
                        selectName += '（' + skuName + '）';
                    }
                    goodsStoreData.push({id: goods[i]['id'], selectName: selectName, amount: goods[i]['amount']});
                }
                this.find('orderGoodsId').setStore(Ext.create('Ext.data.Store', {data: goodsStoreData}));
            }
        }
    }

});