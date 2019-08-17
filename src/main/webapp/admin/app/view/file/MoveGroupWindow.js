Ext.define('App.file.MoveGroupWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.tree.Panel',
        'Ext.tree.View',
        'Ext.tree.Column'
    ],

    height: 500,
    width: 400,
    layout: 'fit',
    title: '选择要移动的分组',
    defaultListenerScope: true,

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            items: [
                '->',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'MoveTo2_dark'),
                    text: '移动文件',
                    listeners: {
                        click: 'onButtonClick'
                    }
                },
                '->'
            ]
        }
    ],
    items: [
        {
            xtype: 'treepanel',
            name: 'tree_panel',
            border: false,
            header: false,
            viewConfig: {},
            columns: [
                {
                    xtype: 'treecolumn',
                    dataIndex: 'groupName',
                    text: '组名称',
                    flex: 1
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'pos',
                    text: '排序'
                }
            ]
        }
    ],

    onButtonClick: function (button, e, eOpts) {
        var json = this.find('tree_panel').getIgnoreSelect(arguments);
        if (this._callback) {
            this._callback(json);
            this.close();
        }
    },

    setStore: function (store) {
        this.find('tree_panel').setStore(store);
    }

});