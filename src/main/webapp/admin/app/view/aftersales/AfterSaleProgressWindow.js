Ext.define('App.aftersales.AfterSaleProgressWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.grid.Panel',
        'Ext.grid.column.RowNumberer',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'Ext.toolbar.Paging'
    ],

    autoShow: true,
    height: 550,
    width: 850,
    layout: 'fit',
    title: '售后单处理进度',

    items: [
        {
            xtype: 'gridpanel',
            name: 'grid',
            border: false,
            header: false,
            columns: [
                {
                    xtype: 'rownumberer'
                },
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'number',
                    text: '售后单'
                },
                {
                    xtype: 'gridcolumn',
                    width: 100,
                    dataIndex: 'status',
                    text: '状态',
                    renderer: function (v) {
                        if (this.ownerCt.mp && this.ownerCt.mp.getStatusName) {
                            return this.ownerCt.mp.getStatusName(v);
                        }
                        return '';
                    }
                },
                {
                    xtype: 'gridcolumn',
                    width: 100,
                    dataIndex: 'TableAdmin',
                    text: '处理人',
                    renderer: function (v) {
                        if (v) {
                            if (v['realName']) return v['realName'];
                            else return v['userName'];
                        }
                        return '[无]';
                    }
                },
                {
                    xtype: 'gridcolumn',
                    width: 300,
                    dataIndex: 'detail',
                    text: '处理描述',
                    renderer: function (v) {
                        return '<div style="width: 100%;height: 100%;overflow: hidden;white-space: normal">' + v + '</div>';
                    }
                },
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'createTime',
                    text: '处理时间',
                    renderer: function (v) {
                        if (v) return (new Date(v)).format();
                        return '';
                    }
                }
            ],
            dockedItems: [
                {
                    xtype: 'pagingtoolbar',
                    dock: 'bottom',
                    width: 360,
                    displayInfo: true
                }
            ]
        }
    ],

    setValue: function (asid, mp) {
        var store = this.apis.AfterSales.getAfterSaleProgress.createPageStore({asid: asid});
        this.find('grid').setStore(store);
        store.load();
    }

});