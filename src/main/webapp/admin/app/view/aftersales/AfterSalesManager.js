Ext.define('App.aftersales.AfterSalesManager', {
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
        AfterSales: {
            getAfterSales: {},
            getAfterSaleProgress: {},
            getAfterSalesById: {},
            cancelAfterSales: {},
            rebackGoods: {},
            repairFailure: {},
            repairSuccess: {},
            resendGoods: {},
            createRefundOrder: {},
            sureStartRepair: {},
            sureCheckGoodsSuccess: {},
            sureCheckGoodsFailure: {},
            sureGetGoods: {},
            auditRefused: {},
            auditPass: {},
            getRefundMoney: {}
        },
        UserAddress: {
            getUserAddressByUserUid: {}
        },
        Area: {
            getProvince: {},
            getCity: {},
            getArea: {}
        },
        Logistics: {
            getLogisticsCompany: {}
        }
    },
    search: true,
    columns: [
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'TableUser',
            text: '用户名',
            renderer: function (v) {
                if (v) {
                    return v['userName']
                }
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'number',
            text: '售后单号'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'TableOrder',
            text: '订单号',
            renderer: function (v) {
                if (v) {
                    return v['number']
                }
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'type',
            text: '售后类型',
            renderer: function (v) {
                return this.getTypeName(v);
            }
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'TableOrderGoods',
            text: '售后商品',
            renderer: function (v, mate, record) {
                var togp = record.get('TableOrderGoodsProperty');
                var goodsId = v['goodsId'];
                var skuId = v['skuId'];
                var attr = [];
                if (togp && Ext.isArray(togp)) {
                    for (var i = 0; i < togp.length; i++) {
                        if (togp[i]["goodsId"] == goodsId
                            && togp[i]['skuId'] == skuId) {
                            attr.push(togp[i]['valueName']);
                        }
                    }
                    if (attr.length > 0) {
                        return attr;
                    }
                }
                var s = '';
                if (attr.length > 0) {
                    s = v['goodsName'] + '（' + attr.join('，') + '）'
                } else {
                    s = v['goodsName'];
                }
                if (v['serialNumber']) {
                    return s + '</br>商品编号：' + v['serialNumber'];
                } else {
                    return s;
                }
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'amount',
            text: '售后数量'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'status',
            text: '状态',
            renderer: function (v) {
                return this.getStatusName(v);
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'detail',
            text: '问题描述'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'createTime',
            text: '创建时间',
            renderer: function (v) {
                if (v) {
                    return (new Date(v)).format();
                }
                return '';
            }
        },
        {
            xtype: 'actioncolumn',
            text: '操作',
            dataIndex: 'id',
            align: 'center',
            items: [
                {
                    tooltip: '查看售后单',
                    iconCls: "x-fa fa-edit green",
                    handler: 'onDetailClick'
                },
                {
                    tooltip: '售后单处理进度',
                    iconCls: "x-fa fa-paper-plane green",
                    handler: 'onProgressClick'
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
                    text: '列出售后单',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                '->',
                {
                    xtype: 'image',
                    height: 20,
                    width: 20,
                    src: 'image/icon/dp.png'
                },
                {
                    xtype: 'container',
                    margin: 'auto 20 auto auto',
                    html: '<span style="color: #666666">双击可以查看售后单详情并处理售后单</span>'
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
        itemdblclick: 'onItemDBClick'
    },

    onListClick: function (button, e, eOpts) {
        this.refreshStore();
    },

    onAfterApply: function () {
        var module = this.parent.moduleObject;
        var type = module.get('type');
        var p = {};
        if (type.toLowerCase() != 'normal') p = {type: type};
        var store = this.apis.AfterSales.getAfterSales.createPageStore(p);
        this.setStore(store);
        store.load();
    },

    onDetailClick: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var sub = this.parent.forward('App.aftersales.AfterSalesPanel', {
                apis: this.apis,
                preView: this,
                _callback: function () {
                    self.refreshStore();
                }
            });
            sub.setValue(data, this);
            sub.refreshView();
        } else {
            Dialog.alert('请选择至少一条售后单');
        }
    },

    onProgressClick: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var win = Dialog.openWindow('App.aftersales.AfterSaleProgressWindow', {
                mp: self,
                apis: self.apis
            });
            win.setValue(data['id'], this);
        } else {
            Dialog.alert('请选择至少一条售后单');
        }
    },

    getStatusName: function (status) {
        if (status == 10) return '<span style="color: #0f74a8">新建售后单</span>';
        if (status == 20) return '<span style="color: #0f74a8">审核已通过，等待用户发货</span>';
        if (status == 21) return '<span style="color: #0f74a8">审核已拒绝</span>';
        if (status == 30) return '<span style="color: #0f74a8">用户已发货</span>';
        if (status == 31) return '<span style="color: #0f74a8">物流已拒绝</span>';
        if (status == 40) return '<span style="color: #0f74a8">卖家已收货</span>';
        if (status == 41) return '<span style="color: #0f74a8">卖家已拒收</span>';
        if (status == 42) return '<span style="color: #0f74a8">验货已通过</span>';
        if (status == 50) return '<span style="color: #0f74a8">正在维修中</span>';
        if (status == 51) return '<span style="color: #0f74a8">维修失败</span>';
        if (status == 52) return '<span style="color: #0f74a8">维修成功</span>';
        if (status == 60) return '<span style="color: #0f74a8">卖家已发货</span>';
        if (status == 61) return '<span style="color: #0f74a8">买家已拒收</span>';
        if (status == 80) return '<span style="color: #0f74a8">正在等待退款</span>';
        if (status == 81) return '<span style="color: #0f74a8">退款失败</span>';
        if (status == 82) return '<span style="color: #0f74a8">退款成功</span>';
        if (status == 90) return '<span style="color: #0f74a8">售后单已取消</span>';
        if (status == 100) return '<span style="color: #0f74a8">售后已完成</span>';
        return '不支持状态';
    },

    getTypeName: function (type) {
        if (type == 10) return '<span style="color: #99230f;">退货</span>';
        if (type == 20) return '<span style="color: #305a7d;">换货</span>';
        if (type == 30) return '<span style="color: #369917;">维修</span>';
        return '不支持类型';
    },

    getDeliveryName: function (type) {
        if (type == 0) return '快递';
        return '其他';
    },

    setButtonsShow: function (data, view) {
        var b1 = view.find('btns_cancel'); //取消售后申请单
        var b2 = view.find('btns_send_back'); //商品寄回
        var b3 = view.find('btns_repair_fail'); //维修失败
        var b4 = view.find('btns_repair_succ'); //维修成功
        var b5 = view.find('btns_create_money'); //创建退款单
        var b6 = view.find('btns_start_repair');  //确认开始维修
        var b12 = view.find('btns_check_fail'); //验货有问题
        var b7 = view.find('btns_check_pass'); //确认验货通过
        var b8 = view.find('btns_get_goods'); //确认已收货
        var b10 = view.find('btns_audit_fail'); //审核拒绝
        var b11 = view.find('btns_audit_succ'); //审核通过

        var b13 = view.find('btns_new_goods'); //重新发货

        var status = data['status'];
        var type = data['type'];

        b1.hide(), b2.hide(), b3.hide(), b4.hide(), b5.hide(),
            b6.hide(), b7.hide(), b8.hide(), b10.hide(),
            b11.hide(), b12.hide(), b13.hide();

        if (status == 10) {
            b1.show(), b2.hide(), b3.hide(), b4.hide(), b5.hide(),
                b6.hide(), b7.hide(), b8.hide(), b10.show(),
                b11.show(), b12.hide(), b13.hide();
        }
        if (status == 20) {
            b1.hide(), b2.hide(), b3.hide(), b4.hide(), b5.hide(),
                b6.hide(), b7.hide(), b8.hide(), b10.hide(),
                b11.hide(), b12.hide(), b13.hide();
        }
        if (status == 21) {
            b1.hide(), b2.hide(), b3.hide(), b4.hide(), b5.hide(),
                b6.hide(), b7.hide(), b8.hide(), b10.hide(),
                b11.hide(), b12.hide(), b13.hide();
        }
        if (status == 30) {
            b1.hide(), b2.hide(), b3.hide(), b4.hide(), b5.hide(),
                b6.hide(), b7.hide(), b8.show(), b10.hide(),
                b11.hide(), b12.hide(), b13.hide();
        }
        if (status == 31) {
            b1.hide(), b2.hide(), b3.hide(), b4.hide(), b5.hide(),
                b6.hide(), b7.hide(), b8.hide(), b10.hide(),
                b11.hide(), b12.hide(), b13.hide();
        }
        if (status == 40) {
            b1.hide(), b2.hide(), b3.hide(), b4.hide(), b5.hide(),
                b6.hide(), b7.show(), b8.hide(), b10.hide(),
                b11.hide(), b12.show(), b13.hide();
        }
        if (status == 41) {
            b1.hide(), b2.hide(), b3.hide(), b4.hide(), b5.hide(),
                b6.hide(), b7.hide(), b8.hide(), b10.hide(),
                b11.hide(), b12.hide(), b13.hide();
        }
        if (status == 42) {
            if (type == 10) {
                b1.hide(), b2.hide(), b3.hide(), b4.hide(), b5.show(),
                    b6.hide(), b7.hide(), b8.hide(), b10.hide(),
                    b11.hide(), b12.hide(), b13.hide();
            } //退货
            if (type == 20) {
                b1.hide(), b2.hide(), b3.hide(), b4.hide(), b5.hide(),
                    b6.hide(), b7.hide(), b8.hide(), b10.hide(),
                    b11.hide(), b12.hide(), b13.show();
            } //换货
            if (type == 30) {
                b1.hide(), b2.hide(), b3.hide(), b4.hide(), b5.hide(),
                    b6.show(), b7.hide(), b8.hide(), b10.hide(),
                    b11.hide(), b12.hide(), b13.hide();
            } //维修
        }
        if (status == 50) {
            b1.hide(), b2.hide(), b3.show(), b4.show(), b5.hide(),
                b6.hide(), b7.hide(), b8.hide(), b10.hide(),
                b11.hide(), b12.hide(), b13.hide();
        }
        if (status == 51) {
            b1.hide(), b2.show(), b3.hide(), b4.hide(), b5.hide(),
                b6.hide(), b7.hide(), b8.hide(), b10.hide(),
                b11.hide(), b12.hide(), b13.hide();
        }
        if (status == 52) {
            b1.hide(), b2.show(), b3.hide(), b4.hide(), b5.hide(),
                b6.hide(), b7.hide(), b8.hide(), b10.hide(),
                b11.hide(), b12.hide(), b13.hide();
        }
        if (status == 60) {
            b1.hide(), b2.hide(), b3.hide(), b4.hide(), b5.hide(),
                b6.hide(), b7.hide(), b8.hide(), b10.hide(),
                b11.hide(), b12.hide(), b13.hide();
        }
        if (status == 61) {
            b1.hide(), b2.hide(), b3.hide(), b4.hide(), b5.hide(),
                b6.hide(), b7.hide(), b8.hide(), b10.hide(),
                b11.hide(), b12.hide(), b13.hide();
        }
        if (status == 90) {
            b1.hide(), b2.hide(), b3.hide(), b4.hide(), b5.hide(),
                b6.hide(), b7.hide(), b8.hide(), b10.hide(),
                b11.hide(), b12.hide(), b13.hide();
        }
        if (status == 100) {
            b1.hide(), b2.hide(), b3.hide(), b4.hide(), b5.hide(),
                b6.hide(), b7.hide(), b8.hide(), b10.hide(),
                b11.hide(), b12.hide(), b13.hide();
        }
    },

    onItemDBClick: function (dataview, record, item, index, e, eOpts) {
        var status = record.get('status');
        var self = this;
        var sub = this.parent.forward('App.aftersales.AfterSalesPanel', {
            apis: this.apis,
            preView: this,
            _callback: function () {
                self.refreshStore();
            }
        });
        sub.setValue(record.getData(), this);
        sub.refreshView();
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
                name: 'number',
                fieldLabel: '售后订单号'
            },
            {
                xtype: 'textfield',
                name: 'userName',
                fieldLabel: '用户名'
            },
            {
                xtype: 'combobox',
                name: 'status',
                fieldLabel: '订单状态',
                displayField: 'name',
                valueField: 'id',
                editable: false,
                store: {
                    data: [
                        {id: 0, name: '新建售后单'},
                        {id: 10, name: '未审核售后单'},
                        {id: 20, name: '审核通过售后单'},
                        {id: 21, name: '审核拒绝售后单'},
                        {id: 30, name: '用户寄回商品'},
                        {id: 31, name: '物流拒绝揽件'},
                        {id: 40, name: '卖家已收到商品'},
                        {id: 41, name: '卖家验货不通过'},
                        {id: 42, name: '卖家验货已通过'},
                        {id: 50, name: '商品正在维修'},
                        {id: 51, name: '商品维修失败'},
                        {id: 52, name: '商品维修成功'},
                        {id: 60, name: '卖家寄回商品中'},
                        {id: 61, name: '卖家寄回商品失败'},
                        {id: 80, name: '正在退款中'},
                        {id: 81, name: '退款失败'},
                        {id: 90, name: '已取消售后单'},
                        {id: 100, name: '买家收货/售后完成'}
                    ]
                }
            },
            {
                xtype: 'combobox',
                name: 'type',
                fieldLabel: '售后类型',
                displayField: 'name',
                valueField: 'id',
                editable: false,
                store: {
                    data: [
                        {id: 10, name: '退货'},
                        {id: 20, name: '换货'},
                        {id: 30, name: '维修'}
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
        var store = this.apis.AfterSales.getAfterSales.createPageStore({search: data});
        this.setStore(store);
        store.load();
    }

});
