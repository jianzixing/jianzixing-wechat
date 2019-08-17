Ext.define('App.system.ModuleManager', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.grid.Panel',
        'Ext.grid.column.Action',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.tree.Panel',
        'Ext.tree.View',
        'Ext.tree.Column'
    ],

    layout: 'border',
    header: false,
    defaultListenerScope: true,
    apis: {
        Module: {
            deleteTopModule: {},
            addModule: {},
            updateModule: {},
            getFullTreeModules: {}
        }
    },

    items: [
        {
            xtype: 'gridpanel',
            name: 'top_module',
            region: 'west',
            split: true,
            width: 520,
            title: '顶级目录',
            header: false,
            api: {Module: {getTopModules: {_page: 'App.system.ModuleManager'}}},
            columns: [
                {
                    xtype: 'gridcolumn',
                    width: 120,
                    dataIndex: 'text',
                    text: '名称'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'linkModule',
                    text: '标识符'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'tabIcon',
                    text: '图标'
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
                            icon: Resource.png('jet', 'editFolder'),
                            tooltip: '修改模块',
                            iconCls: 'ml10',
                            handler: 'onEditModuleClick'
                        },
                        {
                            icon: Resource.png('jet', 'delete'),
                            tooltip: '删除模块',
                            iconCls: 'ml10',
                            handler: 'onDeleteModuleClick'
                        }
                    ]
                }
            ],
            listeners: {
                itemclick: 'onGridpanelItemClick'
            }
        },
        {
            xtype: 'treepanel',
            name: 'tree_list',
            region: 'center',
            rootVisible: false,
            split: false,
            header: false,
            title: '菜单列表',
            apiDelay: true,
            columns: [
                {
                    xtype: 'treecolumn',
                    width: 280,
                    dataIndex: 'text',
                    text: '名称'
                },
                {
                    xtype: 'gridcolumn',
                    width: 200,
                    dataIndex: 'module',
                    text: '模块类'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'pos',
                    text: '排序'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'tabIcon',
                    text: '图标'
                },
                {
                    xtype: 'actioncolumn',
                    text: '操作',
                    items: [
                        {
                            icon: Resource.png('jet', 'addBlankLine'),
                            tooltip: '添加子菜单',
                            handler: 'onAddTreeClick'
                        },
                        '->',
                        {
                            icon: Resource.png('jet', 'edit'),
                            tooltip: '修改菜单',
                            handler: 'onEditTreeClick'
                        },
                        '->',
                        {
                            icon: Resource.png('jet', 'delete'),
                            tooltip: '删除菜单',
                            handler: 'onDeleteTreeClick'
                        }
                    ]
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
                    text: '列出模块',
                    icon: Resource.png('jet', 'listChanges'),
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    text: '添加模块',
                    icon: Resource.png('jet', 'addClass'),
                    listeners: {
                        click: 'onTopAddClick'
                    }
                },
                '-',
                {
                    xtype: 'button',
                    text: '添加根菜单',
                    icon: Resource.png('jet', 'addBlankLine'),
                    listeners: {
                        click: 'onTreeAddClick'
                    }
                },
                {
                    xtype: 'button',
                    text: '刷新整个页面',
                    icon: Resource.png('jet', 'refresh'),
                    listeners: {
                        click: 'onRefreshClick'
                    }
                }
            ]
        }
    ],

    moduleFormItems: [
        {
            xtype: 'textfield',
            name: 'id',
            hidden: true,
            fieldLabel: 'ID',
            inputType: 'hidden'
        },
        {
            xtype: 'textfield',
            name: 'text',
            anchor: '100%',
            fieldLabel: '名称',
            allowBlank: false
        },
        {
            xtype: 'textfield',
            name: 'linkModule',
            anchor: '100%',
            fieldLabel: '标识符',
            allowBlank: false
        },
        {
            xtype: 'textfield',
            name: 'tabIcon',
            anchor: '100%',
            fieldLabel: '图标'
        },
        {
            xtype: 'textfield',
            name: 'pos',
            anchor: '100%',
            fieldLabel: '排序',
            inputType: 'number',
            emptyText: '0'
        }
    ],

    treeFormItems: [
        {
            xtype: 'textfield',
            name: 'id',
            hidden: true,
            fieldLabel: 'ID',
            inputType: 'hidden'
        },
        {
            xtype: 'textfield',
            name: 'pid',
            hidden: true,
            fieldLabel: '父节点ID',
            inputType: 'hidden'
        },
        {
            xtype: 'textfield',
            name: 'text',
            anchor: '100%',
            fieldLabel: '名称',
            allowBlank: false
        },
        {
            xtype: 'textfield',
            name: 'module',
            anchor: '100%',
            fieldLabel: '模块'
        },
        {
            xtype: 'textfield',
            name: 'linkModule',
            anchor: '100%',
            fieldLabel: '标识符',
            allowBlank: false,
            editable: false
        },
        {
            xtype: 'radiogroup',
            fieldLabel: '是否叶子',
            columns: 2,
            vertical: true,
            items: [
                {boxLabel: '否', name: 'leaf', inputValue: '0', checked: true},
                {boxLabel: '是', name: 'leaf', inputValue: '1'}
            ]
        },
        {
            xtype: 'textfield',
            name: 'tabIcon',
            anchor: '100%',
            fieldLabel: '图标'
        },
        {
            xtype: 'textfield',
            name: 'pos',
            anchor: '100%',
            fieldLabel: '排序',
            inputType: 'number',
            emptyText: '0'
        }
    ],

    onListClick: function (button, e, eOpts) {
        this.find('top_module').refreshStore();
    },

    onRefreshClick: function () {
        Dialog.confirm('提示', '请先确保您已经保存所有工作！确定刷新当前登录的后台页面吗？', function (btn) {
            if (btn == 'yes') {
                window.location.reload();
            }
        });
    },

    onTopAddClick: function (button, e, eOpts) {
        var self = this;
        Dialog.openFormWindow({
            title: '添加顶级目录',
            width: 433,
            height: 280,
            items: self.moduleFormItems,
            success: function (json, win) {
                json['top'] = 1;
                self.apis.Module.addModule
                    .wait(self, '正在添加顶级目录...')
                    .call({object: json}, function () {
                        win.close();
                        self.find('top_module').refreshStore();
                    });
            }
        });
    },

    onGridpanelItemClick: function (dataview, record, item, index, e, eOpts) {
        var text = record.get('text');
        var module = record.get('linkModule');
        var tree = this.find('tree_list');
        tree.setTitle('菜单列表 - ' + text);

        this.refreshTreeStore(module)
    },

    refreshTreeStore: function (module) {
        var self = this;
        var tree = this.find('tree_list');
        this.apis.Module.getFullTreeModules
            .wait(self, '正在加载模块菜单...')
            .call({module: module}, function (json) {
                var store = Ext.create('Ext.data.TreeStore', {
                    root: {
                        expanded: true,
                        text: "根节点",
                        children: json
                    }
                });
                tree.setStore(store);
            });
    },

    onTreeAddClick: function (button, e, eOpts) {
        var self = this;
        var topModule = this.find('top_module').getIgnoreSelect(arguments);

        if (topModule) {
            Dialog.openFormWindow({
                title: '添加菜单',
                width: 433,
                height: 350,
                items: self.treeFormItems,
                success: function (json, win) {
                    self.apis.Module.addModule
                        .wait(self, '正在添加菜单...')
                        .call({object: json}, function () {
                            win.close();
                            self.refreshTreeStore(topModule['linkModule'])
                        });
                }
            }).setValues({linkModule: topModule['linkModule'], pid: topModule['id']});
        } else {
            Dialog.alert('请先选中一个模块后再添加模块功能菜单!');
        }
    },

    onEditModuleClick: function () {
        var self = this;
        var json = this.find('top_module').getIgnoreSelect(arguments);
        Dialog.openFormWindow({
            title: '修改顶级目录',
            width: 433,
            height: 280,
            items: self.moduleFormItems,
            success: function (json, win) {
                self.apis.Module.updateModule
                    .wait(self, '正在修改顶级目录...')
                    .call({object: json}, function () {
                        win.close();
                        self.find('top_module').refreshStore();
                    });
            }
        }).setValues(json);
    },

    onDeleteModuleClick: function () {
        var self = this;
        var json = this.find('top_module').getIgnoreSelect(arguments);
        var grid = this.find('top_module');

        Dialog.batch({
            message: '确定删除模块{d}吗？',
            data: json,
            key: 'text',
            callback: function (btn) {
                if (btn == Global.YES) {
                    self.apis.Module.deleteTopModule
                        .wait(self, '正在删除模块...')
                        .call({id: json['id']}, function () {
                            grid.refreshStore();
                        });
                }
            }
        });
    },

    onAddTreeClick: function () {
        var self = this;
        var json = this.find('tree_list').getIgnoreSelect(arguments);
        var topModule = this.find('top_module').getIgnoreSelect(arguments);

        Dialog.openFormWindow({
            title: '添加菜单',
            width: 433,
            height: 350,
            items: self.treeFormItems,
            success: function (json, win) {
                self.apis.Module.addModule
                    .wait(self, '正在添加菜单...')
                    .call({object: json}, function () {
                        win.close();
                        self.refreshTreeStore(topModule['linkModule'])
                    });
            }
        }).setValues({linkModule: topModule['linkModule'], pid: json['id']});
    },

    onEditTreeClick: function () {
        var self = this;
        var topModule = this.find('top_module').getIgnoreSelect(arguments);
        var json = this.find('tree_list').getIgnoreSelect(arguments);
        json['leaf'] = json['leaf'] ? 1 : 0;

        Dialog.openFormWindow({
            title: '修改菜单',
            width: 433,
            height: 350,
            items: self.treeFormItems,
            success: function (json, win) {
                self.apis.Module.updateModule
                    .wait(self, '正在修改菜单...')
                    .call({object: json}, function () {
                        win.close();
                        self.refreshTreeStore(topModule['linkModule'])
                    });
            }
        }).setValues(json);
    },

    onDeleteTreeClick: function () {
        var self = this;
        var json = this.find('tree_list').getIgnoreSelect(arguments);
        var topModule = this.find('top_module').getIgnoreSelect(arguments);

        Dialog.batch({
            message: '确定删除菜单{d}吗？',
            data: json,
            key: 'text',
            callback: function (btn) {
                if (btn == Global.YES) {
                    self.apis.Module.deleteTopModule
                        .wait(self, '正在删除菜单...')
                        .call({id: json['id']}, function () {
                            self.refreshTreeStore(topModule['linkModule'])
                        });
                }
            }
        })
    }

});