Ext.define('App.file.DocManager', {
    extend: 'App.file.ImageManager',

    requires: ['App.file.ImageManager'],

    onAddTab: function () {
        this.callParent(arguments);
    }

});