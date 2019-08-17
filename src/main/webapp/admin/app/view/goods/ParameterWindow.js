Ext.define('App.goods.ParameterWindow', {
    extend: 'Ext.window.Window',

    height: 600,
    width: 800,
    layout: 'fit',
    title: '属性列表池',
    defaultListenerScope: true,

    items: [
        {
            xtype: 'gridpanel',
            name: 'param_grid',
            header: false,
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'name',
                    width: 200,
                    text: '名称'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'type',
                    text: '类型',
                    renderer: function (v) {
                        if (v == 0) {
                            return '输入类型';
                        } else if (v == 1) {
                            return '多项选择';
                        } else if (v == 2) {
                            return '单项选择';
                        } else if (v == 4) {
                            return '输入URL地址';
                        }
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'isPrimary',
                    text: '是否规格属性',
                    renderer: function (value) {
                        if (value && value == 1) return "<span style='color: #61a4ff;font-weight: bold'>是</span>";
                        else return "否";
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'isColor',
                    text: '是否颜色属性',
                    renderer: function (value) {
                        if (value && value == 1) return "<span style='color: #61a4ff;font-weight: bold'>是</span>";
                        else return "否";
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'pos',
                    text: '排序'
                },
                {
                    xtype: 'actioncolumn',
                    width: 80,
                    text: '操作',
                    items: [
                        {
                            icon: Resource.png('jet', 'selectall_dark'),
                            tooltip: '选择属性',
                            handler: 'onSelectParamClick'
                        }
                    ]
                }
            ],
            listeners: {
                itemclick: 'onParamItemClick'
            },
            dockedItems: [
                {
                    xtype: 'pagingtoolbar',
                    dock: 'bottom',
                    displayInfo: true
                },
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'list'),
                            text: '列出属性列表',
                            listeners: {
                                click: 'onListClick'
                            }
                        },
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'addFolder'),
                            text: '选择并添加到属性组',
                            listeners: {
                                click: 'onSelectParamClick'
                            }
                        },
                        '->',
                        {
                            xtype: 'textfield',
                            name: 'keyword',
                            emptyText: '关键字'
                        },
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'search_dark'),
                            text: '搜索',
                            listeners: {
                                click: 'onSearchClick'
                            }
                        }
                    ]
                }
            ],
            selModel: {
                selType: 'checkboxmodel'
            }
        }
    ],

    onInit: function () {
        var store = this.apis.GoodsParameter.getParameterList.createPageStore();
        this.find('param_grid').setStore(store);
        store.load();
    },

    onListClick: function () {
        this.onInit();
    },

    onSearchClick: function () {
        var keyword = this.find('keyword').getValue();
        var store = this.apis.GoodsParameter.getParameterList.createPageStore({keyword: keyword});
        this.find('param_grid').setStore(store);
        store.load();
    },

    onSelectParamClick: function () {
        var datas = this.find('param_grid').getIgnoreSelects(arguments);
        if (datas) {
            var self = this;
            var ids = Array.splitArray(datas, "id");
            this.apis.GoodsParameter.relParameters
                .wait(self, '正在添加属性...')
                .call({gid: this._gid, ids: ids}, function () {
                    self.close();
                    if (self._callback) {
                        self._callback(datas);
                    }
                });
        }
    }
});
