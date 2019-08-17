Ext.define('App.wxplugin.WXSignRecordManager', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.selection.CheckboxModel'
    ],

    border: false,
    header: false,
    defaultListenerScope: true,

    apis: {
        WxpluginSign: {
            getRecords: {},
            getLogs: {}
        }
    },

    columns: [
        {
            xtype: 'gridcolumn',
            dataIndex: 'TableWxpluginSignGroup',
            text: '所属组',
            renderer: function (v) {
                if (v) return v['name'];
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'TableUser',
            text: '签到用户',
            renderer: function (v) {
                if (v) return v['userName'];
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'TableUser',
            text: 'openid',
            renderer: function (v) {
                if (v) return v['openid'];
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'count',
            text: '签到总次数'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'cntCount',
            text: '连续签到次数'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'cntCount',
            text: '连续签到次数'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'lastTime',
            text: '最后签到时间'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'createTime',
            text: '第一次签到时间',
            renderer: function (v) {
                if (v) return (new Date(v)).format();
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
                    icon: Resource.png('jet', 'list'),
                    text: '列出签到记录',
                    listeners: {
                        click: 'onListClick'
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

    groupFormWindow: [
        {
            xtype: 'gridpanel',
            name: 'grid',
            header: false,
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'id',
                    text: 'ID'
                },
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'TableWxpluginSignGroup',
                    text: '所属组',
                    renderer: function (v) {
                        if (v) return v['name'];
                        return '[空]';
                    }
                },
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'TableUser',
                    text: '用户',
                    renderer: function (v) {
                        if (v) return v['userName'];
                        return '[空]';
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'count',
                    text: '总投票次数'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'cntCount',
                    text: '连续投票次数'
                },
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'createTime',
                    text: '投票时间',
                    renderer: function (v) {
                        if (v) return (new Date(v)).format();
                        return '[空]';
                    }
                }
            ],
            dockedItems: [
                {
                    xtype: 'pagingtoolbar',
                    dock: 'bottom',
                    width: 360,
                    displayInfo: true
                }
            ]
        }
    ],

    onListClick: function (button, e, eOpts) {
        this.onAfterApply();
    },

    onAfterApply: function () {
        var store = this.apis.WxpluginSign.getRecords.createPageStore();
        this.setStore(store);
        store.load();
    }

});