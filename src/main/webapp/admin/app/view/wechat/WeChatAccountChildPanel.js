Ext.define('App.wechat.WeChatAccountChildPanel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.tree.Panel',
        'Ext.tree.View',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    border: false,
    layout: 'border',
    header: false,
    defaultListenerScope: true,

    items: [
        {
            xtype: 'treepanel',
            name: 'tree',
            region: 'west',
            split: true,
            width: 240,
            header: false,
            border: false,
            rootVisible: false,
            viewConfig: {
                listeners: {
                    itemclick: 'onViewItemClick'
                }
            },
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'back'),
                            text: '返回公众号',
                            listeners: {
                                click: 'onBackClick'
                            }
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'panel',
            name: 'view',
            region: 'center',
            layout: 'fit',
            header: false,
            border: false,
            items: [
                {
                    xtype: 'container',
                    html: '<div style="width: 100%;height: 100%;background: url(/admin/image/wechat/wechat.png) no-repeat center"></div>'
                }
            ]
        }
    ],

    onBackClick: function (button, e, eOpts) {
        this.parent.back();
        if (this._callback) {
            this._callback();
        }
    },

    onViewItemClick: function (dataview, record, item, index, e, eOpts) {
        var module = record.get('module');
        this.setView(module);
    },

    setView: function (module) {
        if (module && module != '') {
            var view = Ext.create(module, {
                accountData: this.accountData,
                openType: this.openType
            });
            if (!view.apis) view.apis = this.apis;
            var panel = this.find('view');
            panel.removeAll();
            panel.add(view);
            if (view.onAfterApply) view.onAfterApply();
        }
    },

    onAfterApply: function () {
        var self = this;
        var tree = this.find('tree');
        this.apis.WeChatPublic.getAccountTrees.call({}, function (data) {
            var store = Ext.create('Ext.data.TreeStore', {
                defaultRootId: '0',
                root: {
                    expanded: true,
                    text: "公众号功能",
                    children: data
                }
            });
            tree.setStore(store);

            if (data.length > 0) {
                self.setView(data[0]['module']);
            }
        });
    }

});