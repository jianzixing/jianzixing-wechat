Ext.define('App.recommend.RecommendManager', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.tree.Panel',
        'Ext.tree.View',
        'Ext.tree.Column',
        'Ext.button.Button',
        'Ext.selection.CheckboxModel',
        'Ext.grid.Panel',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'Ext.toolbar.Paging'
    ],

    border: false,
    layout: 'border',
    defaultListenerScope: true,

    apis: {
        Recommend: {
            addGroup: {},
            delGroup: {},
            updateGroup: {},
            getGroups: {},
            addContent: {},
            delContent: {},
            updateContent: {},
            getContents: {},
            setRecommendTop: {},
            setRecommendTopNormal: {},
            setRecommendImage: {},
            updateContent: {}
        },
        Content: {
            getTopics: {},
            getContents: {},
            getContentById: {}
        },
        News: {
            getGroups: {},
            getNews: {},
            getNewById: {}
        },
        Product: {
            getCategorys: {},
            getProducts: {},
            getProductById: {}
        },
        Solution: {
            getGroups: {},
            getSolutions: {},
            getSolutionById: {}
        },
        Document: {
            getGroups: {},
            getTreeCatalog: {},
            getTreeCatalogById: {}
        },
        HtmlPage: {
            getFullHtmlPage: {}
        }
    },

    items: [
        {
            xtype: 'treepanel',
            name: 'group',
            region: 'west',
            split: true,

            displayField: 'name',
            useArrows: true,

            width: 500,
            title: '推荐分组管理',
            viewConfig: {},
            columns: [
                {
                    xtype: 'treecolumn',
                    width: 300,
                    dataIndex: 'name',
                    text: '分组名称'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'code',
                    text: '分组码'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'type',
                    text: '推荐类型',
                    renderer: function (v) {
                        if (v == 10) return '标准内容';
                        if (v == 11) return '新闻';
                        if (v == 12) return '产品';
                        if (v == 13) return '解决方案';
                        if (v == 14) return '文档';
                        if (v == 0) return '链接地址';
                        if (v == 999) return '混合推荐';
                        return '其他';
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'admin',
                    text: '创建人'
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
                },
                {
                    xtype: 'actioncolumn',
                    text: '操作',
                    dataIndex: 'id',
                    items: [
                        {
                            tooltip: '修改',
                            iconCls: "x-fa fa-pencil green",
                            handler: 'onUpdateGroupClick',
                            getClass: function (v) {
                                if (v == 'root' || v == 0) {
                                    return "x-hidden";
                                } else {
                                    return 'x-fa fa-pencil green';
                                }
                            }
                        },
                        {
                            iconCls: "x-fa fa-times red",
                            tooltip: '删除',
                            handler: 'onDelGroupClick',
                            getClass: function (v) {
                                if (v == 'root' || v == 0) {
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
                            text: '列出分组',
                            listeners: {
                                click: 'onListGroupClick'
                            }
                        },
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'addFavoritesList'),
                            text: '添加分组',
                            listeners: {
                                click: 'onAddGroupClick'
                            }
                        },
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'RemoveMulticaret'),
                            text: '批量删除分组',
                            listeners: {
                                click: 'onDelGroupClick'
                            }
                        }
                    ]
                }
            ],
            selModel: {
                selType: 'checkboxmodel'
            },
            listeners: {
                itemclick: 'onGridpanelItemClick'
            }
        },
        {
            xtype: 'gridpanel',
            name: 'recommend',
            region: 'center',
            title: '推荐内容管理',
            columns: [
                {
                    xtype: 'gridcolumn',
                    width: 90,
                    dataIndex: 'id',
                    text: 'ID'
                },
                {
                    xtype: 'gridcolumn',
                    width: 90,
                    dataIndex: 'value',
                    text: '关联ID'
                },
                {
                    xtype: 'gridcolumn',
                    width: 100,
                    dataIndex: 'cover',
                    text: '推荐图片',
                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                        if (!value || value == "") {
                            value = Resource.create('/admin/image/exicon/nopic_80.gif');
                        } else {
                            value = Resource.image(value);
                        }
                        var width = 60;
                        var height = 60;
                        return '<div style="height: ' + height + 'px;width: ' + width + 'px;vertical-align: middle;display:table-cell;">' +
                            '<img style="max-height: ' + height + 'px;max-width: ' + width + 'px;vertical-align: middle" src=' + value + '></div> ';
                    }
                },
                {
                    xtype: 'gridcolumn',
                    width: 300,
                    dataIndex: 'title',
                    text: '推荐内容'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'type',
                    text: '推荐类型',
                    renderer: function (v) {
                        if (v == 10) return '标准内容';
                        if (v == 11) return '新闻';
                        if (v == 12) return '产品';
                        if (v == 13) return '解决方案';
                        if (v == 14) return '文档';
                        if (v == 0) return '链接地址';
                        return '其他';
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'top',
                    text: '是否置顶',
                    renderer: function (v) {
                        if (v > 0) {
                            return '置顶(' + v + ')';
                        } else {
                            return '正常';
                        }
                    }
                },

                {
                    xtype: 'gridcolumn',
                    dataIndex: 'admin',
                    text: '创建人'
                },
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'createTime',
                    text: '推荐时间',
                    renderer: function (value) {
                        if (value) {
                            var d = new Date(value);
                            return d.format();
                        }
                    }
                },
                {
                    xtype: 'actioncolumn',
                    text: '操作',
                    items: [
                        {
                            tooltip: '修改',
                            iconCls: "x-fa fa-pencil green",
                            handler: 'onUpdateDataClick'
                        },
                        {
                            iconCls: "x-fa fa-times red",
                            tooltip: '删除',
                            handler: 'onDelDataClick'
                        }
                    ]
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
                            text: '列出推荐',
                            listeners: {
                                click: 'onListDataClick'
                            }
                        },
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'addFavoritesList'),
                            text: '添加推荐',
                            listeners: {
                                click: 'onAddDataClick'
                            }
                        },
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'RemoveMulticaret'),
                            text: '删除推荐',
                            listeners: {
                                click: 'onDelDataClick'
                            }
                        },
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'fileStatus'),
                            text: '设置推荐图片',
                            listeners: {
                                click: 'onSetImageClick'
                            }
                        },
                        '-',
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'moveUp'),
                            text: '置顶推荐',
                            listeners: {
                                click: 'onTopRecommendClick'
                            }
                        },
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'arrowRight'),
                            text: '取消置顶',
                            listeners: {
                                click: 'onCancelTopRecommendClick'
                            }
                        }
                    ]
                }
            ],
            selModel: {
                selType: 'checkboxmodel'
            }
        }
    ],

    onListGroupClick: function (button, e, eOpts) {
        this.onAfterApply();
    },

    onSetImageClick: function (b, e, o) {
        var self = this;
        var data = this.find('recommend').getIgnoreSelect(arguments);
        if (data) {
            var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
            win.setSelectionCallback(function (files) {
                var fileName = files.fileName;
                Dialog.confirm('提示', '确定设置当前推荐图片吗？', function (btn) {
                    if (btn == 'yes') {
                        self.apis.Recommend.setRecommendImage
                            .wait(self.find('recommend'), '正在设置请稍后...')
                            .call({id: data['id'], cover: fileName}, function () {
                                self.find('recommend').refreshStore();
                            });
                    }
                });
            }, true);
        } else {
            Dialog.alert('请先选中一条推荐信息!');
        }
    },

    onCancelTopRecommendClick: function (b, e, o) {
        var self = this;
        var data = this.find('recommend').getIgnoreSelect(arguments);
        if (data) {
            var pos = data['top'];
            if (pos == 0 || pos == '0') {
                pos = 1;
            }
            Dialog.confirm('提示', '确定要取消置顶吗？', function (btn) {
                if (btn == 'yes') {
                    self.apis.Recommend.setRecommendTopNormal
                        .wait(self.find('recommend'), '正在设置请稍后...')
                        .call({id: data['id']}, function () {
                            self.find('recommend').refreshStore();
                        });
                }
            }, pos);
        } else {
            Dialog.alert('请先选中一条推荐信息!');
        }
    },

    onTopRecommendClick: function (b, e, o) {
        var self = this;
        var data = this.find('recommend').getIgnoreSelect(arguments);
        if (data) {
            var pos = data['top'];
            if (pos == 0 || pos == '0') {
                pos = 1;
            }
            Dialog.prompt('提示', '请输入置顶排序序号(需要大于0)', function (btn, text) {
                if (btn == 'ok') {
                    self.apis.Recommend.setRecommendTop
                        .wait(self.find('recommend'), '正在设置请稍后...')
                        .call({id: data['id'], level: text}, function () {
                            self.find('recommend').refreshStore();
                        });
                }
            }, pos);
        } else {
            Dialog.alert('请先选中一条推荐信息!');
        }
    },

    onAddGroupClick: function (button, e, eOpts) {
        var self = this;
        var data = self.find('group').getIgnoreSelect(arguments);
        if (!data) data = {};

        Dialog.openWindow('App.recommend.RecommendGroupWindow', {
            apis: self.apis,
            _parentId: data['id'],
            _callback: function () {
                self.onAfterApply();
            }
        });
    },

    onUpdateGroupClick: function (button, e, eOpts) {
        var self = this;
        var data = self.find('group').getIgnoreSelect(arguments);

        Dialog.openWindow('App.recommend.RecommendGroupWindow', {
            apis: self.apis,
            _callback: function () {
                self.onAfterApply();
            }
        }).setValue(data);
    },

    onDelGroupClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.find('group').getIgnoreSelects(arguments);

        if (jsons) {
            Dialog.batch({
                message: '确定删除推荐分组{d}吗？',
                data: jsons,
                key: 'id',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, "id");
                        self.apis.Recommend.delGroup
                            .wait(self, '正在删除分组...')
                            .call({ids: ids}, function () {
                                self.onAfterApply();
                            })
                    }
                }
            });
        } else {
            Dialog.alert('请选选中要删除的分组数据');
        }
    },

    onListDataClick: function (button, e, eOpts) {
        this.find('recommend').refreshStore();
    },

    onAddDataClick: function (button, e, eOpts) {
        var self = this;
        var group = this.find('group').getIgnoreSelect(arguments);
        if (group) {
            var win = Dialog.openWindow('App.recommend.RecommendInfoWindow', {
                _groupData: group,
                apis: self.apis,
                _callback: function () {
                    self.find('recommend').refreshStore();
                }
            });
            if (group['type'] != 100) {
                win.setRecommendType(group['type']);
            }
        } else {
            Dialog.alert('请先选中一条分组信息后再添加推荐内容');
        }
    },

    onUpdateDataClick: function (button, e, eOpts) {
        var self = this;
        var data = this.find('recommend').getIgnoreSelect(arguments);
        if (data) {
            Dialog.openWindow('App.recommend.RecommendInfoWindow', {
                apis: self.apis,
                _callback: function () {
                    self.find('recommend').refreshStore();
                }
            }).setValue(data);
        } else {
            Dialog.alert('请先选中一条推荐信息后再修改推荐内容');
        }
    },

    onDelDataClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.find('recommend').getIgnoreSelects(arguments);

        if (jsons) {
            Dialog.batch({
                message: '确定删除推荐内容{d}吗？',
                data: jsons,
                key: 'id',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, "id");
                        self.apis.Recommend.delContent
                            .wait(self, '正在删除推荐...')
                            .call({ids: ids}, function () {
                                self.find('recommend').refreshStore();
                            })
                    }
                }
            });
        } else {
            Dialog.alert('请选选中要删除的推荐数据');
        }
    },

    onGridpanelItemClick: function (dataview, record, item, index, e, eOpts) {
        var gid = record.get('id');
        var store = this.apis.Recommend.getContents.createPageStore({gid: gid});
        this.find('recommend').setStore(store);
        this.find('paging').bindStore(store);
        store.load();
    },

    onAfterApply: function () {
        var self = this;
        this.apis.Recommend.getGroups
            .wait(self.find('group'), '正在刷新数据...')
            .call({}, function (data) {
                data = data || [];
                var store = Ext.create('Ext.data.TreeStore', {
                    root: {
                        name: '推荐分组',
                        admin: '[无]',
                        code: '[无]',
                        id: '0',
                        expanded: true,
                        children: data
                    }
                });
                self.find('group').setStore(store);
            })
    }

});