Ext.define('App.goods.GoodsEditTitleWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.TextArea',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 250,
    width: 600,
    layout: 'fit',
    title: '修改商品标题',
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
                    fieldLabel: '商品名称',
                    allowBlank: false
                },
                {
                    xtype: 'textareafield',
                    name: 'subtitle',
                    anchor: '100%',
                    height: 80,
                    fieldLabel: '商品副标题'
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
                    text: '确定保存',
                    icon: Resource.png('jet', 'menu-saveall'),
                    listeners: {
                        click: 'onSaveClick'
                    }
                },
                {
                    xtype: 'button',
                    text: '取消关闭',
                    icon: Resource.png('jet', 'closeActive'),
                    listeners: {
                        click: 'onCloseClick'
                    }
                },
                '->'
            ]
        }
    ],

    onSaveClick: function (button, e, eOpts) {
        var form = this.find('form').getForm();
        var values = form.getValues();
        if (form.isValid()) {
            var self = this;
            Dialog.confirm('提示', '确定修改商品标题？', function (btn) {
                if (btn == 'yes') {
                    self.close();
                    if (self.callback) {
                        self.callback(values)
                    }
                }
            });

        }
    },

    onCloseClick: function (button, e, eOpts) {
        this.close();
    },

    setGoodsValue: function (json) {
        this.find('form').getForm().setValues(json);
    }

});