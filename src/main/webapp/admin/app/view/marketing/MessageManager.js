Ext.define('App.marketing.MessageManager', {
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

    header: false,
    defaultListenerScope: true,
    apis: {
        Message: {
            getMessages: {},
            delMessage: {}
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
            dataIndex: 'fromUser',
            text: '发送人',
            renderer: function (v) {
                if (v) {
                    return v['userName'] + " & " + decodeURIComponent(v['nick']);
                }
                return '[系统]';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'targetUser',
            text: '接收人',
            renderer: function (v) {
                if (v) {
                    return v['userName'] + " & " + decodeURIComponent(v['nick']);
                }
                return '[系统]';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 300,
            dataIndex: 'title',
            text: '标题'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'createTime',
            text: '创建时间',
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
                    text: '列出站内信',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    hidden: true,
                    icon: Resource.png('jet', 'addJira'),
                    text: '添加站内信',
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '批量删除站内信',
                    listeners: {
                        click: 'onDelClick'
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

    },

    onDelClick: function (button, e, eOpts) {

    },

    onAfterApply: function () {
        var store = this.apis.Message.getMessages.createPageStore();
        this.setStore(store);
        store.load();
    }

});
