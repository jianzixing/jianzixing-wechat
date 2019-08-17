Ext.define('App.logistics.LogisticsTemplateWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.Text',
        'Ext.form.RadioGroup',
        'Ext.form.field.Radio',
        'Ext.grid.Panel',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.view.Table',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.selection.CheckboxModel',
        'Ext.grid.column.Widget'
    ],

    height: 600,
    width: 950,
    layout: 'fit',
    title: '运费模板',
    defaultListenerScope: true,

    items: [
        {
            xtype: 'form',
            border: false,
            scrollable: 'y',
            bodyPadding: 10,
            header: false,
            name: 'templateForm',
            items: [
                {
                    xtype: 'textfield',
                    anchor: '100%',
                    fieldLabel: '模板名称',
                    allowBlank: false,
                    blankText: '模板名称不能为空',
                    name: 'name'
                },
                {
                    xtype: 'radiogroup',
                    fieldLabel: '是否包邮',
                    name: 'free_group',
                    items: [
                        {
                            xtype: 'radiofield',
                            name: 'free',
                            boxLabel: '自定义运费',
                            inputValue: '0',
                            checked: true
                        },
                        {
                            xtype: 'radiofield',
                            name: 'free',
                            boxLabel: '包邮',
                            inputValue: '1'
                        }
                    ],
                    listeners: {
                        change: 'onFreeRadioChange'
                    }
                },
                {
                    xtype: 'radiogroup',
                    fieldLabel: '计价方式',
                    name: 'type',
                    listeners: {
                        change: 'changeType'
                    },
                    items: [
                        {
                            xtype: 'radiofield',
                            boxLabel: '按件数',
                            inputValue: '11',
                            checked: true
                        },
                        {
                            xtype: 'radiofield',
                            boxLabel: '按重量',
                            inputValue: '12'
                        },
                        {
                            xtype: 'radiofield',
                            boxLabel: '按体积',
                            inputValue: '13'
                        }
                    ]
                },
                {
                    xtype: 'checkboxgroup',
                    fieldLabel: '运送方式',
                    name: 'deliveryTypeCheckbox',
                    listeners: {
                        change: 'changeDeliveryType'
                    },
                    allowBlank: false,
                    items: [
                        {
                            xtype: 'checkboxfield',
                            name: 'deliveryType',
                            boxLabel: '快递',
                            inputValue: '10'
                        },
                        {
                            xtype: 'checkboxfield',
                            name: 'deliveryType',
                            boxLabel: 'EMS',
                            inputValue: '11'
                        },
                        {
                            xtype: 'checkboxfield',
                            name: 'deliveryType',
                            boxLabel: '平邮',
                            inputValue: '12'
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    fieldLabel: '快递配送',
                    disabled: true,
                    name: 'container_delivery_10',
                    items: [
                        {
                            xtype: 'gridpanel',
                            border: false,
                            header: false,
                            forceFit: true,
                            rowLines: false,
                            name: 'money_rule_10',
                            store: {
                                data: [
                                    {
                                        isDefault: 1,
                                        area: '<span style="color: #0f74a8">默认运费</span>',
                                        first: 1,
                                        next: 1,
                                        firstMoney: 0,
                                        nextMoney: 0
                                    }
                                ]
                            },
                            columns: [
                                {
                                    xtype: 'gridcolumn',
                                    dataIndex: 'area',
                                    text: '地区',
                                    width: 200
                                },
                                {
                                    xtype: 'widgetcolumn',
                                    widget: {xtype: 'textfield', bind: '{record.first}'},
                                    dataIndex: 'first',
                                    text: '首件数(件)',
                                    name: 'kdfirst'
                                },
                                {
                                    xtype: 'widgetcolumn',
                                    widget: {xtype: 'textfield', bind: '{record.firstMoney}'},
                                    dataIndex: 'firstMoney',
                                    text: '首费(元)'
                                },
                                {
                                    xtype: 'widgetcolumn',
                                    widget: {xtype: 'textfield', bind: '{record.next}'},
                                    dataIndex: 'next',
                                    text: '续件数(件)',
                                    name: 'kdsecond'
                                },
                                {
                                    xtype: 'widgetcolumn',
                                    widget: {xtype: 'textfield', bind: '{record.nextMoney}'},
                                    dataIndex: 'nextMoney',
                                    text: '续费(元)'
                                },
                                {
                                    xtype: 'actioncolumn',
                                    text: '操作',
                                    dataIndex: 'id',
                                    align: 'center',
                                    items: [
                                        {
                                            iconCls: "x-fa fa-times red",
                                            style: {marginRight: 10},
                                            tooltip: '删除',
                                            handler: 'onKDDeleteClick'
                                        }
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
                                            icon: Resource.png('jet', 'addIcon'),
                                            text: '添加地区',
                                            type: '10',
                                            listeners: {
                                                click: 'onAddKDClick'
                                            }
                                        },
                                        {
                                            xtype: 'button',
                                            icon: Resource.png('jet', 'RemoveMulticaret_dark'),
                                            text: '删除地区',
                                            type: '10',
                                            listeners: {
                                                click: 'onDelKDClick'
                                            }
                                        }
                                    ]
                                }
                            ],
                            selModel: {
                                selType: 'checkboxmodel'
                            }
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    fieldLabel: 'EMS配送',
                    disabled: true,
                    name: 'container_delivery_11',
                    items: [
                        {
                            xtype: 'gridpanel',
                            border: false,
                            header: false,
                            forceFit: true,
                            rowLines: false,
                            name: 'money_rule_11',
                            store: {
                                data: [
                                    {
                                        isDefault: 1,
                                        area: '<span style="color: #0f74a8">默认运费</span>',
                                        first: 1,
                                        next: 1,
                                        firstMoney: 0,
                                        nextMoney: 0
                                    }
                                ]
                            },
                            columns: [
                                {
                                    xtype: 'gridcolumn',
                                    dataIndex: 'area',
                                    text: '地区',
                                    width: 200
                                },
                                {
                                    xtype: 'widgetcolumn',
                                    widget: {xtype: 'textfield', bind: '{record.first}'},
                                    dataIndex: 'first',
                                    text: '首件数(件)',
                                    name: 'emsfirst'
                                },
                                {
                                    xtype: 'widgetcolumn',
                                    widget: {xtype: 'textfield', bind: '{record.firstMoney}'},
                                    dataIndex: 'firstMoney',
                                    text: '首费(元)'
                                },
                                {
                                    xtype: 'widgetcolumn',
                                    widget: {xtype: 'textfield', bind: '{record.next}'},
                                    dataIndex: 'next',
                                    text: '续件数(件)',
                                    name: 'emssecond'
                                },
                                {
                                    xtype: 'widgetcolumn',
                                    widget: {xtype: 'textfield', bind: '{record.nextMoney}'},
                                    dataIndex: 'nextMoney',
                                    text: '续费(元)'
                                },
                                {
                                    xtype: 'actioncolumn',
                                    text: '操作',
                                    dataIndex: 'id',
                                    align: 'center',
                                    items: [
                                        {
                                            iconCls: "x-fa fa-times red",
                                            style: {marginRight: 10},
                                            tooltip: '删除',
                                            handler: 'onEMSDeleteClick'
                                        }
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
                                            icon: Resource.png('jet', 'addIcon'),
                                            text: '添加地区',
                                            type: '11',
                                            listeners: {
                                                click: 'onAddKDClick'
                                            }
                                        },
                                        {
                                            xtype: 'button',
                                            icon: Resource.png('jet', 'RemoveMulticaret_dark'),
                                            text: '删除地区',
                                            type: '11',
                                            listeners: {
                                                click: 'onDelKDClick'
                                            }
                                        }
                                    ]
                                }
                            ],
                            selModel: {
                                selType: 'checkboxmodel'
                            }
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    fieldLabel: '平邮配送',
                    disabled: true,
                    name: 'container_delivery_12',
                    items: [
                        {
                            xtype: 'gridpanel',
                            border: false,
                            header: false,
                            forceFit: true,
                            rowLines: false,
                            name: 'money_rule_12',
                            store: {
                                data: [
                                    {
                                        isDefault: 1,
                                        area: '<span style="color: #0f74a8">默认运费</span>',
                                        first: 1,
                                        next: 1,
                                        firstMoney: 0,
                                        nextMoney: 0
                                    }
                                ]
                            },
                            columns: [
                                {
                                    xtype: 'gridcolumn',
                                    dataIndex: 'area',
                                    text: '地区',
                                    width: 200
                                },
                                {
                                    xtype: 'widgetcolumn',
                                    widget: {xtype: 'textfield', bind: '{record.first}'},
                                    dataIndex: 'first',
                                    text: '首件数(件)',
                                    name: 'pyfirst'
                                },
                                {
                                    xtype: 'widgetcolumn',
                                    widget: {xtype: 'textfield', bind: '{record.firstMoney}'},
                                    dataIndex: 'firstMoney',
                                    text: '首费(元)'
                                },
                                {
                                    xtype: 'widgetcolumn',
                                    widget: {xtype: 'textfield', bind: '{record.next}'},
                                    dataIndex: 'next',
                                    text: '续件数(件)',
                                    name: 'pysecond'
                                },
                                {
                                    xtype: 'widgetcolumn',
                                    widget: {xtype: 'textfield', bind: '{record.nextMoney}'},
                                    dataIndex: 'nextMoney',
                                    text: '续费(元)'
                                },
                                {
                                    xtype: 'actioncolumn',
                                    text: '操作',
                                    dataIndex: 'id',
                                    align: 'center',
                                    items: [
                                        {
                                            iconCls: "x-fa fa-times red",
                                            style: {marginRight: 10},
                                            tooltip: '删除',
                                            handler: 'onPYDeleteClick'
                                        }
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
                                            icon: Resource.png('jet', 'addIcon'),
                                            text: '添加地区',
                                            type: '12',
                                            listeners: {
                                                click: 'onAddKDClick'
                                            }
                                        },
                                        {
                                            xtype: 'button',
                                            icon: Resource.png('jet', 'RemoveMulticaret_dark'),
                                            text: '删除地区',
                                            type: '12',
                                            listeners: {
                                                click: 'onDelKDClick'
                                            }
                                        }
                                    ]
                                }
                            ],
                            selModel: {
                                selType: 'checkboxmodel'
                            }
                        }
                    ]
                },
                {
                    xtype: 'checkboxfield',
                    name: 'isRuleFree',
                    anchor: '100%',
                    fieldLabel: '包邮条件',
                    boxLabel: '指定条件包邮 (可选)',
                    listeners: {
                        change: 'byChange'
                    }
                },
                {
                    xtype: 'fieldcontainer',
                    fieldLabel: '指定包邮地区',
                    disabled: true,
                    name: 'bycontainer',
                    items: [
                        {
                            xtype: 'gridpanel',
                            border: false,
                            header: false,
                            forceFit: true,
                            name: 'free_rule',
                            columns: [
                                {
                                    xtype: 'gridcolumn',
                                    dataIndex: 'areaName',
                                    text: '地区'
                                },
                                {
                                    xtype: 'gridcolumn',
                                    dataIndex: 'deliveryTypeDesc',
                                    text: '运送方式'
                                },
                                {
                                    xtype: 'gridcolumn',
                                    dataIndex: 'freeRule',
                                    text: '包邮条件',
                                    renderer: function (v, mate, record) {
                                        var freeRule = record.get('freeRule');
                                        var condition = record.get('condition');
                                        if (freeRule == 11) {
                                            if (condition == 1) return '件数';
                                            if (condition == 2) return '金额';
                                            if (condition == 3) return '件数+金额';
                                        }
                                        if (freeRule == 12) {
                                            if (condition == 1) return '重量';
                                            if (condition == 2) return '金额';
                                            if (condition == 3) return '重量+金额';
                                        }
                                        if (freeRule == 13) {
                                            if (condition == 1) return '体积';
                                            if (condition == 2) return '金额';
                                            if (condition == 3) return '体积+金额';
                                        }
                                    }
                                },
                                {
                                    xtype: 'gridcolumn',
                                    dataIndex: 'condition',
                                    text: '满条件包邮',
                                    renderer: function (v, mate, record) {
                                        var freeRule = record.get('freeRule');
                                        var condition = record.get('condition');
                                        var v1 = record.get('value1');
                                        var v2 = record.get('value2');
                                        if (freeRule == 11) {
                                            if (condition == 1) return '满' + v1 + '件包邮';
                                            if (condition == 2) return '满' + v2 + '元包邮';
                                            if (condition == 3) return '满' + v1 + '件,' + v2 + '元以上包邮';
                                        }
                                        if (freeRule == 12) {
                                            if (condition == 1) return '在' + v1 + 'kg内包邮';
                                            if (condition == 2) return '满' + v2 + '元包邮';
                                            if (condition == 3) return '在' + v1 + 'kg内,' + v2 + '元以上包邮';
                                        }
                                        if (freeRule == 13) {
                                            if (condition == 1) return '在' + v1 + 'm³内包邮';
                                            if (condition == 2) return '满' + v2 + '元包邮';
                                            if (condition == 3) return '在' + v1 + 'm³内,' + v2 + '元以上包邮';
                                        }
                                    }
                                },
                                {
                                    xtype: 'actioncolumn',
                                    text: '操作',
                                    dataIndex: 'id',
                                    align: 'center',
                                    items: [
                                        {
                                            iconCls: "x-fa fa-times red",
                                            tooltip: '删除',
                                            handler: 'onBYDeleteClick'
                                        }
                                    ]
                                }
                            ],
                            store: {
                                data: []
                            },
                            dockedItems: [
                                {
                                    xtype: 'toolbar',
                                    dock: 'top',
                                    items: [
                                        {
                                            xtype: 'button',
                                            icon: Resource.png('jet', 'addIcon'),
                                            text: '添加包邮条件',
                                            type: 'by',
                                            listeners: {
                                                click: 'onAddBYClick'
                                            }
                                        },
                                        {
                                            xtype: 'button',
                                            icon: Resource.png('jet', 'RemoveMulticaret_dark'),
                                            text: '删除包邮条件',
                                            type: 'by',
                                            listeners: {
                                                click: 'onDelKDClick'
                                            }
                                        }
                                    ]
                                }
                            ],
                            selModel: {
                                selType: 'checkboxmodel'
                            }
                        }
                    ]
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
                    text: '保存模板',
                    listeners: {
                        click: 'onSaveClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'cancel'),
                    text: '取消关闭',
                    listeners: {
                        click: 'onCloseClick'
                    }
                },
                '->'
            ]
        }
    ],

    onAddKDClick: function (button, e, eOpts) {
        var self = this;
        var win = Dialog.openWindow('App.logistics.LogisticsAreaWindow', {apis: this.apis});
        win.loadData();
        win.setType(button.type);
        win.setSelectCallback(function (data) {
            var gp = self.find('money_rule_' + data.type);
            var store = gp.getStore();
            store.add({isDefault: 0, area: data.name, first: 1, next: 1, code: data.code});
        });

    },

    onDelKDClick: function (button, e, eOpts) {
        var gridpanel = this.find('money_rule_' + button.type);
        var selection = gridpanel.getSelection();
        var store = gridpanel.getStore();

        console.dir(selection);
        console.dir(store.getData().items);

        var data = Ext.clone(store.getData().items);
        store.removeAll();

        for (var i = 0; i < data.length; i++) {
            for (var j = 0; j < selection.length; j++) {
                if (data[i].id == selection[j].id) {
                    data.splice(i, 1);
                }
            }
        }
        if (data.length > 1) {
            for (var k = 0; k < data.length; k++) {
                store.add(data[k]);
            }
        }
    },

    onAddBYClick: function (button, e, eOpts) {
        var typeValue = this.find('type').getValue();
        var deliveryType = this.find('deliveryTypeCheckbox').getValue();
        deliveryType = deliveryType.deliveryType;
        if (!typeValue.type) {
            Ext.MessageBox.alert("提示信息", "请选择计价方式");
            return false;
        }
        if (!deliveryType) {
            Ext.MessageBox.alert("提示信息", "请勾选运送方式");
            return false;
        }
        var win = Dialog.openWindow('App.logistics.LogisticsByWindow', {apis: this.apis});
        win.setValue({
            type: typeValue.type,
            deliveryType: deliveryType
        });
        var self = this;
        win.setCallback(function (data) {
            var gp = self.find('free_rule');
            var store = gp.getStore();
            store.add(data);
        });
    },

    onSaveClick: function (button, e, eOpts) {
        var self = this;
        var name = this.find('name').getValue();
        var free = this.find('free_group').getValue().free; //是否免运费

        if (name == '') {
            Dialog.alert('提示', '模板名称不能为空');
            return false;
        }

        var deliveryType = this.find('deliveryTypeCheckbox').getValue(); //运送方式
        if (typeof deliveryType.deliveryType === "string") {
            deliveryType = [deliveryType.deliveryType];
        } else {
            deliveryType = deliveryType.deliveryType;
        }

        if (!deliveryType || deliveryType.length == 0) {
            Dialog.alert('提示', '请选择运送方式');
            return false;
        }

        var submitResult = {};
        if (free === "1") {
            submitResult['name'] = name;
            submitResult['free'] = free;
            var deliveryValue = [];
            for (var i = 0; i < deliveryType.length; i++) {  //10快递，11EMS，12平邮
                var value = deliveryType[i];
                deliveryValue.push({deliveryType: value});
            }
            submitResult['deliveryValue'] = deliveryValue;
        } else {
            var type = this.find('type').getValue().type; //计价方式

            var deliveryValue = [];
            for (var i = 0; i < deliveryType.length; i++) {  //10快递，11EMS，12平邮
                var value = deliveryType[i];
                var valueArray = self.find('money_rule_' + value).getStore().getData().items;
                if (valueArray.length > 0) {
                    for (var j = 0; j < valueArray.length; j++) {
                        var data = valueArray[j].data;
                        data.deliveryType = value;
                        if (data['first'] == null || data['next'] == null || data['firstMoney'] == null || data['nextMoney'] == null) {
                            Dialog.alert('配送模板数量和价格必须填写完整');
                            return false;
                        }
                        deliveryValue.push(data);
                    }
                }
            }
            var isRuleFree = self.find('isRuleFree').getValue();
            var freeCondition = [];
            if (isRuleFree) {
                var items = self.find('free_rule').getStore().getData().items;
                if (items.length > 0) {
                    for (var z = 0; z < items.length; z++) {
                        freeCondition.push(items[z].data);
                    }
                }
            }
            submitResult['name'] = name;
            submitResult['free'] = free;
            submitResult['type'] = type;
            submitResult['deliveryType'] = deliveryType;
            submitResult['deliveryValue'] = deliveryValue;
            submitResult['isRuleFree'] = isRuleFree;
            submitResult['freeCondition'] = freeCondition;
        }

        if (this._data) {
            submitResult['id'] = this._data['id'];
            this.apis.Logistics.updateTemplate
                .wait(self, '正在修改运费模板...')
                .call({object: submitResult}, function () {
                    self.close();
                    if (self._callback) {
                        self._callback();
                    }
                })
        } else {
            this.apis.Logistics.addTemplates
                .wait(self, '正在添加运费模板...')
                .call({object: submitResult}, function () {
                    self.close();
                    if (self._callback) {
                        self._callback();
                    }
                })
        }
    },

    onCloseClick: function (button, e, eOpts) {
        this.close();
    },

    changeType: function (t, newValue, oldValue, eOpts) {
        newValue = parseInt(newValue.type);
        //11件数，12重量，13体积
        var kdFirst = this.find('kdfirst');
        var kdSecond = this.find('kdsecond');
        var emsFirst = this.find('emsfirst');
        var emsSecond = this.find('emssecond');
        var pyFirst = this.find('pyfirst');
        var pySecond = this.find('pysecond');

        var firstTitle = '';
        var secondTitle = '';
        if (newValue == 11) {
            firstTitle = '首费件数(件)';
            secondTitle = '每增加(件)';
        } else if (newValue == 12) {
            firstTitle = '首费重量(kg)';
            secondTitle = '每增加(kg)';
        } else if (newValue == 13) {
            firstTitle = '首费体积(m3)';
            secondTitle = '每增加(m3)';
        }
        kdFirst.setText(firstTitle);
        emsFirst.setText(firstTitle);
        pyFirst.setText(firstTitle);

        kdSecond.setText(secondTitle);
        emsSecond.setText(secondTitle);
        pySecond.setText(secondTitle);
    },

    changeDeliveryType: function (t, newValue, oldValue, eOpts) {
        for (var i = 0; i < 3; i++) this.find('container_delivery_1' + i).setDisabled(true);

        newValue = newValue.deliveryType;
        if (Array.isArray(newValue)) {
            for (var i = 0; i < newValue.length; i++) {
                var nv = parseInt(newValue[i]);
                this.find('container_delivery_' + nv).setDisabled(false);
            }

        } else {
            if (newValue) {
                var nv = parseInt(newValue);
                this.find('container_delivery_' + nv).setDisabled(false);
            }
        }
    },
    onKDDeleteClick: function (view, rowIndex, colIndex, item, e, record, row) {
        if (!record.data.code) {
            return false;
        }
        var gp = this.find('money_rule_10');
        var store = gp.getStore();
        var data = Ext.clone(store.getData().items);
        store.removeAll();
        if (data.length > 1) {
            for (var i = 0; i < data.length; i++) {
                if (i != rowIndex) {
                    store.add(data[i]);
                }
            }
        }
    },
    onEMSDeleteClick: function (view, rowIndex, colIndex, item, e, record, row) {
        if (!record.data.code) {
            return false;
        }
        var gp = this.find('money_rule_11');
        var store = gp.getStore();
        var data = Ext.clone(store.getData().items);
        store.removeAll();
        if (data.length > 1) {
            for (var i = 0; i < data.length; i++) {
                if (i != rowIndex) {
                    store.add(data[i]);
                }
            }
        }
    },
    onPYDeleteClick: function (view, rowIndex, colIndex, item, e, record, row) {
        if (!record.data.code) {
            return false;
        }
        var gp = this.find('money_rule_12');
        var store = gp.getStore();
        var data = Ext.clone(store.getData().items);
        store.removeAll();
        if (data.length > 1) {
            for (var i = 0; i < data.length; i++) {
                if (i != rowIndex) {
                    store.add(data[i]);
                }
            }
        }
    },
    onBYDeleteClick: function (view, rowIndex, colIndex, item, e, record, row) {
        var gp = this.find('free_rule');
        var store = gp.getStore();
        var data = Ext.clone(store.getData().items);
        store.removeAll();
        if (data.length > 1) {
            for (var i = 0; i < data.length; i++) {
                if (i != rowIndex) {
                    store.add(data[i]);
                }
            }
        }
    },
    byChange: function (e, newValue, oldValue, eOpts) {
        if (newValue) {
            this.find('bycontainer').setDisabled(false);
        } else {
            this.find('bycontainer').setDisabled(true);
        }
    },

    onFreeRadioChange: function (e, newValue, oldValue) {
        if (newValue['free'] == 1) {
            this.find('type').hide();
            var deliveryTypeCheckbox = this.find('deliveryTypeCheckbox');
            // deliveryTypeCheckbox.hide();
            for (var k = 0; k < deliveryTypeCheckbox.items.items.length; k++) {
                this.find('container_delivery_' + deliveryTypeCheckbox.items.items[k].inputValue).hide();
            }
            this.find('isRuleFree').hide();
            this.find('bycontainer').hide();
        } else {
            this.find('type').show();
            var deliveryTypeCheckbox = this.find('deliveryTypeCheckbox');
            // deliveryTypeCheckbox.show();
            for (var k = 0; k < deliveryTypeCheckbox.items.items.length; k++) {
                this.find('container_delivery_' + deliveryTypeCheckbox.items.items[k].inputValue).show();
            }
            this.find('isRuleFree').show();
            this.find('bycontainer').show();
        }
    },

    setValue: function (data) {
        var self = this;
        this._data = data;
        var form = this.find('templateForm').getForm();
        this.setTitle('修改运费模板');
        var tid = data['id'];
        var deliveryTypeCheckbox = this.find('deliveryTypeCheckbox');
        var deliveryTypes = {};
        for (var k = 0; k < deliveryTypeCheckbox.items.items.length; k++) {
            deliveryTypes[deliveryTypeCheckbox.items.items[k].inputValue] = deliveryTypeCheckbox.items.items[k].boxLabel;
        }


        this.apis.Logistics.getTemplate
            .wait(this, '正在加载运费模板...')
            .call({tid: tid}, function (data) {
                form.setValues(data);
                var tdts = data['deliveryType'];
                var dts = tdts.split(",");

                if (data['free'] == 1) {
                    self.find('deliveryTypeCheckbox').setValue({deliveryType: dts});
                }

                var moneyRules = data['TableLogisticsMoneyRule'];
                var freeRules = data['TableLogisticsFreeRule'];
                if (moneyRules) {
                    var deliveryValues = {};
                    var deliveryTypeValues = [];

                    for (var i = 0; i < moneyRules.length; i++) {
                        var dt = moneyRules[i]['deliveryType'];
                        var areas = moneyRules[i]['TableLogisticsAddress'];
                        if (!deliveryValues['money_rule_' + dt]) deliveryValues['money_rule_' + dt] = [];
                        deliveryValues['money_rule_' + dt].push(moneyRules[i]);
                        if (areas && areas.length > 0) {
                            var areaCodes = [];
                            for (var j = 0; j < areas.length; j++) {
                                areaCodes.push(areas[j]['provinceCode']);
                                areaCodes.push(areas[j]['cityCode']);
                            }
                            moneyRules[i]['area'] = areas[0]['province'] + areas[0]['city'] + '等' + areas.length + '省市';
                            moneyRules[i]['code'] = areaCodes.join(",");
                        }
                        if (moneyRules[i]['isDefault'] == 1) {
                            moneyRules[i]['area'] = '<span style="color: #0f74a8">默认运费</span>';
                        }
                        deliveryTypeValues.push('' + dt);
                    }

                    self.find('deliveryTypeCheckbox').setValue({deliveryType: deliveryTypeValues});
                    for (var name in deliveryValues) {
                        var moneyStore = Ext.create('Ext.data.Store', {
                            data: deliveryValues[name]
                        });
                        self.find(name).setStore(moneyStore);
                    }
                }
                if (freeRules) {
                    if (freeRules.length > 0) {
                        self.find('isRuleFree').setValue(true);
                    }
                    for (var i = 0; i < freeRules.length; i++) {
                        var areas = freeRules[i]['TableLogisticsAddress'];
                        if (areas && areas.length > 0) {
                            var areaCodes = [];
                            for (var j = 0; j < areas.length; j++) {
                                areaCodes.push(areas[j]['provinceCode']);
                                areaCodes.push(areas[j]['cityCode']);
                            }
                            freeRules[i]['areaName'] = areas[0]['province'] + areas[0]['city'] + '等' + areas.length + '省市';
                            freeRules[i]['deliveryTypeDesc'] = deliveryTypes['' + freeRules[i]['deliveryType']];
                            if (freeRules[i]['freeRule'] === 11) {
                                if (freeRules[i]['condition'] === 1) {
                                    freeRules[i]['conditionDesc'] = '件数';
                                }
                                if (freeRules[i]['condition'] === 2) {
                                    freeRules[i]['conditionDesc'] = '金额';
                                }
                                if (freeRules[i]['condition'] === 3) {
                                    freeRules[i]['conditionDesc'] = '件数+金额';
                                }
                            }
                            if (freeRules[i]['freeRule'] === 12) {
                                if (freeRules[i]['condition'] === 1) {
                                    freeRules[i]['conditionDesc'] = '重量';
                                }
                                if (freeRules[i]['condition'] === 2) {
                                    freeRules[i]['conditionDesc'] = '金额';
                                }
                                if (freeRules[i]['condition'] === 3) {
                                    freeRules[i]['conditionDesc'] = '重量+金额';
                                }
                            }
                            if (freeRules[i]['freeRule'] === 13) {
                                if (freeRules[i]['condition'] === 1) {
                                    freeRules[i]['conditionDesc'] = '体积';
                                }
                                if (freeRules[i]['condition'] === 2) {
                                    freeRules[i]['conditionDesc'] = '金额';
                                }
                                if (freeRules[i]['condition'] === 3) {
                                    freeRules[i]['conditionDesc'] = '体积+金额';
                                }
                            }
                            freeRules[i]['code'] = areaCodes.join(",");
                        }
                    }

                    var freeStore = Ext.create('Ext.data.Store', {
                        data: freeRules
                    });
                    self.find('free_rule').setStore(freeStore);
                }
            });
    }
});
