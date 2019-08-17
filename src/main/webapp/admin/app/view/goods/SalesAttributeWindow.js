Ext.define('App.goods.SalesAttributeWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.form.field.Text',
        'Ext.form.FieldContainer',
        'Ext.grid.Panel',
        'Ext.grid.column.Column',
        'Ext.grid.View'
    ],

    height: 355,
    width: 513,
    layout: 'border',
    title: '销售规格属性变更',
    defaultListenerScope: true,

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            items: [
                '->',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'editColors_dark'),
                    text: '确定更改',
                    listeners: {
                        click: 'onOkClick'
                    }
                },
                {
                    xtype: 'button',
                    text: '取消',
                    icon: Resource.png('jet', 'closeActive'),
                    listeners: {
                        click: 'onCancelClick'
                    }
                },
                '->'
            ]
        },
        {
            xtype: 'toolbar',
            name: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'button',
                    text: '添加属性值',
                    icon: Resource.png('jet', 'addClass'),
                    listeners: {
                        click: 'onAddValueClick'
                    }
                }
            ]
        }
    ],
    items: [
        {
            xtype: 'panel',
            region: 'north',
            border: false,
            header: false,
            items: [
                {
                    xtype: 'textfield',
                    name: 'attr_name',
                    margin: 5,
                    width: 500,
                    fieldLabel: '属性名称'
                }
            ]
        },
        {
            xtype: 'panel',
            region: 'center',
            border: false,
            layout: 'fit',
            header: false,
            items: [
                {
                    xtype: 'fieldcontainer',
                    margin: 5,
                    layout: 'fit',
                    fieldLabel: '属性值',
                    items: [
                        {
                            xtype: 'gridpanel',
                            name: 'values_grid',
                            plugins: [
                                {
                                    ptype: 'cellediting',
                                    clicksToEdit: 1,
                                    listeners: {
                                        edit: 'onCellEditor'
                                    }
                                }
                            ],
                            border: false,
                            header: false,
                            forceFit: true,
                            hideHeaders: true,
                            store: {
                                data: []
                            },
                            style: {
                                border: '1px solid #d0d0d0'
                            },
                            columns: [
                                {
                                    xtype: 'gridcolumn',
                                    dataIndex: 'value',
                                    text: '属性值',
                                    field: {
                                        xtype: 'textfield'
                                    }
                                },
                                {
                                    xtype: 'actioncolumn',
                                    text: '操作',
                                    align: 'center',
                                    width: 50,
                                    items: [
                                        {
                                            icon: Resource.png('jet', 'exclude'),
                                            tooltip: '删除',
                                            handler: 'onDeleteValueClick'
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ],

    onDeleteValueClick: function (grid, rowNumber) {
        var grid = this.find('values_grid');
        var store = grid.getStore();
        store.removeAt(rowNumber);
    },

    onAddValueClick: function (button, e, eOpts) {
        var grid = this.find('values_grid');
        var store = grid.getStore();

        store.add([{value: ''}, {value: ''}])
    },

    onOkClick: function (button, e, eOpts) {
        var name = this.find('attr_name').getValue();
        var attrId = this.find('attr_name').attrId;
        var grid = this.find('values_grid');
        var ds = grid.getStore().getData().items;
        var data = {id: attrId, name: name};
        var values = [];
        for (var i in ds) {
            if (ds[i].getData()['value'] != '') {
                var vvv = ds[i].getData();
                vvv['id'] = vvv['id'] || -1;
                values.push(vvv)
            }
        }
        data['values'] = values;

        try {
            if (this._callback) {
                this._callback(data, this.isCustom ? true : false);
            }
        } catch (e) {
        }
        this.close();
    },

    onCellEditor: function (editor, e) {
        e.record.commit();

        var grid = this.find('values_grid');
        var store = grid.getStore();

        store.add({value: ''})
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    onShow: function () {
        this.callParent();

        this.onAddValueClick()
    },

    setValues: function (data) {
        var name = data['name'];
        var values = data['values'];
        var grid = this.find('values_grid');
        this.find('attr_name').setValue(name);
        this.find('attr_name').attrId = data['id'];
        if (values) {
            grid.setStore({data: values})
        }
    }

});