Ext.define('App.coupon.CouponGoodsPanel', {
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
            xtype: 'gridcolumn',
            dataIndex: 'fileName',
            width: 80,
            text: '图片',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (!value || value == "") {
                    value = Resource.create('/admin/image/exicon/nopic_40.gif');
                } else {
                    value = Resource.image(value);
                }
                return '<div style="height: 40px;width: 40px;vertical-align: middle;display:table-cell;">' +
                    '<img style="max-height:40px;max-width: 40px;vertical-align: middle" src=' + value + '></div> ';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'name',
            width: 300,
            text: '商品名称',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                metaData.style = 'white-space:normal;word-break:break-all;';
                return '<a style="text-decoration: none" href = "">' + Color.string(value, '#19438B') + '</a>';
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'TableGoodsBrand',
            width: 150,
            text: '品牌',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                metaData.style = 'white-space:normal;word-break:break-all;';
                if (value) {
                    return value['name'];
                } else {
                    return '[无]';
                }
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'amount',
            width: 100,
            align: 'center',
            text: '库存数',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                var count = value || 0;
                var str = [];
                str.push("<span style='color: #000;'>");
                str.push(count + ' ' + (record.get('unit') || ''));
                str.push("</span>");
                return str.join("");
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'TableGoodsSku',
            width: 150,
            text: '销售价格',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (value) {
                    if (Ext.isArray(value)) {
                        var min = 0;
                        var max = 0;
                        for (var i in value) {
                            var price = value[i]['price'];
                            if (min == 0) min = price;
                            if (max == 0) max = price;
                            if (min >= price) {
                                min = price;
                            }
                            if (max <= price) {
                                max = price;
                            }
                        }
                        return Color.string('￥', '#999999') + Color.string(PriceUtils.string(min), '#ff0000')
                            + ' - ' +
                            Color.string('￥', '#999999') + Color.string(PriceUtils.string(max), '#ff0000');
                    } else {
                        return Color.string('￥', '#999999') + Color.string(PriceUtils.string(value), '#ff0000');
                    }
                } else {
                    return Color.string('￥', '#999999') + Color.string(PriceUtils.string(record.get('price')), '#ff0000');
                }
            }
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'couponSku',
            width: 180,
            text: '规格&排除',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                var couponSkuId = record.get('couponSkuId');
                var exclude = record.get('exclude');
                if (exclude == 1) {
                    return '<span style="color:#a622ff;font-weight: bold">已排除</span>';
                }
                if (couponSkuId <= 0) {
                    return '<span style="color:#15abff;font-weight: bold">全部规格</span>';
                }
                if (value != null) {
                    return value['propertyNames'];
                }
                return '<span style="color:#aaaaaa;font-weight: bold">[无规格限制]</span>';
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
                    text: '列出优惠券商品',
                    listeners: {
                        click: 'onListClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'add_goods_btn',
                    icon: Resource.png('jet', 'addPackage'),
                    text: '添加优惠券商品',
                    hidden: true,
                    listeners: {
                        click: 'onAddClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'RemoveMulticaret_dark'),
                    text: '移除优惠券商品',
                    listeners: {
                        click: 'onRemoveClick'
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
        this.loadGoods();
    },

    onBackClick: function () {
        this.parent.back();
    },

    onAddClick: function (button, e, eOpts) {
        var self = this;
        if (this._coupon) {
            var win = Dialog.openWindow('App.goods.SelectGoodsWindow', {
                apis: this.apis,
                _callSelectGoods: function (goods) {
                    if (goods && Ext.isArray(goods)) {
                        var couponGoods = [];
                        for (var i = 0; i < goods.length; i++) {
                            var gitem = goods[i];
                            couponGoods.push({
                                gid: gitem['buyGoodsId'],
                                skuId: gitem['buySkuId'] || 0
                            });
                        }

                        self.apis.Coupon.addCouponGoods
                            .wait(self, '正在添加优惠券商品...')
                            .call({id: self._coupon['id'], gids: couponGoods}, function () {
                                self.loadGoods();
                            })
                    }
                }
            });
            win.setSkuMode();
            win.showBaseColumns();

            var store = this.apis.Goods.getGoods.createPageStore();
            win.setValue(store);
            store.load();
        } else {
            Dialog.alert('当前页面没有设置优惠券信息');
        }
    },

    onRemoveClick: function () {
        var self = this;
        if (this._coupon) {
            var datas = this.getIgnoreSelects(arguments);
            if (datas) {
                var gidList = [];
                for (var i = 0; i < datas.length; i++) {
                    var couponSku = datas[i]['couponSku'];
                    gidList.push({gid: datas[i]['id'], skuId: couponSku ? couponSku['id'] : 0});
                }

                Dialog.batch({
                    message: '确定移除当前优惠券商品{d}吗？',
                    data: datas,
                    key: 'name',
                    callback: function (btn) {
                        if (btn == Global.YES) {
                            self.apis.Coupon.removeCouponGoods
                                .wait(self, '正在移除优惠券商品...')
                                .call({id: self._coupon['id'], gids: gidList}, function () {
                                    self.refreshStore();
                                });
                        }
                    }
                });

            } else {
                Dialog.alert('提示', '请先选中优惠券商品后再移除');
            }
        } else {
            Dialog.alert('当前页面没有设置优惠券信息');
        }
    },

    loadGoods: function () {
        var cid = this._coupon['id'];
        var type = this._coupon['type'];
        if (type == 1) { // 商品类型
            this.find('add_goods_btn').show();
        }
        var store = this.apis.Coupon.getCouponGoods.createPageStore({cid: cid});
        this.setStore(store);
        store.load();
    }
});
