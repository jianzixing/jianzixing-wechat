Ext.define('App.balance.BalanceRecordManager', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.view.Table',
        'Ext.button.Button',
        'Ext.toolbar.Paging'
    ],

    border: false,
    header: false,
    defaultListenerScope: true,

    apis: {
        Balance: {
            getRecords: {}
        }
    },

    search: true,
    columns: [
        {
            xtype: 'gridcolumn',
            dataIndex: 'id',
            text: 'ID'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'TableUser',
            text: '用户名',
            renderer: function (v) {
                if (v) return v['userName'];
                return '';
            }
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
            dataIndex: 'changeBalance',
            text: '余额变更',
            renderer: function (v) {
                if (v >= 0) {
                    return '增加 ' + v;
                } else {
                    return '扣减' + Math.abs(v);
                }
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'beforeBalance',
            text: '变更前数量'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'afterBalance',
            text: '变更后数量'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'detail',
            text: '变更说明'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'createTime',
            text: '变更时间',
            renderer: function (v) {
                if (v) {
                    return new Date(v).format();
                }
                return '';
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
                    icon: Resource.png('jet', 'listChanges'),
                    text: '列出余额记录',
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

    onListClick: function (button, e, eOpts) {
        this.onAfterApply();
    },

    onAfterApply: function () {
        var store = this.apis.Balance.getRecords.createPageStore();
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
            }
        ]
    },

    onSearchFormButtonClick: function (form) {
        var data = form.getForm().getValues();
        var store = this.apis.Balance.getRecords.createPageStore({search: data});
        this.setStore(store);
        store.load();
    }

});