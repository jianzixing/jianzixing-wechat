Ext.define('App.message.MessageAdmin', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.button.Button',
        'Ext.grid.Panel',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'Ext.toolbar.Paging'
    ],

    height: 600,
    width: 800,
    layout: 'fit',
    title: '选择要发送的管理员',
    defaultListenerScope: true,

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'top',
            items: [
                '->',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'send'),
                    text: '发送选中的人',
                    listeners: {
                        click: 'onSendSelectClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'sendAll'),
                    text: '发送给所有人',
                    listeners: {
                        click: 'onSendAllClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'cancel'),
                    text: '取消发送',
                    listeners: {
                        click: 'onCancelClick'
                    }
                }
            ]
        }
    ],
    items: [
        {
            xtype: 'gridpanel',
            name: 'grid',
            border: false,
            header: false,
            columns: [
                {
                    xtype: 'gridcolumn',
                    width: 100,
                    dataIndex: 'userName',
                    text: '用户名'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'realName',
                    text: '姓名'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'jobNumber',
                    text: '工号'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'checkInTime',
                    text: '入职时间',
                    width: 120,
                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                        if (!value || value === "") {
                            if (!value || value == "") {
                                return "<span color='#333333'>[无]</span>";
                            }
                            else {
                                return value;
                            }
                        } else {
                            return new Date(value).format("yyyy-MM-dd");
                        }
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'status',
                    text: '员工状态',
                    renderer: function (value) {
                        value = parseInt(value);
                        if (value == 0) {
                            return "试用期";
                        } else if (value == 1) {
                            return "正常";
                        } else if (value == 2) {
                            return "离职";
                        }
                    }
                },
                {
                    xtype: 'gridcolumn',
                    width: 120,
                    dataIndex: 'phoneNumber',
                    text: '联系电话'
                },
                {
                    xtype: 'gridcolumn',
                    width: 80,
                    dataIndex: 'TableAdminDepartment',
                    text: '部门',
                    renderer: function (value) {
                        if (value) {
                            return value['name'];
                        }
                    }
                },
                {
                    xtype: 'gridcolumn',
                    width: 80,
                    dataIndex: 'TableAdminPosition',
                    text: '职位',
                    renderer: function (value) {
                        if (value) {
                            return value['name'];
                        }
                    }
                },
                {
                    xtype: 'gridcolumn',
                    width: 120,
                    dataIndex: 'TableRoles',
                    text: '角色',
                    renderer: function (value) {
                        if (value) {
                            return value['roleName']
                        }
                    }
                },
                {
                    xtype: 'gridcolumn',
                    width: 100,
                    dataIndex: 'extension',
                    text: '分机号'
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
            ],
            selModel: {
                selType: 'checkboxmodel'
            }
        }
    ],

    onSendSelectClick: function (button, e, eOpts) {
        var jsons = this.find('grid').getIgnoreSelects(arguments);
        var ids = Array.splitArray(jsons, "id");
        this.close();
        Dialog.openWindow('App.message.SystemMessageWindow', {
            apis: this.apis,
            _adminIds: ids,
            _sendSelects: this._sendSelects,
            _sendAll: this._sendAll
        }).setToAdmins(jsons);
    },

    onSendAllClick: function (button, e, eOpts) {
        this.close();
        Dialog.openWindow('App.message.SystemMessageWindow', {
            apis: this.apis,
            _adminSendAll: true,
            _sendSelects: this._sendSelects,
            _sendAll: this._sendAll
        }).setToAll();
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    onLoadAdmins: function () {
        var store = this.apis.Admin.getAdmins.createPageStore();
        this.find('grid').setStore(store);
        this.find('paging').bindStore(store);
        store.load();
    }

});