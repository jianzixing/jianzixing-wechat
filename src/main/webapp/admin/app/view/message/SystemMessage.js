Ext.define('App.message.SystemMessage', {
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
        SystemMessage: {
            sendMessage: {},
            sendAllMessage: {},
            delMessage: {},
            getMessages: {},
            markRead: {},
            markAllRead: {}
        },
        Admin: {
            getAdmins: {}
        },
        SystemDict: {
            getDictsByTable: {}
        }
    },
    search: true,
    columns: [
        {
            xtype: 'gridcolumn',
            width: 90,
            dataIndex: 'id',
            text: 'ID'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'TableAdmin',
            text: '发送人',
            renderer: function (v) {
                if (v) {
                    return v['realName'] || v['userName'];
                }
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 300,
            dataIndex: 'title',
            text: '标题内容'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'TableSystemDict',
            text: '消息类型',
            renderer: function (v) {
                if (v != null) {
                    return v['name']
                }
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'isRead',
            text: '是否已读',
            renderer: function (v) {
                if (v == 1) {
                    return '<span style="color: #0f74a8">已读</span>'
                }
                return '<span style="color: red">未读</span>';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'toAdminId',
            text: '发件记录',
            renderer: function (v, mate, record) {
                var toAdminId = record.get('toAdminId');
                var fromAdminId = record.get('fromAdminId');
                if (toAdminId == fromAdminId) {
                    return '<span style="color: #9cb945">发送者</span>';
                }
                return '<span style="color: #0f74a8">接收者</span>';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'createTime',
            text: '发送时间',
            renderer: function (value) {
                if (value) {
                    var d = new Date(value);
                    return d.format();
                }
                return '';
            }
        },
        {
            xtype: 'actioncolumn',
            width: 90,
            text: '操作',
            items: [
                {
                    iconCls: 'x-fa fa-eye',
                    tooltip: '查看消息详情',
                    handler: 'onLookMessageClick'
                },
                {
                    iconCls: 'x-fa fa-times',
                    tooltip: '删除消息',
                    handler: 'onBatchClick'
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
                    text: '列出消息',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'sendToTheRight'),
                    text: '发送消息',
                    listeners: {
                        click: 'onSendClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'read'),
                    text: '选中标记已读',
                    listeners: {
                        click: 'onSelectReadClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'allRevisions'),
                    text: '全部标记已读',
                    listeners: {
                        click: 'onAllReadClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '批量删除消息',
                    listeners: {
                        click: 'onBatchClick'
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
        this.onAfterApply();
    },

    onLookMessageClick: function () {
        var data = this.getIgnoreSelect(arguments);
        var panel = this.parent.forward('App.message.MessageTabDetail', {});
        panel.setValue(data['id']);
    },

    onSendClick: function (button, e, eOpts) {
        var self = this;
        var win = Dialog.openWindow('App.message.MessageAdmin', {
            apis: self.apis,
            _sendSelects: function (ids) {
                self.refreshStore();
            },
            _sendAll: function () {
                self.refreshStore();
            }
        });

        win.onLoadAdmins();
    },

    onSelectReadClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.getIgnoreSelects(arguments);
        if (jsons != null) {
            Dialog.batch({
                message: '确定标记消息{d}为已读吗？',
                data: jsons,
                key: 'id',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, "id");
                        self.apis.SystemMessage.markRead
                            .wait(self, '正在标记已读...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            })
                    }
                }
            });
        } else {
            Dialog.alert('请先勾选要标记为已读的消息!');
        }
    },

    onAllReadClick: function (button, e, eOpts) {
        var self = this;
        Dialog.batch({
            message: '确定将所有未读消息置为已读吗？',
            data: [],
            key: 'id',
            callback: function (btn) {
                if (btn == Global.YES) {
                    self.apis.SystemMessage.markAllRead
                        .wait(self, '正在标记已读...')
                        .call({}, function () {
                            self.refreshStore();
                        })
                }
            }
        });
    },

    onBatchClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.getIgnoreSelects(arguments);
        if (jsons != null) {
            Dialog.batch({
                message: '确定删除消息{d}吗？',
                data: jsons,
                key: 'id',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, "id");
                        self.apis.SystemMessage.delMessage
                            .wait(self, '正在删除消息...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            })
                    }
                }
            });
        } else {
            Dialog.alert('请先勾选要删除的消息!');
        }
    },

    onAfterApply: function () {
        var store = this.apis.SystemMessage.getMessages.createPageStore();
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
                name: 'fromAdminUserName',
                fieldLabel: '发件人用户名'
            },
            {
                xtype: 'textfield',
                name: 'toAdminUserName',
                fieldLabel: '收件人用户名'
            },
            {
                xtype: 'textfield',
                name: 'title',
                fieldLabel: '标题'
            },
            {
                xtype: 'textfield',
                name: 'content',
                fieldLabel: '内容'
            },
            {
                xtype: 'combobox',
                name: 'isRead',
                fieldLabel: '是否已读',
                displayField: 'name',
                valueField: 'id',
                editable: false,
                store: {
                    data: [
                        {id: 0, name: '未读'},
                        {id: 1, name: '已读'}
                    ]
                }
            },
            {
                xtype: 'datetimefield',
                name: 'createTimeStart',
                anchor: '100%',
                format: 'Y-m-d H:i:s',
                fieldLabel: '发送时间(开始)'
            },
            {
                xtype: 'datetimefield',
                name: 'createTimeEnd',
                anchor: '100%',
                format: 'Y-m-d H:i:s',
                fieldLabel: '发送时间(结束)'
            }
        ]
    },

    onSearchFormButtonClick: function (form) {
        var data = form.getForm().getValues();
        var store = this.apis.SystemMessage.getMessages.createPageStore({search: data});
        this.setStore(store);
        store.load();
    }
});