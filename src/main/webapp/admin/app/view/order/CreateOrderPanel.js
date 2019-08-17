Ext.define('App.order.CreateOrderPanel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.form.Panel',
        'Ext.form.Label',
        'Ext.form.field.ComboBox',
        'Ext.grid.Panel',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.selection.CheckboxModel',
        'UXApp.field.GridComboBox'
    ],

    border: false,
    layout: 'fit',
    header: false,
    defaultListenerScope: true,

    items: [
        {
            xtype: 'form',
            name: 'form',
            border: false,
            layout: 'auto',
            bodyPadding: '10px 10px 80px 30px',
            header: false,
            autoScroll: true,
            items: [
                {
                    xtype: 'label',
                    margin: '10px auto 10px -5px',
                    style: {
                        display: 'block'
                    },
                    html: '<div class="basetitle"> <div class="img"> <img border="0" src="image/icon/order.png"> </div> <div class="text">订单基本信息</div> </div>'
                },
                {
                    xtype: 'gridcombobox',
                    name: 'uid',
                    width: 600,
                    hideTrigger: true,
                    fieldLabel: '购买用户',
                    listeners: {
                        gridChange: 'onUserChange'
                    },
                    allowBlank: false,
                    treePanelConfig: {
                        forceFit: true,
                        columns: [
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'userName',
                                text: '用户名'
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'nick',
                                text: '昵称',
                                renderer: function (v) {
                                    if (v) return decodeURIComponent(v);
                                    return '';
                                }
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'email',
                                text: '邮箱'
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'phone',
                                text: '手机号'
                            }
                        ]
                    },
                    searchQuery: true,
                    searchQueryField: 'keyword',
                    displayField: 'selectName',
                    valueField: 'id',
                    gridDisplayField: function (data) {
                        var name = data['userName'];
                        if (data['nick']) {
                            name += " (" + decodeURIComponent(data['nick']) + ")";
                        }
                        return name;
                    }
                },
                {
                    xtype: 'combobox',
                    name: 'aid',
                    width: 600,
                    displayField: 'selectName',
                    valueField: 'id',
                    editable: false,
                    allowBlank: false,
                    fieldLabel: '配送地址'
                },
                {
                    xtype: 'combobox',
                    name: 'platform',
                    width: 600,
                    displayField: 'name',
                    valueField: 'id',
                    fieldLabel: '使用平台',
                    editable: false,
                    allowBlank: false
                },
                {
                    xtype: 'label',
                    margin: '30px auto 10px -5px',
                    style: {
                        display: 'block'
                    },
                    html: '<div class="basetitle"><div class="img"> <img border="0" src="image/icon/product.png"> </div> <div class="text">订单商品信息</div> </div>'
                },
                {
                    xtype: 'gridpanel',
                    name: 'product',
                    border: false,
                    width: 800,
                    header: false,
                    columnLines: true,
                    bodyCls: 'table-cell-middle',
                    columns: [
                        {
                            xtype: 'gridcolumn',
                            width: 380,
                            dataIndex: 'name',
                            text: '商品',
                            renderer: 'getProductHtml'
                        },
                        {
                            xtype: 'gridcolumn',
                            width: 100,
                            dataIndex: 'buySkuId',
                            text: '规格',
                            renderer: function (value, mate, record) {
                                if (value == -1) {
                                    return '默认规格';
                                } else {
                                    var skus = record.get('TableGoodsSku');
                                    if (skus && Ext.isArray(skus)) {
                                        var pers = record.get('TableGoodsProperty');
                                        for (var i = 0; i < skus.length; i++) {
                                            var skuId = skus[i]['id'];
                                            if (value == skuId) {
                                                var name = [];
                                                for (var j = 0; j < pers.length; j++) {
                                                    if (pers[j]['skuId'] == skuId) {
                                                        name.push(pers[j]['valueName']);
                                                    }
                                                }
                                                if (name.length == 0) name.push("未知");
                                                return name.join(" + ") + " -> ￥" + skus[i]['price'];
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        {
                            xtype: 'widgetcolumn',
                            width: 143,
                            text: '价格',
                            widget: {
                                xtype: 'container',
                                layout: 'auto',
                                items: [
                                    {
                                        xtype: 'container',
                                        name: 'price',
                                        width: 120,
                                        anchor: '100%'
                                    },
                                    {
                                        xtype: 'combobox',
                                        name: 'discount',
                                        width: 120,
                                        anchor: '100%',
                                        displayField: 'name',
                                        valueField: 'id',
                                        hideLabel: true,
                                        editable: false,
                                        listeners: {
                                            change: 'onGoodsDiscountChange'
                                        }
                                    }
                                ]
                            },
                            onWidgetAttach: function (column, widget) {
                                var record = widget.getWidgetRecord();
                                var priceWidget = widget.find('price');
                                var discountWidget = widget.find('discount');
                                var value = record.get('price');
                                var discounts = record.get('discounts');
                                var discountId = record.get('discountId');
                                priceWidget.setHtml(Color.string('￥', '#999999') + Color.string(PriceUtils.string(value), '#ff0000'));

                                if (!discounts) discounts = [];
                                discounts.push({id: 0, name: '不使用优惠'});
                                var store = Ext.create('Ext.data.Store', {
                                    data: discounts
                                });
                                discountWidget.setStore(store);
                                discountWidget.$isload = true;
                                try {
                                    if (discountId) {
                                        for (var i = 0; i < store.getData().length; i++) {
                                            var at = store.getAt(i);
                                            if (("" + at.get('id')) == ("" + discountId)) {
                                                discountWidget.setValue(store.getAt(i));
                                            }
                                        }
                                    } else {
                                        discountWidget.setValue(store.getAt(0));
                                    }
                                } finally {
                                    discountWidget.$isload = false;
                                }
                            }
                        },
                        {
                            xtype: 'widgetcolumn',
                            width: 143,
                            dataIndex: 'buyAmount',
                            text: '数量',
                            widget: {
                                xtype: 'numberfield',
                                bind: '{record.buyAmount}',
                                allowBlank: false,
                                allowDecimals: false
                            },
                            onWidgetAttach: function (column, widget) {
                                var ownerCt = column.ownerCt.ownerCt.ownerCt.ownerCt;
                                var spinner = widget.getEl().select('.x-form-trigger-spinner', false);
                                if (spinner && spinner.elements && spinner.elements.length > 0) {
                                    spinner.elements[0].addEventListener('click', function () {
                                        ownerCt.onAmountChange();
                                    });
                                }
                            }
                        }
                    ],
                    dockedItems: [
                        {
                            xtype: 'toolbar',
                            dock: 'top',
                            items: [
                                {
                                    xtype: 'button',
                                    icon: Resource.png('jet', 'add_dark'),
                                    text: '添加商品',
                                    listeners: {
                                        click: 'onAddProductClick'
                                    }
                                },
                                {
                                    xtype: 'button',
                                    icon: Resource.png('jet', 'close'),
                                    text: '批量移除商品',
                                    listeners: {
                                        click: 'onRemoveProductClick'
                                    }
                                },
                                '->',
                                {
                                    xtype: 'label',
                                    text: '提示：修改修改数量后需要保存订单'
                                }
                            ]
                        }
                    ],
                    selModel: {
                        selType: 'checkboxmodel'
                    }
                },
                {
                    xtype: 'label',
                    margin: '30px auto 10px -5px',
                    style: {
                        display: 'block'
                    },
                    html: '<div class="basetitle"><div class="img"><img border="0" src="image/icon/product.png"></div><div class="text">配送方式</div></div>'
                },
                {
                    xtype: 'combobox',
                    name: 'deliveryType',
                    width: 820,
                    displayField: 'name',
                    valueField: 'id',
                    editable: false,
                    allowBlank: false,
                    fieldLabel: '配送方式',
                    listeners: {
                        change: 'onDeliveryTypeChange'
                    }
                },
                {
                    xtype: 'label',
                    margin: '30px auto 10px -5px',
                    style: {
                        display: 'block'
                    },
                    html: '<div class="basetitle"><div class="img"><img border="0" src="image/icon/cash.png"></div><div class="text">订单结算信息</div></div>'
                },
                {
                    xtype: 'container',
                    name: 'cash_info',
                    width: 820
                }
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    items: [
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'menu-saveall'),
                            text: '保存订单',
                            listeners: {
                                click: 'onSaveClick'
                            }
                        },
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'Reset_to_empty'),
                            text: '重新填写',
                            listeners: {
                                click: 'onReloadClick'
                            }
                        }
                    ]
                },
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'back'),
                            text: '返回列表',
                            listeners: {
                                click: 'onBackClick'
                            }
                        }
                    ]
                }
            ]
        }
    ],

    onUserChange: function (field, data) {
        if (data) {
            var delivery = this.find('aid');
            var uid = data['id'];
            var store = this.apis.UserAddress.getUserAddressByUserUid.createListStore({uid: uid}, function (record) {
                var data = record.getData();
                if (data != null && data.length > 0) {
                    delivery.setValue(data.getAt(0)['id']);
                }
            });
            delivery.setStore(store);
            store.load();
        }
    },

    getOrderValue: function () {
        var self = this;
        var form = this.find('form').getForm();
        var data = form.getValues();
        var grid = self.find('product');
        var store = grid.getStore();
        var storeData = store.getData();
        var goods = [];
        storeData.each(function (item) {
            var dataItem = item.getData();
            var pd = {pid: dataItem['buyGoodsId'], buyAmount: dataItem['buyAmount']};
            if (dataItem['buySkuId']) {
                pd['skuId'] = dataItem['buySkuId'];
            }
            if (dataItem['discountId']) {
                pd['discountId'] = dataItem['discountId'];
            }
            goods.push(pd);
        });

        return {order: data, products: goods};
    },

    onSaveClick: function (button, e, eOpts) {
        var self = this;
        var form = this.find('form').getForm();
        var order = this.getOrderValue();
        if (order) {
            if (form.isValid()) {
                Dialog.confirm('确定添加', '确定添加订单吗？', function (btn) {
                    if (btn == 'yes') {
                        self.apis.Order.addOrder
                            .wait(self, '正在添加订单...')
                            .call(order, function () {
                                self.parent.back();
                                if (self._callback) {
                                    self._callback();
                                }
                            })
                    }
                });
            }
        }
    },

    onGoodsDiscountChange: function (combobox) {
        var record = combobox.ownerCt.getWidgetRecord();
        record.set('discountId', combobox.getValue());
        if (!combobox.$isload) {
            this.reCalChange();
        }
    },

    onAmountChange: function () {
        var productView = this.find('product');
        if (!productView.$isload) {
            this.reCalChange();
        }
    },

    reCalChangeCall: function (data) {
        if (data) {
            var aid = 'goods-price';
            var bid = 'goods-discounts';
            var cid = 'goods-delivery';
            var did = 'goods-total';
            var view = this.find('cash_info');
            var anode = view.getEl().selectNode('.' + aid);
            var bnode = view.getEl().selectNode('.' + bid);
            var cnode = view.getEl().selectNode('.' + cid);
            var dnode = view.getEl().selectNode('.' + did);

            anode.innerHTML = "¥" + data['goodsPrice'];
            bnode.innerHTML = "¥" + data['discountPrice'];
            cnode.innerHTML = "¥" + data['freightPrice'];
            dnode.innerHTML = "¥" + data['orderPrice'];
        }
    },

    reCalChange: function () {
        var self = this;
        var order = this.getOrderValue();
        this.apis.Order.getOrderPrices
            .wait(this, '正在计算价格...')
            .call(order, function (data) {
                if (data) {
                    self.reCalChangeCall(data);
                }
            });
        return false;
    },

    onAddProductClick: function () {
        var self = this;
        var uid = this.find('uid').getValue();
        var platform = this.find('platform').getValue();
        if (uid && platform != null) {
            var win = Dialog.openWindow('App.goods.SelectGoodsWindow', {
                _callSelectGoods: function (goods) {
                    if (goods && Ext.isArray(goods)) {
                        var order = self.getOrderValue();
                        if (!order.products) order.products = [];
                        for (var i = 0; i < goods.length; i++) {
                            var gitem = goods[i];
                            order.products.push({
                                pid: gitem['buyGoodsId'],
                                buyAmount: gitem['buyAmount'],
                                buySkuId: gitem['buySkuId'] || 0
                            });
                        }
                        self.apis.Order.getOrderPrices
                            .wait(self, '正在加载商品...')
                            .call(order, function (data) {
                                var productView = self.find('product');
                                var deliveryTypeView = self.find('deliveryType');

                                productView.$isload = true;
                                try {
                                    var storeGoods = Ext.create('Ext.data.Store', {
                                        data: data['buyGoods']
                                    });
                                    productView.setStore(storeGoods);
                                } finally {
                                    productView.$isload = false;
                                }

                                deliveryTypeView.$isload = true;
                                try {
                                    var storeDeliveryTypes = Ext.create('Ext.data.Store', {
                                        data: data['deliveryTypes']
                                    });
                                    deliveryTypeView.setStore(storeDeliveryTypes);
                                    if (data['deliveryType']) {
                                        for (var j = 0; j < data['deliveryTypes'].length; j++) {
                                            if (storeDeliveryTypes.getAt(j).get('id') == data['deliveryType']) {
                                                deliveryTypeView.setValue(storeDeliveryTypes.getAt(j));
                                            }
                                        }
                                    }
                                } finally {
                                    deliveryTypeView.$isload = false;
                                }
                                self.reCalChangeCall(data);
                            })
                    }
                }
            });
            win.showBuyAmount();
            win.setSureButtonText('确认添加到订单');

            var store = this.apis.Goods.getGoods.createPageStore();
            win.setValue(store);
            store.load();
        } else {
            Dialog.alert('请先选择购买用户和使用平台');
        }
    },

    onRemoveProductClick: function () {
        var gs = this.find('product').getSelection();
        if (gs && gs.length > 0) {
            var store = this.find('product').getStore();
            store.remove(gs);
            this.reCalChange();
        }
    },

    onDeliveryTypeChange: function (combobox) {
        if (!combobox.$isload) {
            this.reCalChange();
        }
    },

    onReloadClick: function (button, e, eOpts) {
        this.parent.redraw();
    },

    onBackClick: function (button, e, eOpts) {
        this.parent.back();
    },

    getProductHtml: function (value, mate, record) {
        try {
            var html = this.preView.getOrderProductHtml([record.getData()], 2);
        } catch (e) {
            console.error(e);
        }
        return html;
    },

    setCashInfoHtml: function () {
        var cmpId = this.id;
        var view = this.find('cash_info');
        var html = [
            '<div style="min-height: 150px;color: #666;font-size: 12px;font-weight: 500;padding: 20px;overflow: hidden">',
            '<div style="width: 300px;float: right;height: 28px;line-height: 28px">',
            '<div style="width:170px;float:left;text-align: right">商品总额：</div>',
            '<div class="goods-price" style="width: 130px;float:left;text-align: right">¥0.00</div>',
            '<div style="width:170px;float:left;text-align: right">优　　惠：</div>',
            '<div class="goods-discounts" style="width: 130px;float:left;text-align: right">-¥0.00</div>',
            '<div style="width:170px;float:left;text-align: right">运　　费：</div>',
            '<div class="goods-delivery" style="width: 130px;float:left;text-align: right">¥0.00</div>',
            '<div style="width:170px;float:left;text-align: right;color: #e4393c">订单总额：</div>',
            '<div class="goods-total" style="width: 130px;float:left;text-align: right;font-size:18px;line-height: 28px;color: #e4393c">¥0.00</div>',
            '</div>',
            '</div>'
        ];
        view.setHtml(html.join(''));
    },

    setValue: function (values) {
        if (values && values['product']) {
            var productPanel = this.find('product');
            productPanel.setStore(Ext.create('Ext.data.Store', {
                data: values['products']
            }));
        }

        var userIdView = this.find('uid');
        var store = this.apis.User.getOrderUsers.createPageStore({});
        userIdView.setGridStore(store);
        userIdView.setPaging(true);
        store.load();

        this.setCashInfoHtml();

        var store = this.apis.Order.getOrderPlatforms.createListStore();
        this.find('platform').setStore(store);
    }

});