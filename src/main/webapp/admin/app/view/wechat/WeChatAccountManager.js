Ext.define('App.wechat.WeChatAccountManager', {
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
            addAccount: {},
            deleteAccount: {},
            updateAccount: {},
            getAccounts: {},
            getAccountTrees: {},
            enableAccounts: {},
            disableAccounts: {},
            getDefaultAccount: {},
            setDefaultAccount: {}
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
                return '<div style="height: ' + height + 'px;width: ' + width + 'px;vertical-align: middle;display:table-cell">' +
                    '<img style="max-height: ' + height + 'px;max-width: ' + width + 'px;vertical-align: middle" src=' + value + '></div> ';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'name',
            text: '名称/类型',
            renderer: function (v, mate, record) {
                var type = record.get('type');
                var typeName = '其它类型';
                if (type == 1) typeName = '普通订阅号';
                else if (type == 2) typeName = '普通服务号';
                else if (type == 3) typeName = '认证订阅号';
                else if (type == 4) typeName = '认证服务号/认证媒体/政府订阅号';
                var html = [];
                html.push('<div style="color: #252424;min-height: 45px">' + v + '</div>');
                html.push('<div style="color: #98999a"> 类型：' + typeName + '</div>');
                return html.join('');
            }
        },
        {
            xtype: 'gridcolumn',
            width: 460,
            dataIndex: 'weAccount',
            text: '基本信息',
            renderer: function (v, mate, record) {
                var weAccount = record.get('weAccount');
                var appId = record.get('appId');
                var appSecret = record.get('appSecret');
                var token = record.get('token');
                var code = record.get('code');
                var authUrl = record.get('authUrl');

                var html = [];
                if (code) {
                    html.push('<div style="color: #666;overflow: hidden;text-overflow:ellipsis;white-space: nowrap;">');
                    html.push('<span style="width: 80px;display: block;float: left">标识码: </span>');
                    html.push(code);
                    html.push('</div>');
                }
                if (weAccount) {
                    html.push('<div style="color: #666;overflow: hidden;text-overflow:ellipsis;white-space: nowrap;">' +
                        '<span style="width: 80px;display: block;float: left">账号：</span>' + weAccount + '</div>');
                }
                if (appId) {
                    html.push('<div style="color: #666;overflow: hidden;text-overflow:ellipsis;white-space: nowrap;">' +
                        '<span style="width: 80px;display: block;float: left">AppID：</span>' + appId + '</div>');
                }
                if (authUrl) {
                    html.push('<div style="color: #666;overflow: hidden;text-overflow:ellipsis;white-space: nowrap;">');
                    html.push('<span style="width: 80px;display: block;float: left">配置地址:</span>');
                    html.push(authUrl);
                    html.push('</div>');
                }
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
            hidden: true,
            dataIndex: 'createTime',
            text: '创建时间',
            renderer: function (v) {
                if (v) {
                    return (new Date(v)).format();
                }
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
                    iconCls: 'x-fa fa-cog',
                    tooltip: '管理公众号',
                    handler: 'onManagerClick'
                },
                {
                    iconCls: 'x-fa fa-pencil',
                    tooltip: '修改公众号',
                    handler: 'onUpdateClick'
                },
                {
                    iconCls: 'x-fa fa-times',
                    tooltip: '删除公众号',
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
                    text: '列出公众号',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addLink'),
                    text: '添加公众号',
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
                    icon: Resource.png('jet', 'toolWindowRun'),
                    text: '启用公众号',
                    listeners: {
                        click: 'onEnableClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'recording_stop'),
                    text: '禁用公众号',
                    listeners: {
                        click: 'onDisableClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret_dark'),
                    text: '批量删除公众号',
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

    onManagerClick: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);

        this.parent.forward('App.wechat.WeChatAccountChildPanel', {
            apis: self.apis,
            accountData: data,
            openType: 1,
            _callback: function () {
                self.refreshStore();
            }
        });
    },

    onAddClick: function (button, e, eOpts) {
        var self = this;

        this.parent.forward('App.wechat.WeChatAccountPanel', {
            apis: self.apis,
            _callback: function () {
                self.refreshStore();
            }
        });
    },

    onDefaultClick: function () {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas && datas.length == 1) {
            self.apis.WeChatPublic.getDefaultAccount
                .wait(self, '正在获取默认账号...')
                .call({}, function (data) {
                    var msg = '';
                    if (data) {
                        msg = '已经存在默认账号<span style="color: #0f8783">'
                            + (data['name'] ? '[' + data['name'] + '] ' : '') + data['appId']
                            + '</span>,确定重置公众号{d}为系统默认公众号吗？';
                    } else {
                        msg = '确定设置公众号{d}为系统默认公众号吗？';
                    }
                    Dialog.batch({
                        message: msg,
                        data: datas,
                        key: 'name',
                        callback: function (btn) {
                            if (btn == Global.YES) {
                                var id = datas[0]['id'];
                                self.apis.WeChatPublic.setDefaultAccount
                                    .wait(self, '正在设置默认公众号...')
                                    .call({accountId: id}, function () {
                                        self.refreshStore();
                                    });
                            }
                        }
                    });

                });
        } else {
            Dialog.alert('提示', '只能选中一个公众号信息后再设置为默认账号');
        }
    },

    onDelClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            Dialog.batch({
                message: '确定删除公众号{d}吗？',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(datas, "id");
                        self.apis.WeChatPublic.deleteAccount
                            .wait(self, '正在删除公众号...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            });
                    }
                }
            });

        } else {
            Dialog.alert('提示', '请先选中公众号后再删除');
        }
    },

    onUpdateClick: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var tab = this.parent.forward('App.wechat.WeChatAccountPanel', {
                apis: self.apis,
                _callback: function () {
                    self.refreshStore();
                }
            });
            tab.setValue(data);
        } else {
            Dialog.alert('请先选中至少一条公众号信息');
        }
    },

    onEnableClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            Dialog.batch({
                message: '确定启用公众号{d}吗？',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(datas, "id");
                        self.apis.WeChatPublic.enableAccounts
                            .wait(self, '正在启用公众号...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            });
                    }
                }
            });

        } else {
            Dialog.alert('提示', '请先选中一条公众号后再启用');
        }
    },

    onDisableClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            Dialog.batch({
                message: '确定禁用公众号{d}吗？',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(datas, "id");
                        self.apis.WeChatPublic.disableAccounts
                            .wait(self, '正在禁用公众号...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            });
                    }
                }
            });

        } else {
            Dialog.alert('提示', '请先选中一条公众号后再禁用');
        }
    },

    onAfterApply: function () {
        var store = this.apis.WeChatPublic.getAccounts.createPageStore();
        this.setStore(store);
        store.load();
    }

});
