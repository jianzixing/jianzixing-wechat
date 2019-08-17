Ext.define('App.wechat.WeChatAccountReplyManager', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'Ext.toolbar.Paging',
        'Ext.button.Button',
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
            text: '规则名称'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'type',
            text: '回复条件',
            renderer: function (v) {
                if (v == 1) return "全匹配关键字";
                if (v == 2) return "半匹配关键字";
                if (v == 5) return "关注公众号";
                if (v == 6) return "取消关注";
                if (v == 7) return "扫描带参数二维码";
                if (v == 8) return "自定义菜单事件";
                return "";
            }
        },
        {
            xtype: 'gridcolumn',
            width: 200,
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
                return "";
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
                    tooltip: '修改回复',
                    handler: 'onUpdateClick'
                },
                {
                    iconCls: 'x-fa fa-times',
                    tooltip: '删除回复',
                    handler: 'onDeleteClick'
                }
            ]
        }
    ],
    dockedItems: [
        {
            xtype: 'pagingtoolbar',
            dock: 'bottom',
            width: 360,
            displayInfo: true
        },
        {
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'listChanges'),
                    text: '列出回复',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addRemoteDatasource'),
                    text: '添加回复',
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '批量删除回复',
                    listeners: {
                        click: 'onDeleteClick'
                    }
                }
            ]
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
        var win = Dialog.openWindow('App.wechat.WeChatAccountReplyWindow', {
            apis: self.apis,
            accountData: this.accountData,
            openType: this.openType,
            _callback: function () {
                self.refreshStore();
            }
        });
        win.setInit();
    },

    onDeleteClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            Dialog.batch({
                message: '确定删除自动回复{d}吗？',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(datas, "id");
                        self.apis.WeChatReply.delReply
                            .wait(self, '正在删除自动回复...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            });
                    }
                }
            });

        } else {
            Dialog.alert('提示', '请先选中自动回复后再删除');
        }
    },

    onUpdateClick: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var win = Dialog.openWindow('App.wechat.WeChatAccountReplyWindow', {
                apis: self.apis,
                accountData: this.accountData,
                openType: this.openType,
                _callback: function () {
                    self.refreshStore();
                }
            });
            win.setInit();
            win.setValue(data);
        } else {
            Dialog.alert('提示', '请先选中自动回复后再修改');
        }
    },

    onAfterApply: function () {
        var store = this.apis.WeChatReply.getReplys.createPageStore({
            accountId: this.accountData['id'],
            openType: this.openType
        });
        this.setStore(store);
        store.load();
    }

});
