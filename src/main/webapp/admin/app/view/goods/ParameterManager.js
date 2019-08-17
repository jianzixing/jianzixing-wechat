Ext.define('App.goods.ParameterManager', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.goodsparametermanager',
    requires: [
        'Ext.tree.Panel',
        'Ext.tree.View',
        'Ext.tree.Column',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.grid.Panel',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'Ext.selection.CheckboxModel'
    ],

    layout: 'border',
    header: false,
    defaultListenerScope: true,

    apis: {
        GoodsParameter: {
            getGroups: {},

            getParameterList: {},
            addParameter: {},
            deleteParameters: {},
            updateParameter: {},
            relParameters: {},
            removeRelParameters: {},

            addValue: {},
            updateValue: {},
            deleteValues: {}
        }
    },

    items: [
        {
            xtype: 'treepanel',
            name: 'tree_panel',
            region: 'west',
            split: true,
            width: 360,
            title: '属性模板分类',
            viewConfig: {},
            columns: [
                {
                    xtype: 'treecolumn',
                    dataIndex: 'name',
                    text: '组名称',
                    flex: 1
                },
                {
                    xtype: 'gridcolumn',
                    hidden: true,
                    dataIndex: 'pos',
                    text: '排序'
                }
            ],
            listeners: {
                itemclick: 'onTreeItemClick'
            },
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'list'),
                            text: '列出分类',
                            listeners: {
                                click: 'onListGroupClick'
                            }
                        },
                        '->',
                        {
                            xtype: 'button',
                            name: 'select_button',
                            icon: Resource.png('jet', 'selectall'),
                            style: {
                                backgroundColor: '#ffffff'
                            },
                            hidden: true,
                            text: '选择分类',
                            listeners: {
                                click: 'onSelectClick'
                            }
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'gridpanel',
            name: 'param_grid',
            region: 'center',
            api: {GoodsParameter: {getParameters: {_page: 'App.goods.ParameterManager'}}},
            apiDelay: true,
            title: '属性列表',
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'name',
                    text: '名称'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'type',
                    text: '类型',
                    renderer: function (v) {
                        if (v == 0) {
                            return '输入类型';
                        } else if (v == 1) {
                            return '多项选择';
                        } else if (v == 2) {
                            return '单项选择';
                        } else if (v == 4) {
                            return '输入URL地址';
                        }
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'isPrimary',
                    text: '是否规格属性',
                    renderer: function (value) {
                        if (value && value == 1) return "<span style='color: #61a4ff;font-weight: bold'>是</span>";
                        else return "否";
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'isColor',
                    text: '是否颜色属性',
                    renderer: function (value) {
                        if (value && value == 1) return "<span style='color: #61a4ff;font-weight: bold'>是</span>";
                        else return "否";
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'pos',
                    text: '排序'
                },
                {
                    xtype: 'actioncolumn',
                    width: 120,
                    text: '操作',
                    items: [
                        {
                            icon: Resource.png('jet', 'editSource'),
                            tooltip: '修改属性',
                            handler: 'onUpdateParamClick'
                        },
                        '->',
                        {
                            icon: Resource.png('jet', 'delete'),
                            tooltip: '删除属性',
                            handler: 'onDeleteParamClick'
                        },
                        '->',
                        {
                            icon: Resource.png('jet', 'RemoveMulticaret_dark'),
                            tooltip: '移除属性',
                            handler: 'onRemoveParamClick'
                        }
                    ]
                }
            ],
            listeners: {
                itemclick: 'onParamItemClick'
            },
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'list'),
                            text: '刷新属性',
                            listeners: {
                                click: 'onListParamClick'
                            }
                        },
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'addFolder'),
                            text: '添加属性',
                            listeners: {
                                click: 'onAddParamClick'
                            }
                        },
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'delete'),
                            text: '删除属性',
                            listeners: {
                                click: 'onDeleteParamClick'
                            }
                        },
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'RemoveMulticaret_dark'),
                            text: '移除属性',
                            listeners: {
                                click: 'onRemoveParamClick'
                            }
                        },
                        '|',
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'addFolder'),
                            text: '从属性池中选择',
                            listeners: {
                                click: 'onSelectFromParameters'
                            }
                        }
                    ]
                }
            ],
            selModel: {
                selType: 'checkboxmodel'
            }
        },
        {
            xtype: 'gridpanel',
            name: 'value_grid',
            region: 'east',
            split: true,
            width: 320,
            api: {GoodsParameter: {getValues: {_page: 'App.goods.ParameterManager'}}},
            apiDelay: true,
            title: '属性值',
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'value',
                    text: '属性值'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'color',
                    text: '颜色',
                    renderer: function (value) {
                        if (value) {
                            return "<div style='width: 30px;height: 30px;display: table-cell;text-align: center;vertical-align: middle;background-color: " + value + "'></div>";
                        }
                    }
                },
                {
                    xtype: 'actioncolumn',
                    width: 80,
                    text: '操作',
                    items: [
                        {
                            icon: Resource.png('jet', 'editSource'),
                            tooltip: '修改属性值',
                            handler: 'onUpdateValueClick'
                        },
                        '->',
                        {
                            icon: Resource.png('jet', 'delete'),
                            tooltip: '删除属性值',
                            handler: 'onDeleteValueClick'
                        }
                    ]
                }
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'list'),
                            text: '刷新值',
                            listeners: {
                                click: 'onListValueClick'
                            }
                        },
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'addFolder'),
                            text: '添加值',
                            listeners: {
                                click: 'onAddValueClick'
                            }
                        },
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'RemoveMulticaret_dark'),
                            text: '删除值',
                            listeners: {
                                click: 'onDeleteValueClick'
                            }
                        }
                    ]
                }
            ],
            selModel: {
                selType: 'checkboxmodel'
            },
            plugins: [{ptype: 'cellediting', clicksToEdit: 2}]
        }
    ],

    paramFormWindow: [
        {
            xtype: 'textfield',
            name: 'id',
            hidden: true,
            fieldLabel: 'ID',
            inputType: 'hidden'
        },
        {
            xtype: 'textfield',
            name: 'name',
            anchor: '100%',
            fieldLabel: '属性名称',
            allowBlank: false
        },
        {
            xtype: 'combobox',
            name: 'type',
            fieldLabel: '属性类型',
            anchor: '100%',
            valueField: 'id',
            allowBlank: false,
            displayField: 'name',
            editable: false,
            store: {
                data: [
                    {id: 0, name: '输入类型'},
                    {id: 1, name: '多项选择'},
                    {id: 2, name: '单项选择'},
                    {id: 4, name: '输入URL地址'}
                ]
            }
        },
        {
            xtype: 'radiogroup',
            fieldLabel: '是否规格属性',
            columns: 2,
            vertical: true,
            items: [
                {boxLabel: '否', name: 'isPrimary', inputValue: '0', checked: true},
                {boxLabel: '是', name: 'isPrimary', inputValue: '1'}
            ]
        },
        {
            xtype: 'radiogroup',
            fieldLabel: '是否颜色属性',
            columns: 2,
            vertical: true,
            items: [
                {boxLabel: '否', name: 'isColor', inputValue: '0', checked: true},
                {boxLabel: '是', name: 'isColor', inputValue: '1'}
            ]
        },
        {
            xtype: 'numberfield',
            name: 'pos',
            anchor: '100%',
            fieldLabel: '排序',
            emptyText: '0'
        }
    ],

    valueFormWindow: [
        {
            xtype: 'textfield',
            name: 'id',
            hidden: true,
            fieldLabel: 'ID',
            inputType: 'hidden'
        },
        {
            xtype: 'textfield',
            name: 'value',
            anchor: '100%',
            fieldLabel: '属性名称',
            allowBlank: false
        },
        {
            xtype: 'panel',
            name: 'color_panel',
            border: false,
            height: 45,
            margin: '0px 0px 0px 0px',
            layout: 'column',
            header: false,
            items: [
                {
                    xtype: 'container',
                    height: 20,
                    html: '属性颜色:',
                    margin: '8px 5px 0px 0px',
                    width: 100
                },
                {
                    xtype: 'label',
                    name: 'value_color',
                    style: {
                        border: '1px solid #999999'
                    },
                    height: 32,
                    width: 32
                },
                {
                    xtype: 'button',
                    margin: '0px 20px auto 20px',
                    text: '选择颜色',
                    listeners: {
                        click: 'selectColor'
                    }
                }
            ]
        },
        {
            xtype: 'numberfield',
            name: 'pos',
            anchor: '100%',
            fieldLabel: '排序',
            emptyText: '0'
        }
    ],

    onListGroupClick: function () {
        this.setGroupStore()
    },

    onParamItemClick: function (dataview, record, item, index, e, eOpts) {
        var pid = record.get('id');
        var type = record.get('type');
        if (type != 1 && type != 2) {
            this.find('value_grid').disable()
        } else {
            this.find('value_grid').enable()
        }
        this.find('value_grid').getStore().reloadReset({pid: pid})
    },

    onTreeItemClick: function (dataview, record, item, index, e, eOpts) {
        var gid = record.get('id');
        this.find('param_grid').getStore().reloadReset({gid: gid});
        this.find('value_grid').getStore().reloadReset({pid: 0})
    },

    onListParamClick: function () {
        this.find('param_grid').refreshStore()
    },

    onAddParamClick: function (button, e, eOpts) {
        var gid = this.find('tree_panel').getIgnoreSelect(arguments);
        if (gid == null) {
            Dialog.alert("必须选中一个属性分组");
            return false;
        }

        var self = this;
        Dialog.openFormWindow({
            title: '添加属性',
            width: 433,
            height: 320,
            items: self.paramFormWindow,
            success: function (json, win) {
                json['gid'] = gid['id'];
                self.apis.GoodsParameter.addParameter
                    .wait(self, '正在添加属性...')
                    .call({object: json}, function () {
                        win.close();
                        self.find('param_grid').refreshStore()
                    });
            }
        });
    },

    onDeleteParamClick: function (button, e, eOpts) {
        var jsons = this.find('param_grid').getIgnoreSelects(arguments);
        var self = this;
        Dialog.batch({
            message: '确定删除属性{d}吗,删除属性对应的属性值也会被删除？',
            data: jsons,
            key: 'name',
            callback: function (btn) {
                if (btn == 'yes') {
                    var ids = Array.splitArray(jsons, "id");
                    self.apis.GoodsParameter.deleteParameters
                        .wait(self, '正在删除属性...')
                        .call({ids: ids}, function () {
                            self.find('param_grid').refreshStore()
                        })
                }
            }
        });
    },

    onRemoveParamClick: function (button, e, eOpts) {
        var self = this;
        var jsons = this.find('param_grid').getIgnoreSelects(arguments);
        var gid = this.find('tree_panel').getIgnoreSelect(arguments);
        if (gid == null) {
            Dialog.alert("必须选中一个属性分组");
            return false;
        }
        Dialog.batch({
            message: '确定移除属性{d}吗,移除并不会删除您可以重新从属性池中添加？',
            data: jsons,
            key: 'name',
            callback: function (btn) {
                if (btn == 'yes') {
                    var ids = Array.splitArray(jsons, "id");
                    self.apis.GoodsParameter.removeRelParameters
                        .wait(self, '正在移除属性...')
                        .call({gid: gid['id'], ids: ids}, function () {
                            self.find('param_grid').refreshStore()
                        })
                }
            }
        });
    },

    onSelectFromParameters: function () {
        var self = this;
        var gid = this.find('tree_panel').getIgnoreSelect(arguments);
        if (gid == null) {
            Dialog.alert("必须选中一个属性分组");
            return false;
        }

        var win = Dialog.openWindow('App.goods.ParameterWindow', {
            apis: this.apis,
            _gid: gid['id'],
            _callback: function (datas) {
                self.find('param_grid').refreshStore();
            }
        });
        win.onInit();
    },

    onUpdateParamClick: function () {
        var json = this.find('param_grid').getIgnoreSelect(arguments);
        var self = this;
        Dialog.openFormWindow({
            title: '修改属性',
            width: 433,
            height: 320,
            items: self.paramFormWindow,
            success: function (json, win) {
                self.apis.GoodsParameter.updateParameter
                    .wait(self, '正在修改属性...')
                    .call({object: json}, function () {
                        win.close();
                        self.find('param_grid').refreshStore()
                    });
            }
        }).setValues(json);
    },

    onListValueClick: function () {
        this.find('value_grid').refreshStore()
    },

    selectColor: function (button) {
        var self = this;
        var parent = button.ownerCt;
        var label = parent.items.items[1];
        Dialog.openWindow('App.goods.ColorSelectorWindow', {
            _callback: function (color) {
                label.colorValue = '#' + color;
                label.getEl().setStyle({
                    backgroundColor: '#' + color
                })
            }
        });
    },

    onAddValueClick: function (button, e, eOpts) {
        var param = this.find('param_grid').getIgnoreSelect(arguments);
        if (param == null) {
            Dialog.alert("必须选中一个属性");
            return false;
        }
        var self = this;
        var win = Dialog.openFormWindow({
            title: '添加属性值',
            width: 433,
            height: 240,
            items: self.valueFormWindow,
            getValueCallback: function (form, json) {
                var color = form.find('value_color').colorValue;
                if (color) {
                    json['color'] = color;
                }
                return json;
            },
            success: function (json, win) {
                json['parameterId'] = param['id'];
                self.apis.GoodsParameter.addValue
                    .wait(self, '正在添加属性值...')
                    .call({object: json}, function () {
                        win.close();
                        self.find('value_grid').refreshStore()
                    });
            },
            funs: {
                selectColor: self.selectColor
            }
        });
        if (param['isColor'] != 1) {
            win.find('color_panel').hide();
            win.setHeight(200);
        }
    },

    onUpdateValueClick: function (button, e, eOpts) {
        var param = this.find('value_grid').getIgnoreSelect(arguments);
        if (param == null) {
            Dialog.alert("必须选中一个属性值");
            return false;
        }
        var self = this;
        var win = Dialog.openFormWindow({
            title: '修改属性值',
            width: 433,
            height: 240,
            items: self.valueFormWindow,
            setValueCallback: function (form, json) {
                form.find('value_color').colorValue = json['color'];
                form.find('value_color').getEl().setStyle({
                    backgroundColor: json['color']
                });
                return json;
            },
            getValueCallback: function (form, json) {
                var color = form.find('value_color').colorValue;
                if (color) {
                    json['color'] = color;
                }
                return json;
            },
            success: function (json, win) {
                self.apis.GoodsParameter.updateValue
                    .wait(self, '正在修改属性值...')
                    .call({object: json}, function () {
                        win.close();
                        self.find('value_grid').refreshStore()
                    });
            },
            funs: {
                selectColor: self.selectColor
            }
        });
        win.setValues(param);
        if (param['isColor'] != 1) {
            win.find('color_panel').hide();
            win.setHeight(200)
        }
    },

    onDeleteValueClick: function (button, e, eOpts) {
        var jsons = this.find('value_grid').getIgnoreSelects(arguments)
        var self = this;
        if (jsons == null) {
            return false;
        }
        Dialog.batch({
            message: '确定删除属性值{d}吗？',
            data: jsons,
            key: 'value',
            callback: function (btn) {
                if (btn == 'yes') {
                    var ids = Array.splitArray(jsons, "id");
                    self.apis.GoodsParameter.deleteValues
                        .wait(self, '正在删除属性值...')
                        .call({ids: ids}, function () {
                            self.find('value_grid').refreshStore()
                        })
                }
            }
        });
    },

    onAfterApply: function () {
        this.setGroupStore();
    },

    setSelectModel: function () {
        this.find('select_button').show();
    },

    onSelectClick: function () {
        var json = this.find('tree_panel').getIgnoreSelect(arguments);
        var self = this;
        if (json) {
            Dialog.confirm('提示', '确定选择属性模板 ' + Color.string(json['groupName']) + ' 吗？', function (btn) {
                if (btn == 'yes') {
                    if (self.onSelectCallback) {
                        self.onSelectCallback(json);
                    }
                }
            });
        } else {
            Dialog.alert('必须选择一个[属性模板分类]')
        }
    },

    setGroupStore: function () {
        var self = this;
        var tree = this.find('tree_panel');

        self.apis.GoodsParameter.getGroups
            .wait(self, '正在加载商品分组...')
            .call({}, function (d) {
                var store = Ext.create('Ext.data.TreeStore', {
                    defaultRootId: '0',
                    root: {
                        expanded: true,
                        name: "属性属性分组",
                        children: d
                    }
                });
                tree.setStore(store);
            })
    }

});
