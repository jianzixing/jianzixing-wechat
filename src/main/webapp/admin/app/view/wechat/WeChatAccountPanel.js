Ext.define('App.wechat.WeChatAccountPanel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.ComboBox',
        'Ext.form.RadioGroup',
        'Ext.form.field.Radio',
        'Ext.form.field.TextArea',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    title: '添加公众号',
    header: false,
    defaultListenerScope: true,
    layout: 'fit',

    // 1.设置公众号信息
    // 2.设置权限
    // 3.引导页面
    items: [
        {
            xtype: 'form',
            name: 'form',
            border: false,
            bodyPadding: 30,
            header: false,
            autoScroll: true,
            items: [
                {
                    xtype: 'label',
                    margin: '0px auto 20px auto',
                    style: {
                        display: 'block'
                    },
                    html: '<div class="basetitle"> <div class="img"><img border="0" src="/admin/image/icon/base.png"> </div> <div class="text">微信公众号配置</div> </div>'
                },
                {
                    xtype: 'textfield',
                    name: 'name',
                    width: 800,
                    fieldLabel: '公众号名称' + Color.string('*', 'red'),
                    allowBlank: false,
                    blankText: '公众号名称必须填写'
                },
                {
                    xtype: 'combobox',
                    name: 'type',
                    width: 800,
                    fieldLabel: '公众号类型' + Color.string('*', 'red'),
                    displayField: 'name',
                    valueField: 'id',
                    editable: false,
                    store: {
                        data: [
                            {id: 1, name: '普通订阅号'},
                            {id: 2, name: '普通服务号'},
                            {id: 3, name: '认证订阅号'},
                            {id: 4, name: '认证服务号/认证媒体/政府订阅号'}
                        ]
                    }
                },
                {
                    xtype: 'textfield',
                    name: 'code',
                    width: 800,
                    fieldLabel: '唯一标识码' + Color.string('*', 'red'),
                    regex: /^[A-Za-z0-9]+$/,
                    regexText: '唯一标识码只能是字母或者数字或者字母数字组合'
                },
                {
                    xtype: 'textfield',
                    name: 'weAccount',
                    width: 800,
                    fieldLabel: '微信号'
                },
                {
                    xtype: 'textfield',
                    name: 'originalId',
                    width: 800,
                    fieldLabel: '原始ID'
                },
                {
                    xtype: 'textfield',
                    name: 'appId',
                    width: 800,
                    fieldLabel: 'AppID' + Color.string('*', 'red'),
                    allowBlank: false,
                    blankText: '公众号appid必须填写'
                },
                {
                    xtype: 'textfield',
                    name: 'appSecret',
                    width: 800,
                    fieldLabel: 'AppSecret' + Color.string('*', 'red'),
                    allowBlank: false,
                    blankText: '公众号secret必须填写'
                },
                {
                    xtype: 'textfield',
                    name: 'appToken',
                    width: 800,
                    fieldLabel: 'Token' + Color.string('*', 'red'),
                    allowBlank: false,
                    blankText: '公众号Token必须填写'
                },
                {
                    xtype: 'textfield',
                    name: 'appEncodingKey',
                    width: 800,
                    fieldLabel: 'EncodingAESKey'
                },
                {
                    xtype: 'combobox',
                    name: 'encodingType',
                    width: 800,
                    fieldLabel: '消息加密方式',
                    displayField: 'name',
                    valueField: 'id',
                    editable: false,
                    store: {
                        data: [
                            {id: 0, name: '明文模式'},
                            {id: 1, name: '兼容模式'},
                            {id: 2, name: '安全模式'}
                        ]
                    }
                },
                {
                    xtype: 'label',
                    margin: '20px auto 20px auto',
                    style: {
                        display: 'block'
                    },
                    html: '<div class="basetitle"> <div class="img"><img border="0" src="/admin/image/icon/base.png"> </div> <div class="text">其他配置</div> </div>'
                },
                {
                    xtype: 'fieldcontainer',
                    width: 800,
                    height: 55,
                    html: '<span style="color:#999999">在微信公众号请求用户网页授权之前，开发者需要先到公众平台官网中的' +
                        '“开发 - 接口权限 - 网页服务 - 网页帐号 - 网页授权获取用户基本信息”的配置选项中，修改授权回调域名。' +
                        '请注意，这里填写的是域名（是一个字符串），而不是URL，因此请勿加 http:// 等协议头</span>',
                    fieldLabel: '<span style="color:#999999">Oauth 2.0</span>'
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
                    }
                },
                {
                    xtype: 'imgfield',
                    name: 'qrCode',
                    width: 800,
                    height: 120,
                    fieldLabel: '二维码',
                    single: true,
                    addSrc: Resource.png('ex', 'add_img'),
                    replaceSrc: Resource.png('ex', 'replace_img'),
                    listeners: {
                        addimage: 'onSelectImage2Click'
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
                    xtype: 'textareafield',
                    name: 'detail',
                    width: 800,
                    fieldLabel: '描述'
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
                    text: '保存公众号',
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
                    text: '返回公众号列表',
                    listeners: {
                        click: 'onBackClick'
                    }
                }
            ]
        }
    ],

    onBackClick: function (button, e, eOpts) {
        this.parent.back();
    },

    onSaveClick: function (button, e, eOpts) {
        var self = this;
        var headImage = this.find('logo');
        var qrcodeImage = this.find('qrCode');
        var form = this.find('form').getForm();

        if (form.isValid()) {
            var data = this.find('form').getForm().getValues();
            if (headImage) data['logo'] = headImage._fileName;
            if (qrcodeImage) data['qrCode'] = qrcodeImage._fileName;

            if (self._data) {
                data['id'] = self._data['id'];
                Dialog.confirm('确定修改', '确定修改公众号吗？', function (btn) {
                    if (btn == 'yes') {
                        self.apis.WeChatPublic.updateAccount
                            .wait(self, '正在更新公众号信息...')
                            .call({object: data}, function () {
                                self.onBackClick();
                                if (self._callback) {
                                    self._callback();
                                }
                            })
                    }
                });
            } else {
                Dialog.confirm('确定添加', '确定添加公众号吗？', function (btn) {
                    if (btn == 'yes') {
                        self.apis.WeChatPublic.addAccount
                            .wait(self, '正在添加公众号信息...')
                            .call({object: data}, function () {
                                self.onBackClick();
                                if (self._callback) {
                                    self._callback();
                                }
                            })
                    }
                });
            }
        }
    },

    onReloadClick: function (button, e, eOpts) {
        this.parent.redraw();
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

    onSelectImage2Click: function (button) {
        var parent = button.ownerCt;
        var image = parent.find('qrCode');

        var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
        win.setSelectionCallback(function (files) {
            if (files) {
                image._fileName = files['fileName'];
                image.setValue(files['fileName']);
            }
        })
    },

    setValue: function (data) {
        this._data = data;
        this.setTitle('修改公众号');
        var form = this.find('form');
        form.getForm().setValues(data);
        var headImage = this.find('logo');
        var qrcodeImage = this.find('qrCode');
        if (data['logo']) {
            headImage._fileName = data['logo'];
            headImage.setValue(Resource.image(data['logo']));
        }
        if (data['qrCode']) {
            qrcodeImage._fileName = data['qrCode'];
            qrcodeImage.setValue(Resource.image(data['qrCode']));
        }
        this.find('redraw').hide();
    }

});
