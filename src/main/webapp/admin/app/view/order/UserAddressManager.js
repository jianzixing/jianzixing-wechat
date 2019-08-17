Ext.define('App.order.UserAddressManager', {
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
        User: {
            getOrderUsers: {}
        },
        UserAddress: {
            getUserAddress: {},
            addUserAddress: {},
            deleteUserAddress: {},
            updateUserAddress: {}
        },
        Area: {
            getProvince: {},
            getCity: {},
            getArea: {}
        }
    },
    search: true,
    columns: [
        {
            xtype: 'gridcolumn',
            width: 80,
            dataIndex: 'id',
            text: 'ID'
        },
        {
            xtype: 'gridcolumn',
            width: 100,
            dataIndex: 'TableUser',
            text: '所属用户',
            renderer: function (user) {
                if (user) {
                    return user['userName'];
                }
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 100,
            dataIndex: 'realName',
            text: '收货人'
        },
        {
            xtype: 'gridcolumn',
            width: 180,
            dataIndex: 'country',
            text: '所在地区',
            renderer: function (value, mate, record) {
                var country = record.get('country');
                var province = record.get('province');
                var city = record.get('city');
                var county = record.get('county');
                if (country && province && city) {
                    return country + '-' + province + '-' + city + '-' + county;
                }
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'address',
            text: '详细地址'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'phoneNumber',
            text: '手机号码'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'telNumber',
            text: '固定电话'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'email',
            text: '电子邮箱'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'postcode',
            text: '邮编地址'
        },
        {
            xtype: 'gridcolumn',
            width: 100,
            dataIndex: 'isDefault',
            text: '是否默认地址',
            renderer: function (v) {
                if (v == 1) {
                    return '默认地址';
                }
                return '否';
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
                    text: '列出收货地址',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addFavoritesList'),
                    text: '添加收货地址',
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '批量删除地址',
                    listeners: {
                        click: 'onDeleteClick'
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
        this.onAfterApply();
    },

    onAddClick: function (button, e, eOpts) {
        var self = this;
        Dialog.openWindow('App.order.UserAddressWindow', {
            apis: self.apis,
            _callback: function () {
                self.refreshStore();
            }
        }).onInitWindow();
    },

    onUpdateClick: function (button, e, eOpts) {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var win = Dialog.openWindow('App.order.UserAddressWindow', {
                apis: self.apis,
                _callback: function () {
                    self.refreshStore();
                }
            });
            win.onInitWindow();
            win.setValue(data);
        }
    },

    onDeleteClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.getIgnoreSelects(arguments);
        if (jsons != null) {
            Dialog.batch({
                message: '确定删除用户收货地址{d}吗？',
                data: jsons,
                key: 'id',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, "id");
                        self.apis.UserAddress.deleteUserAddress
                            .wait(self, '正在删除收货地址...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            })
                    }
                }
            });
        } else {
            Dialog.alert('提示', '请先选中至少一条收货信息后再删除!');
        }
    },

    onAfterApply: function () {
        var store = this.apis.UserAddress.getUserAddress.createPageStore();
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
                name: 'userName',
                fieldLabel: '用户名'
            },
            {
                xtype: 'textfield',
                name: 'realName',
                fieldLabel: '收货人'
            },
            {
                xtype: 'textfield',
                name: 'address',
                fieldLabel: '地址'
            },
            {
                xtype: 'textfield',
                name: 'phone',
                fieldLabel: '手机'
            }
        ]
    },

    onSearchFormButtonClick: function (form) {
        var data = form.getForm().getValues();
        var store = this.apis.UserAddress.getUserAddress.createPageStore({search: data});
        this.setStore(store);
        store.load();
    }

});