Ext.define('App.wechat.WeChatQRCodeManager', {
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

    columns: [
        {
            xtype: 'gridcolumn',
            dataIndex: 'name',
            text: '二维码名称',
            width: 150
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'scanCount',
            text: '扫描次数'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'focusCount',
            text: '关注次数'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'userCount',
            text: '关注人数'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'keepCount',
            text: '留存粉丝'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'sceneId',
            text: '场景值',
            width: 200
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'actionName',
            text: '类型',
            renderer: function (v) {
                if (v === 'QR_SCENE') return '临时整数';
                if (v === 'QR_STR_SCENE') return '临时字符串';
                if (v === 'QR_LIMIT_SCENE') return '永久整数';
                if (v === 'QR_LIMIT_STR_SCENE') return '永久字符串';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'expireSeconds',
            text: '有效期',
            width: 100,
            renderer: function (v) {
                if (v && v > 0) {
                    return v + "秒";
                }
                return '永久';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'createTime',
            text: '创建时间',
            width: 150,
            renderer: function (v) {
                if (v) {
                    return (new Date(v)).format();
                }
                return v;
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
                    tooltip: '查看修改二维码',
                    handler: 'onUpdateQRCode'
                },
                {
                    iconCls: 'x-fa fa-times',
                    tooltip: '删除二维码',
                    handler: 'onBatchDelClick'
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
                    icon: Resource.png('jet', 'list'),
                    text: '列出二维码',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addYouTrack_dark'),
                    text: '添加二维码',
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret_dark'),
                    text: '批量删除二维码',
                    listeners: {
                        click: 'onBatchDelClick'
                    }
                },
                '|',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'list'),
                    text: '查看二维码',
                    listeners: {
                        click: 'onUpdateQRCode'
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
        this.refreshStore();
    },

    onAddClick: function (button, e, eOpts) {
        var self = this;
        var win = Dialog.openWindow('App.wechat.WeChatQRCodeWindow', {
            apis: self.apis,
            openType: this.openType,
            accountData: this.accountData,
            _callback: function () {
                self.refreshStore();
            }
        });
    },

    onBatchDelClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            Dialog.batch({
                message: '确定删除二维码{d}吗？',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(datas, "id");
                        self.apis.WeChatQRCode.deleteQRCodes
                            .wait(self, '正在删除二维码...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            });
                    }
                }
            });

        } else {
            Dialog.alert('提示', '请先选中二维码后再删除');
        }
    },

    onUpdateQRCode: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var win = Dialog.openWindow('App.wechat.WeChatQRCodeWindow', {
                apis: self.apis,
                openType: this.openType,
                accountData: this.accountData,
                _callback: function () {
                    self.refreshStore();
                }
            });
            win.setValue(data)
        } else {
            Dialog.alert('请选择一个二维码后再查看修改');
        }
    },

    onAfterApply: function () {
        // var actionName = 'QR_STR_SCENE';
        // var actionName = 'QR_LIMIT_STR_SCENE';
        var store = this.apis.WeChatQRCode.getQRCodes.createPageStore({
            openType: this.openType,
            accountId: this.accountData['id']
        });
        this.setStore(store);
        this.find('paging').bindStore(store);
        store.load();
    }

});
