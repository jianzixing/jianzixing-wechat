Ext.define('App.goods.GoodsEditAmountWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.Text',
        'Ext.grid.Panel',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.view.Table',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 235,
    width: 850,
    layout: 'border',
    title: '修改销售属性',
    defaultListenerScope: true,

    items: [
        {
            xtype: 'form',
            name: 'form',
            region: 'north',
            border: false,
            layout: {
                type: 'table',
                columns: 2
            },
            bodyPadding: 10,
            header: false,
            items: [
                {
                    xtype: 'textfield',
                    name: 'price',
                    margin: '5px 20px 5px 10px',
                    width: 380,
                    fieldLabel: '销售价(元)',
                    selectOnFocus: true
                },
                {
                    xtype: 'textfield',
                    name: 'vipPrice',
                    margin: '5px 20px 5px 10px',
                    width: 380,
                    fieldLabel: '会员价(元)',
                    selectOnFocus: true
                },
                {
                    xtype: 'textfield',
                    name: 'originalPrice',
                    margin: '5px 20px 5px 10px',
                    width: 380,
                    fieldLabel: '原价(元)',
                    selectOnFocus: true
                },
                {
                    xtype: 'textfield',
                    name: 'costPrice',
                    margin: '5px 20px 5px 10px',
                    width: 380,
                    fieldLabel: '成本价(元)',
                    selectOnFocus: true
                },
                {
                    xtype: 'textfield',
                    name: 'amount',
                    margin: '5px 20px 5px 10px',
                    width: 380,
                    fieldLabel: '库存',
                    selectOnFocus: true
                },
                {
                    xtype: 'textfield',
                    name: 'serialNumber',
                    margin: '5px 20px 5px 10px',
                    width: 380,
                    fieldLabel: '商品编号',
                    selectOnFocus: true
                }
            ]
        },
        {
            xtype: 'gridpanel',
            name: 'sales_grid',
            region: 'center',
            border: false,
            header: false,
            cls: 'grid-simple-line',
            minHeight: 150,
            bodyPadding: 10,
            plugins: [
                {
                    ptype: 'cellediting',
                    name: 'cellediting_edit_product',
                    clicksToEdit: 1,
                    listeners: {
                        edit: 'onFieldEdit'
                    }
                }
            ],
            columns: []
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
                    text: '确定保存',
                    icon: Resource.png('jet', 'menu-saveall'),
                    listeners: {
                        click: 'onSaveClick'
                    }
                },
                {
                    xtype: 'button',
                    text: '取消关闭',
                    icon: Resource.png('jet', 'closeActive'),
                    listeners: {
                        click: 'onCloseClick'
                    }
                },
                '->'
            ]
        }
    ],

    onSaveClick: function (button, e, eOpts) {
        var price = this.find('price').getValue();
        var vipPrice = this.find('vipPrice').getValue();
        var originalPrice = this.find('originalPrice').getValue();
        var costPrice = this.find('costPrice').getValue();
        var amount = this.find('amount').getValue();
        var serialNumber = this.find('serialNumber').getValue();

        var data = {
            price: price,
            vipPrice: vipPrice,
            originalPrice: originalPrice,
            costPrice: costPrice,
            amount: amount,
            serialNumber: serialNumber
        };

        var sku = [];

        var grid = this.find('sales_grid');
        var store = grid.getStore();
        var items = store.getData().items;
        for (var i = 0; i < items.length; i++) {
            var item = items[i];
            item.commit();
            sku.push(item.getData());
        }
        var self = this;
        Dialog.confirm('提示', '确定修改售卖信息？', function (btn) {
            if (btn == 'yes') {
                self.close();
                if (self.callback) {
                    self.callback(data, sku);
                }
            }
        });
    },

    onCloseClick: function (button, e, eOpts) {
        this.close();
    },

    onFieldEdit: function (p, e) {
        // e.record.commit();

        var grid = this.find('sales_grid');
        var store = grid.getStore();
        var items = store.getData().items;
        var amount = 0;
        for (var i = 0; i < items.length; i++) {
            amount += parseInt(items[i].get('amount'));
        }
        this.find('amount').setValue(amount);
    },

    setSingle: function () {
        var height = 235;
        this.setHeight(height);
        this.find('sales_grid').hide();
    },

    setGoodsValue: function (json) {
        this.setTitle('修改销售属性 - ' + json['name']);
        var form = this.find('form');
        var sku = json['TableGoodsSku'];
        var property = json['TableGoodsProperty'];
        form.getForm().setValues(json);
        if (sku) {
            this.setHeight(450);
            this.setY(this.getY() - 100);
            this.find('amount').setDisabled(true);
            var gridColumns = [];
            var data = [];

            for (var j = 0; j < sku.length; j++) {
                var skuItem = sku[j];
                var skuId = skuItem['id'];
                var skuName = [];
                for (var i = 0; i < property.length; i++) {
                    var p = property[i];
                    if (p['skuId'] == skuId) {
                        skuName.push(p['valueName']);
                    }
                }
                skuItem['skuName'] = skuName;
                data.push(skuItem);
            }

            gridColumns.push(Ext.create(
                {
                    xtype: 'gridcolumn',
                    align: 'center',
                    dataIndex: 'skuName',
                    text: '规格SKU',
                    renderer: function (value) {
                        return value.join("<span style='margin: auto 5px auto 5px;color: #000;font-weight: bold'>X</span>")
                    }
                }
            ));

            gridColumns.push(Ext.create(
                {
                    xtype: 'gridcolumn',
                    minWidth: 150 - 10,
                    align: 'center',
                    dataIndex: 'price',
                    text: '销售价' + Color.string('*', 'red') + '(元)',
                    renderer: function (v) {
                        return PriceUtils.string(v || 0);
                    },
                    field: {
                        xtype: 'textfield',
                        inputType: 'number',
                        selectOnFocus: true
                    }
                }
            ));

            gridColumns.push(Ext.create(
                {
                    xtype: 'gridcolumn',
                    minWidth: 150 - 10,
                    align: 'center',
                    dataIndex: 'vipPrice',
                    text: '会员价(元)',
                    renderer: function (v) {
                        return PriceUtils.string(v || 0);
                    },
                    field: {
                        xtype: 'textfield',
                        inputType: 'number',
                        selectOnFocus: true
                    }
                }
            ));

            gridColumns.push(Ext.create(
                {
                    xtype: 'gridcolumn',
                    minWidth: 150 - 10,
                    align: 'center',
                    dataIndex: 'originalPrice',
                    text: '原价(元)',
                    renderer: function (v) {
                        return PriceUtils.string(v || 0);
                    },
                    field: {
                        xtype: 'textfield',
                        inputType: 'number',
                        selectOnFocus: true
                    }
                }
            ));

            gridColumns.push(Ext.create(
                {
                    xtype: 'gridcolumn',
                    minWidth: 150 - 10,
                    align: 'center',
                    dataIndex: 'costPrice',
                    text: '成本价(元)',
                    renderer: function (v) {
                        return PriceUtils.string(v || 0);
                    },
                    field: {
                        xtype: 'textfield',
                        inputType: 'number',
                        selectOnFocus: true
                    }
                }
            ));

            gridColumns.push(Ext.create(
                {
                    xtype: 'gridcolumn',
                    minWidth: 150 - 10,
                    align: 'center',
                    dataIndex: 'amount',
                    text: '库存',
                    renderer: function (v) {
                        if (v == null) {
                            return 0;
                        }
                        return v;
                    },
                    field: {
                        xtype: 'textfield',
                        inputType: 'number',
                        selectOnFocus: true
                    }
                }
            ));

            gridColumns.push(Ext.create(
                {
                    xtype: 'gridcolumn',
                    minWidth: 150,
                    align: 'center',
                    dataIndex: 'serialNumber',
                    text: '商品编号',
                    field: {
                        xtype: 'textfield',
                        selectOnFocus: true
                    }
                }
            ));

            this.find('sales_grid').setColumns(gridColumns);
            var store = Ext.create('Ext.data.Store', {data: data});
            this.find('sales_grid').setStore(store);
            gridColumns[0].autoSize();
        } else {
            this.setSingle();
        }
    }

});
