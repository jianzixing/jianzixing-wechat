Ext.define('App.page.PageConfigPanel', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.RowNumberer',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.view.Table',
        'Ext.button.Button',
        'Ext.toolbar.Paging'
    ],

    header: false,
    defaultListenerScope: true,

    columns: [
        {
            xtype: 'gridcolumn',
            dataIndex: 'name',
            width: 300,
            text: '后台标题'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'type',
            width: 100,
            text: '内容类型',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (value == 1) {
                    return "轮播图";
                } else if (value == 2) {
                    return "网站快播";
                } else if (value == 3) {
                    return "活动分类入口";
                } else if (value == 4) {
                    return "楼层(上2下4)";
                } else if (value == 5) {
                    return "楼层(上4下4)";
                } else if (value == 6) {
                    return "商品推荐";
                } else if (value == 7) {
                    return "单图广告";
                }

            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'pos',
            width: 150,
            text: '排序'
        },
        {
            xtype: 'actioncolumn',
            dataIndex: 'id',
            text: '操作',
            align: 'center',
            items: [
                {
                    iconCls: 'x-fa fa-pencil',
                    tooltip: '修改',
                    handler: 'onUpdatePageContent'
                },
                {
                    iconCls: 'x-fa fa-sort-numeric-asc',
                    tooltip: '修改排序',
                    handler: 'onOrderPageContent'
                },
                {
                    iconCls: 'x-fa fa-times',
                    tooltip: '删除',
                    handler: 'onDeletePageContent'
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
                    icon: Resource.png('jet', 'back'),
                    text: '返回',
                    listeners: {
                        click: 'onBackClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'listChanges'),
                    text: '列出页面配置内容',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'split'),
                    text: '添加内容',
                    menu: {
                        xtype: 'menu',
                        items: [
                            {
                                xtype: 'menuitem',
                                icon: Resource.png('jet', 'editColors_dark'),
                                text: '添加轮播图',
                                listeners: {
                                    click: 'onAddLunBoClick'
                                }
                            },
                            {
                                xtype: 'menuitem',
                                icon: Resource.png('jet', 'editSource'),
                                text: '添加网站快报',
                                listeners: {
                                    click: 'onAddKuaiBaoClick'
                                }
                            },
                            {
                                xtype: 'menuitem',
                                icon: Resource.png('jet', 'editSource'),
                                text: '添加活动分类入口',
                                listeners: {
                                    click: 'onAddRuKouClick'
                                }
                            },
                            {
                                xtype: 'menuitem',
                                icon: Resource.png('jet', 'editSource'),
                                text: '添加商品推荐',
                                listeners: {
                                    click: 'onAddRecommendClick'
                                }
                            },
                            {
                                xtype: 'menuitem',
                                icon: Resource.png('jet', 'showEditorHighlighting_dark'),
                                text: '添加楼层(上2下4)',
                                listeners: {
                                    click: 'onAddLouCent24Click'
                                }
                            },
                            {
                                xtype: 'menuitem',
                                icon: Resource.png('jet', 'keymapEditor'),
                                text: '添加楼层(上4下4)',
                                listeners: {
                                    click: 'onAddLouCent44Click'
                                }
                            },
                            {
                                xtype: 'menuitem',
                                icon: Resource.png('jet', 'keymapEditor'),
                                text: '添加单图广告',
                                listeners: {
                                    click: 'onAddDanTuAdv'
                                }
                            }
                        ]
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret_dark'),
                    text: '删除页面内容',
                    listeners: {
                        click: 'onDeletePageContent'
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

    onOrderPageContent: function (grid, rowIndex, colIndex) {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            Dialog.prompt('提示', '请输入排序序号(需要大于0)', function (btn, text) {
                if (btn == 'ok' && text != '' && parseInt(text) > 0) {
                    self.apis.Page.updatePageContent
                        .wait(self, '正在修改页面...')
                        .call({object: {id: data.id, pos: text}}, function () {
                            self.loadPageContent();
                        });
                }
            }, data.pos);
        }
    },

    onUpdatePageContent: function () {
        var json = this.getIgnoreSelect(arguments);
        var self = this;
        var type = json.type;
        if (type == 1) {
            self.onAddLunBoClick(json);
        } else if (type == 2) {
            self.onAddKuaiBaoClick(json);
        } else if (type == 3) {
            self.onAddRuKouClick(json);
        } else if (type == 4) {
            self.onAddLouCent24Click(json);
        } else if (type == 5) {
            self.onAddLouCent44Click(json);
        } else if (type == 6) {
            self.onAddRecommendClick(json);
        } else if (type == 7) {
            self.onAddDanTuAdv(json);
        }
    },
    onDeletePageContent: function () {
        var json = this.getIgnoreSelect(arguments);
        var self = this;
        Dialog.batch({
            message: '确定删除{d}吗？',
            data: json,
            key: 'name',
            callback: function (btn) {
                if (btn == 'yes') {
                    self.apis.Page.deletePageContent
                        .wait(self, '正在删除内容...')
                        .call({ids: [json['id']]}, function () {
                            self.loadPageContent();
                        })
                }
            }
        });
    },

    onListClick: function (button, e, eOpts) {
        this.loadPageContent();
    },

    onBackClick: function () {
        this.parent.back();
    },

    loadPageContent: function () {
        var pageId = this.page['id'];
        var store = this.apis.Page.getPageContent.createPageStore({id: pageId});
        this.setStore(store);
        store.load();
    },

    //轮播图form
    pageContentFormWindow1: [
        {
            xtype: 'textfield',
            name: 'id',
            hidden: true,
            inputType: 'hidden'
        },
        {
            xtype: 'textfield',
            name: 'name',
            anchor: '100%',
            fieldLabel: '后台显示标题',
            allowBlank: false
        },
        {
            xtype: 'textfield',
            name: 'pos',
            anchor: '100%',
            fieldLabel: '排序',
            allowBlank: false,
            value: '99'
        },
        {
            xtype: 'gridpanel',
            name: 'lunBoPageContent',
            store: {
                data: []
            },
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'img',
                    text: '图片',
                    width: 120,
                    renderer: function (v) {
                        return '<div style="height: 50px">' +
                            (v != null ? '<img style="height: 50px;width:auto;" src="' + Resource.image(v) + '"/>' : '') +
                            '</div>'
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'link',
                    text: '链接',
                    width: 200,
                    renderer: function (v) {
                        return '<a target="_blank" href="' + v + '">' + v + '</a>';
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'pos',
                    text: '排序',
                    width: 60
                },
                {
                    xtype: 'actioncolumn',
                    dataIndex: 'id',
                    text: '操作',
                    align: 'center',
                    items: [
                        {
                            iconCls: 'x-fa fa-pencil',
                            tooltip: '修改',
                            handler: 'onUpdateLunBoItem'
                        },
                        {
                            iconCls: 'x-fa fa-times',
                            tooltip: '删除',
                            handler: 'onDeleteLunBoItem'
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
                            text: '添加轮播项',
                            listeners: {
                                click: 'onAddLunBoItem'
                            }
                        }
                    ]
                }
            ]
        }

    ],

    lunBoFormWindow: [
        {
            xtype: 'textfield',
            name: 'rowIndex',
            hidden: true,
            inputType: 'hidden',
            fieldLabel: 'ID',
        },
        {
            xtype: 'textfield',
            name: 'link',
            anchor: '100%',
            fieldLabel: '链接',
            allowBlank: false,
            value: 'http://'
        },
        {
            xtype: 'fieldcontainer',
            anchor: '100%',
            fieldLabel: '图片',
            items: [
                {
                    xtype: 'image',
                    name: 'img',
                    height: 120,
                    width: 120
                },
                {
                    xtype: 'button',
                    margin: 'auto auto auto 40px',
                    text: '选择图片',
                    listeners: {
                        click: 'onSelectImageClick'
                    }
                }
            ]
        },
        {
            xtype: 'textfield',
            name: 'pos',
            anchor: '100%',
            fieldLabel: '排序',
            inputType: 'number',
            value: '99'
        }
    ],

    onAddLunBoClick: function (formData) {
        var self = this;
        var title = '添加轮播图';
        var update = formData.type ? true : false;
        if (update) {
            title = '修改轮播图';
        }
        var form = Dialog.openFormWindow({
            title: title,
            width: 600,
            height: 500,
            formConfig: {
                autoScroll: true
            },
            items: self.pageContentFormWindow1,
            setValueCallback: function (form, json) {
                if (update) {
                    form.find('lunBoPageContent').getStore().add(JSON.parse(formData.data));
                }
                return json;
            },
            getValueCallback: function (form, json) {
                var gp = form.find('lunBoPageContent');
                var items = gp.getStore().getData().items;
                var data = [];
                if (items.length > 0) {
                    for (var i = 0; i < items.length; i++) {
                        data.push(items[i].data);
                    }
                }
                json['data'] = data;
                return json;
            },
            success: function (json, win) {
                if (json.name == '') {
                    Dialog.alert("后台显示名称不能为空");
                    return false;
                }
                if (!json.data || json.data.length == 0) {
                    Dialog.alert("轮播项不能为空");
                    return false;
                }
                json.pageId = self.page.id;
                json.type = 1;
                console.dir(json);
                if (!json.id) {
                    self.apis.Page.addPageContent
                        .wait(self, '正在添加页面内容...')
                        .call({object: json}, function () {
                            win.close();
                            self.refreshStore();
                        });
                } else {
                    self.apis.Page.updatePageContent
                        .wait(self, '正在修改页面内容...')
                        .call({object: json}, function () {
                            win.close();
                            self.refreshStore();
                        });
                }

            },
            funs: {
                onAddLunBoItem: function (button) {
                    var pageContentForm = this;
                    Dialog.openFormWindow({
                        title: '添加轮播项',
                        width: 400,
                        height: 330,
                        items: self.lunBoFormWindow,
                        setValueCallback: function (form, json) {
                            var image = form.find('img');
                            image.fileName = json['logo'];
                            image.setSrc(Resource.image(image.fileName));
                            return json;
                        },
                        getValueCallback: function (form, json) {
                            var image = form.find('img');
                            json['img'] = image.fileName;
                            return json;
                        },
                        success: function (json, win) {
                            var gridPanel = pageContentForm.find("lunBoPageContent");
                            gridPanel.getStore().add(json);
                            win.close();
                        },
                        funs: {
                            onSelectImageClick: function (button) {
                                var parent = button.ownerCt;
                                var image = parent.find('img');

                                var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
                                win.setSelectionCallback(function (files) {
                                    if (files) {
                                        image.fileName = files['fileName'];
                                        image.setSrc(Resource.image(image.fileName));
                                    }
                                })
                            }
                        }
                    });
                },

                onUpdateLunBoItem: function (grid, rowIndex, colIndex) {
                    var json = grid.getStore().getData().getAt(rowIndex).data;
                    json.rowIndex = rowIndex;
                    console.dir(json);
                    var pageContentForm = this;
                    Dialog.openFormWindow({
                        title: '修改轮播项',
                        width: 400,
                        height: 330,
                        items: self.lunBoFormWindow,
                        setValueCallback: function (form, json) {
                            var image = form.find('img');
                            image.fileName = json['img'];
                            image.setSrc(Resource.image(image.fileName));
                            return json;
                        },
                        getValueCallback: function (form, json) {
                            var image = form.find('img');
                            json['img'] = image.fileName;
                            return json;
                        },
                        success: function (json, win) {
                            var gridPanel = pageContentForm.find("lunBoPageContent");
                            var store = gridPanel.getStore();
                            var data = Ext.clone(store.getData().items);
                            store.removeAll();
                            if (data.length > 0) {
                                for (var i = 0; i < data.length; i++) {
                                    if (i == json.rowIndex) {
                                        store.add(json);
                                    } else {
                                        store.add(data[i]);
                                    }
                                }
                            }
                            win.close();
                        },
                        funs: {
                            onSelectImageClick: function (button) {
                                var parent = button.ownerCt;
                                var image = parent.find('img');

                                var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
                                win.setSelectionCallback(function (files) {
                                    if (files) {
                                        image.fileName = files['fileName'];
                                        image.setSrc(Resource.image(image.fileName));
                                    }
                                })
                            }
                        }
                    }).setValues(json);
                },
                onDeleteLunBoItem: function (grid, rowIndex, colIndex) {
                    var store = grid.getStore();
                    var data = Ext.clone(store.getData().items);
                    store.removeAll();
                    if (data.length > 0) {
                        for (var i = 0; i < data.length; i++) {
                            if (i != rowIndex) {
                                store.add(data[i]);
                            }
                        }
                    }
                }
            }
        });
        if (update) {
            form.setValues(formData);
        }
    },
    //---------轮播结束
    //------快报
    pageContentFormWindow2: [
        {
            xtype: 'textfield',
            name: 'id',
            hidden: true,
            inputType: 'hidden'
        },
        {
            xtype: 'textfield',
            name: 'name',
            anchor: '100%',
            fieldLabel: '后台显示标题',
            allowBlank: false
        },
        {
            xtype: 'textfield',
            name: 'pos',
            anchor: '100%',
            fieldLabel: '排序',
            allowBlank: false,
            value: '99'
        },
        {
            xtype: 'gridpanel',
            name: 'kuaiBaoPageContent',
            store: {
                data: []
            },
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'name',
                    text: '标题',
                    width: 150
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'link',
                    text: '链接',
                    width: 200,
                    renderer: function (v) {
                        return '<a target="_blank" href="' + v + '">' + v + '</a>';
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'pos',
                    text: '排序',
                    width: 60
                },
                {
                    xtype: 'actioncolumn',
                    dataIndex: 'id',
                    text: '操作',
                    align: 'center',
                    items: [
                        {
                            iconCls: 'x-fa fa-pencil',
                            tooltip: '修改',
                            handler: 'onUpdateKuaiBaoItem'
                        },
                        {
                            iconCls: 'x-fa fa-times',
                            tooltip: '删除',
                            handler: 'onDeleteKuaiBaoItem'
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
                            text: '添加快报项',
                            listeners: {
                                click: 'onAddLunBoItem'
                            }
                        }
                    ]
                }
            ]
        }

    ],

    kuaiBaoFormWindow: [
        {
            xtype: 'textfield',
            name: 'rowIndex',
            hidden: true,
            inputType: 'hidden'
        },
        {
            xtype: 'textfield',
            name: 'name',
            anchor: '100%',
            fieldLabel: '标题',
            allowBlank: false
        },
        {
            xtype: 'textfield',
            name: 'link',
            anchor: '100%',
            fieldLabel: '链接',
            allowBlank: false,
            value: 'http://'
        },
        {
            xtype: 'textfield',
            name: 'pos',
            anchor: '100%',
            fieldLabel: '排序',
            inputType: 'number',
            value: '99'
        }
    ],

    onAddKuaiBaoClick: function (formData) {
        var self = this;
        var title = '添加网站快报';
        var update = formData.type ? true : false;
        if (update) {
            title = '修改网站快报';
        }
        var form = Dialog.openFormWindow({
            title: title,
            width: 600,
            height: 500,
            formConfig: {
                autoScroll: true
            },
            items: self.pageContentFormWindow2,
            setValueCallback: function (form, json) {
                if (update) {
                    form.find('kuaiBaoPageContent').getStore().add(JSON.parse(formData.data));
                }
                return json;
            },
            getValueCallback: function (form, json) {
                var gp = form.find('kuaiBaoPageContent');
                var items = gp.getStore().getData().items;
                var data = [];
                if (items.length > 0) {
                    for (var i = 0; i < items.length; i++) {
                        data.push(items[i].data);
                    }
                }
                json['data'] = data;
                return json;
            },
            success: function (json, win) {
                if (json.name == '') {
                    Dialog.alert("后台显示名称不能为空");
                    return false;
                }
                if (!json.data || json.data.length == 0) {
                    Dialog.alert("快报项不能为空");
                    return false;
                }
                json.pageId = self.page.id;
                json.type = 2;
                console.dir(json);
                if (json.id) {
                    self.apis.Page.updatePageContent
                        .wait(self, '正在修改页面内容...')
                        .call({object: json}, function () {
                            win.close();
                            self.refreshStore();
                        });
                } else {
                    self.apis.Page.addPageContent
                        .wait(self, '正在添加页面内容...')
                        .call({object: json}, function () {
                            win.close();
                            self.refreshStore();
                        });
                }

            },
            funs: {
                onAddLunBoItem: function (button) {
                    var pageContentForm = this;
                    Dialog.openFormWindow({
                        title: '添加快报项',
                        width: 400,
                        height: 330,
                        items: self.kuaiBaoFormWindow,
                        setValueCallback: function (form, json) {
                            return json;
                        },
                        getValueCallback: function (form, json) {
                            return json;
                        },
                        success: function (json, win) {
                            var gridPanel = pageContentForm.find("kuaiBaoPageContent");
                            gridPanel.getStore().add(json);
                            win.close();
                        }
                    });
                },

                onUpdateKuaiBaoItem: function (grid, rowIndex, colIndex) {
                    var json = grid.getStore().getData().getAt(rowIndex).data;
                    json.rowIndex = rowIndex;
                    console.dir(json);
                    var pageContentForm = this;
                    Dialog.openFormWindow({
                        title: '修改快报项',
                        width: 400,
                        height: 330,
                        items: self.kuaiBaoFormWindow,
                        setValueCallback: function (form, json) {
                            return json;
                        },
                        getValueCallback: function (form, json) {
                            return json;
                        },
                        success: function (json, win) {
                            var gridPanel = pageContentForm.find("kuaiBaoPageContent");
                            var store = gridPanel.getStore();
                            var data = Ext.clone(store.getData().items);
                            store.removeAll();
                            if (data.length > 0) {
                                for (var i = 0; i < data.length; i++) {
                                    if (i == json.rowIndex) {
                                        store.add(json);
                                    } else {
                                        store.add(data[i]);
                                    }
                                }
                            }
                            win.close();
                        },
                        funs: {
                            onSelectImageClick: function (button) {
                                var parent = button.ownerCt;
                                var image = parent.find('img');

                                var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
                                win.setSelectionCallback(function (files) {
                                    if (files) {
                                        image.fileName = files['fileName'];
                                        image.setSrc(Resource.image(image.fileName));
                                    }
                                })
                            }
                        }
                    }).setValues(json);
                },
                onDeleteKuaiBaoItem: function (grid, rowIndex, colIndex) {
                    var store = grid.getStore();
                    var data = Ext.clone(store.getData().items);
                    store.removeAll();
                    if (data.length > 0) {
                        for (var i = 0; i < data.length; i++) {
                            if (i != rowIndex) {
                                store.add(data[i]);
                            }
                        }
                    }
                }
            }
        });
        if (update) {
            form.setValues(formData);
        }
    },
    //---------快报结束
    //------分类入口开始
    pageContentFormWindow3: [
        {
            xtype: 'textfield',
            name: 'id',
            hidden: true,
            inputType: 'hidden'
        },
        {
            xtype: 'textfield',
            name: 'name',
            anchor: '100%',
            fieldLabel: '后台显示标题',
            allowBlank: false
        },
        {
            xtype: 'textfield',
            name: 'pos',
            anchor: '100%',
            fieldLabel: '排序',
            allowBlank: false,
            value: '99'
        },
        {
            xtype: 'gridpanel',
            name: 'ruKouPageContent',
            store: {
                data: []
            },
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'name',
                    text: '标题',
                    width: 80
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'img',
                    text: '图片',
                    width: 60,
                    renderer: function (v) {
                        return '<div style="height: 50px">' +
                            (v != null ? '<img style="height: 50px;width:auto;" src="' + Resource.image(v) + '"/>' : '') +
                            '</div>'
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'link',
                    text: '链接',
                    width: 200,
                    renderer: function (v) {
                        return '<a target="_blank" href="' + v + '">' + v + '</a>';
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'pos',
                    text: '排序',
                    width: 60
                },
                {
                    xtype: 'actioncolumn',
                    dataIndex: 'id',
                    text: '操作',
                    align: 'center',
                    items: [
                        {
                            iconCls: 'x-fa fa-pencil',
                            tooltip: '修改',
                            handler: 'onUpdateLunBoItem'
                        },
                        {
                            iconCls: 'x-fa fa-times',
                            tooltip: '删除',
                            handler: 'onDeleteLunBoItem'
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
                            text: '添加入口项',
                            listeners: {
                                click: 'onAddRuKouItem'
                            }
                        }
                    ]
                }
            ]
        }
    ],

    ruKouFormWindow: [
        {
            xtype: 'textfield',
            name: 'rowIndex',
            hidden: true,
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
            xtype: 'textfield',
            name: 'link',
            anchor: '100%',
            fieldLabel: '链接',
            allowBlank: false,
            value: 'http://'
        },
        {
            xtype: 'fieldcontainer',
            anchor: '100%',
            fieldLabel: '图片',
            items: [
                {
                    xtype: 'image',
                    name: 'img',
                    height: 120,
                    width: 120
                },
                {
                    xtype: 'button',
                    margin: 'auto auto auto 40px',
                    text: '选择图片',
                    listeners: {
                        click: 'onSelectImageClick'
                    }
                }
            ]
        },
        {
            xtype: 'textfield',
            name: 'pos',
            anchor: '100%',
            fieldLabel: '排序',
            inputType: 'number',
            value: '99'
        }
    ],

    onAddRuKouClick: function (formData) {
        var self = this;
        var update = formData.type ? true : false;
        var title = '添加分类入口';
        if (update) {
            title = '修改分类入口';
        }
        var form = Dialog.openFormWindow({
            title: title,
            width: 600,
            height: 500,
            formConfig: {
                autoScroll: true
            },
            items: self.pageContentFormWindow3,
            setValueCallback: function (form, json) {
                if (update) {
                    form.find('ruKouPageContent').getStore().add(JSON.parse(formData.data));
                }
                return json;
            },
            getValueCallback: function (form, json) {
                var gp = form.find('ruKouPageContent');
                var items = gp.getStore().getData().items;
                var data = [];
                if (items.length > 0) {
                    for (var i = 0; i < items.length; i++) {
                        data.push(items[i].data);
                    }
                }
                json['data'] = data;
                return json;
            },
            success: function (json, win) {
                if (json.name == '') {
                    Dialog.alert("后台显示名称不能为空");
                    return false;
                }
                if (!json.data || json.data.length == 0) {
                    Dialog.alert("分类入口项不能为空");
                    return false;
                }
                json.pageId = self.page.id;
                json.type = 3;
                console.dir(json);
                if (json.id) {
                    self.apis.Page.updatePageContent
                        .wait(self, '正在修改页面内容...')
                        .call({object: json}, function () {
                            win.close();
                            self.refreshStore();
                        });
                } else {
                    self.apis.Page.addPageContent
                        .wait(self, '正在添加页面内容...')
                        .call({object: json}, function () {
                            win.close();
                            self.refreshStore();
                        });
                }

            },
            funs: {
                onAddRuKouItem: function (button) {
                    var pageContentForm = this;
                    Dialog.openFormWindow({
                        title: '添加分类入口项',
                        width: 400,
                        height: 380,
                        items: self.ruKouFormWindow,
                        setValueCallback: function (form, json) {
                            var image = form.find('img');
                            image.fileName = json['logo'];
                            image.setSrc(Resource.image(image.fileName));
                            return json;
                        },
                        getValueCallback: function (form, json) {
                            var image = form.find('img');
                            json['img'] = image.fileName;
                            return json;
                        },
                        success: function (json, win) {
                            var gridPanel = pageContentForm.find("ruKouPageContent");
                            gridPanel.getStore().add(json);
                            win.close();
                        },
                        funs: {
                            onSelectImageClick: function (button) {
                                var parent = button.ownerCt;
                                var image = parent.find('img');

                                var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
                                win.setSelectionCallback(function (files) {
                                    if (files) {
                                        image.fileName = files['fileName'];
                                        image.setSrc(Resource.image(image.fileName));
                                    }
                                })
                            }
                        }
                    });
                },

                onUpdateLunBoItem: function (grid, rowIndex, colIndex) {
                    var json = grid.getStore().getData().getAt(rowIndex).data;
                    json.rowIndex = rowIndex;
                    console.dir(json);
                    var pageContentForm = this;
                    Dialog.openFormWindow({
                        title: '修改分类入口项',
                        width: 400,
                        height: 380,
                        formConfig: {
                            autoScroll: true
                        },
                        items: self.ruKouFormWindow,
                        setValueCallback: function (form, json) {
                            var image = form.find('img');
                            image.fileName = json['img'];
                            image.setSrc(Resource.image(image.fileName));
                            return json;
                        },
                        getValueCallback: function (form, json) {
                            var image = form.find('img');
                            json['img'] = image.fileName;
                            return json;
                        },
                        success: function (json, win) {
                            var gridPanel = pageContentForm.find("ruKouPageContent");
                            var store = gridPanel.getStore();
                            var data = Ext.clone(store.getData().items);
                            store.removeAll();
                            if (data.length > 0) {
                                for (var i = 0; i < data.length; i++) {
                                    if (i == json.rowIndex) {
                                        store.add(json);
                                    } else {
                                        store.add(data[i]);
                                    }
                                }
                            }
                            win.close();
                        },
                        funs: {
                            onSelectImageClick: function (button) {
                                var parent = button.ownerCt;
                                var image = parent.find('img');

                                var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
                                win.setSelectionCallback(function (files) {
                                    if (files) {
                                        image.fileName = files['fileName'];
                                        image.setSrc(Resource.image(image.fileName));
                                    }
                                })
                            }
                        }
                    }).setValues(json);
                },
                onDeleteLunBoItem: function (grid, rowIndex, colIndex) {
                    var store = grid.getStore();
                    var data = Ext.clone(store.getData().items);
                    store.removeAll();
                    if (data.length > 0) {
                        for (var i = 0; i < data.length; i++) {
                            if (i != rowIndex) {
                                store.add(data[i]);
                            }
                        }
                    }
                }
            }
        });
        if (update) {
            form.setValues(formData);
        }
    },
    //---------分类入口结束
    //-------楼层（上二下四）
    pageContentFormWindow4: [
        {
            xtype: 'textfield',
            name: 'id',
            hidden: true,
            inputType: 'hidden'
        },
        {
            xtype: 'textfield',
            name: 'name',
            anchor: '100%',
            fieldLabel: '后台显示标题',
            allowBlank: false
        },
        {
            xtype: 'textfield',
            name: 'pos',
            anchor: '100%',
            fieldLabel: '排序',
            allowBlank: false,
            value: '99'
        },
        {
            xtype: 'textfield',
            name: 'title',
            anchor: '100%',
            fieldLabel: '前台显示标题',
            allowBlank: false
        },
        {
            xtype: 'fieldset',
            title: '上一',
            items: [
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: "title1",
                    fieldLabel: '标题',
                    allowBlank: false
                },
                {
                    xtype: 'fieldcontainer',
                    anchor: '100%',
                    fieldLabel: '图片',
                    items: [
                        {
                            xtype: 'image',
                            name: 'img1',
                            height: 40,
                            width: 40
                        },
                        {
                            xtype: 'button',
                            margin: 'auto auto auto 40px',
                            text: '选择图片',
                            imgNo: 1,
                            listeners: {
                                click: 'onSelectImageClick'
                            }
                        }
                    ]
                },
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: 'link1',
                    fieldLabel: '链接地址',
                    allowBlank: false
                }
            ]
        },
        {
            xtype: 'fieldset',
            title: '上二',
            items: [
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: 'title2',
                    fieldLabel: '标题',
                    allowBlank: false
                },
                {
                    xtype: 'fieldcontainer',
                    anchor: '100%',
                    fieldLabel: '图片',
                    items: [
                        {
                            xtype: 'image',
                            name: 'img2',
                            height: 40,
                            width: 40
                        },
                        {
                            xtype: 'button',
                            margin: 'auto auto auto 40px',
                            text: '选择图片',
                            imgNo: 2,
                            listeners: {
                                click: 'onSelectImageClick'
                            }
                        }
                    ]
                },
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: 'link2',
                    fieldLabel: '链接地址',
                    allowBlank: false
                }
            ]
        },
        {
            xtype: 'fieldset',
            title: '下一',
            items: [
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: 'title3',
                    fieldLabel: '标题',
                    allowBlank: false
                },
                {
                    xtype: 'fieldcontainer',
                    anchor: '100%',
                    fieldLabel: '图片',
                    items: [
                        {
                            xtype: 'image',
                            name: 'img3',
                            height: 40,
                            width: 40
                        },
                        {
                            xtype: 'button',
                            margin: 'auto auto auto 40px',
                            text: '选择图片',
                            imgNo: 3,
                            listeners: {
                                click: 'onSelectImageClick'
                            }
                        }
                    ]
                },
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: "link3",
                    fieldLabel: '链接地址',
                    allowBlank: false
                }
            ]
        },
        {
            xtype: 'fieldset',
            title: '下二',
            items: [
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: 'title4',
                    fieldLabel: '标题',
                    allowBlank: false
                },
                {
                    xtype: 'fieldcontainer',
                    anchor: '100%',
                    fieldLabel: '图片',
                    items: [
                        {
                            xtype: 'image',
                            name: 'img4',
                            height: 40,
                            width: 40
                        },
                        {
                            xtype: 'button',
                            margin: 'auto auto auto 40px',
                            text: '选择图片',
                            imgNo: 4,
                            listeners: {
                                click: 'onSelectImageClick'
                            }
                        }
                    ]
                },
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: 'link4',
                    fieldLabel: '链接地址',
                    allowBlank: false
                }
            ]
        },
        {
            xtype: 'fieldset',
            title: '下三',
            items: [
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: 'title5',
                    fieldLabel: '标题',
                    allowBlank: false
                },
                {
                    xtype: 'fieldcontainer',
                    anchor: '100%',
                    fieldLabel: '图片',
                    items: [
                        {
                            xtype: 'image',
                            name: 'img5',
                            height: 40,
                            width: 40
                        },
                        {
                            xtype: 'button',
                            margin: 'auto auto auto 40px',
                            imgNo: 5,
                            text: '选择图片',
                            listeners: {
                                click: 'onSelectImageClick'
                            }
                        }
                    ]
                },
                {
                    xtype: 'textfield',
                    name: 'link5',
                    anchor: '100%',
                    fieldLabel: '链接地址',
                    allowBlank: false
                }
            ]
        },
        {
            xtype: 'fieldset',
            title: '下四',
            items: [
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: 'title6',
                    fieldLabel: '标题',
                    allowBlank: false
                },
                {
                    xtype: 'fieldcontainer',
                    anchor: '100%',
                    fieldLabel: '图片',
                    items: [
                        {
                            xtype: 'image',
                            name: 'img6',
                            height: 40,
                            width: 40
                        },
                        {
                            xtype: 'button',
                            margin: 'auto auto auto 40px',
                            text: '选择图片',
                            imgNo: 6,
                            listeners: {
                                click: 'onSelectImageClick'
                            }
                        }
                    ]
                },
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: 'link6',
                    fieldLabel: '链接地址',
                    allowBlank: false
                }
            ]
        }
    ],
    onAddLouCent24Click: function (formData) {
        var self = this;
        var update = !!formData.type;
        var title = '添加楼层（上二下四）';
        if (update) {
            title='修改楼层（上二下四）';
        }
        var form=Dialog.openFormWindow({
            title: title,
            width: 600,
            height: 500,
            items: self.pageContentFormWindow4,
            formConfig: {
                autoScroll: true
            },
            setValueCallback: function (form, json) {
                var data= JSON.parse(json.data);
                form.find('img1').fileName = data['img1'];
                form.find('img1').setSrc(Resource.image(data['img1']));

                form.find('img2').fileName = data['img2'];
                form.find('img2').setSrc(Resource.image(data['img2']));

                form.find('img3').fileName = data['img3'];
                form.find('img3').setSrc(Resource.image(data['img3']));

                form.find('img4').fileName = data['img4'];
                form.find('img4').setSrc(Resource.image(data['img4']));

                form.find('img5').fileName = data['img5'];
                form.find('img5').setSrc(Resource.image(data['img5']));

                form.find('img6').fileName = data['img6'];
                form.find('img6').setSrc(Resource.image(data['img6']));

                json=Ext.apply(json, data);
                return json;
            },
            getValueCallback: function (form, json) {
                json['img1'] = form.find('img1').fileName;
                json['img2'] = form.find('img2').fileName;
                json['img3'] = form.find('img3').fileName;
                json['img4'] = form.find('img4').fileName;
                json['img5'] = form.find('img5').fileName;
                json['img6'] = form.find('img6').fileName;
                return json;
            },
            success: function (json, win) {
                if (json.name == '') {
                    Dialog.alert("后台显示标题不能为空");
                    return false;
                }
                if (json.title == '') {
                    Dialog.alert("前台显示标题不能为空");
                    return false;
                }
                json.pageId = self.page.id;
                json.type = 4;
                var data = {
                    title: json.title
                };
                for (var i = 1; i <= 6; i++) {
                    data['title' + i] = json['title' + i];
                    data['img' + i] = json['img' + i];
                    data['link' + i] = json['link' + i];
                }
                json.data = data;
                console.dir(json);
                if(update){
                    self.apis.Page.updatePageContent
                        .wait(self, '正在添加页面内容...')
                        .call({object: json}, function () {
                            win.close();
                            self.refreshStore();
                        });
                }else{
                    self.apis.Page.addPageContent
                        .wait(self, '正在添加页面内容...')
                        .call({object: json}, function () {
                            win.close();
                            self.refreshStore();
                        });
                }

            },
            funs: {
                onSelectImageClick: function (button) {
                    var parent = button.ownerCt;
                    var image = parent.find('img' + button.imgNo);

                    var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
                    win.setSelectionCallback(function (files) {
                        if (files) {
                            image.fileName = files['fileName'];
                            image.setSrc(Resource.image(image.fileName));
                        }
                    })
                }
            }
        });
        if(update){
            form.setValues(formData);
        }
    },
    //-------楼层（上二下四） end
    //-------楼层（上四下四）
    pageContentFormWindow5: [
        {
            xtype: 'textfield',
            name: 'id',
            hidden: true,
            inputType: 'hidden'
        },
        {
            xtype: 'textfield',
            name: 'name',
            anchor: '100%',
            fieldLabel: '后台显示标题',
            allowBlank: false
        },
        {
            xtype: 'textfield',
            name: 'pos',
            anchor: '100%',
            fieldLabel: '排序',
            allowBlank: false,
            value: '99'
        },
        {
            xtype: 'textfield',
            name: 'title',
            anchor: '100%',
            fieldLabel: '前台显示标题',
            allowBlank: false
        },
        {
            xtype: 'fieldset',
            title: '上一',
            items: [
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: "title1",
                    fieldLabel: '标题',
                    allowBlank: false
                },
                {
                    xtype: 'fieldcontainer',
                    anchor: '100%',
                    fieldLabel: '图片',
                    items: [
                        {
                            xtype: 'image',
                            name: 'img1',
                            height: 40,
                            width: 40
                        },
                        {
                            xtype: 'button',
                            margin: 'auto auto auto 40px',
                            text: '选择图片',
                            imgNo: 1,
                            listeners: {
                                click: 'onSelectImageClick'
                            }
                        }
                    ]
                },
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: 'link1',
                    fieldLabel: '链接地址',
                    allowBlank: false
                }
            ]
        },
        {
            xtype: 'fieldset',
            title: '上二',
            items: [
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: 'title2',
                    fieldLabel: '标题',
                    allowBlank: false
                },
                {
                    xtype: 'fieldcontainer',
                    anchor: '100%',
                    fieldLabel: '图片',
                    items: [
                        {
                            xtype: 'image',
                            name: 'img2',
                            height: 40,
                            width: 40
                        },
                        {
                            xtype: 'button',
                            margin: 'auto auto auto 40px',
                            text: '选择图片',
                            imgNo: 2,
                            listeners: {
                                click: 'onSelectImageClick'
                            }
                        }
                    ]
                },
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: 'link2',
                    fieldLabel: '链接地址',
                    allowBlank: false
                }
            ]
        },
        {
            xtype: 'fieldset',
            title: '上三',
            items: [
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: 'title3',
                    fieldLabel: '标题',
                    allowBlank: false
                },
                {
                    xtype: 'fieldcontainer',
                    anchor: '100%',
                    fieldLabel: '图片',
                    items: [
                        {
                            xtype: 'image',
                            name: 'img3',
                            height: 40,
                            width: 40
                        },
                        {
                            xtype: 'button',
                            margin: 'auto auto auto 40px',
                            text: '选择图片',
                            imgNo: 3,
                            listeners: {
                                click: 'onSelectImageClick'
                            }
                        }
                    ]
                },
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: 'link3',
                    fieldLabel: '链接地址',
                    allowBlank: false
                }
            ]
        },
        {
            xtype: 'fieldset',
            title: '上四',
            items: [
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: 'title4',
                    fieldLabel: '标题',
                    allowBlank: false
                },
                {
                    xtype: 'fieldcontainer',
                    anchor: '100%',
                    fieldLabel: '图片',
                    items: [
                        {
                            xtype: 'image',
                            name: 'img4',
                            height: 40,
                            width: 40
                        },
                        {
                            xtype: 'button',
                            margin: 'auto auto auto 40px',
                            text: '选择图片',
                            imgNo: 4,
                            listeners: {
                                click: 'onSelectImageClick'
                            }
                        }
                    ]
                },
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: 'link4',
                    fieldLabel: '链接地址',
                    allowBlank: false
                }
            ]
        },
        {
            xtype: 'fieldset',
            title: '下一',
            items: [
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: 'title5',
                    fieldLabel: '标题',
                    allowBlank: false
                },
                {
                    xtype: 'fieldcontainer',
                    anchor: '100%',
                    fieldLabel: '图片',
                    items: [
                        {
                            xtype: 'image',
                            name: 'img5',
                            height: 40,
                            width: 40
                        },
                        {
                            xtype: 'button',
                            margin: 'auto auto auto 40px',
                            text: '选择图片',
                            imgNo: 5,
                            listeners: {
                                click: 'onSelectImageClick'
                            }
                        }
                    ]
                },
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: "link5",
                    fieldLabel: '链接地址',
                    allowBlank: false
                }
            ]
        },
        {
            xtype: 'fieldset',
            title: '下二',
            items: [
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: 'title6',
                    fieldLabel: '标题',
                    allowBlank: false
                },
                {
                    xtype: 'fieldcontainer',
                    anchor: '100%',
                    fieldLabel: '图片',
                    items: [
                        {
                            xtype: 'image',
                            name: 'img6',
                            height: 40,
                            width: 40
                        },
                        {
                            xtype: 'button',
                            margin: 'auto auto auto 40px',
                            text: '选择图片',
                            imgNo: 6,
                            listeners: {
                                click: 'onSelectImageClick'
                            }
                        }
                    ]
                },
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: 'link6',
                    fieldLabel: '链接地址',
                    allowBlank: false
                }
            ]
        },
        {
            xtype: 'fieldset',
            title: '下三',
            items: [
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: 'title7',
                    fieldLabel: '标题',
                    allowBlank: false
                },
                {
                    xtype: 'fieldcontainer',
                    anchor: '100%',
                    fieldLabel: '图片',
                    items: [
                        {
                            xtype: 'image',
                            name: 'img7',
                            height: 40,
                            width: 40
                        },
                        {
                            xtype: 'button',
                            margin: 'auto auto auto 40px',
                            imgNo: 7,
                            text: '选择图片',
                            listeners: {
                                click: 'onSelectImageClick'
                            }
                        }
                    ]
                },
                {
                    xtype: 'textfield',
                    name: 'link7',
                    anchor: '100%',
                    fieldLabel: '链接地址',
                    allowBlank: false
                }
            ]
        },
        {
            xtype: 'fieldset',
            title: '下四',
            items: [
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: 'title8',
                    fieldLabel: '标题',
                    allowBlank: false
                },
                {
                    xtype: 'fieldcontainer',
                    anchor: '100%',
                    fieldLabel: '图片',
                    items: [
                        {
                            xtype: 'image',
                            name: 'img8',
                            height: 40,
                            width: 40
                        },
                        {
                            xtype: 'button',
                            margin: 'auto auto auto 40px',
                            text: '选择图片',
                            imgNo: 8,
                            listeners: {
                                click: 'onSelectImageClick'
                            }
                        }
                    ]
                },
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    name: 'link8',
                    fieldLabel: '链接地址',
                    allowBlank: false
                }
            ]
        }
    ],

    onAddLouCent44Click: function (formData) {
        var self = this;
        var update=!!formData.type;
        var title='添加楼层（上四下四）';
        if(update){
            title='修改楼层（上四下四）'
        }
        var form= Dialog.openFormWindow({
            title: title,
            width: 600,
            height: 500,
            items: self.pageContentFormWindow5,
            formConfig: {
                autoScroll: true
            },
            setValueCallback: function (form, json) {
                var data= JSON.parse(json.data);
                form.find('img1').fileName = data['img1'];
                form.find('img1').setSrc(Resource.image(data['img1']));

                form.find('img2').fileName = data['img2'];
                form.find('img2').setSrc(Resource.image(data['img2']));

                form.find('img3').fileName = data['img3'];
                form.find('img3').setSrc(Resource.image(data['img3']));

                form.find('img4').fileName = data['img4'];
                form.find('img4').setSrc(Resource.image(data['img4']));

                form.find('img5').fileName = data['img5'];
                form.find('img5').setSrc(Resource.image(data['img5']));

                form.find('img6').fileName = data['img6'];
                form.find('img6').setSrc(Resource.image(data['img6']));

                form.find('img7').fileName = data['img7'];
                form.find('img7').setSrc(Resource.image(data['img7']));

                form.find('img8').fileName = data['img8'];
                form.find('img8').setSrc(Resource.image(data['img8']));

                json=Ext.apply(json, data);
                return json;
            },
            getValueCallback: function (form, json) {
                json['img1'] = form.find('img1').fileName;
                json['img2'] = form.find('img2').fileName;
                json['img3'] = form.find('img3').fileName;
                json['img4'] = form.find('img4').fileName;
                json['img5'] = form.find('img5').fileName;
                json['img6'] = form.find('img6').fileName;
                json['img7'] = form.find('img7').fileName;
                json['img8'] = form.find('img8').fileName;
                return json;
            },
            success: function (json, win) {
                if (json.name == '') {
                    Dialog.alert("后台显示标题不能为空");
                    return false;
                }
                if (json.title == '') {
                    Dialog.alert("前台显示标题不能为空");
                    return false;
                }
                json.pageId = self.page.id;
                json.type = 5;
                var data = {
                    title: json.title
                };
                for (var i = 1; i <= 8; i++) {
                    data['title' + i] = json['title' + i];
                    data['img' + i] = json['img' + i];
                    data['link' + i] = json['link' + i];
                }
                json.data = data;
                console.dir(json);
                if(json.id){
                    self.apis.Page.updatePageContent
                        .wait(self, '正在修改页面内容...')
                        .call({object: json}, function () {
                            win.close();
                            self.refreshStore();
                        });
                }else{
                    self.apis.Page.addPageContent
                        .wait(self, '正在添加页面内容...')
                        .call({object: json}, function () {
                            win.close();
                            self.refreshStore();
                        });
                }

            },
            funs: {
                onSelectImageClick: function (button) {
                    var parent = button.ownerCt;
                    var image = parent.find('img' + button.imgNo);

                    var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
                    win.setSelectionCallback(function (files) {
                        if (files) {
                            image.fileName = files['fileName'];
                            image.setSrc(Resource.image(image.fileName));
                        }
                    })
                }
            }
        });
        if(update){
            form.setValues(formData);
        }
    },
    //-------楼层（上四下四） end
    //-------推荐商品
    pageContentFormWindow6: [
        {
            xtype: 'textfield',
            name: 'id',
            hidden: true,
            inputType: 'hidden'
        },
        {
            xtype: 'textfield',
            name: 'name',
            anchor: '100%',
            fieldLabel: '后台显示标题',
            allowBlank: false
        },
        {
            xtype: 'textfield',
            name: 'pos',
            anchor: '100%',
            fieldLabel: '排序',
            allowBlank: false,
            value: '99'
        },
        {
            xtype: 'textfield',
            name: 'title',
            anchor: '100%',
            fieldLabel: '前台显示标题',
            allowBlank: false
        },
        {
            xtype: 'gridpanel',
            name: 'recommendPageContent',
            store: {
                data: []
            },
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'goodsId',
                    hidden: true
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'img',
                    text: '图片',
                    width: 60,
                    renderer: function (v) {
                        return '<div style="height: 50px">' +
                            (v != null ? '<img style="height: 50px;width:auto;" src="' + Resource.image(v) + '"/>' : '') +
                            '</div>'
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'name',
                    text: '商品名称',
                    width: 260,
                    renderer: function (v) {
                        return '<a target="_blank" href="http://mywx.jianzixing.com.cn/wx/goods_detail.jhtml?id=' + v.goodsId + '">' + v + '</a>';
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'pos',
                    text: '排序',
                    width: 80
                },
                {
                    xtype: 'actioncolumn',
                    dataIndex: 'id',
                    text: '操作',
                    align: 'center',
                    items: [
                        {
                            iconCls: 'x-fa fa-sort-numeric-asc',
                            tooltip: '修改排序',
                            handler: 'onOrderRecommendItem'
                        },
                        {
                            iconCls: 'x-fa fa-times',
                            tooltip: '删除',
                            handler: 'onDeleteRecommendItem'
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
                            text: '添加商品',
                            listeners: {
                                click: 'onAddRecommendItem'
                            }
                        }
                    ]
                }
            ]
        }

    ],

    onAddRecommendClick: function (formData) {
        var self = this;
        var update = !!formData.type;
        var title = "添加商品推荐";
        if (update) {
            title = "修改商品推荐";
        }
        var form = Dialog.openFormWindow({
            title: title,
            width: 600,
            height: 500,
            formConfig: {
                autoScroll: true
            },
            items: self.pageContentFormWindow6,
            setValueCallback: function (form, json) {
                if (update) {
                    var data = JSON.parse(formData.data);
                    form.find('recommendPageContent').getStore().add(data.data);
                    form.find('title').setValue(data.title);
                }
                return json;
            },
            getValueCallback: function (form, json) {
                var gp = form.find('recommendPageContent');
                var items = gp.getStore().getData().items;
                var data = [];
                if (items.length > 0) {
                    for (var i = 0; i < items.length; i++) {
                        data.push(items[i].data);
                    }
                }
                json['data'] = data;
                return json;
            },
            success: function (json, win) {
                if (json.name == '') {
                    Dialog.alert("后台显示名称不能为空");
                    return false;
                }
                if (json.title == '') {
                    Dialog.alert("前台显示名称不能为空");
                    return false;
                }
                if (!json.data || json.data.length == 0) {
                    Dialog.alert("商品推荐项不能为空");
                    return false;
                }
                var jsonData = {
                    title: json.title,
                    data: json.data
                };
                json.data = jsonData;
                json.pageId = self.page.id;
                json.type = 6;
                console.dir(json);
                if (json.id) {
                    self.apis.Page.updatePageContent
                        .wait(self, '正在修改页面内容...')
                        .call({object: json}, function () {
                            win.close();
                            self.refreshStore();
                        });
                } else {
                    self.apis.Page.addPageContent
                        .wait(self, '正在添加页面内容...')
                        .call({object: json}, function () {
                            win.close();
                            self.refreshStore();
                        });
                }

            },
            funs: {
                onAddRecommendItem: function (button) {
                    var pageContentForm = this;
                    var goods = Dialog.openWindow('App.page.RecommendProductWindow', {
                        apis: this.apis,
                        _callSelectGoods: function (vs) {
                            var gp = pageContentForm.find('recommendPageContent');
                            var data = Ext.clone(gp.getStore().getData().items);
                            if (vs && vs.length > 0) {
                                if (data.length == 0) {
                                    for (var i = 0; i < vs.length; i++) {
                                        gp.getStore().add({
                                            goodsId: vs[i].id,
                                            img: vs[i].fileName,
                                            name: vs[i].name,
                                            pos: 99
                                        });
                                    }
                                } else {
                                    for (var j = 0; j < vs.length; j++) {
                                        var has = false;
                                        for (var k = 0; k < data.length; k++) {
                                            if (vs[j].id == data[k].data.goodsId) {
                                                has = true;
                                                break;
                                            }
                                        }
                                        if (!has) {
                                            gp.getStore().add({
                                                goodsId: vs[j].id,
                                                img: vs[j].fileName,
                                                name: vs[j].name,
                                                pos: 99
                                            });
                                        }
                                    }

                                }
                            }

                        }
                    });
                    var store = self.apis.Goods.getGoods.createPageStore();
                    goods.setValue(store)
                },
                onOrderRecommendItem: function (grid, rowIndex, colIndex) {
                    var pos = grid.getStore().getData().items[rowIndex].data.pos;
                    Dialog.prompt('提示', '请输入排序序号(需要大于0)', function (btn, text) {
                        if (btn == 'ok' && text != '' && parseInt(text) > 0) {
                            var store = grid.getStore();
                            var data = Ext.clone(store.getData().items);
                            store.removeAll();
                            if (data.length > 0) {
                                for (var i = 0; i < data.length; i++) {
                                    if (i != rowIndex) {
                                        store.add(data[i]);
                                    } else {
                                        var item = data[i].data;
                                        item.pos = text;
                                        store.add(item);
                                    }
                                }
                            }
                        }
                    }, pos);
                },
                onDeleteRecommendItem: function (grid, rowIndex, colIndex) {
                    var store = grid.getStore();
                    var data = Ext.clone(store.getData().items);
                    store.removeAll();
                    if (data.length > 0) {
                        for (var i = 0; i < data.length; i++) {
                            if (i != rowIndex) {
                                store.add(data[i]);
                            }
                        }
                    }
                }
            }
        });
        if (update) {
            form.setValues(formData);
        }
    },
    //---------推荐商品
    pageContentFormWindow7: [
        {
            xtype: 'textfield',
            name: 'id',
            hidden: true,
            inputType: 'hidden',
            fieldLabel: 'ID',
        },
        {
            xtype: 'textfield',
            name: 'name',
            anchor: '100%',
            fieldLabel: '后台标题',
            allowBlank: false
        },
        {
            xtype: 'textfield',
            name: 'link',
            anchor: '100%',
            fieldLabel: '链接',
            allowBlank: false,
            value: 'http://'
        },
        {
            xtype: 'fieldcontainer',
            anchor: '100%',
            fieldLabel: '图片',
            items: [
                {
                    xtype: 'image',
                    name: 'img',
                    height: 120,
                    width: 120
                },
                {
                    xtype: 'button',
                    margin: 'auto auto auto 40px',
                    text: '选择图片',
                    listeners: {
                        click: 'onSelectImageClick'
                    }
                }
            ]
        },
        {
            xtype: 'textfield',
            name: 'pos',
            anchor: '100%',
            fieldLabel: '排序',
            inputType: 'number',
            value: '99'
        }
    ],

    onAddDanTuAdv :function(formData){
        var self = this;
        var update = !!formData.type;
        var title = "添加单图广告";
        if (update) {
            title = "修改单图广告";
        }
        var form = Dialog.openFormWindow({
            title: title,
            width: 400,
            height: 390,
            formConfig: {
                autoScroll: true
            },
            items: self.pageContentFormWindow7,
            setValueCallback: function (form, json) {
                var image = form.find('img');
                if(update){
                    var data = JSON.parse(formData.data);
                    image.fileName = data.img;
                    image.setSrc(Resource.image(data.img));
                }else{
                    image.fileName = json['img'];
                    image.setSrc(Resource.image(image.fileName));
                }
                return json;
            },
            getValueCallback: function (form, json) {
                var image = form.find('img');
                json['img'] = image.fileName;
                return json;
            },
            success: function (json, win) {
                if(json.name==''){
                    Dialog.alert("后台标题不能为空");
                    return false;
                }
                json.pageId = self.page.id;
                json.type = 7;
                var d={
                    img: json.img,
                    link: json.link
                };
                json.data=d;
                console.dir(json);
                if (json.id) {
                    self.apis.Page.updatePageContent
                        .wait(self, '正在修改页面内容...')
                        .call({object: json}, function () {
                            win.close();
                            self.refreshStore();
                        });
                } else {
                    self.apis.Page.addPageContent
                        .wait(self, '正在添加页面内容...')
                        .call({object: json}, function () {
                            win.close();
                            self.refreshStore();
                        });
                }
            },
            funs: {
                onSelectImageClick: function (button) {
                    var parent = button.ownerCt;
                    var image = parent.find('img');

                    var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
                    win.setSelectionCallback(function (files) {
                        if (files) {
                            image.fileName = files['fileName'];
                            image.setSrc(Resource.image(image.fileName));
                        }
                    })
                }
            }
        });
        if (update) {
            form.setValues(formData);
        }
    }
});