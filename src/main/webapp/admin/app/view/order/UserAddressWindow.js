Ext.define('App.order.UserAddressWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.form.Panel',
        'Ext.form.field.ComboBox',
        'Ext.form.RadioGroup',
        'Ext.form.field.Radio',
        'Ext.form.field.TextArea',
        'UXApp.field.GridComboBox'
    ],

    height: 570,
    width: 700,
    layout: 'fit',
    title: '添加收货地址',
    defaultListenerScope: true,

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            items: [
                '->',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'saveTempConfig'),
                    text: '确定保存',
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
    items: [
        {
            xtype: 'form',
            name: 'form',
            border: false,
            bodyPadding: 10,
            header: false,
            items: [
                {
                    xtype: 'gridcombobox',
                    name: 'userId',
                    anchor: '100%',
                    hideTrigger: true,
                    showPagingView: true,
                    fieldLabel: '所属用户',
                    treePanelConfig: {
                        forceFit: true,
                        columns: [
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'userName',
                                text: '用户名'
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'nick',
                                text: '昵称',
                                renderer: function (v) {
                                    if (v) {
                                        try {
                                            return decodeURIComponent(v);
                                        } catch (e) {
                                            return v;
                                        }
                                    }
                                    return '';
                                }
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'email',
                                text: '邮箱'
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'phone',
                                text: '手机号'
                            }
                        ]
                    },
                    searchQuery: true,
                    searchQueryField: 'keyword',
                    displayField: 'selectName',
                    valueField: 'id',
                    allowBlank: false,
                    gridDisplayField: function (data) {
                        var name = data['userName'];
                        if (data['nick']) {
                            name += " (" + decodeURIComponent(data['nick']) + ")";
                        }
                        return name;
                    }
                },
                {
                    xtype: 'textfield',
                    name: 'realName',
                    anchor: '100%',
                    fieldLabel: '收货人姓名',
                    allowBlank: false
                },
                {
                    xtype: 'fieldcontainer',
                    layout: 'column',
                    fieldLabel: '所在地区',
                    items: [
                        {
                            xtype: 'combobox',
                            name: 'provinceCode',
                            width: '32%',
                            displayField: 'name',
                            valueField: 'code',
                            hideLabel: true,
                            allowBlank: false,
                            editable: false,
                            listeners: {
                                change: 'onProvinceChange'
                            }
                        },
                        {
                            xtype: 'combobox',
                            name: 'cityCode',
                            width: '32%',
                            displayField: 'name',
                            valueField: 'code',
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
                            name: 'countyCode',
                            displayField: 'name',
                            valueField: 'code',
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
                    name: 'address',
                    anchor: '100%',
                    fieldLabel: '详细地址',
                    allowBlank: false
                },
                {
                    xtype: 'textfield',
                    name: 'phoneNumber',
                    anchor: '100%',
                    fieldLabel: '手机号码'
                },
                {
                    xtype: 'textfield',
                    name: 'telNumber',
                    anchor: '100%',
                    fieldLabel: '固定电话'
                },
                {
                    xtype: 'textfield',
                    name: 'email',
                    anchor: '100%',
                    fieldLabel: '电子邮箱',
                    vtype: 'email'
                },
                {
                    xtype: 'radiogroup',
                    fieldLabel: '是否默认地址',
                    items: [
                        {
                            xtype: 'radiofield',
                            name: 'isDefault',
                            boxLabel: '默认地址',
                            inputValue: '1'
                        },
                        {
                            xtype: 'radiofield',
                            name: 'isDefault',
                            boxLabel: '否',
                            checked: true,
                            inputValue: '0'
                        }
                    ]
                },
                {
                    xtype: 'textfield',
                    name: 'postcode',
                    anchor: '100%',
                    fieldLabel: '邮编地址'
                },
                {
                    xtype: 'textareafield',
                    name: 'detail',
                    anchor: '100%',
                    height: 80,
                    fieldLabel: '备注'
                }
            ]
        }
    ],
    onProvinceChange: function (field, newValue, oldValue, eOpts) {
        var city = this.find('cityCode');
        var county = this.find('countyCode');
        city.setValue(null);
        county.setValue(null);
        var selectRecord = field.getSelection();
        var provinceCode = selectRecord.get('code');
        var store = this.apis.Area.getCity.createListStore({provinceCode: provinceCode});
        city.setStore(store);
        store.load();

        if (this._data && provinceCode === this._data['provinceCode']) {
            city.setValue(this._data['cityCode']);
        }
    },
    onCityChange: function (field, newValue, oldValue, eOpts) {
        var county = this.find('countyCode');
        county.setValue(null);
        var selectRecord = field.getSelection();
        if (selectRecord) {
            var cityCode = selectRecord.get('code');
            var store = this.apis.Area.getArea.createListStore({cityCode: cityCode});
            county.setStore(store);
            store.load();
            if (this._data && cityCode === this._data['cityCode']) {
                county.setValue(this._data['countyCode']);
            }
        }
    },

    onSaveClick: function (button, e, eOpts) {
        var form = this.find('form').getForm();
        var self = this;
        if (form.isValid()) {
            var data = form.getValues();
            if (this._data) {
                data['id'] = this._data['id'];
                self.apis.UserAddress.updateUserAddress
                    .wait(self, '正在保存收货地址...')
                    .call({object: data}, function () {
                        if (self._callback) {
                            self._callback();
                        }
                        self.close();
                    });
            } else {
                self.apis.UserAddress.addUserAddress
                    .wait(self, '正在保存收货地址...')
                    .call({object: data}, function () {
                        if (self._callback) {
                            self._callback();
                        }
                        self.close();
                    });
            }
        }
    },

    onCancelClick: function (button, e, eOpts) {
        this.close(0);
    },

    onInitWindow: function () {
        var user = this.find('userId');
        var store = this.apis.User.getOrderUsers.createPageStore();
        user.setGridStore(store);
        store.load();

        var province = this.find('provinceCode');
        var city = this.find('cityCode');
        var county = this.find('countyCode');
        province.setValue(null);
        city.setValue(null);
        county.setValue(null);
        var store = this.apis.Area.getProvince.createListStore();
        province.setStore(store);
        store.load();
    },

    setValue: function (value) {
        this._data = value;
        this.setTitle('修改收货地址');
        var form = this.find('form').getForm();
        var user = value['TableUser'];
        user['selectName'] = user['userName'] + (user['nick'] ? " ( " + decodeURIComponent(user['nick']) + " ) " : "");
        var store = Ext.create('Ext.data.Store', {data: [user]});
        this.find('userId').setStore(store);
        form.setValues(value);
    }

});