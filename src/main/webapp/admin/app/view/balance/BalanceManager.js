Ext.define('App.balance.BalanceManager', {
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
        Balance: {
            getBalances: {},
            getRecordsByUid: {}
        }
    },

    search: true,
    columns: [
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'userName',
            text: '用户名'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'nick',
            text: '昵称',
            renderer: function (v) {
                if (v) return decodeURIComponent(v);
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'TableBalance',
            text: '余额',
            renderer: function (v) {
                if (v) return v['balance'];
                return 0;
            }
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'openid',
            text: 'openid'
        },
        {
            xtype: 'actioncolumn',
            text: '操作',
            dataIndex: 'id',
            align: 'center',
            items: [
                {
                    iconCls: "x-fa fa-database green",
                    tooltip: '查看余额日志',
                    handler: 'onSeeRecordClick'
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
                    text: '列出余额',
                    listeners: {
                        click: 'onListClick'
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

    onSeeRecordClick: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var store = this.apis.Balance.getRecordsByUid.createPageStore({uid: data['id']});
            var win = Dialog.openWindow('App.balance.BalanceRecordWindow', {});
            win.setDatas(store);
        } else {
            Dialog.alert('请先选中至少一条余额信息');
        }
    },

    onAfterApply: function () {
        var store = this.apis.Balance.getBalances.createPageStore();
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
                name: 'userName',
                fieldLabel: '用户名'
            },
            {
                xtype: 'textfield',
                name: 'openid',
                fieldLabel: 'openid'
            },
            {
                xtype: 'textfield',
                name: 'balanceStart',
                fieldLabel: '余额(开始)'
            },
            {
                xtype: 'textfield',
                name: 'balanceEnd',
                fieldLabel: '余额(结束)'
            }
        ]
    },

    onSearchFormButtonClick: function (form) {
        var data = form.getForm().getValues();
        var store = this.apis.Balance.getBalances.createPageStore({search: data});
        this.setStore(store);
        store.load();
    }

});
