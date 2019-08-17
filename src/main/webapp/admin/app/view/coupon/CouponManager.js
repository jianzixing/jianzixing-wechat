Ext.define('App.coupon.CouponManager', {
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
        Coupon: {
            getCouponInit: {},
            getCoupons: {},
            addCoupon: {},
            delCoupons: {},
            updateCoupon: {},
            enableCoupons: {},
            disableCoupons: {},
            finishCoupons: {},
            addCouponGoods: {},
            getCouponGoods: {},
            removeCouponGoods: {},
            getUserCoupons: {},
            declareUserCoupons: {}
        },
        Goods: {
            getGoods: {}
        }
    },

    columns: [
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'name',
            text: '优惠券名称'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'channel',
            text: '获取渠道',
            renderer: function (v) {
                if (v == 0) return '其他渠道';
                if (v == 1) return '网站领取';
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'status',
            text: '状态',
            renderer: function (v) {
                if (v == 0) return '<span style="color: #aa2222">未启用</span>';
                if (v == 1) return '未开始';
                if (v == 2) return '获取中';
                if (v == 3) return '已结束';
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'orderPrice',
            text: '优惠',
            renderer: function (v, mate, record) {
                var orderPrice = record.get('orderPrice');
                var couponPrice = record.get('couponPrice');
                return '满' + orderPrice + '减' + couponPrice;
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'count',
            text: '每人限领'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'amount',
            text: '发行量'
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'prepareAmount',
            text: '已领取'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'startTime',
            text: '开始时间',
            renderer: function (v) {
                return new Date(v).format();
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'finishTime',
            text: '结束时间',
            renderer: function (v) {
                return new Date(v).format();
            }
        },
        {
            xtype: 'actioncolumn',
            dataIndex: 'isDel',
            width: 150,
            text: '操作',
            align: 'center',
            items: [
                {
                    iconCls: 'x-fa fa-cubes',
                    tooltip: '查看优惠券商品',
                    handler: 'onGoodsClick',
                    getClass: function (v) {
                        if (v == 1) return 'x-hidden';
                        else return 'x-fa fa-cubes';
                    }
                },
                {
                    iconCls: 'x-fa fa-newspaper-o',
                    tooltip: '查看已领取优惠券',
                    handler: 'onCouponListClick',
                    getClass: function (v) {
                        if (v == 1) return 'x-hidden';
                        else return 'x-fa fa-newspaper-o';
                    }
                },
                {
                    iconCls: 'x-fa fa-pencil',
                    tooltip: '修改活动',
                    handler: 'onUpdateClick',
                    getClass: function (v) {
                        if (v == 1) return 'x-hidden';
                        else return 'x-fa fa-pencil';
                    }
                },
                {
                    iconCls: 'x-fa fa-times',
                    tooltip: '删除活动',
                    handler: 'onDelClick',
                    getClass: function (v) {
                        if (v == 1) return 'x-hidden';
                        else return 'x-fa fa-times';
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
                    text: '列出优惠券',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addLink'),
                    text: '添加优惠券',
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret_dark'),
                    text: '删除优惠券',
                    listeners: {
                        click: 'onDelClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'toolWindowRun'),
                    text: '启用优惠券',
                    listeners: {
                        click: 'onEnableClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'recording_stop'),
                    text: '结束优惠券',
                    listeners: {
                        click: 'onEndClick'
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

    onAddClick: function (button, e, eOpts) {
        var self = this;
        var tab = this.parent.forward('App.coupon.CouponPanel', {
            apis: this.apis,
            _callback: function () {
                self.refreshStore();
            }
        });
        tab.initWindow();
    },

    onEnableClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            Dialog.batch({
                message: '确定启用优惠券{d}吗？<br/>' +
                    '<span style="color: red">启用后如果活动状态是"获取中"则不允许再修改参数配置</span>',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(datas, "id");
                        self.apis.Coupon.enableCoupons
                            .wait(self, '正在启用优惠券...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            });
                    }
                }
            });

        } else {
            Dialog.alert('提示', '请先选中优惠券后再启用');
        }
    },

    onDelClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            Dialog.batch({
                message: '确定删除优惠券{d}吗？',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(datas, "id");
                        self.apis.Coupon.delCoupons
                            .wait(self, '正在删除优惠券...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            });
                    }
                }
            });

        } else {
            Dialog.alert('提示', '请先选中优惠券后再删除');
        }
    },

    onEndClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            Dialog.batch({
                message: '确定结束优惠券{d}吗？<br/>' +
                    '<span style="color: red">结束优惠券后将无法再修改优惠券状态，且用户领取的所有优惠券将全部失效</span>',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(datas, "id");
                        self.apis.Coupon.finishCoupons
                            .wait(self, '正在结束优惠券...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            });
                    }
                }
            });

        } else {
            Dialog.alert('提示', '请先选中优惠券后再结束');
        }
    },

    onGoodsClick: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var tab = self.parent.forward('App.coupon.CouponGoodsPanel', {
                _coupon: data,
                apis: this.apis
            });
            tab.loadGoods(data);
        } else {
            Dialog.alert('提示', '请先选中优惠券后再查看商品列表');
        }
    },

    onCouponListClick: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var tab = self.parent.forward('App.coupon.CouponUserPanel', {
                _coupon: data,
                apis: this.apis
            });
            tab.loadUserCoupons(data);
        } else {
            Dialog.alert('提示', '请先选中优惠券后再查看已领取列表');
        }
    },

    onUpdateClick: function () {
        var self = this;
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            if (data['status'] == 0 || data['status'] == 1) {
                var tab = self.parent.forward('App.coupon.CouponPanel', {
                    apis: this.apis,
                    _callback: function () {
                        self.refreshStore();
                    }
                });
                tab.setValue(data);
                tab.initWindow();
            } else {
                var tab = self.parent.forward('App.coupon.CouponDetail', {
                    apis: this.apis,
                    _callback: function () {
                        self.refreshStore();
                    }
                });
                tab.setValue(data);
            }
        } else {
            Dialog.alert('请先选中一条优惠券信息');
        }
    },

    onAfterApply: function () {
        var store = this.apis.Coupon.getCoupons.createPageStore();
        this.setStore(store);
        store.load();
    }

});
