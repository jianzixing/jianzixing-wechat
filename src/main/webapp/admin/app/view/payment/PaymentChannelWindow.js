Ext.define('App.payment.PaymentChannelWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.form.Panel',
        'Ext.form.field.ComboBox',
        'Ext.form.RadioGroup',
        'Ext.form.field.Radio',
        'Ext.form.field.TextArea',
        'Ext.grid.Panel',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.view.Table'
    ],

    autoShow: true,
    height: 650,
    width: 800,
    layout: 'fit',
    title: '添加支付方式',
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
                        click: 'onCloseClick'
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
                    xtype: 'hiddenfield',
                    name: 'id'
                },
                {
                    xtype: 'textfield',
                    name: 'name',
                    anchor: '100%',
                    fieldLabel: '支付方式名称'
                },
                {
                    xtype: 'combobox',
                    name: 'impl',
                    displayField: 'name',
                    valueField: 'impl',
                    anchor: '100%',
                    editable: false,
                    fieldLabel: '支付实现方式',
                    listeners: {
                        change: 'onComboboxChange'
                    }
                },
                {
                    xtype: 'radiogroup',
                    fieldLabel: '是否启用',
                    items: [
                        {
                            xtype: 'radiofield',
                            name: 'enable',
                            boxLabel: '启用',
                            checked: true,
                            inputValue: '1'
                        },
                        {
                            xtype: 'radiofield',
                            name: 'enable',
                            boxLabel: '禁用',
                            inputValue: '0'
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    name: 'certUploadContainer',
                    hidden: true,
                    fieldLabel: '证书文件',
                    layout: {
                        type: 'hbox',
                        align: 'stretch'
                    },
                    items: [
                        {
                            xtype: 'textfield',
                            editable: false,
                            name: 'certFile',
                            flex: 1
                        },
                        {
                            xtype: 'button',
                            margin: '0 0 0 10',
                            text: '上传证书',
                            listeners: {
                                click: 'onUploadCertClick'
                            }
                        }
                    ]
                },
                {
                    xtype: 'textfield',
                    hidden: true,
                    name: 'certPassword',
                    anchor: '100%',
                    fieldLabel: '证书密码'
                },
                {
                    xtype: 'textareafield',
                    name: 'detail',
                    anchor: '100%',
                    height: 80,
                    fieldLabel: '描述'
                },
                {
                    xtype: 'fieldcontainer',
                    height: 240,
                    fieldLabel: '支付方式参数',
                    layout: 'fit',
                    items: [
                        {
                            xtype: 'gridpanel',
                            name: 'param_grid',
                            border: false,
                            height: 200,
                            header: false,
                            hidden: true,
                            columns: [
                                {
                                    xtype: 'gridcolumn',
                                    width: 100,
                                    dataIndex: 'key',
                                    text: '参数名称'
                                },
                                {
                                    xtype: 'widgetcolumn',
                                    width: 450,
                                    dataIndex: 'value',
                                    text: '参数配置',
                                    widget: {
                                        xtype: 'textfield',
                                        bind: '{record.value}'
                                    }
                                },
                                {
                                    xtype: 'gridcolumn',
                                    width: 120,
                                    dataIndex: 'name',
                                    text: '参数说明'
                                }
                            ]
                        },
                        {
                            xtype: 'container',
                            name: 'param_cnt',
                            hidden: false,
                            html: '<div style="width: 100%;height: 100%;text-align: center;line-height: 200px">' +
                                '该支付方式实现不需要额外配置信息' +
                                '</div>'
                        }
                    ]
                }
            ]
        }
    ],

    onSaveClick: function (button, e, eOpts) {
        var self = this;
        var grid = this.find('param_grid');
        var form = this.find('form').getForm();
        if (form.isValid()) {
            var dt = form.getValues();
            var store = grid.getStore();
            var data = store.getData();
            var params = [];
            for (var i = 0; i < data.length; i++) {
                params.push(data.getAt(i).getData());
            }

            dt['arguments'] = params;
            if (self._data) {
                this.apis.Payment.updateChannel
                    .wait(self, '正在保存支付方式...')
                    .call({object: dt}, function () {
                        self.close();
                        if (self._callback) {
                            self._callback();
                        }
                    });
            } else {
                this.apis.Payment.addChannel
                    .wait(self, '正在保存支付方式...')
                    .call({object: dt}, function () {
                        self.close();
                        if (self._callback) {
                            self._callback();
                        }
                    });
            }
        }
    },

    onCloseClick: function (button, e, eOpts) {
        this.close();
    },

    onUploadCertClick: function (button, e, eOpts) {
        var self = this;
        var uploadWin = Dialog.openWindow('App.file.UploadWindow', {
            canDownload: "false",
            singleFile: true,
            _callback: function (files) {
                if (files && files['data'] && files['data'].length > 0) {
                    self.find('certFile').setValue(files['data'][0]['fileName']);
                }
            }
        });
    },

    onComboboxChange: function (field, newValue, oldValue, eOpts) {
        var record = field.getSelection();
        if (record.get('cert') == 1) {
            this.find('certUploadContainer').show();
            this.find('certPassword').show();
        } else {
            this.find('certUploadContainer').hide();
            this.find('certPassword').hide();
        }
        var params = record.get('params');
        if (params && params.length > 0) {
            if (this._data) {
                var args = this._data['TablePaymentArgument'];
                for (var i = 0; i < params.length; i++) {
                    for (var j = 0; j < args.length; j++) {
                        if (params[i]['key'] == args[j]['key']) {
                            params[i]['value'] = args[j]['value'];
                        }
                    }
                }
            }
            var store = Ext.create('Ext.data.Store', {data: params});
            this.find('param_grid').setStore(store);
            this.find('param_grid').show();
            this.find('param_cnt').hide();
        } else {
            var store = Ext.create('Ext.data.Store', {data: []});
            this.find('param_grid').setStore(store);
            this.find('param_grid').hide();
            this.find('param_cnt').show();
        }
    },

    initWindow: function () {
        var impl = this.find('impl');
        var store = this.apis.Payment.getModels.createListStore();
        impl.setStore(store);
        store.load();
    },

    setValue: function (value) {
        var self = this;
        this.setTitle('修改支付方式');
        this._data = value;
        var form = this.find('form').getForm();
        form.setValues(value);
        this.find('impl').disable();
    }

});
