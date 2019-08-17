Ext.define('UXApp.field.GridComboBox', {
    extend: 'Ext.form.field.ComboBox',
    alias: 'widget.gridcombobox',

    multiSelect: false,  //控制单选,多选
    displayField: 'text',
    valueField: 'text',
    store: {
        data: []
    },
    defaultListenerScope: true,
    listeners: {
        change: 'onSelfChange',
        focus: 'onSelfFocus',
        specialkey: 'onSelfKeyUp'
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

    onSelfChange: function (self, newValue, oldValue) {
        if (this._current_item_click) {
            this._current_item_click = false;
            return;
        }
        if (this.gridStore && this.searchQuery) {
            var ext = {};
            if (this.gridStore.proxy) {
                ext = this.gridStore.proxy.extraParams;
            }
            ext[this.searchQueryField] = newValue;
            this.gridStore.load(ext);
            this.expand();
            this.focus();
        }
    },

    onSelfFocus: function () {
        if (this._current_item_click) {
            this._current_item_click = false;
            return;
        }
        if (!this.isExpanded && this.searchQuery && this.gridStore) {
            this.expand();
            this.focus();
        }
    },

    onSelfKeyUp: function (field, e) {
        if (e.getKey() == Ext.EventObject.BACKSPACE && this.searchQuery) {
            var self = this;
            var d = new Ext.util.DelayedTask(function () {
                self.onSelfFocus();
            });
            d.delay(100);
        }
    },

    setGridStore: function (store) {
        if (store) {
            this.gridStore = store;
        }
    },

    setPaging: function (is) {
        this.showPagingView = is;
    },

    setComboBoxStoreData: function (data, defaultSelect) {
        var store = this.getStore();
        store.removeAll();
        store.add(data);
        if (this.valueField && defaultSelect) {
            this.setValue(data[this.valueField]);
        }
    },

    createPicker: function () {
        var me = this;

        if (this.gridStore) {
            var store = this.gridStore;
            var treeConfig = {
                store: store,
                selModel: {
                    mode: me.multiSelect ? 'SIMPLE' : 'SINGLE'
                },
                floating: true,
                refresh: function () {
                    me.onListRefresh(arguments)
                },
                bindStore: function () {
                    // 有bug只能这样避免出错
                    try {
                        this.callParent(arguments)
                    } catch (e) {
                    }
                }
            };

            if (this.showPagingView) {
                treeConfig['dockedItems'] = [
                    {
                        xtype: 'pagingtoolbar',
                        store: store,
                        dock: 'bottom',
                        width: 360,
                        displayInfo: true
                    }
                ]
            }

            if (this.treePanelConfig) {
                for (var i in this.treePanelConfig) {
                    treeConfig[i] = this.treePanelConfig[i];
                }
            }

            var picker = Ext.create('Ext.grid.Panel', treeConfig);

            me.mon(picker, {
                itemclick: me.onItemClick,
                scope: me
            });

            return picker;
        }
        return null;
    },

    onItemClick: function (dataview, record, item, index, e, eOpts) {
        var data = record.getData();
        if (this.gridDisplayField) {
            data[this.displayField] = this.gridDisplayField(data);
        }
        var store = this.getStore();
        store.removeAll();
        store.add(data);
        this._current_item_click = true;
        this.setValue(record.getData()[this.valueField]);

        this.fireEvent('gridChange', this, data);
    },

    onListRefresh: function () {

    }
});