Ext.define('App.system.SelectModuleWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.tree.Panel',
        'Ext.tree.View',
        'Ext.tree.Column',
        'Ext.selection.CheckboxModel',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 500,
    width: 600,
    layout: 'fit',
    title: '选择目录菜单',
    defaultListenerScope: true,

    items: [
        {
            xtype: 'treepanel',
            name: 'tree_panel',
            border: false,
            height: 250,
            width: 400,
            header: false,
            rootVisible: false,
            expandAll: true,
            viewConfig: {
                getRowClass: function (record, rowIndex, rowParams, store) {
                    return 'x-grid-record-white';
                }
            },
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
                }
            ],
            selModel: {
                selType: 'checkboxmodel',
                checkOnly: true
            }
        }
    ],
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            items: [
                '->',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'menu-saveall'),
                    text: '确认',
                    listeners: {
                        click: 'onOkClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'closeHover'),
                    text: '取消',
                    listeners: {
                        click: 'onCancelClick'
                    }
                },
                '->'
            ]
        }
    ],

    onOkClick: function (button, e, eOpts) {
        var self = this;
        var datas = self.find('tree_panel').getSelects();
        this.close();
        if (this._callback) this._callback(datas);
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    onShow: function () {
        this.callParent(arguments);
        var self = this;
        self.apis.Module.getAllTreeModules
            .wait(self, '正在获取全部模块...')
            .call({}, function (datas) {
                self.find('tree_panel').setStore(Ext.create('Ext.data.TreeStore', {
                    root: {
                        expanded: true,
                        children: datas
                    }
                }));

                self._checked();
            });
    },

    setValues: function (ds) {
        this._datas = ds;
        this._checked();
    },

    _checked: function () {
        var tp = this.find('tree_panel');
        tp.selectCheckBoxModel(this._datas, 'id');
    }

});