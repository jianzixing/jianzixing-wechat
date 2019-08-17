Ext.define('App.logistics.LogisticsByWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.field.ComboBox',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    autoShow: true,
    height: 330,
    width: 400,
    title: '添加包邮条件',
    layout: 'fit',
    defaultListenerScope: true,

    items: [
        {
            xtype: 'form',
            name: 'form',
            border: false,
            bodyPadding: 10,
            header: false,
            items: [
                {
                    xtype: 'textfield',
                    fieldLabel: '地区',
                    anchor: '100%',
                    name: 'area',
                    listeners: {
                        focusenter: 'chooseArea'
                    }
                },
                {
                    xtype: 'combobox',
                    fieldLabel: '运送方式',
                    anchor: '100%',
                    name: 'deliveryType',
                    displayField: 'name',
                    valueField: 'value',
                    editable: false,
                    allowBlank: false
                },
                {
                    xtype: 'combobox',
                    fieldLabel: '包邮条件',
                    anchor: '100%',
                    name: 'condition',
                    displayField: 'name',
                    valueField: 'value',
                    editable: false,
                    allowBlank: false,
                    listeners: {
                        change: 'conditionChange'
                    }
                },
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    fieldLabel: '件数小于(件)',
                    name: 'value1'
                },
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    fieldLabel: '满（元）',
                    name: 'value2'
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
                    text: '确定',
                    listeners: {
                        click: 'addByParam'
                    }
                },
                {
                    xtype: 'button',
                    text: '关闭',
                    listeners: {
                        click: 'winClose'
                    }
                }
            ]
        }
    ],
    chooseArea: function () {
        var self = this;
        var win = Dialog.openWindow('App.logistics.LogisticsAreaWindow', {apis: this.apis});
        win.loadData();
        win.setSelectCallback(function (data) {
            var gp = self.find('area');
            gp.setValue(data.name);
            self.code = data.code;
        });

    },
    setValue: function (data) {
        var type = data.type;
        this.type = type;
        var value1 = this.find('value1');
        var store;
        if (parseInt(type) == 11) { //按件数
            store = Ext.create('Ext.data.Store', {
                data: [
                    {name: '件数', value: 1},
                    {name: '金额', value: 2},
                    {name: '件数+金额', value: 3}
                ]
            });
            value1.setFieldLabel('满(件)');
        } else if (parseInt(type) == 12) { //按重量
            store = Ext.create('Ext.data.Store', {
                data: [
                    {name: '重量', value: 1},
                    {name: '金额', value: 2},
                    {name: '重量+金额', value: 3}
                ]
            });
            value1.setFieldLabel('满(kg)');
        } else if (parseInt(type) == 13) { //按体积
            store = Ext.create('Ext.data.Store', {
                data: [
                    {name: '体积', value: 1},
                    {name: '金额', value: 2},
                    {name: '体积+金额', value: 3}
                ]
            });
            value1.setFieldLabel('满(m3)');
        }
        this.find('condition').setStore(store);

        var deliveryType = data.deliveryType; //10快递，11EMS，12平邮
        var deliveryData = [];
        for (var i = 0; i < deliveryType.length; i++) {
            if (deliveryType[i] == 10) {
                deliveryData.push({
                    name: '快递',
                    value: 10
                });
            } else if (deliveryType[i] == 11) {
                deliveryData.push({
                    name: 'EMS',
                    value: 11
                });
            } else if (deliveryType[i] == 12) {
                deliveryData.push({
                    name: '平邮',
                    value: 12
                });
            }
        }
        var deliveryTypeStore = Ext.create('Ext.data.Store', {
            data: deliveryData
        });
        this.find('deliveryType').setStore(deliveryTypeStore);
    },
    conditionChange: function (e, newValue, oldValue, eOpts) {
        var type = this.type;
        var value1 = this.find('value1');
        var value2 = this.find('value2');
        if (newValue == 1) {
            value1.setVisible(true);
            value2.setVisible(false);
        } else if (newValue == 2) {
            value1.setVisible(false);
            value2.setVisible(true)
        } else if (newValue == 3) {
            value1.setVisible(true);
            value2.setVisible(true);
        }
    },
    winClose: function () {
        this.close();
    },
    addByParam: function () {
        if (!this.code) {
            Dialog.alert('提示', '请选择选择地区');
            return false;
        }
        var code = this.code;
        var areaName = this.find('area').getValue();
        var deliveryType = this.find('deliveryType').getValue();
        if (!deliveryType) {
            Dialog.alert('提示', '请选择运送方式');
            return false;
        }
        var condition = this.find('condition').getValue();
        if (!condition) {
            Dialog.alert('提示', '请选择包邮条件');
            return false;
        }
        var that = this;
        var value1 = this.find('value1').getValue();
        var value2 = this.find('value2').getValue();
        var data = {
            code: code,
            areaName: areaName,
            condition: condition,
            freeRule: this.type,
            deliveryType: deliveryType,
            deliveryTypeDesc: that.find('deliveryType').getDisplayValue(),
            conditionDesc: that.find('condition').getDisplayValue(),
            value1: value1,
            value2: value2
        };
        if (this.callback) {
            this.callback(data);
            this.close();
        }
    },
    setCallback: function (fun) {
        this.callback = fun;
    }

});