Ext.define('App.goods.ColorSelectorWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'UXApp.colorpick.Selector'
    ],

    height: 500,
    width: 700,
    layout: 'fit',
    title: '颜色选择器',
    defaultListenerScope: true,

    items: [
        {
            xtype: 'colorselector',
            name: 'color'
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
                    text: '确定选择',
                    icon: Resource.png('jet', 'get'),
                    listeners: {
                        click: 'onSelectClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'closeHover'),
                    text: '取消选择',
                    listeners: {
                        click: 'onCancelClick'
                    }
                },
                '->'
            ]
        }
    ],

    onSelectClick: function (button, e, eOpts) {
        var cs = this.find('color');
        var color = cs.value;

        if (this._callback) {
            this._callback(color);
            this.close();
        }
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    }

});