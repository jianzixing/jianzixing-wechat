Ext.define('App.wechat.WeChatMaterialSelector', {
    extend: 'Ext.window.Window',

    requires: [
        'App.wechat.WeChatMaterialManager',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 600,
    width: 900,
    defaultListenerScope: true,
    title: '选中微信素材',

    layout: 'fit',
    items: [
        {
            xtype: 'wechat_material_manager',
            name: 'wechat_material',
        }
    ],
    dockedItems: [
        {
            xtype: 'toolbar',
            flex: 1,
            dock: 'bottom',
            items: [
                '->',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'checkSpelling_dark'),
                    text: '确定选择',
                    listeners: {
                        click: 'onSelectClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'cancel'),
                    text: '取消关闭',
                    listeners: {
                        click: 'onCancelClick'
                    }
                },
                '->'
            ]
        }
    ],

    onSelectClick: function (button, e, eOpts) {
        var datas = this.find('wechat_material').getDataSelects();
        this.close();
        if (this._callback) {
            this._callback(datas);
        }
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    setInit: function () {
        this.find('wechat_material').apis = this.apis;
        this.find('wechat_material').accountData = this.accountData;
        this.find('wechat_material').openType = this.openType;
        this.find('wechat_material').materialType = this.materialType;
        this.find('wechat_material').onAfterApply();

        this.find('image_button').hide();
        this.find('video_button').hide();
        this.find('voice_button').hide();
        this.find('news_button').hide();
        this.find('delete_button').hide();
        if (this.materialType == 3) {
            this.find('image_button').show();
        }
        if (this.materialType == 4) {
            this.find('voice_button').show();
        }
        if (this.materialType == 5) {
            this.find('video_button').show();
        }
    },

    setMode: function (type) {
        this.find('wechat_material').getSelectionModel().setSelectionMode(type);
    }

});
