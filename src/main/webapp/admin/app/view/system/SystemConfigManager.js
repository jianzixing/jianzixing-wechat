Ext.define('App.system.SystemConfigManager', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.grid.Panel',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View'
    ],

    border: false,
    layout: 'border',
    header: false,
    defaultListenerScope: true,
    apis: {
        SystemConfig: {
            addGroup: {},
            delGroup: {},
            updateGroup: {},
            getGroups: {},
            addConfig: {},
            delConfig: {},
            updateConfig: {},
            getConfigs: {}
        }
    },

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'list'),
                    text: '列出配置分组',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addPackage_dark'),
                    text: '添加配置分组',
                    listeners: {
                        click: 'onAddGroupClick'
                    }
                },
                '|',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addYouTrack_dark'),
                    text: '添加系统参数',
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret_dark'),
                    text: '删除系统参数',
                    listeners: {
                        click: 'onDelClick'
                    }
                }
            ]
        }
    ],
    items: [
        {
            xtype: 'gridpanel',
            name: 'config',
            region: 'center',
            border: false,
            header: false,
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'name',
                    text: '配置名称'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'key',
                    text: '配置键'
                },
                {
                    xtype: 'gridcolumn',
                    width: 200,
                    dataIndex: 'value',
                    text: '配置值',
                    renderer: function (value, mate, record) {
                        var type = record.get('type');
                        if (type == 1) {
                            if (!value || value == "") {
                                value = Resource.create('/admin/image/exicon/nopic_40.gif');
                            } else {
                                value = Resource.image(value);
                            }
                            var width = 40;
                            var height = 40;
                            return '<div style="height: ' + height + 'px;width: ' + width + 'px;vertical-align: middle;display:table-cell;">' +
                                '<img style="max-height: ' + height + 'px;max-width: ' + width + 'px;vertical-align: middle" src=' + value + '></div> ';
                        }
                        return value;
                    }
                },
                {
                    xtype: 'gridcolumn',
                    width: 200,
                    dataIndex: 'detail',
                    text: '配置描述'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'isSystem',
                    text: '是否系统参数',
                    renderer: function (v) {
                        if (v == 0) {
                            return "非系统参数";
                        }
                        if (v == 1) {
                            return '系统参数';
                        }
                        if (v == 2) {
                            return '隐藏参数';
                        }
                        return v;
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'pos',
                    text: '排序'
                },
                {
                    xtype: 'actioncolumn',
                    dataIndex: 'isSystem',
                    text: '操作',
                    items: [
                        {
                            iconCls: 'x-fa fa-pencil green',
                            tooltip: '更新系统配置',
                            handler: 'onUpdateClick',
                            getClass: function (v) {
                                if (v == 2) {
                                    return "x-hidden";
                                } else {
                                    return 'x-fa fa-pencil green';
                                }
                            }
                        },
                        {
                            iconCls: 'x-fa fa-times red',
                            tooltip: '删除系统配置',
                            handler: 'onDelClick',
                            getClass: function (v) {
                                if (v == 1 || v == 2) {
                                    return "x-hidden";
                                } else {
                                    return 'x-fa fa-times red';
                                }
                            }
                        }
                    ]
                }
            ],
            dockedItems: [
                {
                    xtype: 'pagingtoolbar',
                    name: 'paging',
                    dock: 'bottom',
                    width: 360,
                    displayInfo: true
                }
            ],
            selModel: {
                selType: 'checkboxmodel'
            }
        },
        {
            xtype: 'gridpanel',
            name: 'group',
            region: 'west',
            split: true,
            border: false,
            width: 300,
            header: false,
            columns: [
                {
                    xtype: 'gridcolumn',
                    width: 60,
                    dataIndex: 'id',
                    text: 'ID'
                },
                {
                    xtype: 'gridcolumn',
                    width: 190,
                    dataIndex: 'name',
                    text: '分组名称'
                },
                {
                    xtype: 'actioncolumn',
                    width: 50,
                    dataIndex: 'id',
                    text: '操作',
                    items: [
                        {
                            iconCls: 'x-fa fa-pencil green',
                            tooltip: '更新系统配置',
                            handler: 'onUpdateGroupClick'
                        },
                        {
                            iconCls: 'x-fa fa-times red',
                            tooltip: '删除系统配置',
                            handler: 'onDeleteGroupClick'
                        }
                    ]
                }
            ],
            listeners: {
                itemclick: 'onGridpanelItemClick'
            }
        }
    ],

    addGroupForm: [
        {
            xtype: 'textfield',
            name: 'id',
            hidden: true,
            fieldLabel: 'ID',
            inputType: 'hidden'
        },
        {
            xtype: 'textfield',
            name: 'name',
            anchor: '100%',
            fieldLabel: '分组名称',
            allowBlank: false
        }
    ],

    onListClick: function (button, e, eOpts) {
        this.find('group').refreshStore();
    },

    onAddGroupClick: function () {
        var self = this;
        Dialog.openFormWindow({
            title: '添加系统分组',
            width: 500,
            height: 150,
            items: self.addGroupForm,
            success: function (json, win) {

                self.apis.SystemConfig.addGroup
                    .wait(self, '正在添加系统分组...')
                    .call({object: json}, function () {
                        win.close();
                        self.find('group').refreshStore()
                    });
            }
        })
    },

    onUpdateGroupClick: function () {
        var json = this.find('group').getIgnoreSelect(arguments);
        var self = this;
        Dialog.openFormWindow({
            title: '修改系统分组',
            width: 500,
            height: 150,
            items: self.addGroupForm,
            success: function (json, win) {
                self.apis.SystemConfig.updateGroup
                    .wait(self, '正在修改系统分组...')
                    .call({object: json}, function () {
                        win.close();
                        self.find('group').refreshStore()
                    });
            }
        }).setValues(json);
    },

    onDeleteGroupClick: function () {
        var json = this.find('group').getIgnoreSelect(arguments);
        var self = this;
        if (json) {
            Dialog.confirm('确定删除', '确定删除系统配置分组' + Color.string(json['name']) + '信息？', function (btn) {
                if (btn == 'yes') {
                    self.apis.SystemConfig.delGroup
                        .wait(self, '正在删除分组...')
                        .call({id: json['id']}, function () {
                            self.find('group').refreshStore();
                        })
                }
            })
        } else {
            Dialog.alert('请先选中一条要删除的分组信息');
        }
    },

    onGridpanelItemClick: function (dataview, record, item, index, e, eOpts) {
        var store = this.apis.SystemConfig.getConfigs.createPageStore({gid: record.get('id')});
        this.find('config').setStore(store);
        store.load();
    },

    onAddClick: function (button, e, eOpts) {
        var group = this.find('group').getIgnoreSelect(arguments);
        var self = this;

        if (group) {
            Dialog.openWindow('App.system.SystemConfigWindow', {
                apis: self.apis,
                _groupId: group['id'],
                _callback: function () {
                    self.find('config').refreshStore();
                }
            })
        } else {
            Dialog.alert('请先选中一条配置分组后再添加系统配置')
        }
    },

    onUpdateClick: function (button, e, eOpts) {
        var self = this;
        var data = self.find('config').getIgnoreSelect(arguments);

        Dialog.openWindow('App.system.SystemConfigWindow', {
            apis: self.apis,
            _callback: function () {
                self.find('config').refreshStore();
            }
        }).setValue(data)
    },

    onDelClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.find('config').getIgnoreSelects(arguments);

        if (jsons != null) {
            Dialog.batch({
                message: '确定删除系统配置项{d}吗？',
                data: jsons,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var keys = Array.splitArray(jsons, "key");
                        self.apis.SystemConfig.delConfig
                            .wait(self, '正在删除系统配置项...')
                            .call({names: keys}, function () {
                                self.find('config').refreshStore();
                            })
                    }
                }
            });
        } else {
            Dialog.alert('提示', '请先选中系统配置项后再删除');
        }
    },

    onAfterApply: function () {
        var store = this.apis.SystemConfig.getGroups.createListStore();
        this.find('group').setStore(store);
        store.load();
    }

});