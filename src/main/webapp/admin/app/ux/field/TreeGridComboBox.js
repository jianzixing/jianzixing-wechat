Ext.define('UXApp.field.TreeGridComboBox', {
    extend: 'Ext.form.field.ComboBox',
    alias: 'widget.treegridcombobox',

    multiSelect: false,  //控制单选,多选
    displayField: 'text',
    valueField: 'text',
    store: {
        data: []
    },

    onUnbindStore: function () {
        var me = this,
            picker = me.picker;
        // If we'd added a local filter, remove it.
        // Listeners are unbound, so we don't need the changingFilters flag
        if (me.queryFilter && !me.store.destroyed) {
            me.clearLocalFilter();
        }
        if (picker && picker.unbindStore) {
            picker.unbindStore();
        }
        me.pickerSelectionModel.destroy();
    },

    setTreeGridStore: function (store) {
        if (store) {
            this.treeGridStore = store;
        }
    },

    setComboBoxStoreData: function (data) {
        var store = this.getStore();
        store.removeAll();
        store.add(data);
    },

    createPicker: function () {
        var me = this;

        if (this.treeGridStore) {
            var store = this.treeGridStore;
            var treeConfig = {
                store: store,
                useArrows: true,
                selModel: {
                    mode: me.multiSelect ? 'SIMPLE' : 'SINGLE'
                },
                floating: true,
                refresh: function () {
                    me.onListRefresh(arguments)
                }
            };

            if (this.treePanelConfig) {
                for (var i in this.treePanelConfig) {
                    treeConfig[i] = this.treePanelConfig[i];
                }
            }

            var picker = Ext.create('Ext.tree.Panel', treeConfig);

            me.mon(picker, {
                itemclick: me.onItemClick,
                scope: me
            });

            this.picker = picker;
            return picker;
        }
        return null;
    },

    onItemClick: function (dataview, record, item, index, e, eOpts) {
        var data = record.getData();
        var store = this.getStore();
        store.removeAll();
        store.add(data);
        this.setValue(record.getData()[this.valueField]);
        this.fireEvent('itemsclick', this, data);
    },

    onListRefresh: function () {

    }
});