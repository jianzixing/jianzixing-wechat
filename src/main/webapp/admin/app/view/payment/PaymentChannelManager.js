Ext.define('App.payment.PaymentChannelManager', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.view.Table',
        'Ext.button.Button',
        'Ext.toolbar.Paging',
        'Ext.selection.CheckboxModel'
    ],

    border: false,
    header: false,
    defaultListenerScope: true,
    apis: {
        Payment: {
            addChannel: {},
            deleteChannel: {},
            updateChannel: {},
            getChannels: {},
            getModels: {}
        }
    },

    columns: [
        {
            xtype: 'gridcolumn',
            dataIndex: 'id',
            text: 'ID'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'name',
            text: '支付方式名称'
        },
        {
            xtype: 'gridcolumn',
            width: 100,
            dataIndex: 'enable',
            text: '是否启用',
            renderer: function (v) {
                if (v == 1) {
                    return '启用';
                }
                return '禁用';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 300,
            dataIndex: 'detail',
            text: '支付方式描述'
        },
        {
            xtype: 'actioncolumn',
            text: '操作',
            dataIndex: 'id',
            align: 'center',
            items: [
                {
                    tooltip: '修改',
                    iconCls: "x-fa fa-pencil green",
                    handler: 'onUpdateClick'
                },
                {
                    iconCls: "x-fa fa-times red",
                    tooltip: '删除',
                    handler: 'onDeleteClick'
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
                    text: '列出支付方式',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addFavoritesList'),
                    text: '添加支付方式',
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '批量删除支付方式',
                    listeners: {
                        click: 'onDeleteClick'
                    }
                }
            ]
        },
        {
            xtype: 'pagingtoolbar',
            name: 'paging',
            dock: 'bottom',
            width: 360,
            displayInfo: true
        }
    ],
    selModel: {
        selType: 'checkboxmodel'
    },

    onListClick: function (button, e, eOpts) {
        this.refreshStore();
    },

    onAddClick: function (button, e, eOpts) {
        var self = this;
        Dialog.openWindow('App.payment.PaymentChannelWindow', {
            apis: self.apis,
            _callback: function () {
                self.refreshStore();
            }
        }).initWindow();
    },

    onDeleteClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.getIgnoreSelects(arguments);
        if (jsons != null) {
            Dialog.batch({
                message: '确定删除支付方式{d}吗？',
                data: jsons,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, "id");
                        self.apis.Payment.deleteChannel
                            .wait(self, '正在删除支付方式...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            })
                    }
                }
            });
        } else {
            Dialog.alert('提示', '请先选中至少一条支付方式后再删除!');
        }
    },

    onUpdateClick: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var win = Dialog.openWindow('App.payment.PaymentChannelWindow', {
                apis: self.apis,
                _callback: function () {
                    self.refreshStore();
                }
            });
            win.initWindow();
            win.setValue(data);
        } else {
            Dialog.alert('请先选中一条支付方式后再修改!');
        }
    },

    onAfterApply: function () {
        var store = this.apis.Payment.getChannels.createPageStore();
        this.setStore(store);
        this.find('paging').bindStore(store);
        store.load();
    }

});