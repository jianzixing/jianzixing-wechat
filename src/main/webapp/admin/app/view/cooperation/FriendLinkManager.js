Ext.define('App.cooperation.FriendLinkManager', {
    extend: 'Ext.grid.Panel',
    name: 'friendLink_manager',
    preventHeader: true,
    border: false,
    loadMask: true,
    defaultListenerScope: true,
    apis: {
        FriendLink: {
            addFriendLink: {},
            deleteFriendLink: {},
            updateFriendLink: {}
        }
    },
    api: {FriendLink: {getFriendLink: {_page: 'App.cooperation.FriendLinkManager'}}},
    plugins: [{ptype: 'cellediting', clicksToEdit: 1}],
    selModel: {
        selType: 'checkboxmodel'
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
            width: 200,
            tdCls: 'td_align_middle',
            align: 'center',
            dataIndex: 'name',
            field: {
                xtype: 'textfield'
            },
            text: '名称'
        },
        {
            xtype: 'gridcolumn',
            width: 280,
            align: 'center',
            tdCls: 'td_align_middle',
            dataIndex: 'link',
            text: '链接',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (!value || value == "") return "<span color='#333333'>[无]</span>";
                else return "<a href='" + value + "' target='_blank'>" + value + "</a>";
            }
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            align: 'center',
            tdCls: 'td_align_middle',
            dataIndex: 'order',
            text: '排序'
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
                    handler: 'onUpdateClick'
                },
                '->',
                {
                    icon: Resource.png('jet', 'exclude'),
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
                    icon: Resource.png('jet', 'list'),
                    text: '列出',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addJira'),
                    text: '添加友情链接',
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '删除',
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

    onListClick: function (button, e, options) {
        this.refreshStore();
    },

    onAddClick: function (button, e, options) {
        var self = this;
        Dialog.openWindow('App.cooperation.FriendLinkWindow', {
            apis: this.apis,
            _callback: function () {
                self.refreshStore();
            }
        });
    },

    onDeleteClick: function (button, e, options) {
        var jsons = this.getIgnoreSelects(arguments, this);
        var self = this;
        Dialog.batch({
            message: '确定删除友情链接{d}吗？',
            data: jsons,
            key: 'id',
            callback: function (btn) {
                if (btn == Global.YES) {
                    var ids = Array.splitArray(jsons, 'id');
                    self.apis.FriendLink.deleteFriendLink
                        .wait(self, '正在删除友情链接...')
                        .call({ids: ids}, function () {
                            self.refreshStore()
                        });
                }
            }
        });
    },
    onUpdateClick: function (grid, rowIndex, colIndex) {
        var self = this;
        var json = this.getIgnoreSelect(arguments);
        if (json != null) {
            Dialog.openWindow('App.cooperation.FriendLinkWindow', {
                apis: this.apis,
                _callback: function () {
                    self.refreshStore();
                }
            }).setValues(json);
        }
    },

    onSearchClick: function (button, e, options) {
        if (this.searchPanel.isVisible()) {
            this.searchPanel.hide();
        } else {
            this.searchPanel.show();
        }
    },

    onAfterApply: function () {
        this.find('member_paging').bindStore(this.getStore());
    },

    onBackApply: function () {
        this.refreshStore();
    },

    onSearchEventListener: function (form) {

    }

});

