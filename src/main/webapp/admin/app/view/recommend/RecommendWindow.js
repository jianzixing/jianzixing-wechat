Ext.define('App.recommend.RecommendWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.grid.Panel',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'Ext.toolbar.Paging',
        'Ext.selection.CheckboxModel',
        'Ext.button.Button'
    ],

    height: 650,
    width: 1100,
    title: '推荐选择',
    defaultListenerScope: true,

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            items: [
                '->',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'inspectionsTypos'),
                    text: '确定选择',
                    listeners: {
                        click: 'onSaveClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'cancel'),
                    text: '取消',
                    listeners: {
                        click: 'onCancelClick'
                    }
                },
                '->'
            ]
        }
    ],

    onSaveClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.find(self._selectPanelName).getIgnoreSelects(arguments);
        if (datas) {
            if (self._callback) {
                self._callback(datas);
            }
            self.close();
        } else {
            Dialog.alert('请选中记录后再点击确认选择');
        }
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    getComponent: function (name) {
        return this.find(name);
    },

    onPanelItemClick: function (dataview, record, item, index, e, eOpts) {
        if (this._onItemClick) {
            this._onItemClick(this, record, item, index);
        }
    }

});