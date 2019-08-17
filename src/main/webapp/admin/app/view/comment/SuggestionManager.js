Ext.define('App.comment.SuggestionManager', {
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
        Suggestion: {
            getSuggestions: {},
            delSuggestions: {},
            sendReply: {}
        }
    },

    columns: [
        {
            xtype: 'gridcolumn',
            dataIndex: 'id',
            text: '意见ID'
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'text',
            text: '意见内容'
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'reply',
            text: '回复意见',
            renderer: function (v) {
                if (!v) {
                    return '<span style="color: #00a0e9">暂未回复</span>';
                } else {
                    return '<span style="color: #00a0e9">' + v + '</span>';
                }
            }
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'TableUser',
            text: '用户',
            renderer: function (v) {
                if (v) {
                    return v['userName']
                }
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'qq',
            text: 'QQ号码'
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'email',
            text: '邮箱'
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'phone',
            text: '手机号码'
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'type',
            text: '意见类型',
            renderer: function (v) {
                if (v == 'DEFAULT') {
                    return '意见建议';
                }
                if (v == 'BUG') {
                    return '程序BUG'
                }
                return v;
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'createTime',
            text: '创建时间',
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
            items: [
                {
                    iconCls: "x-fa fa-file-text green",
                    tooltip: '查看详情',
                    handler: 'onDetailClick'
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
                    text: '列出意见',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '批量删除意见',
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

    onDetailClick: function (button, e, eOpts) {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            if (data['createTime']) {
                var d = new Date(data['createTime']);
                data['ctime'] = d.format();
            }
            data['userName'] = data['TableUser'] ? data['TableUser']['userName'] : '';
            Dialog.openWindow('App.comment.SuggestionListWindow', {
                apis: this.apis,
                _sid: data['id'],
                _callback: function () {
                    self.refreshStore();
                }
            }).setValue(data);
        } else {
            Dialog.alert('请先选中数据后再查看详情!');
        }
    },

    onDelClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.getIgnoreSelects(arguments);

        if (jsons) {
            Dialog.batch({
                message: '确定删除意见{d}吗？',
                data: jsons,
                key: 'text',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, "id");
                        self.apis.SensitiveWords.delWord
                            .wait(self, '正在删除意见...')
                            .call({ids: ids}, function () {
                            self.refreshStore();
                        })
                    }
                }
            });
        } else {
            Dialog.alert('请先选中要删除的意见!');
        }
    },

    onAfterApply: function () {
        var store = this.apis.Suggestion.getSuggestions.createPageStore();
        this.setStore(store);
        this.find('paging').bindStore(store);
        store.load();
    }

});