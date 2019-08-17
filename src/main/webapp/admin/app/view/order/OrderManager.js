Ext.define('App.order.OrderManager', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'Ext.button.Cycle',
        'Ext.menu.Menu',
        'Ext.menu.CheckItem',
        'Ext.toolbar.Paging',
        'Ext.selection.CheckboxModel'
    ],

    border: false,
    header: false,
    defaultListenerScope: true,

    apis: {
        User: {
            getOrderUsers: {}
        },
        Goods: {
            getGoods: {}
        },
        UserAddress: {
            getUserAddressByUserUid: {}
        },
        Order: {
            addOrder: {},
            getOrderPrices: {},
            getOrders: {},
            getOrder: {},
            sendOutOrder: {},
            setOrderFinish: {},
            deleteOrders: {},
            setOrderDelivery: {},
            setOrderCancel: {},
            updateOrderPrice: {},
            updateOrderAddress: {},
            updateOrderDelivery: {},
            getOrderPlatforms: {}
        },
        Logistics: {
            getLogisticsCompany: {}
        },
        Area: {
            getProvince: {},
            getCity: {},
            getArea: {}
        },
        AfterSales: {
            addAfterSales: {}
        }
    },
    search: true,
    columns: [
        {
            xtype: 'gridcolumn',
            width: 200,
            hidden: true,
            dataIndex: 'number',
            text: '订单号'
        },
        {
            xtype: 'gridcolumn',
            width: 380,
            dataIndex: 'TableOrderGoods',
            text: '订单商品',
            renderer: function (v, mate, record) {
                return this.getOrderProductHtml(v, record.getData());
            }
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'TableOrderLogistics',
            text: '配送信息',
            renderer: function (v, mate, record) {
                return this.getOrderDeliveryHtml(v, record);
            }
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'TableOrderAddress',
            text: '收货人信息',
            renderer: function (v) {
                return this.getOrderConsigneeHtml(v);
            }
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'status',
            text: '订单状态',
            renderer: function (v, mate, record) {
                return this.getOrderStateHtml(record);
            }
        },
        {
            xtype: 'gridcolumn',
            width: 180,
            dataIndex: 'payStatus',
            text: '订单金额',
            renderer: function (v, mate, record) {
                return this.getOrderPriceHtml(record);
            }
        },
        {
            xtype: 'actioncolumn',
            text: '操作',
            dataIndex: 'id',
            align: 'center',
            items: [
                {
                    tooltip: '查看订单信息',
                    iconCls: "x-fa fa-eye green",
                    handler: 'onOrderDetailClick'
                },
                {
                    iconCls: "x-fa fa-times red",
                    tooltip: '删除',
                    handler: 'onDelClick'
                }
            ]
        }
    ],
    features: [
        {
            ftype: 'grouping',
            startCollapsed: false,
            groupHeaderTpl: [
                '{columnName}: {name} ',
                '<span style="margin-left: 20px"></span>',
                '创建时间：{[new Date(values.rows[0].get("createTime")).format()]}'
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
                    text: '列出订单',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'addJdk'),
                    text: '添加订单',
                    listeners: {
                        click: 'onAddOrderClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret'),
                    text: '删除订单',
                    listeners: {
                        click: 'onDelClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'order_fqsh_btn',
                    hidden: true,
                    icon: Resource.png('jet', 'resetStrip_dark'),
                    text: '发起售后',
                    listeners: {
                        click: 'onStartAfterSalesClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'menus_status',
                    hidden: true,
                    icon: Resource.png('jet', 'sortbyDuration'),
                    text: '修改状态',
                    menu: {
                        xtype: 'menu',
                        width: 120,
                        items: [
                            {
                                name: 'order_fh_btn',
                                hidden: true,
                                icon: Resource.png('jet', 'applyNotConflictsLeft'),
                                text: '开始发货',
                                listeners: {
                                    click: 'onFHClick'
                                }
                            },
                            {
                                name: 'order_ck_btn',
                                hidden: true,
                                icon: Resource.png('jet', 'outgoingChangesOn'),
                                text: '商品出库',
                                listeners: {
                                    click: 'onCKClick'
                                }
                            },
                            {
                                name: 'order_qx_btn',
                                hidden: true,
                                icon: Resource.png('jet', 'popFrame'),
                                text: '取消订单',
                                listeners: {
                                    click: 'onCancelClick'
                                }
                            }
                        ]
                    }
                },
                {
                    xtype: 'button',
                    hidden: true,
                    name: 'menus_update',
                    icon: Resource.png('jet', 'sortbyDuration'),
                    text: '修改订单',
                    menu: {
                        xtype: 'menu',
                        width: 120,
                        items: [
                            {
                                name: 'order_edit_jg_btn',
                                hidden: true,
                                icon: Resource.png('jet', 'editItemInSection'),
                                text: '修改价格',
                                listeners: {
                                    click: 'onEditPriceClick'
                                }
                            },
                            {
                                name: 'order_edit_dz_btn',
                                hidden: true,
                                icon: Resource.png('jet', 'edit'),
                                text: '修改发货地址',
                                listeners: {
                                    click: 'onEditAddressClick'
                                }
                            },
                            {
                                name: 'order_edit_ydh_btn',
                                hidden: true,
                                icon: Resource.png('jet', 'editSource_dark'),
                                text: '修改运单号',
                                listeners: {
                                    click: 'onEditDeliveryClick'
                                }
                            }
                        ]
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
    listeners: {
        itemclick: 'onOrderItemClick'
    },

    onOrderItemClick: function (dataview, record, item, index, e, eOpts) {
        var order = record.getData();
        this.setButtonShows(order, this);
    },

    onListClick: function (button, e, eOpts) {
        this.onAfterApply();
    },

    onAddOrderClick: function (button, e, eOpts) {
        var self = this;
        var sub = this.parent.forward('App.order.CreateOrderPanel', {
            apis: this.apis,
            preView: this,
            _callback: function () {
                self.refreshStore();
            }
        });
        sub.setValue();
    },

    onDelClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.getIgnoreSelects(arguments);
        if (jsons != null) {
            Dialog.batch({
                message: '确定删除订单信息{d}吗？</br><span style="color: red">标记删除(非物理)后您将看不见订单信息</span>',
                data: jsons,
                key: 'number',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, "id");
                        Dialog.prompt('确定删除订单', '请在下方输入“delete”单词后点击OK按钮', function (btn, txt) {
                            if (btn == 'ok') {
                                if (txt == 'delete') {
                                    self.apis.Order.deleteOrders
                                        .wait(self, '正在删除订单信息...')
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
            Dialog.alert('提示', '请先选中至少一条订单信息后再删除!');
        }
    },

    onCancelClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.getIgnoreSelects(arguments);
        if (jsons != null) {
            Dialog.batch({
                message: '确定取消订单{d}吗？',
                data: jsons,
                key: 'number',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, "id");

                        self.apis.Order.setOrderCancel
                            .wait(self, '正在取消订单...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            });
                    }
                }
            });
        } else {
            Dialog.alert('提示', '请先选中至少一条订单信息后再取消订单!');
        }
    },

    onFHClick: function (button, e, eOpts) {
        var self = this;
        var json = this.getIgnoreSelect(arguments);
        if (json != null) {
            var win = Dialog.openWindow('App.order.OrderSendOutWindow', {
                apis: this.apis,
                _callback: function () {
                    self.refreshStore();
                }
            });
            win.setValue(json);
        } else {
            Dialog.alert('提示', '请先选中至少一条订单信息后再填写发货信息!');
        }
    },

    onCKClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.getIgnoreSelects(arguments);
        if (jsons != null) {
            Dialog.batch({
                message: '确定将订单{d}设置为出库状态吗？',
                data: jsons,
                key: 'number',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(jsons, "id");

                        self.apis.Order.setOrderDelivery
                            .wait(self, '正在设置订单出库...')
                            .call({ids: ids}, function () {
                                self.refreshStore();
                            });
                    }
                }
            });
        } else {
            Dialog.alert('提示', '请先选中至少一条订单信息后再设置订单状态!');
        }
    },

    onEditPriceClick: function (button, e, eOpts) {
        var self = this;
        var json = this.getIgnoreSelect(arguments);
        if (json != null) {
            var win = Dialog.openWindow('App.order.OrderUpdatePriceWindow', {
                apis: this.apis,
                _callback: function () {
                    self.refreshStore();
                }
            });
            win.setValue(json);
        } else {
            Dialog.alert('提示', '请先选中至少一条订单信息后再修改订单价格!');
        }
    },

    onEditAddressClick: function (button, e, eOpts) {
        var self = this;
        var json = this.getIgnoreSelect(arguments);
        if (json != null) {
            var win = Dialog.openWindow('App.order.OrderAddressWindow', {
                apis: this.apis,
                _callback: function () {
                    self.refreshStore();
                }
            });
            win.setValue(json);
        } else {
            Dialog.alert('提示', '请先选中至少一条订单信息后再修改地址信息!');
        }
    },

    onEditDeliveryClick: function () {
        var self = this;
        var json = this.getIgnoreSelect(arguments);
        if (json != null) {
            var win = Dialog.openWindow('App.order.OrderSendOutWindow', {
                apis: this.apis,
                _callback: function () {
                    self.refreshStore();
                }
            });
            win.setEdit(true)
            win.setValue(json);
        } else {
            Dialog.alert('提示', '请先选中至少一条订单信息后再修改发货信息!');
        }
    },

    onOrderDetailClick: function () {
        var data = this.getIgnoreSelect(arguments);
        if (data) {
            var panel = this.parent.forward('App.order.OrderDetail', {
                apis: this.apis,
                preView: this
            });
            panel.setValue(data['id'], this);
        } else {
            Dialog.alert('请先选中一条订单记录后再修改');
        }
    },

    onStartAfterSalesClick: function () {
        var self = this;
        var order = this.getIgnoreSelect(arguments);
        if (order) {
            var win = Dialog.openWindow('App.order.OrderAfterSalesWindow', {
                apis: this.apis,
                orderManager: this,
                _callback: function () {
                    self.refreshStore();
                }
            });
            win.setValue(order);
        } else {
            Dialog.alert('请先选中一条订单信息后再发起售后');
        }
    },

    setButtonShows: function (order, cmp) {
        var fh = cmp.find('order_fh_btn'); //发货
        var ck = cmp.find('order_ck_btn'); //出库
        var xqdd = cmp.find('order_qx_btn');//取消订单

        var xgjg = cmp.find('order_edit_jg_btn'); //修改价格
        var xgdz = cmp.find('order_edit_dz_btn'); //修改地址
        var xgydh = cmp.find('order_edit_ydh_btn'); //修改运单号

        var fqsh = cmp.find('order_fqsh_btn');

        var menus_status = cmp.find('menus_status');
        var menus_update = cmp.find('menus_update');

        var status = order['status'];
        if (status == 0) { // 新建
            fh.hide(), ck.hide(), xqdd.show(), menus_status.show();
            xgjg.show(), xgdz.show(), xgydh.hide(), menus_update.show();
            fqsh.hide();

            if (fh) fh.hide();
            if (ck) ck.hide();
            if (xqdd) xqdd.show();
            if (menus_status) menus_status.show();
            if (xgjg) xgjg.show();
            if (xgdz) xgdz.show();
            if (xgydh) xgydh.hide();
            if (menus_update) menus_update.show();
            if (fqsh) fqsh.hide();
        }
        if (status == 10) { // 订单已支付
            if (fh) fh.show();
            if (ck) ck.hide();
            if (xqdd) xqdd.show(); //未发货订单可取消
            if (menus_status) menus_status.show();
            if (xgjg) xgjg.hide();
            if (xgdz) xgdz.show();
            if (xgydh) xgydh.hide();
            if (menus_update) menus_update.show();
            if (fqsh) fqsh.hide();
        }
        if (status == 20) { // 订单已确认
            if (fh) fh.show();
            if (ck) ck.hide();
            if (xqdd) xqdd.hide();
            if (menus_status) menus_status.show();
            if (xgjg) xgjg.hide();
            if (xgdz) xgdz.show();
            if (xgydh) xgydh.hide();
            if (menus_update) menus_update.show();
            if (fqsh) fqsh.hide();
        }
        if (status == 30) { // 订单商品已发货
            if (fh) fh.hide();
            if (ck) ck.show();
            if (xqdd) xqdd.hide();
            if (menus_status) menus_status.show();
            if (xgjg) xgjg.hide();
            if (xgdz) xgdz.hide();
            if (xgydh) xgydh.show();
            if (menus_update) menus_update.show();
            if (fqsh) fqsh.hide();
        }
        if (status == 40) { // 订单商品已出库
            if (fh) fh.hide();
            if (ck) ck.hide();
            if (xqdd) xqdd.hide();
            if (menus_status) menus_status.hide();
            if (xgjg) xgjg.hide();
            if (xgdz) xgdz.hide();
            if (xgydh) xgydh.hide();
            if (menus_update) menus_update.hide();
            if (fqsh) fqsh.hide();
        }
        if (status == 41) { // 订单商品退回或拒收
            if (fh) fh.hide();
            if (ck) ck.hide();
            if (xqdd) xqdd.hide();
            if (menus_status) menus_status.hide();
            if (xgjg) xgjg.hide();
            if (xgdz) xgdz.hide();
            if (xgydh) xgydh.hide();
            if (menus_update) menus_update.hide();
            if (fqsh) fqsh.hide();
        }
        if (status == 50 || status == 60) { // 用户已确认收货 , 订单完成
            if (fh) fh.hide();
            if (ck) ck.hide();
            if (xqdd) xqdd.hide();
            if (menus_status) menus_status.hide();
            if (xgjg) xgjg.hide();
            if (xgdz) xgdz.hide();
            if (xgydh) xgydh.hide();
            if (menus_update) menus_update.hide();
            if (fqsh) fqsh.show();
        }
        if (status == 90) { // 订单取消
            if (fh) fh.hide();
            if (ck) ck.hide();
            if (xqdd) xqdd.hide();
            if (menus_status) menus_status.hide();
            if (xgjg) xgjg.hide();
            if (xgdz) xgdz.hide();
            if (xgydh) xgydh.hide();
            if (menus_update) menus_update.hide();
            if (fqsh) fqsh.show();
        }
    },

    getOrderProductHtml: function (products, order) {
        var html = [];
        html.push('<div style="width: 100%;overflow: hidden;padding: 4px">');
        for (var i = 0; i < products.length; i++) {
            var p = products[i];
            if (i == products.length - 1) {
                html.push('<div style="width: 380px;overflow: hidden">');
            } else {
                html.push('<div style="width: 380px;overflow: hidden;border-bottom: 1px solid #e9e9e9;margin-bottom: 10px">');
            }
            html.push('<div style="width: 60px;height: 60px;float: left">')
            html.push('<div style="display:table-cell;vertical-align:middle;text-align:center;width: 60px;height: 60px;border: 1px solid #e9e9e9;overflow: hidden">');
            html.push('<a href="javascript:void(0);">');
            html.push('<img src="' + Resource.image(p['fileName']) + '" title="" style="max-width: 60px;max-height: 60px">');
            html.push('</a>');
            html.push('</div>');
            html.push('</div>');
            html.push('<div style="float:left;width:280px;overflow: hidden;margin-left: 14px;word-wrap:break-word;word-break:break-all;white-space:normal;text-align: left">');
            html.push('<div style="width: 100%;height: 38px;overflow: hidden">');
            html.push('<a style="text-decoration: none;color: #0f74a8;font-size: 13px" href="javascript:void(0);">');
            html.push(p['goodsName'] || p['name']);
            html.push('</a>');
            html.push('</div>');
            html.push('<div style="width: 100%">');
            html.push('<div style="float: left;font-size: 12px;line-height: 30px;width: 100%">');
            var unit = p['unit'] || '';
            if (order == 2) {
                html.push('<span style="color: #aaaaaa">剩余数量： ' + p['amount'] + unit + '</span>');
            } else {
                html.push('<span style="color: #aaaaaa">购买数量： ' + p['amount'] + unit + '</span>');
            }
            var skuName = this.getSkuName(order, p);
            if (skuName && Ext.isArray(skuName) && skuName.length > 0) {
                html.push('<span style="color: #aaaaaa;margin-left: 20px">规格：' + skuName.join('&nbsp;&nbsp;') + ' </span>');
            }
            html.push('</div>');
            html.push('</div>');
            html.push('</div>');
            html.push('</div>');
        }

        html.push('</div>');
        return html.join("");
    },

    getSkuName: function (order, goods) {
        if (order && goods) {
            var togp = order['TableOrderGoodsProperty'];
            var goodsId = goods['goodsId'];
            var skuId = goods['skuId'];
            if (togp && Ext.isArray(togp)) {
                var attr = [];
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
        }
    },

    getOrderDeliveryHtml: function (delivery, record) {
        var name,
            deliveryType = record.get('deliveryId'),
            price = record.get('freightPrice'),
            cmp = record.get('lgsCompanyName'),
            nm = record.get('trackingNumber'),
            sendTime = record.get('sendTime');

        if (delivery) {
            name = delivery['name'];
        } else {
            if (deliveryType) {
                if (deliveryType == 10) name = '快递';
                if (deliveryType == 11) name = 'EMS';
                if (deliveryType == 12) name = '平邮';
            }
        }

        if (!!!name) name = "未知方式";
        var html = [];
        html.push('<div style="width: 100%;overflow: hidden;padding: 4px;font-size: 12px">');
        html.push('<div style="color: #666666;margin: 0px 0px 5px 0px"><span style="width: 100px">配送方式：</span>' + name + '</div>');
        html.push('<div style="color: #666666;margin: 5px 0px"><span style="width: 100px">运费：</span>¥' + price + '</div>');
        if (sendTime) {
            html.push('<div style="color: #666666;margin: 5px 0px"><span style="width: 100px">发货时间：</span>' + (new Date(sendTime)).format() + '</div>');
        }
        if (cmp && nm) {
            html.push('<div style="color: #666666;margin: 5px 0px"><span style="width: 100px">' + cmp + '：</span>' + nm + '</div>');
        }
        html.push('</div>');
        return html.join("");
    },

    getOrderConsigneeHtml: function (consignee) {
        var html = [];
        html.push('<div style="width: 100%;overflow: hidden;padding: 4px;font-size: 12px">');
        html.push('<div style="color: #666666;font-weight: bold;margin: 0px 0px 5px 0px"><strong>' + consignee['realName'] + '</strong></div>');
        html.push('<div style="color: #666666;white-space: normal;;margin: 5px 0px">');
        html.push(consignee['province'] + consignee['city'] + consignee['county'] + consignee['address']);
        html.push('</div>');
        html.push('<div style="color: #666666;margin: 5px 0px">' + (consignee['phoneNumber'] || consignee['telNumber']) + '</div>');
        html.push('</div>');
        return html.join("");
    },

    getOrderStateHtml: function (record) {
        var status = record.get('status');
        var payStatus = record.get('payStatus');
        var refundStatus = record.get('refundStatus');
        var createTime = record.get('createTime');
        var payTime = record.get('payTime');
        var statusName = '';
        if (status == 0) statusName = '<span style="color: #dc561d">未支付</span>';
        if (status == 10) statusName = '<span style="color: #326a8f">已支付</span>';
        if (status == 20) statusName = '<span style="color: #326a8f">已确认</span>';
        if (status == 30) statusName = '<span style="color: #326a8f">已发货</span>';
        if (status == 40) statusName = '<span style="color: #326a8f">已出库</span>';
        if (status == 41) statusName = '<span style="color: red">退回或拒收</span>';
        if (status == 50) statusName = '<span style="color: #305a7d">已确认收货</span>';
        if (status == 60) statusName = '<span style="color: #305a7d">订单已完成</span>';
        if (status == 90) statusName = '<span style="color: red">订单已取消</span>';

        var refundStatusName = '';
        if (refundStatus == 1) refundStatusName = '全部退款';
        if (refundStatus == 2) refundStatusName = '部分退款';

        var payStatusName = "";
        if (payStatus == 0) payStatusName = '<span style="color: #dc561d">未支付</span>';
        if (payStatus == 1) payStatusName = '<span style="color: #15abff">已支付</span>';
        if (payStatus == 2) payStatusName = '<span style="color: #61a4ff">半支付</span>';
        if (payStatus == 3) payStatusName = '<span style="color: #dc7b71">全退款</span>';
        if (payStatus == 4) payStatusName = '<span style="color: #ff4546">半退款</span>';

        var html = [];
        html.push('<div style="width: 100%;overflow: hidden;padding: 4px;font-size: 12px">');
        html.push('<div style="color: #666666;margin: 0px 0px 5px 0px"><span style="width: 100px">支付状态：</span>'
            + payStatusName + '</div>');
        if (payTime) {
            html.push('<div style="color: #666666;margin: 0px 0px 5px 0px"><span style="width: 100px">支付时间：</span>' + (new Date(payTime)).format() + '</div>');
        }
        html.push('<div style="color: #666666;margin: 5px 0px"><span style="width: 100px">订单状态：</span>' + statusName + '</div>');
        if (refundStatusName != '') {
            html.push('<div style="color: darkred;margin: 5px 0px"><span style="width: 100px">退款状态：</span>' + refundStatusName + '</div>');
        }
        html.push('<div style="color: #666666;margin: 0px 0px 5px 0px"><span style="width: 100px">下单时间：</span>' + (new Date(createTime)).format() + '</div>');
        html.push('</div>');
        return html.join("");
    },

    getOrderPriceHtml: function (record) {
        var payPrice = record.get('payPrice');
        var totalGoodsPrice = record.get('totalGoodsPrice');
        var freightPrice = record.get('freightPrice');
        var discountPrice = record.get('discountPrice');
        var html = [];
        html.push('<div style="width: 100%;overflow: hidden;padding: 4px;font-size: 12px">');
        html.push('<div style="color: #e4393c;font-weight: bold;font-size:12px;margin: 0px 0px 5px 0px"><span style="width: 100px">订单总额：</span>¥' + payPrice + '</div>');
        html.push('<div style="color: #666666;margin: 5px 0px"><span style="width: 100px">商品总额：</span>¥' + totalGoodsPrice + '</div>');
        html.push('<div style="color: #666666;margin: 5px 0px"><span style="width: 100px">配送金额：</span>+&nbsp;¥' + freightPrice + '</div>');
        html.push('<div style="color: #666666;margin: 5px 0px"><span style="width: 100px">优惠金额：</span>-&nbsp;&nbsp;¥' + discountPrice + '</div>');
        html.push('</div>');
        return html.join("");
    },

    onAfterApply: function () {
        var moduleObject = this.parent.moduleObject;
        var type = moduleObject.get('type');
        var param = {};
        if (type == 'create') param = {status: [0]};
        if (type == 'pay') param = {status: [10, 20]};
        if (type == 'delivery') param = {status: [30, 40]};
        if (type == 'finish') param = {status: [50, 60]};
        if (type == 'cancel') param = {status: [90]};
        var data = this.apis.Order.getOrders.info(param);
        var store = Ext.create('Ext.data.Store', {
            groupField: 'number',
            remoteSort: true,
            proxy: {
                type: 'ajax',
                url: data.url,
                extraParams: data.data || {},
                reader: {
                    type: 'json',
                    root: 'records',
                    totalProperty: 'total'
                }
            }
        });
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
                name: 'number',
                fieldLabel: '订单号'
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
                        {id: 0, name: '新建订单'},
                        {id: 10, name: '订单已支付'},
                        {id: 20, name: '订单已确认'},
                        {id: 30, name: '订单已发货'},
                        {id: 50, name: '用户已收货'},
                        {id: 60, name: '订单已完成'},
                        {id: 90, name: '订单已取消'}
                    ]
                }
            },
            {
                xtype: 'combobox',
                name: 'payStatus',
                fieldLabel: '支付状态',
                displayField: 'name',
                valueField: 'id',
                editable: false,
                store: {
                    data: [
                        {id: 0, name: '订单未支付'},
                        {id: 1, name: '订单已支付'}
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
        var values = form.getForm().getValues();
        var data = this.apis.Order.getOrders.info({search: values});
        var store = Ext.create('Ext.data.Store', {
            groupField: 'number',
            remoteSort: true,
            proxy: {
                type: 'ajax',
                url: data.url,
                extraParams: data.data || {},
                reader: {
                    type: 'json',
                    root: 'records',
                    totalProperty: 'total'
                }
            }
        });
        this.setStore(store);
        store.load();
    }

});
