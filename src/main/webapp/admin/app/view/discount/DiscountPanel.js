Ext.define('App.discount.DiscountPanel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.form.Panel',
        'Ext.form.RadioGroup',
        'Ext.form.field.Radio',
        'Ext.form.field.ComboBox',
        'Ext.form.field.Date',
        'Ext.form.field.TextArea'
    ],

    layout: 'fit',
    header: false,
    title: '添加优惠活动',
    defaultListenerScope: true,

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'back'),
                    text: '返回活动列表',
                    listeners: {
                        click: 'onBackClick'
                    }
                },
                {
                    xtype: 'button',
                    text: '重新填写',
                    icon: Resource.png('jet', 'resetStrip_dark'),
                    listeners: {
                        click: 'onReloadClick'
                    }
                }
            ]
        }
    ],
    items: [
        {
            xtype: 'form',
            name: 'form',
            border: false,
            bodyPadding: 30,
            header: false,
            autoScroll: true,
            items: [
                {
                    xtype: 'label',
                    margin: '0px auto 20px auto',
                    style: {
                        display: 'block'
                    },
                    html: '<div class="basetitle"> <div class="img"><img border="0" src="/admin/image/icon/base.png"> </div> <div class="text">活动基本信息</div> </div>'
                },
                {
                    xtype: 'textfield',
                    width: 800,
                    name: 'name',
                    fieldLabel: '活动名称',
                    allowBlank: false
                },
                {
                    xtype: 'radiogroup',
                    width: 800,
                    name: 'type_radio',
                    fieldLabel: '活动归属',
                    items: [
                        {
                            xtype: 'radiofield',
                            name: 'type',
                            boxLabel: '商品分类',
                            checked: true,
                            inputValue: '0'
                        },
                        {
                            xtype: 'radiofield',
                            name: 'type',
                            boxLabel: '商品',
                            inputValue: '1'
                        },
                        {
                            xtype: 'radiofield',
                            name: 'type',
                            boxLabel: '品牌',
                            inputValue: '2'
                        }
                    ],
                    listeners: {
                        change: 'onTypeChange'
                    }
                },
                {
                    xtype: 'radiogroup',
                    width: 800,
                    fieldLabel: '是否启用',
                    items: [
                        {
                            xtype: 'radiofield',
                            name: 'enable',
                            boxLabel: '启用',
                            inputValue: '1'
                        },
                        {
                            xtype: 'radiofield',
                            name: 'enable',
                            boxLabel: '禁用',
                            checked: true,
                            inputValue: '0'
                        }
                    ]
                },
                {
                    xtype: 'displayfield',
                    height: 55,
                    width: 800,
                    value: '<span style="color:#999999">必须设置[开始时间]和[结束时间]。活动分为三种[商品分类]和[商品品牌]' +
                        '都可以设置不在活动的列表，排除商品是以商品为单位不精确到SKU，上传文件可以根据商品编码精确到SKU。' +
                        '<span style="color: #0f0f0f">一旦活动建立后只能修改标题、描述和活动时间</span>。可参与次数如果不填或者填写小于等于0的数字' +
                        '都视为不限制次数，只有大于0才能生效。</span>',
                    fieldLabel: '<span style="color:#ff0000">活动配置说明</span>'
                },
                {
                    xtype: 'datetimefield',
                    format: 'Y-m-d H:i:s',
                    name: 'startTime',
                    width: 800,
                    fieldLabel: '开始时间',
                    allowBlank: false
                },
                {
                    xtype: 'datetimefield',
                    format: 'Y-m-d H:i:s',
                    name: 'finishTime',
                    width: 800,
                    fieldLabel: '结束时间',
                    allowBlank: false
                },
                {
                    xtype: 'numberfield',
                    name: 'count',
                    width: 800,
                    fieldLabel: '可参与次数',
                    allowBlank: false,
                    value: 1
                },
                {
                    xtype: 'checkboxgroup',
                    name: 'user_level_view',
                    width: 800,
                    fieldLabel: '适用会员',
                    items: []
                },
                {
                    xtype: 'checkboxgroup',
                    name: 'platform_view',
                    width: 800,
                    fieldLabel: '使用平台',
                    items: []
                },
                {
                    xtype: 'label',
                    margin: '0px auto 20px auto',
                    style: {
                        display: 'block'
                    },
                    html: '<div class="basetitle"> <div class="img"><img border="0" src="/admin/image/icon/base.png"> </div> <div class="text">活动商品配置</div> </div>'
                },

                {
                    xtype: 'treegridcombobox',
                    treePanelConfig: {
                        displayField: 'name'
                    },
                    name: 'gid',
                    displayField: 'name',
                    valueField: 'id',
                    width: 800,
                    editable: false,
                    fieldLabel: '商品分类',
                    listeners: {
                        itemsclick: 'onParameterChange'
                    }
                },
                {
                    xtype: 'fieldcontainer',
                    name: 'cid_list',
                    width: 800,
                    margin: 'auto auto 30px auto',
                    fieldLabel: '已选分类',
                    layout: 'auto',
                    items: []
                },
                {
                    xtype: 'fieldcontainer',
                    name: 'upload_cid_file',
                    width: 800,
                    hidden: true,
                    margin: '10px auto 30px auto',
                    layout: 'column',
                    fieldLabel: '排除商品',
                    items: [
                        {
                            xtype: 'textfield',
                            width: 495,
                            name: 'cidFile',
                            editable: false
                        },
                        {
                            xtype: 'hiddenfield',
                            width: 495,
                            name: 'cidHideFile',
                            editable: false
                        },
                        {
                            xtype: 'button',
                            width: 80,
                            margin: 'auto auto auto 20px',
                            text: '选择文件',
                            listeners: {
                                click: 'onSelectCidFile'
                            }
                        },
                        {
                            xtype: 'button',
                            width: 80,
                            margin: 'auto auto auto 20px',
                            text: '下载模板',
                            listeners: {
                                click: 'onDownloadTemplateFile'
                            }
                        }
                    ]
                },

                {
                    xtype: 'gridcombobox',
                    name: 'bid',
                    width: 800,
                    hidden: true,
                    hideTrigger: true,
                    fieldLabel: '商品品牌',
                    listeners: {
                        gridChange: 'onBrandChange'
                    },
                    treePanelConfig: {
                        forceFit: true,
                        columns: [
                            {
                                xtype: 'gridcolumn',
                                width: 200,
                                dataIndex: 'name',
                                text: '品牌名称'
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'detail',
                                text: '描述'
                            }
                        ]
                    },
                    searchQuery: true,
                    searchQueryField: 'keyword',
                    displayField: 'name',
                    valueField: 'id'
                },
                {
                    xtype: 'fieldcontainer',
                    name: 'bid_list',
                    hidden: true,
                    width: 800,
                    margin: 'auto auto 30px auto',
                    fieldLabel: '已选品牌',
                    layout: 'auto',
                    items: []
                },
                {
                    xtype: 'fieldcontainer',
                    name: 'upload_bid_file',
                    width: 800,
                    hidden: true,
                    margin: '10px auto 30px auto',
                    layout: 'column',
                    fieldLabel: '排除商品',
                    items: [
                        {
                            xtype: 'textfield',
                            width: 495,
                            name: 'bidFile',
                            editable: false
                        },
                        {
                            xtype: 'hiddenfield',
                            width: 495,
                            name: 'bidHideFile',
                            editable: false
                        },
                        {
                            xtype: 'button',
                            width: 80,
                            margin: 'auto auto auto 20px',
                            text: '选择文件',
                            listeners: {
                                click: 'onSelectBidFile'
                            }
                        },
                        {
                            xtype: 'button',
                            width: 80,
                            margin: 'auto auto auto 20px',
                            text: '下载模板',
                            listeners: {
                                click: 'onDownloadTemplateFile'
                            }
                        }
                    ]
                },

                {
                    xtype: 'button',
                    name: 'select_goods_btn',
                    width: 800,
                    hidden: true,
                    text: '选择商品',
                    listeners: {
                        click: 'onSelectGoodsWindow'
                    }
                },
                {
                    xtype: 'fieldcontainer',
                    name: 'upload_goods_file',
                    width: 800,
                    hidden: true,
                    margin: '10px auto 30px auto',
                    layout: 'column',
                    fieldLabel: '上传商品文件',
                    items: [
                        {
                            xtype: 'textfield',
                            width: 495,
                            name: 'goodsFile',
                            editable: false
                        },
                        {
                            xtype: 'hiddenfield',
                            width: 495,
                            name: 'goodsHideFile',
                            editable: false
                        },
                        {
                            xtype: 'button',
                            width: 80,
                            margin: 'auto auto auto 20px',
                            text: '选择文件',
                            listeners: {
                                click: 'onSelectFile'
                            }
                        },
                        {
                            xtype: 'button',
                            width: 80,
                            margin: 'auto auto auto 20px',
                            text: '下载模板',
                            listeners: {
                                click: 'onDownloadTemplateFile'
                            }
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    name: 'gid_list',
                    hidden: true,
                    width: 800,
                    margin: 'auto auto 30px auto',
                    fieldLabel: '已选商品',
                    layout: 'auto',
                    items: []
                },
                {
                    xtype: 'label',
                    margin: '0px auto 20px auto',
                    style: {
                        display: 'block'
                    },
                    html: '<div class="basetitle"> <div class="img"><img border="0" src="/admin/image/icon/base.png"> </div> <div class="text">活动参数配置</div> </div>'
                },
                {
                    xtype: 'combobox',
                    name: 'impl',
                    width: 800,
                    fieldLabel: '活动实现',
                    displayField: 'name',
                    valueField: 'impl',
                    editable: false,
                    listeners: {
                        change: 'onComboboxChange'
                    }
                },
                {
                    xtype: 'fieldcontainer',
                    name: 'discount_active_view',
                    width: 800,
                    fieldLabel: '活动参数',
                    items: []
                },
                {
                    xtype: 'textareafield',
                    name: 'detail',
                    width: 800,
                    height: 80,
                    fieldLabel: '活动描述'
                },
                {
                    xtype: 'fieldcontainer',
                    margin: '30px 0 0 0',
                    items: [
                        {
                            xtype: 'button',
                            text: '保存活动',
                            listeners: {
                                click: 'onSaveClick'
                            }
                        },
                        {
                            xtype: 'button',
                            margin: 'auto auto auto 20px',
                            text: '返回列表',
                            listeners: {
                                click: 'onBackClick'
                            }
                        }
                    ]
                }
            ]
        }
    ],

    onSaveClick: function (button, e, eOpts) {
        var self = this;
        var form = this.find('form').getForm();
        var paramsView = this.find('discount_active_view');
        var params = [];
        if (paramsView.items.items && paramsView.items.items.length > 0) {
            params = paramsView.items.items[0].getValue();
        }

        var bidList = self.find('bid_list'); //品牌分类
        var bidFile = self.find('bidHideFile'); //商品文件
        var gidList = self.find('gid_list'); //商品列表
        var goodsFile = self.find('goodsHideFile'); //商品文件
        var cidList = self.find('cid_list'); //分类列表
        var cidFile = self.find('cidHideFile'); //商品文件

        var bids = [];
        var bidData = bidList.finds('data_cmp');
        if (bidData) {
            for (var i = 0; i < bidData.length; i++) {
                if (bidData[i]._data) {
                    bids.push(bidData[i]._data.getData()['id']);
                }
            }
        }

        var gids = [];
        var gidData = gidList.finds('data_cmp');
        if (gidData) {
            for (var i = 0; i < gidData.length; i++) {
                if (gidData[i]._data) {
                    gids.push(gidData[i]._data['id']);
                }
            }
        }

        var cids = [];
        var cidData = cidList.finds('data_cmp');
        if (cidData) {
            for (var i = 0; i < cidData.length; i++) {
                if (cidData[i]._data) {
                    cids.push(cidData[i]._data.getData()['id']);
                }
            }
        }

        var dt = form.getValues();
        if (!Ext.isArray(dt['userLevels'])) {
            dt['userLevels'] = [dt['userLevels']]
        }
        if (!Ext.isArray(dt['platforms'])) {
            dt['platforms'] = [dt['platforms']]
        }
        console.log(dt);

        if (form.isValid()) {
            dt['params'] = params;
            dt['bids'] = bids;
            dt['bidExcludeFile'] = bidFile.getValue();
            dt['gids'] = gids;
            dt['gidIncludeFile'] = goodsFile.getValue();
            dt['cids'] = cids;
            dt['cidExcludeFile'] = cidFile.getValue();
            if (self._data) {
                dt['id'] = self._data['id'];
                Dialog.confirm('确定修改', '确定修改优惠活动吗？', function (btn) {
                    if (btn == 'yes') {
                        self.apis.Discount.updateDiscount
                            .wait(self, '正在更新活动...')
                            .call({object: dt}, function () {
                                self.onBackClick();
                                if (self._callback) {
                                    self._callback();
                                }
                            })
                    }
                });

            } else {
                Dialog.confirm('确定添加', '确定添加优惠活动吗？<span style="color: red">注意：优惠活动添加后主要信息不可以更改！</span>', function (btn) {
                    if (btn == 'yes') {
                        self.apis.Discount.addDiscount
                            .wait(self, '正在添加活动...')
                            .call({object: dt}, function () {
                                self.onBackClick();
                                if (self._callback) {
                                    self._callback();
                                }
                            });
                    }
                });
            }
        }
    },

    onBackClick: function () {
        this.parent.back();
    },

    onReloadClick: function () {
        this.parent.redraw();
    },

    resetBidListViews: function (data) {
        var self = this;
        var cidList = self.find('bid_list');
        if (!cidList.data) cidList.data = [];
        if (data && cidList.data.indexOf(data) < 0) {
            cidList.data.push(data);
        }
        cidList.removeAll();
        for (var i = 0; i < cidList.data.length; i++) {
            var dt = cidList.data[i];
            if (dt) {
                var v = Ext.create('Ext.container.Container', {
                    name: 'data_cmp',
                    layout: 'column',
                    margin: 'auto auto 10px auto',
                    items: [
                        {
                            xtype: 'container',
                            width: 300,
                            html: '<span style="line-height: 32px;font-weight: bold">' + dt.get('name') + '</span>'
                        },
                        {
                            xtype: 'button',
                            text: '删除',
                            _data: dt,
                            listeners: {
                                click: function () {
                                    var cidList = self.find('bid_list');
                                    cidList.data.splice(cidList.data.indexOf(this._data), 1);
                                    self.resetBidListViews()
                                }
                            }
                        }
                    ]
                });
                v._data = dt;
                cidList.add(v);
            }
        }
    },

    onBrandChange: function (view, value) {
        var data = view.getSelection();
        if (data) {
            this.resetBidListViews(data);
        }
    },

    onSelectFile: function () {
        var self = this;
        var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
        win.setSelectionCallback(function (files) {
            self.find('goodsFile').setValue(files['originalName']);
            self.find('goodsHideFile').setValue(files['fileName']);
        }, true);
    },


    onSelectBidFile: function () {
        var self = this;
        var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
        win.setSelectionCallback(function (files) {
            self.find('bidFile').setValue(files['originalName']);
            self.find('bidHideFile').setValue(files['fileName']);
        }, true);
    },


    onSelectCidFile: function () {
        var self = this;
        var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
        win.setSelectionCallback(function (files) {
            self.find('cidFile').setValue(files['originalName']);
            self.find('cidHideFile').setValue(files['fileName']);
        }, true);
    },

    resetGidListViews: function (data) {
        var self = this;
        var gidList = self.find('gid_list');
        if (!gidList.data) gidList.data = [];
        if (Ext.isArray(data)) {
            for (var i = 0; i < data.length; i++) {
                if (data && gidList.data.indexOf(data[i]) < 0) {
                    gidList.data.push(data[i]);
                }
            }
        } else {
            if (data && gidList.data.indexOf(data) < 0) {
                gidList.data.push(data);
            }
        }
        gidList.removeAll();
        for (var i = 0; i < gidList.data.length; i++) {
            var dt = gidList.data[i];
            if (dt) {
                var imgUrl = Resource.image(dt['fileName']);
                if (!dt['fileName']) imgUrl = Resource.create('/admin/image/exicon/nopic_40.gif');
                var img = '<div style="height: 40px;width: 40px;vertical-align: middle;display:table-cell;">' +
                    '<img style="max-height:40px;max-width: 40px;vertical-align: middle" src=' + imgUrl + '></div> ';

                var v = Ext.create('Ext.container.Container', {
                    name: 'data_cmp',
                    layout: 'column',
                    margin: 'auto auto 10px auto',
                    items: [
                        {
                            xtype: 'container',
                            width: 40,
                            html: img
                        },
                        {
                            xtype: 'container',
                            width: 300,
                            margin: 'auto auto auto 20px',
                            html: '<span style="/*line-height: 32px*/;color:#19438B">' + dt['name'] + '</span>'
                        },
                        {
                            xtype: 'button',
                            text: '删除',
                            _data: dt,
                            listeners: {
                                click: function () {
                                    var gidList = self.find('gid_list');
                                    gidList.data.splice(gidList.data.indexOf(this._data), 1);
                                    self.resetGidListViews()
                                }
                            }
                        }
                    ]
                });
                v._data = dt;
                gidList.add(v);
            }
        }
    },

    onDownloadTemplateFile: function () {
        window.open("/admin/admin/getFiles.action?name=discount")
    },

    onSelectGoodsWindow: function () {
        var self = this;
        var goods = Dialog.openWindow('App.discount.DiscountProductWindow', {
            apis: this.apis,
            _callSelectGoods: function (vs) {
                self.resetGidListViews(vs);
            }
        });
        var store = this.apis.Goods.getGoods.createPageStore();
        goods.setValue(store)
    },

    resetCidListViews: function (data) {
        var self = this;
        var cidList = self.find('cid_list');
        if (!cidList.data) cidList.data = [];
        if (data && cidList.data.indexOf(data) < 0) {
            cidList.data.push(data);
        }
        cidList.removeAll();
        for (var i = 0; i < cidList.data.length; i++) {
            var dt = cidList.data[i];
            if (dt) {
                var v = Ext.create('Ext.container.Container', {
                    name: 'data_cmp',
                    layout: 'column',
                    margin: 'auto auto 10px auto',
                    items: [
                        {
                            xtype: 'container',
                            width: 300,
                            html: '<span style="line-height: 32px;font-weight: bold">' + dt.get('name') + '</span>'
                        },
                        {
                            xtype: 'button',
                            text: '删除',
                            _data: dt,
                            listeners: {
                                click: function () {
                                    var cidList = self.find('cid_list');
                                    cidList.data.splice(cidList.data.indexOf(this._data), 1);
                                    self.resetCidListViews()
                                }
                            }
                        }
                    ]
                });
                v._data = dt;
                cidList.add(v);
            }
        }
    },

    onParameterChange: function (view, newValue, oldValue) {
        var data = view.getSelection();
        if (data) {
            this.resetCidListViews(data);
        }
    },

    onComboboxChange: function (field, newValue, oldValue, eOpts) {
        var data = field.getSelection();
        var view = this.find('discount_active_view');
        var viewName = data.get('view');

        view.removeAll();
        var newView = Ext.create(viewName, {
            discountView: this
        });
        view.add(newView);
        newView.onInitApply();
    },

    onTypeChange: function (field, newValue) {
        if (newValue['type'] == 0) { // 分类
            this.find('gid').show();
            this.find('cid_list').show();
            this.find('bid').hide();
            this.find('bid_list').hide();
            this.find('select_goods_btn').hide();
            this.find('upload_goods_file').hide();
            this.find('gid_list').hide();
        } else if (newValue['type'] == 1) { // 商品
            this.find('gid').hide();
            this.find('cid_list').hide();
            this.find('bid').hide();
            this.find('bid_list').hide();
            this.find('select_goods_btn').show();
            this.find('upload_goods_file').show();
            this.find('gid_list').show();
        } else if (newValue['type'] == 2) { // 品牌
            this.find('gid').hide();
            this.find('cid_list').hide();
            this.find('bid').show();
            this.find('bid_list').show();
            this.find('select_goods_btn').hide();
            this.find('upload_goods_file').hide();
            this.find('gid_list').hide();
        }
    },

    initWindow: function () {
        var self = this;
        this.apis.Discount.getDiscountInit
            .wait(this, '正在加载初始化数据...')
            .call({}, function (data) {
                var storeImpl = Ext.create('Ext.data.Store', {
                    data: data['impls']
                });
                self.find('impl').setStore(storeImpl);

                var storeGroup = Ext.create('Ext.data.TreeStore', {
                    defaultRootId: '0',
                    root: {
                        expanded: true,
                        name: "商品分类管理",
                        children: data['groups']
                    }
                });
                self.find('gid').setTreeGridStore(storeGroup);

                var userLevelView = self.find('user_level_view');
                userLevelView.removeAll();
                if (data['levels']) {
                    for (var i = 0; i < data['levels'].length; i++) {
                        userLevelView.add({
                            xtype: 'checkboxfield',
                            name: 'userLevels',
                            boxLabel: data['levels'][i]['name'],
                            inputValue: data['levels'][i]['id'],
                            listeners: {
                                change: function (me, newValue, oldValue) {
                                    if (me.inputValue + "" == "0") {
                                        if (newValue == 1) {
                                            var items = me.ownerCt.items.items;
                                            for (var k = 0; k < items.length; k++) {
                                                if (items[k].inputValue + "" != "0") {
                                                    items[k].setValue(true);
                                                }
                                            }
                                        } else {
                                            var items = me.ownerCt.items.items;
                                            for (var k = 0; k < items.length; k++) {
                                                if (items[k].inputValue + "" != "0") {
                                                    items[k].setValue(false);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
                var platformView = self.find('platform_view');
                platformView.removeAll();
                if (data['platforms']) {
                    for (var i = 0; i < data['platforms'].length; i++) {
                        platformView.add({
                            xtype: 'checkboxfield',
                            name: 'platforms',
                            boxLabel: data['platforms'][i]['name'],
                            inputValue: data['platforms'][i]['id'],
                            listeners: {
                                change: function (me, newValue, oldValue) {
                                    if (me.inputValue + "" == "0") {
                                        if (newValue == 1) {
                                            var items = me.ownerCt.items.items;
                                            for (var k = 0; k < items.length; k++) {
                                                if (items[k].inputValue + "" != "0") {
                                                    items[k].setValue(true);
                                                }
                                            }
                                        } else {
                                            var items = me.ownerCt.items.items;
                                            for (var k = 0; k < items.length; k++) {
                                                if (items[k].inputValue + "" != "0") {
                                                    items[k].setValue(false);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            });

        var bidView = this.find('bid');
        var store = this.apis.Brand.getBrands.createPageStore();
        bidView.setGridStore(store);
        store.load();
    }
});
