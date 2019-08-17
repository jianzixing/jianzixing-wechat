Ext.define('UXApp.window.Selector', {
    extend: 'Ext.window.Window',
    alias: 'widget.productclassifywindow',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.TextArea',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 100,
    width: 345,
    header: false,
    frame: true,
    //shadow: false,
    border: false,
    resizable: false,
    constrainHeader: true,
    layout: 'fit',
    defaultListenerScope: true,
    dockedItems: [
        {
            xtype: 'toolbar',
            hidden: true,
            dock: 'bottom',
            border: false,
            items: []
        }
    ],
    constructor: function (config) {
        this.callParent([config]);
    },

    initEvents: function () {
        var me = this;
        me.callParent();
        var bodyClose = function () {
            me.close();
            document.body.removeEventListener('click', bodyClose);
        };
        document.body.addEventListener('click', bodyClose);
        me.isAddBodyEvent = false;
        me.el.dom.onmousemove = function (e) {
            if (!me.isAddBodyEvent) {
                document.body.removeEventListener('click', bodyClose);
                me.isAddBodyEvent = true;
            }
        }
        me.el.dom.onmouseout = function (e) {
            if (me.isAddBodyEvent) {
                document.body.addEventListener('click', bodyClose);
                me.isAddBodyEvent = false;
            }
        }
    },

    setValue: function (value) {
        if (value instanceof Ext.data.Model) {
            if (this.callback)this.callback(value);
        } else {
            var model = Ext.create('Ext.data.Model');
            for (var i in value) {
                model.set(i, value);
            }
            if (this.callback)this.callback(model);
        }
        this.close();
    }
});