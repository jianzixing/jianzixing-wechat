Ext.define('App.wxplugin.WXSignAwardManager', {
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
        WxpluginSign: {
            addAward: {},
            delAward: {},
            updateAward: {},
            getAwards: {},
            getGroups: {}
        }
    },

    columns: [
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'TableWxpluginSignGroup',
            text: '所属组',
            renderer: function (v) {
                if (v) return v['name'];
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'name',
            text: '奖励名称'
        },
        {
            xtype: 'gridcolumn',
            width: 100,
            dataIndex: 'icon',
            text: '奖励图片',
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
            text: '连续签到可得次数'
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'detail',
            text: '奖励描述'
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
                    tooltip: '修改奖励',
                    handler: 'onUpdateClick'
                },
                {
                    iconCls: 'x-fa fa-times',
                    tooltip: '删除奖励',
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
                    text: '列出签到奖励',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addIcon'),
                    text: '添加签到奖励',
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret_dark'),
                    text: '删除签到奖励',
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
            fieldLabel: '奖励短名称'
        },
        {
            xtype: 'textfield',
            name: 'subName',
            anchor: '100%',
            allowBlank: false,
            fieldLabel: '奖励长名称'
        },
        {
            xtype: 'imgfield',
            name: 'icon',
            width: 800,
            height: 80,
            fieldLabel: '奖励图片',
            single: true,
            allowBlank: false,
            addSrc: Resource.png('ex', 'add_img'),
            replaceSrc: Resource.png('ex', 'replace_img'),
            listeners: {
                addimage: 'onSelectImageClick'
            }
        },
        {
            xtype: 'radiogroup',
            name: 'type_group',
            anchor: '100%',
            fieldLabel: '奖励类型',
            items: [
                {
                    xtype: 'radiofield',
                    name: 'type',
                    inputValue: 0,
                    boxLabel: '物品奖励'
                },
                {
                    xtype: 'radiofield',
                    name: 'type',
                    inputValue: 1,
                    boxLabel: '虚拟积分奖励'
                }
            ],
            listeners: {
                change: 'onTypeChange'
            }
        },
        {
            xtype: 'radiogroup',
            name: 'everyday_group',
            anchor: '100%',
            fieldLabel: '赠送频率',
            items: [
                {
                    xtype: 'radiofield',
                    name: 'everyday',
                    inputValue: 0,
                    boxLabel: '连续签到赠送'
                },
                {
                    xtype: 'radiofield',
                    name: 'everyday',
                    inputValue: 1,
                    boxLabel: '每次签到赠送'
                }
            ],
            listeners: {
                change: 'onEverydayChange'
            }
        },
        {
            xtype: 'numberfield',
            name: 'count',
            anchor: '100%',
            allowBlank: false,
            fieldLabel: '连续签到次数'
        },
        {
            xtype: 'textareafield',
            name: 'detail',
            anchor: '100%',
            fieldLabel: '奖励描述'
        }
    ],

    onListClick: function (button, e, eOpts) {
        this.onAfterApply();
    },

    onAddClick: function (button, e, eOpts) {
        var self = this;
        var form = Dialog.openFormWindow({
            title: '添加签到奖励',
            width: 600,
            height: 550,
            items: self.groupFormWindow,
            defaultListenerScope: true,
            success: function (json, win) {
                self.apis.WxpluginSign.addAward
                    .wait(self, '正在添加签到奖励...')
                    .call({object: json}, function () {
                        win.close();
                        self.refreshStore();
                    });
            },
            funs: {
                onSelectImageClick: function (button) {
                    self.onSelectImageClick(button);
                },
                onTypeChange: function (field, newValue, oldValue) {
                    var form = field.ownerCt;
                    if (newValue['type'] == 1) {
                        form.find('everyday_group').enable();
                        form.find('count').setFieldLabel('虚拟积分数量');
                    } else {
                        form.find('everyday_group').disable();
                        form.find('everyday_group').setValue({everyday: 0});
                        form.find('count').setFieldLabel('连续签到次数');
                    }
                },
                onEverydayChange: function (field, newValue, oldValue) {
                    var form = field.ownerCt;
                    var typeValue = form.find('type_group').getValue();
                    if (newValue['everyday'] == 1 && typeValue['type'] == 0) {
                        form.find('count').hide();
                    } else {
                        form.find('count').show();
                    }
                }
            }
        });
        var store = this.apis.WxpluginSign.getGroups.createListStore();
        form.find('gid').setStore(store);
    },

    onUpdateClick: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);

        if (data) {
            var form = Dialog.openFormWindow({
                title: '修改签到奖励',
                width: 600,
                height: 550,
                items: self.groupFormWindow,
                defaultListenerScope: true,
                success: function (json, win) {
                    self.apis.WxpluginSign.updateAward
                        .wait(self, '正在修改签到奖励...')
                        .call({object: json}, function () {
                            win.close();
                            self.refreshStore();
                        });
                },
                funs: {
                    onSelectImageClick: function (button) {
                        self.onSelectImageClick(button);
                    },
                    onTypeChange: function (field, newValue, oldValue) {

                    },
                    onEverydayChange: function (field, newValue, oldValue) {

                    }
                }
            });
            var store = this.apis.WxpluginSign.getGroups.createListStore();
            form.find('gid').setStore(store);
            store.add(data['TableWxpluginSignGroup']);
            form.setValues(data);
        } else {
            Dialog.alert('请先选中一条奖励信息后再修改');
        }
    },

    onDelClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);

        if (datas) {
            Dialog.batch({
                message: '确定删除签到奖励{d}吗？',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == 'yes') {
                        var ids = Array.splitArray(datas, 'id');
                        self.apis.WxpluginSign.delAward
                            .wait(self, '正在删除签到奖励...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            })
                    }
                }
            });
        } else {
            Dialog.alert('请先选中一条奖励信息后再删除');
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
        var store = this.apis.WxpluginSign.getAwards.createPageStore({search: {gid: gid}});
        this.setStore(store);
        store.load();
    },

    onAfterApply: function () {
        var store = this.apis.WxpluginSign.getAwards.createPageStore();
        this.setStore(store);
        store.load();

        var store = this.apis.WxpluginSign.getGroups.createListStore();
        this.find('searchGroupId').setStore(store);
    }

});