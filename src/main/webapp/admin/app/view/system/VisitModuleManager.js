Ext.define('App.system.VisitModuleManager', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.grid.Panel',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.tree.Panel',
        'Ext.tree.View',
        'Ext.tree.Column'
    ],

    height: 540,
    width: 832,
    layout: 'border',
    header: false,
    defaultListenerScope: true,

    apis: {
        Module: {
            getAllTreeModules: {}
        },
        System: {
            addPageApi: {},
            deletePageApis: {},
            updatePageApi: {},
            getPageApis: {}
        }
    },

    items: [
        {
            xtype: 'treepanel',
            name: 'tree_panel',
            region: 'west',
            split: true,
            // header: false,
            rootVisible: false,
            width: 400,
            title: '模块目录菜单',
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
                            text: '列出目录菜单',
                            listeners: {
                                click: 'onListTreeClick'
                            }
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'gridpanel',
            name: 'access_grid_panel',
            region: 'center',
            apiDelay: true,
            split: true,
            // header: false,
            title: '菜单操作权限',
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'clazz',
                    text: '模块'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'method',
                    text: '资源码'
                },
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'name',
                    text: '说明',
                    renderer: function (v) {
                        if (v) return v;
                        return '[空]';
                    }
                },
                {
                    xtype: 'actioncolumn',
                    text: '操作',
                    items: [
                        {
                            icon: Resource.png('jet', 'delete'),
                            tooltip: '删除操作权限',
                            handler: 'onDeleteRequestClick'
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
                            icon: Resource.png('jet', 'addJdk'),
                            text: '添加操作权限',
                            listeners: {
                                click: 'onAddRequestClick'
                            }
                        },
                        {
                            xtype: 'button',
                            text: '批量删除权限',
                            icon: Resource.png('jet', 'RemoveMulticaret'),
                            listeners: {
                                click: 'onDeleteRequestClick'
                            }
                        }
                    ]
                }
            ]
        }
    ],
    pageFormItems: [
        {
            xtype: 'textfield',
            name: 'moduleId',
            hidden: true,
            fieldLabel: 'ID',
            inputType: 'hidden'
        },
        {
            xtype: 'textfield',
            name: 'page',
            anchor: '100%',
            fieldLabel: '页面名称',
            allowBlank: false
        }
    ],
    requestFormItems: [
        {
            xtype: 'textfield',
            name: 'page',
            anchor: '100%',
            fieldLabel: '页面名称',
            editable: false,
            allowBlank: false
        },
        {
            xtype: 'textfield',
            name: 'clazz',
            anchor: '100%',
            fieldLabel: '模块名称',
            allowBlank: false
        },
        {
            xtype: 'textfield',
            name: 'method',
            anchor: '100%',
            fieldLabel: '资源码',
            allowBlank: false
        }
    ],

    onListTreeClick: function () {
        this.loadTreeData();
    },

    onAddRequestClick: function (button, e, eOpts) {
        var self = this;
        var page = this.find('tree_panel').getIgnoreSelect(arguments);

        if (page) {
            Dialog.openFormWindow({
                title: '添加资源码',
                width: 500,
                height: 240,
                items: self.requestFormItems,
                success: function (json, win) {
                    self.apis.System.addPageApi
                        .wait(self, '正在添加资源码...')
                        .call({object: json}, function () {
                            win.close();
                            self.find('access_grid_panel').refreshStore();
                        });
                }
            }).setValues({page: page['module']});
        } else {
            Dialog.alert('请先选中一个功能菜单后再点击添加!');
        }
    },

    onDeleteRequestClick: function (button, e, eOpts) {
        var jsons = this.find('access_grid_panel').getIgnoreSelects(arguments);
        var self = this;

        Dialog.batch({
            message: '确定删除资源码{d}吗？',
            data: jsons,
            key: 'method',
            callback: function (btn) {
                if (btn == Global.YES) {
                    var ids = Array.splitArray(jsons, 'id');
                    self.apis.System.deletePageApis
                        .wait(self, '正在删除资源码...')
                        .call({ids: ids}, function () {
                            self.find('access_grid_panel').refreshStore();
                        })
                }
            }
        });
    },

    onGridpanelItemClick: function (dataview, record, item, index, e, eOpts) {
        var store = this.apis.System.getPageApis.createListStore({page: record.get('module')});
        this.find('access_grid_panel').setStore(store);
        store.load();
    },

    onAfterApply: function () {
        this.loadTreeData();
    },

    loadTreeData: function () {
        var self = this;
        this.apis.Module.getAllTreeModules
            .wait(self, '正在加载模块...')
            .call({}, function (datas) {
                self.find('tree_panel').setStore(Ext.create('Ext.data.TreeStore', {
                    root: {
                        expanded: true,
                        children: datas
                    }
                }));
            });
    }

});