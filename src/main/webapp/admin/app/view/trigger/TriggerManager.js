Ext.define('App.trigger.TriggerManager', {
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
    header: false,
    defaultListenerScope: true,
    apis: {
        Trigger: {
            getTriggers: {},
            getEvents: {},
            addTrigger: {},
            updateTrigger: {},
            deleteTriggers: {},
            getProcessorImpls: {}
        },
        Email: {
            getEnableEmails: {}
        },
        Sms: {
            getEnableSms: {}
        }
    },

    columns: [
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'name',
            text: '触发器名称'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'eventName',
            text: '事件名称'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'timeType',
            text: '触发时间',
            renderer: function (v, mate, record) {
                var useRule = record.get('useRule');
                if (useRule == 0) {
                    return '[不限制]';
                } else {
                    var totalCount = record.get('totalCount');
                    var amount = record.get('triggerCount');
                    if (v == 1) {
                        return '每年触发' + amount + '次,最多' + totalCount + '次';
                    }
                    if (v == 2) {
                        return '每月触发' + amount + '次,最多' + totalCount + '次';
                    }
                    if (v == 3) {
                        return '每天触发' + amount + '次,最多' + totalCount + '次';
                    }
                    if (v == 4) {
                        return '每时触发' + amount + '次,最多' + totalCount + '次';
                    }
                    if (v == 5) {
                        return '每分触发' + amount + '次,最多' + totalCount + '次';
                    }
                    if (v == 6) {
                        return '每秒触发' + amount + '次,最多' + totalCount + '次';
                    }
                    if (v == 7) {
                        return '总共触发' + amount + '次,最多' + totalCount + '次';
                    }
                    return '永久';
                }
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'triggerCount',
            text: '触发次数',
            renderer: function (v, mate, record) {
                var useRule = record.get('useRule');
                if (useRule == 0) {
                    return '[不限制]';
                } else {
                    var triggerInfinite = record.get('triggerInfinite');
                    if (triggerInfinite == 1) {
                        return '不限次数';
                    }
                    return v + ' 次';
                }
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'startTime',
            text: '开始时间',
            renderer: function (v) {
                if (v) {
                    return new Date(v).format();
                }
                return '[不限制]';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'finishTime',
            text: '结束时间',
            renderer: function (v) {
                if (v) {
                    return new Date(v).format();
                }
                return '[不限制]';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'enable',
            text: '是否启用',
            renderer: function (v) {
                if (v == 1) {
                    return '<span style="color: green;">启用</span>'
                }
                return '<span style="color: red;">禁用</span>'
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'createTime',
            text: '创建时间',
            renderer: function (v) {
                if (v) {
                    return new Date(v).format();
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
                    icon: Resource.png('jet', 'listChanges'),
                    text: '列出触发器',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addFavoritesList'),
                    text: '添加触发器',
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '批量删除触发器',
                    listeners: {
                        click: 'onDelClick'
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
        this.refreshStore();
    },

    onAddClick: function (button, e, eOpts) {
        var self = this;
        var tab = self.parent.forward('App.trigger.TriggerPanel', {
            apis: this.apis,
            _callback: function () {
                self.refreshStore();
            }
        });
        tab.setInit();
    },

    onUpdateClick: function () {
        var self = this;
        var data = self.getIgnoreSelect(arguments);
        if (data) {
            var tab = self.parent.forward('App.trigger.TriggerPanel', {
                apis: self.apis,
                _callback: function () {
                    self.refreshStore();
                }
            });
            tab.setValue(data);
            tab.setInit();
        } else {
            Dialog.alert('请先选中一条数据后再修改！');
        }
    },

    onDelClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            Dialog.batch({
                message: '确定删除触发器{d}吗？',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(datas, "id");
                        self.apis.Trigger.deleteTriggers
                            .wait(self, '正在删除触发器...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            })
                    }
                }
            });
        } else {
            Dialog.alert('请先选中要删除的触发器！')
        }
    },

    onAfterApply: function () {
        var store = this.apis.Trigger.getTriggers.createPageStore();
        this.setStore(store);
        store.load();
    }

});
