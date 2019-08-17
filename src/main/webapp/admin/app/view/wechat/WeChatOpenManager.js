Ext.define('App.wechat.WeChatOpenManager', {
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
    apis: {
        WeChatPublic: {
            getAccountTrees: {}
        },
        WeChatOpen: {
            addOpen: {},
            deleteOpens: {},
            updateOpen: {},
            getOpens: {},
            enableOpens: {},
            disableOpens: {},
            getOpenAccounts: {},
            getOpenUrls: {},
            getOpenDetail: {},
            getDefaultMiniProgramAccount: {},
            setDefaultMiniProgramAccount: {},
            getDefaultPublicAccount: {},
            setDefaultPublicAccount: {}
        },
        WeChatMaterial: {
            getTemporaryMaterials: {},
            getForeverMaterials: {},
            getRemoteMaterials: {},
            deleteForeverMaterials: {},
            addImageText: {},
            getImageTexts: {},
            delImageText: {},
            updateImageText: {},
            uploadImageWeChat: {},
            uploadMaterial: {}
        },
        WeChatReply: {
            getReplys: {},
            addReply: {},
            delReply: {},
            updateReply: {}
        },
        WeChatMass: {
            getWeChatLabels: {},
            addMass: {},
            delMasses: {},
            updateMass: {},
            getMesses: {},
            enableMasses: {},
            disableMasses: {}
        },
        WeChatQRCode: {
            addQRCode: {},
            updateQRCode: {},
            getQRCodes: {},
            deleteQRCodes: {}
        },
        WeChatUser: {
            getUsers: {},
            syncWeChatUsers: {},
            setUserRemark: {},
            getLabels: {},
            delLabel: {},
            updateLabel: {},
            createLabel: {},
            setUserLabel: {},
            cancelUserLabel: {}
        }
    },

    columns: [
        {
            xtype: 'gridcolumn',
            width: 120,
            align: 'center',
            dataIndex: 'logo',
            text: 'LOGO',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (!value || value == "") {
                    value = Resource.create('/admin/image/exicon/nopic_80.gif');
                } else {
                    value = Resource.image(value);
                }
                var width = 80;
                var height = 80;
                return '<div style="height: ' + height + 'px;width: ' + width + 'px;vertical-align: middle;display:table-cell;background-color: #BBBBBB">' +
                    '<img style="max-height: ' + height + 'px;max-width: ' + width + 'px;vertical-align: middle" src=' + value + '></div> ';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'name',
            text: '名称'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'code',
            text: '标识码'
        },
        {
            xtype: 'gridcolumn',
            width: 180,
            dataIndex: 'name',
            text: '基本信息',
            renderer: function (v, mate, record) {
                var appId = record.get('appId');
                var appSecret = record.get('appSecret');
                var token = record.get('appToken');
                var appKey = record.get('appKey');

                var html = [];
                if (appId) html.push('<div style="color: #666;overflow: hidden;text-overflow:ellipsis;white-space: nowrap;"><span style="width: 80px;display: block;float: left">AppId：</span>' + appId + '</div>');
                if (appSecret) html.push('<div style="color: #666;overflow: hidden;text-overflow:ellipsis;white-space: nowrap;"><span style="width: 80px;display: block;float: left">AppSecret：</span>' + appSecret + '</div>');
                if (token) html.push('<div style="color: #666;overflow: hidden;text-overflow:ellipsis;white-space: nowrap;"><span style="width: 80px;display: block;float: left">Token：</span>' + token + '</div>');
                if (appKey) html.push('<div style="color: #666;overflow: hidden;text-overflow:ellipsis;white-space: nowrap;"><span style="width: 80px;display: block;float: left">AppKey：</span>' + appKey + '</div>');
                return html.join('');
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
            dataIndex: 'checked',
            text: '是否连接',
            renderer: function (v) {
                if (v == 0) {
                    return '<span style="color: red">未连接</span>'
                }
                return '<span style="color: #1686b9">已连接</span>'
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'detail',
            text: '描述'
        },
        {
            xtype: 'actioncolumn',
            width: 150,
            dataIndex: 'id',
            text: '操作',
            align: 'center',
            items: [
                {
                    iconCls: 'x-fa fa-file-text',
                    tooltip: '查看详情',
                    handler: 'onDetailClick'
                },
                {
                    iconCls: 'x-fa fa-wechat',
                    tooltip: '授权公众号',
                    handler: 'onAccreditClick'
                },
                {
                    iconCls: 'x-fa fa-pencil',
                    tooltip: '修改第三方平台',
                    handler: 'onUpdateClick'
                },
                {
                    iconCls: 'x-fa fa-times',
                    tooltip: '删除第三方平台',
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
                    text: '列出第三方平台',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addLink'),
                    text: '添加第三方平台',
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'webSettings_dark'),
                    text: '查看授权公众号',
                    listeners: {
                        click: 'onAccreditClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'webToolWindow'),
                    text: '打开授权地址',
                    listeners: {
                        click: 'onAuthUrlClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'toolWindowRun'),
                    text: '启用第三方平台',
                    listeners: {
                        click: 'onEnableClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'recording_stop'),
                    text: '禁用第三方平台',
                    listeners: {
                        click: 'onDisableClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret_dark'),
                    text: '批量删除第三方平台',
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
        var tab = this.parent.forward('App.wechat.WeChatOpenPanel', {
            apis: this.apis,
            _callback: function () {
                self.refreshStore();
            }
        });
    },

    onDetailClick: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var tab = this.parent.forward('App.wechat.WeChatOpenDetailPanel', {
                apis: this.apis
            });
            tab.setValue(data);
        } else {
            Dialog.alert('请先选中一条第三方平台数据后再查看');
        }
    },

    onUpdateClick: function (button, e, o) {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var tab = this.parent.forward('App.wechat.WeChatOpenPanel', {
                apis: this.apis,
                _callback: function () {
                    self.refreshStore();
                }
            });
            tab.setValue(data);
        } else {
            Dialog.alert('请先选中一条第三方平台数据后再修改');
        }
    },

    onAccreditClick: function () {
        var self = this;
        var data = this.getIgnoreSelects(arguments);
        if (data && data.length == 1) {
            var tab = this.parent.forward('App.wechat.WeChatOpenAccreditManager', {
                apis: this.apis,
                openData: data[0],
                _callback: function () {
                    self.refreshStore();
                }
            });
            tab.onAfterApply();
        } else {
            Dialog.alert('只能选中一条第三方平台后再查看授权公众号');
        }
    },

    onAuthUrlClick: function () {
        var self = this;
        var data = this.getIgnoreSelects(arguments);
        if (data && data.length == 1) {
            var win = Dialog.openWindow('App.wechat.WeChatOpenAuthWindow', {
                apis: self.apis
            });
            win.setValue(data[0]);
        } else {
            Dialog.alert('只能选中一条第三方平台后再查看授权地址');
        }
    },

    onEnableClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            Dialog.batch({
                message: '确定启用第三方平台{d}吗？',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(datas, "id");
                        self.apis.WeChatOpen.enableOpens
                            .wait(self, '正在启用第三方平台...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            });
                    }
                }
            });

        } else {
            Dialog.alert('提示', '请先选中一条第三方平台后再启用');
        }
    },

    onDisableClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            Dialog.batch({
                message: '确定禁用第三方平台{d}吗？',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(datas, "id");
                        self.apis.WeChatOpen.disableOpens
                            .wait(self, '正在禁用第三方平台...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            });
                    }
                }
            });

        } else {
            Dialog.alert('提示', '请先选中一条第三方平台后再禁用');
        }
    },

    onDelClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            Dialog.batch({
                message: '确定删除第三方平台{d}吗？',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(datas, "id");
                        self.apis.WeChatOpen.deleteOpens
                            .wait(self, '正在删除第三方平台...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            });
                    }
                }
            });

        } else {
            Dialog.alert('提示', '请先选中一条第三方平台后再删除');
        }
    },

    onAfterApply: function () {
        var store = this.apis.WeChatOpen.getOpens.createPageStore();
        this.setStore(store);
        store.load();
    }

});
