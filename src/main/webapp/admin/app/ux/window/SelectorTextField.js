Ext.define('UXApp.window.SelectorTextField', {
    extend: 'Ext.container.Container',
    alias: 'widget.selectortextfield',

    requires: [
        'Ext.form.field.ComboBox',
        'Ext.button.Button'
    ],

    layout: 'table',

    afterRender: function () {
        this.callParent(arguments);
        var self = this;
        if (!this.combobox) {
            var cfg = this.box || {};
            cfg.editable = false;
            cfg.name = this.name;
            this.combobox = new Ext.form.field.ComboBox(cfg);
            this.add(this.combobox);
            this.button = Ext.create('Ext.button.Button', {
                margin: '-10 0 0 0',
                icon: Resource.png('jet', 'search'),
                listeners: {
                    click: function () {
                        self.onSearch();
                    }
                },
                style: {
                    border: "1px solid #cecece",
                    borderLeft: "0px",
                    borderRadius: '0px',
                    backgroundColor: "#ffffff"
                }
            });
            this.add(this.button);
        }
        if (this.combobox) {
            this.combobox.setWidth(this.width - 31);
        }

        // this._privateSetData();
    },

    onSearch: function () {
        if (this.selector) {
            try {
                this.openWin.close();
            } catch (e) {
            }
            this.openWin = null;
            var me = this;
            if (this.openWin == null) {
                var position = this.getWindowPosition();
                var openSelector = Ext.create(this.selector, {
                    modal: true,
                    constrainHeader: true,
                    plain: true
                });
                openSelector.callback = function (model) {
                    try {
                        var store = me.combobox.getStore();
                        if (model instanceof Ext.data.Model) {
                            model = model.getData();
                        }
                        model = (model instanceof Array) ? model : [model]

                        if (store != null) {
                            var datas = store.getData().items;
                            for (var i = 0; i < datas.length; i++) {
                                model.push(datas[i].getData())
                            }
                        }

                        var store = Ext.create('Ext.data.Store', {
                            fields: [me.box.valueField, me.box.displayField],
                            data: model
                        });
                        me.combobox.bindStore(store);
                        me.combobox.select(store.getAt(0));
                        // me._privateSaveData(model);
                    } catch (e) {
                        console.error(e);
                    }
                    if (me.hasListeners.change) {
                        me.fireEvent('change', me, model, model[0]);
                    }
                };
                openSelector.show();
                var y = 0;
                if (me.down) {
                    y = position.y + me.height;
                } else {
                    y = position.y - openSelector.height;
                }
                openSelector.setX(position.x);
                openSelector.setY(y <= 0 ? 0 : y);
                this.openWin = openSelector;
            } else {
                try {
                    this.openWin.close();
                } catch (e) {
                }
                this.openWin = null;
            }
        }
    },

    _privateSaveData: function (data) {
        if (this.sessionId) {
            try {
                sessionStorage.setItem(this.sessionId, JSON.stringify(data));
            } catch (e) {
            }
        }
    },

    _privateSetData: function () {
        if (this.sessionId) {
            var me = this;
            try {
                var s = sessionStorage.getItem(this.sessionId);
                if (s && s != '') {
                    var json = JSON.parse(s);
                    var store = Ext.create('Ext.data.Store', {
                        fields: [me.box.valueField, me.box.displayField],
                        data: json
                    });
                    me.combobox.bindStore(store);
                    me.combobox.select(store.getAt(0));
                }
            } catch (e) {
            }
        }
    },

    getWindowPosition: function () {
        //var dom = this.items.first().getEl().dom;
        //dom = document.getElementById(dom.id + "-inputWrap");
        return this.absPos(this);
    },

    absPos: function (node) {
        var x = 0,
            y = 0;
        do {
            x += node.offsetLeft;
            y += node.offsetTop;
        } while (node = node.offsetParent);
        return {
            'x': x,
            'y': y
        };
    },

    getValue: function () {
        return this.combobox.getValue();
    },

    setValue: function (data) {
        var me = this;
        try {
            if (data instanceof Ext.data.Model) {
                data = data.getData();
            }
            var store = Ext.create('Ext.data.Store', {
                fields: [me.box.valueField, me.box.displayField],
                data: [data]
            });
            me.combobox.bindStore(store);
            me.combobox.select(store.getAt(0));
        } catch (e) {
            console.error(e);
        }
        if (me.hasListeners.change) {
            me.fireEvent('change', me, [data], data);
        }
    },

    setInputValue: function (inputValue) {
        var me = this;
        var store = Ext.create('Ext.data.Store', {
            fields: [me.box.valueField, me.box.displayField],
            data: [inputValue]
        });
        me.combobox.bindStore(store);
        me.combobox.select(store.getAt(0));
    }
});