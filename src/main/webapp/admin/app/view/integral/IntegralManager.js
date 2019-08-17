Ext.define('App.integral.IntegralManager', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.view.Table',
        'Ext.button.Button',
        'Ext.toolbar.Paging',
        'Ext.selection.CheckboxModel'
    ],

    border: false,
    header: false,
    defaultListenerScope: true,
    apis: {
        Integral: {
            getIntegrals: {},
            getRecordsByUid: {},
            setUserIntegralZero: {}
        }
    },

    search: true,
    columns: [
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'userName',
            text: '用户名'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'nick',
            text: '昵称',
            renderer: function (v) {
                if (v) return decodeURIComponent(v);
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'TableIntegral',
            text: '积分数量',
            renderer: function (v) {
                if (v) return v['amount'];
                return 0;
            }
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'openid',
            text: 'openid'
        },
        {
            xtype: 'actioncolumn',
            text: '操作',
            dataIndex: 'id',
            align: 'center',
            items: [
                {
                    iconCls: "x-fa fa-database green",
                    tooltip: '查看积分记录',
                    handler: 'onSeeRecordClick'
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
                    text: '列出用户积分',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'Reset_to_empty_dark'),
                    text: '用户积分清零',
                    listeners: {
                        click: 'onZeroClick'
                    }
                },
                '->',
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
            dock: 'bottom',
            width: 360,
            displayInfo: true
        }
    ],
    selModel: {
        selType: 'checkboxmodel'
    },

    onListClick: function (button, e, eOpts) {
        this.onAfterApply();
    },

    onZeroClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.getIgnoreSelects(arguments);
        if (jsons) {
            Dialog.batch({
                message: '确定清空积分{d}吗？',
                data: jsons,
                key: 'userName',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, "id");
                        Dialog.prompt('确定清空积分', '请在下方输入“clear”单词后点击OK按钮', function (btn, txt) {
                            if (btn == 'ok') {
                                if (txt == 'clear') {
                                    self.apis.Integral.setUserIntegralZero
                                        .wait(self, '正在清空积分...')
                                        .call({ids: ids}, function () {
                                            self.refreshStore();
                                        })
                                } else {
                                    Dialog.alert('确认单词输入错误！');
                                }
                            }
                        });

                    }
                }
            });
        } else {
            Dialog.alert('请先选中至少一条积分信息');
        }
    },

    onSeeRecordClick: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var store = this.apis.Integral.getRecordsByUid.createPageStore({uid: data['id']});
            var win = Dialog.openWindow('App.integral.IntegralRecordWindow', {});
            win.setDatas(store);
        } else {
            Dialog.alert('请先选中至少一条积分信息');
        }
    },

    onAfterApply: function () {
        var store = this.apis.Integral.getIntegrals.createPageStore();
        this.setStore(store);
        store.load();
    },
    
    onSearchClick: function (button, e, options) {
        this.searchPanel.setSearchShow();
    },

    getSearchFormItems: function () {
        return [
            {
                xtype: 'textfield',
                name: 'userName',
                fieldLabel: '用户名'
            },
            {
                xtype: 'textfield',
                name: 'openid',
                fieldLabel: 'openid'
            },
            {
                xtype: 'textfield',
                name: 'integralStart',
                fieldLabel: '积分数量(开始)'
            },
            {
                xtype: 'textfield',
                name: 'integralEnd',
                fieldLabel: '积分数量(结束)'
            }
        ]
    },

    onSearchFormButtonClick: function (form) {
        var data = form.getForm().getValues();
        var store = this.apis.Integral.getIntegrals.createPageStore({search: data});
        this.setStore(store);
        store.load();
    }

});
