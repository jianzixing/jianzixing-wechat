Ext.define('App.system.SystemDictTypeWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.Text',
        'Ext.form.RadioGroup',
        'Ext.form.field.Radio',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 300,
    width: 400,
    layout: 'fit',
    title: '添加字典类型',
    defaultListenerScope: true,

    items: [
        {
            xtype: 'form',
            name: 'form',
            bodyPadding: 10,
            header: false,
            items: [
                {
                    xtype: 'hiddenfield',
                    name: 'id',
                    anchor: '100%'
                },
                {
                    xtype: 'textfield',
                    name: 'name',
                    anchor: '100%',
                    fieldLabel: '字典类型名称',
                    allowBlank: false
                },
                {
                    xtype: 'textfield',
                    name: 'table',
                    anchor: '100%',
                    fieldLabel: '关联表名',
                    allowBlank: false
                },
                {
                    xtype: 'textfield',
                    name: 'field',
                    anchor: '100%',
                    fieldLabel: '关联字段名',
                    allowBlank: false
                },
                {
                    xtype: 'radiogroup',
                    fieldLabel: '分类',
                    items: [
                        {
                            xtype: 'radiofield',
                            name: 'type',
                            boxLabel: '文本分类',
                            inputValue: '1',
                            checked: true
                        },
                        {
                            xtype: 'radiofield',
                            name: 'type',
                            inputValue: '2',
                            boxLabel: '状态分类'
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
                        click: 'onSave'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'cancel'),
                    text: '取消',
                    listeners: {
                        click: 'onCancel'
                    }
                },
                '->'
            ]
        }
    ],

    onSave: function (button, e, eOpts) {
        var self = this;
        var form = this.find('form').getForm();
        if (form.isValid()) {
            var data = form.getValues();
            if (this._copy) {
                this.apis.SystemDict.copyType
                    .wait(self, '正在复制类型...')
                    .call({object: data}, function () {
                        if (self._callback) {
                            self._callback();
                        }
                    });
            } else {
                if (this._data) {
                    this.apis.SystemDict.updateType
                        .wait(self, '正在修改类型...')
                        .call({object: data}, function () {
                            if (self._callback) {
                                self._callback();
                            }
                        });
                } else {
                    this.apis.SystemDict.addDictType
                        .wait(self, '正在添加字典类型...')
                        .call({object: data}, function () {
                            if (self._callback) {
                                self._callback();
                            }
                        });
                }
            }
            this.close();
        }
    },

    onCancel: function (button, e, eOpts) {
        this.close();
    },

    setValue: function (value) {
        this._data = value;
        this.find('form').getForm().setValues(value);
        this.setTitle('修改字典类型');
        this.find('table').disable();
        this.find('field').disable();
    },

    setCopy: function (value) {
        this._copy = value;
        this.find('form').getForm().setValues(value);
        this.setTitle('拷贝字典类型 - 请修改拷贝后的信息')
    }

});