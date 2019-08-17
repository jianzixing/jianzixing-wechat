Ext.define('App.support.SupportManager', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.view.Table',
        'Ext.selection.CheckboxModel',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    border: false,
    header: false,
    defaultListenerScope: true,
    apis: {
        Support: {
            addSupport: {},
            delSupport: {},
            updateSupport: {},
            getSupports: {}
        }
    },

    columns: [
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'name',
            text: '服务名称'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'type',
            text: '服务类型'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'serTime',
            text: '服务有效期',
            renderer: function (v) {
                return v;
            }
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'detail',
            text: '服务描述'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'createTime',
            text: '创建时间',
            renderer: function (v) {
                if (v) return new Date(v).format();
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
                    iconCls: 'x-fa fa-times',
                    tooltip: '删除服务',
                    handler: 'onDelClick'
                }
            ]
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
                    icon: Resource.png('jet', 'list'),
                    text: '列出服务',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addFavoritesList'),
                    text: '添加服务',
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret_dark'),
                    text: '批量删除服务',
                    listeners: {
                        click: 'onDelClick'
                    }
                }
            ]
        }
    ],

    onListClick: function (button, e, eOpts) {
        this.onAfterApply();
    },

    onAddClick: function (button, e, eOpts) {
        var self = this;
        var win = Dialog.openWindow('App.support.SupportWindow', {
            apis: this.apis,
            _callback: function () {
                self.refreshStore();
            }
        });
    },

    onUpdateClick: function (button, e, eOpts) {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var win = Dialog.openWindow('App.support.SupportWindow', {
                apis: this.apis,
                _callback: function () {
                    self.refreshStore();
                }
            });
            win.setValue(data);
        }
    },

    onDelClick: function (button, e, eOpts) {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data != null) {
            Dialog.batch({
                message: '确定删除服务{d}吗？',
                data: [data],
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        self.apis.Support.delSupport
                            .wait(self, '正在删除服务...')
                            .call({id: data['id']}, function () {
                                self.refreshStore();
                            })
                    }
                }
            });
        }
    },

    onAfterApply: function () {
        var store = this.apis.Support.getSupports.createListStore();
        this.setStore(store);
        store.load();
    }

});
