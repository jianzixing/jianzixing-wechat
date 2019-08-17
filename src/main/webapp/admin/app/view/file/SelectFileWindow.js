Ext.define('App.file.SelectFileWindow', {
    extend: 'Ext.window.Window',
    height: 600,
    width: 1100,
    layout: 'fit',
    title: '选择文件',
    constrainHeader: true,
    requires: ['App.file.FileExplorer'],
    defaultListenerScope: true,
    items: [
        {
            xtype: 'filexplorer',
            name: 'fileExplorer'
        }
    ],
    listeners: {
        show: 'onShowWindow'
    },

    onShowWindow: function (button, e, options) {
        if (!this.isShowwing) {
            var me = this;
            this.fileExplorer = this.find('fileExplorer');
            this.fileExplorer.onAfterApply(this.selectOne, this.groupSourceType);
            this.fileExplorer.onSelectWindow(function (fs) {
                me.close();
                if (me.callback) me.callback(fs);
            });
            this.isShowwing = true;
        }
    },

    setSelectionCallback: function (fun, mustOne) {
        var me = this;
        if (this.fileExplorer) {
            this.fileExplorer.onSelectWindow(function (files) {
                me.close();
                if (fun) fun(files);
            });
        }
    }

});