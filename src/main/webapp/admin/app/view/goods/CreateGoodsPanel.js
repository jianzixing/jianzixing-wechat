Ext.define('App.goods.CreateGoodsPanel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.form.Panel',
        'Ext.form.Label',
        'Ext.form.RadioGroup',
        'Ext.form.field.Radio',
        'Ext.form.field.Date',
        'Ext.form.field.ComboBox',
        'Ext.form.field.TextArea',
        'Ext.button.Button',
        'Ext.toolbar.Toolbar',
        'UXApp.image.ImageItemView',
        'UXApp.editor.CKEditor',
        'UXApp.field.MyCheckbox'
    ],

    border: false,
    layout: 'fit',
    header: false,
    defaultListenerScope: true,

    items: [
        {
            xtype: 'tabpanel',
            activeTab: 0,
            items: [
                {
                    xtype: 'panel',
                    title: '基本属性',
                    scrollable: 'y',
                    padding: 30,
                    tabConfig: {
                        xtype: 'tab',
                        iconCls: 'x-fa fa-cubes'
                    },
                    items: [
                        {
                            xtype: 'label',
                            style: {
                                display: 'block'
                            },
                            html: '<div class="basetitle"> <div class="img"> <img border="0" src="/admin/image/icon/base.png"> </div> <div class="text">基本信息&nbsp;-&nbsp;(属性模板关联SKU信息)</div> </div>'
                        },
                        {
                            xtype: 'form',
                            name: 'base_form',
                            border: false,
                            layout: {
                                type: 'table',
                                columns: 2
                            },

                            frameHeader: false,
                            header: false,
                            items: [
                                {
                                    xtype: 'textfield',
                                    colspan: 2,
                                    name: 'name',
                                    margin: '5px 20px 5px 0px',
                                    width: 800,
                                    fieldLabel: '商品名称' + Color.string('*', 0xff0000),
                                    allowBlank: false,
                                    maxLength: 200,
                                    maxLengthText: '商品名称最大长度为 {0} 个字符',
                                    minLength: 1,
                                    minLengthText: '商品名称最小长度为 {0} 个字符',
                                    blankText: '商品名称不能为空'
                                },
                                {
                                    xtype: 'textareafield',
                                    margin: '5px 20px 5px 0px',
                                    name: 'subtitle',
                                    colspan: 2,
                                    width: 800,
                                    maxLength: 200,
                                    maxLengthText: '商品副标题最大长度为 {0} 个字符',
                                    fieldLabel: '商品副标题'
                                },
                                {
                                    xtype: 'fieldcontainer',
                                    fieldLabel: '单品/多规格',
                                    colspan: 2,
                                    width: 800,
                                    layout: {
                                        type: 'hbox',
                                        align: 'middle'
                                    },
                                    items: [
                                        {
                                            xtype: 'radiogroup',
                                            name: 'hasSkuContainer',
                                            width: 510,
                                            items: [
                                                {
                                                    xtype: 'radiofield',
                                                    name: 'hasSku',
                                                    boxLabel: '单规格商品',
                                                    inputValue: '0'
                                                },
                                                {
                                                    xtype: 'radiofield',
                                                    name: 'hasSku',
                                                    boxLabel: '多规格商品',
                                                    checked: true,
                                                    inputValue: '1'
                                                }
                                            ],
                                            listeners: {
                                                change: 'onDDRadiogroupChange'
                                            }
                                        },
                                        {
                                            xtype: 'image',
                                            height: 20,
                                            width: 20,
                                            src: 'image/icon/dp.png'
                                        },
                                        {
                                            xtype: 'label',
                                            width: 160,
                                            margin: 'auto auto auto 10',
                                            html: '<span style="color: #999999">注意：选择后不允许更改</span>'
                                        }
                                    ]
                                },
                                {
                                    xtype: 'treegridcombobox',
                                    treePanelConfig: {
                                        displayField: 'name',
                                        height: 300,
                                    },
                                    name: 'gid',
                                    displayField: 'name',
                                    valueField: 'id',
                                    anchor: '100%',
                                    width: 385,
                                    editable: false,
                                    margin: '5px 20px 5px 0px',
                                    fieldLabel: '商品分类' + Color.string('*', 0xff0000),
                                    listeners: {
                                        change: 'onGoodsGroupChange'
                                    }
                                },
                                {
                                    xtype: 'combobox',
                                    name: 'pwid',
                                    displayField: 'name',
                                    valueField: 'id',
                                    anchor: '100%',
                                    width: 385,
                                    editable: false,
                                    margin: '5px 20px 5px 0px',
                                    fieldLabel: '运费模板' + Color.string('*', 0xff0000)
                                },
                                {
                                    xtype: 'fieldcontainer',
                                    fieldLabel: '商品类型',
                                    colspan: 2,
                                    width: 800,
                                    layout: {
                                        type: 'hbox',
                                        align: 'middle'
                                    },
                                    items: [
                                        {
                                            xtype: 'radiogroup',
                                            width: 510,
                                            items: [
                                                {
                                                    xtype: 'radiofield',
                                                    name: 'type',
                                                    boxLabel: '实体商品',
                                                    checked: true,
                                                    inputValue: '10'
                                                },
                                                {
                                                    xtype: 'radiofield',
                                                    name: 'type',
                                                    boxLabel: '虚拟商品',
                                                    inputValue: '11'
                                                }
                                            ]
                                        },
                                        {
                                            xtype: 'image',
                                            height: 20,
                                            width: 20,
                                            src: 'image/icon/dp.png'
                                        },
                                        {
                                            xtype: 'label',
                                            width: 160,
                                            margin: 'auto auto auto 10',
                                            html: '<span style="color: #999999">注意：选择后不允许更改</span>'
                                        }
                                    ]
                                },
                                {
                                    xtype: 'combobox',
                                    name: 'bid',
                                    displayField: 'name',
                                    valueField: 'id',
                                    anchor: '100%',
                                    width: 385,
                                    margin: '5px 20px 5px 0px',
                                    fieldLabel: '品牌',
                                    queryMode: 'remote',
                                    queryParam: 'keyword',
                                    selectOnFocus: true,
                                    minChars: 1
                                },
                                {
                                    xtype: 'combobox',
                                    name: 'unit',
                                    anchor: '100%',
                                    width: 385,
                                    margin: '5px 20px 5px 0px',
                                    fieldLabel: '单位(默认个)',
                                    displayField: 'name',
                                    valueField: 'name',
                                    value: '个',
                                    store: {
                                        data: [
                                            {name: '个'}, {name: '件'}, {name: '部'}, {name: '条'}, {name: '瓶'}, {name: '双'},
                                            {name: '套'}, {name: '只'}, {name: '付'}, {name: '尊'}, {name: '把'}, {name: '罐'},
                                            {name: '包'}, {name: '盒'}, {name: '辆'}, {name: '升'}, {name: '方'}, {name: '块'},
                                            {name: '斤'}, {name: '吨'}, {name: '公斤'}, {name: '公升'}
                                        ]
                                    }
                                },
                                {
                                    xtype: 'numberfield',
                                    mouseWheelEnabled: false,
                                    name: 'weight',
                                    anchor: '100%',
                                    width: 385,
                                    margin: '5px 20px 5px 0px',
                                    fieldLabel: '商品重量(kg)',
                                    emptyText: '千克/kg'
                                },
                                {
                                    xtype: 'numberfield',
                                    mouseWheelEnabled: false,
                                    name: 'volume',
                                    anchor: '100%',
                                    width: 385,
                                    margin: '5px 20px 5px 0px',
                                    fieldLabel: '商品体积(m³)',
                                    emptyText: '立方米/m³'
                                }
                            ]
                        },
                        {
                            xtype: 'label',
                            margin: '30px auto 10px auto',
                            style: {
                                display: 'block'
                            },
                            html: '<div class="basetitle"> <div class="img"> <img border="0" src="/admin/image/icon/base.png"> </div> <div class="text">售卖信息&nbsp;-&nbsp;(销售价和会员价为支付价格，如果填写规格信息此填写无效)</div> </div>'
                        },
                        {
                            xtype: 'form',
                            name: 'base_price_form',
                            border: false,
                            layout: {
                                type: 'table',
                                columns: 2
                            },

                            frameHeader: false,
                            header: false,
                            items: [
                                {
                                    xtype: 'numberfield',
                                    name: 'price',
                                    allowBlank: false,
                                    mouseWheelEnabled: false,
                                    margin: '5px 20px 5px 0px',
                                    width: 385,
                                    fieldLabel: '销售价(元)' + Color.string('*', 0xff0000),
                                    blankText: '销售价不能为空',
                                    emptyText: '0'
                                },
                                {
                                    xtype: 'numberfield',
                                    mouseWheelEnabled: false,
                                    name: 'vipPrice',
                                    margin: '5px 20px 5px 0px',
                                    width: 385,
                                    fieldLabel: '会员价(元)',
                                    emptyText: '0'
                                },
                                {
                                    xtype: 'numberfield',
                                    mouseWheelEnabled: false,
                                    name: 'originalPrice',
                                    margin: '5px 20px 5px 0px',
                                    width: 385,
                                    fieldLabel: '原价(元)',
                                    emptyText: '0'
                                },
                                {
                                    xtype: 'numberfield',
                                    mouseWheelEnabled: false,
                                    name: 'costPrice',
                                    margin: '5px 20px 5px 0px',
                                    width: 385,
                                    fieldLabel: '成本价(元)',
                                    emptyText: '0'
                                },
                                {
                                    xtype: 'numberfield',
                                    mouseWheelEnabled: false,
                                    name: 'amount',
                                    margin: '5px 20px 5px 0px',
                                    width: 385,
                                    fieldLabel: '库存' + Color.string('*', 0xff0000),
                                    emptyText: '0'
                                },
                                {
                                    xtype: 'textfield',
                                    name: 'serialNumber',
                                    margin: '5px 20px 5px 0px',
                                    width: 385,
                                    fieldLabel: '商品编号',
                                    maxLength: 100,
                                    maxLengthText: '商品编号最大响度为 {0} 个字符',
                                    minLength: 1,
                                    minLengthText: '商品编号最小长度为 {0} 个字符',
                                    blankText: '商品编号不能为空'
                                }
                            ]
                        },
                        {
                            xtype: 'label',
                            margin: '30px auto 10px auto',
                            style: {
                                display: 'block'
                            },
                            html: '<div class="basetitle"> <div class="img"> <img border="0" src="/admin/image/icon/guige.png"> </div> <div class="text">商品属性</div> </div>'
                        },
                        {
                            xtype: 'form',
                            name: 'attribute_form',
                            region: 'center',
                            border: false,
                            margin: 'auto auto 50px auto',
                            layout: {
                                type: 'table',
                                columns: 1
                            },

                            frameHeader: false,
                            header: false,
                            items: [
                                {
                                    xtype: 'container',
                                    width: 820,
                                    height: 150,
                                    isNoAttr: true,
                                    cls: 'center-text',
                                    html: '没有销售属性'
                                }
                            ],
                            dockedItems: [
                                {
                                    xtype: 'toolbar',
                                    hidden: true,
                                    dock: 'bottom',
                                    items: [
                                        {
                                            xtype: 'button',
                                            text: '添加属性',
                                            icon: Resource.png('jet', 'AddNewSectionRule'),
                                            listeners: {
                                                click: 'onAddAttributeClick'
                                            }
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'panel',
                    name: 'sku_panel_manager',
                    title: '规格SKU管理',
                    scrollable: 'y',
                    layout: 'fit',
                    tabConfig: {
                        xtype: 'tab',
                        iconCls: 'x-fa fa-bars'
                    },
                    items: [
                        {
                            xtype: 'panel',
                            name: 'sale_have_panel',
                            layout: 'auto',
                            scrollable: 'y',
                            border: false,
                            header: false,
                            titleCollapse: false,
                            padding: 30,
                            items: [
                                {
                                    xtype: 'label',
                                    margin: '0px auto 10px auto',
                                    style: {
                                        display: 'block'
                                    },
                                    html: '<div class="basetitle"> <div class="img"> <img border="0" src="/admin/image/icon/base.png"> </div> <div class="text">销售属性</div> </div>'
                                },
                                {
                                    xtype: 'form',
                                    name: 'sales_attributes',
                                    split: true,
                                    border: false,
                                    header: false,
                                    layout: 'form',
                                    maxWidth: 960,
                                    title: '销售属性',
                                    margin: 'auto auto 20px auto',
                                    items: [
                                        {
                                            xtype: 'container',
                                            height: 150,
                                            isNoAttr: true,
                                            cls: 'center-text',
                                            html: '没有销售属性'
                                        }
                                    ],
                                    dockedItems: [
                                        {
                                            xtype: 'toolbar',
                                            hidden: true,
                                            dock: 'top',
                                            items: [
                                                {
                                                    xtype: 'button',
                                                    text: '添加规格属性',
                                                    icon: Resource.png('jet', 'AddNewSectionRule'),
                                                    listeners: {
                                                        click: 'onAddSaleAttrClick'
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                },
                                {
                                    xtype: 'label',
                                    name: 'attr_images_grid_label',
                                    hidden: true,
                                    margin: '0px auto 10px auto',
                                    style: {
                                        display: 'block'
                                    },
                                    html: '<div class="basetitle"> <div class="img"> <img border="0" src="/admin/image/icon/base.png"> </div> <div class="text">规格图片</div> </div>'
                                },
                                {
                                    xtype: 'gridpanel',
                                    name: 'attr_images_grid',
                                    maxWidth: 960,
                                    header: false,
                                    hideHeaders: true,
                                    hidden: true,
                                    store: {data: []},
                                    columns: [
                                        {
                                            xtype: 'gridcolumn',
                                            width: 360,
                                            dataIndex: 'name',
                                            text: '属性名称',
                                            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                                                metaData.tdStyle = 'vertical-align:middle;';
                                                return value;
                                            }
                                        },
                                        {
                                            xtype: 'gridcolumn',
                                            dataIndex: 'fileName',
                                            text: '图片',
                                            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                                                var str = '<div style="width: 60px;height: 60px;display: table-cell;text-align: center;vertical-align: middle;border: 1px solid #CFCFCF">';
                                                if (value) {
                                                    str += '<img style="max-width: 60px;max-height: 60px" src="' + Resource.image(value) + '"/>'
                                                }
                                                str += '</div>';
                                                return str;
                                            }
                                        },
                                        {
                                            xtype: 'actioncolumn',
                                            text: '操作',
                                            align: 'center',
                                            items: [
                                                {
                                                    tooltip: '设置图片',
                                                    icon: Resource.png('jet', 'editColors'),
                                                    handler: 'onSetAttrImageClick'
                                                }
                                            ],
                                            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                                                metaData.tdStyle = 'vertical-align:middle;';
                                            }
                                        }
                                    ]
                                },
                                {
                                    xtype: 'label',
                                    margin: '30px auto 10px auto',
                                    style: {
                                        display: 'block'
                                    },
                                    html: '<div class="basetitle"> <div class="img"> <img border="0" src="/admin/image/icon/base.png"> </div> <div class="text">商品规格设置(SKU)</div> </div>'
                                },
                                {
                                    xtype: 'panel',
                                    layout: 'column',
                                    margin: '20 0 20 0',
                                    items: [
                                        {
                                            xtype: 'numberfield',
                                            mouseWheelEnabled: false,
                                            name: 'fill_price',
                                            width: 190,
                                            fieldLabel: '销售价',
                                            labelWidth: 60,
                                            emptyText: '0',
                                            margin: '0 30 10 0',
                                            selectOnFocus: true
                                        },
                                        {
                                            xtype: 'numberfield',
                                            mouseWheelEnabled: false,
                                            name: 'fill_vip_price',
                                            margin: '0 30 10 0',
                                            width: 190,
                                            fieldLabel: '会员价',
                                            labelWidth: 60,
                                            emptyText: '0',
                                            selectOnFocus: true
                                        },
                                        {
                                            xtype: 'numberfield',
                                            mouseWheelEnabled: false,
                                            name: 'fill_original_price',
                                            margin: '0 30 10 0',
                                            width: 190,
                                            fieldLabel: '原价',
                                            labelWidth: 60,
                                            emptyText: '0',
                                            selectOnFocus: true
                                        },
                                        {
                                            xtype: 'numberfield',
                                            mouseWheelEnabled: false,
                                            name: 'fill_cost_price',
                                            margin: '0 30 10 0',
                                            width: 190,
                                            fieldLabel: '成本价',
                                            labelWidth: 60,
                                            emptyText: '0',
                                            selectOnFocus: true
                                        },
                                        {
                                            xtype: 'numberfield',
                                            mouseWheelEnabled: false,
                                            name: 'fill_amount',
                                            margin: '0 30 10 0',
                                            width: 190,
                                            fieldLabel: '库存',
                                            labelWidth: 60,
                                            emptyText: '0',
                                            selectOnFocus: true
                                        },
                                        {
                                            xtype: 'textfield',
                                            name: 'fill_serial_number',
                                            margin: '0 30 10 0',
                                            width: 190,
                                            fieldLabel: '商品编号',
                                            labelWidth: 60
                                        },
                                        {
                                            xtype: 'button',
                                            text: '填充',
                                            listeners: {
                                                click: 'onFillSku'
                                            }
                                        }
                                    ]
                                },
                                {
                                    xtype: 'gridpanel',
                                    name: 'product_standard',
                                    title: '商品规格',
                                    border: false,
                                    header: false,
                                    cls: 'grid-simple-line',
                                    minHeight: 150,
                                    margin: 'auto 30px 100px auto',
                                    plugins: [
                                        {
                                            ptype: 'cellediting',
                                            name: 'cellediting_product',
                                            clicksToEdit: 1,
                                            listeners: {
                                                edit: function (p, e) {
                                                    e.record.commit();
                                                }
                                            }
                                        }
                                    ],
                                    columns: []
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'panel',
                    title: '图片管理',
                    scrollable: 'y',
                    layout: 'border',
                    tabConfig: {
                        xtype: 'tab',
                        iconCls: 'x-fa fa-file-image-o'
                    },
                    items: [
                        {
                            xtype: 'gridpanel',
                            name: 'image_attributes_grid',
                            region: 'west',
                            split: true,
                            hidden: true,
                            width: 230,
                            title: '相关属性',
                            forceFit: true,
                            columns: [
                                {
                                    xtype: 'gridcolumn',
                                    dataIndex: 'name',
                                    text: '属性名称'
                                },
                                {
                                    xtype: 'gridcolumn',
                                    dataIndex: 'value',
                                    text: '属性值'
                                }
                            ],
                            store: {
                                xtype: 'store',
                                data: [
                                    {id: 0, name: '全部', value: '全部'}
                                ]
                            },
                            listeners: {
                                itemclick: 'onImageAttrItemClick'
                            }
                        },
                        {
                            xtype: 'panel',
                            name: 'images_panel',
                            layout: 'column',
                            region: 'center',
                            title: '属性对应的图片 (如果选择"全部"则图片不属于任何属性)',
                            items: [],
                            header: false,
                            dockedItems: [
                                {
                                    xtype: 'toolbar',
                                    dock: 'top',
                                    items: [
                                        {
                                            xtype: 'button',
                                            text: '添加商品图片',
                                            icon: Resource.png('jet', 'addYouTrack'),
                                            listeners: {
                                                click: 'onAddGoodsImageClick'
                                            }
                                        },
                                        '->',
                                        {
                                            xtype: 'label',
                                            name: 'top_ctrl_lb',
                                            width: 390,
                                            html: '<img style="display: block;float: left" src="/admin/image/hd.png" alt="提示"/>'
                                                + '<span style="color: #999999;font-size: 13px;display: block;float: left;margin-top: 2px;margin-left: 3px">' +
                                                '提示:可以绑定图片到属性 (比如颜色属性) 如果不选择则默认全部</span>',
                                            text: ''
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'panel',
                    title: '商品描述',
                    scrollable: false,
                    layout: 'fit',
                    tabConfig: {
                        xtype: 'tab',
                        iconCls: 'x-fa fa-pencil-square'
                    },
                    items: [
                        {
                            xtype: 'ckeditor',
                            name: 'product_desc',
                            border: false,
                            margin: 20,
                            openFileWindow: function (editor) {
                                var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
                                win.setSelectionCallback(function (files) {
                                    editor.insertHtml("<img src=\"" + Resource.image(files.fileName) + "\"/>");
                                }, true);
                            }
                        }
                    ]
                },
                {
                    xtype: 'panel',
                    title: '商品服务',
                    layout: 'fit',
                    tabConfig: {
                        xtype: 'tab',
                        iconCls: 'x-fa fa-flag'
                    },
                    items: [
                        {
                            xtype: 'container',
                            margin: '30',
                            name: 'goods_supports'
                        }
                    ]
                }
            ]
        }
    ],
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            items: [
                {
                    xtype: 'button',
                    name: 'goods_panel_save_btn',
                    margin: 'auto auto auto 20px',
                    text: '保存商品',
                    icon: Resource.png('jet', 'menu-saveall'),
                    listeners: {
                        click: 'onAddGoodsClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'goods_panel_save_btn',
                    margin: 'auto auto auto 20px',
                    text: '取消关闭',
                    icon: Resource.png('jet', 'delete'),
                    listeners: {
                        click: 'onCloseGoodsClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'goods_panel_reset_btn',
                    text: '重新填写',
                    margin: 'auto auto auto 20px',
                    icon: Resource.png('jet', 'Reset_to_empty'),
                    listeners: {
                        click: 'onResetPanelClick'
                    }
                }
            ]
        }
    ],

    onCloseGoodsClick: function () {
        this.parent.close();
    },

    onAddGoodsImageClick: function (button, e, eOpts) {
        var self = this;
        var grid = this.find('image_attributes_grid');
        var win = Dialog.openWindow('App.file.SelectFileWindow');
        win.setSelectionCallback(function (files) {
            if (grid.$currentRecord && grid.$currentRecord.get('fileName') != 0) {
                var array = grid.$currentRecord.get("images") || [];
                for (var i = 0; i < files.length; i++) {
                    if (array.indexOf(files[i]['fileName']) < 0) {
                        array.push(files[i]['fileName']);
                    }
                }
                grid.$currentRecord.set("images", array);
            } else {
                var array = grid.images || [];
                for (var i = 0; i < files.length; i++) {
                    if (array.indexOf(files[i]['fileName']) < 0) {
                        array.push(files[i]['fileName']);
                    }
                }
                grid.images = array;
            }
            self.onImageAttrItemClick(grid, grid.$currentRecord);
        }, true);
    },

    onDDRadiogroupChange: function (field, newValue, oldValue, eOpts) {
        if (newValue['hasSku'] == 1) {
            this.find('sku_panel_manager').enable();
        } else {
            this.find('sku_panel_manager').disable();
        }
    },

    onAddGoodsClick: function (button, e, eOpts) {
        var baseForm = this.find('base_form').getForm();
        var priceForm = this.find('base_price_form').getForm();

        var groupId = this.find('base_form').find('gid').getValue();
        var brandId = this.find('base_form').find('bid').getValue();
        var deliverId = this.find('base_form').find('pwid').getValue();

        var attrForm = this.find('attribute_form');
        var salesForm = this.find('sales_attributes');
        var attrSimImgForm = this.find('attr_images_grid');
        var standard = this.find('product_standard');
        var imageForm = this.find('image_attributes_grid');
        var goodsDetail = this.find('product_desc');
        var supportView = this.find('goods_supports_checkboxgroup');

        var standardStore = standard.getStore();
        var imageStore = imageForm.getStore();
        var attrItems = attrForm.items.items;
        var salesItems = salesForm.items.items;
        var hasSku = baseForm['hasSku'];

        if (!baseForm.isValid()) {
            return false;
        }

        if (!groupId) {
            Dialog.alert('提示', '[商品分类]必须选择一条！');
            return false;
        }

        if (!deliverId) {
            Dialog.alert('提示', '[运费模板]必须选择一条！');
            return false;
        }

        //商品基本信息
        var baseDataSource = baseForm.getValues();
        baseDataSource['gid'] = groupId;
        baseDataSource['bid'] = brandId;
        baseDataSource['pwid'] = deliverId;
        var baseData = {};

        //商品价格信息(如果是SKU商品这个价格是无效的)
        var priceData = priceForm.getValues();
        var hasPriceData = false;
        if (priceData) {
            for (var k in priceData) {
                if (priceData[k] && priceData[k] != '') {
                    baseData[k] = priceData[k];
                    hasPriceData = true;
                }
            }
        }

        for (var k in baseDataSource) {
            if (baseDataSource[k] != null && baseDataSource[k] != '') {
                baseData[k] = baseDataSource[k];
            }
        }

        //商品属性信息
        var attrData = [];
        for (var i = 0; i < attrItems.length; i++) {
            var attr = attrItems[i];
            if (attr instanceof Ext.form.FieldContainer && !attr['isNoAttr']) {
                var data = attr.getValue();
                if (data) {
                    attrData.push(data)
                }
            }
        }

        //商品规格信息
        var standardData = [];
        var storeData = standardStore.getData().items;
        for (var k = 0; k < storeData.length; k++) {
            var sd = storeData[k];
            var dt = sd.getData();
            var values = [];
            var data = {};
            for (var o in dt) {
                if (o == 'price' || o == 'amount' || o == 'vipPrice'
                    || o == 'originalPrice' || o == 'costPrice' || o == 'serialNumber') {
                    data[o] = dt[o];
                } else if (o == 'id') {
                } else {
                    var v = dt[o];
                    values.push(v);
                }
            }
            if (values.length > 0) {
                data['values'] = values;
            }
            if (parseFloat(data['price']) <= 0) {
                Dialog.alert('价格不能为0');
                return false;
            }
            if (parseInt(data['amount']) <= 0) {
                Dialog.alert('数量不能为0');
                return false;
            }
            if (isNaN(parseInt(data['amount'])) || isNaN(parseFloat(data['price']))) {
                Dialog.alert('规格商品的价格或者数量必须填写且是数字');
                return false;
            }
            standardData.push(data);
        }

        if (hasSku == 1 && standardData.length <= 0 && !hasPriceData) {
            Dialog.alert('售卖信息和规格信息必须填写一个<br/><span style="color: #999999">如果填写规格信息那么售卖信息无效</span>');
            return false;
        }

        var imageData = [];
        //属性图片信息
        var attrImageDataStore = attrSimImgForm.getStore().getData();
        if (attrImageDataStore) {
            var aidItems = attrImageDataStore.items;
            for (var i = 0; i < aidItems.length; i++) {
                var item = aidItems[i];
                var attrImgItem = item.getData();
                imageData.push({
                    attrId: attrImgItem['attrId'],
                    valueId: attrImgItem['id'],
                    images: [attrImgItem['fileName']]
                })
            }
        }

        //商品图片信息
        var imageStoreData = imageStore.getData().items;
        var gridImages = imageForm.images;
        if (gridImages != null) {
            imageData.push({attrId: 0, images: gridImages})
        }
        for (var i = 0; i < imageStoreData.length; i++) {
            var dt = imageStoreData[i];
            var images = dt.get('images');
            var id = dt.get('id');
            var name = dt.get('name');
            var value = dt.get('value');
            if (images && images.length > 0) {
                imageData.push({attrId: id, name: name, value: value, images: images || []})
            }
        }

        //商品描述信息
        var descData = goodsDetail.getValue();
        if (!descData) {
            Dialog.alert('商品描述必须填写');
            return false;
        }

        var goods = {};

        if (supportView) {
            var supports = supportView.getValue();
            supports = supports['goods_supports_checkboxgroup'];
            if (supports) {
                if (!Ext.isArray(supports)) {
                    supports = [supports];
                }
                goods['supports'] = supports.join(",");
            }
        }

        goods['info'] = baseData;
        if (attrData.length > 0) goods['attrs'] = attrData;
        if (standardData.length > 0) goods['sku'] = standardData;
        if (imageData.length > 0) goods['images'] = imageData;
        if (descData) goods['desc'] = descData;

        var self = this;
        if (this.$goods) {
            Dialog.confirm('提示', '确定修改当前商品 ' + Color.string(this.$goods['name'], '#999999') + ' ？', function (btn) {
                if (btn == 'yes') {
                    goods['id'] = self.$goods['id'];
                    self.apis.Goods.updateGoods
                        .wait(self, '正在修改商品...')
                        .call({object: goods}, function (res) {
                            Dialog.confirm('提示', '修改商品成功！', function (btn) {
                                self.parent.close();
                                if (self._closecallback) {
                                    self._closecallback();
                                }
                            })
                        })
                }
            });
        } else {
            Dialog.confirm('提示', (imageData.length == 0 ? '当前商品没有添加图片，' : '') + '确定添加当前商品？', function (btn) {
                if (btn == 'yes') {
                    self.apis.Goods.addGoods
                        .wait(self, '正在添加商品...')
                        .call({object: goods}, function (res) {
                            Dialog.confirm('提示', '添加商品成功！是否继续添加？', function (btn) {
                                if (btn == 'yes') {
                                    self.parent.redraw();
                                } else {
                                    self.parent.close();
                                    if (self._closecallback) {
                                        self._closecallback();
                                    }
                                }
                            })
                        })
                }
            });
        }
    },

    onGoodsGroupChange: function (view, newValue, oldValue) {
        var self = this;
        var model = view.getStore().getById(newValue);
        var attrGroupId = model.get('id');
        // 加载属性模板
        this.onParameterChange(view, attrGroupId);
        // 加载商品服务
        if (self.$goods) {
            this.loadGoodsSupports(model.get('id'), self.$goods['TableGoodsSupport'], true);
        } else {
            this.loadGoodsSupports(model.get('id'));
        }
    },

    loadGoodsSupports: function (attrGroupId, existSupports, isUpdate) {
        // 加载关联的商品服务
        var self = this;
        var ids = [];
        if (isUpdate && existSupports) {
            for (var i = 0; i < existSupports.length; i++) {
                ids.push(parseInt(existSupports[i]['supportId']))
            }
        }

        self.find('goods_supports').removeAll();
        this.apis.Support.getSupportByGroup
            .call({groupId: attrGroupId}, function (data) {
                if (data && Ext.isArray(data)) {
                    var items = [];
                    for (var i = 0; i < data.length; i++) {
                        var cbf = {
                            xtype: 'checkboxfield',
                            boxLabel: data[i]['supportName'],
                            inputValue: data[i]['supportId']
                        };
                        if (isUpdate) {
                            if (ids.indexOf(data[i]['supportId']) >= 0) {
                                cbf.checked = true;
                            } else {
                                cbf.checked = false;
                            }
                        } else {
                            cbf.checked = true;
                        }
                        items.push(cbf);
                    }
                    var checkBoxView = Ext.create({
                        xtype: 'checkboxgroup',
                        name: 'goods_supports_checkboxgroup',
                        fieldLabel: '当前商品分类支持的服务',
                        labelWidth: 150,
                        items: items
                    });
                    self.find('goods_supports').add(checkBoxView);
                } else {
                    self.find('goods_supports').add({
                        xtype: 'container',
                        height: 150,
                        cls: 'center-text',
                        html: '没有支持的商品服务'
                    })
                }
            });
    },

    onParameterChange: function (view, newValue, oldValue) {
        var self = this;

        var attrForm = self.find('attribute_form');
        var salesForm = self.find('sales_attributes');
        attrForm.removeAll(true);
        salesForm.removeAll(true);

        if (newValue > 0) {
            self.apis.GoodsParameter.getGroupSet
                .wait(self, '正在初始化参数...')
                .call({gid: newValue}, function (data) {
                    if (data && data['TableGoodsParameter']) {
                        var params = data['TableGoodsParameter'];
                        for (var i = 0; i < params.length; i++) {
                            var p = params[i];
                            var isPrimary = p['isPrimary'];
                            if (isPrimary == 0) {
                                var params_item = params[i];
                                var TableGoodsValue = params_item['TableGoodsValue'];
                                //修改数据初始化
                                if (self.$property) {
                                    var pts = self.$property;
                                    for (var k = 0; k < pts.length; k++) {
                                        var pt = pts[k];
                                        var attrId = pt['attrId'];
                                        var valueId = pt['valueId'];
                                        var valueName = pt['valueName'];
                                        var pos = pt['pos'];
                                        params_item['pos'] = pos;
                                        if (attrId && valueId && params_item['id'] == attrId) {
                                            for (var l = 0; l < TableGoodsValue.length; l++) {
                                                var tgv = TableGoodsValue[l];
                                                if (tgv['id'] == valueId) {
                                                    tgv['editValue'] = true;
                                                }
                                            }
                                        } else if (attrId && !valueId && params_item['id'] == attrId) {
                                            params_item['editValue'] = valueName;
                                        }
                                    }
                                }

                                self.onAddAttributeClick(params_item);
                            } else {
                                var params_item = params[i];
                                var TableGoodsValue = params_item['TableGoodsValue'];

                                self.$attrImages = {};

                                if (self.$salesAttr) {
                                    for (var k = 0; k < self.$salesAttr.length; k++) {
                                        var sa = self.$salesAttr[k];
                                        var attrId = sa['attrId'];
                                        var attrName = sa['attrName'];
                                        var valueId = sa['valueId'];
                                        var valueName = sa['valueName'];
                                        if (sa['fileName']) {
                                            self.$attrImages[valueId] = sa['fileName'];
                                        }
                                        if (attrId && valueId && params_item['id'] == attrId) {
                                            for (var l = 0; l < TableGoodsValue.length; l++) {
                                                var tgv = TableGoodsValue[l];
                                                if (tgv['id'] == valueId) {
                                                    tgv['editValue'] = true;
                                                    tgv['editText'] = valueName;
                                                }
                                            }
                                        }
                                    }
                                }
                                self.onAddSalesAttributeClick(params_item, false, null, false)
                            }
                        }
                    } else {
                        attrForm.add({
                            xtype: 'container',
                            width: 820,
                            height: 150,
                            isNoAttr: true,
                            cls: 'center-text',
                            html: '没有销售属性'
                        });
                        salesForm.add({
                            xtype: 'container',
                            height: 150,
                            isNoAttr: true,
                            cls: 'center-text',
                            html: '没有销售属性'
                        });
                    }
                })
        }
    },

    onImageAttrItemClick: function (dataview, record, item, index, e, eOpts) {
        var grid = this.find('image_attributes_grid');
        var imageview = this.find('images_panel');

        var array = [];
        if (record && record.get('id') != 0) {
            grid.$currentRecord = record;
            array = record.get('images') || [];
        } else {
            grid.$currentRecord = null;
            array = grid.images || [];
            var store = grid.getStore();
            var items = store.getData().items;
            for (var i = 0; i < items.length; i++) {
                var s = items[i].get('images');
                if (s) {
                    for (var k = 0; k < s.length; k++) {
                        if (array.indexOf(s[k]) < 0) {
                            array.push(s[k]);
                        }
                    }
                }
            }
        }
        imageview.removeAll(true);
        array = array || [];
        for (var i = 0; i < array.length; i++) {
            var label = Ext.create(
                {
                    xtype: 'imageitemview',
                    src: Resource.image(array[i]),
                    imageId: array[i],
                    listeners: {
                        deleteclick: 'onRemoveImageClick'
                    }
                });
            imageview.add(label);
        }
    },

    onAddSaleAttrClick: function () {
        var self = this;
        Dialog.openWindow('App.goods.SalesAttributeWindow', {
            _callback: function (data, isCustom) {
                self.onAddSalesAttributeClick(data, true, null, isCustom);
                self.onSalesCheckboxGroupChange();
            },
            isCustom: true
        });
        //this.onAddSalesAttributeClick({name: '颜色', values: [{value: '红色'}, {value: '黄色'}, {value: '蓝色'}]}, true);
    },

    onAddAttributeClick: function (data) {
        var form = this.find('attribute_form');
        var self = this;
        var type = data['type'];
        var values = data['TableGoodsValue'];
        var pos = data['pos'] || 0;

        if (type == 1) {
            var checkboxfield = [];
            for (var i = 0; i < values.length; i++) {
                var value = values[i];
                checkboxfield.push(
                    {
                        xtype: 'checkboxfield',
                        margin: 'auto 15px auto auto',
                        boxLabel: value['value'],
                        inputValue: value['id'] || -1,
                        checked: value['editValue'] ? true : false
                    }
                );
            }
            var checkgroup = Ext.create('Ext.form.FieldContainer', {
                layout: 'table',
                margin: 'auto 50px auto 10px',
                items: [
                    {
                        xtype: 'checkboxgroup',
                        width: 305,
                        margin: '5px 5px 5px 0px',
                        layout: 'column',
                        fieldLabel: data['name'],
                        attrId: data['id'],
                        listeners: {
                            change: 'onCheckboxgroupChange'
                        },
                        items: checkboxfield
                    },
                    {
                        xtype: 'textfield',
                        name: 'attr_show_pos',
                        margin: '5px 10px 5px 0px',
                        width: 30,
                        emptyText: '0',
                        value: pos,
                        listeners: {
                            blur: 'onTextfieldBlur'
                        }
                    },
                    {
                        xtype: 'fieldcontainer',
                        margin: '5px 5px 5px 100px',
                        height: 32,
                        fieldLabel: data['name'],
                        cls: 'label-middle',
                        items: [
                            {
                                xtype: 'label',
                                name: 'attr_show_value',
                                text: '<属性值>'
                            }
                        ]
                    }
                ],
                getValue: function () {
                    var items = this.items.items;
                    var cbg = items[0];
                    var children = cbg.items.items;
                    var attrId = cbg['attrId'];
                    var values = [];
                    var pos = items[1].getValue();
                    var name = cbg.fieldLabel;
                    for (var i = 0; i < children.length; i++) {
                        if (children[i].getValue()) {
                            values.push({id: children[i].inputValue, name: children[i].boxLabel})
                        }
                    }
                    if (values == null || values.length == 0) {
                        return null;
                    }
                    return {attrId: attrId, name: name, values: values, pos: pos}
                }
            });
            form.add(checkgroup);

            this.onCheckboxgroupChange(checkgroup.items.items[0]);
        } else if (type == 0) {
            var inputType = Ext.create('Ext.form.FieldContainer', {
                layout: 'table',
                margin: 'auto 50px auto 10px',
                items: [
                    {
                        xtype: 'textfield',
                        fieldLabel: data['name'],
                        attrId: data['id'],
                        width: 305,
                        margin: '5px 5px 5px 0px',
                        emptyText: '属性值',
                        listeners: {
                            change: 'onTextfieldChange'
                        },
                        value: data['editValue'] || ''
                    },
                    {
                        xtype: 'textfield',
                        name: 'attr_show_pos',
                        margin: '5px 10px 5px 0px',
                        width: 30,
                        emptyText: '0',
                        value: pos,
                        listeners: {
                            blur: 'onTextfieldBlur'
                        }
                    },
                    {
                        xtype: 'fieldcontainer',
                        margin: '5px 5px 5px 100px',
                        height: 32,
                        fieldLabel: data['name'],
                        cls: 'label-middle',
                        items: [
                            {
                                xtype: 'label',
                                name: 'attr_show_value',
                                text: '<属性值>'
                            }
                        ]
                    }
                ],
                getValue: function () {
                    var items = this.items.items;
                    var tf = items[0];
                    var attrId = tf.attrId;
                    var pos = items[1].getValue();

                    if (!tf.getValue() || tf.getValue() == '') {
                        return null;
                    }

                    return {attrId: attrId, name: tf.fieldLabel, values: [{name: tf.getValue()}], pos: pos};
                }
            });
            form.add(inputType);

            this.onTextfieldChange(inputType.items.items[0]);
        } else if (type == 2) {
            var storeData = [];
            if (values) {
                for (var i = 0; i < values.length; i++) {
                    storeData.push(values[i]);
                }
            }
            var combobox = Ext.create('Ext.form.FieldContainer', {
                layout: 'table',
                margin: 'auto 50px auto 10px',
                items: [
                    {
                        xtype: 'combobox',
                        name: 'attr_show_cmb',
                        margin: '5px 5px 5px 0px',
                        width: 305,
                        fieldLabel: data['name'],
                        attrId: data['id'],
                        displayField: 'value',
                        valueField: 'id',
                        editable: false,
                        store: {
                            data: storeData
                        },
                        listeners: {
                            change: 'onComboboxChange'
                        }
                    },
                    {
                        xtype: 'textfield',
                        name: 'attr_show_pos',
                        margin: '5px 10px 5px 0px',
                        width: 30,
                        emptyText: '0',
                        value: pos,
                        listeners: {
                            blur: 'onTextfieldBlur'
                        }
                    },
                    {
                        xtype: 'fieldcontainer',
                        name: 'attr_show_name',
                        margin: '5px 5px 5px 100px',
                        height: 32,
                        fieldLabel: data['name'],
                        cls: 'label-middle',
                        items: [
                            {
                                xtype: 'label',
                                name: 'attr_show_value',
                                text: '<属性值>'
                            }
                        ]
                    }
                ],
                getValue: function () {
                    var items = this.items.items;
                    var cbb = items[0];
                    var pos = items[1].getValue();
                    var sel = cbb.getSelection();
                    var data = {};
                    if (sel) {
                        data = sel.getData();
                    } else {
                        return null;
                    }

                    return {
                        attrId: cbb.attrId,
                        name: cbb.fieldLabel,
                        values: [{id: data['id'], name: data['value']}],
                        pos: pos
                    }
                }
            });

            form.add(combobox);
            if (values) {
                for (var i = 0; i < values.length; i++) {
                    if (values[i]['editValue']) {
                        var cmb = combobox.find('attr_show_cmb');
                        cmb.setValue(values[i]['id']);
                    }
                }
            }
        } else {
            var textfield = Ext.create('Ext.form.FieldContainer', {
                layout: 'table',
                margin: 'auto 50px auto 10px',
                items: [
                    {
                        xtype: 'textfield',
                        margin: '5px 5px 5px 0px',
                        width: 100,
                        emptyText: '属性名称',
                        listeners: {
                            change: 'onTextfieldNameChange'
                        }
                    },
                    {
                        xtype: 'textfield',
                        width: 200,
                        margin: '5px 5px 5px 0px',
                        emptyText: '属性值',
                        listeners: {
                            change: 'onTextfieldChange'
                        }
                    },
                    {
                        xtype: 'textfield',
                        name: 'attr_show_pos',
                        margin: '5px 10px 5px 0px',
                        width: 30,
                        emptyText: '0',
                        value: pos,
                        listeners: {
                            blur: 'onTextfieldBlur'
                        }
                    },
                    {
                        xtype: 'button',
                        text: '删除',
                        listeners: {
                            click: 'onDeleteAttributeClick'
                        }
                    },
                    {
                        xtype: 'fieldcontainer',
                        name: 'attr_show_name',
                        margin: '5px 5px 5px 50px',
                        height: 32,
                        fieldLabel: '属性名',
                        cls: 'label-middle',
                        items: [
                            {
                                xtype: 'label',
                                name: 'attr_show_value',
                                text: '<属性值>'
                            }
                        ]
                    }
                ],
                getValue: function () {
                    var items = this.items.items;
                    var name = items[0].getValue();
                    var value = items[1].getValue();
                    var pos = items[2].getValue();

                    if (value == null || name == null
                        || name == '' || value == '') {
                        return null;
                    }

                    return {name: name, values: [{name: value}], pos: pos}
                }
            });
            form.add(textfield)
        }

        //每加一个组件就重新排序
        this.onTextfieldBlur();
    },

    onFillPrices: function () {
        var p_g = this.find('product_standard');
        var tf = this.find('fill_price');
        var store = p_g.getStore();

        var ds = store.getData().items;
        if (tf.getValue()) {
            for (var i = 0; i < ds.length; i++) {
                ds[i].set('price', tf.getValue());
                ds[i].commit()
            }
            store.setData(ds);
        }

    },

    onFillAmount: function () {
        var p_g = this.find('product_standard');
        var tf = this.find('fill_amount');
        var store = p_g.getStore();

        var ds = store.getData().items;
        if (tf.getValue()) {
            for (var i = 0; i < ds.length; i++) {
                ds[i].set('amount', tf.getValue());
                ds[i].commit()
            }
            store.setData(ds);
        }
    },

    onFillVipPrice: function () {
        var p_g = this.find('product_standard');
        var tf = this.find('fill_vip_price');
        var store = p_g.getStore();

        var ds = store.getData().items;
        if (tf.getValue()) {
            for (var i = 0; i < ds.length; i++) {
                ds[i].set('vipPrice', tf.getValue());
                ds[i].commit()
            }
            store.setData(ds);
        }
    },

    onFillOriginalPrice: function () {
        var p_g = this.find('product_standard');
        var tf = this.find('fill_original_price');
        var store = p_g.getStore();

        var ds = store.getData().items;
        if (tf.getValue()) {
            for (var i = 0; i < ds.length; i++) {
                ds[i].set('originalPrice', tf.getValue());
                ds[i].commit()
            }
            store.setData(ds);
        }
    },

    onFillCostPrice: function () {
        var p_g = this.find('product_standard');
        var tf = this.find('fill_cost_price');
        var store = p_g.getStore();

        var ds = store.getData().items;
        if (tf.getValue()) {
            for (var i = 0; i < ds.length; i++) {
                ds[i].set('costPrice', tf.getValue());
                ds[i].commit()
            }
            store.setData(ds);
        }
    },

    onFillSerialNumber: function () {
        var p_g = this.find('product_standard');
        var tf = this.find('fill_serial_number');
        var store = p_g.getStore();

        var ds = store.getData().items;
        console.log(tf.getValue())
        if (tf.getValue()) {
            for (var i = 0; i < ds.length; i++) {
                ds[i].set('serialNumber', tf.getValue());
                ds[i].commit()
            }
            store.setData(ds);
        }
    },

    onFillSku: function () {
        this.onFillPrices();
        this.onFillAmount();
        this.onFillVipPrice();
        this.onFillOriginalPrice();
        this.onFillCostPrice();
        this.onFillSerialNumber();
    },

    onSalesCheckboxGroupChange: function (field, newValue, oldValue, eOpts) {
        var s_g = this.find('sales_attributes');
        var p_g = this.find('product_standard');
        var items = s_g.items.items;
        var ds = [];
        //某一个属性的图片集合比如:红色的衣服
        var imageStoreData = [{id: 0, attrId: 0, valueId: 0, name: '全部', value: '全部'}];
        //颜色属性的封面
        var attrImageStoreData = [];

        for (var i = 0; i < items.length; i++) {
            var item = items[i];
            if (item['isNoAttr']) {
                continue;
            }
            var data = {};
            var values = [];
            var group = item.items.items[0];
            var cks = group.items.items;
            data['name'] = group.fieldLabel;
            data['id'] = group.attrId;
            for (var j = 0; j < cks.length; j++) {
                var ck = cks[j];
                if (ck.value) {
                    values.push({
                        id: ck.inputValue,
                        value: ck.getLabelValue(),
                        oldValue: ck.boxLabel,
                        isColorAttr: ck['isColorAttr']
                    });
                    if (ck['isColorAttr']) {
                        var attrInputValue = ck.inputValue;
                        var fileName = this.$attrImages[attrInputValue];
                        if (fileName) {
                            attrImageStoreData.push({
                                id: ck.inputValue,
                                attrId: ck.attrId,
                                fileName: fileName,
                                name: ck.getLabelValue()
                            });
                        } else {
                            attrImageStoreData.push({
                                id: ck.inputValue,
                                attrId: ck.attrId,
                                name: ck.getLabelValue()
                            });
                        }
                    }
                }
            }
            data['values'] = values;
            if (values.length > 0) {
                ds.push(data);
            }
        }

        var com = [];
        var gridColumns = [];
        var gridColumnWidth = 150;
        for (var i = 0; i < ds.length; i++) {
            var values = ds[i]['values'];
            var name = ds[i]['name'];
            var new_com = [];
            if (values) {
                for (var j = 0; j < values.length; j++) {
                    var dd = {};
                    values[j]['attr'] = {name: ds[i]['name'], id: ds[i]['id'] || -1};
                    // 已废弃
                    // if (values[j]['isColorAttr']) {
                    // imageStoreData.push({
                    //     id: values[j]['id'] || -1,
                    //     valueId: values[j]['id'] || -1,
                    //     attrId: ds[i]['id'] || -1,
                    //     name: name,
                    //     value: values[j]['value']
                    // });
                    // }
                    if (i == 0) {
                        dd[name] = values[j];
                        new_com.push(dd);
                    } else {
                        for (var k = 0; k < com.length; k++) {
                            var com_item = com[k];
                            var nci = {};
                            for (var l in com_item) {
                                nci[l] = com_item[l];
                            }
                            nci[name] = values[j];
                            new_com.push(nci)
                        }
                    }
                }
                com = new_com;

                gridColumns.push(Ext.create(
                    {
                        xtype: 'gridcolumn',
                        minWidth: gridColumnWidth,
                        align: 'center',
                        dataIndex: name,
                        text: name,
                        renderer: function (v) {
                            return v['value'];
                        }
                    }
                ))
            }
        }

        gridColumns.push(Ext.create(
            {
                xtype: 'gridcolumn',
                minWidth: gridColumnWidth - 10,
                align: 'center',
                dataIndex: 'price',
                text: '销售价' + Color.string('*', 'red') + '(元)',
                renderer: function (v) {
                    return PriceUtils.string(v || 0);
                },
                field: {
                    xtype: 'numberfield',
                    selectOnFocus: true
                }
            }
        ));

        gridColumns.push(Ext.create(
            {
                xtype: 'gridcolumn',
                minWidth: gridColumnWidth - 10,
                align: 'center',
                dataIndex: 'vipPrice',
                text: '会员价(元)',
                renderer: function (v) {
                    return PriceUtils.string(v || 0);
                },
                field: {
                    xtype: 'numberfield',
                    selectOnFocus: true
                }
            }
        ));

        gridColumns.push(Ext.create(
            {
                xtype: 'gridcolumn',
                minWidth: gridColumnWidth - 10,
                align: 'center',
                dataIndex: 'originalPrice',
                text: '原价(元)',
                renderer: function (v) {
                    return PriceUtils.string(v || 0);
                },
                field: {
                    xtype: 'numberfield',
                    selectOnFocus: true
                }
            }
        ));

        gridColumns.push(Ext.create(
            {
                xtype: 'gridcolumn',
                minWidth: gridColumnWidth - 10,
                align: 'center',
                dataIndex: 'costPrice',
                text: '成本价(元)',
                renderer: function (v) {
                    return PriceUtils.string(v || 0);
                },
                field: {
                    xtype: 'numberfield',
                    selectOnFocus: true
                }
            }
        ));

        gridColumns.push(Ext.create(
            {
                xtype: 'gridcolumn',
                minWidth: gridColumnWidth - 10,
                align: 'center',
                dataIndex: 'amount',
                text: '库存',
                renderer: function (v) {
                    if (v == null) {
                        return 0;
                    }
                    return v;
                },
                field: {
                    xtype: 'numberfield',
                    selectOnFocus: true
                }
            }
        ));

        gridColumns.push(Ext.create(
            {
                xtype: 'gridcolumn',
                minWidth: gridColumnWidth,
                align: 'center',
                dataIndex: 'serialNumber',
                text: '商品编号',
                field: {
                    xtype: 'textfield',
                    selectOnFocus: true
                }
            }
        ));


        p_g.setColumns(gridColumns);
        //如果已经存在填写好的数据那么修改时复制过来
        var store = p_g.getStore();
        var sds = store.getData().items;
        for (var o = 0; o < sds.length; o++) {
            var record = sds[o];
            for (var y = 0; y < com.length; y++) {
                var com_item = com[y];
                var is_sim = true;
                for (var v in com_item) {
                    if (record.get(v) && com_item[v] && record.get(v)['oldValue'] != com_item[v]['oldValue']) {
                        is_sim = false;
                    }
                }
                if (is_sim) {
                    com[y]['price'] = record.get('price');
                    com[y]['amount'] = record.get('amount');
                    com[y]['vipPrice'] = record.get('vipPrice');
                    com[y]['originalPrice'] = record.get('originalPrice');
                    com[y]['costPrice'] = record.get('costPrice');
                    com[y]['serialNumber'] = record.get('serialNumber');
                }
            }
        }
        store = Ext.create('Ext.data.Store', {});
        if (com.length > 0) {
            /**
             * 设置修改时com的值
             */
            var skus = this.$skuValue || [];
            var attrs = this.$salesAttr || [];
            for (var a = 0; a < skus.length; a++) {
                var sku = skus[a];
                sku['attrs'] = {};
                for (var b = 0; b < attrs.length; b++) {
                    if (attrs[b]['skuId'] == sku['id']) {
                        var skuAttr = attrs[b];
                        sku['attrs'][skuAttr['attrName']] = skuAttr;
                    }
                }
                var skuAttrs = sku['attrs'];
                for (var c = 0; c < com.length; c++) {
                    var comItem = com[c];
                    var isSet = true;
                    for (var d in comItem) {
                        var e = comItem[d];
                        if (e instanceof Object) {
                            if (skuAttrs[d] && skuAttrs[d]['valueId'] == e['id']) {

                            } else {
                                isSet = false;
                            }
                        }
                    }
                    if (isSet) {
                        comItem['price'] = sku['price'];
                        comItem['amount'] = sku['amount'];
                        comItem['vipPrice'] = sku['vipPrice'];
                        comItem['originalPrice'] = sku['originalPrice'];
                        comItem['costPrice'] = sku['costPrice'];
                        comItem['serialNumber'] = sku['serialNumber'];
                    }
                }
            }
            store.setData(com);
            store.commitChanges();
        } else {
            store.setData([]);
            store.commitChanges();
        }
        p_g.setStore(store);

        var imageStore = this.find('image_attributes_grid').getStore();
        var ivs = this.$imageValue;
        if (ivs) {
            for (var j = 0; j < imageStoreData.length; j++) {
                for (var p = 0; p < ivs.length; p++) {
                    var ivsItem = ivs[p];
                    if (imageStoreData[j]['valueId'] == ivsItem['valueId']) {
                        var images = imageStoreData[j]['images'];
                        images = images || [];
                        images.push(ivsItem['fileName']);
                        imageStoreData[j]['images'] = images;
                    }
                }
            }
        }
        imageStore.setData(imageStoreData);
        this.onImageAttrItemClick()


        //设置颜色属性的图片
        var attrImageGrid = this.find('attr_images_grid');
        var attrImageGridLabel = this.find('attr_images_grid_label');
        if (attrImageStoreData.length > 0) {
            attrImageGrid.show();
            attrImageGridLabel.show();
            var oldStore = attrImageGrid.getStore();
            if (oldStore) {
                var oldData = oldStore.getData().items;
                for (var i = 0; i < oldData.length; i++) {
                    for (var j = 0; j < attrImageStoreData.length; j++) {
                        if (oldData[i].get('id') == attrImageStoreData[j]['id']) {
                            attrImageStoreData[j]['attrId'] = oldData[i].get('attrId');
                            attrImageStoreData[j]['fileName'] = oldData[i].get('fileName');
                        }
                    }
                }
            }
            var attrImageStore = Ext.create('Ext.data.Store', {data: attrImageStoreData});
            attrImageGrid.setStore(attrImageStore);
        } else {
            attrImageGrid.hide();
            attrImageGridLabel.hide();
        }
    },

    onAddSalesAttributeClick: function (data, canEdit, checkgroup, canDelete) {
        var form = this.find('sales_attributes');
        var attrId = data['id'] || -1;
        data['values'] = data['values'] || data['TableGoodsValue'];
        if (!checkgroup) {
            checkgroup = Ext.create('Ext.form.FieldContainer', {
                layout: 'column',
                items: [
                    {
                        xtype: 'checkboxgroup',
                        layout: 'column',
                        margin: 'auto auto 10px auto',
                        fieldLabel: data['name'],
                        attrId: attrId,
                        labelWidth: 80,
                        items: [],
                        listeners: {
                            change: 'onSalesCheckboxGroupChange'
                        }
                    }
                ]
            });
        } else {
            checkgroup.items.items[0].setFieldLabel(data['name'])
        }

        if (data && data.values) {
            var datas = data.values;
            var cbg = checkgroup.items.items[0];
            cbg.removeAll(true);

            for (var i = 0; i < datas.length; i++) {
                var checked = canDelete ? true : false;
                var d = datas[i];
                if (d['editValue']) {
                    checked = true;
                }
                cbg.add(
                    {
                        xtype: 'mycheckbox',
                        minWidth: 180,
                        margin: 'auto 20px auto auto',
                        checked: checked,
                        isColorAttr: data['isColor'] == 1 ? true : false,
                        checkBoxColor: d['color'],
                        boxLabel: d['value'],
                        inputValue: d['id'] || -1,
                        attrId: attrId,
                        textValue: d['editText'] || null,
                        listeners: {
                            inputchange: 'onSalesInputChange'
                        }
                    }
                )
            }

            if (canEdit) {
                cbg.add(
                    {
                        xtype: 'label',
                        style: {
                            lineHeight: '37px',
                            cursor: 'pointer'
                        },
                        margin: 'auto 5px auto auto',
                        html: '<img src="' + Resource.png('', 'edit') + '"/>',
                        listeners: {
                            click: 'onEditSalesAttributeClick'
                        }
                    }
                );
            }
            if (canDelete) {
                cbg.add(
                    {
                        xtype: 'label',
                        style: {
                            lineHeight: '37px',
                            cursor: 'pointer'
                        },
                        margin: 'auto 5px auto auto',
                        html: '<img src="' + Resource.png('', 'delete') + '"/>',
                        listeners: {
                            click: 'onDeleteSalesAttributeClick'
                        }
                    }
                )
            }
        }

        form.add(checkgroup);

        this.onSalesInputChange();
    },

    onEditSalesAttributeClick: function (button, e) {
        var container = button.ownerCt.ownerCt;
        var group = button.ownerCt;
        var self = this;
        var data = {};
        var values = [];
        data['name'] = container.items.items[0].fieldLabel;
        data['id'] = container.items.items[0].attrId;

        for (var i = 0; i < group.items.items.length; i++) {
            var checkbox = group.items.items[i];
            if (checkbox instanceof Ext.form.field.Checkbox) {
                var value = checkbox.boxLabel;
                values.push({id: checkbox.inputValue, value: value})
            }
        }

        data['values'] = values;

        Dialog.openWindow('App.goods.SalesAttributeWindow', {
            _callback: function (data, isCustom) {
                self.onAddSalesAttributeClick(data, true, container, isCustom);
                self.onSalesCheckboxGroupChange();
            },
            isCustom: data['id'] > 0 ? false : true
        }).setValues(data);
    },

    onDeleteSalesAttributeClick: function (button, e) {
        var container = button.ownerCt.ownerCt;
        var form = this.find('sales_attributes');
        form.remove(container);
        this.onSalesCheckboxGroupChange();
    },

    onRemoveImageClick: function (imageitemview) {
        var self = this;
        var grid = this.find('image_attributes_grid');
        var store = grid.getStore();
        var imageId = imageitemview.imageId;
        var array = grid.images;
        var remove = function (arr, imgId) {
            if (arr) {
                var remove_id = [];
                for (var i = 0; i < arr.length; i++) {
                    if (arr[i] == imgId) {
                        remove_id.push(i);
                    }
                }
                for (var j = 0; j < remove_id.length; j++) {
                    arr.splice(remove_id[j], 1);
                }
            }
            return arr;
        };
        Dialog.confirm('提示', '确定移除当前图片吗？', function (btn) {
            if (btn == 'yes') {
                grid.images = remove(array, imageId);
                var record = store.getData().items;
                for (var i = 0; i < record.length; i++) {
                    var arr = record[i].get('images');
                    record[i].set('images', remove(arr, imageId));
                }

                var image = self.find('images_panel');
                image.remove(imageitemview)
            }
        });
    },

    onDeleteAttributeClick: function (button, e, eOpts) {
        var form = this.find('attribute_form');
        var parent = button.ownerCt;
        form.remove(parent)
    },

    onComboboxChange: function (field, newValue, oldValue, eOpts) {
        var parent = field.ownerCt;
        var value = parent.find('attr_show_value');
        var data = field.getSelection();

        value.setText(data.get('value'))
    },

    onCheckboxgroupChange: function (field, newValue, oldValue, eOpts) {
        var parent = field.ownerCt;
        var checked = field.getChecked();
        var value = parent.find('attr_show_value');
        var str = "";
        for (var i = 0; i < checked.length; i++) {
            str += checked[i].boxLabel + " , ";
        }
        if (str.length > 0) {
            value.setText(str.substring(0, str.length - 3))
        } else {
            value.setText('<属性值>')
        }
    },

    onTextfieldNameChange: function (field, newValue, oldValue, eOpts) {
        var parent = field.ownerCt;
        var name = parent.find('attr_show_name');
        var v = field.getValue();
        if (v == '') v = '属性名:';
        name.setFieldLabel(v);
    },

    onTextfieldChange: function (field, newValue, oldValue, eOpts) {
        var parent = field.ownerCt;
        var value = parent.find('attr_show_value');
        var v = field.getValue();
        if (v == '') v = '<属性值>';
        value.setText(v);
    },

    onTextfieldBlur: function (component, event, eOpts) {
        var form = this.find('attribute_form');
        var items = form.items.items;
        var newItems = [];

        for (var i = 0; i < items.length; i++) {
            var c = items[i];
            var posInput = c.find('attr_show_pos');
            var idx = posInput.getValue();
            newItems.push({pos: parseInt(idx || 0), field: c})
        }
        newItems.sort(function (a, b) {
            return a.pos - b.pos;
        });

        for (var j = 0; j < newItems.length; j++) {
            form.insert(j, newItems[j]['field'])
        }
    },

    onResetPanelClick: function (button, e, eOpts) {
        this.parent.redraw()
    },

    onAfterApply: function () {
        this.onSalesCheckboxGroupChange();

        // 加载商品分类
        var gidView = this.find('gid');
        this.apis.GoodsGroup.getGoodsGroups.call({}, function (jsons) {
            var store = Ext.create('Ext.data.TreeStore', {
                defaultRootId: '0',
                root: {
                    expanded: true,
                    name: "商品分类管理",
                    children: jsons
                }
            });
            gidView.setTreeGridStore(store);
        });

        // 加载商品品牌
        var bidView = this.find('bid');
        var store = this.apis.Brand.getBrands.createPageStore();
        bidView.setStore(store);
        store.load();

        // 加载运费模板
        var store = this.apis.Logistics.getTemplatesByKeyword.createListStore();
        var deliverView = this.find('pwid');
        deliverView.setStore(store);
    },

    setValues: function (goods) {
        this.$goods = goods;
        this.find('goods_panel_save_btn').setText('保存修改');
        this.find('goods_panel_reset_btn').hide();
        var baseForm = this.find('base_form').getForm();
        var basePriceForm = this.find("base_price_form").getForm();

        var groupSelector = this.find('base_form').find('gid');
        var brandSelector = this.find('base_form').find('bid');
        var hasSkuContainer = this.find('base_form').find('hasSkuContainer');

        var groupValue = goods['TableGoodsGroup'];
        var brandValue = goods['TableGoodsBrand'];
        var attrGroupValue = goods['TableGoodsParameterGroup'];

        var describeValue = goods['TableGoodsDescribe'];
        var propertyValue = goods['TableGoodsProperty'];
        var skuValue = goods['TableGoodsSku'];
        var imageValue = goods['TableGoodsImage'];

        if (groupValue) groupSelector.setComboBoxStoreData(groupValue);
        if (brandValue) brandSelector.setValue(brandValue['id']);

        baseForm.setValues(goods);
        basePriceForm.setValues(goods);
        hasSkuContainer.disable();

        this.find('pwid').getStore().load();

        this.$property = [];
        this.$salesAttr = [];
        if (propertyValue) {
            for (var i = 0; i < propertyValue.length; i++) {
                var pv = propertyValue[i];
                if (pv['skuId'] && pv['skuId'] > 0) {
                    if (imageValue) {
                        for (var j = 0; j < imageValue.length; j++) {
                            var imageValueItem = imageValue[j];
                            if (pv['attrId'] == imageValueItem['attrId']
                                && pv['valueId'] == imageValueItem['valueId']) {
                                pv['fileName'] = imageValueItem['fileName'];
                            }
                        }
                    }
                    this.$salesAttr.push(pv);
                } else {
                    this.$property.push(pv);
                }
            }
        }

        this.$imageValue = imageValue;
        this.$skuValue = skuValue;
        var goodsDetail = this.find('product_desc');

        goodsDetail.setValue(describeValue['desc']);
        goodsDetail._descId = describeValue['id'];

        if (this.parent && this.parent.moduleObject && this.parent.moduleObject.id) {
            localStorage.setItem("cache_" + this.parent.moduleObject.id, JSON.stringify(this.$goods));
        }
    },

    onSalesInputChange: function (value) {
        this.onSalesCheckboxGroupChange();
    },

    onSetAttrImageClick: function () {
        var model = arguments[5];
        var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
        win.setSelectionCallback(function (files) {
            model.set('fileName', files['fileName']);
            model.commit();
        }, true);
    },

    setGoodsGroup: function (json) {
        this.find('gid').setValue(json);
    },

    setCloseCallback: function (fn) {
        this._closecallback = fn;
    },

    onCacheLoad: function () {
        if (this.parent && this.parent.moduleObject && this.parent.moduleObject.id) {
            var goods = localStorage.getItem("cache_" + this.parent.moduleObject.id);
            if (goods) {
                this.setValues(JSON.parse(goods));
                return true;
            }
        }
        this.parent.close();
    }
});
