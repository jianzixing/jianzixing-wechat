Ext.define('App.logistics.LogisticsCompanyManager', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.view.Table',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.selection.CheckboxModel'
    ],

    border: false,
    header: false,
    defaultListenerScope: true,

    apis: {
        Logistics: {
            addCompany: {},
            deleteCompany: {},
            updateCompany: {},
            setDefaultCompany: {},
            getCompany: {}
        }
    },

    columns: [
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'name',
            text: '快递名称'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'code',
            text: '标识码'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'logo',
            text: 'LOGO',
            renderer: function (value) {
                if (!value || value == "") {
                    value = Resource.create('/admin/image/exicon/nopic_40.gif');
                } else {
                    value = Resource.image(value);
                }
                return "<img src='" + value + "' style='width: 80px;height: 40px'/>";
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'isDefault',
            text: '是否默认',
            renderer: function (v) {
                if (v == 1) {
                    return '默认快递';
                } else {
                    return '非默认';
                }
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'enable',
            text: '是否启用',
            renderer: function (v) {
                if (v == 1) {
                    return '启用';
                } else {
                    return '禁用';
                }
            }
        },
        {
            xtype: 'gridcolumn',
            width: 300,
            dataIndex: 'detail',
            text: '公司描述'
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
                    icon: Resource.png('jet', 'listChanges'),
                    text: '列出快递公司',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addFavoritesList'),
                    text: '添加快递公司',
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '批量删除快递公司',
                    listeners: {
                        click: 'onDeleteClick'
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
        var win = Dialog.openWindow('App.logistics.LogisticsCompanyWindow', {
            apis: this.apis,
            _callback: function () {
                self.refreshStore();
            }
        });
    },

    onDeleteClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.getIgnoreSelects(arguments);
        if (jsons != null) {
            Dialog.batch({
                message: '确定删除物流公司{d}吗？',
                data: jsons,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, "id");
                        self.apis.Logistics.deleteCompany
                            .wait(self, '正在删除物流公司...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            })
                    }
                }
            });
        } else {
            Dialog.alert('提示', '请先选中至少一条物流公司后再删除!');
        }
    },

    onUpdateClick: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var win = Dialog.openWindow('App.logistics.LogisticsCompanyWindow', {
                apis: this.apis,
                _callback: function () {
                    self.refreshStore();
                }
            });
            win.setValue(data);
        } else {
            Dialog.alert('请先选中一条记录后再修改');
        }
    },

    onAfterApply: function () {
        var store = this.apis.Logistics.getCompany.createListStore();
        this.setStore(store);
        store.load();
    }

});
