Ext.define('App.spcard.ShoppingCardLog', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.grid.Panel',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.view.Table'
    ],

    autoShow: true,
    height: 500,
    width: 900,
    layout: 'fit',
    title: '购物卡消费记录',

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
                    width: 120,
                    dataIndex: 'TableShoppingCard',
                    text: '批次名称',
                    renderer: function (v) {
                        if (v) return v['name'];
                        return '';
                    }
                },
                {
                    xtype: 'gridcolumn',
                    width: 120,
                    dataIndex: 'TableShoppingCard',
                    text: '所属批次',
                    renderer: function (v) {
                        if (v) return v['number'];
                        return '';
                    }
                },
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'TableUser',
                    text: '用户名',
                    renderer: function (v) {
                        if (v) return v['userName'];
                        return '';
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'money',
                    text: '消费金额'
                },
                {
                    xtype: 'gridcolumn',
                    width: 200,
                    dataIndex: 'detail',
                    text: '日志描述',
                    renderer: function (v) {
                        return '<div style="white-space: normal;word-break: break-all;">' + v + '</div>';
                    }
                },
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'createTime',
                    text: '消费时间',
                    renderer: function (v) {
                        if (v) return new Date(v).format();
                        return '';
                    }
                }
            ]
        }
    ],

    loadLogs: function () {
        var store = this.apis.ShoppingCard.getShoppingCardSpending.createListStore({
            id: this.cardData['scid'],
            cardNumber: this.cardData['cardNumber']
        });
        this.find('grid').setStore(store);
        store.load();
    }

});