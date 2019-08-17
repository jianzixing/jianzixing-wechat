Ext.define('App.wechat.WeChatUserManager', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.view.Table',
        'Ext.button.Button',
        'Ext.toolbar.Paging'
    ],

    border: false,
    header: false,
    defaultListenerScope: true,

    columns: [
        {
            xtype: 'gridcolumn',
            hidden: true,
            width: 80,
            dataIndex: 'id',
            text: 'ID'
        },
        {
            xtype: 'gridcolumn',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (!value || value == "") {
                    value = Resource.create('/admin/image/exicon/nopic_40.gif');
                } else {
                    value = Resource.image(value);
                }
                var width = 40;
                var height = 40;
                return '<div style="height: ' + height + 'px;width: ' + width + 'px;vertical-align: middle;display:table-cell;">' +
                    '<img style="max-height: ' + height + 'px;max-width: ' + width + 'px;vertical-align: middle" src=' + value + '></div> ';
            },
            width: 60,
            align: 'center',
            dataIndex: 'headimgurl',
            text: '头像'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'TableUser',
            text: '用户名',
            renderer: function (v) {
                if (v) {
                    return v['userName'];
                }
                return '[无关联用户]';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'nickname',
            text: '微信昵称',
            renderer: function (v) {
                if (v) {
                    return decodeURIComponent(v);
                }
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'openid',
            text: 'OPENID'
        },
        {
            xtype: 'gridcolumn',
            hidden: true,
            width: 150,
            dataIndex: 'unionid',
            text: 'UNIONID'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'sex',
            text: '性别',
            renderer: function (v) {
                if (v == 0) return '未知';
                if (v == 1) return '男';
                if (v == 2) return '女';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'remark',
            text: '备注'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'TableWeChatUserTag',
            text: '标签',
            width: 150,
            renderer: function (v) {
                if (v && Ext.isArray(v)) {
                    var str = [];
                    for (var i = 0; i < v.length; i++) {
                        str.push(v[i]['tagName']);
                    }
                    return str.join(" , ");
                }
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'country',
            text: '用户地址',
            renderer: function (value, metaData, record) {
                return (record.get('country') || '') + '-'
                    + (record.get('province') || '') + '-'
                    + (record.get('city') || '');
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'createTime',
            text: '创建时间',
            renderer: function (v) {
                if (v) {
                    return new Date(v).format();
                }
                return '';
            }
        }
    ],
    selModel: {
        selType: 'checkboxmodel'
    },
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'listChanges'),
                    text: '列出粉丝',
                    listeners: {
                        click: 'onButtonClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'editItemInSection'),
                    text: '设置备注',
                    listeners: {
                        click: 'onSetRemarkClick'
                    }
                },
                '|',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'refresh'),
                    text: '同步粉丝',
                    listeners: {
                        click: 'onSyncClick'
                    }
                },
                '|',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'nodeSelectionMode'),
                    text: '标签管理',
                    listeners: {
                        click: 'onLabelClick'
                    }
                },
                '|',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'add_dark'),
                    text: '设置标签',
                    listeners: {
                        click: 'onSetUserLabelClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'closeActive_dark'),
                    text: '取消标签',
                    listeners: {
                        click: 'onCancelUserLabelClick'
                    }
                },
                '->',
                {
                    xtype: 'image',
                    height: 20,
                    width: 20,
                    src: 'image/icon/dp.png'
                },
                {
                    xtype: 'container',
                    html: '<span style="color: #999999">同步粉丝请勿经常使用</span>'
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

    onButtonClick: function (button, e, eOpts) {
        this.refreshStore();
    },

    onSetRemarkClick: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var win = Ext.create('Ext.window.Window', {
                requires: [
                    'Ext.form.Panel',
                    'Ext.form.field.TextArea',
                    'Ext.toolbar.Toolbar',
                    'Ext.button.Button'
                ],
                constrainHeader: true,
                autoShow: true,
                modal: true,
                height: 250,
                width: 400,
                layout: 'fit',
                title: '修改备注',
                defaultListenerScope: true,

                items: [
                    {
                        xtype: 'form',
                        name: 'form',
                        border: false,
                        bodyPadding: 10,
                        header: false,
                        items: [
                            {
                                xtype: 'displayfield',
                                name: 'nickname',
                                anchor: '100%',
                                fieldLabel: '用户昵称',
                                value: ''
                            },
                            {
                                xtype: 'textareafield',
                                name: 'remark',
                                anchor: '100%',
                                height: 90,
                                fieldLabel: '备注'
                            }
                        ]
                    }
                ],
                dockedItems: [
                    {
                        xtype: 'toolbar',
                        dock: 'bottom',
                        items: [
                            '->',
                            {
                                xtype: 'button',
                                icon: Resource.png('jet', 'saveTempConfig'),
                                text: '确定保存',
                                listeners: {
                                    click: 'onSaveClick'
                                }
                            },
                            {
                                xtype: 'button',
                                icon: Resource.png('jet', 'cancel'),
                                text: '取消关闭',
                                listeners: {
                                    click: 'onCancelClick'
                                }
                            },
                            '->'
                        ]
                    }
                ],

                onSaveClick: function (button, e, eOpts) {
                    var remark = this.find('remark').getValue();
                    var win = this;
                    self.apis.WeChatUser.setUserRemark
                        .wait(win, '正在修改用户备注...')
                        .call({uid: data['id'], remark: remark}, function () {
                            win.close();
                            self.refreshStore();
                        })
                },

                onCancelClick: function (button, e, eOpts) {
                    this.close();
                }

            });
            win.find('nickname').setValue(decodeURIComponent(data['nickname']));
            win.find('remark').setValue(data['remark']);
        } else {
            Dialog.alert('请先选中一条微信用户后再修改备注');
        }
    },

    onSyncClick: function () {
        var self = this;
        Dialog.confirm('确定同步微信用户', '确定开始同步微信用户吗（<span style="color: red">过程可能会很慢</span>）？', function (btn) {
            if (btn == 'yes') {
                self.apis.WeChatUser.syncWeChatUsers
                    .wait(self, '正在同步微信用户...')
                    .call({
                        openType: self.openType,
                        accountId: self.accountData['id']
                    }, function () {
                        self.refreshStore();
                    });
            }
        })
    },


    onLabelClick: function () {
        var self = this;
        var win = Dialog.openWindow('App.wechat.WeChatUserLabelWindow', {
            apis: this.apis,
            openType: self.openType,
            accountData: self.accountData
        });
        win.setInit();
    },

    labelMenuView: Ext.create('Ext.menu.Menu'),
    onSetUserLabelClick: function (button) {
        var self = this;
        this.labelMenuView.removeAll();
        this.apis.WeChatUser.getLabels
            .wait(button, "...")
            .call({
                openType: this.openType,
                accountId: this.accountData['id']
            }, function (data) {
                if (data) {
                    for (var i = 0; i < data.length; i++) {
                        self.labelMenuView.add({
                            text: data[i]['name'],
                            tagid: data[i]['id'],
                            handler: function () {
                                self.onUserLabelCallback(this.tagid, this.text, true);
                            }
                        });
                        self.labelMenuView.showBy(button)
                    }
                }
            });
    },

    onCancelUserLabelClick: function (button) {
        var self = this;
        this.labelMenuView.removeAll();
        this.apis.WeChatUser.getLabels
            .wait(button, "...")
            .call({
                openType: this.openType,
                accountId: this.accountData['id']
            }, function (data) {
                if (data) {
                    for (var i = 0; i < data.length; i++) {
                        self.labelMenuView.add({
                            text: data[i]['name'],
                            tagid: data[i]['id'],
                            handler: function () {
                                self.onUserLabelCallback(this.tagid, this.text, false);
                            }
                        });
                        self.labelMenuView.showBy(button)
                    }
                }
            });
    },

    onUserLabelCallback: function (tagid, text, isSet) {
        var self = this;
        var openType = this.openType;
        var accountId = this.accountData['id'];
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            var names = [];
            for (var i = 0; i < datas.length; i++) {
                names.push({name: decodeURIComponent(datas[i]['nickname'])});
            }
            Dialog.batch({
                message: isSet ? '确定给粉丝{d}设置标签' + Color.string(text) + '吗？'
                    : '确定取消粉丝{d}的标签' + Color.string(text) + '吗？',
                data: names,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(datas, "id");
                        if (isSet) {
                            self.apis.WeChatUser.setUserLabel
                                .wait(self, '正在设置标签...')
                                .call({
                                    openType: openType,
                                    accountId: accountId,
                                    tagid: tagid,
                                    uids: ids,
                                    text: text
                                }, function () {
                                    self.refreshStore();
                                });
                        } else {
                            self.apis.WeChatUser.cancelUserLabel
                                .wait(self, '正在取消标签...')
                                .call({
                                    openType: openType,
                                    accountId: accountId,
                                    tagid: tagid,
                                    uids: ids
                                }, function () {
                                    self.refreshStore();
                                });
                        }
                    }
                }
            });

        } else {
            Dialog.alert('提示', '请先选中粉丝后再设置标签');
        }
    },

    onAfterApply: function () {
        var store = this.apis.WeChatUser.getUsers.createPageStore({
            openType: this.openType,
            accountId: this.accountData['id']
        });
        this.setStore(store);
        store.load();
    }

});