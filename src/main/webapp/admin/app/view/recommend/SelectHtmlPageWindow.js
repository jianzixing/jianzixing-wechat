Ext.define('App.recommend.SelectHtmlPageWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.grid.Panel',
        'Ext.grid.column.RowNumberer',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 700,
    width: 800,
    layout: 'fit',
    title: '选择链接页面',
    defaultListenerScope: true,

    items: [
        {
            xtype: 'gridpanel',
            name: 'grid',
            border: false,
            columns: [
                {
                    xtype: 'gridcolumn',
                    width: 160,
                    dataIndex: 'name',
                    text: '网页名称'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'TablePageTemplate',
                    text: '所属模板',
                    renderer: function (v) {
                        if (v) {
                            return v['name'];
                        }
                        return '';
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'type',
                    text: '网页类型',
                    renderer: function (v) {
                        if (v == 10) {
                            return "HTML文件"
                        } else if (v == 20) {
                            return "HTML片段";
                        } else if (v == 30) {
                            return "网页设计器";
                        }
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'url',
                    text: 'URL地址'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'isHome',
                    text: '主页',
                    renderer: function (v, mate, record) {
                        if (v == 1) {
                            return '<span style="color:#ff8744">电脑端主页</span>';
                        } else if (v == 2) {
                            return '<span style="color:#369917">手机端主页</span>';
                        } else {
                            return '非主页';
                        }
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'cacheTime',
                    text: '缓存时间',
                    renderer: function (v) {
                        if (v == 0) {
                            return '不缓存';
                        } else if (v > 0) {
                            return v + '秒';
                        } else {
                            return '永久缓存';
                        }
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'pos',
                    text: '排序'
                },
                {
                    xtype: 'gridcolumn',
                    hidden: true,
                    dataIndex: 'createTime',
                    text: '创建时间'
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
                    icon: Resource.png('jet', 'inspectionsTypos'),
                    text: '确定选择',
                    listeners: {
                        click: 'onSaveClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'cancel'),
                    text: '取消',
                    listeners: {
                        click: 'onCancelClick'
                    }
                },
                '->'
            ]
        },
        {
            xtype: 'toolbar',
            dock: 'top',
            items: [
                '->',
                {
                    xtype: 'textfield',
                    name: 'keyword',
                    emptyText: '输入关键字'
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'search'),
                    text: '搜索',
                    listeners: {
                        click: 'loadValue'
                    }
                }
            ]
        }
    ],

    onSaveClick: function (button, e, eOpts) {
        var data = this.find('grid').getIgnoreSelect(arguments);
        this.close();
        if (this._callback) {
            this._callback(data);
        }
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    loadValue: function () {
        var keyword = this.find('keyword').getValue();
        var dt = {};
        if (keyword && keyword != '') {
            dt['search'] = [{name: 'name', symbol: 'like', value: '%' + keyword + '%'}];
        }
        var store = this.apis.HtmlPage.getFullHtmlPage.createPageStore(dt);
        this.find('grid').setStore(store);
        this.find('paging').bindStore(store);
        store.load();
    }

});