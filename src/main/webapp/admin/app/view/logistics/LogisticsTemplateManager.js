Ext.define('App.logistics.LogisticsTemplateManager', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.view.Table',
        'Ext.toolbar.Paging',
        'Ext.button.Button',
        'Ext.selection.CheckboxModel'
    ],

    border: false,
    header: false,
    defaultListenerScope: true,
    apis: {
        Logistics: {
            getTemplates: {},
            addTemplates: {},
            updateTemplate: {},
            delTemplates: {},
            getTemplate: {}
        },
        Area: {
            getClassifyArea: {},
            getAreaByCodes: {}
        }
    },

    columns: [
        {
            xtype: 'gridcolumn',
            width: 80,
            dataIndex: 'id',
            text: 'ID'
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'name',
            text: '模板名称'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'free',
            text: '是否包邮',
            renderer: function (v) {
                if (v == 1) {
                    return '包邮';
                }
                return '不包邮';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'type',
            text: '模板类型',
            renderer: function (v) {
                if (v == 10) {
                    return '固定价格';
                } else if (v == 11) {
                    return '按件数';
                } else if (v == 12) {
                    return '按重量';
                } else if (v == 13) {
                    return '按体积';
                }
                return '未知类型'
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
        },
        {
            xtype: 'actioncolumn',
            text: '操作',
            dataIndex: 'id',
            align: 'center',
            items: [
                {
                    tooltip: '修改',
                    iconCls: "x-fa fa-pencil green",
                    handler: 'onUpdateClick'
                },
                {
                    iconCls: "x-fa fa-times red",
                    tooltip: '删除',
                    handler: 'onDelClick'
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
                    text: '列出运费模板',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addFavoritesList'),
                    text: '添加运费模板',
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '批量删除运费模板',
                    listeners: {
                        click: 'onDelClick'
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
        var win = Dialog.openWindow('App.logistics.LogisticsTemplateWindow', {
            apis: this.apis,
            _callback: function () {
                self.refreshStore();
            }
        });
    },

    onUpdateClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.getIgnoreSelects(arguments);
        if (jsons != null) {
            var win = Dialog.openWindow('App.logistics.LogisticsTemplateWindow', {
                apis: this.apis,
                _callback: function () {
                    self.refreshStore();
                }
            });
            win.setValue(jsons[0]);
        } else {
            Dialog.alert('提示', '请先选中至少一条物流模板后再修改!');
        }
    },

    onDelClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.getIgnoreSelects(arguments);
        if (jsons != null) {
            Dialog.batch({
                message: '确定删除物流模板{d}吗？',
                data: jsons,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, "id");
                        self.apis.Logistics.delTemplates
                            .wait(self, '正在删除物流模板...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            })
                    }
                }
            });
        } else {
            Dialog.alert('提示', '请先选中至少一条物流模板后再删除!');
        }
    },

    onAfterApply: function () {
        var store = this.apis.Logistics.getTemplates.createPageStore();
        this.setStore(store);
        store.load();
    }

});