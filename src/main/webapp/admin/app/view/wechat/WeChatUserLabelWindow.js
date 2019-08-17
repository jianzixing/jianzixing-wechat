Ext.define('App.wechat.WeChatUserLabelWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.grid.Panel',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'Ext.selection.CheckboxModel',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    autoShow: true,
    height: 500,
    width: 800,
    layout: 'fit',
    title: '标签管理',
    defaultListenerScope: true,

    items: [
        {
            xtype: 'gridpanel',
            name: 'grid',
            border: false,
            header: false,
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'id',
                    text: 'ID'
                },
                {
                    xtype: 'gridcolumn',
                    width: 200,
                    dataIndex: 'name',
                    text: '名称'
                },
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'count',
                    text: '人数'
                },
                {
                    xtype: 'actioncolumn',
                    dataIndex: 'id',
                    text: '操作',
                    align: 'center',
                    items: [
                        {
                            iconCls: 'x-fa fa-pencil',
                            tooltip: '修改标签',
                            handler: 'onUpdateClick'
                        },
                        {
                            iconCls: 'x-fa fa-times',
                            tooltip: '删除标签',
                            handler: 'onDelClick'
                        }
                    ]
                }
            ],
            selModel: {
                selType: 'checkboxmodel'
            }
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
                    text: '列出标签',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addRemoteDatasource'),
                    text: '添加标签',
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret_dark'),
                    text: '批量删除标签',
                    listeners: {
                        click: 'onDelClick'
                    }
                }
            ]
        }
    ],

    onListClick: function (button, e, eOpts) {
        this.find('grid').refreshStore();
    },

    onAddClick: function (button, e, eOpts) {
        var self = this;
        var win = Ext.create('Ext.window.Window', {
            requires: [
                'Ext.form.Panel',
                'Ext.toolbar.Toolbar',
                'Ext.button.Button'
            ],
            constrainHeader: true,
            autoShow: true,
            modal: true,
            height: 250,
            width: 400,
            layout: 'fit',
            title: '添加标签',
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
                            fieldLabel: '公众号',
                            value: ''
                        },
                        {
                            xtype: 'textfield',
                            name: 'tagName',
                            anchor: '100%',
                            fieldLabel: '标签名称'
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
                var tagName = this.find('tagName').getValue();
                var win = this;
                self.apis.WeChatUser.createLabel
                    .wait(self, '正在添加标签...')
                    .call({
                        openType: self.openType,
                        accountId: self.accountData['id'],
                        tagName: tagName
                    }, function () {
                        win.close();
                        self.find('grid').refreshStore();
                    })
            },

            onCancelClick: function (button, e, eOpts) {
                this.close();
            }
        });
        win.find('nickname').setValue(this.accountData['name']);
    },

    onUpdateClick: function (button, e, eOpts) {
        var self = this;
        var data = this.find('grid').getIgnoreSelect(arguments);
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
                title: '修改标签',
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
                                fieldLabel: '公众号',
                                value: ''
                            },
                            {
                                xtype: 'textfield',
                                name: 'tagName',
                                anchor: '100%',
                                fieldLabel: '标签名称'
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
                    var tagName = this.find('tagName').getValue();
                    var win = this;
                    self.apis.WeChatUser.updateLabel
                        .wait(self, '正在更新标签...')
                        .call({
                            openType: self.openType,
                            accountId: self.accountData['id'],
                            id: data['id'],
                            tagName: tagName
                        }, function () {
                            win.close();
                            self.find('grid').refreshStore();
                        })
                },

                onCancelClick: function (button, e, eOpts) {
                    this.close();
                }
            });
            win.find('nickname').setValue(this.accountData['name']);
            win.find('tagName').setValue(data['name']);
        } else {
            Dialog.alert('请先选中一条标签后再修改');
        }
    },

    onDelClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.find('grid').getIgnoreSelects(arguments);
        if (datas) {
            Dialog.batch({
                message: '确定删除标签{d}吗？',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(datas, "id");
                        self.apis.WeChatUser.delLabel
                            .wait(self, '正在删除标签...')
                            .call({
                                openType: self.openType,
                                accountId: self.accountData['id'],
                                ids: ids
                            }, function () {
                                self.find('grid').refreshStore();
                            });
                    }
                }
            });

        } else {
            Dialog.alert('提示', '请先选中二维码后再删除');
        }
    },

    setInit: function () {
        this.setTitle("标签管理 - " + this.accountData['name']);
        var store = this.apis.WeChatUser.getLabels.createListStore({
            openType: this.openType,
            accountId: this.accountData['id']
        });
        this.find('grid').setStore(store);
        store.load();
    }

});