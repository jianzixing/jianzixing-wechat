Ext.define('App.goods.GoodsManager', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'Ext.button.Button',
        'Ext.toolbar.Paging',
        'Ext.selection.CheckboxModel',
        'BaseApp.datetime.DateTime'
    ],

    border: false,
    header: false,
    defaultListenerScope: true,
    layout: 'border',

    apis: {
        GoodsGroup: {
            getGoodsGroups: {}
        },
        GoodsParameter: {
            getGroups: {},
            getGroupSet: {}
        },
        Goods: {
            deleteGoods: {},
            setGoodsPutAway: {},
            setGoodSoldOut: {},

            getGoods: {},
            getSingleGoods: {},
            updateGoodsSales: {},
            updateGoodsTitle: {},
            updateGoodsBrand: {},
            updateGoodsValidTime: {},

            addGoods: {},
            updateGoods: {}
        },
        Brand: {getBrands: {}},
        Logistics: {getTemplatesByKeyword: {}},
        Support: {getSupportByGroup: {}}
    },

    search: true,

    items: [
        {
            xtype: 'treepanel',
            name: 'classify',
            region: 'west',
            split: true,
            hidden: true,
            displayField: 'name',
            collapsible: true,
            collapsed: true,
            title: '商品分类信息',
            width: 210,
            viewConfig: {
                listeners: {
                    itemclick: 'onViewItemClick',
                    itemcontextmenu: 'onViewItemContextMenu'
                }
            }
        },
        {
            xtype: 'gridpanel',
            region: 'center',
            name: 'products',
            split: true,
            border: false,
            header: false,
            bodyCls: 'table-cell-middle',
            apiDelay: true,
            selModel: {
                selType: 'checkboxmodel'
            },
            columns: [
                {
                    xtype: 'actioncolumn',
                    width: 100,
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
                            icon: Resource.png('jet', 'Editor'),
                            tooltip: '修改商品',
                            handler: 'onCellEditProduct'
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
                    dataIndex: 'fileName',
                    width: 100,
                    text: '图片',
                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                        if (!value || value == "") {
                            value = Resource.create('/admin/image/exicon/nopic_40.gif');
                        } else {
                            value = Resource.image(value);
                        }
                        return '<div style="height: 60px;width: 60px;vertical-align: middle;display:table-cell;overflow: hidden">' +
                            '<img style="max-height:60px;max-width: 60px;vertical-align: middle" src=' + value + '></div> ';
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'name',
                    width: 350,
                    text: '商品名称',
                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                        metaData.style = 'white-space:normal;word-break:break-all;height:70px;';
                        var td = record.get('TableDiscount');
                        var html = [];
                        html.push('<div style="max-height: 40px;overflow: hidden">');
                        html.push('<a style="text-decoration:none" href="javascript:void(0);">' + Color.string(value, '#19438B') + '</a>');
                        html.push('</div>');
                        if (td && td.length > 0) {
                            html.push('<div style="color: red">');
                            html.push('优惠: ');
                            for (var i = 0; i < td.length; i++) {
                                html.push(td[i]['name']);
                                if (i !== td.length - 1) {
                                    html.push(" , ");
                                }
                            }
                            html.push('</div>');
                        }
                        return html.join('');
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'TableGoodsGroup',
                    width: 150,
                    text: '分类',
                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                        metaData.style = 'white-space:normal;word-break:break-all;';
                        if (value) {
                            var listNames = value['listName'];
                            if (listNames) {
                                var listNameArr = listNames.split(',');
                                listNameArr.splice(0, 1);
                                listNameArr.push(value['name']);
                                return Color.string(listNameArr.join(" > "), '#999999');
                            }
                        } else {
                            var gid = record.get('gid');
                            if (gid == 0) {
                                return Color.string('[无分类]', '#BBBBBB');
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
                    dataIndex: 'TableLogisticsTemplate',
                    text: '运费模板',
                    renderer: function (value) {
                        if (!value) {
                            return Color.string('[未设置]', '#a94442')
                        }
                        return value['name'];
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
                        } else if (value == 50) {
                            return Color.string('已过期', '#ff0000')
                        } else {
                            return Color.string('未知状态', '#E9AE6A')
                        }
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'amount',
                    width: 180,
                    align: 'center',
                    text: '库存数',
                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                        var count = value || 0;
                        var str = [];
                        var unitStr = (record.get('unit') || '');
                        str.push("<span style='color: #000;'>");
                        str.push(count + ' ' + unitStr);
                        var TableGoodsSku = record.get('TableGoodsSku');
                        if (TableGoodsSku && Ext.isArray(TableGoodsSku)) {
                            var min = 0;
                            var max = 0;
                            for (var i = 0; i < TableGoodsSku.length; i++) {
                                var amount = TableGoodsSku[i]['amount'];
                                if (min == 0) min = amount;
                                if (max == 0) max = amount;
                                if (min >= amount) min = amount;
                                if (max <= amount) max = amount;
                            }
                            str.push("<br/>");
                            str.push(" ( 最少" + min + unitStr + " ~ 最多" + max + unitStr + " )");
                        }
                        str.push("</span>");
                        return str.join("");
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'TableGoodsSku',
                    width: 180,
                    text: '销售价格',
                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                        if (value) {
                            if (Ext.isArray(value)) {
                                var min = 0;
                                var max = 0;
                                for (var i = 0; i < value.length; i++) {
                                    var price = value[i]['price'];
                                    if (min == 0) min = price;
                                    if (max == 0) max = price;
                                    if (min >= price) min = price;
                                    if (max <= price) max = price;
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
                            str.push((new Date(editTime)).format('yyyy-MM-dd HH:mm'));
                            str.push(" (修改)")
                        }
                        str.push("</span>");
                        return str.join("");
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'validTime',
                    width: 165,
                    text: '有效期至',
                    renderer: function (value) {
                        var str = [];
                        str.push("<span style='font-size: 12px;color: #333333'>");
                        if (value && value != 0) {
                            str.push((new Date(value)).format('yyyy-MM-dd HH:mm:ss'));
                        } else {
                            str.push('未设置有效期');
                        }
                        str.push("</span>");
                        return str.join("");
                    }
                }
            ],
            dockedItems: [
                {
                    xtype: 'pagingtoolbar',
                    name: 'pagingtoolbar',
                    dock: 'bottom',
                    width: 360,
                    displayInfo: true
                }
            ]
        }
    ],
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'button',
                    text: '列出商品',
                    icon: Resource.png('jet', 'list'),
                    listeners: {
                        click: 'onListGoodsClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addFolder'),
                    text: '添加商品',
                    listeners: {
                        click: 'onAddGoodsClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '批量删除商品',
                    listeners: {
                        click: 'onBatchDeleteClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'upLevel'),
                    text: '上架',
                    listeners: {
                        click: 'onProductUpClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'downLevel'),
                    text: '下架',
                    listeners: {
                        click: 'onProductDownClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'split'),
                    text: '变更信息',
                    menu: {
                        xtype: 'menu',
                        items: [
                            {
                                xtype: 'menuitem',
                                icon: Resource.png('jet', 'editColors_dark'),
                                text: '售卖信息',
                                listeners: {
                                    click: 'onEditAmountClick'
                                }  //价格和数量
                            },
                            {
                                xtype: 'menuitem',
                                icon: Resource.png('jet', 'editSource'),
                                text: '名称标题',
                                listeners: {
                                    click: 'onEditTitleClick'
                                }  //标题和副标题
                            },
                            {
                                xtype: 'menuitem',
                                icon: Resource.png('jet', 'showEditorHighlighting_dark'),
                                text: '运费信息',
                                listeners: {
                                    click: 'onEditPostTplClick'
                                }
                                //运费模板、重量、体积和单位
                            },
                            {
                                xtype: 'menuitem',
                                icon: Resource.png('jet', 'keymapEditor'),
                                text: '商品品牌',
                                listeners: {
                                    click: 'onEditBandClick'
                                }
                            },
                            {
                                xtype: 'menuitem',
                                icon: Resource.png('jet', 'messageHistory'),
                                text: '设置有效期',
                                listeners: {
                                    click: 'onSetAgeClick'
                                }  //重量、体积和单位
                            }
                        ]
                    }
                },
                '->',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'search'),
                    text: '搜索',
                    listeners: {
                        click: 'onSearchClick'
                    }
                }
            ]
        }
    ],

    refreshModuleTree: function () {
        var wrapper = ApplicationLoader.getCurrentTreeModuleWrapper();
        if (wrapper) {
            wrapper.reload(5002);
        }
    },

    onProductUpClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.find('products').getSelect();
        if (!jsons) {
            Dialog.alert('请先选中一条商品信息');
            return false;
        }
        Dialog.batch({
            message: '确定上架商品{d}吗？',
            data: jsons,
            key: 'name',
            callback: function (btn) {
                if (btn == 'yes') {
                    var ids = Array.splitArray(jsons, 'id');
                    self.apis.Goods.setGoodsPutAway
                        .wait(self, '正在上架...')
                        .call({ids: ids}, function () {
                            self.find('products').refreshStore();
                        })
                }
            }
        });
    },

    onProductDownClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.find('products').getSelect();
        if (!jsons) {
            Dialog.alert('请先选中一条商品信息');
            return false;
        }
        Dialog.batch({
            message: '确定下架商品{d}吗？',
            data: jsons,
            key: 'name',
            callback: function (btn) {
                if (btn == 'yes') {
                    var ids = Array.splitArray(jsons, 'id');
                    self.apis.Goods.setGoodSoldOut
                        .wait(self, '正在下架...')
                        .call({ids: ids}, function () {
                            self.find('products').refreshStore();
                        })
                }
            }
        });
    },

    onListGoodsClick: function (button, e, eOpts) {
        this.find('products').refreshStore();
    },

    onViewItemClick: function (dataview, record, item, index, e, eOpts) {
        var store = this.find('products').getStore();
        store.reloadMember({gid: record.get('id')});
    },

    onAddGoodsClick: function (button, e, eOpts) {
        var self = this;
        var panel = this.parent.parent.addTab({
            config: {
                apis: self.apis
            },
            group: 'product',
            text: '添加新商品',
            tabIcon: 'image/micon/icon_79.png',
            module: 'App.goods.CreateGoodsPanel'
        }, true, true);

        panel.firstItem.setCloseCallback(function () {
            self.find('products').refreshStore();
        });
    },

    onBatchDeleteClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.find('products').getSelect();
        if (!jsons) {
            Dialog.alert('请先选中至少一条商品信息');
            return false;
        }
        Dialog.batch({
            message: '确定删除（回收站）商品{d}吗？',
            data: jsons,
            key: 'name',
            callback: function (btn) {
                if (btn == 'yes') {
                    var ids = Array.splitArray(jsons, 'id');
                    self.apis.Goods.deleteGoods
                        .wait(self, '正在删除商品...')
                        .call({ids: ids}, function () {
                            self.find('products').refreshStore();
                        })
                }
            }
        });
    },

    onCellDeleteProduct: function () {
        var self = this;
        var jsons = this.getIgnoreSelects(arguments);
        Dialog.batch({
            message: '确定删除（回收站）商品{d}吗？',
            data: jsons,
            key: 'name',
            callback: function (btn) {
                if (btn == 'yes') {
                    var ids = Array.splitArray(jsons, 'id');
                    self.apis.Goods.deleteGoods
                        .wait(self, '正在删除商品...')
                        .call({ids: ids}, function () {
                            self.find('products').refreshStore();
                        })
                }
            }
        });
    },

    onCellEditProduct: function () {
        var self = this;
        var json = this.find('products').getIgnoreSelect(arguments);
        if (json) {
            self.mask('正在加载商品数据...');
            self.apis.Goods.getSingleGoods
                .wait(self, '正在加载商品...')
                .call({id: json['id']}, function (goods) {
                    self.unmask();
                    var tab = self.topView.addTab({
                        id: "update_goods_" + json['id'],
                        config: {
                            apis: self.apis
                        },
                        module: 'App.goods.CreateGoodsPanel',
                        text: '修改商品信息 - ' + json['name'],
                        goodsId: goods['id']
                    }, true, true);
                    tab.firstItem.setValues(goods);
                    tab.firstItem.setCloseCallback(function () {
                        self.find('products').refreshStore();
                    });
                }, function () {
                    self.unmask();
                });
        } else {
            Dialog.alert('修改一个商品前必须选中一条');
        }
    },

    onSearchClick: function (button, e, options) {
        this.searchPanel.setSearchShow();
    },

    onAfterApply: function (m) {
        var module = m || this._module;
        if (m) this._module = m;
        var originalId = module.get('originalId');
        var store = this.apis.Goods.getGoods.createPageStore({gid: originalId});
        this.find('products').setStore(store);
        store.load();
    },

    onEditAmountClick: function () {
        var self = this;
        var json = this.find('products').getIgnoreSelect(arguments);
        if (!json) {
            Dialog.alert('修改销售信息必须选中一条商品');
            return false;
        }
        Dialog.openWindow('App.goods.GoodsEditAmountWindow', {
            callback: function (data, sku) {
                data['id'] = json['id'];
                self.apis.Goods.updateGoodsSales
                    .wait(self, '正在修改售卖信息...')
                    .call({object: data, sku: sku}, function () {
                        self.find('products').refreshStore();
                        Dialog.alert('修改售卖信息成功！');
                    })
            }
        }).setGoodsValue(json);
    },

    onEditTitleClick: function () {
        var self = this;
        var json = this.find('products').getIgnoreSelect(arguments);
        if (!json) {
            Dialog.alert('修改标题信息必须选中一条商品信息');
            return false;
        }
        Dialog.openWindow('App.goods.GoodsEditTitleWindow', {
            callback: function (data) {
                data['id'] = json['id'];
                self.apis.Goods.updateGoodsTitle
                    .wait(self, '正在修改标题...')
                    .call({object: data}, function () {
                        self.find('products').refreshStore();
                        Dialog.alert('修改标题信息成功！');
                    })
            }
        }).setGoodsValue(json);
    },

    onEditPostTplClick: function () {

    },

    onEditBandClick: function () {
        var self = this;
        var goods = this.find('products').getSelect();
        if (!goods) {
            Dialog.alert('修改商品品牌必须选中至少一条商品信息');
            return false;
        }

        var win = Dialog.openWindow('App.goods.SelectBrandWindow', {
            apis: self.apis,
            callback: function (jsons) {
                var dt = jsons[0];
                Dialog.batch({
                    message: '确定修改商品{d}品牌为 ' + Color.string(dt['name'], '#999999') + ' 吗？',
                    data: goods,
                    key: 'name',
                    callback: function (btn) {
                        if (btn == 'yes') {
                            var ids = Array.splitArray(goods, 'id');
                            self.apis.Goods.updateGoodsBrand
                                .wait(self, '正在修改品牌...')
                                .call({ids: ids, bid: dt['id']}, function () {
                                    self.find('products').refreshStore();
                                })
                        }
                    }
                });
            }
        });
        win.initSelect();
    },

    onSetAgeClick: function () {
        var self = this;
        var goods = this.find('products').getIgnoreSelect(arguments);

        if (!goods) {
            Dialog.alert('修改商品有效期必须选中一条商品信息');
            return false;
        }

        var win = Dialog.openFormWindow({
            title: '修改商品有效期',
            width: 433,
            height: 200,
            items: [
                {
                    xtype: 'datetimefield',
                    name: 'validTime',
                    anchor: '100%',
                    format: 'Y-m-d H:i:s',
                    fieldLabel: '有效期至'
                },
                {
                    xtype: 'button',
                    anchor: '100%',
                    margin: '0px auto 0px auto',
                    text: '取消清除有效期时间',
                    listeners: {
                        click: 'clearTime'
                    }
                }
            ],
            success: function (json, win) {
                var strtime = json['validTime'];
                var validTime;
                if (strtime != '') {
                    validTime = (new Date(json['validTime'])).getTime();
                } else {
                    validTime = 0;
                }
                var str = [];
                str.push('确定更新商品 ');
                str.push(Color.string(goods['name'], '#999999'));
                if (validTime == 0) {
                    str.push(' <br/>清空有效期时间');
                } else {
                    str.push(' <br/>有效期至 ' + Color.string(strtime, '#999999'))
                }
                Dialog.confirm('提示', str.join(""), function (btn) {
                    if (btn == 'yes') {
                        win.close();
                        self.apis.Goods.updateGoodsValidTime
                            .wait(self, '正在修改有效期...')
                            .call({ids: [goods['id']], time: validTime}, function () {
                                self.find('products').refreshStore();
                            })
                    }
                });
            },
            funs: {
                clearTime: function (button) {
                    var validTime = button.ownerCt.find('validTime');
                    validTime.setValue('')
                }
            }
        });
        if (goods['validTime'] && goods['validTime'] != 0) {
            win.setValues({validTime: new Date(goods['validTime'])});
        }
    },


    getSearchFormItems: function () {
        return [
            {
                xtype: 'textfield',
                name: 'name',
                fieldLabel: '商品名称'
            },
            {
                xtype: 'combobox',
                name: 'bid',
                displayField: 'name',
                valueField: 'id',
                fieldLabel: '品牌',
                queryMode: 'remote',
                queryParam: 'keyword',
                selectOnFocus: true,
                minChars: 1
            },
            {
                xtype: 'combobox',
                name: 'status',
                displayField: 'name',
                valueField: 'id',
                fieldLabel: '状态',
                store: {
                    data: [
                        {id: 20, name: '已下架'},
                        {id: 30, name: '已上架'},
                        {id: 50, name: '已过期'}
                    ]
                }
            },
            {
                xtype: 'radiogroup',
                fieldLabel: '规格类型',
                items: [
                    {
                        xtype: 'radiofield',
                        name: 'hasSku',
                        boxLabel: '单规格',
                        inputValue: '0'
                    },
                    {
                        xtype: 'radiofield',
                        name: 'hasSku',
                        boxLabel: '多规格',
                        inputValue: '1'
                    }
                ]
            },
            {
                xtype: 'numberfield',
                name: 'priceStart',
                anchor: '100%',
                fieldLabel: '价格(最小)',
                emptyText: '不填表示不限制最小价格'
            },
            {
                xtype: 'numberfield',
                name: 'priceEnd',
                anchor: '100%',
                fieldLabel: '价格(最大)',
                emptyText: '不填表示不限制最大价格'
            },
            {
                xtype: 'numberfield',
                name: 'skuStart',
                anchor: '100%',
                fieldLabel: '库存(最小)',
                emptyText: '不填表示不限制最小库存'
            },
            {
                xtype: 'numberfield',
                name: 'skuEnd',
                anchor: '100%',
                fieldLabel: '库存(最大)',
                emptyText: '不填表示不限制最大库存'
            },
            {
                xtype: 'datetimefield',
                name: 'createTimeStart',
                anchor: '100%',
                format: 'Y-m-d H:i:s',
                fieldLabel: '创建时间(开始)'
            },
            {
                xtype: 'datetimefield',
                name: 'createTimeEnd',
                anchor: '100%',
                format: 'Y-m-d H:i:s',
                fieldLabel: '创建时间(结束)'
            }
        ]
    },

    onSearchFormButtonClick: function (form) {
        var search = form.getForm().getValues();
        if (search) {
            var originalId = this._module.get('originalId');
            var store = this.apis.Goods.getGoods.createPageStore({gid: originalId, search: search});
            this.find('products').setStore(store);
            store.load();
        }
    },

    onInitSearchPanel: function (panel) {
        var bidView = panel.findForm('bid');
        var store = this.apis.Brand.getBrands.createPageStore();
        bidView.setStore(store);
    }

});
