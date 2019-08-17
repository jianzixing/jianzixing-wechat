Ext.define('App.wechat.WeChatOpenPanel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.Text',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    border: false,
    header: false,
    defaultListenerScope: true,
    layout: 'fit',

    items: [
        {
            xtype: 'form',
            border: false,
            bodyPadding: "10 30 30 30",
            name: 'form',
            header: false,
            autoScroll: true,
            items: [
                {
                    xtype: 'label',
                    margin: '20px auto 20px auto',
                    style: {
                        display: 'block'
                    },
                    html: '<div class="basetitle"> <div class="img"><img border="0" src="/admin/image/icon/base.png"></div><div class="text">第三方平台配置</div> </div>'
                },
                {
                    xtype: 'textfield',
                    name: 'name',
                    width: 800,
                    fieldLabel: '开发平台名称',
                    labelWidth: 150,
                    allowBlank: false,
                    blankText: '开发平台名称必须填写'
                },
                {
                    xtype: 'textfield',
                    name: 'code',
                    width: 800,
                    fieldLabel: '唯一标识码',
                    labelWidth: 150,
                    regex: /^[A-Za-z0-9]+$/,
                    regexText: '唯一标识码只能是字母或者数字或者字母数字组合'
                },
                {
                    xtype: 'textfield',
                    name: 'appId',
                    width: 800,
                    fieldLabel: 'AppId',
                    labelWidth: 150,
                    allowBlank: false,
                    blankText: '开发平台appid必须填写'
                },
                {
                    xtype: 'textfield',
                    name: 'appSecret',
                    width: 800,
                    fieldLabel: 'AppSecret',
                    labelWidth: 150,
                    allowBlank: false,
                    blankText: '开发平台secret必须填写'
                },
                {
                    xtype: 'textfield',
                    name: 'appToken',
                    width: 800,
                    fieldLabel: '公众号消息校验Token',
                    labelWidth: 150
                },
                {
                    xtype: 'textfield',
                    name: 'appKey',
                    width: 800,
                    fieldLabel: '公众号消息加解密Key',
                    labelWidth: 150
                },
                {
                    xtype: 'label',
                    margin: '20px auto 20px auto',
                    style: {
                        display: 'block'
                    },
                    html: '<div class="basetitle"><div class="img"><img border="0" src="/admin/image/icon/base.png"> </div> <div class="text">其他配置</div> </div>'
                },
                {
                    xtype: 'imgfield',
                    name: 'logo',
                    width: 800,
                    height: 120,
                    fieldLabel: '头像',
                    single: true,
                    addSrc: Resource.png('ex', 'add_img'),
                    replaceSrc: Resource.png('ex', 'replace_img'),
                    listeners: {
                        addimage: 'onSelectImage1Click'
                    },
                    labelWidth: 150
                },
                {
                    xtype: 'radiogroup',
                    fieldLabel: '是否启用',
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
                    ],
                    labelWidth: 150
                },
                {
                    xtype: 'textareafield',
                    name: 'detail',
                    width: 800,
                    fieldLabel: '描述',
                    labelWidth: 150
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
                    icon: Resource.png('jet', 'back'),
                    text: '返回列表',
                    listeners: {
                        click: 'onBackClick'
                    }
                }
            ]
        },
        {
            xtype: 'toolbar',
            dock: 'bottom',
            items: [
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'menu-saveall'),
                    text: '确定保存',
                    listeners: {
                        click: 'onSaveClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'redraw',
                    icon: Resource.png('jet', 'Reset_to_empty'),
                    text: '重新填写',
                    listeners: {
                        click: 'onReloadClick'
                    }
                },
                '|',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'rollback'),
                    text: '返回第三方平台列表',
                    listeners: {
                        click: 'onCancelClick'
                    }
                }
            ]
        }
    ],

    onSaveClick: function (button, e, eOpts) {
        var self = this;
        var form = this.find('form').getForm();
        if (form.isValid()) {
            var data = form.getValues();
            var logo = this.find('logo');
            if (logo) data['logo'] = logo._fileName;
            if (self._data) {
                data['id'] = self._data['id'];
                Dialog.confirm('修改', '确定修改第三方平台吗？', function (btn) {
                    if (btn == 'yes') {
                        self.apis.WeChatOpen.updateOpen
                            .wait('正在修改第三方平台...')
                            .call({object: data}, function () {
                                self.onBackClick();
                                if (self._callback) {
                                    self._callback();
                                }
                            })
                    }
                })
            } else {
                Dialog.confirm('添加', '确定添加第三方平台吗？', function (btn) {
                    if (btn == 'yes') {
                        self.apis.WeChatOpen.addOpen
                            .wait('正在添加第三方平台...')
                            .call({object: data}, function () {
                                self.onBackClick();
                                if (self._callback) {
                                    self._callback();
                                }
                            })
                    }
                })
            }
        }
    },

    onSelectImage1Click: function (button) {
        var parent = button.ownerCt;
        var image = parent.find('logo');

        var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
        win.setSelectionCallback(function (files) {
            if (files) {
                image._fileName = files['fileName'];
                image.setValue(files['fileName']);
            }
        })
    },

    onCancelClick: function (button, e, eOpts) {
        this.onBackClick();
    },

    onBackClick: function (button, e, eOpts) {
        this.parent.back();
    },

    onReloadClick: function (button, e, eOpts) {
        this.parent.redraw();
    },

    setValue: function (data) {
        this._data = data;
        this.find('redraw').hide();
        var form = this.find('form').getForm();
        form.setValues(data);
        var logo = this.find('logo');
        logo.setValue(Resource.image(data['logo']));
    }

});