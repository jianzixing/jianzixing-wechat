Ext.define('App.wechat.WeChatImageTextSelector', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.grid.Panel',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.view.Table',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.selection.CheckboxModel'
    ],

    height: 500,
    width: 800,
    layout: 'fit',
    title: '选择图文',
    defaultListenerScope: true,

    items: [
        {
            xtype: 'gridpanel',
            name: 'grid',
            border: false,
            header: false,
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'id',
                    text: 'ID'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'coverUrl',
                    text: '封面URL',
                    renderer: function (v, mate, record) {
                        v = record.get('TableWeChatImageTextSub')[0]['coverUrl'];
                        return '<img style="width: 30px;height: 30px" src="' + v + '"/>';
                    }
                },
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'title',
                    text: '标题',
                    renderer: function (v, mate, record) {
                        v = record.get('TableWeChatImageTextSub')[0]['title'];
                        return v;
                    }
                },
                {
                    xtype: 'gridcolumn',
                    width: 200,
                    dataIndex: 'desc',
                    text: '描述',
                    renderer: function (v, mate, record) {
                        v = record.get('TableWeChatImageTextSub')[0]['desc'];
                        return v;
                    }
                },
                {
                    xtype: 'gridcolumn',
                    width: 200,
                    dataIndex: 'resUrl',
                    text: '图文地址',
                    renderer: function (v, mate, record) {
                        v = record.get('TableWeChatImageTextSub')[0]['url'];
                        return '<a style="text-decoration: none;color: #0f74a8" href="' + v + '" target="_blank">点击打开</a>';
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'createTime',
                    text: '创建时间',
                    width: 150,
                    renderer: function (v) {
                        if (v) return (new Date(v)).format();
                        return '';
                    }
                }
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    items: [
                        '->',
                        {
                            xtype: 'button',
                            text: '确定选择',
                            listeners: {
                                click: 'onSaveClick'
                            }
                        },
                        {
                            xtype: 'button',
                            text: '取消关闭',
                            listeners: {
                                click: 'onCancelClick'
                            }
                        },
                        '->'
                    ]
                }
            ],
            selModel: {
                selType: 'checkboxmodel'
            }
        }
    ],

    onSaveClick: function (button, e, eOpts) {
        var data = this.find('grid').getSelects();
        this.close();
        if (this._callback) {
            this._callback(data);
        }
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    setInit: function () {
        var store = this.apis.WeChatMaterial.getImageTexts.createPageStore({
            accountId: this.accountData['id'],
            openType: this.openType
        });
        this.find('grid').setStore(store);
        store.load();
    }
});
