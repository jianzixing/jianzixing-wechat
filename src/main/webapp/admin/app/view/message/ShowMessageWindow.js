Ext.define('App.message.ShowMessageWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.container.Container',
        'App.message.MessageDetail'
    ],

    height: 600,
    width: 900,
    layout: 'fit',
    title: '查看消息',

    items: [
        {
            xtype: 'messagedetail',
            name: 'messagedetail',
        }
    ],

    setValue: function (id) {
        if (id) {
            this.find('messagedetail').setWindowView({window: this, toolbarMenuView: this.toolbarMenuView});
            this.find('messagedetail').setValue(id);
        }
    }

});