Ext.define('App.wxplugin.WXSignGroupManager', {
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
            addGroup: {},
            delGroup: {},
            updateGroup: {},
            getGroups: {},
            enableGroups: {},
            disableGroups: {}
        }
    },

    columns: [
        {
            xtype: 'gridcolumn',
            dataIndex: 'id',
            text: 'ID'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'name',
            text: '分组名称'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'code',
            text: '分组编码'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'enable',
            text: '是否启用',
            renderer: function (v) {
                if (v == 0) return '<span style="color: darkred">未启用</span>';
                if (v == 1) return '<span style="color: green">已启用</span>';
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'startTime',
            text: '开始时间',
            renderer: function (v) {
                if (v) return (new Date(v)).format();
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'finishTime',
            text: '结束时间',
            renderer: function (v) {
                if (v) return (new Date(v)).format();
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'createTime',
            text: '创建时间',
            renderer: function (v) {
                if (v) return (new Date(v)).format();
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
                    tooltip: '修改分组',
                    handler: 'onUpdateClick'
                },
                {
                    iconCls: 'x-fa fa-times',
                    tooltip: '删除分组',
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
                    text: '列出签到分组',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addIcon'),
                    text: '添加签到分组',
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'toolWindowRun'),
                    text: '启用签到分组',
                    listeners: {
                        click: 'onEnableClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'recording_stop'),
                    text: '禁用签到分组',
                    listeners: {
                        click: 'onDisableClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret_dark'),
                    text: '删除签到分组',
                    listeners: {
                        click: 'onDelClick'
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
            xtype: 'hiddenfield',
            name: 'id',
            anchor: '100%'
        },
        {
            xtype: 'textfield',
            name: 'name',
            anchor: '100%',
            allowBlank: false,
            fieldLabel: '分组名称'
        },
        {
            xtype: 'textfield',
            name: 'code',
            anchor: '100%',
            allowBlank: false,
            fieldLabel: '分组编码'
        },
        {
            xtype: 'datetimefield',
            name: 'startTime',
            anchor: '100%',
            format: 'Y-m-d H:i:s',
            allowBlank: false,
            fieldLabel: '开始时间'
        },
        {
            xtype: 'datetimefield',
            name: 'finishTime',
            anchor: '100%',
            format: 'Y-m-d H:i:s',
            allowBlank: false,
            fieldLabel: '结束时间'
        },
        {
            xtype: 'htmleditor',
            height: 300,
            name: 'detail',
            anchor: '100%',
            fieldLabel: '分组描述'
        }
    ],

    onListClick: function (button, e, eOpts) {
        this.onAfterApply();
    },

    onAddClick: function (button, e, eOpts) {
        var self = this;
        var form = Dialog.openFormWindow({
            title: '添加签到分组',
            width: 800,
            height: 650,
            items: self.groupFormWindow,
            defaultListenerScope: true,
            success: function (json, win) {
                json['openType'] = self.openType;
                json['accountId'] = self.accountData['id'];
                self.apis.WxpluginSign.addGroup
                    .wait(self, '正在添加签到分组...')
                    .call({object: json}, function () {
                        win.close();
                        self.refreshStore();
                    });
            },
            funs: {}
        });
    },

    onDelClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);

        if (datas) {
            Dialog.batch({
                message: '确定删除签到分组{d}吗？',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == 'yes') {
                        var ids = Array.splitArray(datas, 'id');
                        self.apis.WxpluginSign.delGroup
                            .wait(self, '正在删除签到分组...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            })
                    }
                }
            });
        } else {
            Dialog.alert('请先选中一条分组信息后再删除');
        }
    },

    onEnableClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);

        if (datas) {
            Dialog.batch({
                message: '确定启用签到分组{d}吗？',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == 'yes') {
                        var ids = Array.splitArray(datas, 'id');
                        self.apis.WxpluginSign.enableGroups
                            .wait(self, '正在启用签到分组...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            })
                    }
                }
            });
        } else {
            Dialog.alert('请先选中一条分组信息后再启用');
        }
    },


    onDisableClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);

        if (datas) {
            Dialog.batch({
                message: '确定禁用签到分组{d}吗？',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == 'yes') {
                        var ids = Array.splitArray(datas, 'id');
                        self.apis.WxpluginSign.disableGroups
                            .wait(self, '正在禁用签到分组...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            })
                    }
                }
            });
        } else {
            Dialog.alert('请先选中一条分组信息后再禁用');
        }
    },

    onUpdateClick: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);

        if (data) {
            var form = Dialog.openFormWindow({
                title: '修改签到分组',
                width: 800,
                height: 650,
                items: self.groupFormWindow,
                defaultListenerScope: true,
                success: function (json, win) {
                    self.apis.WxpluginSign.updateGroup
                        .wait(self, '正在修改签到分组...')
                        .call({object: json}, function () {
                            win.close();
                            self.refreshStore();
                        });
                },
                funs: {}
            });
            form.setValues(data);
            form.find('startTime').setValue(new Date(data['startTime']));
            form.find('finishTime').setValue(new Date(data['finishTime']));
        } else {
            Dialog.alert('请先选中一条分组信息后再修改');
        }
    },

    onAfterApply: function () {
        var store = this.apis.WxpluginSign.getGroups.createListStore();
        this.setStore(store);
        store.load();
    }

});