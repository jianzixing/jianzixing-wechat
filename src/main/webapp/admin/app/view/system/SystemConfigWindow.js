Ext.define('App.system.SystemConfigWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.form.Panel',
        'Ext.form.field.ComboBox',
        'Ext.form.FieldContainer',
        'Ext.form.field.TextArea'
    ],

    height: 450,
    width: 700,
    layout: 'fit',
    title: '添加配置项',
    defaultListenerScope: true,

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            items: [
                '->',
                {
                    xtype: 'button',
                    name: 'buttonSave',
                    icon: Resource.png('jet', 'saveTempConfig'),
                    text: '保存',
                    listeners: {
                        click: 'onSaveClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'buttonCancel',
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
    items: [
        {
            xtype: 'form',
            name: 'form',
            border: false,
            bodyPadding: 10,
            items: [
                {
                    xtype: 'textfield',
                    name: 'name',
                    anchor: '100%',
                    fieldLabel: '配置名称'
                },
                {
                    xtype: 'textfield',
                    name: 'key',
                    anchor: '100%',
                    fieldLabel: '配置键'
                },
                {
                    xtype: 'combobox',
                    name: 'type',
                    anchor: '100%',
                    fieldLabel: '配置类型',
                    listeners: {
                        change: 'onComboboxChange'
                    },
                    displayField: 'name',
                    valueField: 'type',
                    editable: false,
                    store: {
                        data: [
                            {name: '文本类型', type: 0},
                            {name: '图片类型', type: 1}
                        ]
                    }
                },
                {
                    xtype: 'fieldcontainer',
                    hidden: true,
                    name: 'image',
                    anchor: '100%',
                    layout: 'table',
                    margin: '0 auto 10px auto',
                    width: "100%",
                    fieldLabel: '配置值',
                    items: [
                        {
                            xtype: 'image',
                            name: 'imageLogo',
                            style: {
                                maxWidth: '120px',
                                maxHeight: '90px'
                            },
                            src: '/admin/image/exicon/nopic_120.gif',
                        },
                        {
                            xtype: 'button',
                            margin: 'auto auto auto 100px',
                            text: '选择图片文件',
                            listeners: {
                                click: 'onSelectImageClick'
                            }
                        },
                        {
                            xtype: 'hiddenfield',
                            name: 'cover'
                        }
                    ]
                },
                {
                    xtype: 'textfield',
                    name: 'value',
                    anchor: '100%',
                    fieldLabel: '配置值'
                },
                {
                    xtype: 'textfield',
                    name: 'pos',
                    anchor: '100%',
                    fieldLabel: '排序',
                    inputType: 'number'
                },
                {
                    xtype: 'textareafield',
                    name: 'detail',
                    anchor: '100%',
                    fieldLabel: '配置描述'
                }
            ]
        }
    ],

    onSaveClick: function (button, e, eOpts) {
        var self = this;
        var form = this.find('form').getForm();
        if (form.isValid()) {
            var data = form.getValues();
            if (data['type'] == 1) {
                data['value'] = self.find('cover').getValue();
            }
            data['gid'] = this._groupId;
            if (self._data) {
                data['id'] = self._data['id'];
                data['key'] = self._data['key'];
                self.apis.SystemConfig.updateConfig
                    .disable(self, ['buttonSave', 'buttonCancel'])
                    .wait(self, '正在修改系统配置项...')
                    .call({object: data}, function () {
                        self.close();
                        if (self._callback) {
                            self._callback();
                        }
                    });
            } else {
                self.apis.SystemConfig.addConfig
                    .disable(self, ['buttonSave', 'buttonCancel'])
                    .wait(self, '正在添加系统配置项...')
                    .call({object: data}, function () {
                        self.close();
                        if (self._callback) {
                            self._callback();
                        }
                    });
            }
        }
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    onComboboxChange: function (field, newValue, oldValue, eOpts) {
        if (newValue == 1) {
            this.find('image').show();
            this.find('value').hide();
        } else {
            this.find('value').show();
            this.find('image').hide();
        }
    },

    onSelectImageClick: function (button, e, eOpts) {
        var self = this;
        var field = this.find('cover');
        var image = this.find('imageLogo');
        var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
        win.setSelectionCallback(function (files) {
            image.setSrc(Resource.image(files.fileName));
            field.setValue(files.fileName);
        }, true);
    },

    setValue: function (value) {
        this._data = value;
        var form = this.find('form').getForm();
        form.setValues(value);
        this.find('key').disable();
        this.setTitle('修改配置项');
        if (value['type'] == 1) {
            this.find('cover').setValue(value['value']);
            this.find('imageLogo').setSrc(Resource.image(value['value']))
        }
    }

});