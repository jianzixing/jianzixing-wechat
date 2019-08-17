Ext.define('App.wxplugin.WXVotingItemManager', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.selection.CheckboxModel'
    ],

    border: false,
    header: false,
    defaultListenerScope: true,

    apis: {
        WxpluginVoting: {
            addItem: {},
            delItem: {},
            updateItem: {},
            getItems: {},
            getGroups: {}
        }
    },

    columns: [
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'TableWxpluginVotingGroup',
            text: '所属组',
            renderer: function (v) {
                if (v) return v['name'];
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'name',
            text: '投票条目名称'
        },
        {
            xtype: 'gridcolumn',
            width: 100,
            dataIndex: 'icon',
            text: '投票条目图片',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (!value || value == "") {
                    value = Resource.create('/admin/image/exicon/nopic_40.gif');
                } else {
                    value = Resource.image(value);
                }
                return '<div style="height: 60px;width: 60px;vertical-align: middle;display:table-cell;overflow: hidden">' +
                    '<img style="max-height:60px;max-width: 60px;vertical-align: middle" src=' + value + '></div> ';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'count',
            text: '获得票数'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'createTime',
            text: '创建时间',
            renderer: function (v) {
                if (v) return (new Date()).format();
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
                    iconCls: 'x-fa fa-pencil',
                    tooltip: '修改投票',
                    handler: 'onUpdateClick'
                },
                {
                    iconCls: 'x-fa fa-times',
                    tooltip: '删除投票',
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
                    text: '列出投票条目',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addIcon'),
                    text: '添加投票条目',
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret_dark'),
                    text: '删除投票条目',
                    listeners: {
                        click: 'onDelClick'
                    }
                },
                '->',
                {
                    xtype: 'combobox',
                    name: 'searchGroupId',
                    hideLabel: true,
                    displayField: 'name',
                    valueField: 'id',
                    queryMode: 'remote',
                    queryParam: 'keyword',
                    selectOnFocus: true,
                    allowBlank: false,
                    minChars: 1
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'search'),
                    text: '搜索',
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

    groupFormWindow: [
        {
            xtype: 'hiddenfield',
            name: 'id',
            anchor: '100%'
        },
        {
            xtype: 'combobox',
            name: 'gid',
            anchor: '100%',
            displayField: 'name',
            valueField: 'id',
            fieldLabel: '所属分组',
            queryMode: 'remote',
            queryParam: 'keyword',
            selectOnFocus: true,
            allowBlank: false,
            minChars: 1
        },
        {
            xtype: 'textfield',
            name: 'name',
            anchor: '100%',
            allowBlank: false,
            fieldLabel: '投票条目名称'
        },
        {
            xtype: 'textarea',
            name: 'subName',
            anchor: '100%',
            allowBlank: false,
            fieldLabel: '投票条目副名称'
        },
        {
            xtype: 'imgfield',
            name: 'icon',
            width: 800,
            height: 80,
            fieldLabel: '投票条目图片',
            single: true,
            allowBlank: false,
            addSrc: Resource.png('ex', 'add_img'),
            replaceSrc: Resource.png('ex', 'replace_img'),
            listeners: {
                addimage: 'onSelectImageClick'
            }
        },
        {
            xtype: 'htmleditor',
            height: 350,
            name: 'detail',
            anchor: '100%',
            fieldLabel: '投票条目描述'
        }
    ],

    onListClick: function (button, e, eOpts) {
        this.onAfterApply();
    },

    onAddClick: function (button, e, eOpts) {
        var self = this;
        var form = Dialog.openFormWindow({
            title: '添加投票条目',
            width: 800,
            height: 750,
            items: self.groupFormWindow,
            defaultListenerScope: true,
            success: function (json, win) {
                self.apis.WxpluginVoting.addItem
                    .wait(self, '正在添加投票条目...')
                    .call({object: json}, function () {
                        win.close();
                        self.refreshStore();
                    });
            },
            funs: {
                onSelectImageClick: function (button) {
                    self.onSelectImageClick(button);
                }
            }
        });
        var store = this.apis.WxpluginVoting.getGroups.createPageStore();
        form.find('gid').setStore(store);
    },

    onUpdateClick: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);

        if (data) {
            var form = Dialog.openFormWindow({
                title: '修改投票条目',
                width: 800,
                height: 750,
                items: self.groupFormWindow,
                defaultListenerScope: true,
                success: function (json, win) {
                    self.apis.WxpluginVoting.updateItem
                        .wait(self, '正在修改投票条目...')
                        .call({object: json}, function () {
                            win.close();
                            self.refreshStore();
                        });
                },
                funs: {
                    onSelectImageClick: function (button) {
                        self.onSelectImageClick(button);
                    }
                }
            });
            var store = this.apis.WxpluginVoting.getGroups.createPageStore();
            form.find('gid').setStore(store);
            store.add(data['TableWxpluginVotingGroup']);
            form.setValues(data);
        } else {
            Dialog.alert('请先选中一条投票条目信息后再修改');
        }
    },

    onDelClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);

        if (datas) {
            Dialog.batch({
                message: '确定删除投票条目{d}吗？',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == 'yes') {
                        var ids = Array.splitArray(datas, 'id');
                        self.apis.WxpluginVoting.delItem
                            .wait(self, '正在删除投票条目...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            })
                    }
                }
            });
        } else {
            Dialog.alert('请先选中一条投票条目信息后再删除');
        }
    },

    onSelectImageClick: function (button) {
        var parent = button.ownerCt;
        var image = parent.find('icon');

        var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
        win.setSelectionCallback(function (files) {
            if (files) {
                image._fileName = files['fileName'];
                image.setValue(files['fileName']);
            }
        })
    },

    onSearchClick: function () {
        var gid = this.find('searchGroupId').getValue();
        var store = this.apis.WxpluginVoting.getItems.createPageStore({search: {gid: gid}});
        this.setStore(store);
        store.load();
    },

    onAfterApply: function () {
        var store = this.apis.WxpluginVoting.getItems.createPageStore();
        this.setStore(store);
        store.load();

        var store = this.apis.WxpluginVoting.getGroups.createPageStore();
        this.find('searchGroupId').setStore(store);
    }

});