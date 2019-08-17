Ext.define('App.order.OrderDetail', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    border: false,
    header: false,
    autoScroll: true,
    defaultListenerScope: true,
    bodyStyle: {
        backgroundColor: '#f5f5f5'
    },

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'back'),
                    text: '返回列表',
                    listeners: {
                        click: 'onBackClick'
                    }
                },
                {
                    xtype: 'button',
                    hidden: true,
                    icon: Resource.png('jet', 'print'),
                    text: '打印订单',
                    listeners: {
                        click: 'onPrintClick'
                    }
                }
            ]
        },
        {
            xtype: 'toolbar',
            dock: 'bottom',
            items: [
                '->',
                {
                    xtype: 'button',
                    name: 'order_fh_btn',
                    hidden: true,
                    icon: Resource.png('jet', 'applyNotConflictsLeft'),
                    text: '开发发货',
                    listeners: {
                        click: 'onSendOutClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'order_ck_btn',
                    hidden: true,
                    icon: Resource.png('jet', 'outgoingChangesOn'),
                    text: '商品出库',
                    listeners: {
                        click: 'onCKClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'order_edit_jg_btn',
                    hidden: true,
                    icon: Resource.png('jet', 'editItemInSection'),
                    text: '修改价格',
                    listeners: {
                        click: 'onEditPriceClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'order_edit_dz_btn',
                    hidden: true,
                    icon: Resource.png('jet', 'edit'),
                    text: '修改发货地址',
                    listeners: {
                        click: 'onEditAddressClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'order_edit_ydh_btn',
                    hidden: true,
                    icon: Resource.png('jet', 'editSource_dark'),
                    text: '修改运单号',
                    listeners: {
                        click: 'onEditDeliveryClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'order_qx_btn',
                    hidden: true,
                    icon: Resource.png('jet', 'popFrame'),
                    text: '取消订单',
                    listeners: {
                        click: 'onCancelClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'order_fqsh_btn',
                    icon: Resource.png('jet', 'resetStrip_dark'),
                    text: '发起售后',
                    listeners: {
                        click: 'onStartAfterSalesClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'order_edit_ydh_btn',
                    icon: Resource.png('jet', 'rollback'),
                    text: '返回订单列表',
                    listeners: {
                        click: 'onBackClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'set_finish_button',
                    hidden: true,
                    icon: Resource.png('jet', 'GreenOK'),
                    text: '完成订单',
                    listeners: {
                        click: 'onFinishClick'
                    }
                },
                '->'
            ]
        }
    ],
    items: [
        {
            xtype: 'container',
            name: 'order',
            width: 1050,
            style: {
                marginLeft: 'auto',
                marginRight: 'auto',
                marginTop: '30px',
                paddingBottom: '50px'
            }
        }
    ],

    onBackClick: function (button, e, eOpts) {
        this.parent.back();
        this.preView.refreshStore();
    },

    onPrintClick: function () {

    },

    onCKClick: function () {
        var self = this;
        if (this._order) {
            var jsons = [this._order];
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
                                self.reloadOrder();
                            });
                    }
                }
            });
        }
    },

    onCancelClick: function () {
        var self = this;
        if (this._order) {
            var jsons = [this._order];
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
                                self.onBackClick();
                            });
                    }
                }
            });
        }
    },

    onEditPriceClick: function () {
        var self = this;
        var json = this._order;
        if (json != null) {
            var win = Dialog.openWindow('App.order.OrderUpdatePriceWindow', {
                apis: this.apis,
                _callback: function () {
                    self.reloadOrder();
                }
            });
            win.setValue(json);
        }
    },

    onEditAddressClick: function () {
        var self = this;
        var json = this._order;
        if (json != null) {
            var win = Dialog.openWindow('App.order.OrderAddressWindow', {
                apis: this.apis,
                _callback: function () {
                    self.reloadOrder();
                }
            });
            win.setValue(json);
        }
    },

    onEditDeliveryClick: function () {
        var self = this;
        var json = this._order;
        if (json != null) {
            var win = Dialog.openWindow('App.order.OrderSendOutWindow', {
                apis: this.apis,
                _callback: function () {
                    self.reloadOrder();
                }
            });
            win.setEdit(true);
            win.setValue(json);
        }
    },

    onFinishClick: function () {
        var self = this;
        Dialog.confirm('提示', '确定设置订单已完成吗？', function (btn) {
            if (btn == 'yes') {
                self.apis.Order.setOrderFinish
                    .wait(self, '正在设置订单状态...')
                    .call({oid: self._order['id']}, function () {
                        self.reloadOrder();
                    });
            }
        })
    },

    onSendOutClick: function () {
        var self = this;
        var win = Dialog.openWindow('App.order.OrderSendOutWindow', {
            apis: this.apis,
            _callback: function () {
                self.reloadOrder();
            }
        });
        win.setValue(this._order);
    },

    getOrderHeadHtml: function (order) {
        var status = order['status'];
        var number = order['number'];
        var createTime = order['createTime'];
        var user = order['TableUser'] || {};

        var bg = ['50% -204px', '50% -170px', '50% 0', '50% -68px', '50% -102px', '50% -136px', '50% -34px'];
        var step1 = bg[0], step2 = bg[4], step3 = bg[4], step4 = bg[4], step5 = bg[5],
            sn1 = false, sn2 = false, sn3 = false, sn4 = false, sn5 = false;
        var state = 5;
        if (status == 0) state = 1;
        if (status == 10 || status == 20) state = 2;
        if (status == 30 || status == 40) state = 3;
        if (status == 50) state = 4;
        if (status == 60) state = 5;
        if (state == 1) step1 = bg[6], step2 = bg[4], step3 = bg[4], step4 = bg[4], step5 = bg[5],
            sn1 = true, sn2 = true, sn3 = true, sn4 = true, sn5 = true;
        if (state == 2) step1 = bg[0], step2 = bg[2], step3 = bg[4], step4 = bg[4], step5 = bg[5],
            sn1 = false, sn2 = true, sn3 = true, sn4 = true, sn5 = true;
        if (state == 3) step1 = bg[0], step2 = bg[1], step3 = bg[2], step4 = bg[4], step5 = bg[5],
            sn1 = false, sn2 = false, sn3 = true, sn4 = true, sn5 = true;
        if (state == 4) step1 = bg[0], step2 = bg[1], step3 = bg[1], step4 = bg[2], step5 = bg[5],
            sn1 = false, sn2 = false, sn3 = false, sn4 = true, sn5 = true;
        if (state == 5) step1 = bg[0], step2 = bg[1], step3 = bg[1], step4 = bg[1], step5 = bg[3],
            sn1 = false, sn2 = false, sn3 = false, sn4 = false, sn5 = true;


        var html = [];
        html.push('<div style="width: 100%;font-size: 12px;margin-top: 20px;background-color: #fff;overflow: hidden;padding-bottom:50px">');
        html.push('<div style="width: 100%;border-top:3px solid #009349;font-size: 12px;"></div>');
        html.push('<div style="width: 100%;font-size: 13px;text-align: center;margin-top: 30px;color: #666666;overflow: hidden">');
        html.push('<span style="margin-right:10px;font-weight: bold">订单编号：' + number + '</span>');
        html.push('于<span style="margin-left:10px;margin-right:10px;font-weight: bold">' + (new Date(createTime)).format() + '</span>');
        html.push('被<span style="margin-left:10px;margin-right:10px;font-weight: bold">' +
            (user['userName'] + (user['nick'] ? ' (' + decodeURIComponent(user['nick']) + ')' : '')) + '</span>创建');
        html.push('</div>');
        html.push('<ul style="width: 100%;text-decoration: none;padding:0px;margin:0px;margin: 30px 0px 50px 0px;">');

        html.push('<li style="width: 210px;float: left;text-align: center;list-style: none">');
        html.push('<div style="width: 100%">');
        html.push('<div style="width: 100%;font-weight: 700;padding: 3px 0px;color: #888">');
        html.push('拍下商品');
        html.push('</div>');
        html.push('<div style="height: 34px;width: 100%;font-size: 18px;line-height: 34px;color:#fff;font-weight:bold;' +
            'background: url(image/order_step.png) ' + step1 + ' no-repeat;">' + (sn1 ? '1' : '') + '</div>');
        html.push('<div style="color:#999;width: 210px;font-size: 12px;margin-top: 10px;padding: 8px 0px;">2018-06-27 11:22:40</div>');
        html.push('</div>');
        html.push('</li>');

        html.push('<li style="width: 210px;float: left;text-align: center;list-style: none">');
        html.push('<div style="width: 100%">');
        html.push('<div style="width: 100%;font-weight: 700;padding: 3px 0px;color: #888">');
        html.push('付款成功');
        html.push('</div>');
        html.push('<div style="height: 34px;width: 100%;font-size: 18px;line-height: 34px;color:#fff;font-weight:bold;' +
            'background: url(image/order_step.png) ' + step2 + ' no-repeat;">' + (sn2 ? '2' : '') + '</div>');
        html.push('<div style="color:#999;width: 210px;font-size: 12px;margin-top: 10px;padding: 8px 0px;">2018-06-27 11:22:40</div>');
        html.push('</div>');
        html.push('</li>');

        html.push('<li style="width: 210px;float: left;text-align: center;list-style: none">');
        html.push('<div style="width: 100%">');
        html.push('<div style="width: 100%;font-weight: 700;padding: 3px 0px;color: #888">');
        html.push('卖家发货');
        html.push('</div>');
        html.push('<div style="height: 34px;width: 100%;font-size: 18px;line-height: 34px;color:#fff;font-weight:bold;' +
            'background: url(image/order_step.png) ' + step3 + ' no-repeat;">' + (sn3 ? '3' : '') + '</div>');
        html.push('<div style="color:#999;width: 210px;font-size: 12px;margin-top: 10px;padding: 8px 0px;">2018-06-27 11:22:40</div>');
        html.push('</div>');
        html.push('</li>');

        html.push('<li style="width: 210px;float: left;text-align: center;list-style: none">');
        html.push('<div style="width: 100%">');
        html.push('<div style="width: 100%;font-weight: 700;padding: 3px 0px;color: #888">');
        html.push('确认收货');
        html.push('</div>');
        html.push('<div style="height: 34px;width: 100%;font-size: 18px;line-height: 34px;color:#fff;font-weight:bold;' +
            'background: url(image/order_step.png) ' + step4 + ' no-repeat;">' + (sn4 ? '4' : '') + '</div>');
        html.push('<div style="color:#999;width: 210px;font-size: 12px;margin-top: 10px;padding: 8px 0px;">2018-06-27 11:22:40</div>');
        html.push('</div>');
        html.push('</li>');

        html.push('<li style="width: 210px;float: left;text-align: center;list-style: none">');
        html.push('<div style="width: 100%">');
        html.push('<div style="width: 100%;font-weight: 700;padding: 3px 0px;color: #888">');
        html.push('评价');
        // html.push('完成');
        html.push('</div>');
        html.push('<div style="height: 34px;width: 100%;font-size: 18px;line-height: 34px;color:#fff;font-weight:bold;' +
            'background: url(image/order_step.png) ' + step5 + ' no-repeat;">' + (sn5 ? '5' : '') + '</div>');
        html.push('<div style="color:#999;width: 210px;font-size: 12px;margin-top: 10px;padding: 8px 0px;">2018-06-27 11:22:40</div>');
        html.push('</div>');
        html.push('</li>');

        html.push('</ul>');
        html.push('</div>');
        return html;
    },

    getOrderInfoHtml: function (order) {
        var consignee = order['TableOrderAddress'];
        var html = [];
        html.push('<div style="width: 100%;font-size: 12px;margin-top: 20px;background-color: #fff;overflow: hidden;padding: 20px 0px 30px 80px">');

        html.push('<div style="width: 320px;padding: 0px 30px 0px 0px;border-right: 1px solid #f1f1f1;float: left">');
        html.push('<div style="width: 100%;height: 28px;color: #333;line-height: 28px;font-size: 14px;font-weight: 400;">收货人信息</div>');
        html.push('<div style="width: 100%;color: #333;line-height: 24px;font-size: 12px;;overflow: hidden">');
        html.push('<span style="display:block;float:left;width: 85px;">收货人：</span><span style="display: block;float:left;width: 204px">' + consignee['realName'] + '</span>');
        html.push('</div>');
        html.push('<div style="width: 100%;color: #333;line-height: 24px;font-size: 12px;;overflow: hidden">');
        html.push('<span style="display:block;float:left;width: 85px">地址：</span><span style="display: block;float:left;width: 204px">');
        html.push(consignee['province'] + consignee['city'] + consignee['county'] + consignee['address']);
        html.push('</span>');
        html.push('</div>');
        html.push('<div style="width: 100%;color: #333;line-height: 24px;font-size: 12px;;overflow: hidden">');
        html.push('<span style="display:block;float:left;width: 85px">手机号码：</span><span style="display: block;float:left;width: 204px">');
        html.push(consignee['phoneNumber'] || consignee['telNumber']);
        html.push('</span>');
        html.push('</div>');
        html.push('</div>');

        var delivery = order['TableOrderLogistics'] || {};
        var name = delivery['name'];
        var price = delivery['price'] || '0';
        if (!!!name) name = "未知方式";
        var sendTime = order['sendTime'];
        var lgsCompanyName = order['lgsCompanyName'];
        var trackingNumber = order['trackingNumber'];

        html.push('<div style="width: 320px;padding: 0px 30px;border-right: 1px solid #f1f1f1;float: left">');
        html.push('<div style="width: 100%;height: 28px;color: #333;line-height: 28px;font-size: 14px;font-weight: 400;">配送信息</div>');
        if (name) {
            html.push('<div style="width: 100%;color: #333;line-height: 24px;font-size: 12px;;overflow: hidden">');
            html.push('<span style="display:block;float:left;width: 85px;">配送方式：</span><span style="display: block;float:left;width: 150px">' + name + '</span>');
            html.push('</div>');
        }
        if (price) {
            html.push('<div style="width: 100%;color: #333;line-height: 24px;font-size: 12px;;overflow: hidden">');
            html.push('<span style="display:block;float:left;width: 85px">运费：</span><span style="display: block;float:left;width: 150px">¥' + price + '</span>');
            html.push('</div>');
        }
        if (lgsCompanyName) {
            html.push('<div style="width: 100%;color: #333;line-height: 24px;font-size: 12px;;overflow: hidden">');
            html.push('<span style="display:block;float:left;width: 85px">承运商：</span><span style="display: block;float:left;width: 150px">' + lgsCompanyName + '</span>');
            html.push('</div>');
        }
        if (trackingNumber) {
            html.push('<div style="width: 100%;color: #333;line-height: 24px;font-size: 12px;;overflow: hidden">');
            html.push('<span style="display:block;float:left;width: 85px">货运单：</span><span style="display: block;float:left;width: 150px">' + trackingNumber + '</span>');
            html.push('</div>');
        }
        if (sendTime) {
            html.push('<div style="width: 100%;color: #333;line-height: 24px;font-size: 12px;;overflow: hidden">');
            html.push('<span style="display:block;float:left;width: 85px">发货日期：</span><span style="display: block;float:left;width: 150px">' + (new Date(sendTime)).format() + '</span>');
            html.push('</div>');
        }
        html.push('</div>');

        var methods = order['TablePaymentTransaction'] || {};
        var name = [];
        if (methods != null) {
            for (var i = 0; i < methods.length; i++) {
                name.push(methods[i]['payChannelName']);
            }
        }
        name = name.join(",");
        var payTime = order['payTime'];
        var goodsPrice = order['totalGoodsPrice'] || 0;
        var payPrice = order['payPrice'] || 0;
        html.push('<div style="width: 320px;padding: 0px 0px 0px 30px;float: left">');
        html.push('<div style="width: 100%;height: 28px;color: #333;line-height: 28px;font-size: 14px;font-weight: 400;">付款信息</div>');
        if (name) {
            html.push('<div style="width: 100%;color: #333;line-height: 24px;font-size: 12px;;overflow: hidden">');
            html.push('<span style="display:block;float:left;width: 85px;">付款方式：</span><span style="display: block;float:left;width: 204px">' + name + '</span>');
            html.push('</div>');
        }
        if (payTime) {
            html.push('<div style="width: 100%;color: #333;line-height: 24px;font-size: 12px;;overflow: hidden">');
            html.push('<span style="display:block;float:left;width: 85px">付款时间：</span><span style="display: block;float:left;width: 204px">' + (new Date(payTime)).format() + '</span>');
            html.push('</div>');
        }
        html.push('<div style="width: 100%;color: #333;line-height: 24px;font-size: 12px;;overflow: hidden">');
        html.push('<span style="display:block;float:left;width: 85px">商品总额：</span><span style="display: block;float:left;width: 204px">¥' + goodsPrice + '</span>');
        html.push('</div>');

        html.push('<div style="width: 100%;color: #333;line-height: 24px;font-size: 12px;;overflow: hidden">');
        html.push('<span style="display:block;float:left;width: 85px">应支付金额：</span><span style="display: block;float:left;width: 204px">¥' + payPrice + '</span>');
        html.push('</div>');

        html.push('</div>');

        html.push('</div>');
        return html;
    },

    getOrderProductsHtml: function (order) {
        var products = order['TableOrderGoods'];

        var html = [
            '<div style="width: 100%;font-size: 12px;margin-top: 20px;background-color: #fff;overflow: hidden;">',
            '<div style="overflow: hidden">',
            '<table style="width: 100%;border-bottom: 1px solid #eee;border-spacing: 0;border-collapse:collapse;">',
            '<thead>',
            '<tr style="height: 32px;background: #FCFCFC;line-height: 32px;font-weight: 400;color: #333;text-align: center;font-size: 12px;">',
            '<td>商品</td>',
            '<td>商品编号</td>',
            '<td>商品价格</td>',
            '<td>商品数量</td>',
            '</tr>',
            '</thead>',
            '<tbody style="font-size: 12px;color:#333;text-align: center">'];

        for (var i = 0; i < products.length; i++) {
            var product = products[i];
            html.push('<tr style="border-bottom: 1px dashed #eee">');
            html.push('<td style="width: 400px;">');
            html.push('<div style="width: 400px;text-align: left">');
            html.push('<div style="float: left;width: 60px;height: 60px;border: 1px solid #eee;margin: 20px;overflow: hidden">');
            html.push('<div style="display:table-cell;text-align:center;vertical-align:middle;width: 60px;height: 60px;">');
            html.push('<a href=""><img src="' + Resource.image(product['fileName']) + '" style="max-width: 60px;max-height: 60px"/></a>');
            html.push('</div>');
            html.push('</div>');
            html.push('<div style="float: left;width: 300px;margin: 20px 0px">');
            html.push('<span>' + product['goodsName'] + '</span>');
            html.push('</div>');
            html.push('</div>');
            html.push('</td>');
            html.push('<td>');
            html.push('<span style="width: 100px">' + (product['serialNumber'] || '') + '</span>');
            html.push('</td>');
            html.push('<td>¥' + (product['price']) + '</td>');
            html.push('<td>' + (product['amount']) + '</td>');
            html.push('</tr>');
        }

        html.push('</tbody>');
        html.push('</table>');
        html.push('</dib>');
        html.push('<div style="min-height: 150px;color: #666;font-size: 12px;font-weight: 500;padding: 20px;overflow: hidden">');
        html.push('<div style="width: 300px;float: right;height: 28px;line-height: 28px">');
        html.push('<div style="width:170px;float:left;text-align: right">商品总额：</div>');
        html.push('<div style="width: 130px;float:left;text-align: right">¥' + (order['totalGoodsPrice']) + '</div>');
        html.push('<div style="width:170px;float:left;text-align: right">优　　惠：</div>');
        html.push('<div style="width: 130px;float:left;text-align: right">-¥' + (order['discountPrice']) + '</div>');
        html.push('<div style="width:170px;float:left;text-align: right">运　　费：</div>');
        html.push('<div style="width: 130px;float:left;text-align: right">¥' + (order['freightPrice']) + '</div>');
        html.push('<div style="width:170px;float:left;text-align: right;color: #e4393c">订单总额：</div>');
        html.push('<div style="width: 130px;float:left;text-align: right;font-size:18px;line-height: 28px;color: #e4393c">¥' + (order['payPrice']) + '</div>');
        html.push('</div>');
        html.push('</div>');
        html.push('</div>');
        return html;
    },

    reloadOrder: function () {
        this.setValue(this._order['id'], this._orderManager);
    },

    setValue: function (orderId, orderManager) {
        var self = this;
        self._orderManager = orderManager;

        this.apis.Order.getOrder
            .wait('正在加载订单...')
            .call({id: orderId}, function (order) {
                if (order) {
                    self._order = order;
                    var cnt = self.find('order');
                    var str = "";
                    var html = self.getOrderHeadHtml(order);
                    str += html.join("");
                    var html = self.getOrderInfoHtml(order);
                    str += html.join("");
                    var html = self.getOrderProductsHtml(order);
                    str += html.join("");
                    cnt.setHtml(str);

                    orderManager.setButtonShows(order, self);
                }
            });
    }
});
