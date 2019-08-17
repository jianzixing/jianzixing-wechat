Ext.define('App.file.VideoManager', {
    extend: 'App.file.ImageManager',

    requires: ['App.file.ImageManager'],

    onAddTab: function () {
        this.callParent(arguments);
    }

});