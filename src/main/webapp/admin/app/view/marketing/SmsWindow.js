Ext.define('App.marketing.SmsWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.ComboBox',
        'Ext.form.field.Display',
        'Ext.form.FieldContainer',
        'Ext.grid.Panel',
        'Ext.grid.column.Column',
        'Ext.view.Table',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 450,
    width: 600,
    layout: 'fit',
    title: '添加短信服务',
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
                    name: 'name',
                    anchor: '100%',
                    fieldLabel: '短信名称'
                },
                {
                    xtype: 'combobox',
                    name: 'impl',
                    displayField: 'name',
                    valueField: 'value',
                    anchor: '100%',
                    editable: false,
                    fieldLabel: '短信接口',
                    listeners: {
                        change: 'onInterfaceChange'
                    }
                },
                {
                    xtype: 'displayfield',
                    name: 'typeDesc',
                    anchor: '100%',
                    fieldLabel: '短信内容类型',
                    value: ''
                },
                {
                    xtype: 'fieldcontainer',
                    height: 200,
                    layout: 'fit',
                    fieldLabel: '短信接口参数',
                    items: [
                        {
                            xtype: 'gridpanel',
                            name: 'params',
                            border: false,
                            header: false,
                            plugins: [{
                                ptype: 'cellediting', clicksToEdit: 1, listeners: {
                                    edit: 'onRuleCellEditor'
                                }
                            }],
                            columns: [
                                {
                                    xtype: 'gridcolumn',
                                    width: 180,
                                    dataIndex: 'name',
                                    text: '参数名称'
                                },
                                {
                                    xtype: 'gridcolumn',
                                    width: 280,
                                    dataIndex: 'value',
                                    text: '参数值',
                                    field: {
                                        xtype: 'textfield'
                                    }
                                }
                            ]
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
                    text: '保存',
                    listeners: {
                        click: 'onSaveClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'cancel'),
                    text: '取消',
                    listeners: {
                        click: 'onCancelClick'
                    }
                },
                '->'
            ]
        }
    ],

    onSaveClick: function () {
        var self = this;
        var form = this.find('form').getForm();
        if (form.isValid()) {
            var grid = this.find('params');
            var data = form.getValues();

            var store = grid.getStore();
            if (store) {
                var gridData = store.getData();
                var values = [];
                gridData.each(function (item) {
                    values.push(item.getData());
                });
                data['params'] = values;
            }

            if (this._data) {
                data['id'] = this._data['id'];
                self.apis.Sms.updateSms
                    .wait(self, '正在修改短信服务...')
                    .call({object: data}, function () {
                        self.close();
                        if (self._callback) {
                            self._callback();
                        }
                    });
            } else {
                self.apis.Sms.addSms
                    .wait(self, '正在添加短信服务...')
                    .call({object: data}, function () {
                        self.close();
                        if (self._callback) {
                            self._callback();
                        }
                    });
            }
        }
    },

    onCancelClick: function () {
        this.close();
    },

    onInterfaceChange: function (field, newValue, oldValue) {
        var model = field.getSelection();
        var params = model.get('params');
        if (!params) params = [];

        if (this._data) {
            var dp = this._data['params'];
            if (dp) {
                if (typeof dp == "string") dp = JSON.parse(dp);
                for (var i = 0; i < params.length; i++) {
                    for (var j = 0; j < dp.length; j++) {
                        if (params[i]['code'] == dp[j]['code']) {
                            params[i]['value'] = dp[j]['value'];
                        }
                    }
                }
            }
        }

        var store = Ext.create('Ext.data.Store', {data: params});
        this.find('params').setStore(store);
        if (model.get('type') == 1) {
            this.find('typeDesc').setValue('需要配置短信模板');
        } else {
            this.find('typeDesc').setValue('文本内容');
        }
    },

    setInit: function () {
        var store = this.apis.Sms.getSmsImpls.createListStore();
        this.find('impl').setStore(store);
        store.load();
    },

    setValue: function (data) {
        this._data = data;
        var form = this.find('form').getForm();
        form.setValues(data);
    }

});
