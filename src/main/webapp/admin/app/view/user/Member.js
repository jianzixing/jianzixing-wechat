Ext.define('App.user.Member', {
    extend: 'Ext.grid.Panel',
    name: 'account_member',
    preventHeader: true,
    border: false,
    loadMask: true,
    defaultListenerScope: true,
    apis: {
        User: {
            deleteUser: {},
            addUser: {},
            updateUser: {},
            getUsers: {},
            resetUserPwd: {}
        }
    },
    selModel: {
        selType: 'checkboxmodel'
    },

    search: true,

    columns: [
        {
            xtype: 'gridcolumn',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (!value || value == "") {
                    value = Resource.create('/admin/image/exicon/nopic_40.gif');
                } else {
                    value = Resource.image(value);
                }
                var width = 40;
                var height = 40;
                return '<div style="height: ' + height + 'px;width: ' + width + 'px;vertical-align: middle;display:table-cell;">' +
                    '<img style="max-height: ' + height + 'px;max-width: ' + width + 'px;vertical-align: middle" src=' + value + '></div> ';
            },
            width: 60,
            align: 'center',
            dataIndex: 'avatar',
            text: '头像'
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            tdCls: 'td_align_middle',
            align: 'center',
            dataIndex: 'userName',
            text: '用户名'
        },
        {
            xtype: 'gridcolumn',
            tdCls: 'td_align_middle',
            align: 'center',
            dataIndex: 'TableUserLevel',
            text: '等级',
            renderer: function (v) {
                if (v) {
                    return v['name'];
                }
                return '[其他]';
            }
        },
        {
            xtype: 'gridcolumn',
            tdCls: 'td_align_middle',
            align: 'center',
            dataIndex: 'TableIntegral',
            text: '积分',
            renderer: function (v) {
                if (v) return v['amount'];
                return '0';
            }
        },
        {
            xtype: 'gridcolumn',
            hidden: true,
            tdCls: 'td_align_middle',
            align: 'center',
            dataIndex: 'openid',
            text: 'openid'
        },
        {
            xtype: 'gridcolumn',
            width: 180,
            align: 'center',
            tdCls: 'td_align_middle',
            dataIndex: 'nick',
            text: '昵称',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (!value || value == "") return "<span color='#333333'>[无]</span>";
                else return decodeURIComponent(value);
            }
        },
        {
            xtype: 'gridcolumn',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (value == '0') return '女';
                else if (value == '1') return '男';
                else return '未知';
            },
            width: 150,
            align: 'center',
            tdCls: 'td_align_middle',
            dataIndex: 'gender',
            text: '性别'
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            align: 'center',
            tdCls: 'td_align_middle',
            dataIndex: 'birthday',
            text: '生日',
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
            width: 260,
            align: 'center',
            dataIndex: 'email',
            tdCls: 'td_align_middle',
            text: '邮件地址',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (!value || value == "") return "<span color='#333333'>[无]</span>";
                else return value;
            }
        },
        {
            xtype: 'actioncolumn',
            width: 180,
            text: '操作',
            align: 'center',
            tdCls: 'td_align_middle',
            items: [
                {
                    tooltip: '修改',
                    icon: Resource.png('jet', 'editItemInSection'),
                    handler: 'onUpdateUserClick'
                },
                '->',
                {
                    icon: Resource.png('jet', 'exclude'),
                    tooltip: '删除',
                    handler: 'onDeleteUserClick'
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
                    text: '列出会员',
                    listeners: {
                        click: 'onListUserClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addJira'),
                    text: '添加新用户',
                    listeners: {
                        click: 'onAddUserClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '批量删除用户',
                    listeners: {
                        click: 'onDeleteUserClick'
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
            name: 'member_paging',
            dock: 'bottom',
            width: 360,
            displayInfo: true
        }
    ],

    onGridCellClick: function (grid, record, item, index, e, eOpts) {
        var btn = e.getTarget();
        if (btn) {
        }
    },

    onListUserClick: function (button, e, options) {
        this.onAfterApply();
    },

    onAddUserClick: function (button, e, options) {
        var self = this;
        this.parent.forward("App.user.OperationMember", {
            apis: this.apis,
            _callback: function () {
                self.refreshStore();
            }
        });
    },

    onDeleteUserClick: function (button, e, options) {
        var jsons = this.getIgnoreSelects(arguments);
        var self = this;
        Dialog.batch({
            message: '确定删除用户{d}吗？',
            data: jsons,
            key: 'userName',
            callback: function (btn) {
                if (btn == Global.YES) {
                    var ids = Array.splitArray(jsons, 'id');
                    self.apis.User.deleteUser
                        .wait(self, '正在删除用户...')
                        .call({ids: ids}, function () {
                            self.refreshStore()
                        });
                }
            }
        });
    },
    onUpdateUserClick: function (grid, rowIndex, colIndex) {
        var self = this;
        var json = this.getIgnoreSelect(arguments);
        if (json != null) {
            var contextPanel = this.parent.forward("App.user.OperationMember", {
                apis: this.apis,
                _callback: function () {
                    self.refreshStore();
                }
            });
            contextPanel.setValues(json);
        }
    },

    onAfterApply: function () {
        var store = this.apis.User.getUsers.createPageStore();
        this.setStore(store);
        store.load();
    },

    onBackApply: function () {
        this.refreshStore();
    },

    onSearchClick: function (button, e, options) {
        this.searchPanel.setSearchShow();
    },

    getSearchFormItems: function () {
        return [
            {
                xtype: 'textfield',
                name: 'userName',
                fieldLabel: '用户名'
            },
            {
                xtype: 'textfield',
                name: 'nick',
                fieldLabel: '昵称'
            },
            {
                xtype: 'textfield',
                name: 'openid',
                fieldLabel: 'openid'
            },
            {
                xtype: 'textfield',
                name: 'integralStart',
                fieldLabel: '积分数量(开始)'
            },
            {
                xtype: 'textfield',
                name: 'integralEnd',
                fieldLabel: '积分数量(结束)'
            }
        ]
    },

    onSearchFormButtonClick: function (form) {
        var data = form.getForm().getValues();
        var store = this.apis.User.getUsers.createPageStore({search: data});
        this.setStore(store);
        store.load();
    }

});

