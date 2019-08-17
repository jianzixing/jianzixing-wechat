Ext.define('App.spcard.ShoppingCardManager', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.RowNumberer',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.view.Table',
        'Ext.button.Button',
        'Ext.toolbar.Paging'
    ],

    header: false,
    defaultListenerScope: true,
    apis: {
        ShoppingCard: {
            getShoppingCards: {},
            addShoppingCard: {},
            updateShoppingCard: {},
            declareShoppingCard: {},
            buildShoppingCard: {},
            getShoppingCardList: {},
            declareShoppingCardList: {},
            getShoppingCardSpending: {},
            exportShoppingCardList: {}
        }
    },

    columns: [
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'name',
            text: '购物卡名称'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'number',
            text: '批次号'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'money',
            text: '面额'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'count',
            text: '数量'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'status',
            text: '状态',
            renderer: function (v) {
                if (v == 0) return '<span style="color: #0f8783">未创建</span>';
                if (v == 1) return '<span style="color: #61a4ff">已创建</span>';
                if (v == 2) return '<span style="color: #dc331d">已作废</span>';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'finishTime',
            text: '有效日期',
            renderer: function (v) {
                return new Date(v).format();
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'detail',
            text: '描述'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'createTime',
            text: '创建日期',
            renderer: function (v) {
                return new Date(v).format();
                return '';
            }
        },
        {
            xtype: 'actioncolumn',
            dataIndex: 'status',
            width: 150,
            text: '操作',
            align: 'center',
            items: [
                {
                    iconCls: 'x-fa fa-newspaper-o',
                    tooltip: '查看购物卡',
                    handler: 'onCardsClick'
                },
                {
                    iconCls: 'x-fa fa-exchange',
                    tooltip: '生成购物卡',
                    handler: 'onBuildClick',
                    getClass: function (v) {
                        if (v != 0) return 'x-hidden';
                        else return 'x-fa fa-exchange';
                    }
                },
                {
                    iconCls: 'x-fa fa-pencil',
                    tooltip: '修改购物卡',
                    handler: 'onUpdateClick',
                    getClass: function (v) {
                        if (v != 0) return 'x-hidden';
                        else return 'x-fa fa-pencil';
                    }
                },
                {
                    iconCls: 'x-fa fa-times',
                    tooltip: '作废购物卡',
                    handler: 'onDelClick',
                    getClass: function (v) {
                        if (v == 2) return 'x-hidden';
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
                    text: '列出购物卡批次',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addLink'),
                    text: '添加购物卡批次',
                    listeners: {
                        click: 'onAddClick'
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
        var win = Dialog.openWindow('App.spcard.ShoppingCardWindow', {
            apis: this.apis,
            callback: function () {
                self.refreshStore();
            }
        });
    },

    onCardsClick: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            this.parent.forward('App.spcard.ShoppingCardList', {
                apis: this.apis,
                scdata: data,
                callback: function () {
                    self.refreshStore();
                }
            }).loadCards();
        }
    },

    onUpdateClick: function (button, e, eOpts) {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            if (data['status'] == 0) {
                var win = Dialog.openWindow('App.spcard.ShoppingCardWindow', {
                    apis: this.apis,
                    callback: function () {
                        self.refreshStore();
                    }
                });
                win.setValue(data);
            } else {
                Dialog.alert('购物卡批次状态在"未创建"时才可以修改');
            }
        } else {
            Dialog.alert('请先选中一条购物卡批次信息');
        }
    },

    onDelClick: function () {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            Dialog.batch({
                message: '确定作废购物卡批次{d}吗？<br/>' +
                    '<span style="color: red">作废购物卡后，所有的购物卡都不再允许购买支付!</span>',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(datas, "id");
                        self.apis.ShoppingCard.declareShoppingCard
                            .wait(self, '正在作废购物卡...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            });
                    }
                }
            });

        } else {
            Dialog.alert('提示', '请先选中购物卡批次后再作废');
        }
    },

    onBuildClick: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            Dialog.prompt('确定生成当前批次' + data['name'] + '购物卡？',
                '请在下方输入批次密码后点击OK按钮', function (btn, password) {
                    if (btn == 'ok') {
                        self.apis.ShoppingCard.buildShoppingCard
                            .wait(self, '正在生成购物卡...')
                            .call({id: data['id'], password: password}, function () {
                                self.refreshStore();
                            })
                    }
                });
        } else {
            Dialog.alert('提示', '请先选中购物卡批次后再生成');
        }
    },

    onAfterApply: function () {
        var store = this.apis.ShoppingCard.getShoppingCards.createPageStore();
        this.setStore(store);
        store.load();
    }

});