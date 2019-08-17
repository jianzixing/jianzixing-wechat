Ext.define('App.system.RoleManager', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.grid.Panel',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'Ext.button.Button',
        'Ext.toolbar.Paging',
        'Ext.tree.Panel',
        'Ext.tree.View',
        'Ext.tree.Column'
    ],

    border: false,
    height: 604,
    width: 925,
    layout: 'border',
    header: false,
    defaultListenerScope: true,
    apis: {
        System: {
            addRole: {},
            deleteRole: {},
            updateRole: {},
            setModuleAndRole: {},
            removeModuleAndRole: {}
        },
        Module: {
            getRoleTreeModules: {},
            getRoleTreeList: {},
            getAllTreeModules: {}
        }
    },

    items: [
        {
            xtype: 'gridpanel',
            name: 'role_grid',
            region: 'west',
            split: true,
            width: 540,
            title: '角色管理',
            api: {System: {getRoles: {_page: 'App.system.RoleManager'}}},
            columns: [
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'roleName',
                    text: '名称'
                },
                {
                    xtype: 'gridcolumn',
                    width: 120,
                    dataIndex: 'isSystemRole',
                    text: '是否超级管理员',
                    renderer: function (value) {
                        if (value && value == 1) return "<span style='color: #0f74a8'>超级管理员</span>";
                        else return "<span style='color: #999999'>普通管理员</span>";
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'pos',
                    text: '排序'
                },
                {
                    xtype: 'actioncolumn',
                    text: '操作',
                    items: [
                        {
                            iconCls: 'x-fa fa-pencil',
                            tooltip: '修改角色',
                            handler: 'onUpdateRoleClick'
                        },
                        {
                            iconCls: 'x-fa fa-times',
                            tooltip: '删除角色',
                            handler: 'onDeleteRoleClick'
                        }
                    ]
                }
            ],
            listeners: {
                itemclick: 'onGridpanelItemClick'
            },
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'list'),
                            text: '列出角色',
                            listeners: {
                                click: 'onListRoleClick'
                            }
                        },
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'addRemoteDatasource'),
                            text: '添加角色',
                            listeners: {
                                click: 'onAddRoleClick'
                            }
                        }
                    ]
                },
                {
                    xtype: 'pagingtoolbar',
                    name: 'pagingtoolbar',
                    dock: 'bottom',
                    width: 360,
                    displayInfo: true
                }
            ]
        },
        {
            xtype: 'treepanel',
            name: 'tree_panel',
            region: 'center',
            title: '权限管理',
            rootVisible: false,
            viewConfig: {},
            columns: [
                {
                    xtype: 'treecolumn',
                    width: 280,
                    dataIndex: 'text',
                    text: '名称'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'pos',
                    text: '排序'
                },
                {
                    xtype: 'actioncolumn',
                    text: '操作',
                    items: [
                        {
                            icon: Resource.png('jet', 'delete'),
                            tooltip: '移除菜单',
                            iconCls: 'ml10',
                            handler: 'onRemoveTreeClick'
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
                            text: '修改菜单权限',
                            icon: Resource.png('jet', 'editColors_dark'),
                            listeners: {
                                click: 'onEditTreeClick'
                            }
                        }
                    ]
                }
            ]
        }
    ],

    roleFormItems: [
        {
            xtype: 'textfield',
            name: 'id',
            hidden: true,
            fieldLabel: 'ID',
            inputType: 'hidden'
        },
        {
            xtype: 'textfield',
            name: 'roleName',
            anchor: '100%',
            fieldLabel: '名称',
            allowBlank: false
        },
        {
            xtype: 'textfield',
            name: 'pos',
            anchor: '100%',
            fieldLabel: '排序',
            inputType: 'number',
            emptyText: '0'
        },
        {
            xtype: 'radiogroup',
            fieldLabel: '是否超级管理员',
            columns: 2,
            vertical: true,
            items: [
                {boxLabel: '否', name: 'isSystemRole', inputValue: '0', checked: true},
                {boxLabel: '是', name: 'isSystemRole', inputValue: '1'}
            ]
        },
        {
            xtype: 'textareafield',
            name: 'detail',
            anchor: '100%',
            fieldLabel: '描述'
        }
    ],

    onListRoleClick: function () {
        this.find('role_grid').refreshStore();
    },

    onGridpanelItemClick: function (dataview, record, item, index, e, eOpts) {
        var roleId = record.get('id');
        this.onRefreshTreeStore(roleId)
    },

    onRefreshTreeStore: function (roleId) {
        var self = this;
        this.apis.Module.getRoleTreeModules
            .wait(self, '正在加载权限...')
            .call({roleId: roleId}, function (d) {
                self.find('tree_panel').setStore(Ext.create('Ext.data.TreeStore', {
                    root: {
                        expanded: true,
                        children: d
                    }
                }))
            })
    },

    onAddRoleClick: function (button, e, eOpts) {
        var self = this;
        Dialog.openFormWindow({
            title: '添加角色',
            width: 433,
            height: 310,
            items: self.roleFormItems,
            success: function (json, win) {
                self.apis.System.addRole
                    .wait(self, '正在添加角色...')
                    .call({object: json}, function () {
                        win.close();
                        self.find('role_grid').refreshStore();
                    });
            }
        });
    },

    onEditTreeClick: function (button, e, eOpts) {
        var self = this;
        var json = self.find('role_grid').getIgnoreSelect(arguments);
        if (json == null) {
            Dialog.alert("必须选中一个角色")
            return;
        }
        this.apis.Module.getRoleTreeList
            .wait(self.find('tree_panel'), '正在加载数据...')
            .call({roleId: json['id']}, function (d) {
                Dialog.openWindow('App.system.SelectModuleWindow', {
                    apis: self.apis,
                    _callback: function (datas) {
                        var ids = Array.splitArray(datas, "id");
                        self.apis.System.setModuleAndRole
                            .wait(self.find('tree_panel'), '正在配置权限...')
                            .call({roleId: json['id'], moduleId: ids}, function () {
                                self.onRefreshTreeStore(json['id'])
                            });
                    }
                }).setValues(d)
            })
    },

    onRemoveTreeClick: function () {
        var self = this;
        var tree = this.find('tree_panel').getIgnoreSelect(arguments);
        var json = this.find('role_grid').getIgnoreSelect(arguments);

        Dialog.batch({
            message: '确定从角色 ' + json['roleName'] + ' 中移除目录{d}吗？',
            data: tree,
            key: 'text',
            callback: function (btn) {
                if (btn == Global.YES) {
                    self.apis.System.removeModuleAndRole
                        .wait(self, '正在移除角色权限...')
                        .call({roleId: json['id'], moduleId: tree['id']}, function () {
                            self.onRefreshTreeStore(json['id'])
                        })
                }
            }
        });
    },

    onUpdateRoleClick: function () {
        var self = this;
        var json = this.find('role_grid').getIgnoreSelect(arguments);
        Dialog.openFormWindow({
            title: '修改角色',
            width: 433,
            height: 310,
            items: self.roleFormItems,
            success: function (json, win) {
                self.apis.System.updateRole
                    .wait(self, '正在修改角色...')
                    .call({object: json}, function () {
                        win.close();
                        self.find('role_grid').refreshStore();
                    });
            }
        }).setValues(json);
    },

    onDeleteRoleClick: function () {
        var self = this;
        var json = this.find('role_grid').getIgnoreSelect(arguments);
        Dialog.batch({
            message: '确定删除角色{d}吗？',
            data: json,
            key: 'text',
            callback: function (btn) {
                if (btn == Global.YES) {
                    self.apis.System.deleteRole
                        .wait(self, '正在删除角色...')
                        .call({id: json['id']}, function () {
                            self.find('tree_panel').refreshStore()
                        })
                }
            }
        });
    },

    onAfterApply: function () {
        this.find('pagingtoolbar').bindStore(this.find('role_grid').getStore());
    }

});