Ext.define('App.goods.RecycleManage', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.view.Table',
        'Ext.button.Button',
        'Ext.form.field.Text',
        'Ext.toolbar.Paging',
        'Ext.selection.CheckboxModel'
    ],

    height: 450,
    width: 743,
    header: false,
    defaultListenerScope: true,

    apis: {
        Goods: {
            deleteRecycleGoods: {},
            resetRecycleGoods: {}
        }
    },

    api: {
        Goods: {getRecycleGoods: {_page: 'App.goods.RecycleManage'}}
    },

    columns: [
        {
            xtype: 'actioncolumn',
            width: 70,
            text: '操作',
            dataIndex: 'id',
            items: [
                {
                    icon: Resource.png('jet', 'addToWatch_dark'),
                    tooltip: '商品详情',
                    handler: 'onShowDetailProduct'
                },
                '->',
                {
                    icon: Resource.png('jet', 'cancel'),
                    tooltip: '删除商品',
                    handler: 'onCellDeleteProduct'
                }
            ]
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'TableGoodsImage',
            width: 80,
            text: '图片',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (!value || value == "") {
                    value = Resource.create('/bg/image/exicon/nopic_40.gif');
                } else {
                    value = Resource.image(value[0]['fid']);
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
        },
        {
            xtype: 'gridcolumn',
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
            dataIndex: '',
            text: '运费模板',
            renderer: function (value) {
                if (!value) {
                    return Color.string('<未设置>', '#a94442')
                }
                return value;
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'status',
            width: 80,
            text: '销售状态',
            align: 'center',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (value == 0) {
                    return Color.string('未审核', '#999999')
                } else if (value == 10) {
                    return Color.string('已审核', '#2E68AA')
                } else if (value == 20) {
                    return Color.string('下架', '#b4206e')
                } else if (value == 30) {
                    return Color.string('上架', '#56A36C')
                } else if (value == 40) {
                    return Color.string('无效', '#ff0000')
                } else {
                    return Color.string('未知状态', '#E9AE6A')
                }
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'amount',
            width: 85,
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
            width: 180,
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
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'createTime',
            width: 165,
            text: '时间',
            renderer: function (value, md, record) {
                var createTime = value;
                var editTime = record.get('editTime');
                var str = [];
                str.push("<span style='font-size: 12px;color: #333333'>");
                if (createTime) {
                    str.push((new Date(createTime)).format('yyyy-MM-dd HH:mm'));
                    str.push(" (发布)")
                }
                if (editTime) {
                    str.push("<br/>");
                    str.push((new Date(createTime)).format('yyyy-MM-dd HH:mm'));
                    str.push(" (修改)")
                }
                str.push("</span>");
                return str.join("");
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
                    icon: Resource.png('jet', 'list'),
                    text: '列出回收站',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'resetStrip'),
                    text: '批量恢复商品',
                    listeners: {
                        click: 'onResetClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '批量彻底删除',
                    listeners: {
                        click: 'onBatchDeleteClick'
                    }
                },
                '->',
                {
                    xtype: 'textfield',
                    name: 'name',
                    fieldLabel: '商品名称',
                    labelWidth: 60
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'search'),
                    text: '搜索',
                    listeners: {
                        click: 'onSearchClick'
                    }
                }
            ]
        },
        {
            xtype: 'pagingtoolbar',
            name: 'pagingtoolbar',
            dock: 'bottom',
            width: 360,
            displayInfo: true
        }
    ],
    selModel: {
        selType: 'checkboxmodel'
    },

    onListClick: function () {
        this.getStore().reloadMember();
    },

    onBatchDeleteClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.getSelect();
        Dialog.batch({
            message: '确定彻底删除（无法恢复）商品{d}吗？',
            data: jsons,
            key: 'name',
            callback: function (btn) {
                if (btn == 'yes') {
                    var ids = Array.splitArray(jsons, 'id');
                    self.apis.Goods.deleteRecycleGoods
                        .wait(self, '正在删除商品...')
                        .call({ids: ids}, function () {
                            self.refreshStore();
                        })
                }
            }
        });
    },

    onCellDeleteProduct: function () {
        var self = this;
        var jsons = this.getIgnoreSelects(arguments);
        Dialog.batch({
            message: '确定彻底删除（无法恢复）商品{d}吗？',
            data: jsons,
            key: 'name',
            callback: function (btn) {
                if (btn == 'yes') {
                    var ids = Array.splitArray(jsons, 'id');
                    self.apis.Goods.deleteRecycleGoods
                        .wait(self, '正在删除商品...')
                        .call({ids: ids}, function () {
                            self.refreshStore();
                        })
                }
            }
        });
    },


    onResetClick: function () {
        var self = this;
        var jsons = this.getSelect();
        Dialog.batch({
            message: '确定恢复商品{d}吗（恢复到原本商品分类中）？',
            data: jsons,
            key: 'name',
            callback: function (btn) {
                if (btn == 'yes') {
                    var ids = Array.splitArray(jsons, 'id');
                    self.apis.Goods.resetRecycleGoods
                        .wait(self, '正在恢复商品...')
                        .call({ids: ids}, function () {
                            self.refreshStore();
                        })
                }
            }
        });
    },

    onSearchClick: function () {
        var name = this.find('name').getValue();
        this.getStore().reloadMember({name: name});
    },

    onAfterApply: function () {
        this.find('pagingtoolbar').setStore(this.getStore());
    }

});