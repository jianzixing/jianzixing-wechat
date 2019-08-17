Ext.define('App.wechat.WeChatImageTextManager', {
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

    resUrl: '/wxplugin/imagetext.jhtml',

    columns: [
        {
            xtype: 'gridcolumn',
            dataIndex: 'id',
            text: 'ID'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'coverUrl',
            text: '封面URL',
            renderer: function (v, mate, record) {
                v = record.get('TableWeChatImageTextSub')[0]['coverUrl'];
                return '<img style="width: 30px;height: 30px" src="' + v + '"/>';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'title',
            text: '标题',
            renderer: function (v, mate, record) {
                v = record.get('TableWeChatImageTextSub')[0]['title'];
                return v;
            }
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'desc',
            text: '描述',
            renderer: function (v, mate, record) {
                v = record.get('TableWeChatImageTextSub')[0]['desc'];
                return v;
            }
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'resUrl',
            text: '图文地址',
            renderer: function (v, mate, record) {
                v = record.get('TableWeChatImageTextSub')[0]['url'];
                return '<a style="text-decoration: none;color: #0f74a8" href="' + v + '" target="_blank">点击打开</a>';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'createTime',
            text: '创建时间',
            width: 150,
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
                    tooltip: '修改图文',
                    handler: 'onUpdateClick'
                },
                {
                    iconCls: 'x-fa fa-times',
                    tooltip: '删除图文',
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
                    text: '列出自定义图文',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addJdk'),
                    text: '添加自定义图文',
                    listeners: {
                        click: 'onAddClick'
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
        var self = this;
        var win = Dialog.openWindow('App.wechat.WeChatImageTextWindow', {
            maximizable: true,
            apis: self.apis,
            accountData: this.accountData,
            openType: this.openType,
            resUrl: this.resUrl,
            _callback: function () {
                self.refreshStore();
            }
        });
        win.maximize();
    },

    onUpdateClick: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var win = Dialog.openWindow('App.wechat.WeChatImageTextWindow', {
                maximizable: true,
                apis: self.apis,
                accountData: this.accountData,
                openType: this.openType,
                resUrl: this.resUrl,
                _callback: function () {
                    self.refreshStore();
                }
            });
            win.maximize();
            win.setValue(data);
        }
    },

    onDelClick: function () {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            Dialog.batch({
                message: '确定删除图文{d}吗？',
                data: datas,
                key: 'title',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(datas, "id");
                        self.apis.WeChatMaterial.delImageText
                            .wait(self, '正在删除图文...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            });
                    }
                }
            });

        }
    },

    onAfterApply: function () {
        var store = this.apis.WeChatMaterial.getImageTexts.createPageStore({
            accountId: this.accountData['id'],
            openType: this.openType
        });
        this.setStore(store);
        store.load();
    }

});
