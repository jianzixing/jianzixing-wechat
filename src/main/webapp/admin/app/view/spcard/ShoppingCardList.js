Ext.define('App.spcard.ShoppingCardList', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.RowNumberer',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.view.Table',
        'Ext.button.Button',
        'Ext.toolbar.Paging',
        'Ext.selection.CheckboxModel'
    ],

    header: false,
    defaultListenerScope: true,

    columns: [
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'TableShoppingCard',
            text: '批次名称',
            renderer: function (v) {
                if (v) return v['name'];
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'TableShoppingCard',
            text: '所属批次',
            renderer: function (v) {
                if (v) return v['number'];
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 180,
            dataIndex: 'cardNumber',
            text: '卡号'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'balance',
            text: '余额'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'status',
            text: '状态',
            renderer: function (v) {
                if (v == 0) return '<span style="color: #0f8783">未绑定</span>';
                if (v == 1) return '<span style="color: #dc331d">已作废</span>';
                if (v == 2) return '<span style="color: #aaaaaa">已使用</span>';
                if (v == 3) return '<span style="color: #61a4ff">已绑定</span>';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'bindTime',
            text: '绑卡时间',
            renderer: function (v) {
                if (v) return new Date(v).format();
                return '[未绑卡]';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'useTime',
            text: '使用时间',
            renderer: function (v) {
                if (v) return new Date(v).format();
                return '[未使用]';
            }
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
            width: 150,
            text: '操作',
            align: 'center',
            items: [
                {
                    iconCls: 'x-fa fa-clone',
                    tooltip: '查看消费记录',
                    handler: 'onLogClick'
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
                    icon: Resource.png('jet', 'back'),
                    text: '返回购物卡批次',
                    listeners: {
                        click: 'onBackClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'listChanges'),
                    text: '列出购物卡列表',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'stripNull'),
                    text: '批量作废购物卡',
                    listeners: {
                        click: 'onDeclareClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'download'),
                    text: '下载购物卡信息',
                    listeners: {
                        click: 'onDownloadClick'
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

    onBackClick: function (button, e, eOpts) {
        this.parent.back();
    },

    onListClick: function () {
        this.loadCards();
    },

    onDeclareClick: function () {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            Dialog.batch({
                message: '确定作废购物卡{d}吗？<br/>' +
                    '<span style="color: red">作废购物卡后，所有的购物卡都不再允许购买支付!</span>',
                data: datas,
                key: 'cardNumber',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var numbers = Array.splitArray(datas, "cardNumber");
                        self.apis.ShoppingCard.declareShoppingCardList
                            .wait(self, '正在作废购物卡...')
                            .call({id: self.scdata['id'], numbers: numbers}, function () {
                                self.refreshStore();
                            });
                    }
                }
            });

        } else {
            Dialog.alert('提示', '请先选中购物卡后再作废');
        }
    },

    onLogClick: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var win = Dialog.openWindow('App.spcard.ShoppingCardLog', {
                apis: self.apis,
                cardData: data
            });
            win.loadLogs();
        }
    },

    onDownloadClick: function () {
        var self = this;
        Dialog.prompt('确定下载当前批次购物卡吗？',
            '请在下方输入批次密码后点击OK按钮', function (btn, password) {
                if (btn == 'ok') {
                    // self.apis.ShoppingCard.exportShoppingCardList
                    //     .wait(self, '正在导出购物卡信息...')
                    //     .call({id: self.scdata['id'], password: password}, function () {
                    //
                    //     });

                    var info = self.apis.ShoppingCard.exportShoppingCardList.info();
                    window.open(info.url
                        + "?_page=" + info.data['_page']
                        + "&id=" + self.scdata['id']
                        + "&password=" + password);
                }
            });
    },

    loadCards: function () {
        var store = this.apis.ShoppingCard.getShoppingCardList.createPageStore({id: this.scdata['id']});
        this.setStore(store);
        store.load();
    }

});
