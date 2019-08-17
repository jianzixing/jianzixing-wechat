Ext.define('App.wechat.WeChatOpenAccreditManager', {
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
            width: 120,
            dataIndex: 'headImg',
            text: 'LOGO',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (!value || value == "") {
                    value = Resource.create('/admin/image/exicon/nopic_80.gif');
                } else {
                    if (value.indexOf('/admin/resources') == 0) {

                    } else {
                        value = Resource.image(value);
                    }
                }
                var width = 80;
                var height = 80;
                return '<div style="height: ' + height + 'px;width: ' + width + 'px;vertical-align: middle;display:table-cell;background-color: #BBBBBB">' +
                    '<img style="max-height: ' + height + 'px;max-width: ' + width + 'px;vertical-align: middle" src=' + value + '></div> ';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 120,
            dataIndex: 'qrCodeUrl',
            text: 'QRCode',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (!value || value == "") {
                    value = Resource.create('/admin/image/exicon/nopic_80.gif');
                } else {
                    if (value.indexOf('/admin/resources') == 0) {

                    } else {
                        value = Resource.image(value);
                    }
                }
                var width = 80;
                var height = 80;
                return '<div style="height: ' + height + 'px;width: ' + width + 'px;vertical-align: middle;display:table-cell;background-color: #BBBBBB">' +
                    '<img style="max-height: ' + height + 'px;max-width: ' + width + 'px;vertical-align: middle" src=' + value + '></div> ';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'nickName',
            text: '昵称'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'serviceTypeInfo',
            text: '类型',
            renderer: function (value, mate, record) {
                var verifyTypeInfo = record.get('verifyTypeInfo');
                var r = "";
                if (verifyTypeInfo == -1) r = "未认证"; else r = "已认证";
                if (value == 0 || value == 1) r += "订阅号";
                if (value == 2) r += "服务号";
                return r;
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'userName',
            text: '原始ID'
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
            width: 150,
            dataIndex: 'createTime',
            text: '创建时间',
            renderer: function (value, mate, record) {
                if (value) return (new Date(value)).format();
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
                    iconCls: 'x-fa fa-times',
                    hidden: true,
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
                    icon: Resource.png('jet', 'rollback'),
                    text: '返回第三方平台列表',
                    listeners: {
                        click: 'onBackClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'listChanges'),
                    text: '列出授权公众号',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'configurableDefault'),
                    text: '设置默认账号',
                    listeners: {
                        click: 'onDefaultClick'
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

    onBackClick: function (button, e, eOpts) {
        this.parent.back();
    },


    onDefaultClick: function () {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas && datas.length == 1) {
            var data = datas[0],
                isMiniProgram = data['isMiniProgram'],
                openType = isMiniProgram == 1 ? 4 : 2,
                myGetApi = openType == 2 ?
                    self.apis.WeChatOpen.getDefaultPublicAccount :
                    self.apis.WeChatOpen.getDefaultMiniProgramAccount,
                mySetApi = openType == 2 ?
                    self.apis.WeChatOpen.setDefaultPublicAccount :
                    self.apis.WeChatOpen.setDefaultMiniProgramAccount;

            myGetApi.wait(self, '正在获取默认账号...')
                .call({}, function (data) {
                    var msg = '';
                    if (data) {
                        msg = '已经存在默认账号<span style="color: #0f8783">'
                            + data['appId']
                            + '</span>,确定重置授权账号{d}为系统默认授权账号吗？';
                    } else {
                        msg = '确定设置授权账号{d}为系统默认授权账号吗？';
                    }
                    Dialog.batch({
                        message: msg,
                        data: datas,
                        key: 'name',
                        callback: function (btn) {
                            if (btn == Global.YES) {
                                var id = datas[0]['id'];
                                mySetApi.wait(self, '正在设置默认授权账号...')
                                    .call({accountId: id}, function () {
                                        self.refreshStore();
                                    });
                            }
                        }
                    });

                });
        } else {
            Dialog.alert('提示', '只能选中一个授权账号信息后再设置为默认账号');
        }
    },


    onManagerClick: function () {
        var self = this,
            data = this.getIgnoreSelect(arguments),
            isMiniProgram = data['isMiniProgram'],
            openType = isMiniProgram == 1 ? 4 : 2;

        if (openType == 2) {
            this.parent.forward('App.wechat.WeChatAccountChildPanel', {
                apis: self.apis,
                openData: this.openData,
                accountData: data,
                openType: 2,
                _callback: function () {
                    self.refreshStore();
                }
            });
        } else {
            Dialog.alert('暂时不支持小程序管理');
        }
    },

    onAfterApply: function () {
        var store = this.apis.WeChatOpen.getOpenAccounts.createPageStore({opid: this.openData['id']});
        this.setStore(store);
        store.load();
    }

});