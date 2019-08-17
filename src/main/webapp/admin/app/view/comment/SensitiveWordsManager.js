Ext.define('App.comment.SensitiveWordsManager', {
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
        SensitiveWords: {
            addWord: {},
            delWord: {},
            updateWord: {},
            getWords: {},
            setEnable: {},
            setDisable: {}
        }
    },

    columns: [
        {
            xtype: 'gridcolumn',
            dataIndex: 'id',
            text: '敏感字ID'
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'text',
            text: '敏感字'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'isEnable',
            text: '是否启用',
            renderer: function (v) {
                if (v == 1) {
                    return '<span style="color: green">启用</span>';
                } else {
                    return '<span style="color: red">禁用</span>';
                }
            }
        },
        {
            xtype: 'actioncolumn',
            text: '操作',
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
                    icon: Resource.png('jet', 'listChanges'),
                    text: '列出敏感字',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addFavoritesList'),
                    text: '添加敏感字',
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '批量删除敏感字',
                    listeners: {
                        click: 'onDelClick'
                    }
                },
                '-',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '批量启用',
                    listeners: {
                        click: 'onEnableClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '批量禁用',
                    listeners: {
                        click: 'onDisableClick'
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

    onEnableClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.getIgnoreSelects(arguments);

        if (jsons) {
            Dialog.batch({
                message: '确定启用敏感字{d}吗？',
                data: jsons,
                key: 'text',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, "id");
                        self.apis.SensitiveWords.setEnable
                            .wait(self, '正在启用敏感字...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            })
                    }
                }
            });
        } else {
            Dialog.alert('请先选中要启用的敏感字!');
        }
    },

    onDisableClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.getIgnoreSelects(arguments);

        if (jsons) {
            Dialog.batch({
                message: '确定启用禁用字{d}吗？',
                data: jsons,
                key: 'text',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, "id");
                        self.apis.SensitiveWords.setDisable
                            .wait(self, '正在启用禁用字...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            })
                    }
                }
            });
        } else {
            Dialog.alert('请先选中要启用的禁用字!');
        }
    },

    onListClick: function (button, e, eOpts) {
        this.refreshStore();
    },

    onAddClick: function (button, e, eOpts) {
        var self = this;

        Dialog.openWindow('App.comment.SensitiveWordsWindow', {
            apis: self.apis,
            _callback: function () {
                self.refreshStore();
            }
        })
    },

    onUpdateClick: function (button, e, eOpts) {
        var self = this;
        var data = self.getIgnoreSelect(arguments);

        Dialog.openWindow('App.comment.SensitiveWordsWindow', {
            apis: self.apis,
            _callback: function () {
                self.refreshStore();
            }
        }).setValue(data)
    },

    onDelClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.getIgnoreSelects(arguments);

        if (jsons) {
            Dialog.batch({
                message: '确定删除敏感字{d}吗？',
                data: jsons,
                key: 'text',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, "id");
                        self.apis.SensitiveWords.delWord
                            .wait(self, '正在删除敏感字...')
                            .call({ids: ids}, function () {
                            self.refreshStore();
                        })
                    }
                }
            });
        } else {
            Dialog.alert('请先选中要删除的敏感字!');
        }
    },

    onAfterApply: function () {
        var store = this.apis.SensitiveWords.getWords.createPageStore();
        this.setStore(store);
        this.find('paging').bindStore(store);
        store.load();
    }

});