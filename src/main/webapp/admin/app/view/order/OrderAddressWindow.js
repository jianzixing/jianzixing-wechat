Ext.define('App.order.OrderAddressWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.FieldContainer',
        'Ext.form.field.ComboBox',
        'Ext.button.Button',
        'Ext.toolbar.Toolbar'
    ],

    height: 500,
    width: 700,
    layout: 'fit',
    title: '修改收货地址',
    defaultListenerScope: true,

    items: [
        {
            xtype: 'form',
            name: 'form',
            border: false,
            bodyPadding: 10,
            header: false,
            autoScroll: true,
            items: [
                {
                    xtype: 'fieldcontainer',
                    name: 'detail',
                    fieldLabel: '账号信息'
                },
                {
                    xtype: 'radiogroup',
                    fieldLabel: '地址来源',
                    items: [
                        {
                            xtype: 'radiofield',
                            name: 'source',
                            boxLabel: '配送地址列表',
                            checked: true,
                            inputValue: '1'
                        },
                        {
                            xtype: 'radiofield',
                            name: 'source',
                            boxLabel: '修改配送地址',
                            inputValue: '2'
                        }
                    ],
                    listeners: {
                        change: 'onRadiogroupChange'
                    }
                },
                {
                    xtype: 'combobox',
                    name: 'aid',
                    editable: false,
                    anchor: '100%',
                    fieldLabel: '配送地址',
                    displayField: 'selectName',
                    valueField: 'id'
                },
                {
                    xtype: 'textfield',
                    hidden: true,
                    name: 'realName',
                    anchor: '100%',
                    fieldLabel: '收货人姓名',
                    allowBlank: false
                },
                {
                    xtype: 'textfield',
                    hidden: true,
                    name: 'phoneNumber',
                    anchor: '100%',
                    fieldLabel: '手机号码'
                },
                {
                    xtype: 'textfield',
                    hidden: true,
                    name: 'telNumber',
                    anchor: '100%',
                    fieldLabel: '固定电话'
                },
                {
                    xtype: 'fieldcontainer',
                    name: 'addressArea',
                    hidden: true,
                    layout: 'column',
                    fieldLabel: '所在地区',
                    items: [
                        {
                            xtype: 'combobox',
                            name: 'province',
                            width: '32%',
                            displayField: 'name',
                            valueField: 'name',
                            hideLabel: true,
                            allowBlank: false,
                            editable: false,
                            listeners: {
                                change: 'onProvinceChange'
                            }
                        },
                        {
                            xtype: 'combobox',
                            name: 'city',
                            width: '32%',
                            displayField: 'name',
                            valueField: 'name',
                            style: {marginLeft: '2%'},
                            hideLabel: true,
                            allowBlank: false,
                            editable: false,
                            listeners: {
                                change: 'onCityChange'
                            }
                        },
                        {
                            xtype: 'combobox',
                            name: 'county',
                            displayField: 'name',
                            valueField: 'name',
                            style: {marginLeft: '2%'},
                            width: '32%',
                            hideLabel: true,
                            allowBlank: false,
                            editable: false
                        }
                    ]
                },
                {
                    xtype: 'textfield',
                    hidden: true,
                    name: 'address',
                    anchor: '100%',
                    fieldLabel: '详细地址',
                    allowBlank: false
                },
                {
                    xtype: 'textfield',
                    hidden: true,
                    name: 'email',
                    anchor: '100%',
                    fieldLabel: '电子邮箱',
                    vtype: 'email'
                },
                {
                    xtype: 'textfield',
                    hidden: true,
                    name: 'postcode',
                    anchor: '100%',
                    fieldLabel: '邮编地址'
                }
            ]
        }
    ],
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            items: [
                '->',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'saveTempConfig'),
                    text: '确定修改',
                    listeners: {
                        click: 'onSaveClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'cancel'),
                    text: '取消关闭',
                    listeners: {
                        click: 'onCancelClick'
                    }
                },
                '->'
            ]
        }
    ],

    onSaveClick: function (button, e, eOpts) {
        var form = this.find('form').getForm();
        if (this._order) {
            var data = form.getValues();
            data['orderId'] = this._order['id'];
            data['country'] = '中国';
            var self = this;
            this.apis.Order.updateOrderAddress
                .wait(self, '正在修改订单地址...')
                .call({object: data}, function () {
                    self.close();
                    if (self._callback) {
                        self._callback();
                    }
                })
        }
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    onRadiogroupChange: function (field, newValue, oldValue, eOpts) {
        if (newValue['source'] == 1) {
            this.find('aid').show();
            this.find('realName').hide();
            this.find('phoneNumber').hide();
            this.find('telNumber').hide();
            this.find('addressArea').hide();
            this.find('address').hide();
            this.find('email').hide();
            this.find('postcode').hide();
        } else {
            this.find('aid').hide();
            this.find('realName').show();
            this.find('phoneNumber').show();
            this.find('telNumber').show();
            this.find('addressArea').show();
            this.find('address').show();
            this.find('email').show();
            this.find('postcode').show();
        }
    },

    onProvinceChange: function (field, newValue, oldValue, eOpts) {
        var selectRecord = field.getSelection();
        if (selectRecord && selectRecord.get('code')) {
            var city = this.find('city');
            var county = this.find('county');
            city.setValue(null);
            county.setValue(null);

            var store = this.apis.Area.getCity.createListStore({provinceCode: selectRecord.get('code')});
            city.setStore(store);
            store.load();
            if (this._order) {
                city.setValue(this._order['TableOrderAddress'] ? this._order['TableOrderAddress']['city'] : '');
            }
        }
    },
    onCityChange: function (field, newValue, oldValue, eOpts) {
        var selectRecord = field.getSelection();
        if (selectRecord && selectRecord.get('code')) {
            var county = this.find('county');
            county.setValue(null);
            var store = this.apis.Area.getArea.createListStore({cityCode: selectRecord.get('code')});
            county.setStore(store);
            store.load();
            if (this._order) {
                county.setValue(this._order['TableOrderAddress'] ? this._order['TableOrderAddress']['county'] : '');
            }
        }
    },

    setValue: function (order) {
        if (order) {
            this._order = order;
            var number = order['number'];
            this.setTitle('修改收货地址 - ' + number);

            var user = order['TableUser'];
            var detailView = this.find('detail');
            detailView.setHtml('购买账号 ' + user['userName'] + (user['nick'] ? " (" + decodeURIComponent(user['nick']) + ")" : ''));

            var address = this.find('aid');
            var store = this.apis.UserAddress.getUserAddressByUserUid.createListStore({uid: order['userId']});
            address.setStore(store);
            store.load();

            var store = this.apis.Area.getProvince.createListStore();
            this.find('province').setStore(store);
            store.load();

            var addressObject = order['TableOrderAddress'];
            if (addressObject) {
                this.find('realName').setValue(addressObject['realName']);
                this.find('phoneNumber').setValue(addressObject['phoneNumber']);
                this.find('telNumber').setValue(addressObject['telNumber']);
                this.find('address').setValue(addressObject['address']);
                this.find('email').setValue(addressObject['email']);
                this.find('postcode').setValue(addressObject['postcode']);

                this.find('province').setValue(addressObject['province']);

                // var pd = {name: addressObject['city']};
                // this.find('city').setStore(Ext.create('Ext.data.Store', {data: [pd]}));
                // this.find('city').setValue(addressObject['city']);
                //
                // var pd = {name: addressObject['county']};
                // this.find('county').setStore(Ext.create('Ext.data.Store', {data: [pd]}));
                // this.find('county').setValue(addressObject['county']);
            }
        }
    }

});