Ext.define('App.goods.GoodsBrandManager', {
    extend: 'Ext.grid.Panel',

    alias: 'widget.goodsbrandmanager',
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
        Brand: {
            getBrands: {},
            addBrand: {},
            deleteBrand: {},
            updateBrand: {}
        }
    },
    search: true,

    columns: [
        {
            xtype: 'gridcolumn',
            dataIndex: 'name',
            text: '名称'
        },
        {
            xtype: 'gridcolumn',
            width: 120,
            dataIndex: 'logo',
            text: '品牌图片',
            renderer: function (v) {
                return '<div style="height: 25px">' +
                    (v != null ? '<img style="height: 25px;width:90px" src="' + Resource.image(v) + '"/>' : '') +
                    '</div>'
            }
        },
        {
            xtype: 'gridcolumn',
            width: 390,
            dataIndex: 'detail',
            text: '描述'
        },
        {
            xtype: 'actioncolumn',
            dataIndex: 'id',
            text: '操作',
            align: 'center',
            items: [
                {
                    iconCls: 'x-fa fa-pencil',
                    tooltip: '修改品牌',
                    handler: 'onUpdateBrand'
                },
                {
                    iconCls: 'x-fa fa-times',
                    tooltip: '删除品牌',
                    handler: 'onDeleteBrand'
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
                    text: '列出品牌',
                    icon: Resource.png('jet', 'list'),
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    text: '添加品牌',
                    icon: Resource.png('jet', 'addIcon'),
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    text: '批量删除品牌',
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
            fieldLabel: '品牌名称',
            allowBlank: false
        },
        {
            xtype: 'fieldcontainer',
            anchor: '100%',
            fieldLabel: '品牌图片',
            items: [
                {
                    xtype: 'image',
                    name: 'form_image',
                    height: 50,
                    width: 180
                },
                {
                    xtype: 'button',
                    margin: 'auto auto auto 10px',
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
            emptyText: '0'
        },
        {
            xtype: 'textareafield',
            name: 'detail',
            anchor: '100%',
            height: 75,
            fieldLabel: '品牌描述'
        }
    ],

    onListClick: function () {
        this.onAfterApply();
    },

    onUpdateBrand: function () {
        var json = this.getIgnoreSelect(arguments);
        var self = this;
        Dialog.openFormWindow({
            title: '添加品牌',
            width: 400,
            height: 330,
            items: self.brandFormWindow,
            setValueCallback: function (form, json) {
                var image = form.find('form_image');
                image.fileName = json['logo'];
                image.setSrc(Resource.image(image.fileName));
                return json;
            },
            getValueCallback: function (form, json) {
                var image = form.find('form_image');
                json['logo'] = image.fileName;
                return json;
            },
            success: function (json, win) {
                self.apis.Brand.updateBrand
                    .wait(self, '正在修改品牌...')
                    .call({object: json}, function () {
                        win.close();
                        self.refreshStore();
                    });
            },
            funs: {
                onSelectImageClick: function (button) {
                    var parent = button.ownerCt;
                    var image = parent.find('form_image');

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

    onDeleteBrand: function () {
        var json = this.getIgnoreSelect(arguments);
        var self = this;
        Dialog.batch({
            message: '确定删除品牌{d}吗？',
            data: json,
            key: 'name',
            callback: function (btn) {
                if (btn == 'yes') {
                    self.apis.Brand.deleteBrand
                        .wait(self, '正在删除品牌...')
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
            title: '添加品牌',
            width: 400,
            height: 330,
            items: self.brandFormWindow,
            getValueCallback: function (form, json) {
                var image = form.find('form_image');
                json['logo'] = image.fileName;
                return json;
            },
            success: function (json, win) {
                self.apis.Brand.addBrand
                    .wait(self, '正在添加品牌...')
                    .call({object: json}, function () {
                        win.close();
                        self.refreshStore();
                    });
            },
            funs: {
                onSelectImageClick: function (button) {
                    var parent = button.ownerCt;
                    var image = parent.find('form_image');

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

    onBatchDeleteClick: function (button, e, eOpts) {
        var jsons = this.getSelect();
        var self = this;
        Dialog.batch({
            message: '确定删除品牌{d}吗？',
            data: jsons,
            key: 'name',
            callback: function (btn) {
                if (btn == 'yes') {
                    var ids = Array.splitArray(jsons, 'id');
                    self.apis.Brand.deleteBrand
                        .wait(self, '正在删除品牌...')
                        .call({ids: ids}, function () {
                            self.refreshStore();
                        })
                }
            }
        });
    },

    onAfterApply: function () {
        var store = this.apis.Brand.getBrands.createPageStore();
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
                fieldLabel: '品牌名称'
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