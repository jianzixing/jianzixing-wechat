Ext.define('App.message.MessageTabDetail', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'App.message.MessageDetail'
    ],

    border: false,
    layout: 'fit',
    header: false,
    defaultListenerScope: true,

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
        }
    ],
    items: [
        {
            xtype: 'messagedetail',
            name: 'messagedetail',
        }
    ],

    onBackClick: function (button, e, eOpts) {
        this.parent.back();
    },

    setValue: function (data) {
        var messagedetail = this.find('messagedetail');
        messagedetail.setValue(data);
    }

});