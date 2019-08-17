Ext.define('App.refund.RefundOrderManager', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'Ext.button.Button',
        'Ext.toolbar.Paging',
        'Ext.selection.CheckboxModel'
    ],

    border: false,
    header: false,
    defaultListenerScope: true,

    apis: {
        RefundOrder: {
            getRefundOrders: {},
            setRefundPass: {},
            setRefundReject: {},
            startRefund: {}
        }
    },

    search: true,
    columns: [
        {
            xtype: 'gridcolumn',
            dataIndex: 'id',
            text: 'ID'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'TableUser',
            text: '用户名',
            renderer: function (v) {
                return this.getRendererResult.userName(v);
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'from',
            text: '来源',
            renderer: function (v) {
                return this.getRendererResult.from(v);
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'auditStatus',
            text: '审核状态',
            renderer: function (v) {
                return this.getRendererResult.auditStatus(v);
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'type',
            text: '退款类型',
            renderer: function (v) {
                return this.getRendererResult.type(v);
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'status',
            text: '退款状态',
            renderer: function (v) {
                return this.getRendererResult.status(v);
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'money',
            text: '退款金额',
            renderer: function (v) {
                return '￥' + v;
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'sourceMoney',
            text: '原金额',
            renderer: function (v) {
                return '￥' + v;
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'chargeMoney',
            text: '扣款金额',
            renderer: function (v) {
                return '￥' + v;
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'detail',
            text: '退款原因'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'createTime',
            text: '创建日期',
            renderer: function (v) {
                if (v) return new Date().format();
                return '';
            }
        },
        {
            xtype: 'actioncolumn',
            width: 180,
            text: '操作',
            align: 'center',
            dataIndex: 'status',
            tdCls: 'td_align_middle',
            items: [
                {
                    tooltip: '查看详情',
                    iconCls: "x-fa fa-eye green",
                    handler: 'onDetailClick'
                },
                {
                    tooltip: '开始退款',
                    iconCls: "x-fa fa-recycle green",
                    handler: 'onStartRefundClick',
                    getClass: function (v, mate, record) {
                        if (record.get('auditStatus') == 2 && record.get('status') == 10) {
                            return 'x-fa fa-recycle green';
                        }
                        return 'x-hidden'
                    }
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
                    text: '列出退款单',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'audit_yes_btn',
                    hidden: true,
                    icon: Resource.png('jet', 'testPassed_dark'),
                    text: '审核通过',
                    listeners: {
                        click: 'onAuditYesClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'audit_yes_no',
                    hidden: true,
                    icon: Resource.png('jet', 'testError_dark'),
                    text: '审核不通过',
                    listeners: {
                        click: 'onAuditNoClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'audit_start',
                    hidden: true,
                    icon: Resource.png('jet', 'push_dark'),
                    text: '开始退款',
                    listeners: {
                        click: 'onStartRefundClick'
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
        selType: 'checkboxmodel',
        mode: 'SINGLE'
    },
    listeners: {
        itemclick: 'onRefundOrderItemClick'
    },

    getRendererResult: {
        userName: function (v) {
            if (v) return v['userName'];
            return '[空]';
        },
        from: function (v) {
            if (v == 'RETURN_GOODS') {
                return '用户退货';
            }
            if (v == 'CANCEL_ORDER') {
                return '取消订单';
            }
            return '未知来源';
        },
        auditStatus: function (v) {
            if (v == 0) {
                return '<span style="color: red">未审核</span>'
            }
            if (v == 1) {
                return '<span style="color: darkred">审核拒绝</span>'
            }
            if (v == 2) {
                return '<span style="color: #305a7d">审核通过</span>'
            }
            return '未知状态';
        },
        type: function (v) {
            if (v == 10) {
                return '原路返还'
            }
            if (v == 20) {
                return '退到银行卡'
            }
            if (v == 30) {
                return '退到账户余额'
            }
            return '未知类型';
        },
        status: function (v) {
            if (v == 10) {
                return '<span style="color: #305a7d">新建退款单</span>'
            }
            if (v == 20) {
                return '<span style="color: #305a7d">退款成功</span>'
            }
            if (v == 30) {
                return '<span style="color: #15abff">正在退款中</span>'
            }
            if (v == 50) {
                return '<span style="color: #a70c00">退款失败</span>'
            }
            return '未知状态';
        },
        money: function (v) {
            return '￥' + v;
        },
        time: function (v) {
            if (v) return new Date().format();
            return '';
        }
    },

    onListClick: function (button, e, eOpts) {
        this.onAfterApply();
    },

    onRefundOrderItemClick: function (dataview, record, item, index, e, eOpts) {
        var order = record.getData();
        if (order) {
            this.find('audit_yes_btn').hide();
            this.find('audit_yes_no').hide();
            this.find('audit_start').hide();
            if (order['auditStatus'] == 0) {
                this.find('audit_yes_btn').show();
                this.find('audit_yes_no').show();
            }
            if (order['auditStatus'] == 1) {

            }
            if (order['auditStatus'] == 2 && order['status'] == 10) {
                this.find('audit_start').show();
            }
        }
    },

    onAfterApply: function () {
        var store = this.apis.RefundOrder.getRefundOrders.createPageStore();
        this.setStore(store);
        store.load();
    },

    onDetailClick: function () {
        var data = this.getIgnoreSelects(arguments);
        if (data) {
            var win = Dialog.openWindow('App.refund.RefundOrderWindow', {
                mp: this
            });
            win.setValue(data[0]);
        } else {
            Dialog.alert('至少选择一条退款单后再查看详情');
        }
    },

    onAuditYesClick: function () {
        var self = this;
        var data = this.getIgnoreSelects(arguments);
        if (data) {
            var win = Dialog.openWindow('App.refund.RefundOrderAuditWindow', {
                apis: self.apis,
                _callback: function () {
                    self.refreshStore();
                }
            });
            win.setValue(data[0], true);
        } else {
            Dialog.alert('至少选择一条退款单后再审核');
        }
    },

    onAuditNoClick: function () {
        var self = this;
        var data = this.getIgnoreSelects(arguments);
        if (data) {
            var win = Dialog.openWindow('App.refund.RefundOrderAuditWindow', {
                apis: this.apis,
                _callback: function () {
                    self.refreshStore();
                }
            });
            win.setValue(data[0], false);
        } else {
            Dialog.alert('至少选择一条退款单后再审核');
        }
    },

    onStartRefundClick: function () {
        var self = this;
        var data = this.getIgnoreSelects(arguments);
        if (data) {
            Dialog.batch({
                message: '确定开始退款ID:{d}吗？',
                data: data,
                key: 'id',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(data, 'id');
                        self.apis.RefundOrder.startRefund
                            .wait(self, '正在退款...')
                            .call({ids: ids}, function () {
                                self.refreshStore()
                            });
                    }
                }
            });
        } else {
            Dialog.alert('至少选择一条退款单后再开始退款');
        }
    },

    onSearchClick: function (button, e, options) {
        this.searchPanel.setSearchShow();
    },

    getSearchFormItems: function () {
        return [
            {
                xtype: 'textfield',
                name: 'orderNumber',
                fieldLabel: '订单号'
            },
            {
                xtype: 'textfield',
                name: 'userName',
                fieldLabel: '用户名'
            },
            {
                xtype: 'combobox',
                name: 'from',
                fieldLabel: '来源',
                displayField: 'name',
                valueField: 'id',
                editable: false,
                store: {
                    data: [
                        {id: 'RETURN_GOODS', name: '退货退款'},
                        {id: 'CANCEL_ORDER', name: '取消订单'}
                    ]
                }
            },
            {
                xtype: 'combobox',
                name: 'auditStatus',
                fieldLabel: '审核状态',
                displayField: 'name',
                valueField: 'id',
                editable: false,
                store: {
                    data: [
                        {id: 0, name: '新建未审核'},
                        {id: 1, name: '审核不通过'},
                        {id: 2, name: '审核通过'}
                    ]
                }
            },
            {
                xtype: 'combobox',
                name: 'status',
                fieldLabel: '退款状态',
                displayField: 'name',
                valueField: 'id',
                editable: false,
                store: {
                    data: [
                        {id: 10, name: '新建退款单'},
                        {id: 20, name: '退款成功'},
                        {id: 30, name: '正在退款中'},
                        {id: 50, name: '退款失败'}
                    ]
                }
            },
            {
                xtype: 'combobox',
                name: 'type',
                fieldLabel: '退款类型',
                displayField: 'name',
                valueField: 'id',
                editable: false,
                store: {
                    data: [
                        {id: 10, name: '原路返还'},
                        {id: 20, name: '退到银行卡'},
                        {id: 30, name: '退到账户余额'}
                    ]
                }
            },
            {
                xtype: 'datetimefield',
                name: 'createTimeStart',
                anchor: '100%',
                format: 'Y-m-d H:i:s',
                fieldLabel: '创建时间(开始)'
            },
            {
                xtype: 'datetimefield',
                name: 'createTimeEnd',
                anchor: '100%',
                format: 'Y-m-d H:i:s',
                fieldLabel: '创建时间(结束)'
            }
        ]
    },

    onSearchFormButtonClick: function (form) {
        var data = form.getForm().getValues();
        var store = this.apis.RefundOrder.getRefundOrders.createPageStore({search: data});
        this.setStore(store);
        store.load();
    }
});
