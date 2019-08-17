Ext.define('App.hotsearch.HotSearchManager', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.view.Table',
        'Ext.button.Button',
        'Ext.toolbar.Paging',
        'Ext.selection.CheckboxModel'
    ],

    border: false,
    header: false,
    defaultListenerScope: true,

    apis: {
        HotSearch: {
            getHotSearch: {},
            addHotSearch: {},
            delHotSearch: {},
            updateHotSearch: {}
        }
    },

    columns: [
        {
            xtype: 'gridcolumn',
            dataIndex: 'type',
            text: '类型',
            renderer: function (v) {
                if (v == 'wx') return "微信页面";
                if (v == 'pc') return "电脑页面";
                return v;
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'name',
            width: 150,
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
            dataIndex: 'id',
            align: 'center',
            items: [
                {
                    tooltip: '修改',
                    iconCls: "x-fa fa-pencil green",
                    handler: 'onUpdateClick'
                },
                {
                    iconCls: "x-fa fa-times red",
                    tooltip: '删除',
                    handler: 'onDeleteClick'
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
                    icon: Resource.png('jet', 'listChanges'),
                    text: '列出热门搜索',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addFavoritesList'),
                    text: '添加热门搜索',
                    listeners: {
                        click: 'onAddClick'
                    }
                }
            ]
        },
        {
            xtype: 'pagingtoolbar',
            dock: 'bottom',
            width: 360,
            displayInfo: true
        }
    ],
    selModel: {
        selType: 'checkboxmodel'
    },

    onListClick: function (button, e, eOpts) {
        this.onAfterApply();
    },

    onAddClick: function (button, e, eOpts) {
        var self = this;
        var win = Dialog.openWindow('App.hotsearch.HotSearchWindow', {
            apis: this.apis,
            _callback: function () {
                self.refreshStore();
            }
        });
    },

    onUpdateClick: function () {
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var self = this;
            var win = Dialog.openWindow('App.hotsearch.HotSearchWindow', {
                apis: this.apis,
                _callback: function () {
                    self.refreshStore();
                }
            });
            win.setValue(data);
        }
    },

    onDeleteClick: function () {
        var self = this;
        var jsons = this.getIgnoreSelects(arguments);
        if (jsons != null) {
            Dialog.batch({
                message: '确定删除热门搜索{d}吗？',
                data: jsons,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, "id");
                        self.apis.HotSearch.delHotSearch
                            .wait(self, '正在删除热门搜索...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            })
                    }
                }
            });
        } else {
            Dialog.alert('提示', '请先选中至少一条热门搜索后再删除!');
        }
    },

    onAfterApply: function () {
        var store = this.apis.HotSearch.getHotSearch.createPageStore();
        this.setStore(store);
        store.load();
    }

});
