Ext.define('App.statistics.StatisticsViewManager', {
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
        Statistics: {
            getStatistics: {}
        }
    },
    search: true,
    columns: [
        {
            xtype: 'gridcolumn',
            dataIndex: 'dayTime',
            text: '日期'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'uri',
            text: '页面地址',
            renderer: function (v) {
                if (v == '*') {
                    return "全站";
                }
                return v;
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'type',
            text: '统计类型',
            renderer: function (v) {
                if (v) return v;
                return "全站";
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'pv',
            text: 'PV'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'uv',
            text: 'UV'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'iv',
            text: 'IV'
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
                    text: '列出统计',
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
        this.onAfterApply();
    },

    onAfterApply: function () {
        var store = this.apis.Statistics.getStatistics.createPageStore();
        this.setStore(store);
        this.find('paging').bindStore(store);
        store.load();
    },

    onSearchClick: function (button, e, options) {
        this.searchPanel.setSearchShow();
    },

    getSearchFormItems: function () {
        return [
            {
                xtype: 'textfield',
                name: 'time',
                fieldLabel: '日期'
            }
        ]
    },

    onSearchFormButtonClick: function (form) {
        var data = form.getForm().getValues();
        var store = this.apis.Statistics.getStatistics.createPageStore({search: data});
        this.setStore(store);
        store.load();
    }

});