Ext.define('App.wechat.WeChatAppMangager', {
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
        WeChatApp: {
            addApp: {},
            delApps: {},
            updateApp: {},
            getApps: {},
            getDefaultAccount: {},
            setDefaultAccount: {}
        }
    },

    columns: [
        {
            xtype: 'gridcolumn',
            dataIndex: 'name',
            text: '名称'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'code',
            text: '唯一标识码'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'appId',
            text: 'AppID'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'appSecret',
            text: 'AppSecret'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'isDefault',
            text: '是否默认账号',
            renderer: function (v) {
                if (v == 1) {
                    return '<span style="color: #0f74a8">默认账号</span>';
                }
                return '<span style="color: #aaaaaa">非默认账号</span>';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'enable',
            text: '是否启用',
            renderer: function (v) {
                if (v == 1) {
                    return '<span style="color: #0f74a8">已启用</span>';
                }
                return '<span style="color: #a70c00">已禁用</span>';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'detail',
            text: '描述'
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
        },
        {
            xtype: 'actioncolumn',
            dataIndex: 'id',
            text: '操作',
            align: 'center',
            items: [
                {
                    iconCls: 'x-fa fa-pencil',
                    tooltip: '修改APP',
                    handler: 'onUpdateClick'
                },
                {
                    iconCls: 'x-fa fa-times',
                    tooltip: '删除APP',
                    handler: 'onDelClick'
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
                    text: '列出移动应用',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addJdk'),
                    text: '添加移动应用',
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'configurableDefault'),
                    text: '设置默认账号',
                    listeners: {
                        click: 'onDefaultClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '批量删除移动应用',
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
        this.refreshStore();
    },

    onAddClick: function (button, e, eOpts) {
        var self = this;
        var win = Dialog.openWindow('App.wechat.WeChatAppWindow', {
            apis: this.apis,
            _callback: function () {
                self.refreshStore();
            }
        });
    },

    onDefaultClick: function () {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas && datas.length == 1) {
            self.apis.WeChatApp.getDefaultAccount
                .wait(self, '正在获取默认账号...')
                .call({}, function (data) {
                    var msg = '';
                    if (data) {
                        msg = '已经存在默认账号<span style="color: #0f8783">'
                            + (data['name'] ? '[' + data['name'] + '] ' : '') + data['appId']
                            + '</span>,确定重置移动应用{d}为系统默认移动应用吗？';
                    } else {
                        msg = '确定设置移动应用{d}为系统默认移动应用吗？';
                    }
                    Dialog.batch({
                        message: msg,
                        data: datas,
                        key: 'name',
                        callback: function (btn) {
                            if (btn == Global.YES) {
                                var id = datas[0]['id'];
                                self.apis.WeChatApp.setDefaultAccount
                                    .wait(self, '正在设置默认移动应用...')
                                    .call({accountId: id}, function () {
                                        self.refreshStore();
                                    });
                            }
                        }
                    });

                });
        } else {
            Dialog.alert('提示', '只能选中一个移动应用信息后再设置为默认账号');
        }
    },

    onUpdateClick: function (button, e, eOpts) {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var win = Dialog.openWindow('App.wechat.WeChatAppWindow', {
                apis: this.apis,
                _callback: function () {
                    self.refreshStore();
                }
            });
            win.setValue(data);
        } else {
            Dialog.alert('提示', '请先选中一个移动应用后再修改');
        }
    },

    onDelClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            Dialog.batch({
                message: '确定删除移动应用{d}吗？',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(datas, "id");
                        self.apis.WeChatApp.delApps
                            .wait(self, '正在删除移动应用...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            });
                    }
                }
            });

        } else {
            Dialog.alert('提示', '请先选中移动应用后再删除');
        }
    },

    onAfterApply: function () {
        var store = this.apis.WeChatApp.getApps.createPageStore();
        this.setStore(store);
        store.load();
    }

});