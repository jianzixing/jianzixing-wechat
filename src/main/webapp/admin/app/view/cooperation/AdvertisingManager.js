Ext.define('App.cooperation.AdvertisingManager', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'Ext.button.Button',
        'Ext.toolbar.Paging',
        'Ext.selection.CheckboxModel'
    ],

    border: false,
    defaultListenerScope: true,
    apis: {
        Advertising: {
            addAdvertising: {},
            delAdvertising: {},
            updateAdvertising: {},
            getAdvertisings: {}
        }
    },

    columns: [
        {
            xtype: 'gridcolumn',
            dataIndex: 'id',
            text: '广告ID'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'name',
            text: '广告名称'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'code',
            text: '广告码'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'cover',
            text: '广告图片',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (!value || value == "") {
                    value = Resource.create('/admin/image/exicon/nopic_60.gif');
                } else {
                    value = Resource.image(value);
                }
                var width = 60;
                var height = 60;
                return '<div style="height: ' + height + 'px;width: ' + width + 'px;vertical-align: middle;display:table-cell;">' +
                    '<img style="max-height: ' + height + 'px;max-width: ' + width + 'px;vertical-align: middle" src=' + value + '></div> ';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'url',
            text: '链接地址',
            renderer: function (v) {
                if (v) {
                    return v;
                }
                return '<span style="color: #902b2b">未设置</span>';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'script',
            text: '脚本链接',
            renderer: function (v) {
                if (v) {
                    return v;
                }
                return '未设置';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'enable',
            text: '是否有效',
            renderer: function (v) {
                if (v == 1) {
                    return '有效';
                } else {
                    return '<span style="color: red">失效</span>'
                }
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'createTime',
            text: '创建日期',
            renderer: function (value) {
                if (value) {
                    var d = new Date(value);
                    return d.format();
                }
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'modifiedTime',
            text: '上次修改',
            renderer: function (value) {
                if (value) {
                    var d = new Date(value);
                    return d.format();
                }
                return '';
            }
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
                    handler: 'onDelClick'
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
                    icon: Resource.png('jet', 'list'),
                    text: '列出广告',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addJira'),
                    text: '添加广告',
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '批量删除广告',
                    listeners: {
                        click: 'onDelClick'
                    }
                }
            ]
        },
        {
            xtype: 'pagingtoolbar',
            name: 'paging',
            dock: 'bottom',
            width: 360,
            displayInfo: true
        }
    ],
    selModel: {
        selType: 'checkboxmodel'
    },

    onListClick: function (button, e, eOpts) {
        this.refreshStore();
    },

    onAddClick: function (button, e, eOpts) {
        var self = this;

        Dialog.openWindow('App.cooperation.ADWindow', {
            apis: self.apis,
            _callback: function () {
                self.refreshStore();
            }
        })
    },

    onDelClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.getIgnoreSelects(arguments);

        if (jsons != null) {
            Dialog.batch({
                message: '确定删除广告{d}吗？',
                data: jsons,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, "id");
                        self.apis.Advertising.delAdvertising
                            .wait(self, '正在删除广告...')
                            .call({ids: ids}, function () {
                            self.refreshStore();
                        })
                    }
                }
            });
        } else {
            Dialog.alert('提示', '请先选中一条广告数据后再删除!');
        }
    },

    onUpdateClick: function (button, e, eOpts) {
        var self = this;
        var json = this.getIgnoreSelect(arguments);

        if (json) {
            Dialog.openWindow('App.cooperation.ADWindow', {
                apis: self.apis,
                _callback: function () {
                    self.refreshStore();
                }
            }).setValue(json)
        } else {
            Dialog.alert('提示', '请先选中一条广告数据后再修改!');
        }
    },

    onAfterApply: function () {
        var store = this.apis.Advertising.getAdvertisings.createPageStore();
        this.setStore(store);
        this.find('paging').bindStore(store);
        store.load();
    }

});