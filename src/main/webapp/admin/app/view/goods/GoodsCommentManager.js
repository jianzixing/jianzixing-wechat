Ext.define('App.goods.GoodsCommentManager', {
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
        GoodsComment: {
            getComments: {},
            deleteComments: {}
        }
    },

    search: true,
    columns: [
        {
            xtype: 'gridcolumn',
            dataIndex: 'id',
            text: '评论ID'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'TableUser',
            text: '评论用户',
            renderer: function (v) {
                if (v) return v['userName'];
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'TableGoods',
            text: '商品',
            renderer: function (v) {
                if (v) return v['name'];
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'goodsScore',
            text: '商品评价'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'logisticsScore',
            text: '物流评价'
        },
        {
            xtype: 'gridcolumn',
            hidden: true,
            dataIndex: 'serviceScore',
            text: '服务评价'
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'comment',
            text: '评论内容'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'createTime',
            text: '评论时间',
            renderer: function (v) {
                if (v) return new Date(v).format();
                return '';
            }
        },
        {
            xtype: 'actioncolumn',
            dataIndex: 'id',
            text: '操作',
            align: 'center',
            items: [
                {
                    iconCls: "x-fa fa-times red",
                    tooltip: '删除评论',
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
                    icon: Resource.png('jet', 'list'),
                    text: '列出商品评论',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '批量删除评论',
                    listeners: {
                        click: 'onDelClick'
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

    onDelClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            Dialog.batch({
                message: '确定删除商品评论{d}吗？',
                data: datas,
                key: 'id',
                callback: function (btn) {
                    if (btn == 'yes') {
                        var ids = Array.splitArray(datas, "id");
                        self.apis.GoodsComment.deleteComments
                            .wait(self, '正在删除评论...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            })
                    }
                }
            });
        } else {
            Dialog.alert('请先选中至少一条评论信息')
        }
    },

    onAfterApply: function () {
        var store = this.apis.GoodsComment.getComments.createPageStore();
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
                name: 'comment',
                fieldLabel: '评价内容'
            },
            {
                xtype: 'textfield',
                name: 'userName',
                fieldLabel: '用户名'
            },
            {
                xtype: 'textfield',
                name: 'openid',
                fieldLabel: 'openid'
            },
            {
                xtype: 'textfield',
                name: 'gid',
                fieldLabel: '商品ID'
            },
            {
                xtype: 'textfield',
                name: 'serialNumber',
                fieldLabel: '商品编号'
            },
            {
                xtype: 'datetimefield',
                name: 'createTimeStart',
                anchor: '100%',
                format: 'Y-m-d H:i:s',
                fieldLabel: '创建时间(开始)'
            },
            {
                xtype: 'datetimefield',
                name: 'createTimeEnd',
                anchor: '100%',
                format: 'Y-m-d H:i:s',
                fieldLabel: '创建时间(结束)'
            }
        ]
    },

    onSearchFormButtonClick: function (form) {
        var data = form.getForm().getValues();
        var store = this.apis.GoodsComment.getComments.createPageStore({search: data});
        this.setStore(store);
        store.load();
    }

});