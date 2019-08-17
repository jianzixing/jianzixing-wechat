Ext.define('App.user.MemberLevel', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.view.Table',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    border: false,
    header: false,
    defaultListenerScope: true,

    apis: {
        User: {
            getUserLevels: {},
            addUserLevel: {},
            deleteUserLevel: {},
            updateUserLevel: {}
        }
    },

    selModel: {
        selType: 'checkboxmodel'
    },
    columns: [
        {
            xtype: 'gridcolumn',
            dataIndex: 'name',
            width: 200,
            text: '等级名称'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'logo',
            text: '图标',
            renderer: function (value) {
                if (!value || value == "") {
                    value = Resource.create('/admin/image/exicon/nopic_40.gif');
                } else {
                    value = Resource.image(value);
                }
                return '<div style="height: 40px;width: 40px;vertical-align: middle;display:table-cell;overflow: hidden">' +
                    '<img style="max-height:40;max-width: 40px;vertical-align: middle" src=' + value + '></div> ';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'startAmount',
            text: '起始范围'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'endAmount',
            text: '结束范围'
        },
        {
            xtype: 'gridcolumn',
            width: 280,
            dataIndex: 'detail',
            text: '等级描述'
        },
        {
            xtype: 'actioncolumn',
            text: '操作',
            items: [
                {
                    tooltip: '修改',
                    icon: Resource.png('jet', 'editItemInSection'),
                    handler: 'onUpdateClick'
                },
                '->',
                {
                    icon: Resource.png('jet', 'exclude'),
                    tooltip: '删除',
                    handler: 'onDeleteClick'
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
                    text: '列出会员',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addJira'),
                    text: '添加等级',
                    listeners: {
                        click: 'onButtonClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '批量删除等级',
                    listeners: {
                        click: 'onDeleteClick'
                    }
                }
            ]
        }
    ],

    levelFormWindow: [
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
            fieldLabel: '名称',
            allowBlank: false
        },
        {
            xtype: 'displayfield',
            height: 35,
            value: '<span style="color:#999999">起始结束范围只是一个数值(不可重叠如:0-99,100-499)，默认使用用户订单支付总额作为依据</span>',
            fieldLabel: '<span style="color:#999999">注意</span>'
        },
        {
            xtype: 'textfield',
            name: 'startAmount',
            anchor: '100%',
            fieldLabel: '起始范围',
            inputType: 'number',
            emptyText: '0'
        },
        {
            xtype: 'textfield',
            name: 'endAmount',
            anchor: '100%',
            fieldLabel: '结束范围',
            inputType: 'number',
            emptyText: '0'
        },
        {
            xtype: 'displayfield',
            value: '<span style="color:#999999">用户等级图标必须上传，尺寸最好是80x80的png图片</span>',
            fieldLabel: '<span style="color:#999999">注意</span>'
        },
        {
            xtype: 'imgfield',
            name: 'logo',
            width: 800,
            height: 50,
            fieldLabel: '等级图标',
            single: true,
            addSrc: Resource.png('ex', 'add_img'),
            replaceSrc: Resource.png('ex', 'replace_img'),
            listeners: {
                addimage: 'onSelectImageClick'
            }
        },
        {
            xtype: 'textareafield',
            name: 'detail',
            anchor: '100%',
            fieldLabel: '描述'
        }
    ],

    onListClick: function () {
        this.refreshStore();
    },

    onButtonClick: function (button, e, eOpts) {
        var self = this;
        Dialog.openFormWindow({
            title: '添加用户等级',
            width: 600,
            height: 500,
            items: self.levelFormWindow,
            success: function (json, win) {
                if (!json['logo']) {
                    Dialog.alert('等级图标必须上传');
                    return;
                }

                self.apis.User.addUserLevel
                    .wait(self, '正在添加用户等级...')
                    .call({object: json}, function () {
                        win.close();
                        self.refreshStore()
                    });
            },
            funs: {
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
                }
            }
        })
    },

    onUpdateClick: function () {
        var json = this.getIgnoreSelect(arguments);
        var self = this;
        Dialog.openFormWindow({
            title: '修改用户等级',
            width: 600,
            height: 500,
            items: self.levelFormWindow,
            success: function (json, win) {
                if (!json['logo']) {
                    Dialog.alert('等级图标必须上传');
                    return;
                }
                self.apis.User.updateUserLevel
                    .wait(self, '正在修改用户等级...')
                    .call({object: json}, function () {
                        win.close();
                        self.refreshStore()
                    });
            },
            funs: {
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
                }
            }
        }).setValues(json);
    },

    onDeleteClick: function () {
        var jsons = this.getIgnoreSelects(arguments);
        var self = this;
        if (jsons) {
            Dialog.batch({
                message: '确定删除用户等级{d}吗？',
                data: jsons,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, 'id');
                        self.apis.User.deleteUserLevel
                            .wait(self, '正在删除用户等级...')
                            .call({ids: ids}, function () {
                                self.refreshStore()
                            });
                    }
                }
            });
        } else {
            Dialog.alert('请选择至少一个用户等级');
        }
    },

    onAfterApply: function () {
        var store = this.apis.User.getUserLevels.createListStore();
        this.setStore(store);
        store.load();
    }

});
