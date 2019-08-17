Ext.define('App.marketing.SmsManager', {
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
    apis: {
        Sms: {
            getSms: {},
            getSmsImpls: {},
            addSms: {},
            updateSms: {},
            delSms: {},
            enableSms: {},
            disableSms: {}
        }
    },

    columns: [
        {
            xtype: 'gridcolumn',
            dataIndex: 'id',
            text: 'ID'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'name',
            text: '短信服务名称'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'implName',
            text: '使用接口'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'type',
            text: '短信内容类型',
            renderer: function (v) {
                if (v == 1) {
                    return '短信模板';
                }
                return '短信文本';
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
                    icon: Resource.png('jet', 'list'),
                    text: '列出短信服务',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addJira'),
                    text: '添加短信服务',
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '批量删除服务',
                    listeners: {
                        click: 'onDelClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'db_muted_disabled_breakpoint'),
                    text: '启用短信服务',
                    listeners: {
                        click: 'onEnableClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'testError_dark'),
                    text: '启用短信服务',
                    listeners: {
                        click: 'onDisableClick'
                    }
                }
            ]
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
        var win = Dialog.openWindow('App.marketing.SmsWindow', {
            apis: this.apis,
            _callback: function () {
                self.refreshStore();
            }
        });
        win.setInit();
    },

    onUpdateClick: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var win = Dialog.openWindow('App.marketing.SmsWindow', {
                apis: this.apis,
                _callback: function () {
                    self.refreshStore();
                }
            });
            win.setInit();
            win.setValue(data);
        }
    },

    onDelClick: function (button, e, eOpts) {
        var jsons = this.getIgnoreSelects(arguments);
        var self = this;
        if (jsons) {
            Dialog.batch({
                message: '确定删除短信服务{d}吗？',
                data: jsons,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, 'id');
                        self.apis.Sms.delSms
                            .wait(self, '正在删除短信服务...')
                            .call({ids: ids}, function () {
                                self.refreshStore()
                            });
                    }
                }
            });
        } else {
            Dialog.alert('请先勾选要删除的短信服务!')
        }
    },

    onEnableClick: function (button, e, eOpts) {
        var jsons = this.getIgnoreSelects(arguments);
        var self = this;
        if (jsons) {
            Dialog.batch({
                message: '确定启用短信服务{d}吗？',
                data: jsons,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, 'id');
                        self.apis.Sms.enableSms
                            .wait(self, '正在启用短信服务...')
                            .call({ids: ids}, function () {
                                self.refreshStore()
                            });
                    }
                }
            });
        } else {
            Dialog.alert('请先勾选要启用的短信服务!')
        }
    },

    onDisableClick: function (button, e, eOpts) {
        var jsons = this.getIgnoreSelects(arguments);
        var self = this;
        if (jsons) {
            Dialog.batch({
                message: '确定禁用短信服务{d}吗？',
                data: jsons,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, 'id');
                        self.apis.Sms.disableSms
                            .wait(self, '正在禁用短信服务...')
                            .call({ids: ids}, function () {
                                self.refreshStore()
                            });
                    }
                }
            });
        } else {
            Dialog.alert('请先勾选要禁用的短信服务!')
        }
    },

    onAfterApply: function () {
        var store = this.apis.Sms.getSms.createListStore();
        this.setStore(store);
        store.load();
    }

});
