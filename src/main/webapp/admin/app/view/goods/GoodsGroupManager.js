Ext.define('App.goods.GoodsGroupManager', {
    extend: 'Ext.tree.Panel',
    alias: 'widget.goodsgroupmanager',

    requires: [
        'Ext.tree.View',
        'Ext.tree.Column',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.selection.CheckboxModel',
        'UXApp.field.TreeGridComboBox'
    ],

    border: false,
    header: false,
    defaultListenerScope: true,

    apis: {
        GoodsGroup: {
            getGoodsGroups: {},
            deleteGoodsGroups: {},
            addGoodsGroup: {},
            updateGoodsGroup: {}
        },
        GoodsParameter: {
            getGroups: {}
        },
        Support: {
            getSupports: {}
        }
    },

    viewConfig: {},
    columns: [
        {
            xtype: 'treecolumn',
            dataIndex: 'name',
            text: '分组名称',
            width: 300
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'logo',
            text: '分类图片',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                var width = 40;
                if (!value || value == "") {
                    value = Resource.create('/admin/image/exicon/nopic_' + width + '.gif');
                } else {
                    value = Resource.image(value);
                }
                return '<div style="height: 30px;width: 30px;vertical-align: middle;display:table-cell;">' +
                    '<img style="max-height: 30px;max-width: 30px;vertical-align: middle" src="' + value + '"/>' +
                    '</div>';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'pos',
            text: '排序'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'detail',
            text: '描述',
            width: 400
        },
        {
            xtype: 'actioncolumn',
            dataIndex: 'id',
            text: '操作',
            align: 'center',
            items: [
                {
                    icon: Resource.png('jet', 'addBlankLine'),
                    tooltip: '添加商品分类',
                    handler: 'onAddGroupClick'
                },
                '->',
                {
                    tooltip: '修改商品分类',
                    icon: Resource.png('jet', 'edit'),
                    handler: 'onUpdateGroup'
                },
                '->',
                {
                    tooltip: '删除商品分类',
                    icon: Resource.png('jet', 'delete'),
                    handler: 'onDeleteGroup'
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
                    text: '列出商品分类',
                    listeners: {
                        click: 'onListGroupClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addFolder'),
                    text: '添加商品分类',
                    listeners: {
                        click: 'onAddGroupClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '批量删除分类',
                    listeners: {
                        click: 'onDeleteGroup'
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
                }
            ]
        }
    ],
    selModel: {
        selType: 'checkboxmodel'
    },

    groupFormWindow: [
        {
            xtype: 'textfield',
            name: 'id',
            hidden: true,
            fieldLabel: 'ID',
            inputType: 'hidden'
        },
        {
            xtype: 'textfield',
            name: 'pid',
            hidden: true,
            fieldLabel: 'ID',
            inputType: 'hidden'
        },
        {
            xtype: 'textfield',
            name: 'name',
            anchor: '100%',
            fieldLabel: '分组名称' + Color.string('*', 0xff0000),
            allowBlank: false
        },
        {
            xtype: 'imgfield',
            name: 'logo',
            width: 800,
            height: 50,
            fieldLabel: '分类图标',
            single: true,
            addSrc: Resource.png('ex', 'add_img'),
            replaceSrc: Resource.png('ex', 'replace_img'),
            listeners: {
                addimage: 'onSelectImageClick'
            }
        },
        {
            xtype: 'radiogroup',
            fieldLabel: '是否展开节点',
            items: [
                {
                    xtype: 'radiofield',
                    name: 'expanded',
                    boxLabel: '默认关闭',
                    checked: true,
                    inputValue: '0'
                },
                {
                    xtype: 'radiofield',
                    name: 'expanded',
                    boxLabel: '默认展开',
                    inputValue: '1'
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
            fieldLabel: '分组描述'
        },
        {
            xtype: 'fieldcontainer',
            height: 180,
            fieldLabel: '支持的服务',
            items: [
                {
                    xtype: 'gridpanel',
                    name: 'grid',
                    border: false,
                    header: false,
                    columns: [
                        {
                            xtype: 'rownumberer'
                        },
                        {
                            xtype: 'gridcolumn',
                            width: 200,
                            dataIndex: 'name',
                            text: '服务名称'
                        },
                        {
                            xtype: 'actioncolumn',
                            text: '操作',
                            dataIndex: 'id',
                            items: [
                                {
                                    tooltip: '移除服务',
                                    icon: Resource.png('jet', 'delete'),
                                    handler: 'onRemoveSupport'
                                }
                            ]
                        }
                    ],
                    store: {
                        data: []
                    },
                    dockedItems: [
                        {
                            xtype: 'toolbar',
                            dock: 'top',
                            items: [
                                {
                                    xtype: 'combobox',
                                    name: 'supportList',
                                    width: 300,
                                    fieldLabel: '选择服务',
                                    labelWidth: 70,
                                    editable: false,
                                    displayField: 'name',
                                    valueField: 'id'
                                },
                                {
                                    xtype: 'button',
                                    text: '添加到列表',
                                    listeners: {
                                        click: 'onSupportSelectClick'
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ],

    onListGroupClick: function () {
        this.setTreeStore();
    },

    onSelectImageClick: function (button) {
        var parent = button.ownerCt;
        var image = parent.find('logo');

        var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
        win.setSelectionCallback(function (files) {
            if (files) {
                image._fileName = files['fileName'];
                image.setValue(files['fileName']);
            }
        })
    },

    onAddGroupClick: function (button, e, eOpts) {
        var json = this.getIgnoreSelect(arguments, this);
        if (!json) {
            json = {id: 0};
        }
        var self = this;
        var win = Dialog.openFormWindow({
            title: '添加商品分类',
            width: 800,
            height: 620,
            items: self.groupFormWindow,
            defaultListenerScope: true,
            success: function (json, win) {
                var store = this.find('grid').getStore();
                var supports = [];
                store.each(function (m) {
                    supports.push(m.get('id'))
                });
                json['supports'] = supports.join(',');
                self.apis.GoodsGroup.addGoodsGroup
                    .wait(win, '正在添加商品分类...')
                    .call({object: json}, function () {
                        win.close();
                        self.setTreeStore();
                        self.refreshModuleTree();
                    });
            },
            funs: {
                onSelectImageClick: function (button) {
                    self.onSelectImageClick(button);
                },
                onSupportSelectClick: function () {
                    var store = this.find('grid').getStore();
                    var supportList = this.find('supportList');
                    var dt = supportList.getSelection().getData();
                    store.add(dt);
                },
                onRemoveSupport: function (a1, a2, a3, a4, a5, a6) {
                    var store = this.find('grid').getStore();
                    store.remove(a6);
                }
            }
        });
        win.setValues({pid: json['id']});
        var supportStore = this.apis.Support.getSupports.createListStore();
        win.find('supportList').setStore(supportStore);
    },

    setTreeStore: function () {
        var self = this;
        self.apis.GoodsGroup.getGoodsGroups
            .wait(self, '正在加载商品分类...')
            .call({}, function (jsons) {
                var store = Ext.create('Ext.data.TreeStore', {
                    defaultRootId: '0',
                    root: {
                        expanded: true,
                        name: "商品分类管理",
                        children: jsons
                    }
                });
                self.setStore(store);
            })
    },

    onUpdateGroup: function () {
        var json = this.getIgnoreSelect(arguments, this);

        if (json) {
            var self = this;
            var win = Dialog.openFormWindow({
                title: '修改商品分类',
                width: 800,
                height: 620,
                items: self.groupFormWindow,
                defaultListenerScope: true,
                success: function (json, win) {
                    var store = this.find('grid').getStore();
                    var supports = [];
                    store.each(function (m) {
                        supports.push(m.get('id'))
                    });
                    json['supports'] = supports.join(',');

                    self.apis.GoodsGroup.updateGoodsGroup
                        .wait(win, '正在修改商品分类...')
                        .call({object: json}, function () {
                            win.close();
                            self.setTreeStore();
                            self.refreshModuleTree();
                        });
                },
                funs: {
                    onSelectImageClick: function (button) {
                        self.onSelectImageClick(button);
                    },
                    onSupportSelectClick: function () {
                        var store = this.find('grid').getStore();
                        var supportList = this.find('supportList');
                        var dt = supportList.getSelection().getData();
                        store.add(dt);
                    },
                    onRemoveSupport: function (a1, a2, a3, a4, a5, a6) {
                        var store = this.find('grid').getStore();
                        store.remove(a6);
                    }
                }
            });
            win.setValues(json);
            var supportStore = this.apis.Support.getSupports.createListStore();
            win.find('supportList').setStore(supportStore);

            if (json['TableGoodsGroupSupport']) {
                var storeDt = [];
                for (var i = 0; i < json['TableGoodsGroupSupport'].length; i++) {
                    var gs = json['TableGoodsGroupSupport'][i];
                    gs['name'] = gs['supportName'];
                    gs['id'] = gs['supportId'];
                    storeDt.push(gs);
                }
                var store = Ext.create('Ext.data.Store', {
                    data: storeDt
                });
                win.find('grid').setStore(store);
            }
        } else {
            Dialog.alert('请先选择一条商品分类菜单再修改');
        }
    },

    onDeleteGroup: function () {
        var jsons = this.getIgnoreSelects(arguments, this);
        var self = this;
        if (jsons) {
            Dialog.batch({
                message: '确定删除商品分类{d}吗？',
                data: jsons,
                key: 'name',
                callback: function (btn) {
                    if (btn == 'yes') {
                        var ids = Array.splitArray(jsons, 'id');
                        self.apis.GoodsGroup.deleteGoodsGroups
                            .wait(self, '正在删除商品分类...')
                            .call({ids: ids}, function () {
                                self.setTreeStore();
                                self.refreshModuleTree();
                            })
                    }
                }
            });
        } else {
            Dialog.alert('请先选中要删除的商品分类！');
        }
    },

    onAfterApply: function () {
        this.setTreeStore();
    },

    setSelectModel: function () {
        this.find('select_button').show();
        this.find('close_button').show();
    },

    onSelectClick: function () {
        var jsons = this.getSelect();
        if (this.onSelectCallback) {
            this.onSelectCallback(jsons)
        }
    },

    onCloseClick: function () {
        if (this.onCloseCallback) {
            this.onCloseCallback();
        }
    },

    refreshModuleTree: function () {
        var wrapper = ApplicationLoader.getCurrentTreeModuleWrapper();
        if (wrapper) {
            wrapper.reload(5002);
        }
    }

});
