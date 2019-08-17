Ext.define('App.log.RequestAddressManager', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'Ext.button.Button',
        'Ext.toolbar.Paging'
    ],

    border: false,
    defaultListenerScope: true,
    apis: {
        Log: {
            getRequestAddressLog: {}
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
            dataIndex: 'ip',
            text: 'IP'
        },
        {
            xtype: 'gridcolumn',
            width: 280,
            dataIndex: 'sessionid',
            text: 'SESSION ID'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'uri',
            text: '访问页面'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'query',
            text: 'GET参数'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'createTime',
            text: '访问时间',
            renderer: function (value) {
                if (value) {
                    var d = new Date(value);
                    return d.format();
                }
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
                    text: '列出访问日志',
                    listeners: {
                        click: 'onListClick'
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
        },
        {
            xtype: 'pagingtoolbar',
            name: 'paging',
            dock: 'bottom',
            width: 360,
            displayInfo: true
        }
    ],

    onListClick: function (button, e, eOpts) {
        this.refreshStore();
    },

    onSearchClick: function (button, e, eOpts) {

    },

    onAfterApply: function () {
        var store = this.apis.Log.getRequestAddressLog.createPageStore();
        this.setStore(store);
        this.find('paging').bindStore(store);
        store.load();
    }

});