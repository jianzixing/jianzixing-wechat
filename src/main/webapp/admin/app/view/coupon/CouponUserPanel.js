Ext.define('App.coupon.CouponUserPanel', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.RowNumberer',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.view.Table',
        'Ext.button.Button',
        'Ext.toolbar.Paging'
    ],

    header: false,
    defaultListenerScope: true,

    columns: [
        {
            xtype: 'rownumberer'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'TableUser',
            text: '用户名',
            renderer: function (v) {
                if (v) return v['userName'];
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'TableCoupon',
            text: '优惠券',
            renderer: function (v) {
                if (v) return v['name'];
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'status',
            text: '状态',
            renderer: function (v) {
                if (v == 0) return '<span style="color:#61a4ff">未使用</span>';
                if (v == 1) return '<span style="color:#ff7e54">已使用</span>';
                if (v == 2) return '<span style="color:#aaaaaa">已过期</span>';
                if (v == 3) return '<span style="color:#dc331d">已作废</span>';
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'createTime',
            text: '领取时间',
            renderer: function (v) {
                if (v) return new Date(v).format();
                return '';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'useTime',
            text: '使用时间',
            renderer: function (v) {
                if (v) return new Date(v).format();
                return '[未使用]';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 180,
            dataIndex: 'orderNumber',
            text: '使用订单',
            renderer: function (v) {
                if (v) return v;
                return '[未使用]';
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
                    icon: Resource.png('jet', 'back'),
                    text: '返回优惠券列表',
                    listeners: {
                        click: 'onBackClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'listChanges'),
                    text: '列出已领取优惠券',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'listChanges'),
                    text: '作废已领取优惠券',
                    listeners: {
                        click: 'onDeclareClick'
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

    onListClick: function (button, e, eOpts) {
        this.loadUserCoupons();
    },

    onBackClick: function () {
        this.parent.back();
    },

    onDeclareClick: function () {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            Dialog.batch({
                message: '确定作废用户优惠券吗？' +
                    '<span style="color: #aa2222">作废后不允许再使用，也无法再更改状态</span>',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(datas, "id");
                        self.apis.Coupon.declareUserCoupons
                            .wait(self, '正在作废用户优惠券...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            });
                    }
                }
            });

        } else {
            Dialog.alert('提示', '请先选中优惠券后再作废');
        }
    },

    loadUserCoupons: function () {
        var cid = this._coupon['id'];
        var store = this.apis.Coupon.getUserCoupons.createPageStore({cid: cid});
        this.setStore(store);
        store.load();
    }

});