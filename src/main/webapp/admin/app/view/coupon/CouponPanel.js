Ext.define('App.coupon.CouponPanel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.form.Panel',
        'Ext.form.RadioGroup',
        'Ext.form.field.Radio',
        'Ext.form.field.ComboBox',
        'Ext.form.field.Date',
        'Ext.form.field.TextArea'
    ],

    layout: 'fit',
    header: false,
    title: '添加优惠券',
    defaultListenerScope: true,

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
                    text: '重新填写',
                    icon: Resource.png('jet', 'resetStrip_dark'),
                    listeners: {
                        click: 'onReloadClick'
                    }
                }
            ]
        }
    ],
    items: [
        {
            xtype: 'form',
            name: 'form',
            border: false,
            bodyPadding: 30,
            header: false,
            autoScroll: true,
            items: [
                {
                    xtype: 'label',
                    margin: '0px auto 20px auto',
                    style: {
                        display: 'block'
                    },
                    html: '<div class="basetitle"> <div class="img"><img border="0" src="/admin/image/icon/base.png"> </div> <div class="text">优惠券基本信息</div> </div>'
                },
                {
                    xtype: 'textfield',
                    width: 800,
                    name: 'name',
                    fieldLabel: '优惠券名称',
                    allowBlank: false
                },
                {
                    xtype: 'radiogroup',
                    width: 800,
                    fieldLabel: '是否启用',
                    items: [
                        {
                            xtype: 'radiofield',
                            name: 'status',
                            boxLabel: '启用',
                            inputValue: '1'
                        },
                        {
                            xtype: 'radiofield',
                            name: 'status',
                            boxLabel: '禁用',
                            checked: true,
                            inputValue: '0'
                        }
                    ],
                    allowBlank: false
                },
                {
                    xtype: 'radiogroup',
                    width: 800,
                    fieldLabel: '领取渠道',
                    items: [
                        {
                            xtype: 'radiofield',
                            name: 'channel',
                            boxLabel: '网站领取',
                            checked: true,
                            inputValue: '1'
                        },
                        {
                            xtype: 'radiofield',
                            name: 'channel',
                            boxLabel: '其他渠道',
                            inputValue: '0'
                        }
                    ],
                    allowBlank: false
                },
                {
                    xtype: 'displayfield',
                    height: 55,
                    width: 800,
                    value: '<span style="color:#999999">必须设置[开始时间]和[结束时间]。<span style="color: #0f0f0f">一旦优惠券建立后只能修改标题、描述和优惠券时间</span>。可参与次数如果不填或者填写小于等于0的数字' +
                        '都视为不限制次数，只有大于0才能生效。</span>',
                    fieldLabel: '<span style="color:#ff0000">优惠券配置说明</span>'
                },
                {
                    xtype: 'datetimefield',
                    format: 'Y-m-d H:i:s',
                    name: 'startTime',
                    width: 800,
                    fieldLabel: '开始时间',
                    allowBlank: false
                },
                {
                    xtype: 'datetimefield',
                    format: 'Y-m-d H:i:s',
                    name: 'finishTime',
                    width: 800,
                    fieldLabel: '结束时间',
                    allowBlank: false
                },
                {
                    xtype: 'checkboxgroup',
                    name: 'user_level_view',
                    width: 800,
                    fieldLabel: '适用会员',
                    items: []
                },
                {
                    xtype: 'label',
                    margin: '20px auto 20px auto',
                    style: {
                        display: 'block'
                    },
                    html: '<div class="basetitle"> <div class="img"><img border="0" src="/admin/image/icon/base.png"> </div> <div class="text">优惠券参数配置</div> </div>'
                },
                {
                    xtype: 'radiogroup',
                    width: 800,
                    hidden: true,
                    fieldLabel: '是否可叠加',
                    items: [
                        {
                            xtype: 'radiofield',
                            name: 'overlay',
                            boxLabel: '可叠加',
                            inputValue: '1'
                        },
                        {
                            xtype: 'radiofield',
                            name: 'overlay',
                            checked: true,
                            boxLabel: '不可叠加',
                            inputValue: '0'
                        }
                    ],
                    allowBlank: false
                },
                {
                    xtype: 'fieldcontainer',
                    fieldLabel: '使用门槛',
                    width: 800,
                    layout: {
                        type: 'hbox',
                        align: 'middle'
                    },
                    items: [
                        {
                            xtype: 'label',
                            text: '满'
                        },
                        {
                            xtype: 'numberfield',
                            name: 'orderPrice',
                            width: 300,
                            allowBlank: false,
                            margin: '0 10',
                            hideLabel: true
                        },
                        {
                            xtype: 'label',
                            text: '元'
                        }
                    ]
                },
                {
                    xtype: 'numberfield',
                    name: 'couponPrice',
                    width: 800,
                    allowBlank: false,
                    fieldLabel: '优惠金额(元)'
                },
                {
                    xtype: 'numberfield',
                    name: 'amount',
                    width: 800,
                    allowBlank: false,
                    fieldLabel: '发行量'
                },
                {
                    xtype: 'numberfield',
                    name: 'count',
                    width: 800,
                    fieldLabel: '每人限领(张)',
                    allowBlank: false,
                    value: 1
                },
                {
                    xtype: 'textareafield',
                    name: 'detail',
                    width: 800,
                    height: 80,
                    fieldLabel: '优惠券描述'
                },
                {
                    xtype: 'fieldcontainer',
                    margin: '30px 0 0 0',
                    items: [
                        {
                            xtype: 'button',
                            text: '保存优惠券',
                            listeners: {
                                click: 'onSaveClick'
                            }
                        },
                        {
                            xtype: 'button',
                            margin: 'auto auto auto 20px',
                            text: '返回列表',
                            listeners: {
                                click: 'onBackClick'
                            }
                        }
                    ]
                }
            ]
        }
    ],

    onSaveClick: function (button, e, eOpts) {
        var self = this;
        var form = this.find('form').getForm();

        var dt = form.getValues();
        if (!Ext.isArray(dt['userLevels'])) {
            dt['userLevels'] = [dt['userLevels']]
        }
        console.log(dt);

        if (form.isValid()) {
            var dt = form.getValues();
            if (self._data) {
                dt['id'] = self._data['id'];
                Dialog.confirm('确定修改', '确定修改优惠券吗？', function (btn) {
                    if (btn == 'yes') {
                        self.apis.Coupon.updateCoupon
                            .wait(self, '正在更新优惠券...')
                            .call({object: dt}, function () {
                                self.onBackClick();
                                if (self._callback) {
                                    self._callback();
                                }
                            })
                    }
                });

            } else {
                Dialog.confirm('确定添加', '确定添加优惠券吗？<span style="color: red">注意：优惠券添加后主要信息不可以更改！</span>', function (btn) {
                    if (btn == 'yes') {
                        self.apis.Coupon.addCoupon
                            .wait(self, '正在添加优惠券...')
                            .call({object: dt}, function () {
                                self.onBackClick();
                                if (self._callback) {
                                    self._callback();
                                }
                            });
                    }
                });
            }
        }
    },

    onBackClick: function () {
        this.parent.back();
    },

    onReloadClick: function () {
        this.parent.redraw();
    },

    initWindow: function () {
        var self = this;
        this.apis.Coupon.getCouponInit
            .wait(this, '正在加载初始化数据...')
            .call({}, function (data) {
                var userLevelView = self.find('user_level_view');
                userLevelView.removeAll();
                if (data['levels']) {
                    for (var i = 0; i < data['levels'].length; i++) {
                        var obj = {
                            xtype: 'checkboxfield',
                            name: 'userLevels',
                            boxLabel: data['levels'][i]['name'],
                            inputValue: data['levels'][i]['id'],
                            listeners: {
                                change: function (me, newValue, oldValue) {
                                    if (me.inputValue + "" == "0") {
                                        if (newValue == 1) {
                                            var items = me.ownerCt.items.items;
                                            for (var k = 0; k < items.length; k++) {
                                                if (items[k].inputValue + "" != "0") {
                                                    items[k].setValue(true);
                                                }
                                            }
                                        } else {
                                            var items = me.ownerCt.items.items;
                                            for (var k = 0; k < items.length; k++) {
                                                if (items[k].inputValue + "" != "0") {
                                                    items[k].setValue(false);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        };
                        if (self._data) {
                            if (self._data['TableCouponUserLevel']) {
                                for (var i = 0; i < self._data['TableCouponUserLevel'].length; i++) {
                                    if (self._data['TableCouponUserLevel'][i]['ulid'] == data['levels'][i]['id']) {
                                        obj['checked'] = true;
                                    }
                                }
                            }
                        }
                        userLevelView.add(obj);
                    }
                }
            });
    },

    setValue: function (data) {
        this._data = data;
        var form = this.find('form').getForm();
        data['startTime'] = new Date(data['startTime']);
        data['finishTime'] = new Date(data['finishTime']);
        form.setValues(data);
        var userLevels = [];
        if (data['TableCouponUserLevel']) {
            for (var i = 0; i < data['TableCouponUserLevel'].length; i++) {
                userLevels.push(data['TableCouponUserLevel'][i]['ulid'])
            }
        }
        this.find('user_level_view').setValue({userLevels: userLevels});
    }
});
