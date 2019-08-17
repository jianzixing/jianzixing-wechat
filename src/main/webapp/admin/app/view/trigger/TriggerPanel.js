Ext.define('App.trigger.TriggerPanel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.form.Panel',
        'Ext.form.field.ComboBox',
        'Ext.form.FieldContainer',
        'Ext.grid.Panel',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'UXApp.editor.CKEditor'
    ],

    layout: 'auto',
    header: false,
    defaultListenerScope: true,
    autoScroll: true,

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'button',
                    text: '返回列表',
                    icon: Resource.png('jet', 'back'),
                    listeners: {
                        click: 'onBackClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'saveTempConfig'),
                    text: '确定保存',
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
            items: [
                {
                    xtype: 'container',
                    anchor: '100%',
                    items: [
                        {
                            xtype: 'label',
                            style: {
                                display: 'block'
                            },
                            margin: '0 0 20 0',
                            html: '<div class="basetitle"> <div class="img"> <img border="0" src="/admin/image/icon/base.png"> </div> <div class="text">基本信息</div> </div>'
                        },
                        {
                            xtype: 'textfield',
                            name: 'name',
                            width: 600,
                            allowBlank: false,
                            fieldLabel: '触发器名称'
                        },
                        {
                            xtype: 'combobox',
                            name: 'event',
                            width: 600,
                            displayField: 'name',
                            valueField: 'value',
                            editable: false,
                            allowBlank: false,
                            fieldLabel: '触发器事件',
                            listeners: {
                                change: 'onEventComboboxChange'
                            }
                        },
                        {
                            xtype: 'displayfield',
                            name: 'event_params',
                            hidden: true,
                            width: 600,
                            value: '',
                            fieldLabel: '<span style="color:#999999">事件参数(Freemarker)</span>'
                        },
                        {
                            xtype: 'combobox',
                            name: 'processor',
                            width: 600,
                            displayField: 'name',
                            valueField: 'value',
                            editable: false,
                            allowBlank: false,
                            fieldLabel: '处理器实现',
                            listeners: {
                                change: 'onProcessorChange'
                            }
                        },
                        {
                            xtype: 'radiogroup',
                            width: 600,
                            fieldLabel: '使用限制条件',
                            items: [
                                {
                                    xtype: 'radiofield',
                                    name: 'useRule',
                                    boxLabel: '使用限制条件',
                                    inputValue: '1',
                                    checked: true
                                },
                                {
                                    xtype: 'radiofield',
                                    name: 'useRule',
                                    boxLabel: '不使用限制条件',
                                    inputValue: '0'
                                }
                            ],
                            listeners: {
                                change: 'onUseRuleChange'
                            }
                        },
                        {
                            xtype: 'radiogroup',
                            width: 600,
                            fieldLabel: '启用禁用',
                            items: [
                                {
                                    xtype: 'radiofield',
                                    name: 'enable',
                                    boxLabel: '启用',
                                    inputValue: '1',
                                    checked: true
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
                            xtype: 'displayfield',
                            name: 'event_detail',
                            hidden: true,
                            width: 600,
                            value: '',
                            fieldLabel: '<span style="color:#999999">事件说明</span>'
                        },
                        {
                            xtype: 'displayfield',
                            name: 'params_tips',
                            hidden: true,
                            height: 25,
                            width: 600,
                            value: '<span style="color:#999999">以下单元格可单击编辑,请填写您期望的参数,您可以使用Freemarker模板' +
                                '也可以使用固定值。</span>',
                            fieldLabel: '<span style="color:#999999">注意</span>'
                        },
                        {
                            xtype: 'fieldcontainer',
                            name: 'params',
                            width: 600,
                            hidden: true,
                            fieldLabel: '处理器参数',
                            items: [
                                {
                                    xtype: 'gridpanel',
                                    name: 'params_grid',
                                    border: false,
                                    header: false,
                                    forceFit: true,
                                    plugins: [{
                                        ptype: 'cellediting', clicksToEdit: 1, listeners: {
                                            edit: 'onCellEditor'
                                        }
                                    }],
                                    columns: [
                                        {
                                            xtype: 'gridcolumn',
                                            dataIndex: 'name',
                                            text: '参数名称'
                                        },
                                        {
                                            xtype: 'gridcolumn',
                                            dataIndex: 'value',
                                            text: '参数值',
                                            field: {
                                                xtype: 'textfield'
                                            }
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            xtype: 'combobox',
                            name: 'email',
                            width: 600,
                            displayField: 'name',
                            valueField: 'id',
                            hidden: true,
                            editable: false,
                            fieldLabel: '邮件服务'
                        },
                        {
                            xtype: 'combobox',
                            name: 'sms',
                            width: 600,
                            displayField: 'name',
                            valueField: 'id',
                            hidden: true,
                            editable: false,
                            fieldLabel: '短信服务',
                            listeners: {
                                change: 'onSmsFieldChange'
                            }
                        }
                    ]
                },
                {
                    xtype: 'container',
                    name: 'rule_container',
                    anchor: '100%',
                    items: [
                        {
                            xtype: 'label',
                            style: {
                                display: 'block'
                            },
                            margin: '30 0 20 0',
                            html: '<div class="basetitle"> <div class="img"> <img border="0" src="/admin/image/icon/base.png"> </div> <div class="text">限制条件配置</div> </div>'
                        },
                        {
                            xtype: 'combobox',
                            name: 'timeType',
                            width: 600,
                            editable: false,
                            fieldLabel: '时间类型',
                            displayField: 'name',
                            valueField: 'value',
                            store: {
                                data: [
                                    {name: '每年', value: '1'},
                                    {name: '每月', value: '2'},
                                    {name: '每天', value: '3'},
                                    {name: '每时', value: '4'},
                                    {name: '每分', value: '5'},
                                    {name: '每秒', value: '6'},
                                    {name: '不限时间', value: '7'}
                                ]
                            }
                        },
                        {
                            xtype: 'radiogroup',
                            width: 600,
                            name: 'triggerInfiniteGroup',
                            fieldLabel: '是否不限次数',
                            items: [
                                {
                                    xtype: 'radiofield',
                                    name: 'triggerInfinite',
                                    boxLabel: '不限次数',
                                    inputValue: '1'
                                },
                                {
                                    xtype: 'radiofield',
                                    name: 'triggerInfinite',
                                    boxLabel: '限制次数',
                                    inputValue: '0',
                                    checked: true
                                }
                            ],
                            listeners: {
                                change: 'onTriggerInfiniteChange'
                            }
                        },
                        {
                            xtype: 'numberfield',
                            name: 'triggerCount',
                            width: 600,
                            fieldLabel: '触发次数'
                        },
                        {
                            xtype: 'numberfield',
                            name: 'totalCount',
                            width: 600,
                            emptyText: '如果填0表示不限制最大次数',
                            fieldLabel: '最大触发次数'
                        },
                        {
                            xtype: 'datetimefield',
                            name: 'startTime',
                            width: 600,
                            emptyText: '不填写表示不限制开始时间',
                            format: 'Y-m-d H:i:s',
                            fieldLabel: '开始时间'
                        },
                        {
                            xtype: 'datetimefield',
                            name: 'finishTime',
                            width: 600,
                            emptyText: '不填写表示不限制结束时间',
                            format: 'Y-m-d H:i:s',
                            fieldLabel: '结束时间'
                        },
                        {
                            xtype: 'fieldcontainer',
                            height: 25,
                            width: 600,
                            html: '<span style="color:#999999">以下单元格可单击编辑,请选择合适的判断符号或有效值</span>',
                            fieldLabel: '<span style="color:#999999">注意</span>'
                        },
                        {
                            xtype: 'fieldcontainer',
                            width: 600,
                            name: 'trigger_grid',
                            fieldLabel: '触发条件',
                            items: [
                                {
                                    xtype: 'gridpanel',
                                    name: 'grid',
                                    border: false,
                                    header: false,
                                    forceFit: true,
                                    plugins: [{
                                        ptype: 'cellediting', clicksToEdit: 1, listeners: {
                                            edit: 'onRuleCellEditor'
                                        }
                                    }],
                                    columns: [
                                        {
                                            xtype: 'gridcolumn',
                                            dataIndex: 'detail',
                                            text: '参数名称'
                                        },
                                        {
                                            xtype: 'gridcolumn',
                                            dataIndex: 'symbol',
                                            text: '比较符号',
                                            field: {
                                                xtype: 'combobox',
                                                displayField: 'name',
                                                valueField: 'value',
                                                store: {
                                                    data: [
                                                        {name: '等于', value: 'eq'},
                                                        {name: '大于', value: 'gt'},
                                                        {name: '大于等于', value: 'gte'},
                                                        {name: '小于', value: 'lt'},
                                                        {name: '小于等于', value: 'lte'}
                                                    ]
                                                }
                                            },
                                            renderer: function (v) {
                                                if (v == 'eq') return '等于';
                                                if (v == 'gt') return '大于';
                                                if (v == 'gte') return '大于等于';
                                                if (v == 'lt') return '小于';
                                                if (v == 'lte') return '小于等于';
                                            }
                                        },
                                        {
                                            xtype: 'gridcolumn',
                                            dataIndex: 'value',
                                            text: '满足条件',
                                            field: {
                                                xtype: 'textfield'
                                            }
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'container',
                    name: 'email_container',
                    hidden: true,
                    anchor: '100%',
                    items: [
                        {
                            xtype: 'label',
                            style: {
                                display: 'block'
                            },
                            margin: '30 0 20 0',
                            html: '<div class="basetitle"> <div class="img"> <img border="0" src="/admin/image/icon/base.png"> </div> <div class="text">邮件内容</div> </div>'
                        },
                        {
                            xtype: 'textfield',
                            name: 'title10',
                            width: 600,
                            emptyText: '请输入邮件标题'
                        },
                        {
                            xtype: 'ckeditor',
                            name: 'content10',
                            height: 500,
                            width: 600,
                            border: false
                        }
                    ]
                },
                {
                    xtype: 'container',
                    name: 'sms_container',
                    hidden: true,
                    anchor: '100%',
                    items: [
                        {
                            xtype: 'label',
                            style: {
                                display: 'block'
                            },
                            margin: '30 0 20 0',
                            name: 'sms_container_label',
                            html: '<div class="basetitle"> <div class="img"> <img border="0" src="/admin/image/icon/base.png"> </div> <div class="text">短信内容</div> </div>'
                        },
                        {
                            xtype: 'textareafield',
                            name: 'content20',
                            width: 600,
                            height: 100
                        }
                    ]
                },
                {
                    xtype: 'container',
                    name: 'msg_container',
                    hidden: true,
                    anchor: '100%',
                    items: [
                        {
                            xtype: 'label',
                            style: {
                                display: 'block'
                            },
                            margin: '30 0 20 0',
                            html: '<div class="basetitle"> <div class="img"> <img border="0" src="/admin/image/icon/base.png"> </div> <div class="text">站内信内容</div> </div>'
                        },
                        {
                            xtype: 'textfield',
                            name: 'title30',
                            width: 600,
                            emptyText: '请输入站内信标题'
                        },
                        {
                            xtype: 'ckeditor',
                            name: 'content30',
                            height: 500,
                            width: 600,
                            border: false
                        }
                    ]
                }
            ]
        }
    ],
    onBackClick: function () {
        this.parent.back();
    },

    onSaveClick: function (button, e, eOpts) {
        var self = this;
        var form = this.find('form').getForm();
        var grid = this.find('grid');
        var paramsGrid = this.find('params_grid');
        if (form.isValid()) {
            var processorModel = this.find('processor').getSelection();
            var data = form.getValues();

            var store = grid.getStore();
            if (store) {
                var gridData = store.getData();
                var values = [];
                gridData.each(function (item) {
                    values.push(item.getData());
                });
                data['rules'] = values;
            }

            var store = paramsGrid.getStore();
            if (store) {
                var gridData = store.getData();
                var values = [];
                if (gridData) {
                    gridData.each(function (item, index, len) {
                        values.push(item.getData());
                    });
                }
                data['values'] = values;
            }

            if (processorModel) {
                data['processorType'] = processorModel.get('type');
                if (data['processorType'] == 10) {
                    data['sid'] = this.find('email').getValue();
                }
                if (data['processorType'] == 20) {
                    data['sid'] = this.find('sms').getValue();
                }
                if (this.find('content' + data['processorType'])) {
                    data['content'] = this.find('content' + data['processorType']).getValue();
                }
                if (this.find('title' + data['processorType'])) {
                    data['title'] = this.find('title' + data['processorType']).getValue();
                }
            }

            if (this._data) {
                data['id'] = this._data['id'];
                Dialog.confirm('修改触发器', '确定修改当前触发器？', function (btn) {
                    self.apis.Trigger.updateTrigger
                        .wait(self, '正在修改触发器...')
                        .call({object: data}, function () {
                            self.parent.back();
                            if (self._callback) {
                                self._callback();
                            }
                        });
                });
            } else {
                Dialog.confirm('添加触发器', '确定添加当前触发器？', function (btn) {
                    self.apis.Trigger.addTrigger
                        .wait(self, '正在添加触发器...')
                        .call({object: data}, function () {
                            self.parent.back();
                            if (self._callback) {
                                self._callback();
                            }
                        });
                });
            }
        }
    },

    onCloseClick: function (button, e, eOpts) {
        this.close();
    },

    onRuleCellEditor: function (editor, e) {
        e.record.commit();
    },

    onUseRuleChange: function (field, newValue, oldValue, eOpts) {
        if (newValue['useRule'] == 1) {
            this.find('rule_container').show();
        } else {
            this.find('rule_container').hide();
        }
    },

    onSmsFieldChange: function (field, newValue, oldValue, eOpts) {
        var model = this.find('sms').getSelection();
        var type = model.get('type');
        if (type == 1 || type == '1') {
            this.find('content20').setEmptyText('请输入短信模板编号，比如：SMS_153055065');
            this.find('sms_container_label').setHtml(
                '<div class="basetitle">' +
                '   <div class="img">' +
                '       <img border="0" src="/admin/image/icon/base.png">' +
                '   </div>' +
                '   <div class="text">短信模板编号</div>' +
                '</div>');
        } else {
            this.find('content20').setEmptyText('请输入短信要发送的短信内容');
            this.find('sms_container_label').setHtml(
                '<div class="basetitle">' +
                '   <div class="img">' +
                '       <img border="0" src="/admin/image/icon/base.png">' +
                '   </div>' +
                '   <div class="text">短信内容</div>' +
                '</div>');
        }
    },

    onProcessorChange: function (field, newValue, oldValue, eOpts) {
        var model = this.find('processor').getSelection();
        var param = model.get('params');
        var type = model.get('type');
        var name = model.get('name');
        var value = model.get('value');
        this.find('email_container').hide();
        this.find('email').hide();
        this.find('sms_container').hide();
        this.find('sms').hide();
        this.find('msg_container').hide();
        this.find('params').hide();
        this.find('params_tips').hide();
        this.find('event_detail').show();
        this.find('event_detail').setValue('<span style="color:#999999">' + model.get('detail') + '</span>');

        if (type == 10) {
            this.find('email_container').show();
            this.find('email').show();
        } else if (type == 20) {
            this.find('sms_container').show();
            this.find('sms').show();
        } else if (type == 30) {
            this.find('msg_container').show();
        } else {
            this.find('params_tips').show();
            if (param) {
                this.find('params').show();
                var v = JSON.parse(param);
                if (v.length > 0) {
                    if (this._data && newValue == this._data['processor']) {
                        var values = this._data['TableTriggerValue'];
                        if (values) {
                            for (var i = 0; i < values.length; i++) {
                                var v = values[i];
                                for (var j = 0; j < param.length; j++) {
                                    if (param[j]['code'] == v['code']) {
                                        param[j]['value'] = v['value'];
                                    }
                                }
                            }
                        }
                    }
                    var store = Ext.create('Ext.data.Store', {
                        data: v
                    });
                    this.find('params_grid').setStore(store);
                } else {
                    this.find('params').hide();
                }
            }
        }
    },

    onEventComboboxChange: function (field, newValue, oldValue, eOpts) {
        var sel = field.getSelection();
        this.find('trigger_grid').hide();
        if (sel) {
            var params = sel.get('params');
            if (params && params.length > 0) {
                this.find('trigger_grid').show();
                if (Ext.isString(params)) {
                    params = JSON.parse(params);
                }

                var userSetParams = [];
                var contextParams = [];
                for (var i = 0; i < params.length; i++) {
                    userSetParams.push(params[i]);
                    if (params[i]['userSetValue']) {
                        contextParams.push('<div style="overflow:hidden">${' + params[i]['code'] + '} ：' + params[i]['detail'] + '</div>');
                    }
                }

                this.find('event_params').show();
                this.find('event_params').setValue(contextParams.join(''));

                for (var i = 0; i < userSetParams.length; i++) {
                    if (this._data) {
                        var rules = this._data['TableTriggerRule'];
                        if (rules && Ext.isArray(rules)
                            && newValue == this._data['event']) {
                            for (var j = 0; j < rules.length; j++) {
                                var r = rules[j];
                                if (userSetParams[i]['code'] == r['code']) {
                                    userSetParams[i]['symbol'] = r['symbol'];
                                    userSetParams[i]['value'] = r['value'];
                                }
                            }
                        }
                    }
                }
                var store = Ext.create('Ext.data.Store', {
                    data: userSetParams
                });
                this.find('grid').setStore(store);
            } else {
                this.find('event_params').setValue('');
                this.find('event_params').hide();
            }
        }
    },

    onTriggerInfiniteChange: function (field, newValue, oldValue, opts) {
        if (newValue['triggerInfinite'] == 1) {
            this.find('triggerCount').disable();
            this.find('totalCount').disable();
            this.find('triggerCount').setValue(0);
            this.find('totalCount').setValue(0);
        } else {
            this.find('triggerCount').enable();
            this.find('totalCount').enable();
        }
    },

    setInit: function () {
        var processor = this.find('processor');
        var store = this.apis.Trigger.getProcessorImpls.createListStore();
        processor.setStore(store);
        store.load();

        var events = this.find('event');
        var store = this.apis.Trigger.getEvents.createListStore();
        events.setStore(store);
        store.load();

        var store = this.apis.Email.getEnableEmails.createListStore();
        this.find('email').setStore(store);
        store.load();

        var store = this.apis.Sms.getEnableSms.createListStore();
        this.find('sms').setStore(store);
        store.load();
    },

    setValue: function (data) {
        this._data = data;
        if (data['startTime']) data['startTime'] = new Date(data['startTime']);
        if (data['finishTime']) data['finishTime'] = new Date(data['finishTime']);
        if (data['processorType'] == 10) data['email'] = data['sid'];
        if (data['processorType'] == 20) data['sms'] = data['sid'];
        var form = this.find('form').getForm();
        form.setValues(data);
        if (data['processorType'] && data['content'] && this.find('content' + data['processorType'])) {
            this.find('content' + data['processorType']).setValue(data['content']);
        }
        if (data['processorType'] && data['title'] && this.find('title' + data['processorType'])) {
            this.find('title' + data['processorType']).setValue(data['title']);
        }
    }

});
