Ext.define('App.wechat.WeChatOpenDetailPanel', {
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
                    xtype: 'displayfield',
                    name: 'name',
                    width: 800,
                    fieldLabel: '开发平台名称',
                    labelWidth: 150
                },
                {
                    xtype: 'displayfield',
                    name: 'code',
                    width: 800,
                    fieldLabel: '唯一标识码',
                    labelWidth: 150
                },
                {
                    xtype: 'displayfield',
                    name: 'appId',
                    width: 800,
                    fieldLabel: 'AppId',
                    labelWidth: 150
                },
                {
                    xtype: 'displayfield',
                    name: 'appSecret',
                    width: 800,
                    fieldLabel: 'AppSecret',
                    labelWidth: 150
                },
                {
                    xtype: 'displayfield',
                    name: 'appToken',
                    width: 800,
                    fieldLabel: '公众号消息校验Token',
                    labelWidth: 150
                },
                {
                    xtype: 'displayfield',
                    name: 'appKey',
                    width: 800,
                    fieldLabel: '公众号消息加解密Key',
                    labelWidth: 150
                },

                {
                    xtype: 'displayfield',
                    name: 'pc_link',
                    width: 800,
                    fieldLabel: '公众号授权地址(电脑)',
                    labelWidth: 150
                },
                {
                    xtype: 'imgfield',
                    name: 'mobile_qrcode',
                    width: 800,
                    height: 120,
                    fieldLabel: '公众号授权地址(手机)',
                    single: true,
                    labelWidth: 150
                },
                {
                    xtype: 'displayfield',
                    name: 'domain',
                    width: 800,
                    fieldLabel: '登录授权的发起页域名',
                    labelWidth: 150
                },
                {
                    xtype: 'displayfield',
                    name: 'auth_url',
                    width: 800,
                    fieldLabel: '授权事件接收URL',
                    labelWidth: 150
                },
                {
                    xtype: 'displayfield',
                    name: 'auth_msg_url',
                    width: 800,
                    fieldLabel: '消息与事件接收URL',
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
                    xtype: 'displayfield',
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
                    icon: Resource.png('jet', 'rollback'),
                    text: '返回第三方平台列表',
                    listeners: {
                        click: 'onCancelClick'
                    }
                }
            ]
        }
    ],

    onCancelClick: function (button, e, eOpts) {
        this.onBackClick();
    },

    onBackClick: function (button, e, eOpts) {
        this.parent.back();
    },

    setValue: function (data) {
        var self = this;
        var id = data['id'];
        var form = this.find('form').getForm();
        this.apis.WeChatOpen.getOpenDetail
            .wait(this, '正在加载第三方平台信息...')
            .call({id: id}, function (data) {
                form.setValues(data);
                var logo = self.find('logo');
                var mobileQrcode = self.find('mobile_qrcode');
                var pcLink = self.find('pc_link');
                if (data['logo']) {
                    logo.setValue(Resource.image(data['logo']));
                } else {
                    logo.setValue(Resource.image('/admin/image/exicon/nopic_120.gif'));
                }

                if (data['pc']) {
                    pcLink.setValue("<div style='width: 100%;overflow: hidden;text-align: left;font-size: 12px;font-weight: bold'>" +
                        "<a target='_blank' style='text-decoration: none;color: #0f74a8' href='" + data['pc'] + "'>点击打开授权地址(仅HTTPS)</a>" +
                        "</div>");
                }
                mobileQrcode.setValue('/create/qrcode.html?str=' + encodeURIComponent(data['mobile']));
            });
    }

});
