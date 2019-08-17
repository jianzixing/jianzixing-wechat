Ext.define('App.wechat.WeChatMassManager', {
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

    columns: [
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'name',
            text: '群发名称'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'type',
            text: '群发类型',
            renderer: function (v) {
                if (v == 1) return '图文消息';
                if (v == 2) return '文字';
                if (v == 3) return '图片';
                if (v == 4) return '语音';
                if (v == 5) return '视频';
                return "";
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'triggerTime',
            text: '触发时间',
            renderer: function (v) {
                if (v) return (new Date(v)).format();
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'status',
            text: '执行状态',
            renderer: function (v, mate, record) {
                if (v == 0) return '<span style="color: darkred">未执行</span>';
                if (v == 1) return '<span style="color: green">已执行</span>';
                if (v == 2) return '<span style="color: darkred">执行失败:' + record.get('error') + '</span>';
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'enable',
            text: '是否启用',
            renderer: function (v) {
                if (v == 0) return '<span style="color: darkred">未启用</span>';
                if (v == 1) return '<span style="color: green">已启用</span>';
                return '';
            }
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
                    tooltip: '修改群发',
                    handler: 'onUpdateClick'
                },
                {
                    iconCls: 'x-fa fa-times',
                    tooltip: '删除群发',
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
                    text: '列出定时群发',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addRemoteDatasource'),
                    text: '添加定时群发',
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'idle'),
                    text: '批量启用群发',
                    listeners: {
                        click: 'onEnableClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'clean_dark'),
                    text: '批量禁用群发',
                    listeners: {
                        click: 'onDisableClick'
                    }
                },
                '|',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '批量删除群发',
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
        var win = Dialog.openWindow('App.wechat.WeChatMassWindow', {
            apis: this.apis,
            openType: this.openType,
            accountData: this.accountData,
            _callback: function () {
                self.refreshStore();
            }
        });
        win.setInit({openType: this.openType, accountId: this.accountData['id']});
    },

    onUpdateClick: function (button, e, eOpts) {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var win = Dialog.openWindow('App.wechat.WeChatMassWindow', {
                apis: this.apis,
                openType: this.openType,
                accountData: this.accountData,
                _callback: function () {
                    self.refreshStore();
                }
            });
            win.setInit({openType: this.openType, accountId: this.accountData['id']});
            win.setValue(data);
        } else {
            Dialog.alert('提示', '请先选中定时群发后再修改');
        }
    },

    onDelClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            Dialog.batch({
                message: '确定删除定时群发{d}吗？',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(datas, "id");
                        self.apis.WeChatMass.delMasses
                            .wait(self, '正在删除定时群发...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            });
                    }
                }
            });

        } else {
            Dialog.alert('提示', '请先选中定时群发后再删除');
        }
    },

    onEnableClick: function () {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            Dialog.batch({
                message: '确定启用定时群发{d}吗？',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(datas, "id");
                        self.apis.WeChatMass.enableMasses
                            .wait(self, '正在启用定时群发...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            });
                    }
                }
            });

        } else {
            Dialog.alert('提示', '请先选中定时群发后再启用');
        }
    },

    onDisableClick: function () {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            Dialog.batch({
                message: '确定禁用定时群发{d}吗？',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(datas, "id");
                        self.apis.WeChatMass.disableMasses
                            .wait(self, '正在禁用定时群发...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            });
                    }
                }
            });

        } else {
            Dialog.alert('提示', '请先选中定时群发后再禁用');
        }
    },

    onAfterApply: function () {
        var store = this.apis.WeChatMass.getMesses.createPageStore({
            openType: this.openType,
            accountId: this.accountData['id']
        });
        this.setStore(store);
        store.load();
    }

});
