Ext.define('App.wxplugin.WXSignGetManager', {
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
            getGets: {},
            setGetUsed: {}
        }
    },

    columns: [
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'TableWxpluginSignAward',
            text: '奖品名称',
            renderer: function (v) {
                if (v) return v['name'];
                return '[空]';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'totalAmount',
            text: '虚拟积分数量'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'isUsed',
            text: '是否使用',
            renderer: function (v) {
                if (v == 1) return '<span style="color: #0f74a8">已使用</span>';
                return '<span style="color: #e45000">未使用</span>';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'TableWxpluginSignGroup',
            text: '所属组',
            renderer: function (v) {
                if (v) return v['name'];
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'TableUser',
            text: '签到用户',
            renderer: function (v) {
                if (v) return v['userName'];
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'TableUser',
            text: 'openid',
            renderer: function (v) {
                if (v) return v['openid'];
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'count',
            text: '签到总次数'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'cntCount',
            text: '连续签到次数'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'cntCount',
            text: '连续签到次数'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'createTime',
            text: '获得奖品时间',
            renderer: function (v) {
                if (v) return (new Date(v)).format();
                return '';
            }
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
                    text: '列出用户奖品',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'mergeSourcesTree'),
                    text: '设置已使用',
                    listeners: {
                        click: 'onAwardUsedClick'
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

    onAwardUsedClick: function (button, e, eOpts) {
        var self = this;
        var data = this.getIgnoreSelect(arguments);

        if (data) {
            if (data['isUsed'] == 0) {
                data['awardName'] = data['TableWxpluginSignAward'] ? data['TableWxpluginSignAward']['name'] : '';
                Dialog.batch({
                    message: '确定将当期用户奖励({d})设置为已使用吗(设置之后无法重置为未使用)？',
                    data: [data],
                    key: 'awardName',
                    callback: function (btn) {
                        if (btn == 'yes') {
                            var id = data['id'];
                            var type = data['type'];
                            if (type == 1) {
                                Dialog.prompt('扣减奖励', '确定扣减用户' + data['awardName'] + '吗？', function (v, text) {
                                    if (v == 'ok') {
                                        self.apis.WxpluginSign.setGetUsed
                                            .wait(self, '正在设置用户' + data['awardName'] + '...')
                                            .call({id: id, useCount: text}, function () {
                                                self.refreshStore();
                                            })
                                    }
                                })
                            } else {
                                self.apis.WxpluginSign.setGetUsed
                                    .wait(self, '正在设置用户奖励...')
                                    .call({id: id}, function () {
                                        self.refreshStore();
                                    })
                            }
                        }
                    }
                });
            }
        } else {
            Dialog.alert('请先选中一条奖励信息后再设置');
        }
    },

    onAfterApply: function () {
        var store = this.apis.WxpluginSign.getGets.createPageStore();
        this.setStore(store);
        store.load();
    }

});