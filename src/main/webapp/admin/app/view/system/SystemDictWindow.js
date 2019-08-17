Ext.define('App.system.SystemDictWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.Text',
        'Ext.form.RadioGroup',
        'Ext.form.field.Radio',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 190,
    width: 400,
    layout: 'fit',
    title: '添加字典数据',
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
                    fieldLabel: '字典名称',
                    allowBlank: false
                },
                {
                    xtype: 'textfield',
                    name: 'value',
                    anchor: '100%',
                    fieldLabel: '字典值',
                    allowBlank: false
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
            data.dictType = this._typeId;
            if (this._data) {
                this.apis.SystemDict.updateDict
                    .wait(self, '正在保存字典...')
                    .call({object: data}, function () {
                        if (self._callback) {
                            self._callback();
                        }
                    });
            } else {
                this.apis.SystemDict.addDict
                    .wait(self, '正在保存字典...')
                    .call({object: data}, function () {
                        if (self._callback) {
                            self._callback();
                        }
                    });
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
        this.setTitle('修改字典数据')
    }

});