Ext.define('App.goods.SelectGoodsWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.grid.Panel',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'Ext.selection.CheckboxModel',
        'Ext.toolbar.Paging',
        'Ext.button.Button'
    ],

    height: 700,
    width: 1200,
    layout: 'fit',
    title: '选择商品',
    defaultListenerScope: true,
    style: 'background:#d0d0d0',
    skuMode: false,

    items: [
        {
            xtype: 'gridpanel',
            name: 'grid',
            border: false,
            header: false,
            style: 'top:1px',
            columns: [],
            selModel: {
                selType: 'checkboxmodel'
            },
            dockedItems: [
                {
                    xtype: 'pagingtoolbar',
                    name: 'paging',
                    dock: 'bottom',
                    width: 360,
                    displayInfo: true,
                    style: {
                        borderBottom: '1px solid #d0d0d0'
                    }
                }
            ]
        }
    ],
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'top',
            items: [
                '->',
                {
                    xtype: 'button',
                    name: 'ok_btn',
                    icon: Resource.png('jet', 'inspectionsOK'),
                    text: '确认选择',
                    listeners: {
                        click: 'onSaveClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'close'),
                    text: '取消关闭',
                    listeners: {
                        click: 'onCloseClick'
                    }
                }
            ]
        }
    ],

    onSaveClick: function (button, e, eOpts) {
        var goods = this.find('grid').getIgnoreSelects(arguments);
        if (goods) {
            for (var i = 0; i < goods.length; i++) {
                var buyAmount = goods[i]['buyAmount'];
                if (this.skuMode) buyAmount = 1;
                if (!buyAmount || buyAmount <= 0) {
                    Dialog.alert('购买数量必须填写');
                    return false;
                }
            }

            for (var i = 0; i < goods.length; i++) {
                if (goods[i]['TableGoodsSku'] && goods[i]['TableGoodsSku'].length > 0
                    && !!!goods[i]['buySkuId']) {
                    Dialog.alert('购买规格必须填写');
                    return false;
                }
            }

            var newGoods = [];
            for (var i = 0; i < goods.length; i++) {
                var gItem = goods[i];
                gItem['buyGoodsId'] = gItem['id'];
                if (gItem['buySkuId'] > 0) {
                    gItem['id'] = gItem['id'] + "_" + gItem['buySkuId'];
                } else {
                    gItem['id'] = gItem['id'];
                }
                newGoods.push(gItem)
            }
            if (this._callSelectGoods) {
                this._callSelectGoods(newGoods);
            }
            this.close();
        } else {
            Dialog.alert('如果您已经确认选择商品请先勾选一个或多个商品前的复选框')
        }
    },

    onCloseClick: function () {
        this.close();
    },

    setValue: function (store) {
        this.find('grid').setStore(store);
        this.find('paging').setStore(store);
    },

    getBaseColumns: function (base) {
        var columns = [];
        columns.push({
            xtype: 'gridcolumn',
            dataIndex: 'fileName',
            width: 80,
            text: '图片',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (!value || value == "") {
                    value = Resource.create('/admin/image/exicon/nopic_40.gif');
                } else {
                    value = Resource.image(value);
                }
                return '<div style="height: 40px;width: 40px;vertical-align: middle;display:table-cell;">' +
                    '<img style="max-height:40px;max-width: 40px;vertical-align: middle" src=' + value + '></div> ';
            }
        })
        columns.push({
            xtype: 'gridcolumn',
            dataIndex: 'name',
            width: 300,
            text: '商品名称',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                metaData.style = 'white-space:normal;word-break:break-all;';
                return '<a style="text-decoration: none" href = "">' + Color.string(value, '#19438B') + '</a>';
            }
        });
        columns.push({
            xtype: 'gridcolumn',
            dataIndex: 'TableGoodsGroup',
            width: 150,
            text: '分类',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (value) {
                    if (Ext.isArray(value)) {
                        var str = [];
                        for (var i = 0; i < value.length; i++) {
                            var name = value[i]['name'];
                            str.push(name);
                        }
                        return Color.string(str.join(" > "), '#666666');
                    } else {
                        if (value) {
                            return value['name'];
                        } else {
                            return '';
                        }
                    }
                } else {
                    var gid = record.get('gid');
                    if (gid == 0) {
                        return Color.string('<无分类>', '#BBBBBB');
                    }
                }
            }
        });
        columns.push({
            xtype: 'gridcolumn',
            dataIndex: 'TableGoodsBrand',
            width: base ? 150 : 100,
            text: '品牌',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                metaData.style = 'white-space:normal;word-break:break-all;';
                if (value) {
                    return value['name'];
                } else {
                    return '[无]';
                }
            }
        });
        columns.push({
            xtype: 'gridcolumn',
            dataIndex: 'TableLogisticsTemplate',
            width: base ? 150 : 100,
            text: '运费模板',
            renderer: function (value) {
                if (!value) {
                    return Color.string('<未设置>', '#a94442')
                }
                return value['name'];
            }
        });
        columns.push({
            xtype: 'gridcolumn',
            dataIndex: 'amount',
            width: 80,
            align: 'center',
            text: '库存数',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                var count = value || 0;
                var str = [];
                str.push("<span style='color: #000;'>");
                str.push(count + ' ' + (record.get('unit') || ''));
                str.push("</span>");
                return str.join("");
            }
        });
        columns.push({
            xtype: 'gridcolumn',
            dataIndex: 'TableGoodsSku',
            width: 100,
            text: '销售价格',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (value) {
                    if (Ext.isArray(value)) {
                        var min = 0;
                        var max = 0;
                        for (var i in value) {
                            var price = value[i]['price'];
                            if (min == 0) min = price;
                            if (max == 0) max = price;
                            if (min >= price) {
                                min = price;
                            }
                            if (max <= price) {
                                max = price;
                            }
                        }
                        return Color.string('￥', '#999999') + Color.string(PriceUtils.string(min), '#ff0000')
                            + ' - ' +
                            Color.string('￥', '#999999') + Color.string(PriceUtils.string(max), '#ff0000');
                    } else {
                        return Color.string('￥', '#999999') + Color.string(PriceUtils.string(value), '#ff0000');
                    }
                } else {
                    return Color.string('￥', '#999999') + Color.string(PriceUtils.string(record.get('price')), '#ff0000');
                }
            }
        });
        columns.push({
            xtype: 'widgetcolumn',
            dataIndex: 'buySkuId',
            width: 150,
            text: '选择规格',
            onWidgetAttach: function (column, widget, record) {
                var skus = record.get('TableGoodsSku');
                widget.setValue('');
                var grid = column.ownerCt.ownerCt.ownerCt;
                if (skus && Ext.isArray(skus)) {
                    var pers = record.get('TableGoodsProperty');
                    var datas = [];
                    if (grid.skuMode) {
                        datas.push({skuId: -1, name: '全部规格'});
                    }
                    for (var i = 0; i < skus.length; i++) {
                        var skuId = skus[i]['id'];
                        var name = [];
                        for (var j = 0; j < pers.length; j++) {
                            if (pers[j]['skuId'] == skuId) {
                                name.push(pers[j]['valueName']);
                            }
                        }
                        if (name.length == 0) name.push("未知");
                        datas.push({skuId: skuId, name: name.join(" + ") + " -> ￥" + skus[i]['price']});
                    }
                    var store = Ext.create('Ext.data.Store', {
                        data: datas
                    });
                    widget.bindStore(store);
                    if (grid.skuMode) {
                        widget.setValue(-1);
                    }
                } else {
                    var store = Ext.create('Ext.data.Store', {
                        data: [{skuId: -1, name: grid.skuMode ? '全部规格' : '默认规格'}]
                    });
                    widget.bindStore(store);
                    widget.setValue(-1);
                }
            },
            widget: {
                xtype: 'combo',
                bind: '{record.buySkuId}',
                displayField: 'name',
                valueField: 'skuId',
                editable: false
            }
        });

        return columns;
    },

    showBuyAmount: function () {
        var grid = this.find('grid');
        var columns = this.getBaseColumns(false);
        columns.push(Ext.create({
            xtype: 'widgetcolumn',
            dataIndex: 'buyAmount',
            width: 100,
            text: '购买数量',
            onWidgetAttach: function (column, widget, record) {
                widget.setValue(1);
            },
            widget: {
                xtype: 'numberfield',
                bind: '{record.buyAmount}',
                allowBlank: false,
                allowDecimals: false
            }
        }));
        grid.setColumns(columns);
    },

    showBaseColumns: function () {
        var grid = this.find('grid');
        var columns = this.getBaseColumns(true);
        grid.setColumns(columns);
    },

    setSureButtonText: function (text) {
        this.find('ok_btn').setText(text);
    },

    setSkuMode: function () {
        // 设置为sku必选模式，如果非必选则会主动添加一个默认sku
        this.skuMode = true;
    }

});