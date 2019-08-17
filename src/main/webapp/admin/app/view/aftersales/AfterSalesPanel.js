Ext.define('App.aftersales.AfterSalesPanel', {
    extend: 'Ext.form.Panel',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'UXApp.panel.FormDetail'
    ],

    border: false,
    bodyPadding: 10,
    header: false,
    defaultListenerScope: true,
    autoScroll: true,
    bodyStyle: {
        backgroundColor: '#FFF'
    },

    items: [
        {
            xtype: 'formdetail',
            name: 'detail',
            title: '<span style="font-weight: bold;margin-left: 0px">售后申请单详情</span>'
        },
        {
            xtype: 'formdetail',
            hidden: true,
            name: '1_address',
            title: '<span style="font-weight: bold;margin-left: 0px">卖家收货信息</span>'
        },
        {
            xtype: 'panel',
            name: 'images',
            title: '售后图片',
            margin: '20',
            items: []
        },
        {
            xtype: 'formdetail',
            hidden: true,
            name: '2_address',
            title: '<span style="font-weight: bold;margin-left: 0px">买家收货信息</span>'
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
                    text: '返回列表',
                    listeners: {
                        click: 'onBackClick'
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
                    name: 'tips',
                    margin: 'auto 20 auto auto',
                    html: ''
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
                    icon: Resource.png('jet', 'refresh'),
                    text: '刷新售后单',
                    listeners: {
                        click: 'refreshView'
                    }
                },
                {
                    xtype: 'button',
                    name: 'btns_send_back',
                    hidden: true,
                    icon: Resource.png('jet', 'syncPanels'),
                    text: '商品寄回',
                    listeners: {
                        click: 'onJHSPClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'btns_repair_fail',
                    hidden: true,
                    icon: Resource.png('jet', 'testError_dark'),
                    text: '维修失败',
                    listeners: {
                        click: 'onWXSBClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'btns_repair_succ',
                    hidden: true,
                    icon: Resource.png('jet', 'patch_applied_dark'),
                    text: '维修成功',
                    listeners: {
                        click: 'onWXCGClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'btns_new_goods',
                    hidden: true,
                    icon: Resource.png('jet', 'restart'),
                    text: '重新发货',
                    listeners: {
                        click: 'onCXFHClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'btns_create_money',
                    hidden: true,
                    icon: Resource.png('jet', 'copyHovered'),
                    text: '创建退款单',
                    listeners: {
                        click: 'onCJTKDClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'btns_start_repair',
                    hidden: true,
                    icon: Resource.png('jet', 'externalTools_dark'),
                    text: '确认开始维修',
                    listeners: {
                        click: 'onQDKSWXClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'btns_check_pass',
                    hidden: true,
                    icon: Resource.png('jet', 'validator'),
                    text: '确认验货通过',
                    listeners: {
                        click: 'onQDYHTGClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'btns_check_fail',
                    hidden: true,
                    icon: Resource.png('jet', 'balloonWarning'),
                    text: '验货有问题',
                    listeners: {
                        click: 'onQDYHSBClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'btns_get_goods',
                    hidden: true,
                    icon: Resource.png('jet', 'stripUp'),
                    text: '确认已收货',
                    listeners: {
                        click: 'onQRYSHClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'btns_audit_succ',
                    hidden: true,
                    icon: Resource.png('jet', 'testPassed_dark'),
                    text: '审核通过',
                    listeners: {
                        click: 'onSHTGClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'btns_audit_fail',
                    hidden: true,
                    icon: Resource.png('jet', 'popFrame_dark'),
                    text: '审核拒绝',
                    listeners: {
                        click: 'onSHJJClick'
                    }
                },
                '|',
                {
                    xtype: 'button',
                    name: 'btns_cancel',
                    hidden: true,
                    icon: Resource.png('jet', 'cancel'),
                    text: '取消售后单',
                    listeners: {
                        click: 'onQXSHDClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'rollback'),
                    text: '返回售后列表',
                    listeners: {
                        click: 'onBackClick'
                    }
                },
                '->'
            ]
        }
    ],

    onBackClick: function (button, e, eOpts) {
        this.parent.back();
        if (this._callback) {
            this._callback();
        }
    },

    refreshView: function () {
        var self = this;
        this.apis.AfterSales.getAfterSalesById
            .wait(self, '正在重新加载售后单...')
            .call({id: self._data['id']}, function (data) {
                self._data = data;
                self.setValue(data, self._manager);
            })
    },

    // 取消售后申请单
    onQXSHDClick: function () {
        var self = this;
        Dialog.confirm('确定取消售后单', '确定取消售后申请单吗？', function (btn) {
            if (btn == 'yes') {
                self.apis.AfterSales.cancelAfterSales
                    .wait(self, '正在取消售后单...')
                    .call({id: self._data['id']}, function () {
                        self.refreshView();
                    })
            }
        })
    },
    // 商品寄回
    onJHSPClick: function () {

    },
    // 维修失败
    onWXSBClick: function () {
        var self = this;
        Dialog.confirm('确定维修失败', '确定商品维修失败？', function (btn) {
            if (btn == 'yes') {
                self.apis.AfterSales.repairFailure
                    .wait(self, '正在确认维修...')
                    .call({id: self._data['id']}, function () {
                        self.refreshView();
                    })
            }
        })
    },
    // 维修成功
    onWXCGClick: function () {
        var self = this;
        Dialog.confirm('确定维修成功', '确定商品维修成功？', function (btn) {
            if (btn == 'yes') {
                self.apis.AfterSales.repairSuccess
                    .wait(self, '正在确认维修...')
                    .call({id: self._data['id']}, function () {
                        self.refreshView();
                    })
            }
        })
    },
    // 重新发货
    onCXFHClick: function () {
        var self = this;
        var win = Dialog.openWindow('App.aftersales.ReSendGoodsWindow', {
            apis: this.apis,
            _callback: function () {
                self.refreshView();
            }
        });
        win.setValue(this._data);
    },
    // 创建退款单
    onCJTKDClick: function () {
        var self = this;
        this.apis.AfterSales.getRefundMoney
            .wait(this, "正在获取退款价格...")
            .call({id: this._data['id']}, function (prices) {
                var win = Dialog.openWindow('App.aftersales.CreateRefundWindow', {
                    apis: self.apis,
                    _callback: function () {
                        self.refreshView();
                    }
                });
                win.setValue(self._data, prices)
            });
    },
    // 确认开始维修
    onQDKSWXClick: function () {
        var self = this;
        Dialog.confirm('确定开始维修', '确定已验货并开始维修商品？', function (btn) {
            if (btn == 'yes') {
                self.apis.AfterSales.sureStartRepair
                    .wait(self, '正在确认维修...')
                    .call({id: self._data['id']}, function () {
                        self.refreshView();
                    })
            }
        })
    },
    // 确认验货通过
    onQDYHTGClick: function () {
        var self = this;
        Dialog.confirm('验货成功', '确定已验货并且商品没有问题？', function (btn) {
            if (btn == 'yes') {
                self.apis.AfterSales.sureCheckGoodsSuccess
                    .wait(self, '正在设置成功...')
                    .call({id: self._data['id']}, function () {
                        self.refreshView();
                    })
            }
        })
    },

    // 确认验货失败
    onQDYHSBClick: function () {
        var self = this;
        Dialog.confirm('验货失败', '确定已验货并且商品存在问题？', function (btn) {
            if (btn == 'yes') {
                self.apis.AfterSales.sureCheckGoodsFailure
                    .wait(self, '正在设置失败...')
                    .call({id: self._data['id']}, function () {
                        self.refreshView();
                    })
            }
        })
    },
    // 确认已收货
    onQRYSHClick: function () {
        var self = this;
        Dialog.confirm('确定收货', '确定售后单中的商品已经收到吗？</br><span style="color: red">请确保商品已经收到！</span>', function (btn) {
            if (btn == 'yes') {
                self.apis.AfterSales.sureGetGoods
                    .wait(self, '正在确定售后...')
                    .call({id: self._data['id']}, function () {
                        self.refreshView();
                    })
            }
        });
    },
    // 审核拒绝
    onSHJJClick: function () {
        var self = this;
        Dialog.confirm('确定审核拒绝', '确定拒绝审核售后申请单吗？', function (btn) {
            if (btn == 'yes') {
                self.apis.AfterSales.auditRefused
                    .wait(self, '正在拒绝审核...')
                    .call({id: self._data['id']}, function () {
                        self.refreshView();
                    })
            }
        });
    },
    // 审核通过
    onSHTGClick: function () {
        var self = this;
        var win = Dialog.openWindow('App.aftersales.AfterSalesYourAddressWindow', {
            apis: self.apis,
            _callback: function (data) {
                data['id'] = self._data['id'];
                Dialog.confirm('确定审核通过', '确定审核售通过后申请单吗？', function (btn) {
                    if (btn == 'yes') {
                        self.apis.AfterSales.auditPass
                            .wait(self, '正在通过审核...')
                            .call(data, function () {
                                self.refreshView();
                            })
                    }
                })
            }
        });

        win.setAuditBackAddress(self._data['auditBackAddress']);
    },

    resetButtons: function () {
        this._manager.setButtonsShow(this._data, this);
    },

    setValue: function (data, manager) {
        var ds = [];
        this._manager = manager;
        this._data = data;
        this.resetButtons();

        this.find('detail').setTitle('售后申请单详情 - ' + data['number']);
        if (data['number']) ds.push({name: '售后单号', value: data['number']});
        if (data['type']) ds.push({name: '售后类型', value: manager.getTypeName(data['type'])});
        if (data['TableUser']) ds.push({
            name: '申请用户',
            value: data['TableUser']['userName'] + (data['TableUser']['nick'] ? '<span style="margin-left: 20px"></span>' + decodeURIComponent(data['TableUser']['nick']) : '')
        });
        if (data['status']) ds.push({name: '售后状态', value: manager.getStatusName(data['status'])});
        if (data['TableOrder']) ds.push({name: '订单号', value: data['TableOrder']['number']});

        if (data['TableOrderGoods']) {
            var togp = data['TableOrderGoodsProperty'];
            var goodsId = data['TableOrderGoods']['goodsId'];
            var skuId = ['TableOrderGoods']['skuId'];
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
            ds.push({name: '商品名称', value: data['TableOrderGoods']['goodsName']});
            ds.push({name: '商品规格', value: attr.join('，')});
            ds.push({name: '商品编号', value: data['serialNumber'] || ''});
        }

        if (data['amount']) ds.push({name: '商品返还方式', value: manager.getDeliveryName(data['deliveryType'])});
        if (data['deliveryType']) ds.push({name: '售后商品数量', value: data['amount']});
        if (data['hasInvoice']) ds.push({name: '是否有发票', value: data['hasInvoice'] == 1 ? '有' : '无'});
        if (data['hasTestReport']) ds.push({name: '是否有检测报告', value: data['hasTestReport'] == 1 ? '有' : '无'});
        if (data['ip']) ds.push({name: 'IP', value: data['ip']});
        if (data['createTime']) ds.push({name: '创建日期', value: new Date(data['createTime']).format()});
        if (data['detail']) ds.push({name: '问题描述', value: data['detail']});
        if (data['remark']) ds.push({name: '客服备注', value: data['remark']});

        if (data['TableAfterSalesImages']) {
            this.find('images').removeAll();
            for (var i = 0; i < data['TableAfterSalesImages'].length; i++) {
                var img = data['TableAfterSalesImages'][i];
                this.find('images').add({
                    xtype: 'image',
                    height: 200,
                    width: 200,
                    margin: '20 20 0 0',
                    src: Resource.file(img['fileName'])
                });
            }
            var items = this.find('images').items.items;
            if (items) {
                for (var i = 0; i < items.length; i++) {
                    var el = items[i].getEl();
                    el.on('click', function () {
                        window.open(this.getAttribute('src'));
                    })
                }
            }
        }
        if (data['TableAfterSalesAddress']) {
            for (var i = 0; i < data['TableAfterSalesAddress'].length; i++) {
                var addrObj = data['TableAfterSalesAddress'][i];
                var addrHtml = [];
                addrHtml.push({name: '收货人', value: addrObj['realName']});
                addrHtml.push({name: '手机号码', value: addrObj['phoneNumber']});
                addrHtml.push({name: '收货地址', value: addrObj['address']});
                if (addrObj['lgsCompanyCode'])
                    addrHtml.push({name: '物流公司编码', value: addrObj['lgsCompanyCode']});
                if (addrObj['lgsCompanyName'])
                    addrHtml.push({name: '物流公司名称', value: addrObj['lgsCompanyName']});
                if (addrObj['trackingNumber'])
                    addrHtml.push({name: '物流单号', value: addrObj['trackingNumber']});
                if (addrObj['type'] == 1) {
                    this.find('1_address').show();
                    this.find('1_address').setValues(addrHtml);
                }
                if (addrObj['type'] == 2) {
                    this.find('2_address').show();
                    this.find('2_address').setValues(addrHtml);
                }
            }
        }

        this.find('detail').setValues(ds);
        this.find('tips').setHtml('<span style="color: #666666;margin-right: 5px">当前售后单状态</span>' + manager.getStatusName(data['status']));
    }

});
