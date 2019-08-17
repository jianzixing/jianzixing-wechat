Ext.define('App.system.AdminManager', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.View',
        'Ext.button.Button',
        'Ext.toolbar.Paging',
        'Ext.grid.column.Action'
    ],

    header: false,
    defaultListenerScope: true,

    apis: {
        Admin: {
            deleteAdmin: {},
            updatePassword: {},
            getAdmins: {},
            addAdmin: {},
            updateAdmin: {},
            getDepartment: {},
            getPosition: {}
        },
        System: {
            getRoles: {}
        }
    },
    search: {
        hidden: true,
        items: [
            {
                xtype: 'textfield',
                name: 'like_userName',
                fieldLabel: '用户名'
            },
            {
                xtype: 'textfield',
                name: 'like_realName',
                margin: '0 0 0 20',
                fieldLabel: '姓名'
            },
            {
                xtype: 'button',
                margin: '0 0 0 20',
                text: '搜索',
                iconCls: 'fa fa-search',
                listeners: {
                    click: 'onSearchEventListener'
                }
            }
        ]
    },
    columns: [
        {
            xtype: 'gridcolumn',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (!value || value == "") {
                    value = Resource.create('/admin/image/exicon/nopic_40.gif');
                } else {
                    value = Resource.image(value);
                }
                var width = 35;
                var height = 35;
                return '<div style="height: ' + height + 'px;width: ' + width + 'px;vertical-align: middle;display:table-cell;">' +
                    '<img style="max-height: ' + height + 'px;max-width: ' + width + 'px;vertical-align: middle" src=' + value + '></div> ';
            },
            width: 60,
            align: 'center',
            dataIndex: 'logo',
            text: '头像'
        },
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
                    } else {
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
        },
        {
            xtype: 'actioncolumn',
            dataIndex: 'id',
            text: '操作',
            align: 'center',
            items: [
                {
                    iconCls: 'x-fa fa-key green',
                    tooltip: '修改密码',
                    handler: 'onUpdatePasswordAdmin'
                },
                {
                    iconCls: 'x-fa fa-pencil green',
                    tooltip: '修改管理员',
                    handler: 'onUpdateAdmin'
                },
                {
                    iconCls: 'x-fa fa-times red',
                    tooltip: '删除管理员',
                    handler: 'onDeleteAdmin'
                }
            ]
        }
    ],
    selModel: {
        selType: 'checkboxmodel'
    },
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'button',
                    text: '列出管理员',
                    icon: Resource.png('jet', 'listChanges'),
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    text: '添加管理员',
                    icon: Resource.png('jet', 'addIcon'),
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    text: '批量删除',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    listeners: {
                        click: 'onBatchDeleteClick'
                    }
                },
                '->',
                {
                    xtype: 'button',
                    text: '搜索',
                    icon: Resource.png('jet', 'search'),
                    listeners: {
                        click: 'onSearchClick'
                    }
                }
            ]
        },
        {
            xtype: 'pagingtoolbar',
            name: 'pagingtoolbar',
            dock: 'bottom',
            width: 360,
            displayInfo: true
        }
    ],

    passwordFormWindow: [
        {
            xtype: 'textfield',
            name: 'id',
            hidden: true,
            fieldLabel: 'ID',
            inputType: 'hidden'
        },
        {
            xtype: 'textfield',
            anchor: '100%',
            fieldLabel: '管理员密码',
            name: 'loginPassword',
            inputType: 'password',
            allowBlank: false,
            emptyText: '当前登录的管理员密码'
        },
        {
            xtype: 'textfield',
            anchor: '100%',
            fieldLabel: '新密码',
            name: 'password',
            inputType: 'password',
            allowBlank: false
        },
        {
            xtype: 'textfield',
            anchor: '100%',
            fieldLabel: '重复新密码',
            name: 'repassword',
            inputType: 'password',
            allowBlank: false
        }
    ],

    onListClick: function (button, e, eOpts) {
        this.onAfterApply();
    },

    onAddClick: function (button, e, eOpts) {
        var self = this;
        var win = Dialog.openWindow('App.system.AdminWindow', {
            apis: self.apis,
            callback: function () {
                self.refreshStore();
            }
        });
        win.initWindow();
    },

    onUpdateAdmin: function () {
        var json = this.getIgnoreSelect(arguments);
        var self = this;

        var win = Dialog.openWindow('App.system.AdminWindow', {
            apis: self.apis,
            callback: function () {
                self.refreshStore()
            }
        });
        win.initWindow();
        win.setValues(json)
    },

    onUpdatePasswordAdmin: function () {
        var self = this;
        var json = this.getIgnoreSelect(arguments);

        Dialog.openFormWindow({
            title: '修改密码',
            width: 500,
            height: 300,
            items: self.passwordFormWindow,
            success: function (json, win) {
                if (json['password'] != json['repassword']) {
                    Dialog.alert("两次输入的密码不一致");
                    return false;
                }
                self.apis.Admin.updatePassword
                    .wait(self, '正在修改密码...')
                    .call({
                        id: json['id'],
                        password: json['password'],
                        loginPassword: json['loginPassword']
                    }, function () {
                        win.close();
                        self.refreshStore();
                    });
            }
        }).setValues({id: json['id']});
    },

    onDeleteAdmin: function () {
        var json = this.getIgnoreSelect(arguments);
        var self = this;

        Dialog.batch({
            message: '确定删除管理员{d}吗？',
            data: json,
            key: 'userName',
            callback: function (btn) {
                if (btn == Global.YES) {
                    self.apis.Admin.deleteAdmin
                        .wait(self, '正在删除管理员...')
                        .call({ids: [json['id']]}, function () {
                            self.refreshStore();
                        })
                }
            }
        });
    },

    onBatchDeleteClick: function (button, e, eOpts) {
        var jsons = this.getSelects();
        var self = this;
        Dialog.batch({
            message: '确定删除管理员{d}吗？',
            data: jsons,
            key: 'userName',
            callback: function (btn) {
                if (btn == Global.YES) {
                    var ids = Array.splitArray(jsons, 'id');
                    self.apis.Admin.deleteAdmin
                        .wait(self, '正在删除管理员...')
                        .call({ids: ids}, function () {
                            self.refreshStore()
                        })
                }
            }
        });
    },

    onAfterApply: function () {
        var store = this.apis.Admin.getAdmins.createPageStore();
        this.setStore(store);
        store.load();
    },

    onSearchClick: function (button, e, options) {
        this.searchPanel.setSearchShow();
    },

    getSearchFormItems: function () {
        return [
            {
                xtype: 'textfield',
                name: 'jobNumber',
                fieldLabel: '工号'
            },
            {
                xtype: 'textfield',
                name: 'userName',
                fieldLabel: '用户名'
            },
            {
                xtype: 'textfield',
                name: 'realName',
                fieldLabel: '姓名'
            },
            {
                xtype: 'textfield',
                name: 'phoneNumber',
                fieldLabel: '电话'
            },
            {
                xtype: 'textfield',
                name: 'email',
                fieldLabel: '邮箱地址'
            }
        ]
    },

    onSearchFormButtonClick: function (form) {
        var data = form.getForm().getValues();
        var store = this.apis.Admin.getAdmins.createPageStore({search: data});
        this.setStore(store);
        store.load();
    }

});
