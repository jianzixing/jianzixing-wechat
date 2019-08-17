Ext.define('App.discount.DiscountManger', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'Ext.button.Button',
        'Ext.toolbar.Paging',
        'Ext.selection.CheckboxModel'
    ],

    border: false,
    header: false,
    defaultListenerScope: true,

    apis: {
        Discount: {
            addDiscount: {},
            deleteDiscount: {},
            getDiscounts: {},
            updateDiscount: {},
            getDiscountInit: {},
            disableDiscount: {},
            enableDiscount: {},
            getDiscount: {},
            getDiscountGoods: {},
            getSimpleDiscountGoods: {},
            addDiscountGoods: {},
            removeDiscountGoods: {}
        },
        Goods: {
            getGoods: {}
        },
        Brand: {
            getBrands: {}
        }
    },

    search: true,
    columns: [
        {
            xtype: 'gridcolumn',
            dataIndex: 'name',
            text: '活动名称'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'type',
            text: '活动类型',
            renderer: function (v) {
                if (v == 0) return '分类';
                if (v == 1) return '商品';
                if (v == 1) return '品牌';
                return '其它';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'implName',
            text: '活动实现'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'enable',
            text: '是否启用',
            renderer: function (v) {
                if (v == 3) return '<span style="color:red">已过期</span>';
                if (v == 1) return '<span style="color:#305a7d">启用</span>';
                if (v == 0) return '禁用';
                return '其它';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'isDel',
            text: '是否删除',
            renderer: function (v) {
                if (v == 1) return '<span style="color: red">已删除</span>';
                if (v == 0) return '未删除';
                return '其它';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'startTime',
            text: '开始时间',
            renderer: function (v, mate, record) {
                var forever = record.get('forever');
                if (forever == 1) {
                    return '永久有效';
                }
                if (v) {
                    return new Date(v).format();
                }
                return '不限';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'finishTime',
            text: '结束时间',
            renderer: function (v, mate, record) {
                var forever = record.get('forever');
                if (forever == 1) {
                    return '永久有效';
                }
                if (v) {
                    return new Date(v).format();
                }
                return '不限';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'detail',
            text: '活动描述'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'createTime',
            text: '创建时间',
            renderer: function (v) {
                if (v) return new Date(v).format();
                return '';
            }
        },
        {
            xtype: 'actioncolumn',
            dataIndex: 'isDel',
            text: '操作',
            align: 'center',
            items: [
                {
                    iconCls: 'x-fa fa-cubes',
                    tooltip: '查看促销商品',
                    handler: 'onGoodsClick',
                    getClass: function (v) {
                        if (v == 1) return 'x-hidden';
                        else return 'x-fa fa-cubes';
                    }
                },
                {
                    iconCls: 'x-fa fa-eye',
                    tooltip: '查看活动',
                    handler: 'onUpdateClick',
                    getClass: function (v) {
                        if (v == 1) return 'x-hidden';
                        else return 'x-fa fa-eye';
                    }
                },
                {
                    iconCls: 'x-fa fa-times',
                    tooltip: '删除活动',
                    handler: 'onDelClick',
                    getClass: function (v) {
                        if (v == 1) return 'x-hidden';
                        else return 'x-fa fa-times';
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
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'listChanges'),
                    text: '列出活动',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addLink'),
                    text: '添加活动',
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'toolWindowRun'),
                    text: '启用活动',
                    listeners: {
                        click: 'onEnableClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'recording_stop'),
                    text: '禁用活动',
                    listeners: {
                        click: 'onDisableClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret_dark'),
                    text: '批量删除活动',
                    listeners: {
                        click: 'onDelClick'
                    }
                },
                '->',
                {
                    xtype: 'button',
                    text: '搜索',
                    icon: Resource.png('jet', 'search'),
                    listeners: {
                        click: 'onSearchClick'
                    }
                }
            ]
        },
        {
            xtype: 'pagingtoolbar',
            dock: 'bottom',
            width: 360,
            displayInfo: true
        }
    ],
    selModel: {
        selType: 'checkboxmodel'
    },

    onListClick: function (button, e, eOpts) {
        this.onAfterApply();
    },

    onAddClick: function (button, e, eOpts) {
        var self = this;
        var tab = self.parent.forward('App.discount.DiscountPanel', {
            apis: this.apis,
            _callback: function () {
                self.refreshStore();
            }
        });
        tab.initWindow();
    },

    onUpdateClick: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var tab = self.parent.forward('App.discount.DiscountDetailPanel', {
                apis: this.apis,
                _callback: function () {
                    self.refreshStore();
                }
            });
            tab.setValue(data);
        } else {
            Dialog.alert('请先选中一条活动信息');
        }
    },

    onEnableClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            Dialog.batch({
                message: '确定启用优惠活动{d}吗？',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(datas, "id");
                        self.apis.Discount.enableDiscount
                            .wait(self, '正在启用优惠活动...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            });
                    }
                }
            });

        } else {
            Dialog.alert('提示', '请先选中优惠活动后再启用');
        }
    },

    onDisableClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            Dialog.batch({
                message: '确定禁用优惠活动{d}吗？',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(datas, "id");
                        self.apis.Discount.disableDiscount
                            .wait(self, '正在禁用优惠活动...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            });
                    }
                }
            });

        } else {
            Dialog.alert('提示', '请先选中优惠活动后再禁用');
        }
    },

    onDelClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            Dialog.batch({
                message: '确定删除优惠活动{d}吗？',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(datas, "id");
                        self.apis.Discount.deleteDiscount
                            .wait(self, '正在删除优惠活动...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            });
                    }
                }
            });
        } else {
            Dialog.alert('提示', '请先选中优惠活动后再删除');
        }
    },

    onGoodsClick: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var tab = self.parent.forward('App.discount.DiscountGoodsPanel', {
                _discount: data,
                apis: this.apis
            });
            tab.loadGoods(data);
        } else {
            Dialog.alert('提示', '请先选中促销活动后再查看商品列表');
        }
    },

    onAfterApply: function () {
        var store = this.apis.Discount.getDiscounts.createPageStore();
        this.setStore(store);
        store.load();
    },

    onSearchClick: function (button, e, options) {
        this.searchPanel.setSearchShow();
    },

    getSearchFormItems: function () {
        return [
            {
                xtype: 'textfield',
                name: 'name',
                fieldLabel: '活动名称'
            },
            {
                xtype: 'combobox',
                name: 'type',
                fieldLabel: '活动类型',
                displayField: 'name',
                valueField: 'id',
                editable: false,
                store: {
                    data: [
                        {id: 0, name: '商品分类'},
                        {id: 1, name: '商品'},
                        {id: 2, name: '品牌'}
                    ]
                }
            },
            {
                xtype: 'textfield',
                name: 'detail',
                fieldLabel: '活动描述'
            },
            {
                xtype: 'datetimefield',
                name: 'sTimeStart',
                anchor: '100%',
                format: 'Y-m-d H:i:s',
                fieldLabel: '开始时间',
                emptyText: '如果填写匹配大于开始时间的活动'
            },
            {
                xtype: 'datetimefield',
                name: 'eTimeEnd',
                anchor: '100%',
                format: 'Y-m-d H:i:s',
                fieldLabel: '结束时间',
                emptyText: '如果填写匹配小于结束时间的活动'
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
        var data = form.getForm().getValues();
        var store = this.apis.Discount.getDiscounts.createPageStore({search: data});
        this.setStore(store);
        store.load();
    }

});
