Ext.define('App.cooperation.AdvertisingWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.Text',
        'Ext.form.RadioGroup',
        'Ext.form.field.Radio',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 500,
    width: 700,
    layout: 'fit',
    title: '添加广告',
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
                    fieldLabel: '广告名称*',
                    allowBlank: false
                },
                {
                    xtype: 'textfield',
                    name: 'code',
                    anchor: '100%',
                    fieldLabel: '广告码(A-Z0-9)*',
                    allowBlank: false,
                    regex: /^[A-Za-z0-9]+$/,
                    listeners: {
                        change: 'onTextfieldChange'
                    }
                },
                {
                    xtype: 'textfield',
                    name: 'text',
                    anchor: '100%',
                    fieldLabel: '广告文字'
                },
                {
                    xtype: 'fieldcontainer',
                    anchor: '100%',
                    layout: 'table',
                    fieldLabel: '新闻封面',
                    items: [
                        {
                            xtype: 'image',
                            name: 'image',
                            style: {
                                maxWidth: '120px',
                                maxHeight: '120px'
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
                    name: 'url',
                    anchor: '100%',
                    fieldLabel: '链接地址'
                },
                {
                    xtype: 'textfield',
                    name: 'script',
                    anchor: '100%',
                    fieldLabel: '脚本链接'
                },
                {
                    xtype: 'radiogroup',
                    fieldLabel: '是否有效',
                    items: [
                        {
                            xtype: 'radiofield',
                            name: 'enable',
                            boxLabel: '是',
                            checked: true,
                            inputValue: '1'
                        },
                        {
                            xtype: 'radiofield',
                            name: 'enable',
                            boxLabel: '否',
                            inputValue: '0'
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
                    text: '保存',
                    icon: Resource.png('jet', 'saveTempConfig'),
                    listeners: {
                        click: 'onSaveClick'
                    }
                },
                {
                    xtype: 'button',
                    text: '取消',
                    icon: Resource.png('jet', 'cancel'),
                    listeners: {
                        click: 'onCancelClick'
                    }
                },
                '->'
            ]
        }
    ],

    onSelectImageClick: function (button, e, eOpts) {
        var self = this;
        var field = this.find('cover');
        var image = this.find('image');
        var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
        win.setSelectionCallback(function (files) {
            image.setSrc(Resource.image(files.fileName));
            field.setValue(files.fileName);
        }, true);
    },

    onSaveClick: function (button, e, eOpts) {
        var self = this;
        var form = this.find('form');
        if (form.isValid()) {
            var data = form.getForm().getValues();
            if (self._data) {
                data['id'] = self._data['id'];
                self.apis.Advertising.updateAdvertising
                    .wait(self, '正在修改广告...')
                    .call({object: data}, function () {
                        self.close();
                        if (self._callback) {
                            self._callback();
                        }
                    });
            } else {
                self.apis.Advertising.addAdvertising
                    .wait(self, '正在添加广告...')
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

    onTextfieldChange: function (field, newValue, oldValue, eOpts) {
        field.setValue(field.getValue().toUpperCase());
    },

    setValue: function (value) {
        this._data = value;
        var form = this.find('form');
        form.getForm().setValues(value);
        if (value['cover']) {
            this.find('image').setSrc(Resource.image(value['cover']));
        }
    }

});