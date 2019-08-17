Ext.define('App.recommend.RecommendInfoWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.ComboBox',
        'Ext.form.FieldContainer',
        'Ext.button.Button',
        'Ext.Img',
        'Ext.toolbar.Toolbar'
    ],

    autoShow: true,
    height: 450,
    width: 700,
    layout: 'fit',
    title: '添加推荐',
    defaultListenerScope: true,

    items: [
        {
            xtype: 'form',
            name: 'form',
            border: false,
            bodyPadding: 10,
            items: [
                {
                    xtype: 'hiddenfield',
                    name: 'id'
                },
                {
                    xtype: 'combobox',
                    name: 'type',
                    anchor: '100%',
                    fieldLabel: '推荐类型*',
                    allowBlank: false,
                    displayField: 'name',
                    valueField: 'type',
                    editable: false,
                    store: {
                        data: [
                            {name: '链接地址', type: 0},
                            {name: '标准内容', type: 10},
                            {name: '新闻', type: 11},
                            {name: '产品', type: 12},
                            {name: '解决方案', type: 13},
                            {name: '文档', type: 14}
                        ]
                    },
                    listeners: {
                        change: 'onComboboxChange'
                    }
                },
                {
                    xtype: 'fieldcontainer',
                    name: 'selectButtons',
                    hidden: true,
                    fieldLabel: '选择推荐内容',
                    items: [
                        {
                            xtype: 'button',
                            margin: '3 0 0 0',
                            text: '点击选择推荐内容',
                            listeners: {
                                click: 'onSelectContentClick'
                            }
                        }
                    ]
                },
                {
                    xtype: 'hiddenfield',
                    name: 'value',
                    value: 0
                },
                {
                    xtype: 'fieldcontainer',
                    height: 32,
                    layout: 'fit',
                    fieldLabel: '推荐标题*',
                    items: [
                        {
                            xtype: 'panel',
                            border: false,
                            layout: 'border',
                            items: [
                                {
                                    xtype: 'panel',
                                    region: 'center',
                                    border: false,
                                    layout: 'fit',
                                    items: [
                                        {
                                            xtype: 'textfield',
                                            name: 'title',
                                            allowBlank: false
                                        }
                                    ]
                                },
                                {
                                    xtype: 'panel',
                                    name: 'refreshTitle',
                                    hidden: true,
                                    region: 'east',
                                    border: false,
                                    width: 80,
                                    layout: 'fit',
                                    items: [
                                        {
                                            xtype: 'button',
                                            margin: '0 0 0 10',
                                            text: '刷新',
                                            listeners: {
                                                click: 'onRefreshTitleClick'
                                            }
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    name: 'urlField',
                    height: 32,
                    layout: 'fit',
                    fieldLabel: '推荐链接地址*',
                    items: [
                        {
                            xtype: 'panel',
                            border: false,
                            layout: 'border',
                            items: [
                                {
                                    xtype: 'panel',
                                    region: 'center',
                                    border: false,
                                    layout: 'fit',
                                    items: [
                                        {
                                            xtype: 'textfield',
                                            name: 'url'
                                        }
                                    ]
                                },
                                {
                                    xtype: 'panel',
                                    name: 'selectTemplateAddress',
                                    region: 'east',
                                    border: false,
                                    width: 80,
                                    layout: 'fit',
                                    items: [
                                        {
                                            xtype: 'button',
                                            margin: '0 0 0 10',
                                            text: '选择',
                                            listeners: {
                                                click: 'onSelectHtmlPageClick'
                                            }
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    anchor: '100%',
                    layout: 'table',
                    fieldLabel: '新闻封面',
                    items: [
                        {
                            xtype: 'image',
                            name: 'image',
                            style: {
                                maxWidth: '100px',
                                maxHeight: '100px'
                            },
                            src: '/admin/image/exicon/nopic_100.gif'
                        },
                        {
                            xtype: 'button',
                            margin: 'auto auto auto 100px',
                            text: '选择图片文件',
                            listeners: {
                                click: 'onSelectImageClick'
                            }
                        },
                        {
                            xtype: 'hiddenfield',
                            name: 'cover'
                        }
                    ]
                },
                {
                    xtype: 'radiogroup',
                    fieldLabel: '是否置顶',
                    items: [
                        {
                            xtype: 'radiofield',
                            name: 'isTop',
                            boxLabel: '置顶 (永久排在最前面)',
                            inputValue: '1'
                        },
                        {
                            xtype: 'radiofield',
                            name: 'isTop',
                            boxLabel: '正常 (按照时间排序)',
                            checked: true,
                            inputValue: '0'
                        }
                    ],
                    listeners: {
                        change: 'onRadiogroupChange'
                    }
                },
                {
                    xtype: 'numberfield',
                    name: 'top',
                    hidden: true,
                    anchor: '100%',
                    fieldLabel: '置顶排序',
                    value: 0
                }
            ]
        }
    ],
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            items: [
                '->',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'saveTempConfig'),
                    text: '保存',
                    listeners: {
                        click: 'onSaveClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'cancel'),
                    text: '取消',
                    listeners: {
                        click: 'onCancelClick'
                    }
                },
                '->'
            ]
        }
    ],

    onSelectHtmlPageClick: function () {
        var self = this;
        Dialog.openWindow('App.recommend.SelectHtmlPageWindow', {
            apis: this.apis,
            _callback: function (dt) {
                self.find('url').setValue(dt['url']);
            }
        }).loadValue();
    },

    onRadiogroupChange: function (field, newValue, oldValue, eOpts) {
        if (newValue['isTop'] == 0) {
            this.find('top').hide();
        }
        if (newValue['isTop'] == 1) {
            if (!this._data) {
                this.find('top').setValue(1);
            }
            this.find('top').show();
        }
    },

    onSaveClick: function (button, e, eOpts) {
        var self = this;
        var form = this.find('form').getForm();
        Dialog.confirm('提示', '确定添加推荐信息吗？', function (btn) {
            if (btn == 'yes') {
                if (form.isValid()) {
                    var data = form.getValues();
                    if (self._data) {
                        self.apis.Recommend.updateContent.call({object: data}, function () {
                            self.close();
                            if (self._callback) {
                                self._callback();
                            }
                        });
                    } else {
                        if (self._groupData) {
                            data['groupId'] = self._groupData['id']
                        }
                        self.apis.Recommend.addContent.call({object: data}, function () {
                            self.close();
                            if (self._callback) {
                                self._callback();
                            }
                        });
                    }
                }
            }
        });
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    onComboboxChange: function (field, newValue, oldValue, eOpts) {
        if (newValue != 0) {
            this.find('selectButtons').show();
            this.find('refreshTitle').show();
            this.find('urlField').hide();
        } else {
            this.find('selectButtons').hide();
            this.find('refreshTitle').hide();
            this.find('urlField').show();
        }
    },

    onSelectImageClick: function (button, e, eOpts) {
        var self = this;
        var field = this.find('cover');
        var image = this.find('image');
        var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
        win.setSelectionCallback(function (files) {
            image.setSrc(Resource.image(files.fileName));
            field.setValue(files.fileName);
        }, true);
    },

    onRefreshTitleClick: function (b, e, o) {
        var self = this;
        var type = this.find('type').getValue();
        var id = this.find('value').getRawValue();
        if (type && id && type != '' && id != '') {
            if (type == 10) {
                this.mask('正在加载数据...');
                this.apis.Content.getContentById.call({id: id}, function (data) {
                    self.unmask();
                    if (data) {
                        self.find('title').setValue(data['title'])
                    } else {
                        Dialog.alert('当前内容不存在或已失效');
                    }
                }, function () {
                    self.unmask();
                });
            }
            if (type == 11) {
                this.mask('正在加载数据...');
                this.apis.News.getNewById.call({id: id}, function (data) {
                    self.unmask();
                    if (data) {
                        self.find('title').setValue(data['title'])
                    } else {
                        Dialog.alert('当前内容不存在或已失效');
                    }
                }, function () {
                    self.unmask();
                });
            }
            if (type == 12) {
                this.mask('正在加载数据...');
                this.apis.Product.getProductById.call({id: id}, function (data) {
                    self.unmask();
                    if (data) {
                        self.find('title').setValue(data['title'])
                    } else {
                        Dialog.alert('当前内容不存在或已失效');
                    }
                }, function () {
                    self.unmask();
                });
            }
            if (type == 13) {
                this.mask('正在加载数据...');
                this.apis.Solution.getSolutionById.call({id: id}, function (data) {
                    self.unmask();
                    if (data) {
                        self.find('title').setValue(data['title'])
                    } else {
                        Dialog.alert('当前内容不存在或已失效');
                    }
                }, function () {
                    self.unmask();
                });
            }
            if (type == 14) {
                this.mask('正在加载数据...');
                this.apis.Document.getTreeCatalogById.call({id: id}, function (data) {
                    self.unmask();
                    if (data) {
                        self.find('title').setValue(data['title'])
                    } else {
                        Dialog.alert('当前内容不存在或已失效');
                    }
                }, function () {
                    self.unmask();
                });
            }
        }
    },

    setFormData: function (ds) {
        if (ds && ds.length > 0) {
            var d = ds[0];
            this.find('value').setValue(d['id']);
            this.find('title').setValue(d['title']);
        }
    },

    setRecommendType: function (type) {
        this.find('type').disable();
        this.find('type').setValue(type);
    },

    setValue: function (data) {
        this._data = data;
        this.setTitle('修改推荐');
        this.find('type').disable();
        var form = this.find('form').getForm();
        form.setValues(data);
        if (data['cover']) {
            this.find('image').setSrc(Resource.image(data['cover']))
        }
    },

    onSelectContentClick: function () {
        var self = this;
        var type = this.find('type').getValue();
        if (type == 10) {
            var win = Dialog.openWindow('App.recommend.RecommendWindow', {
                layout: 'border',
                _selectPanelName: 'right',
                items: [
                    {
                        xtype: 'treepanel',
                        region: 'west',
                        width: 350,
                        split: true,
                        name: 'left',

                        displayField: 'topic',
                        useArrows: true,
                        rootVisible: false,

                        title: '主题列表',
                        columns: [
                            {
                                xtype: 'treecolumn',
                                width: 200,
                                dataIndex: 'topic',
                                text: '主题名称'
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'code',
                                text: '主题码'
                            }
                        ],
                        listeners: {
                            itemclick: 'onPanelItemClick'
                        }
                    },
                    {
                        xtype: 'gridpanel',
                        region: 'center',
                        name: 'right',
                        title: '内容信息管理',
                        columns: [
                            {
                                xtype: 'gridcolumn',
                                width: 90,
                                dataIndex: 'id',
                                text: 'ID'
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'admin',
                                text: '创建人'
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'author',
                                text: '作者'
                            },
                            {
                                xtype: 'gridcolumn',
                                width: 300,
                                dataIndex: 'content',
                                text: '内容',
                                renderer: function (value) {
                                    if (value) {
                                        var reg = value.replace(/<\/?.+?>/g, "");
                                        var v = reg.replace(/ /g, "");
                                        return v;
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
                            }
                        ],
                        selModel: {
                            selType: 'checkboxmodel'
                        }
                    }
                ],
                _callback: function (datas) {
                    if (datas) {
                        self.setFormData(datas);
                    }
                },
                onPanelItemClick: function (dataview, record, item, index, e, eOpts) {
                    var topicId = record.get('id');
                    var store = self.apis.Content.getContents.createPageStore({topicId: topicId});
                    this.find('right').setStore(store);
                    this.find('paging').bindStore(store);
                    store.load();
                }
            });

            var leftCmpt = win.getComponent('left');
            leftCmpt.mask('正在加载数据...');
            this.apis.Content.getTopics.call({}, function (data) {
                leftCmpt.unmask();
                data = data || [];
                var store = Ext.create('Ext.data.TreeStore', {
                    root: {
                        expanded: true,
                        children: data
                    }
                });
                leftCmpt.setStore(store);
            }, function () {
                leftCmpt.unmask();
            });
        }
        else if (type == 11) {
            var win = Dialog.openWindow('App.recommend.RecommendWindow', {
                layout: 'border',
                _selectPanelName: 'right',
                items: [
                    {
                        xtype: 'treepanel',
                        region: 'west',
                        width: 400,
                        split: true,
                        name: 'left',

                        displayField: '新闻分组',
                        useArrows: true,
                        rootVisible: false,

                        title: '主题列表',
                        columns: [
                            {
                                xtype: 'treecolumn',
                                width: 200,
                                dataIndex: 'name',
                                text: '分组名称'
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'code',
                                text: '分组代码'
                            }
                        ],
                        listeners: {
                            itemclick: 'onPanelItemClick'
                        }
                    },
                    {
                        xtype: 'gridpanel',
                        region: 'center',
                        name: 'right',
                        title: '新闻列表',
                        columns: [
                            {
                                xtype: 'gridcolumn',
                                width: 90,
                                dataIndex: 'id',
                                text: '新闻ID'
                            },
                            {
                                xtype: 'gridcolumn',
                                width: 90,
                                dataIndex: 'cover',
                                text: '封面',
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
                                }
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'author',
                                text: '作者'
                            },
                            {
                                xtype: 'gridcolumn',
                                width: 200,
                                dataIndex: 'title',
                                text: '新闻标题',
                                renderer: function (v, mate, record) {
                                    var html = [];
                                    html.push("<div style='height: 40px;width: 100%;white-space:normal;word-wrap:break-word;overflow: hidden'>");
                                    html.push("<div style='width: 100%;overflow: hidden;height: 40px'><span>" + v + "</span></div>")
                                    html.push("</div>");
                                    return html.join("")
                                }
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'type',
                                text: '新闻类型',
                                renderer: function (v) {
                                    if (v == 'detail') {
                                        return '标准新闻';
                                    }
                                    if (v == 'image') {
                                        return '图文新闻';
                                    }
                                    if (v == -999) {
                                        return '';
                                    }
                                    return '其他新闻';
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
                            },
                            {
                                xtype: 'gridcolumn',
                                width: 150,
                                dataIndex: 'modifiedTime',
                                text: '修改时间',
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
                            }
                        ],
                        selModel: {
                            selType: 'checkboxmodel'
                        }
                    }
                ],
                _callback: function (datas) {
                    if (datas) {
                        self.setFormData(datas);
                    }
                },
                onPanelItemClick: function (dataview, record, item, index, e, eOpts) {
                    var gid = record.get('id');
                    var store = self.apis.News.getNews.createPageStore({gid: gid});
                    this.find('right').setStore(store);
                    this.find('paging').bindStore(store);
                    store.load();
                }
            });

            var leftCmpt = win.getComponent('left');
            leftCmpt.mask('正在加载数据...');
            this.apis.News.getGroups.call({}, function (data) {
                leftCmpt.unmask();
                data = data || [];
                var store = Ext.create('Ext.data.TreeStore', {
                    root: {
                        expanded: true,
                        children: data
                    }
                });
                leftCmpt.setStore(store);
            }, function () {
                leftCmpt.unmask();
            });
        }
        else if (type == 12) {
            var win = Dialog.openWindow('App.recommend.RecommendWindow', {
                layout: 'border',
                _selectPanelName: 'right',
                items: [
                    {
                        xtype: 'treepanel',
                        region: 'west',
                        width: 400,
                        split: true,
                        name: 'left',

                        displayField: '产品分类',
                        useArrows: true,
                        rootVisible: false,

                        title: '主题列表',
                        columns: [
                            {
                                xtype: 'gridcolumn',
                                width: 70,
                                dataIndex: 'id',
                                text: 'ID'
                            },
                            {
                                xtype: 'treecolumn',
                                width: 200,
                                dataIndex: 'name',
                                text: '分类名称',
                                flex: 1
                            }
                        ],
                        listeners: {
                            itemclick: 'onPanelItemClick'
                        }
                    },
                    {
                        xtype: 'gridpanel',
                        region: 'center',
                        name: 'right',
                        title: '产品列表',
                        columns: [
                            {
                                xtype: 'gridcolumn',
                                width: 90,
                                dataIndex: 'id',
                                text: '商品ID'
                            },
                            {
                                xtype: 'gridcolumn',
                                width: 90,
                                dataIndex: 'cover',
                                text: '封面',
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
                                }
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'title',
                                text: '名称'
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'subTitle',
                                text: '描述'
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'constPrice',
                                text: '成本价'
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'originalPrice',
                                text: '原价'
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'salePrice',
                                text: '销售价'
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
                                xtype: 'gridcolumn',
                                width: 150,
                                dataIndex: 'modifiedTime',
                                text: '修改时间',
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
                            }
                        ],
                        selModel: {
                            selType: 'checkboxmodel'
                        }
                    }
                ],
                _callback: function (datas) {
                    if (datas) {
                        self.setFormData(datas);
                    }
                },
                onPanelItemClick: function (dataview, record, item, index, e, eOpts) {
                    var cid = record.get('id');
                    var store = self.apis.Product.getProducts.createPageStore({cid: cid});
                    this.find('right').setStore(store);
                    this.find('paging').bindStore(store);
                    store.load();
                }
            });

            var leftCmpt = win.getComponent('left');
            leftCmpt.mask('正在加载数据...');
            this.apis.Product.getCategorys.call({}, function (data) {
                leftCmpt.unmask();
                data = data || [];
                var store = Ext.create('Ext.data.TreeStore', {
                    root: {
                        expanded: true,
                        children: data
                    }
                });
                leftCmpt.setStore(store);
            }, function () {
                leftCmpt.unmask();
            });
        }
        else if (type == 13) {
            var win = Dialog.openWindow('App.recommend.RecommendWindow', {
                layout: 'border',
                _selectPanelName: 'right',
                items: [
                    {
                        xtype: 'gridpanel',
                        region: 'west',
                        width: 300,
                        split: true,
                        name: 'left',
                        title: '解决方案分组',
                        columns: [
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'id',
                                text: '组ID'
                            },
                            {
                                xtype: 'gridcolumn',
                                width: 200,
                                dataIndex: 'name',
                                text: '组名称'
                            }
                        ],
                        listeners: {
                            itemclick: 'onPanelItemClick'
                        }
                    },
                    {
                        xtype: 'gridpanel',
                        region: 'center',
                        name: 'right',
                        title: '解决方案列表',
                        columns: [
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'id',
                                text: 'ID'
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'title',
                                text: '方案名称'
                            },
                            {
                                xtype: 'gridcolumn',
                                width: 200,
                                dataIndex: 'subTitle',
                                text: '方案标题'
                            },
                            {
                                xtype: 'gridcolumn',
                                width: 200,
                                dataIndex: 'detail',
                                text: '方案描述'
                            },
                            {
                                xtype: 'gridcolumn',
                                width: 200,
                                dataIndex: 'html',
                                text: '方案页面',
                                renderer: function (value) {
                                    if (value) {
                                        var reg = value.replace(/<\/?.+?>/g, "");
                                        var v = reg.replace(/ /g, "");
                                        return v;
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
                            }
                        ],
                        selModel: {
                            selType: 'checkboxmodel'
                        }
                    }
                ],
                _callback: function (datas) {
                    if (datas) {
                        self.setFormData(datas);
                    }
                },
                onPanelItemClick: function (dataview, record, item, index, e, eOpts) {
                    var gid = record.get('id');
                    var store = self.apis.Solution.getSolutions.createListStore({gid: gid});
                    this.find('right').setStore(store);
                    this.find('paging').bindStore(store);
                    store.load();
                }
            });

            var leftCmpt = win.getComponent('left');
            var store = this.apis.Solution.getGroups.createListStore();
            leftCmpt.setStore(store);
            store.load()
        }
        else if (type == 14) {
            var win = Dialog.openWindow('App.recommend.RecommendWindow', {
                layout: 'border',
                _selectPanelName: 'right',
                items: [
                    {
                        xtype: 'gridpanel',
                        region: 'west',
                        width: 320,
                        split: true,
                        name: 'left',
                        title: '文档分组',
                        columns: [
                            {
                                xtype: 'gridcolumn',
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
                                dataIndex: 'version',
                                text: '版本号'
                            }
                        ],
                        listeners: {
                            itemclick: 'onPanelItemClick'
                        },
                        dockedItems: [
                            {
                                xtype: 'pagingtoolbar',
                                name: 'paging',
                                dock: 'bottom',
                                width: 360,
                                displayInfo: true
                            }
                        ]
                    },
                    {
                        xtype: 'treepanel',
                        region: 'center',
                        name: 'right',
                        displayField: 'title',
                        useArrows: true,
                        rootVisible: false,
                        title: '文档目录',
                        columns: [
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'id',
                                text: '分类ID'
                            },
                            {
                                xtype: 'treecolumn',
                                dataIndex: 'title',
                                text: '目录名称',
                                width: 200,
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
                            }
                        ],
                        selModel: {
                            selType: 'checkboxmodel'
                        }
                    }
                ],
                _callback: function (datas) {
                    if (datas) {
                        self.setFormData(datas);
                    }
                },
                onPanelItemClick: function (dataview, record, item, index, e, eOpts) {
                    var gid = record.get('id');
                    var rightCmpt = win.getComponent('right');
                    rightCmpt.mask('正在加载数据...');
                    self.apis.Document.getTreeCatalog.call({gid: gid}, function (data) {
                        rightCmpt.unmask();
                        data = data || [];
                        var store = Ext.create('Ext.data.TreeStore', {
                            root: {
                                expanded: true,
                                children: data
                            }
                        });
                        rightCmpt.setStore(store);
                    }, function () {
                        rightCmpt.unmask();
                    });
                }
            });

            var leftCmpt = win.getComponent('left');
            var paging = win.getComponent('paging');
            var store = this.apis.Document.getGroups.createPageStore();
            leftCmpt.setStore(store);
            paging.bindStore(store);
            store.load()
        }
    }

});