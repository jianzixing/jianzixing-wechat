Ext.define('App.system.SystemDictManager', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.grid.Panel',
        'Ext.grid.View',
        'Ext.button.Button',
        'Ext.selection.CheckboxModel',
        'Ext.toolbar.Paging',
        'Ext.grid.column.Action'
    ],

    layout: 'border',
    header: false,
    defaultListenerScope: true,
    apis: {
        SystemDict: {
            getDicts: {},
            addDictType: {},
            delDictType: {},
            updateType: {},
            addDict: {},
            delDict: {},
            updateDict: {},
            copyType: {}
        }
    },

    items: [
        {
            xtype: 'gridpanel',
            name: 'dict',
            flex: 1,
            region: 'center',
            split: true,
            title: '字典管理',
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'name',
                    text: '字典名称'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'value',
                    text: '字典标识符'
                },
                {
                    xtype: 'actioncolumn',
                    text: '操作',
                    items: [
                        {
                            iconCls: 'x-fa fa-pencil green',
                            tooltip: '修改字典',
                            handler: 'onUpdateDict'
                        },
                        {
                            iconCls: 'x-fa fa-times red',
                            tooltip: '删除字典',
                            handler: 'onDeleteDict'
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
                            text: '列出字典',
                            icon: Resource.png('jet', 'list'),
                            listeners: {
                                click: 'onListDict'
                            }
                        },
                        {
                            xtype: 'button',
                            text: '添加字典',
                            icon: Resource.png('jet', 'addYouTrack_dark'),
                            listeners: {
                                click: 'onAddDict'
                            }
                        },
                        {
                            xtype: 'button',
                            text: '批量删除字典',
                            icon: Resource.png('jet', 'RemoveMulticaret_dark'),
                            listeners: {
                                click: 'onDeleteDict'
                            }
                        }
                    ]
                }
            ],
            selModel: {
                selType: 'checkboxmodel'
            }
        },
        {
            xtype: 'gridpanel',
            name: 'dictType',
            flex: 1,
            region: 'west',
            split: true,
            width: 150,
            title: '字典类型表管理',
            api: {SystemDict: {getTypes: {_page: 'App.system.SystemDictManager'}}},
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'name',
                    text: '类型名称'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'table',
                    text: '表名称'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'field',
                    text: '字段名称'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'type',
                    text: '类别',
                    renderer: function (v) {
                        if (v == 1) {
                            return '文字';
                        }
                        if (v == 2) {
                            return '状态';
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
                            iconCls: 'x-fa fa-pencil green',
                            tooltip: '修改类型',
                            handler: 'onUpdateType'
                        },
                        {
                            iconCls: 'x-fa fa-times red',
                            tooltip: '删除类型',
                            handler: 'onDeleteType'
                        },
                        {
                            iconCls: 'x-fa fa-copy yellow',
                            tooltip: '拷贝类型',
                            handler: 'onCopyType'
                        }
                    ]
                }
            ],
            dockedItems: [
                {
                    xtype: 'pagingtoolbar',
                    name: 'dictPaging',
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
                            text: '列出类型',
                            icon: Resource.png('jet', 'list'),
                            listeners: {
                                click: 'onListDictType'
                            }
                        },
                        {
                            xtype: 'button',
                            text: '添加类型',
                            icon: Resource.png('jet', 'addYouTrack_dark'),
                            listeners: {
                                click: 'onAddType'
                            }
                        },
                        {
                            xtype: 'button',
                            text: '批量删除类型',
                            icon: Resource.png('jet', 'RemoveMulticaret_dark'),
                            listeners: {
                                click: 'onDeleteType'
                            }
                        }
                    ]
                }
            ],
            selModel: {
                selType: 'checkboxmodel'
            },
            listeners: {
                itemclick: 'onGridpanelItemClick'
            }
        }
    ],

    onListDictType: function () {
        this.find('dictType').refreshStore();
    },

    onListDict: function () {
        var typeId = this.find('dictType').getIgnoreSelect(arguments, "id");
        if (typeId) {
            this.loadDictData(typeId);
        }
    },

    onAddDict: function (button, e, eOpts) {
        var self = this;
        var typeId = this.find('dictType').getIgnoreSelect(arguments, "id");

        if (!typeId) {
            Dialog.alert('请先选择一个字典类型');
        }

        Dialog.openWindow('App.system.SystemDictWindow', {
            apis: this.apis,
            _typeId: typeId,
            _callback: function () {
                self.loadDictData(typeId);
            }
        });
    },

    onUpdateDict: function () {
        var self = this;
        var data = this.find('dict').getIgnoreSelect(arguments);
        var typeId = this.find('dictType').getIgnoreSelect(arguments, "id");

        if (!typeId) {
            Dialog.alert('请先选择一个字典类型');
        }

        Dialog.openWindow('App.system.SystemDictWindow', {
            apis: this.apis,
            _typeId: typeId,
            _callback: function () {
                self.loadDictData(typeId);
            }
        }).setValue(data);
    },

    onDeleteDict: function (button, e, eOpts) {
        var self = this;
        var typeId = this.find('dictType').getIgnoreSelect(arguments, "id");
        var jsons = this.find('dict').getIgnoreSelects(arguments);

        Dialog.batch({
            message: '确定删除字典{d}吗？',
            data: jsons,
            key: 'name',
            callback: function (btn) {
                if (btn == Global.YES) {
                    var ids = Array.splitArray(jsons, "id");
                    self.apis.SystemDict.delDict
                        .wait(self, '正在删除字典...')
                        .call({ids: ids}, function () {
                            self.loadDictData(typeId);
                        })
                }
            }
        });
    },

    onUpdateType: function () {
        var self = this;
        var data = this.find('dictType').getIgnoreSelect(arguments);

        if (data) {
            Dialog.openWindow('App.system.SystemDictTypeWindow', {
                apis: this.apis,
                _callback: function () {
                    self.find('dictType').refreshStore();
                }
            }).setValue(data);
        } else {
            Dialog.alert('请先选中一条字典类型数据');
        }
    },

    onDeleteType: function () {
        var self = this;
        var jsons = this.find('dictType').getIgnoreSelects(arguments);

        Dialog.batch({
            message: '确定删除字典分类{d}吗？',
            data: jsons,
            key: 'name',
            callback: function (btn) {
                if (btn == Global.YES) {
                    var ids = Array.splitArray(jsons, "id");
                    self.apis.SystemDict.delDictType
                        .wait(self, '正在删除字典分类...')
                        .call({ids: ids}, function () {
                            self.find('dictType').refreshStore();
                        })
                }
            }
        });
    },

    onCopyType: function () {
        var self = this;
        var data = this.find('dictType').getIgnoreSelect(arguments);

        Dialog.confirm('拷贝字典类型', '确认拷贝字典类型吗？', function (btn) {
            if (btn == "yes") {
                Dialog.openWindow('App.system.SystemDictTypeWindow', {
                    apis: self.apis,
                    _callback: function () {
                        self.find('dictType').refreshStore();
                    }
                }).setCopy(data);
            }
        });
    },

    onAddType: function (button, e, eOpts) {
        var self = this;

        Dialog.openWindow('App.system.SystemDictTypeWindow', {
            apis: this.apis,
            _callback: function () {
                self.find('dictType').refreshStore();
            }
        });
    },

    onAfterApply: function () {

    },

    onGridpanelItemClick: function (dataview, record, item, index, e, eOpts) {
        var self = this;
        var tid = record.get("id");
        this.loadDictData(tid);
    },

    loadDictData: function (tid) {
        var self = this;
        this.apis.SystemDict.getDicts
            .wait(self, '正在加载字典...')
            .call({tid: tid}, function (data) {
                var store = Ext.create("Ext.data.Store", {
                    data: data
                });
                self.find('dict').setStore(store);
            })
    }

});