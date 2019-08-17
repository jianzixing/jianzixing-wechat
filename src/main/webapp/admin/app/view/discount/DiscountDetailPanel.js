Ext.define('App.discount.DiscountDetailPanel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.form.Panel',
        'Ext.form.RadioGroup',
        'Ext.form.field.Radio',
        'Ext.form.field.ComboBox',
        'Ext.form.field.Date',
        'Ext.form.field.TextArea',
        'BaseApp.datetime.DateTime'
    ],

    layout: 'fit',
    header: false,
    title: '查看优惠活动',
    defaultListenerScope: true,

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'back'),
                    text: '返回活动列表',
                    listeners: {
                        click: 'onBackClick'
                    }
                },
                {
                    xtype: 'button',
                    text: '保存活动修改',
                    icon: Resource.png('jet', 'resetStrip_dark'),
                    listeners: {
                        click: 'onSaveClick'
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
                    html: '<div class="basetitle"> <div class="img"><img border="0" src="/admin/image/icon/base.png"> </div> <div class="text">活动基本信息</div> </div>'
                },
                {
                    xtype: 'textfield',
                    width: 800,
                    name: 'name',
                    fieldLabel: '活动名称',
                    allowBlank: false
                },
                {
                    xtype: 'fieldcontainer',
                    width: 800,
                    name: 'type_radio',
                    fieldLabel: '活动归属'
                },
                {
                    xtype: 'fieldcontainer',
                    width: 800,
                    name: 'enable',
                    fieldLabel: '是否启用'
                },
                {
                    xtype: 'radiogroup',
                    width: 800,
                    fieldLabel: '是否永久有效',
                    items: [
                        {
                            xtype: 'radiofield',
                            name: 'forever',
                            boxLabel: '永久有效',
                            inputValue: '1'
                        },
                        {
                            xtype: 'radiofield',
                            name: 'forever',
                            boxLabel: '有效期范围',
                            checked: true,
                            inputValue: '0'
                        }
                    ],
                    listeners: {
                        change: 'onForeverChange'
                    }
                },
                {
                    xtype: 'datetimefield',
                    format: 'Y-m-d H:i:s',
                    name: 'startTime',
                    width: 800,
                    fieldLabel: '开始时间'
                },
                {
                    xtype: 'datetimefield',
                    format: 'Y-m-d H:i:s',
                    name: 'finishTime',
                    width: 800,
                    fieldLabel: '结束时间'
                },
                {
                    xtype: 'displayfield',
                    name: 'count',
                    width: 800,
                    fieldLabel: '可参与次数',
                    value: ''
                },
                {
                    xtype: 'displayfield',
                    name: 'userLevelsView',
                    width: 800,
                    fieldLabel: '适用会员',
                    value: ''
                },
                {
                    xtype: 'displayfield',
                    name: 'platformsView',
                    width: 800,
                    fieldLabel: '使用平台',
                    value: ''
                },
                {
                    xtype: 'label',
                    margin: '30px auto 20px auto',
                    style: {
                        display: 'block'
                    },
                    html: '<div class="basetitle"> <div class="img"><img border="0" src="/admin/image/icon/base.png"> </div> <div class="text">活动商品配置</div> </div>'
                },
                {
                    xtype: 'fieldcontainer',
                    name: 'list_detail',
                    width: 800,
                    margin: 'auto auto 30px auto',
                    fieldLabel: '',
                    layout: 'auto',
                    items: []
                },
                {
                    xtype: 'label',
                    margin: '0px auto 20px auto',
                    style: {
                        display: 'block'
                    },
                    html: '<div class="basetitle"> <div class="img"><img border="0" src="/admin/image/icon/base.png"> </div> <div class="text">活动参数配置</div> </div>'
                },
                {
                    xtype: 'fieldcontainer',
                    name: 'impl',
                    width: 800,
                    fieldLabel: '活动实现'
                },
                {
                    xtype: 'fieldcontainer',
                    name: 'param_detail',
                    width: 800,
                    fieldLabel: '活动参数'
                },
                {
                    xtype: 'textareafield',
                    name: 'detail',
                    width: 800,
                    height: 80,
                    margin: '10 0 0 0',
                    fieldLabel: '活动描述'
                }
            ]
        }
    ],

    onSaveClick: function (button, e, eOpts) {
        var self = this;
        var form = this.find('form').getForm();

        var dt = form.getValues();
        console.log(dt);

        if (form.isValid()) {
            var dt = form.getValues();
            if (self._data) {
                dt['id'] = self._data['id'];
                Dialog.confirm('确定修改', '确定修改优惠活动吗？', function (btn) {
                    if (btn == 'yes') {
                        self.apis.Discount.updateDiscount
                            .wait(self, '正在更新活动...')
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

    onForeverChange: function (field, newValue) {
        if (newValue['forever'] == 1) {
            this.find('startTime').hide();
            this.find('finishTime').hide();
        } else {
            this.find('startTime').show();
            this.find('finishTime').show();
        }
    },

    setValue: function (data) {
        var id = data['id'];
        var self = this;
        this.apis.Discount.getDiscount
            .wait('正在加载数据...')
            .call({id: id}, function (data) {
                self._data = data;
                if (self._data['startTime']) {
                    self._data['startTime'] = new Date(self._data['startTime']);
                }
                if (self._data['finishTime']) {
                    self._data['finishTime'] = new Date(self._data['finishTime']);
                }

                var form = self.find('form');
                form.getForm().setValues(data);

                if (data['count'] > 0) {
                    self.find('count').setValue('<span style="color: #0f0f0f">可参与' + data['count'] + '次</span>');
                } else {
                    self.find('count').setValue('<span style="color: #999999">不限制次数</span>');
                }

                if (data['userLevels']) {
                    var html = [];
                    for (var k = 0; k < data['userLevels'].length; k++) {
                        html.push('<span style="color: #000;font-weight: bold;margin: auto 5px">' + data['userLevels'][k]['name'] + '</span>')
                    }
                    self.find('userLevelsView').setValue(html.join(''));
                } else {
                    self.find('userLevelsView').setValue('<div style="margin-top: 7px">没有配置适用会员</div>');
                }

                if (data['platforms']) {
                    var html = [];
                    for (var k = 0; k < data['platforms'].length; k++) {
                        html.push('<span style="color: #000;font-weight: bold;margin: auto 5px">' + data['platforms'][k]['name'] + '</span>')
                    }
                    self.find('platformsView').setValue(html.join(''));
                } else {
                    self.find('platformsView').setValue('<div style="margin-top: 7px">没有配置使用平台</div>');
                }

                if (data['type'] == 0) self.find('type_radio').setHtml('<div style="margin-top: 7px">商品分类</div>');
                if (data['type'] == 1) self.find('type_radio').setHtml('<div style="margin-top: 7px">商品</div>');
                if (data['type'] == 2) self.find('type_radio').setHtml('<div style="margin-top: 7px">商品品牌</div>');

                if (data['enable'] == 0) self.find('enable').setHtml('<div style="margin-top: 7px">禁用</div>');
                if (data['enable'] == 1) self.find('enable').setHtml('<div style="margin-top: 7px">启用</div>');
                if (data['enable'] == 3) self.find('enable').setHtml('<div style="margin-top: 7px">已过期(已过有效期时间)</div>');

                if (data['forever'] == 1) {
                    self.find('startTime').hide();
                    self.find('finishTime').hide();
                }

                if (data['implName']) self.find('impl').setHtml(data['implName']);
                if (data['view'] && data['params']) {
                    var params = data['params'];
                    if (typeof params === "string") {
                        params = JSON.parse(data['params']);
                    }
                    var viewStr = data['view'];
                    var view = Ext.create(viewStr, {discountView: this});
                    var html = view.getDetail(params);
                    self.find('param_detail').setHtml(html);
                } else {
                    self.find('param_detail').setHtml('<div style="margin-top: 7px">没有配置活动参数</div>');
                }

                if (data['type'] == 0) {
                    var groups = data['groups'];
                    if (groups) {
                        for (var i = 0; i < groups.length; i++) {
                            self.find('list_detail').add({
                                xtype: 'container',
                                width: 300,
                                html: '<span style="line-height: 32px;font-weight: bold">' + groups[i]['name'] + ' </span>'
                            })
                        }
                        self.find('list_detail').setFieldLabel('商品分类配置');
                    }
                }
                if (data['type'] == 1) {
                    var goods = data['goods'];
                    if (goods) {
                        for (var i = 0; i < goods.length; i++) {
                            var dt = goods[i];
                            var imgUrl = Resource.image(dt['fileName']);
                            if (!dt['fileName']) imgUrl = Resource.create('/admin/image/exicon/nopic_40.gif');
                            var img = '<div style="height: 40px;width: 40px;vertical-align: middle;display:table-cell;">' +
                                '<img style="max-height:40px;max-width: 40px;vertical-align: middle" src=' + imgUrl + '></div> ';

                            self.find('list_detail').add({
                                xtype: 'container',
                                width: 800,
                                items: [
                                    {
                                        xtype: 'container',
                                        width: 40,
                                        html: img
                                    },
                                    {
                                        xtype: 'container',
                                        width: 300,
                                        margin: 'auto auto auto 20px',
                                        html: '<span style="/*line-height: 32px*/;color:#19438B">' + dt['name'] + '</span>'
                                    }
                                ]
                            })
                        }
                        self.find('list_detail').setFieldLabel('商品列表');
                    }
                }
                if (data['type'] == 2) {
                    var brands = data['brands'];
                    if (brands) {
                        for (var i = 0; i < brands.length; i++) {
                            self.find('list_detail').add({
                                xtype: 'container',
                                width: 300,
                                html: '<span style="line-height: 32px;font-weight: bold">' + brands[i]['name'] + ' </span>'
                            })
                        }
                        self.find('list_detail').setFieldLabel('商品品牌配置');
                        if (brands.length >= 10) {
                            self.find('list_detail').add({
                                xtype: 'button',
                                text: '查看更多商品',
                                listeners: {
                                    click: function () {
                                        var win = Dialog.openWindow('App.discount.DiscountDetailGoodsWindow', {
                                            apis: self.apis
                                        });
                                        var store = self.apis.Discount.getSimpleDiscountGoods.createPageStore();
                                        win.setStore(store);
                                    }
                                }
                            })
                        }
                    }
                }
            });
    }

});