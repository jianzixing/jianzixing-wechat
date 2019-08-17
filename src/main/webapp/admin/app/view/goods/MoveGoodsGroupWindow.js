Ext.define('App.goods.MoveGoodsGroupWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.tree.Panel',
        'Ext.tree.View',
        'Ext.tree.Column',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 450,
    width: 600,
    layout: 'fit',
    title: '选择商品分组',
    defaultListenerScope: true,

    items: [
        {
            xtype: 'treepanel',
            name: 'tree',
            border: false,
            header: false,
            forceFit: true,
            columns: [
                {
                    xtype: 'treecolumn',
                    dataIndex: 'name',
                    text: '分组名称',
                    width: 150
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'detail',
                    text: '描述',
                    width: 200
                },
                {
                    xtype: 'actioncolumn',
                    dataIndex: 'id',
                    text: '操作',
                    align: 'center',
                    items: [
                        {
                            icon: Resource.png('jet', 'rightDiff'),
                            tooltip: '移动商品分类',
                            handler: 'onSelectClick'
                        }
                    ]
                }
            ]
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
                    text: '确定选择',
                    icon: Resource.png('jet', 'selectall'),
                    listeners: {
                        click: 'onSelectClick'
                    }
                },
                {
                    xtype: 'button',
                    text: '取消关闭',
                    icon: Resource.png('jet', 'cancel'),
                    listeners: {
                        click: 'onCloseClick'
                    }
                },
                '->'
            ]
        }
    ],

    onSelectClick: function (button, e, eOpts) {
        var jsons = this.find('tree').getIgnoreSelects(arguments);
        this.close();
        if (this.callback) {
            this.callback(jsons);
        }
    },

    onCloseClick: function (button, e, eOpts) {
        this.close();
    },

    initGroupWindow: function () {
        var self = this;
        self.apis.GoodsGroup.getGoodsGroups
            .wait(self.find('tree'), '正在加载商品分类...')
            .call({}, function (jsons) {
                var store = Ext.create('Ext.data.TreeStore', {
                    defaultRootId: '0',
                    root: {
                        expanded: true,
                        name: "商品分类管理",
                        children: jsons
                    }
                });
                self.find('tree').setStore(store);
            })
    }

});
