Ext.define('App.page.PageActivityManager', {
    extend: 'Ext.grid.Panel',

    alias: 'widget.pageactvitymanager',
    requires: [
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'Ext.button.Button',
        'Ext.toolbar.Paging'
    ],

    border: false,
    header: false,
    defaultListenerScope: true,

    apis: {
        Page: {
            getActivityPages: {},
            addPage: {},
            updatePage: {},
            deletePage: {},
            addPageContent: {},
            deletePageContent: {},
            updatePageContent: {},
            getPageContent: {}
        },
        Goods:{
            getGoods:{}
        }
    },
    search: true,

    columns: [
        {
            xtype: 'gridcolumn',
            dataIndex: 'id',
            width: 50,
            text: 'ID'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'name',
            width: 300,
            text: '后台标题'
        },
        {
            xtype: 'gridcolumn',
            width: 300,
            dataIndex: 'title',
            text: '前台显示标题',
            renderer: function (v, metaData, record, rowIndex, colIndex, store, view) {
                return '<a target="_blank" href="/wx/promotion/'+ record.id+'.jhtml">'+v+'</a>';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 100,
            dataIndex: 'enable',
            text: '启用状态',
            renderer: function (v) {
                return parseInt(v)==1 ? "启用":"不启用"
            }
        },
        {
            xtype: 'gridcolumn',
            width: 300,
            dataIndex: 'keyword',
            text: 'SEO关键字'
        },
        {
            xtype: 'actioncolumn',
            dataIndex: 'id',
            text: '操作',
            align: 'center',
            items: [
                {
                    iconCls: 'x-fa fa-pencil',
                    tooltip: '修改页面',
                    handler: 'onUpdatePage'
                },
                {
                    iconCls: 'x-fa fa-times',
                    tooltip: '删除页面',
                    handler: 'onDeletePage'
                },
                {
                    iconCls: 'x-fa fa-cog',
                    tooltip: '配置页面',
                    handler: 'onConfigPage'
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
                    text: '列出页面',
                    icon: Resource.png('jet', 'list'),
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    text: '添加页面',
                    icon: Resource.png('jet', 'addIcon'),
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    text: '批量删除页面',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    listeners: {
                        click: 'onBatchDeleteClick'
                    }
                },
                '->',
                {
                    xtype: 'button',
                    name: 'select_button',
                    text: '确定选择',
                    hidden: true,
                    icon: Resource.png('jet', 'selectall'),
                    listeners: {
                        click: 'onSelectClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'close_button',
                    text: '取消关闭',
                    hidden: true,
                    icon: Resource.png('jet', 'closeActive'),
                    listeners: {
                        click: 'onCloseClick'
                    }
                },
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

    brandFormWindow: [
        {
            xtype: 'textfield',
            name: 'id',
            hidden: true,
            fieldLabel: 'ID',
            inputType: 'hidden'
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
            name: 'title',
            anchor: '100%',
            fieldLabel: '前台显示标题',
            allowBlank: false
        },
        {
            xtype: 'textfield',
            name: 'background',
            anchor: '100%',
            fieldLabel: '背景色(#ffffff)',
            allowBlank: true
        },
        {
            xtype: 'radiogroup',
            anchor: '100%',
            fieldLabel: '是否启用',
            allowBlank: false,
            columns: 2,
            vertical: true,
            items: [
                { boxLabel: '是', name: 'enable', inputValue: '1', checked: true},
                { boxLabel: '否', name: 'enable', inputValue: '2'}
            ]
        },
        {
            xtype: 'textfield',
            name: 'keyword',
            anchor: '100%',
            fieldLabel: 'seo关键字',
            allowBlank: true
        },
        {
            xtype: 'textareafield',
            name: 'description',
            anchor: '100%',
            maxRows: 4,
            fieldLabel: 'seo描述',
            allowBlank: true
        }
    ],

    onListClick: function () {
        this.onAfterApply();
    },

    onConfigPage: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var tab = self.parent.forward('App.page.PageConfigPanel', {
                page: data,
                apis: this.apis
            });
            tab.loadPageContent(data);
        } else {
            Dialog.alert('提示', '请先选择要配置的页面');
        }
    },

    onUpdatePage: function () {
        var json = this.getIgnoreSelect(arguments);
        console.log(json);
        var self = this;
        Dialog.openFormWindow({
            title: '编辑页面',
            width: 420,
            height: 400,
            items: self.brandFormWindow,
            setValueCallback: function (form, json) {
                return json;
            },
            getValueCallback: function (form, json) {
                return json;
            },
            success: function (json, win) {
                self.apis.Page.updatePage
                    .wait(self, '正在修改页面...')
                    .call({object: json}, function () {
                        win.close();
                        self.refreshStore();
                    });
            }
        }).setValues(json);
    },

    onDeletePage: function () {
        var json = this.getIgnoreSelect(arguments);
        var self = this;
        Dialog.batch({
            message: '确定删除页面{d}吗？',
            data: json,
            key: 'name',
            callback: function (btn) {
                if (btn == 'yes') {
                    self.apis.Page.deletePage
                        .wait(self, '正在删除页面...')
                        .call({ids: [json['id']]}, function () {
                            self.refreshStore();
                        })
                }
            }
        });
    },

    onAddClick: function (button, e, eOpts) {
        var self = this;
        Dialog.openFormWindow({
            title: '添加页面',
            width: 420,
            height: 380,
            items: self.brandFormWindow,
            getValueCallback: function (form, json) {
                json.type=2;
                return json;
            },
            success: function (json, win) {
                self.apis.Page.addPage
                    .wait(self, '正在添加页面...')
                    .call({object: json}, function () {
                        win.close();
                        self.refreshStore();
                    });
            }
        });
    },

    onBatchDeleteClick: function (button, e, eOpts) {
        var jsons = this.getSelect();
        var self = this;
        Dialog.batch({
            message: '确定删除页面{d}吗？',
            data: jsons,
            key: 'name',
            callback: function (btn) {
                if (btn == 'yes') {
                    var ids = Array.splitArray(jsons, 'id');
                    self.apis.Brand.deleteBrand
                        .wait(self, '正在删除页面...')
                        .call({ids: ids}, function () {
                            self.refreshStore();
                        })
                }
            }
        });
    },

    onAfterApply: function () {
        var store = this.apis.Page.getActivityPages.createPageStore();
        this.setStore(store);
        store.load();
    },

    setSelectModel: function () {
        var button = this.find('select_button');
        var close = this.find('close_button');
        button.show();
        close.show();
    },

    onSelectClick: function () {
        var jsons = this.getSelect();
        if (this.onSelectCallback) {
            this.onSelectCallback(jsons);
        }
    },

    onCloseClick: function () {
        if (this.onCloseCallback) {
            this.onCloseCallback();
        }
    },

    onSearchClick: function (button, e, options) {
        this.searchPanel.setSearchShow();
    },

    getSearchFormItems: function () {
        return [
            {
                xtype: 'textfield',
                name: 'name',
                fieldLabel: '页面名称'
            }
        ]
    },

    onSearchFormButtonClick: function (form) {
        var data = form.getForm().getValues();
        var store = this.apis.Brand.getBrands.createPageStore({search: data});
        this.setStore(store);
        store.load();
    }

});