Ext.define('App.integral.IntegralRecordWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.grid.Panel',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.view.Table'
    ],

    height: 500,
    width: 900,
    layout: 'fit',
    title: '积分消费记录',

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
                    width: 150,
                    dataIndex: 'nick',
                    text: '昵称',
                    renderer: function (v) {
                        if (v) return decodeURIComponent(v);
                        return '';
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'changeAmount',
                    text: '积分变更',
                    renderer: function (v) {
                        if (v >= 0) {
                            return '增加 ' + v;
                        } else {
                            return '扣减' + Math.abs(v);
                        }
                    }
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'beforeAmount',
                    text: '变更前数量'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'afterAmount',
                    text: '变更后数量'
                },
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'detail',
                    text: '变更说明'
                },
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'createTime',
                    text: '变更时间',
                    renderer: function (v) {
                        if (v) {
                            return new Date(v).format();
                        }
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

    setDatas: function (store) {
        this.find('grid').setStore(store);
        store.load();
    }

});