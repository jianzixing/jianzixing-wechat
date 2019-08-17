Ext.define('App.system.RobotsManager', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.selection.CheckboxModel'
    ],

    border: false,
    defaultListenerScope: true,
    apis: {
        Robots: {
            addRobots: {},
            delRobots: {},
            updateRobots: {},
            getRobots: {},
            updateRobotsPos: {}
        }
    },
    api: {Robots: {getRobots: {_page: 'App.system.RobotsManager'}}},

    plugins: [
        {
            ptype: 'cellediting',
            clicksToEdit: 1,
            listeners: {
                edit: 'onCellEditingEdit'
            }
        }
    ],
    columns: [
        {
            xtype: 'gridcolumn',
            dataIndex: 'id',
            text: 'ID'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'cmd',
            text: 'Robots指令'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'value',
            text: 'Robots条件'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'pos',
            text: '排序',
            field: {
                xtype: 'textfield',
                selectOnFocus: true
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
                    text: '列出Robots协议',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addIcon'),
                    text: '添加Robots协议',
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '批量删除Robots协议',
                    listeners: {
                        click: 'onDelClick'
                    }
                }
            ]
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
        Dialog.openWindow('App.system.RobotsWindow', {
            apis: this.apis,
            _callback: function () {
                self.refreshStore();
            }
        });
    },

    onUpdateClick: function (button, e, eOpts) {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        Dialog.openWindow('App.system.RobotsWindow', {
            apis: this.apis,
            _callback: function () {
                self.refreshStore();
            }
        }).setValue(data);
    },

    onDelClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.getIgnoreSelects(arguments);

        Dialog.batch({
            message: '确定删除Robots{d}吗？',
            data: jsons,
            key: 'id',
            callback: function (btn) {
                if (btn == Global.YES) {
                    var ids = Array.splitArray(jsons, "id");
                    self.apis.Robots.delRobots
                        .wait(self, '正在删除Robots...')
                        .call({ids: ids}, function () {
                            self.refreshStore();
                        })
                }
            }
        });
    },

    onCellEditingEdit: function (editor, context, eOpts) {
        var self = this;
        var pos = context.record.get("pos");
        var id = context.record.get("id");
        if (!isNaN(pos)) {
            this.apis.Robots.updateRobotsPos
                .wait(self, '正在更新排序,请稍后...')
                .call({id: id, pos: pos}, function () {
                    context.record.commit();
                    self.refreshStore();
                });
        } else {
            Dialog.alert('排序列需要填写数字,不能包含其他字符')
        }
    }

});