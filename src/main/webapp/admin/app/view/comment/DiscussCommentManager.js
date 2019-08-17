Ext.define('App.comment.DiscussCommentManager', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.grid.Panel',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'Ext.toolbar.Paging',
        'Ext.button.Button',
        'Ext.selection.CheckboxModel'
    ],

    border: false,
    layout: 'border',
    defaultListenerScope: true,
    apis: {
        DiscussComment: {
            addClassify: {},
            delClassify: {},
            updateClassify: {},
            getClassifies: {},
            addComment: {},
            delComment: {},
            updateComment: {},
            getComments: {},
            setShow: {},
            setHide: {}
        }
    },

    items: [
        {
            xtype: 'gridpanel',
            name: 'discuss',
            region: 'center',
            title: '评论列表',
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'id',
                    text: '评论ID'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'User',
                    text: '评论用户',
                    renderer: function (v) {
                        if (v) {
                            return v['userName'];
                        }
                        return v;
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'text',
                    text: '评论文字'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'outValue',
                    text: '主题内容',
                    renderer: function (v) {
                        if (v) {
                            var r = v['title'];
                            if (!r) r = v['name'];
                            return r;
                        }
                        return '&lt;未关联&gt;';
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'isShow',
                    text: '是否可见',
                    renderer: function (v) {
                        if (v == 0) {
                            return '<span style="color: red">隐藏</span>';
                        } else {
                            return '<span style="color: green">可见</span>';
                        }
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
                    }
                }
            ],
            dockedItems: [
                {
                    xtype: 'pagingtoolbar',
                    name: 'paging',
                    dock: 'bottom',
                    width: 360,
                    displayInfo: true
                },
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'listChanges'),
                            text: '列出评论',
                            listeners: {
                                click: 'onListDiscussClick'
                            }
                        },
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'RemoveMulticaret'),
                            text: '批量删除',
                            listeners: {
                                click: 'onDelDiscussClick'
                            }
                        },
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'showReadAccess'),
                            text: '批量可见',
                            listeners: {
                                click: 'onShowClick'
                            }
                        },
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'hideWarnings'),
                            text: '批量隐藏',
                            listeners: {
                                click: 'onHideClick'
                            }
                        }
                    ]
                }
            ],
            selModel: {
                selType: 'checkboxmodel'
            }
        },
        {
            xtype: 'gridpanel',
            name: 'classify',
            region: 'west',
            // columnLines: true,
            split: true,
            width: 320,
            title: '评论分类',
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'id',
                    text: '分类ID'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'name',
                    text: '分类名称'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'code',
                    text: '分类码'
                },
                {
                    xtype: 'actioncolumn',
                    text: '操作',
                    dataIndex: 'isSystem',
                    items: [
                        {
                            tooltip: '修改',
                            iconCls: "x-fa fa-pencil green",
                            handler: 'onUpdateClassifyClick',
                            getClass: function (v) {
                                if (v == 1) {
                                    return "x-hidden";
                                } else {
                                    return 'x-fa fa-pencil green';
                                }
                            }
                        },
                        {
                            iconCls: "x-fa fa-times red",
                            tooltip: '删除',
                            handler: 'onDelClassifyClick',
                            getClass: function (v) {
                                if (v == 1) {
                                    return "x-hidden";
                                } else {
                                    return 'x-fa fa-times red';
                                }
                            }
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
                            text: '列出分类',
                            listeners: {
                                click: 'onListClassifyClick'
                            }
                        },
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'addFavoritesList'),
                            text: '添加分类',
                            listeners: {
                                click: 'onAddClassifyClick'
                            }
                        }
                    ]
                }
            ],
            listeners: {
                itemclick: 'onClassifyItemClick'
            }
        }
    ],

    onListDiscussClick: function (button, e, eOpts) {
        this.find('discuss').getStore().refreshStore();
    },

    onDelDiscussClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.find('discuss').getIgnoreSelects(arguments);

        if (jsons) {
            Dialog.batch({
                message: '确定删除评论{d}吗？',
                data: jsons,
                key: 'id',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, "id");
                        self.apis.DiscussComment.delComment
                            .wait(self, '正在删除评论...')
                            .call({ids: ids}, function () {
                                self.find('discuss').refreshStore();
                            })
                    }
                }
            });
        } else {
            Dialog.alert('请先选中要删除的评论数据!');
        }
    },

    onShowClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.find('discuss').getIgnoreSelects(arguments);

        if (jsons) {
            Dialog.batch({
                message: '确定使评论{d}可见吗？',
                data: jsons,
                key: 'id',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, "id");
                        self.apis.DiscussComment.setShow
                            .wait(self.find('discuss'), '正在设置可见...')
                            .call({ids: ids}, function () {
                                self.find('discuss').refreshStore();
                            })
                    }
                }
            });
        } else {
            Dialog.alert('请先选中要设置的评论数据!');
        }
    },

    onHideClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.find('discuss').getIgnoreSelects(arguments);

        if (jsons) {
            Dialog.batch({
                message: '确定使评论{d}隐藏吗？',
                data: jsons,
                key: 'id',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, "id");
                        self.apis.DiscussComment.setHide
                            .wait(self.find('discuss'), '正在设置隐藏...')
                            .call({ids: ids}, function () {
                                self.find('discuss').refreshStore();
                            })
                    }
                }
            });
        } else {
            Dialog.alert('请先选中要设置的评论数据!');
        }
    },

    onListClassifyClick: function (button, e, eOpts) {
        this.find('classify').refreshStore();
    },

    onAddClassifyClick: function (button, e, eOpts) {
        var self = this;

        Dialog.openWindow('App.comment.DiscussClassifyWindow', {
            apis: this.apis,
            _callback: function () {
                self.find('classify').refreshStore();
            }
        });
    },

    onUpdateClassifyClick: function (button, e, eOpts) {
        var self = this;
        var data = this.find('classify').getIgnoreSelect(arguments);

        Dialog.openWindow('App.comment.DiscussClassifyWindow', {
            apis: this.apis,
            _callback: function () {
                self.find('classify').refreshStore();
            }
        }).setValue(data);
    },

    onDelClassifyClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.find('classify').getIgnoreSelects(arguments);

        if (jsons) {
            Dialog.batch({
                message: '确定删除评论分类{d}吗？',
                data: jsons,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, "id");
                        self.apis.DiscussComment.delClassify
                            .wait(self.find('classify'), '正在删除评论分类...')
                            .call({ids: ids}, function () {
                                self.find('classify').refreshStore();
                            })
                    }
                }
            });
        } else {
            Dialog.alert('请先选中要删除的新闻分组数据!');
        }
    },

    onClassifyItemClick: function (dataview, record, item, index, e, eOpts) {
        var self = this;
        var cid = record.get('id');

        var store = this.apis.DiscussComment.getComments.createPageStore({cid: cid});
        this.find('discuss').setStore(store);
        this.find('paging').bindStore(store);
        store.load();
    },

    onAfterApply: function () {
        var store = this.apis.DiscussComment.getClassifies.createListStore();
        this.find('classify').setStore(store);
        store.load();
    }

});