Ext.define('App.discount.DiscountDetailGoodsWindow', {
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

    items: [
        {
            xtype: 'gridpanel',
            name: 'grid',
            border: false,
            header: false,
            style: 'top:1px',
            columns: [
                {
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
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'name',
                    width: 350,
                    text: '商品名称',
                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                        metaData.style = 'white-space:normal;word-break:break-all;';
                        return '<a style="text-decoration: none" href = "">' + Color.string(value, '#19438B') + '</a>';
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'TableGoodsGroup',
                    width: 200,
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
                },
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'TableGoodsBrand',
                    text: '品牌',
                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                        metaData.style = 'white-space:normal;word-break:break-all;';
                        if (value) {
                            return value['name'];
                        } else {
                            return '';
                        }
                    }
                },
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'TableLogisticsTemplate',
                    text: '运费模板',
                    renderer: function (value) {
                        if (!value) {
                            return Color.string('<未设置>', '#a94442')
                        }
                        return value['name'];
                    }
                },
                {
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
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'TableGoodsSku',
                    width: 150,
                    text: '销售价格',
                    renderer: price = function (value, metaData, record, rowIndex, colIndex, store, view) {
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
                }
            ],
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
                    icon: Resource.png('jet', 'close'),
                    text: '取消关闭',
                    listeners: {
                        click: 'onCloseClick'
                    }
                }
            ]
        }
    ],

    onCloseClick: function () {
        this.close();
    },

    setValue: function (store) {
        this.find('grid').setStore(store);
        this.find('paging').setStore(store);
        store.load();
    }

});