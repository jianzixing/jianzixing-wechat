Ext.define('App.system.DepartmentManager', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.view.Table',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    header: false,
    defaultListenerScope: true,

    apis: {
        Admin: {
            getDepartment: {},
            addDepartment: {},
            deleteDepartment: {},
            updateDepartment: {}
        }
    },

    columns: [
        {
            xtype: 'gridcolumn',
            width: 160,
            dataIndex: 'name',
            text: '部门名称'
        },
        {
            xtype: 'gridcolumn',
            width: 300,
            dataIndex: 'detail',
            text: '部门描述'
        },
        {
            xtype: 'actioncolumn',
            text: '操作',
            items: [
                {
                    iconCls: 'x-fa fa-pencil green',
                    tooltip: '更新部门',
                    handler: 'onUpdateClick'
                },
                {
                    iconCls: 'x-fa fa-times red',
                    tooltip: '删除部门',
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
                    icon: Resource.png('jet', 'list'),
                    text: '列出部门',
                    listeners: {
                        click: 'onListRoleClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addFavoritesList'),
                    text: '添加部门',
                    listeners: {
                        click: 'onButtonClick'
                    }
                }
            ]
        }
    ],

    formWindow: [
        {
            xtype: 'textfield',
            name: 'id',
            hidden: true,
            fieldLabel: 'ID',
            inputType: 'hidden'
        },
        {
            xtype: 'textfield',
            name: 'name',
            anchor: '100%',
            fieldLabel: '名称',
            allowBlank: false
        },
        {
            xtype: 'textareafield',
            name: 'detail',
            anchor: '100%',
            fieldLabel: '描述'
        }
    ],

    onListRoleClick: function () {
        this.refreshStore();
    },

    onButtonClick: function (button, e, eOpts) {
        var self = this;
        Dialog.openFormWindow({
            title: '添加部门',
            width: 433,
            height: 310,
            items: self.formWindow,
            success: function (json, win) {
                self.apis.Admin.addDepartment
                    .wait(self, '正在添加部门...')
                    .call({object: json}, function () {
                        win.close();
                        self.refreshStore();
                    });
            }
        });
    },

    onUpdateClick: function () {
        var json = this.getIgnoreSelect(arguments);
        var self = this;
        Dialog.openFormWindow({
            title: '修改部门',
            width: 433,
            height: 310,
            items: self.formWindow,
            success: function (json, win) {
                self.apis.Admin.updateDepartment
                    .wait(self, '正在修改部门...')
                    .call({object: json}, function () {
                        win.close();
                        self.refreshStore();
                    });
            }
        }).setValues(json);
    },

    onDeleteClick: function () {
        var json = this.getIgnoreSelect(arguments);
        var self = this;
        Dialog.batch({
            message: '确定删除部门{d}吗？',
            data: json,
            key: 'name',
            callback: function (btn) {
                if (btn == Global.YES) {
                    self.apis.Admin.deleteDepartment
                        .wait(self, '正在删除部门...')
                        .call({id: json['id']}, function () {
                            self.refreshStore()
                        })
                }
            }
        });
    },

    onAfterApply: function () {
        var store = this.apis.Admin.getDepartment.createListStore();
        this.setStore(store);
        store.load();
    }

});
