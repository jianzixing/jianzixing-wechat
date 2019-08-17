Ext.define('App.goods.SelectBrandWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.grid.Panel',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.view.Table'
    ],

    height: 500,
    width: 800,
    layout: 'fit',
    title: '选择商品品牌',
    defaultListenerScope: true,

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
    items: [
        {
            xtype: 'gridpanel',
            name: 'grid',
            border: false,
            header: false,
            forceFit: true,
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'name',
                    text: '名称'
                },
                {
                    xtype: 'gridcolumn',
                    width: 120,
                    dataIndex: 'logo',
                    text: '品牌图片',
                    renderer: function (v) {
                        return '<div style="height: 25px">' +
                            (v != null ? '<img style="height: 25px;width:90px" src="' + Resource.image(v) + '"/>' : '') +
                            '</div>'
                    }
                },
                {
                    xtype: 'gridcolumn',
                    width: 390,
                    dataIndex: 'detail',
                    text: '描述'
                },
                {
                    xtype: 'actioncolumn',
                    dataIndex: 'id',
                    text: '操作',
                    align: 'center',
                    items: [
                        {
                            icon: Resource.png('jet', 'rightDiff'),
                            tooltip: '选择商品品牌',
                            handler: 'onSelectClick'
                        }
                    ]
                }
            ],
            dockedItems: [
                {
                    xtype: 'pagingtoolbar',
                    dock: 'bottom',
                    width: 360,
                    displayInfo: true
                }
            ]
        }
    ],

    onSelectClick: function (button, e, eOpts) {
        var jsons = this.find('grid').getIgnoreSelects(arguments);
        this.close();
        if (this.callback) {
            this.callback(jsons);
        }
    },

    onCloseClick: function (button, e, eOpts) {
        this.close();
    },

    initSelect: function () {
        var store = this.apis.Brand.getBrands.createPageStore();
        this.find('grid').setStore(store);
        store.load();
    }

});