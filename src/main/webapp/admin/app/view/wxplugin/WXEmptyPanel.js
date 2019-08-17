Ext.define('App.wxplugin.WXEmptyPanel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.form.Label'
    ],

    header: false,
    layout: 'fit',
    items: [
        {
            xtype: 'container',
            html: '<div style="width: 100%;height: 100%;display: flex;align-items: center;justify-content: center">' +
                '<span style="font-size: 18px;color: #666666">当前功能暂未实现，定制开发可以联系简子行科技</span>' +
                '</div>'
        }
    ]

});