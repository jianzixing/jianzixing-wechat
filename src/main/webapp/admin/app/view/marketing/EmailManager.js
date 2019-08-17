Ext.define('App.marketing.EmailManager', {
    extend: 'Ext.grid.Panel',
    name: 'emailSetting_manager',
    preventHeader: true,
    border: false,
    loadMask: true,
    defaultListenerScope: true,
    apis: {
        Email: {
            addEmail: {},
            delEmails: {},
            updateEmail: {},
            enableEmails: {},
            disableEmails: {},
            getEmails: {}
        }
    },
    plugins: [{ptype: 'cellediting', clicksToEdit: 1}],
    selModel: {
        selType: 'checkboxmodel'
    },

    search: {
        hidden: true,
        items: [
            {
                xtype: 'textfield',
                name: 'like_userName',
                fieldLabel: '邮箱'
            },
            {
                xtype: 'button',
                margin: '0 0 0 20',
                hidden: true,
                text: '搜索',
                iconCls: 'fa fa-search',
                listeners: {
                    click: 'onSearchEventListener'
                }
            }
        ]
    },

    columns: [
        {
            xtype: 'gridcolumn',
            dataIndex: 'id',
            text: 'ID'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'name',
            text: '邮件服务名称'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'smtpAddress',
            text: '服务器地址'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'smtpPort',
            text: '端口'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'smtpUserName',
            text: '用户名'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'encoding',
            text: '邮件编码'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'ssl',
            text: '是否使用SSL',
            renderer: function (v) {
                if (v == 1) {
                    return '<span style="color: #0f74a8">是</span>';
                }
                return '否';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'enable',
            text: '启用状态',
            renderer: function (v) {
                if (v == 1) {
                    return '<span style="color: #0f74a8">启用</span>';
                }
                return '<span style="color: #aa2222">禁用</span>';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'remark',
            text: '备注'
        },
        {
            xtype: 'actioncolumn',
            text: '操作',
            items: [
                {
                    tooltip: '修改',
                    icon: Resource.png('jet', 'editItemInSection'),
                    handler: 'onUpdateClick'
                },
                '->',
                {
                    icon: Resource.png('jet', 'exclude'),
                    tooltip: '删除',
                    handler: 'onDeleteClick'
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
                    text: '列出邮箱服务',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addJira'),
                    text: '添加邮箱服务',
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '批量删除服务',
                    listeners: {
                        click: 'onDeleteClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'db_muted_disabled_breakpoint'),
                    text: '启用邮箱服务',
                    listeners: {
                        click: 'onEnableClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'testError_dark'),
                    text: '启用邮箱服务',
                    listeners: {
                        click: 'onDisableClick'
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
                    html: '<span style="color: #666666;margin-right: 20px">邮件服务只能启用一个作为默认邮箱服务</span>'
                }
            ]
        }
    ],

    onGridCellClick: function (grid, record, item, index, e, eOpts) {
        var btn = e.getTarget();
        if (btn) {
        }
    },

    onListClick: function (button, e, options) {
        this.onAfterApply();
    },

    onAddClick: function (button, e, options) {
        var self = this;
        Dialog.openWindow('App.marketing.EmailWindow', {
            apis: this.apis,
            _callback: function () {
                self.refreshStore();
            }
        });
    },

    onDeleteClick: function (button, e, options) {
        var jsons = this.getIgnoreSelects(arguments);
        var self = this;
        if (jsons) {
            Dialog.batch({
                message: '确定删除邮箱参数{d}吗？',
                data: jsons,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, 'id');
                        self.apis.Email.delEmails
                            .wait(self, '正在删除邮箱参数...')
                            .call({ids: ids}, function () {
                                self.refreshStore()
                            });
                    }
                }
            });
        } else {
            Dialog.alert('请先勾选要删除的邮件服务!')
        }
    },

    onEnableClick: function (button, e, options) {
        var jsons = this.getIgnoreSelects(arguments);
        var self = this;
        if (jsons) {
            Dialog.batch({
                message: '确定启用邮箱服务{d}吗？',
                data: jsons,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, 'id');
                        self.apis.Email.enableEmails
                            .wait(self, '正在启用邮箱服务...')
                            .call({ids: ids}, function () {
                                self.refreshStore()
                            });
                    }
                }
            });
        } else {
            Dialog.alert('请先勾选要启用的邮箱服务!')
        }
    },

    onDisableClick: function (button, e, options) {
        var jsons = this.getIgnoreSelects(arguments);
        var self = this;
        if (jsons) {
            Dialog.batch({
                message: '确定禁用邮箱服务{d}吗？',
                data: jsons,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, 'id');
                        self.apis.Email.disableEmails
                            .wait(self, '正在启用邮箱服务...')
                            .call({ids: ids}, function () {
                                self.refreshStore()
                            });
                    }
                }
            });
        } else {
            Dialog.alert('请先勾选要启用的邮箱服务!')
        }
    },

    onUpdateClick: function (grid, rowIndex, colIndex) {
        var self = this;
        var json = this.getIgnoreSelect(arguments);
        if (json != null) {
            Dialog.openWindow('App.marketing.EmailWindow', {
                apis: this.apis,
                _callback: function () {
                    self.refreshStore();
                }
            }).setValues(json);
        }
    },

    onSearchClick: function (button, e, options) {
        if (this.searchPanel.isVisible()) {
            this.searchPanel.hide();
        } else {
            this.searchPanel.show();
        }
    },

    onAfterApply: function () {
        var store = this.apis.Email.getEmails.createListStore();
        this.setStore(store);
        store.load();
    },

    onBackApply: function () {
        this.refreshStore();
    },

    onSearchEventListener: function (form) {

    }

});

