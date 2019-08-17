Ext.define('App.coupon.CouponDetail', {
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
                    xtype: 'displayfield',
                    name: 'status',
                    width: 800,
                    fieldLabel: '优惠券状态',
                    value: '[未知]'
                },
                {
                    xtype: 'displayfield',
                    name: 'channel',
                    width: 800,
                    fieldLabel: '领取渠道',
                    value: '[未知]'
                },
                {
                    xtype: 'displayfield',
                    name: 'startTime',
                    width: 800,
                    fieldLabel: '开始时间',
                    value: '[未知]'
                },
                {
                    xtype: 'displayfield',
                    name: 'finishTime',
                    width: 800,
                    fieldLabel: '结束时间',
                    value: '[未知]'
                },
                {
                    xtype: 'displayfield',
                    name: 'user_level_view',
                    width: 800,
                    fieldLabel: '适用会员',
                    value: '[未知]'
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
                    xtype: 'displayfield',
                    name: 'overlay',
                    width: 800,
                    fieldLabel: '是否可叠加',
                    value: '[未知]'
                },
                {
                    xtype: 'displayfield',
                    name: 'orderPrice',
                    width: 800,
                    fieldLabel: '使用门槛',
                    value: '[未知]'
                },
                {
                    xtype: 'displayfield',
                    name: 'couponPrice',
                    width: 800,
                    fieldLabel: '优惠金额(元)',
                    value: '[未知]'
                },
                {
                    xtype: 'displayfield',
                    name: 'amount',
                    width: 800,
                    fieldLabel: '发行量',
                    value: '[未知]'
                },
                {
                    xtype: 'displayfield',
                    name: 'count',
                    width: 800,
                    fieldLabel: '每人限领(张)',
                    value: '[未知]'
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
                        userLevelView.add({
                            xtype: 'checkboxfield',
                            name: 'userLevels',
                            boxLabel: data['levels'][i]['name'],
                            inputValue: data['levels'][i]['id']
                        });
                    }
                }
            });
    },

    setValue: function (data) {
        this._data = data;
        if ((data['status']) == 0) this.find('status').setValue('未启用');
        if ((data['status']) == 1) this.find('status').setValue('未开始');
        if ((data['status']) == 2) this.find('status').setValue('获取中');
        if ((data['status']) == 3) this.find('status').setValue('已结束');

        if ((data['channel']) == 0) this.find('channel').setValue('其他渠道');
        if ((data['channel']) == 1) this.find('channel').setValue('网站获取');

        this.find('startTime').setValue(new Date(data['startTime']).format());
        this.find('finishTime').setValue(new Date(data['finishTime']).format());

        if ((data['overlay']) == 0) this.find('overlay').setValue('不可叠加');
        if ((data['overlay']) == 1) this.find('overlay').setValue('可叠加');

        this.find('orderPrice').setValue(data['orderPrice'] + '元');
        this.find('couponPrice').setValue(data['couponPrice'] + '元');
        this.find('amount').setValue(data['amount'] + '张');
        this.find('count').setValue(data['count'] + '张');
        this.find('detail').setValue(data['detail']);
        this.find('name').setValue(data['name']);

        if (data['TableCouponUserLevel']) {
            var levels = data['TableCouponUserLevel'];
            var str = [];
            for (var i = 0; i < levels.length; i++) {
                var level = levels[i]['TableUserLevel'];
                if (level) {
                    str.push(level['name']);
                }
            }
            this.find('user_level_view').setValue(str.join("，"));
        }
    }
});