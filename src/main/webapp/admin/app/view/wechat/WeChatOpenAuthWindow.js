Ext.define('App.wechat.WeChatOpenAuthWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.panel.Panel',
        'Ext.form.FieldSet',
        'Ext.Img',
        'Ext.button.Button'
    ],

    autoShow: true,
    height: 450,
    width: 630,
    layout: 'border',
    title: '授权地址',
    defaultListenerScope: true,

    items: [
        {
            xtype: 'panel',
            flex: 1,
            region: 'center',
            border: false,
            header: false,
            items: [
                {
                    xtype: 'fieldset',
                    height: 300,
                    margin: 20,
                    title: '手机端授权',
                    items: [
                        {
                            xtype: 'image',
                            name: 'image',
                            height: 201,
                            margin: 20,
                            width: 201
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'panel',
            flex: 1,
            region: 'west',
            border: false,
            width: 150,
            header: false,
            items: [
                {
                    xtype: 'fieldset',
                    height: 300,
                    margin: 20,
                    title: '电脑端授权',
                    layout: {
                        type: 'hbox',
                        align: 'middle',
                        pack: 'center'
                    },
                    items: [
                        {
                            xtype: 'container',
                            name: 'link_url',
                            flex: 1,
                            html: ''
                        }
                    ]
                }
            ]
        }
    ],

    setValue: function (data) {
        var self = this;
        var id = data['id'];
        this.apis.WeChatOpen.getOpenUrls
            .wait(this, '正在获取授权地址...')
            .call({id: id}, function (data) {
                self._data = data;
                self.find('image').setSrc('/create/qrcode.jhtml?str=' + encodeURIComponent(data['mobile']));
                if (data['pc']) {
                    self.find('link_url').setHtml(
                        "<div style='width: 100%;overflow: hidden;text-align: center;font-size: 15px;font-weight: bold'>" +
                        "<a target='_blank' style='text-decoration: none;color: #0f74a8' href='" + data['pc'] + "'>点击打开授权地址(仅HTTPS)</a>" +
                        "</div>");
                } else {
                    self.find('link_url').setHtml(
                        "<div style='width: 100%;overflow: hidden;text-align: center;font-size: 15px;font-weight: bold'>" +
                        "获取PC授权地址失败(检查配置)" +
                        "</div>");
                }
            });
    }

});
