Ext.define('UXApp.panel.WindowFormDetail', {
    extend: 'Ext.window.Window',

    requires: [
        'UXApp.panel.FormDetail',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 600,
    width: 800,
    layout: 'fit',
    title: '查看详情',
    defaultListenerScope: true,

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            items: [
                '->',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'closeInactive'),
                    text: '关闭查看',
                    listeners: {
                        click: 'onOKClick'
                    }
                },
                '->'
            ]
        }
    ],
    items: [
        {
            xtype: 'formdetail',
            name: '_form_detail',
            title: '订单基本信息'
        }
    ],

    onOKClick: function (button, e, eOpts) {
        this.close();
    },

    onShow: function () {
        this.callParent(arguments);
        var fd = this.find('_form_detail');
        fd.setValues(this.datas)
    }

});